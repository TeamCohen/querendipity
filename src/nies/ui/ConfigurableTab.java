package nies.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.graph.PathSearcher;
import ghirl.graph.TextNodeFilter;
import ghirl.util.Distribution;
import ghirl.util.TreeDistribution;
import nies.exceptions.BadConfigurationError;
import nies.metadata.NiesConfig;
import nies.ui.ConfigurableResult.Link;

/**
 * A highly flexible Tab which can be configured to display any list of 
 * attributes of the result which conform to the display vocabulary.
 * 
 * <h2>Syntax:</h2>
 * <p>In the nies.properties tab specification, include a tab of title [title]
 * whose type is 'configurable':</p>
 * <pre>nies.tabs=[title]
 * nies.tabtypes=configurable</pre>
 * 
 * <p>Then specify:</p><pre>
# 	nies.[title].filter - the filter string that selects nodes displayable by this tab
#	nies.[title].attributes - the list of attributes as displayed in the block for each search result
#	nies.[title].[attribute] - for each attribute, the PathSearcher list of edges to traverse to select nodes for display
 * </pre>
 * <pr>Attributes *may* include spaces in the .attributes list, but replace 
 * these with _ when you give their PathSearcher list.</p>
 * 
 * <p>In this way we can, for example, simulate the behavior of the PaperTab
 * class with the following configuration:</p>
 * <pre>nies.tabs=Papers
nies.tabtypes=configurable
nies.Papers.filter=isa=$paper
nies.Papers.label=
nies.Papers.attributes=Citation,Authors,Genes
nies.Papers.Citation=hasKeywords
nies.Papers.Authors=paperIsWrittenByAuthor hasKeywords
nies.Papers.Genes=geneIsMentionedInPaperInverse</pre>

 * @author katie
 *
 */
public class ConfigurableTab extends Tab {
	private static final Logger logger = Logger.getLogger(ConfigurableTab.class);
	public static final String ATTRIBUTE_NVALUES_PROP="nies.alltabs.attributes.maxvalues";
	public static final String FILTER_PROP    ="nies.tab.%s.filter",
	                           LABEL_PROP     ="nies.tab.%s.label",
	                           NOLABEL_PROP   ="nies.tab.%s.nolabel",
	                           ATTRIBUTES_PROP="nies.tab.%s.attributes",
	                           ATTRIBUTE_PROP ="nies.tab.%s.attribute.%s",
	                           DEREFERENCE_PROP= "nies.tab.%s.dereference";
	private static final PathSearcher dereferenceSearcher = new PathSearcher("_hasTerm _inFile");
	private static final PathSearcher foridSearcher = new PathSearcher("vt:hasIdInverse");
	NodeFilter filter;
	PathSearcher labelSearcher=null;
	List<Entry<String,PathSearcher>> attrSearchers=new ArrayList<Entry<String,PathSearcher>>();
	boolean dereferenceById=false;
	String noLabel=null;
	public List<Entry<String,PathSearcher>> getAttrSearchers() {
		return attrSearchers;
	}
	int maxnvalues_setting;

	Graph graph=null;
	
	/**
	 * Get all nodes in the graph that have the same ID as this node, for 
	 * all of its external IDs, searching by text string and filtering
	 * by ID type.
	 * @param node
	 * @return A Distribution of nodes that likely represent the same document
	 * as the input node.
	 */
	protected Distribution dereference(GraphId node) {
		Distribution result = new TreeDistribution(node);
		Distribution d_ids = graph.walk1(node, "vt:hasId");
		if (d_ids == null || d_ids.size()==0) return result;
		for(Iterator it=result.iterator(); it.hasNext(); ) {
			GraphId id = (GraphId) it.next();
			Distribution d_idType = graph.walk1(id, "vt:ofType");
			if (d_idType == null || d_idType.size() == 0) continue;
			GraphId idType = (GraphId) d_idType.iterator().next();
			Distribution shareId = dereferenceSearcher.search(id);
			shareId = new TextNodeFilter("vt:ofType="+graph.getTextContent(idType))
				.filter(graph, shareId);
			if (shareId.size() == 0) continue;
			Distribution deref = foridSearcher.search(shareId);
			result.addAll(deref.getTotalWeight(),deref);
		}
		return result;
	}
	
	public void setGraph(Graph g) {
		this.graph = g;
		if (this.labelSearcher != null) this.labelSearcher.setGraph(graph);
		dereferenceSearcher.setGraph(graph);
		foridSearcher.setGraph(graph);
		for (Entry<String,PathSearcher> e : attrSearchers) {
			e.value.setGraph(graph);
		}
		logger.info("Graph set; results may be processed now.");
	}
	
	protected ConfigurableTab(String name, Graph g) {
		init(name,g);
	}
	protected ConfigurableTab(String name) { 
		Graph g = null;
		try {
			ServletContext sc = ServletActionContext.getServletContext();
			if (sc != null) g = (Graph) sc.getAttribute("theGraph");
		} catch (NullPointerException e) {}
		init(name,g);
	}
	protected void init(String name, Graph g) {
		this.title = name;
		this.displayType=Tab.CONFIGURABLE;
		String filtername=String.format(FILTER_PROP,this.title);//this.title+".filter";
		if (NiesConfig.getProperty(filtername) == null) {
			throw new BadConfigurationError("Missing configuration for "+filtername);
		}
		if (logger.isDebugEnabled()) logger.debug("Creating configurable tab "+this.title+" with filter "+NiesConfig.getProperty(filtername));
		this.filter = new NodeFilter(NiesConfig.getProperty(filtername));
		graph = g; if (graph == null) logger.warn("No graph available. Call ConfigurableTab.setGraph() before processing results.");
		String labelname="nies."+this.title+".label";
		String labelpath = NiesConfig.getProperty(labelname,"");
		if (!"".equals(labelpath)) {
			this.labelSearcher = new PathSearcher(labelpath);
			this.labelSearcher.setGraph(graph);
		}
		String attrsListName = String.format(ATTRIBUTES_PROP,this.title);
		String attrsList = NiesConfig.getProperty(attrsListName);
		if (attrsList == null) {
			logger.error("Bad config in nies for tab "+this.title+": "+attrsListName+" required");
		} else {
			for(String attrName : attrsList.split(",")) {
				if ("".equals(attrName)) continue;
				attrName = attrName.replaceAll(" ", "_");
				String path = NiesConfig.getProperty(String.format(ATTRIBUTE_PROP,this.title,attrName));
				if (null == path) {
					logger.error("Bad config in nies for tab "+this.title+": "+attrName+" has null path");
					continue;
				}
				PathSearcher ps = new PathSearcher(path);
				if (graph != null) ps.setGraph(graph);
				attrSearchers.add(new Entry<String,PathSearcher>(attrName, ps));
			}
		}
		dereferenceById = Boolean.parseBoolean(
				NiesConfig.getProperty(String.format(DEREFERENCE_PROP,this.title), String.valueOf(dereferenceById)));
		logger.info("Dereferencing is "+ (dereferenceById ? "on" : "off"));
		noLabel = NiesConfig.getProperty(String.format(NOLABEL_PROP, this.title),null);
		dereferenceSearcher.setGraph(graph);
		foridSearcher.setGraph(graph);
		this.maxnvalues_setting = Integer.parseInt(NiesConfig.getProperty(ATTRIBUTE_NVALUES_PROP,"-1"));
		if (maxnvalues_setting < 0) maxnvalues_setting = Integer.MAX_VALUE;
	}

	public NodeFilter getFilter() { return filter; }

	protected void setLabelOnResult(GraphId result, Distribution startFrom, ConfigurableResult tabresult) {
		try {
			GraphId label = null;
			if (this.labelSearcher != null) {
				Distribution d_label = labelSearcher.search(startFrom);
				if (d_label != null && d_label.size() > 0)
					// there may be a way to select the best available title, but..
					label = (GraphId) d_label.iterator().next();
			}
			if (label == null) {
				if(noLabel != null) {
					tabresult.setLabel(noLabel);
					return;
				} else label = result;
			}
			if (graph.getTextContent(label) != null) tabresult.setLabel(graph.getTextContent(label));
			else                                     tabresult.setLabel(label.getShortName());
		} catch(Exception e) {
			logger.error("Couldn't get label on "+((GraphId) result).getShortName(),e);
		}
	}
	public Result makeResult(Object graphId) {
		return processResult(graphId,makeResult(graphId,null,-1));
	}
	protected Result processResult(Object result, Result v_tabresult) { 
		if (graph == null) throw new IllegalStateException("ConfigurableTab.graph has not been set.");
		long prStart = System.currentTimeMillis();
		ConfigurableResult tabresult = new ConfigurableResult(v_tabresult);

		Distribution startFrom = new TreeDistribution((GraphId) result);
		if (dereferenceById) {
			startFrom = this.filter.filter(graph, dereference((GraphId) result));
			logger.info("Dereferenced "+result.toString()+" to include "+(startFrom.size()-1)+" other nodes");
		}
		setLabelOnResult((GraphId) result, startFrom, tabresult);
		List<Link> values;
		long attrStart = System.currentTimeMillis();
		if (logger.isDebugEnabled()) logger.debug("****** Process Result prologue: "+((double)attrStart - (double)prStart)/1000);
		long attrValues = 0;
		for(Entry<String,PathSearcher> attr : attrSearchers) {
			long searchStart = System.currentTimeMillis();
			PathSearcher searcher = attr.getValue();
			Distribution valuedist = searcher.search(startFrom);
			if (valuedist.size() == 0)
				logger.debug("No results for "+result.toString()+"->"+attr.getKey());
			int maxnvalues = Math.min(valuedist.size(), maxnvalues_setting);
			values = new ArrayList<Link>();
			Iterator<GraphId> it = valuedist.orderedIterator(true);//valuedist.iterator();
			long valStart = System.currentTimeMillis();
			int nvalues=0;
			for (GraphId valueid = null; it.hasNext() && nvalues <= maxnvalues; nvalues++) { 
				valueid=it.next();
				String s = graph.getTextContent(valueid);
				String shortName = valueid.getShortName();
				if (s == null) values.add(tabresult.new Link(shortName,valueid.toString()));
				else           values.add(tabresult.new Link(s,valueid.toString()));
			}
			long dt = System.currentTimeMillis() - valStart;
			attrValues += dt;
			if (logger.isDebugEnabled() && (dt > 1000 || (dt + valStart - searchStart) > 1000)) {
				logger.debug("****** Unusually slow attribute: "+attr.getKey()+" ("+(valStart-searchStart)/1000+" search, "+dt/1000+" assemble)");
			}
			tabresult.addAttribute(attr.getKey(), values);
		}
		long prEnd = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			if (prEnd-attrStart > 1000) logger.debug("******* Unusually slow result: "+((GraphId)result).toString());
			logger.debug("****** Attributes: "+((double)prEnd - (double)attrStart)/1000);
			logger.debug("****** Attribute values: "+((double)attrValues)/1000);
			logger.debug("****** Process Result: "+((double)prEnd - (double)prStart)/1000);
		}
		return tabresult;
	}

	public int getMaxnvalues_setting() {
		return maxnvalues_setting;
	}
}
