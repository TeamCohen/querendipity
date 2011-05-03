package nies.data;

import java.util.Comparator;
import java.util.Date;

public class Relevance extends RelevanceData {
	public static final Comparator MOST_RECENT_FIRST = new Comparator<Relevance> () {
		public int compare(Relevance r1, Relevance r2) {
			return r2.getTimestamp().compareTo(r1.getTimestamp());
		}
	};
	public static final Comparator ALPHA_BY_QUERYTERMS = new Comparator<Relevance> () {
		public int compare(Relevance r1, Relevance r2) {
			int comp = r2.getQuery().compareTo(r1.getQuery());
			if (comp == 0) comp = r2.getQueryId().compareTo(r1.getQueryId());
			return comp;
		}
	};
	private String rid;
	
	public Relevance(RelevanceData rdata, String rid) {
		this.setRid(rid);
		this.setDocument(rdata.getDocument());
		this.setQuery(rdata.getQuery());
		this.setQueryParams(rdata.getQueryParams());
		this.setRank(rdata.getRank());
		this.setTask(rdata.getTask());
		this.setTimestamp(rdata.getTimestamp());
		this.setType(rdata.getType());
		this.setUser(rdata.getUser());
		this.setQueryId(rdata.getQueryId());
	}
	public Relevance(String rid, String query, String queryParams, String document, String type, User user, int rank, String queryId) {
		this(rid,query,queryParams,document,type,user,rank);
		this.setQueryId(queryId);
	}
	public Relevance(String rid, String query, String queryParams, String document, String type, User user, int rank) {
		this(rid, query, queryParams, document, type, user);
		this.setRank(rank);
	}
	public Relevance(String rid, String query, String queryParams, String document, String type, User user) {
		super(query, queryParams, document, type, user);
		this.setRid(rid);
	}

	public String toString() {
		return "[Relevance "+this.rid+" "+this.type+" "+this.document+" q"+this.queryId+"]";
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
}
