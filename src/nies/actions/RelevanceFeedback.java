package nies.actions;

import nies.data.ApplicationDataController;
import nies.data.Relevance;
import nies.data.RelevanceData;
import nies.data.User;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.Interceptor;

/** Records relevance feedback data in the DB; optionally provides a redirect. */
public class RelevanceFeedback extends NiesSupport implements UserAware {
	private static final Logger logger = Logger.getLogger(RelevanceFeedback.class);
	protected  String name = "RelevanceFeedback";
	private String url, queryTerms, queryParams, queryId, document, type;
	private User user;
	private int rank;
	
	public String getUrl() { return url; }
	public String getQueryTerms() {
		return queryTerms;
	}
	public String getDocument() {
		return document;
	}
	public String getType() {
		return type;
	}

	/**
	 * @return the rank
	 */
	public int getRank() { return rank; }
	/**
	 * @return the queryParams
	 */
	public String getQueryParams() { return queryParams; }
	/**
	 * @return the queryId
	 */
	public String getQueryId() { return queryId; }
	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(String queryId) { this.queryId = queryId; }
	/**
	 * @param queryParams the queryParams to set
	 */
	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		logger.debug("Relevance with rank "+rank);
		this.rank = rank;
	}
//	public void setDepth(String d) {
//		d = d.trim();
//		if (d.length() < 1) return;
//		try {
//			this.depth = Integer.parseInt(d);
//		} catch (NumberFormatException e) { logger.error("Wrong format for depth (received '"+d+"'): "+e.getMessage()); }
//	}
	public void setQueryTerms(String query) {
		this.queryTerms = query;
		logger.debug("Set query to "+this.queryTerms+".");
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setUrl(String url) { this.url = url; }
	
	/** Action method. */
	public String execute() {

		ApplicationDataController dataController = (ApplicationDataController) ServletActionContext.getServletContext().getAttribute("dataController");
		try {
			dataController.saveRF(new Relevance(RelevanceData.keyGenerator.newKey(), queryTerms, queryParams, document, type, user, rank, queryId));

		} catch (Exception e) {
			logger.error("Problem adding relevance feedback: ",e);
		}
		return SUCCESS;
	}

	public void setUser(User u, Interceptor i) {
		user = u;
	}
}
