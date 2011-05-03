package nies.actions;

import com.opensymphony.xwork2.Preparable;

import nies.actions.search.Search;
import nies.metadata.NiesConfig;

public class AdminSettings extends NiesSupport implements Preparable {
	private boolean usingDefaultQuery;
	private boolean usingStopEdges;
	private String stopEdgeStringList;
	private String defaultQuery;
	private String graphName;
	private String dbDir;
	private String dbStore;
	private float tabTimeout;
	
	public void prepare() {
		stopEdgeStringList = NiesConfig.getProperty(STOP_EDGES,"");
		defaultQuery       = NiesConfig.getProperty(DEFAULT_QUERY,"");
		usingStopEdges     = Boolean.parseBoolean(NiesConfig.getProperty(USING_STOP_EDGES,
							 String.valueOf(!stopEdgeStringList.equals(""))));
		usingDefaultQuery  = Boolean.parseBoolean(NiesConfig.getProperty(USING_DEFAULT_QUERY,
							 String.valueOf(!defaultQuery.equals(""))));
		graphName          = ghirl.util.Config.getProperty("ghirl.graphName"); 
		dbDir              = ghirl.util.Config.getProperty("ghirl.dbDir");
		dbStore            = ghirl.util.Config.getProperty("ghirl.dbStore");
		tabTimeout         = Float.parseFloat(NiesConfig.getProperty(TAB_TIMEOUT,
							 String.valueOf(Search.DEFAULT_TAB_TIMEOUT_S)));
	}
	
	public String execute() {
		return SUCCESS;
	}
	public String save() {
		NiesConfig.setProperty(STOP_EDGES,stopEdgeStringList);
		NiesConfig.setProperty(DEFAULT_QUERY, defaultQuery);
		NiesConfig.setProperty(USING_STOP_EDGES, String.valueOf(usingStopEdges));
		NiesConfig.setProperty(USING_DEFAULT_QUERY, String.valueOf(usingDefaultQuery));
		this.addActionMessage("Querendipity settings saved.");
		return SUCCESS;
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
	/**
	 * @return the stopEdgeStringList
	 */
	public String getStopEdgeStringList() {
		return stopEdgeStringList;
	}
	/**
	 * @param stopEdgeStringList the stopEdgeStringList to set
	 */
	public void setStopEdgeStringList(String stopEdgeStringList) {
		this.stopEdgeStringList = stopEdgeStringList;
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

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public String getDbDir() {
		return dbDir;
	}

	public void setDbDir(String dbDir) {
		this.dbDir = dbDir;
	}

	public String getDbStore() {
		return dbStore;
	}

	public void setDbStore(String dbStore) {
		this.dbStore = dbStore;
	}

	public float getTabTimeout() {
		return tabTimeout;
	}

	public void setTabTimeout(float tabTimeout) {
		this.tabTimeout = tabTimeout;
	}
}
