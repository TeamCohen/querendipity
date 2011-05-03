package nies.data;

import java.io.Serializable;
import java.util.Date;

public class QueryData implements Serializable {
	public static final String NO_TAG="nies.data.notag";
	public static KeyGenerator keyGenerator = new KeyGenerator();
	protected String queryString, tag;
	protected Date timestamp;
	public QueryData() {}
	
	public QueryData(String query, String queryParams) {
		this(query, queryParams, NO_TAG);
	}
	
	public QueryData(String query, String queryParams, String tag) {
		this.setQueryString(query, queryParams);
		this.tag = tag;
		this.timestamp = new Date();
	}
	public QueryData(Query query) {
		this.queryString = query.getQueryString();
		this.tag = query.getTag();
		this.timestamp = query.getTimestamp();
	}
	
	
	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String query, String queryParams) {
		this.queryString = makeQueryLookupString(query,queryParams);
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public static String makeQueryLookupString(String query, String queryParams) {
		return new StringBuilder(query).append("[").append(queryParams).append("]").toString();
	}
}
