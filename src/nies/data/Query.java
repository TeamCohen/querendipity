package nies.data;

public class Query extends QueryData {
	private String qid;
	public Query(String qid, QueryData qdata) {
		this.setQid(qid);
		this.setQueryString(qdata.getQueryString());
		this.setTag(qdata.getTag());
		this.setTimestamp(qdata.getTimestamp());
	}
	

	public Query(String qid, String query, String queryParams, String tag) {
		super(query, queryParams, tag);
		this.setQid(qid);
	}


	/**
	 * @return the qid
	 */
	public String getQid() {
		return qid;
	}


	/**
	 * @param qid the qid to set
	 */
	public void setQid(String qid) {
		this.qid = qid;
	}
	
	public String toString() {
		return new StringBuilder().append(qid).append(":").append(this.queryString).toString();
	}
	
	public boolean deepEquals(Query q) {
		return q.qid.equals(this.qid)
			&& q.queryString.equals(this.queryString)
			&& q.tag.equals(this.tag)
			&& q.timestamp.equals(this.timestamp);
	}
	public boolean shallowEquals(Query q) {
		return q.qid.equals(this.qid); 
	}
}
