package nies.data;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

public class RelevanceData implements Serializable {
	private static final long serialVersionUID = 503322177633057913L;
	public static final String PROMOTE ="promote", 
						       DEMOTE  ="demote", 
						       CLICK   ="click",
						       UNMARK="unmark",
						       UNSURE  ="unsure";
	public static final int NO_RANK=-1;
	protected String user, task, query, queryParams, queryId, document, type;
	protected int rank;
	protected Date timestamp;
	
	public RelevanceData() {}
	
	public RelevanceData(Relevance rdata) {
		this.setDocument(rdata.getDocument());
		this.setQuery(rdata.getQuery());
		this.setRank(rdata.getRank());
		this.setTask(rdata.getTask());
		this.setTimestamp(rdata.getTimestamp());
		this.setType(rdata.getType());
		this.setUser(rdata.getUser());
		this.setQueryParams(rdata.getQueryParams());
		this.setQueryId(rdata.getQueryId());
	}
	public RelevanceData(String query, String queryParams, String document, String type) {
		this(query, queryParams, document, type, null);
	}
	public RelevanceData(String query, String queryParams, String document, String type, User u) {
		this(query, queryParams, document, type, NO_RANK, u);
	}
	public RelevanceData(String query, String queryParams, String document, String type, int r, User u) {
		this(query, queryParams, document, type, r, u, new Date());
	}
	public RelevanceData(String query, String queryParams, String document, String type, int r, User u, Date t) {
		this.query = query;
		this.queryParams = queryParams;
		this.document = document;
		this.type = type;
		this.user = (u != null ? u.getUid() : "");
		this.rank = r;
		this.timestamp = t;
	}
	public String getUser() {
		return user;
	}
	public String getTask() {
		return task;
	}
	public String getQuery() {
		return query;
	}
	public String getDocument() {
		return document;
	}
	public String getType() {
		return type;
	}
	public int getRank() {
		return rank;
	}
	public Date getTimestamp() {
		return timestamp;
	}

	public String getQueryParams() {
		return queryParams;
	}

	/**
	 * @return the queryId
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setUser(String user) {
		this.user = user;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public static KeyGenerator keyGenerator = new KeyGenerator();
}
