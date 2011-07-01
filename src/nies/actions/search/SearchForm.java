package nies.actions.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nies.actions.NiesSupport;
import nies.metadata.NiesConfig;

import org.apache.log4j.Logger;

/** Holds all the parts of a search form. */
public class SearchForm extends NiesSupport {
	private static final Logger logger = Logger.getLogger(SearchForm.class);
	public static final String EXTRA_SEARCH_FIELDS_PROP="nies.extraSearchFields";
	public static final String DEFAULT_SEARCHFORM="Dispatch";
	protected boolean rf=false;
	protected String authors = "";
	protected String genes   = "";
	protected String papers  = "";
	protected String keywords = "";
	protected int    maxResults = 10;
	protected int    depth      = 2;
	protected String searchAction="";
	protected String searchform=DEFAULT_SEARCHFORM;
	protected List<String> extraSearchFields;
	protected int NUMSTEPS = 1000;
	
	/* Struts action invocation methods */
	
	/** Action method for normal searches. */
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	/** Action method for advanced searches. */
	public String advanced() {
		return SUCCESS;
	}
	
	/** Bean getters/setters **/
	public boolean isRf() {
		return rf;
	}
	public String getAuthors() {
		return authors;
	}
	public String getGenes() {
		return genes;
	}
	public String getPapers() {
		return papers;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public int getDepth() {
		return depth;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public void setRf(boolean rf) {
		this.rf = rf;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public void setGenes(String genes) {
		this.genes = genes;
	}
	public void setPapers(String papers) {
		this.papers = papers;
	}
	public void setMaxResults(int m) {
		this.maxResults = m;
	}
	public void setDepth(int d) {
		logger.debug("Using depth "+d);
		this.depth = d; 
	}

	public List<String> getExtraSearchFields() {
		if (this.extraSearchFields == null) {
			logger.info("Reading extra search field configuration...");
			this.extraSearchFields = new ArrayList<String>();
			for(String f : NiesConfig.getProperty(EXTRA_SEARCH_FIELDS_PROP, "").split("[ ]+")) {
				if (f.trim().equals("")) continue;
				this.extraSearchFields.add(f);
				logger.debug("Added extra search field "+f);
			}
		}
		return extraSearchFields;
	}

	public void setExtraSearchFields(List<String> extraSearchFields) {
		this.extraSearchFields = extraSearchFields;
	}

	public String getSearchAction() {
		return searchAction;
	}
	public void setSearchAction(String searchAction) {
		this.searchAction = searchAction;
	}
	public String getSearchform() { return this.searchform; }
	public void setSearchform(String s) { this.searchform = s; }
	public void setNUMSTEPS(int numsteps) {
		NUMSTEPS = numsteps;
	}
	public int getNUMSTEPS() { return NUMSTEPS; }
	public void setDensity(int d) { NUMSTEPS = d; }
	public int getDensity() { return NUMSTEPS; }
}
