package nies.actions.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import nies.actions.UserAware;
import nies.data.ApplicationDataController;
import nies.data.Query;
import nies.data.QueryData;
import nies.data.RelevanceView;
import nies.data.User;
import nies.metadata.PaperCollection;
import nies.metadata.NiesConfig;
import nies.ui.Result;
import nies.ui.Tab;
import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.graph.TextGraphExtensions;
import ghirl.graph.Walker;
import ghirl.graph.WeightedTextGraph;
import ghirl.util.Distribution;
import ghirl.util.TreeDistribution;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.sleepycat.je.DatabaseException;


/** Fetches the specified pages of search results for a specified query. 
 * 
 * <p>Page handling is a little weird. To allow arbitrary numbers, names, and types 
 * of tabs while still conforming to the beans framework of Struts Actions, we 
 * stuff all the page numbers into a single comma-delimited parameter, "pages,"
 * which is then parsed into an array of integers allocated to be the number of
 * configured tabs (plus one if the "Other" tab is enabled in nies.properties).</p>
 * 
 * <p>Since a user will only ever change to a new page in one tab at a time, we 
 * use a *single* "pagechange" parameter to indicate the tab index and desired
 * page for that tab.  We cache these values so that they may be reused if 
 * through parameter ordering the pages array is written over.</p>
 * 
 * <p>There are several awkward aspects to this:</p><ul>
 * <li>Having to write to pages[] twice to take care of ordering inconsistency
 * </li>
 * <li>It is difficult for Results.jsp to write the <code>pagechange</code>
 * parameter in the
 * standard way with the &lt;s:param/&gt; tag, since it's a constructed string.
 * There may be a workaround with &lt;s:set/&gt; but I haven't looked into it.
 * <li>As a result, currently Results.jsp is writing <code>pagechange</code> 
 * as part of the
 * string argument to the anchor tag.  This is particularly ugly because you 
 * have to be sure
 * to overwrite any leftover <code>pagechange</code> data from the previous
 * request that might be getting into the &lt;s:url/&gt; tag via the 
 * <code>includeParams="get"</code> setting -- and we need it because it's the 
 * only way we can get the rest of the user's 
 * query parameters necessary to perform the search.  Take a look at the 
 * page numbering section of Results.jsp for more details, but seriously,
 * it's pretty bad if we expect future people to pick it up just by
 * looking at it.
 * </li>
 * </ul>
 * 
 * @author katie
 * */
public class Search extends SearchForm implements Preparable, ServletContextAware, UserAware {
	public static final String NIES_TABS_PROP = "nies.tabs";
	public static final String NIES_TABTYPES_PROP = "nies.tabtypes";
	private static final Logger logger = Logger.getLogger(Search.class);
	protected static final String ALLTAB_TITLE = "Other";
	private static final Class WALKER_CLASS = ghirl.graph.BasicWalker.class;
	public static final float DEFAULT_TAB_TIMEOUT_S = 5;
	
	protected static Map<User,QueryCache> queryCache = new TreeMap<User,QueryCache>();
	
	protected int NUMSTEPS = 1000;
	
	protected  String name = "Search";
	
	protected Graph graph;
	protected Walker walker;
	protected PaperCollection paperCollection;
	protected WeightedTextGraph graphResults;

	protected Query query;
	protected User user;
	protected String queryTerms   = "", nodeQuery = "", textQuery = "";
	protected String queryParams  = "";
	protected double runtime_sec;
	protected List<Tab> tabs = new ArrayList<Tab>();
	protected String selectedTab;
	protected List<Result> results;
	protected int nresults;
	
	protected String defaultQuery;
	protected boolean usingDefaultQuery;
	protected String stopEdges;
	protected boolean usingStopEdges;
	
	protected String[] nodenames;
	protected String[] nodetypes;
	protected int[] pages;
	
	protected String pagestring;
	protected int pagechangetab=-1, pagechangepage;
	protected boolean hasAllTab;
	
	protected long timeout_ms = 5000;
	protected int pagechangeOffset = 1;
	
	/** Contains the distribution of raw (non-text) graph nodes used for the random walk query. **/
	protected Distribution queryDistribution;
	
	/* Beans methods (getters and setters) */

	public String[] getNodenames() { return nodenames; }
	public String[] getNodetypes() { return nodetypes; }
	public void setNodenames(String[] nodenames) { this.nodenames = nodenames; }
	public void setNodetypes(String[] nodetypes) { this.nodetypes = nodetypes; }
	public void setNUMSTEPS(int numsteps) { NUMSTEPS = numsteps; }
	public int getNUMSTEPS() { return NUMSTEPS; }

	public String getTab() { return selectedTab; }
	public void setTab(String tab) { this.selectedTab = tab; }
	
	public void setPages(String pagelist) {
		if (logger.isDebugEnabled()) logger.debug("Setting pages: "+pagelist+" ["+pagechangetab+","+pagechangepage+"]");
		pagestring = pagelist;
		String[] pp = pagelist.split(",");
		for (int i=0; i<Math.min(pp.length, pages.length); i++) {
			pages[i]=makePositiveInt(pp[i], nodenames[i]);
		}
		if (pagechangetab >= 0 && pagechangetab < pages.length) 
			pages[pagechangetab] = pagechangepage;
		if (logger.isDebugEnabled()) logger.debug("Pages set: "+getPages());
	}
	
	public String getPages() {
		pagestring = join(",", pages);
		return pagestring;
	}
	
	public void setPagechange(String pagechange) {
		if (logger.isDebugEnabled()) logger.debug("Changing pages: "+pagechange+" ("+getPages()+")");
		String[] pagechangeinfo = pagechange.split(",");
		pagechangetab  = Integer.parseInt(pagechangeinfo[0])-pagechangeOffset;
		pagechangepage = Integer.parseInt(pagechangeinfo[1]);
		changePages(pagechangetab, pagechangepage);
	}
	protected void changePages(int pagechangetab, int pagechangepage) {
		if (pagechangetab >=0 && pagechangetab < pages.length)
			pages[pagechangetab] = pagechangepage;
		if (logger.isDebugEnabled()) logger.debug("Paged changed: "+getPages());
	}
	/**  @return the queryParams */
	public String getQueryParams() { return queryParams; }
	public Query getQuery() { return query; }
	public String getQueryTerms()   { 
		if (isValid(this.queryTerms)) return this.queryTerms;
		return "[invalid queryTerms]";
	}
	public String getNodeQuery() {
		if (isValid(this.nodeQuery)) return this.nodeQuery;
		return "{}";
	}
	public String getTextQuery() {
		if (isValid(this.textQuery)) return this.textQuery;
		return "{}";
	}
	public String getRuntime() {
		return String.valueOf(this.runtime_sec);
	}
	public List<Result> getResults() { return this.results;  }
	public String getNresults()      { return String.valueOf(this.nresults); }
	public List<Tab> getTabs()       { return this.tabs;     }
	public void setQuery(String q)   { this.queryTerms   = q; }
	
	/* Struts methods */
	
	/** (Struts method) Configure tabs and pages. Called before parameters are set in Interceptor phase. */
	public void prepare() {
		hasAllTab = Boolean.parseBoolean(nies.metadata.NiesConfig.getProperty("nies.allTab","false"));
		nodetypes = (NiesConfig.getProperty(NIES_TABTYPES_PROP,"")+(hasAllTab ? ",tab" : "")).split(",");
		nodenames = (NiesConfig.getProperty(NIES_TABS_PROP,"")+(hasAllTab ? ","+ALLTAB_TITLE : "")).split(",");
		if (nodetypes.length != nodenames.length) {
			logger.error("Bad tab configuration:\n\ttabtypes:" 
					+NiesConfig.getProperty(NIES_TABTYPES_PROP)
					+"\n\ttabs:"
					+NiesConfig.getProperty(NIES_TABS_PROP));
			throw new IllegalStateException("nies.properties must include tabtypes and tabs or neither");
		}
		logger.info("Initialized Search display with "+nodetypes.length+" tabs: "
				+NiesConfig.getProperty(NIES_TABTYPES_PROP,"")+" () "
				+NiesConfig.getProperty(NIES_TABS_PROP,""));
		pages = new int[nodenames.length];
		pagestring = ""; String delim = "";
		for (int i=0; i<pages.length; i++) {
			pages[i]=1;
			pagestring += delim + "1";
			delim = ",";
		}
		
        defaultQuery      = NiesConfig.getProperty(DEFAULT_QUERY);
        usingDefaultQuery = Boolean.parseBoolean(NiesConfig.getProperty(USING_DEFAULT_QUERY,
        					String.valueOf(!(defaultQuery==null))));
        stopEdges         = NiesConfig.getProperty(STOP_EDGES);
        usingStopEdges    = Boolean.parseBoolean(NiesConfig.getProperty(USING_STOP_EDGES,
							String.valueOf(!(stopEdges==null))));
        
        this.timeout_ms   = (int) Float.parseFloat(NiesConfig.getProperty(TAB_TIMEOUT,
        				  	String.valueOf(DEFAULT_TAB_TIMEOUT_S))) * 1000;
	    if (logger.isDebugEnabled()) logger.debug("Prepared Search with defaultQuery "+(usingDefaultQuery ? "on" : "off")+" as "+defaultQuery
			 +" and stopEdges "+(usingStopEdges ? "on" : "off")+" as "+stopEdges);
	}
	
	/** Action method. */
	

	public void setServletContext(ServletContext context) {
		this.graph           = (Graph)           context.getAttribute("theGraph"); if (graph == null) logger.error("Null graph stored in servlet context!");
		this.paperCollection = (PaperCollection) context.getAttribute("paperCollection");
		this.controller      = (ApplicationDataController) context.getAttribute("dataController");
	}
	public void setUser(User u, Interceptor i) { this.user = u; }
	
	public String execute() throws Exception {
		if (!this.hasSearchTerms()) return INPUT;

		this.buildQuery();
		Distribution allResults = null;
		long startTime = System.currentTimeMillis();
		long queryTime;

		QueryCache qc = queryCache.get(this.user);
		
		logger.debug("  Query="+query);

		if (qc == null ||
				//query.toString().equals(qc.query.toString())){
				!qc.query.shallowEquals(this.query)) {
			this.setupExperiment();
			printProfile("* Stamp: "+startTime);
			this.graphResults = this.doQuery(this.getQueryTerms());
			queryTime = System.currentTimeMillis();
			printProfile("* Stamp: "+queryTime);
			if (logger.isDebugEnabled()) logger.debug("Query time: "+(((double)queryTime-(double)startTime)/1000)+"secs");
			
			if (this.graphResults != null)
				allResults = extractResultsDistribution(this.graphResults);
			else if (this.hasActionErrors()) return ERROR;
			
			queryCache.put(this.user, new QueryCache(this.query,allResults,this.queryDistribution));
		} else {
			if (logger.isDebugEnabled()) logger.debug("Using cached query results "+qc.query.getQid()+".");
			allResults = qc.cachedResults;
			queryDistribution = qc.cachedQueryDistribution;
			queryTime = System.currentTimeMillis();
		}
		
		if (allResults == null || allResults.size() == 0) {
			this.nresults = 0;
			if (logger.isDebugEnabled()) logger.debug("No results");
		} else {
		this.tabulateResults(allResults);
		}
		
		long endTime   = System.currentTimeMillis();
		this.runtime_sec = ((double)endTime - (double)startTime)/1000;
		if (logger.isDebugEnabled()) { 
			logger.debug("Query time: "+(((double)queryTime-(double)startTime)/1000)+"secs");
			logger.debug("Display time: "+(((double)endTime - (double)queryTime)/1000) + "secs");
			logger.debug("Total time: "+runtime_sec+" secs");
		}
		
		return SUCCESS;
	}
	protected Distribution extractResultsDistribution(WeightedTextGraph walkResults) {
		return walkResults.getNodeDist();
	}
	
	/* Internal methods */
	
	protected boolean hasSearchTerms() {
		return this.isValid(this.queryTerms)
			|| this.isValid(this.keywords)
			|| this.isValid(this.authors)
			|| this.isValid(this.genes)
			|| this.isValid(this.papers);
	}
	
	protected boolean isValid(String term) {
		return (term != null) && term.trim().length() > 0;
	}
	
	protected void buildQuery() {
		StringBuilder termsBuilder = new StringBuilder();
		String parts[] = {this.keywords, this.authors, this.genes, this.papers};
		for (String part : parts) {
			if (isValid(part))   termsBuilder.append(" "+part);
		}
		this.queryTerms = termsBuilder.toString().replaceAll("[,;]"," ").replaceAll("\\s+"," ").trim();
		StringBuilder paramsBuilder = new StringBuilder();
		if (this.queryParams != null) paramsBuilder.append(this.queryParams);
		paramsBuilder.append("depth=").append(depth);
		paramsBuilder.append(",nsteps=").append(this.NUMSTEPS);
		if (this.usingDefaultQuery) paramsBuilder.append(",defaultQuery=").append(defaultQuery);
		if (this.usingStopEdges)    paramsBuilder.append(",stopEdges=").append(stopEdges);
		this.queryParams = paramsBuilder.toString();
		this.query = makeQuery();
		
		
	}
	
	protected ApplicationDataController controller;
	protected Query makeQuery() {
		Query q = (controller == null) ? null : controller.getQuery(queryTerms, queryParams);
		if (q == null) {
			if (logger.isDebugEnabled()) logger.debug("Query not found.  Making a new one...");
			q = new Query(null, queryTerms, queryParams, QueryData.NO_TAG);
			if (controller != null) {
				try {
					controller.saveQuery(q);
					if (logger.isDebugEnabled()) logger.debug("Saved new query '"+q.getQueryString()+"' with id '"+q.getQid()+"'");
				} catch (DatabaseException e) {
					logger.error("Trouble saving query to database!");
					throw new IllegalStateException(e);
				} catch(Exception e) {
					logger.error("Trouble saving query to database!",e);
				}
			}
		}
		return q;
	}
	
	//TODO: Move this to its own class? (cohesion)
	protected void setupExperiment() throws Exception {
		if (logger.isDebugEnabled()) logger.debug("Setting up Experiment...");
		this.walker = (Walker) WALKER_CLASS.newInstance();
		this.walker.setNumSteps(NUMSTEPS); 
		this.walker.setNumLevels(this.depth);
		this.walker.setSamplingPolicy(false);
		if (logger.isDebugEnabled()) logger.debug("Experiment created and set up");
	}
	
	protected String filterForIdQuery(Graph g, String query) {
		StringTokenizer st = new StringTokenizer(query);
		StringBuilder sb = new StringBuilder();
		if (logger.isDebugEnabled()) logger.debug("Searching for graph IDs in "+query);
		for(String termString =""; st.hasMoreTokens(); ) { termString = st.nextToken();
			GraphId id = GraphId.fromString(termString);
			if (!g.contains(id)) {
				if (logger.isDebugEnabled()) logger.debug(id+" not in graph.");
				id = new GraphId(GraphId.DEFAULT_FLAVOR, termString);
				if (!g.contains(id)) {
					if (logger.isDebugEnabled()) logger.debug(id+" not in graph.");
					continue;
				}
			} // if we're still running, then g contains id
			if (logger.isDebugEnabled()) logger.debug("Including "+id+" in node query");
			sb.append(","+termString);
		}
		if (sb.length() > 0) return "{"+sb.substring(1)+"}";
		return null;
	}
	
	protected String filterForTextQuery(String query) {
		return query;
	}
	
	protected Distribution getInitialDistribution(String query) {
		Distribution textDist = null;
		if (graph instanceof TextGraphExtensions) 
			textDist = ((TextGraphExtensions) graph).textQuery(filterForTextQuery(query));
		if (textDist != null) if (logger.isDebugEnabled()) logger.debug(textDist.size()+" nodes from text query");
		else if (logger.isDebugEnabled()) logger.debug("No nodes from text query");
		
		String nodeQuery = filterForIdQuery(graph, query);
		queryDistribution = null;
		if (logger.isDebugEnabled()) logger.debug("Using node query '"+nodeQuery+"'");
		if (nodeQuery != null) queryDistribution = graph.asQueryDistribution(nodeQuery);
		
		if (queryDistribution != null) {
			if (logger.isDebugEnabled()) logger.debug(queryDistribution.size()+" nodes from node query");
			textDist.addAll(queryDistribution.getTotalWeight(), queryDistribution);
		} else {
			if (logger.isDebugEnabled()) logger.debug("No nodes from node query");
			textDist = queryDistribution;
		}
		return textDist;
	}
	

	
	protected WeightedTextGraph doQuery(String query) {
		
		if (logger.isDebugEnabled()) logger.debug("Extracting initial distributions...");
		Distribution initialDistribution = getInitialDistribution(query);
		
        if (initialDistribution==null || initialDistribution.size()==0) {
		    if (logger.isDebugEnabled()) logger.debug("nothing found for '"+query+"'");
		    return null;
        }
        
        if (usingDefaultQuery && !(defaultQuery==null)) {
        	Distribution defaultDist = getInitialDistribution(defaultQuery);
        	if (defaultDist != null) {
        		if (logger.isDebugEnabled()) logger.debug("Added default queryTerms terms "+defaultQuery);
        		initialDistribution.addAll(defaultDist.getTotalWeight(), defaultDist);
        	} else if (logger.isDebugEnabled()) logger.debug("Default queryTerms "+defaultQuery+" has nothing in it!");
        }
        
        if (usingStopEdges && !(stopEdges==null)) {
        	for(String stopEdge : stopEdges.split(" ")) {
        		if (logger.isDebugEnabled()) logger.debug("Using stop edge '"+stopEdge+"'");
        		walker.addToEdgeStopList(stopEdge);
        	}
        }
        
        if (logger.isDebugEnabled()) if (logger.isDebugEnabled()) logger.debug("Using initial distribution of "+initialDistribution.size()+" elements.");
        if (logger.isDebugEnabled()) logger.debug("preparing walker...");
        walker.setGraph(graph);
        walker.setInitialDistribution( initialDistribution );
        walker.setUniformEdgeWeights(); // TODO: ?? should edge weights really start at uniform?
        walker.setSamplingPolicy(false);
        walker.reset();
        walker.walk();
        if (logger.isDebugEnabled()) logger.debug("returning WeightedTextGraph...");
        return new WeightedTextGraph(initialDistribution,walker.getNodeSample(),graph);
	}
	
	protected void printProfile(String s) {
		logger.debug(s);
	}
	
	protected void tabulateResults(Distribution allResults) {
		this.nresults = allResults.size();
		if (logger.isDebugEnabled()) logger.debug("Tabulating "+nresults+" results...");
		long tabulateStart = System.currentTimeMillis();
		// TODO: make tabs Comparable so we can switch to TreeMap
		Map<Tab,Distribution> tabDistributions = new HashMap<Tab,Distribution>();
		Map<Tab,Integer> paginationCounts = new HashMap<Tab,Integer>();
		for (int i=0; i<nodetypes.length; i++) {
			Tab tab = Tab.makeTab(nodetypes[i],nodenames[i],graph);
			tab.setPage(pages[i]);
			paginationCounts.put(tab,1 - ((pages[i]-1) * maxResults));
			tabs.add(tab);
			tabDistributions.put(tab, new TreeDistribution());
		}
		long tabulateConstruct = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug(" *** Constructed tabs: "+((double)tabulateConstruct - (double)tabulateStart)/1000);
		
		// this isn't quite right -- it's only going to get the current page, of each tab.
		// we won't know how many total results the tab will have.
		ArrayList<Tab> buckets = new ArrayList<Tab>(); for(Tab t : tabs) buckets.add(t);
		int i=0;
		Iterator it = allResults.orderedIterator();
		long afterIterator = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug(" *** Orderediterator (for timeout): "+((double)afterIterator - (double)tabulateStart)/1000);

		for (; it.hasNext() && buckets.size() > 0; i++) {
			GraphId graphid = (GraphId) it.next();
			for (Iterator<Tab> buckit = buckets.iterator(); buckit.hasNext();) {Tab tab = buckit.next();
				NodeFilter filter = tab.getFilter();
				if (filter == null || filter.accept(graph, graphid)) {
					int count = paginationCounts.get(tab);
					if (tab.getTopscore() < 0) tab.setTopscore(allResults.getLastWeight());
					if (tabDistributions.get(tab).size() == this.maxResults) {
						if (count == maxResults+1) {
	//						buckit.remove();
							if (logger.isInfoEnabled()) logger.info("Filled distribution for tab "+tab.getTitle() +" after sorting "+i+" results.");
						}
					}
					if (count > 0 && count <= this.maxResults) {
						tabDistributions.get(tab).add(allResults.getLastWeight(), graphid);
					}
					paginationCounts.put(tab, paginationCounts.get(tab)+1);
				}

				// trigger timeout so long as we've got at least one result on the current page
				if (System.currentTimeMillis() - afterIterator > timeout_ms 
						&& tabDistributions.get(tab).size() > 0) {
					int count = paginationCounts.get(tab);
					int est = Math.round((float)(count + (tab.getPage()-1)*maxResults - 1) / (float)i * (float)nresults);
					if (logger.isDebugEnabled()) logger.debug("Timeout with "+count+" results in/after current page. Estimating " +est+ " results total for tab "+tab.getTitle());
					tab.setNresultsEstimate(est);
					paginationCounts.put(tab,-1);
					buckit.remove();
				}
			}
		}
		
		long filterSort = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug(" *** Sort and Filter: " +i+" results examined in "+((double)filterSort - (double)tabulateConstruct)/1000);

		RelevanceView rview = (controller == null) ? null : controller.getRview();
		for (Map.Entry<Tab, Distribution> entry : tabDistributions.entrySet()) {
			long initTab = System.currentTimeMillis();
			Tab tab = entry.getKey();
			int count = paginationCounts.get(tab);
			tab.setPaperCollection(paperCollection);
			tab.init(entry.getValue(), 
					(count < 0) ? count : count + ((tab.getPage()-1)*maxResults) - 1, 
					maxResults, 
					tab.getTopscore());
			long initToRf = System.currentTimeMillis();
			if (initToRf-initTab > 1000 && logger.isDebugEnabled()) logger.debug(" *** tab.init(): "+((double)initToRf - (double)initTab)/1000);
			if (this.user != null && rview != null) {
				for(Result r : tab.getResults()) {
					r.setRelevanceMark(rview.isRelevant(query, user, r.getId()));
				}
			} else { logger.debug("Not setting relevance marks -- "+ (rview == null ? "view" : "user")+" null"); }
			long initTabEnd = System.currentTimeMillis();
			if (initTabEnd-initToRf > 1000 && logger.isDebugEnabled()) logger.debug(" *** rf: "+((double)initTabEnd - (double)initToRf)/1000);
			if (logger.isDebugEnabled()) logger.debug(" **** Collect "+tab.getTitle()+" display data: "+((double)initTabEnd - (double)initTab)/1000);
		}
		long end = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug(" *** Display data collection: "+((double)end-(double)filterSort)/1000);
	}
	
	protected void oldtabulateResults(Distribution allResults) {
		if (logger.isDebugEnabled()) logger.debug("Tabulating results...");
		boolean defaultYet = (this.selectedTab != null);
		RelevanceView rview = controller.getRview();
		if (logger.isDebugEnabled()) logger.debug("Tab selection is "+ (!defaultYet ? "NOT" : "") + " already set.");
		for (int i=0; i<nodetypes.length; i++) {
			long tabStart = System.currentTimeMillis();
			if (logger.isDebugEnabled()) logger.debug("*** Stamp: "+tabStart);
			int page    = pages[i];
			Tab tab = Tab.makeTab(nodetypes[i], nodenames[i],graph);
			long tabConstruct = System.currentTimeMillis();
			if (logger.isDebugEnabled()) logger.debug("*** Tab construction: "+((double)tabConstruct-(double)tabStart)/1000);
			fillTab(tab,allResults,page,rview);
			long tabFill = System.currentTimeMillis();
			if (logger.isDebugEnabled()) logger.debug("*** Tab fill: "+((double)tabFill-(double)tabConstruct)/1000);
			if (!defaultYet && tab.nresults > 0) {
				this.selectedTab = nodenames[i];
				defaultYet = true;
			}
			this.tabs.add(tab);
			this.nresults += tab.nresults;
			long tabEnd = System.currentTimeMillis();
			if (logger.isDebugEnabled()) logger.debug("*** Total tab: "+((double)tabEnd-(double)tabStart)/1000);
		}
		
//		if (hasAllTab) {
//			Tab tab = Tab.makeTab("tab",ALLTAB_TITLE);
//			tab.init(this.graphResults.getNodeDist(), pages[pages.length-1], 3*this.maxResults, this.paperCollection);
//			this.tabs.add(tab);
//		}
		
		if (logger.isDebugEnabled()) logger.debug("Results tabulated.");
	}
	
	
	protected void fillTab(Tab tab, Distribution allResults, int page, RelevanceView rview) {
		NodeFilter filter = tab.getFilter();//new NodeFilter("isa=$"+type);
		Distribution these = allResults;
		long fillStart = System.currentTimeMillis();
		if (filter != null) these = filter.filter(this.graph, allResults);
		long fillFilter = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug("**** Filtering: "+((double)fillFilter-(double)fillStart)/1000);
		tab.init(these, page, this.maxResults, this.paperCollection);
		long fillInit = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug("**** Initialization: "+((double)fillInit-(double)fillFilter)/1000);

		if (this.user != null) {
			for(Result r : tab.getResults()) {
				r.setRelevanceMark(rview.isRelevant(query, user, r.getLabel()));
			}
		}
	}
	
	protected static ServletContext getApplicationContext() {
		return ServletActionContext.getServletContext();
	}
	
	public static String join(String delim, String ... sequence) {
		StringBuilder sb = new StringBuilder();
		for (String term : sequence) {
			if (term != null) {
				sb.append(term); 
				sb.append(delim); 
			}
		}
		return sb.substring(0, sb.length()-delim.length());
	}
	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}
	/**
	 * @param graph the graph to set
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	/**
	 * @return the defaultQuery
	 */
	public String getDefaultQuery() {
		return defaultQuery;
	}
	/**
	 * @param defaultQuery the defaultQuery to set
	 */
	public void setDefaultQuery(String defaultQuery) {
		this.defaultQuery = defaultQuery;
	}
	/**
	 * @return the usingDefaultQuery
	 */
	public boolean isUsingDefaultQuery() {
		return usingDefaultQuery;
	}
	/**
	 * @param usingDefaultQuery the usingDefaultQuery to set
	 */
	public void setUsingDefaultQuery(boolean usingDefaultQuery) {
		this.usingDefaultQuery = usingDefaultQuery;
	}
	/**
	 * @return the stopEdges
	 */
	public String getStopEdges() {
		return stopEdges;
	}
	/**
	 * @param stopEdges the stopEdges to set
	 */
	public void setStopEdges(String stopEdges) {
		this.stopEdges = stopEdges;
	}
	/**
	 * @return the usingStopEdges
	 */
	public boolean isUsingStopEdges() {
		return usingStopEdges;
	}
	/**
	 * @param usingStopEdges the usingStopEdges to set
	 */
	public void setUsingStopEdges(boolean usingStopEdges) {
		this.usingStopEdges = usingStopEdges;
	}
	public static String join(String delim, int ... sequence) {
		StringBuilder sb = new StringBuilder();
		for (int term : sequence) {
			sb.append(term); 
			sb.append(delim);
		}
		return sb.substring(0, sb.length()-delim.length());
	}
	public WeightedTextGraph getGraphResults() {
		return graphResults;
	}
	
	public static class QueryCache {
		public Query query;
		public Distribution cachedResults;
		public Distribution cachedQueryDistribution;
		public QueryCache (Query q, Distribution rd, Distribution qd) {
			query = q;
			cachedResults = rd;
			cachedQueryDistribution = qd;
		}
	}
	
}
