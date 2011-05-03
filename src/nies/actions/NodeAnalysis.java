package nies.actions;

import java.util.Iterator;

import org.apache.log4j.Logger;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.PathSearcher;
import ghirl.util.Distribution;


import nies.actions.search.Search;
import nies.metadata.NiesConfig;
import nies.ui.ConfigurableTab;
import nies.ui.ExternalTab;
import nies.ui.Result;
import nies.ui.Tab;

public class NodeAnalysis extends Search {
	private static final Logger logger = Logger.getLogger(NodeAnalysis.class);
	private static final String ANALYZEDRESULT_TABNAME_PROP = "nies.nodeanalysis.querytabname";
	private static final String FLYBASE="http://flybase.org/cgi-bin/uniq.html?species=Dmel&cs=yes&db=fbgn&caller=genejump&context=%s";
	private static final String FLYMINE = "http://www.flymine.org/cgi-bin/gbrowse/flymine-release-27.0/?name=Gene:%s";
	public Result analyzedResult;
	public ConfigurableTab analyzedResultTab;
	PathSearcher identifierSearcher;
	
	public NodeAnalysis() { 
		this.pagechangeOffset += 2;
	}
	@Override
	public String execute() throws Exception {
		super.execute();
		// query node header
		GraphId queryNode = null;
		if (queryDistribution.size() != 1) { 
			this.addActionError("Needed exactly one matching gene for your query, but I found "+queryDistribution.size());
			return INPUT;
		}
		queryNode = (GraphId) queryDistribution.iterator().next();
		analyzedResult = analyzedResultTab.makeResult(queryNode);
		// getting the id for searching
		String identifier = getIdentifier(queryNode);
		// flybase tab
		ExternalTab flybaseTab = new ExternalTab("FlyBase");
		flybaseTab.setUrl(String.format(FLYBASE, identifier));
		flybaseTab.setNresults(1);
		// flymine tab
		ExternalTab flymineTab = new ExternalTab("FlyMine");
		flymineTab.setUrl(String.format(FLYMINE, identifier));
		flymineTab.setNresults(1);
		// collate
		
		this.tabs.add(0, flymineTab);
		this.tabs.add(0, flybaseTab);
		return SUCCESS;
	}
	/** Prefer the FB code, and return the CG identifier otherwise.  If neither is found, returns null. **/
	protected String getIdentifier(GraphId queryNode) {
		Distribution identifierDistribution = identifierSearcher.search(queryNode);
		String CGname = null;
		for (Iterator it = identifierDistribution.iterator(); it.hasNext();) { String identifier = ((GraphId) it.next()).getShortName();
			if (identifier.startsWith("CG")) CGname = identifier;
			if (identifier.startsWith("FB")) return identifier;
		}
		return CGname;
	}
	@Override
	public void prepare() {
		super.prepare();
		try {
			analyzedResultTab = (ConfigurableTab) Tab.makeTab(Tab.CONFIGURABLE, NiesConfig.getProperty(ANALYZEDRESULT_TABNAME_PROP));
			analyzedResultTab.setTitle("Query");
			identifierSearcher = new PathSearcher("hasSynonymInverse hasSynonym");
			if (this.graph != null) identifierSearcher.setGraph(graph);
		} catch(NullPointerException npe) {
			this.addActionError("NullPointerException (probably a configuration issue)");
			logger.error(npe);
		}
	}
	public Result getAnalyzedResult() {
		return analyzedResult;
	}
	public ConfigurableTab getAnalyzedResultTab() {
		return analyzedResultTab;
	}
	
}
