package nies.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;

import ghirl.graph.Graph;
import ghirl.util.Distribution;
import nies.metadata.EntityCollection;
import nies.metadata.PaperCollection;
import nies.ui.vocabulary.Transformer;

import org.apache.log4j.Logger;

public class Tab extends Transformer {
	private static final Logger log = Logger.getLogger(Tab.class);
	public static final String AUTHOR="author", 
	                           GENE="gene", 
	                           PAPER="paper", 
	                           TAB="tab", 
	                           TERM="term", 
	                           CONFIGURABLE="configurable",
	                           ENTITY="entity",
	                           PMPAPER="pmpaper",
	                           VOCABULARY="vocabulary",
	                           EXPLORE="explore",
	                           EXTERNAL = "external";
	protected static Map<String,Class> tabMap   = new HashMap<String,Class>();
	protected static DecimalFormat decFormatter = new DecimalFormat ("0.0000");
    protected static DecimalFormat expFormatter = new DecimalFormat ("0.00E0");
    
	static {
		tabMap.put(AUTHOR,             AuthorTab.class);
		tabMap.put(GENE,                 GeneTab.class);
		tabMap.put(PAPER,               PaperTab.class);
		tabMap.put(TAB,                      Tab.class);
		tabMap.put(TERM,                 TermTab.class);
		tabMap.put(CONFIGURABLE, ConfigurableTab.class);
		tabMap.put(VOCABULARY,     VocabularyTab.class);
		tabMap.put(EXPLORE, 		  ExploreTab.class);
		//tabMap.put(ENTITY, EntityTab.class);
		tabMap.put(PMPAPER,           PMPaperTab.class);
	}
	public boolean isDefault;
	public String title;
	public int nresults = -1;
	public int nresultsEstimate;
	public int nshowing;
	public int npages;
	public int page;
	/** Index of the first result on the current page (1-indexed) */
	public int starti;
	/** */
	public List<Result> results;
	public String displayType;
	protected double topscore=-1;
	
	protected EntityCollection entityCollection;
	protected PaperCollection paperCollection=null;
	protected Tab() {}
	protected Tab(String name) { this.title = name; this.displayType = Tab.TAB;}
	
	/**
	 * Fill Tab with the current page of search results.
	 * @param pageresults - only the current page of results for this tab
	 * @param nresults - Total number of results for this tab
	 * @param pagesize - size of pages (used to calculate starting rank of results shown)
	 * @param topscore - Weight of the top result for this tab (used for scaling weights to 1)
	 */
	public void init(Distribution pageresults, int nresults, int pagesize, double topscore) {
		this.isDefault = false;
		this.results = pagesize > 0 ? new ArrayList<Result>(pagesize) : new ArrayList<Result>();
		Iterator i = pageresults.orderedIterator();
		this.starti = (page - 1) * pagesize + 1;
		int r = starti;
		for (Object graphid=null; i.hasNext(); r++) {
			try {
				graphid = i.next();
				Result tabresult = new Result();
				String id = graphid.toString().trim();
				if (id.charAt(0) == '$') id = id.substring(1);
				tabresult.setId(id);
				tabresult.setLabel(tabresult.getId());
				tabresult.setRank(String.valueOf(r));
				double score = pageresults.getLastWeight();
				score = score/topscore;
				tabresult.setScore((score < .0001 ? 
						Tab.expFormatter.format(score) :
						Tab.decFormatter.format(score)));
				this.results.add(this.processResult(graphid,tabresult));
			} catch (NoSuchElementException e) { break; }
		}
		if (nresults >= 0) this.nresults = nresults;
		this.nshowing = results.size();
		this.npages   = (int) Math.ceil(
				((float) (this.nresults >= 0 ? this.nresults : this.nresultsEstimate)) / pagesize);
	}
	
	protected Result makeResult(Object graphId, Distribution resultsDistribution, int rank) {
		Result tabresult = new Result();
		String id = graphId.toString().trim();
		if (id.charAt(0) == '$') id = id.substring(1);
		tabresult.setId(id);
		tabresult.setLabel(tabresult.getId());
		tabresult.setRank(String.valueOf(rank));
		if (resultsDistribution != null) {
			double score = resultsDistribution.getLastWeight();
			score = score/topscore;
			tabresult.setScore((score < .0001 ? 
					Tab.expFormatter.format(score) :
					Tab.decFormatter.format(score)));
		}
		return tabresult;
	}
	
	/** 
	 * Fill the Tab with the specified Distribution of search results.
	 * @param resultsDistribution - All results for this tab
	 * @param pageOffset - 1-indexed int of what page we're on.  page 1 gets you results 0-(maxResults-1)
	 * @param maxResults - Page size.  Use -1 to get all results on a single page.
	 * @param paperCollection PaperCollection where metadata is accessible.
	 */
	public void init(Distribution resultsDistribution, int pageOffset, int maxResults, PaperCollection paperCollection) {
		log.info("Getting page "+pageOffset+"x"+maxResults+" of tab...");
		long initStart = System.currentTimeMillis();
		long templateTime = 0;
		this.isDefault=false;
		this.paperCollection = paperCollection;
		this.results = maxResults >0 ? new ArrayList<Result>(maxResults) : new ArrayList<Result>();
		int r=1;
		Iterator i = resultsDistribution.orderedIterator();
		long init1 = System.currentTimeMillis();
		if (i.hasNext()) { i.next(); topscore = resultsDistribution.getLastWeight(); }
		if (topscore <= 0) topscore = 1;
		log.debug("Using "+topscore+" as top score");
		long init2 = System.currentTimeMillis();
		i = resultsDistribution.orderedIterator();
		long init3 = System.currentTimeMillis();
		skipToCurrentPage(i, pageOffset, maxResults);
		long initPrologue = System.currentTimeMillis();
		if (log.isDebugEnabled()) log.debug("***** Init prologue: "+((double) initPrologue - (double) initStart)/1000
				+"\n\t ***** phase 1: "+((double) init1 - (double) initStart)/1000
				+"\n\t ***** phase 2: "+((double) init2 - (double) init1)/1000
				+"\n\t ***** phase 3: "+((double) init3 - (double) init2)/1000
				+"\n\t ***** phase 4: "+((double) initPrologue - (double) init3)/1000);
		long resultTime=0;
		for (Object o=null; i.hasNext(); ++r) {
			if ((maxResults > 0) && r > maxResults) break;
			try {
				long resultStart = System.currentTimeMillis();
				o = i.next();
				Result tabresult = makeResult(o,resultsDistribution,r+starti-1);
//				Result tabresult = new Result();
//				String id = o.toString().trim();
//				if (id.charAt(0) == '$') id = id.substring(1);
//				tabresult.setId(id);
//				tabresult.setLabel(tabresult.getId());
//				tabresult.setRank(String.valueOf(r+starti-1));
//				double score = results.getLastWeight();
//				score = score/topscore;
//				tabresult.setScore((score < .0001 ? 
//						Tab.expFormatter.format(score) :
//						Tab.decFormatter.format(score)));
				long templateStart = System.currentTimeMillis();
				this.results.add(this.processResult(o,tabresult));
				templateTime += System.currentTimeMillis() - templateStart;
				resultTime += templateStart-resultStart;
			} catch (NoSuchElementException e) { break; }
		}
		log.info("Created tab with "+(r-1)+" elements");
		this.nresults = resultsDistribution.size();
		this.nshowing = Math.min(r-1, maxResults);
		this.npages   = (int) Math.ceil(((float)this.nresults) / maxResults);
		long initEnd = System.currentTimeMillis();
		if (log.isDebugEnabled()) log.debug("***** Total non-process result: "+(double)resultTime/1000);
		if (log.isDebugEnabled()) log.debug("***** Total processResult: "+(double)templateTime/1000);
		if (log.isDebugEnabled()) log.debug("***** Total init: "+((double)initEnd - (double)initStart)/1000);
		
		
	}
	
	public static Tab makeTab(String tabtype, String tabname) { return makeTab(tabtype,tabname,null); }
	/**
	 * Factory method for Tab creation keyed to a string type.
	 * @param tabtype - String in 'author','gene','paper','tab','configurable'
	 * @return A Tab containing the results for this page, of the appropriate type.
	 */
	public static Tab makeTab(String tabtype, String tabname, Graph graph) {
		try {
			Class tabclass = Tab.tabMap.get(tabtype);
			if (null == tabclass) throw new RuntimeException("No class for tabtype ["+tabtype+"]");
			Tab tab = (graph == null) ? constructTab(tabclass,tabname) : constructTab(tabclass,tabname,graph);
			if (null == tab) throw new RuntimeException("Tab instance didn't create?");
			tab.setDisplayType(tabtype);
			log.info(tabtype +"-type Tab created.");
			return tab;
		} catch (InstantiationException e) { throw new RuntimeException(e); }
		  catch (IllegalAccessException e) { throw new RuntimeException(e); } 
		  catch (IllegalArgumentException e) { throw new RuntimeException(e); } 
		  catch (SecurityException e)      { throw new RuntimeException(e); } 
		  catch (InvocationTargetException e) { throw new RuntimeException(e); } 
		  catch (NoSuchMethodException e)  { throw new RuntimeException(e); }
	}
	
	protected static Tab constructTab(Class tabclass, String tabname, Graph graph) 
		throws IllegalArgumentException, InstantiationException, IllegalAccessException, 
			   InvocationTargetException, SecurityException, NoSuchMethodException {
		Constructor con;
		Tab tab;
		try {
			con = tabclass.getDeclaredConstructor(String.class,Graph.class);
			tab = (Tab) con.newInstance(tabname,graph);
		} catch (NoSuchMethodException e) {
			log.info("No (string,graph) constructor found for tab "+tabname+"; using (string) constructor as back-up.");
			tab = constructTab(tabclass,tabname);
		}
		return tab;
	}
	
	protected static Tab constructTab(Class tabclass, String tabname) 
	throws IllegalArgumentException, InstantiationException, IllegalAccessException, 
		   InvocationTargetException, SecurityException, NoSuchMethodException {
		Constructor con = tabclass.getDeclaredConstructor(String.class);
		Tab tab = (Tab) con.newInstance(tabname);
		return tab;
	}
	
	/**
	 * Adds information to the Result object passed to the display JSP.  
	 * @param result The GraphId of the search result through the GHIRL graph.
	 * @param tabresult The JSP-displayable {@link nies.ui.Result Result} object, prepopulated with the id(shortname), rank, and score.
	 * @return A Result object which is then added to the list displayed by this Tab.  May be the same as tabresult, may be a new object.
	 */
	protected Result processResult(Object result, Result tabresult) { 
		return tabresult; 
	};
	
//	/**
//	 * Get the filter which admits nodes this Tab knows how to display.
//	 */
//	public NodeFilter getFilter() { return null; }
	
	protected void skipToCurrentPage(Iterator i, int page, int pageSize) {
		if (pageSize < 0) return;
		for (int j=0; j<(page-1)*pageSize; j++) i.next();
		this.starti = (page-1)*pageSize + 1; // 1-indexing
		this.page   = page;
	}
	
	/* Beans methods */
	
	public int getStarti() { return this.starti; }
	public int getEndi()   { return this.starti + this.nshowing - 1; }
	public int getNpages() { return this.npages; }
	public boolean isDefault() {
		return isDefault;
	}
	public String getTitle() {
		return title;
	}
	public int getNresults() {
		return nresults;
	}
	public List<Result> getResults() {
		return results;
	}
	public String getDisplayType() {
		return displayType;
	}
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setNresults(int nresults) {
		this.nresults = nresults;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public void setPage(int i) {
		this.page = i;
	}
	public int getPage() {
		return this.page;
	}
	public double getTopscore() {
		return topscore;
	}
	public void setTopscore(double topscore) {
		this.topscore = topscore;
	}
	public int getNresultsEstimate() {
		return nresultsEstimate;
	}
	public void setNresultsEstimate(int nresultsEstimate) {
		this.nresultsEstimate = nresultsEstimate;
	}
	public PaperCollection getPaperCollection() {
		return paperCollection;
	}
	public void setPaperCollection(PaperCollection paperCollection) {
		this.paperCollection = paperCollection;
	}
}