package nies.ui;

import nies.ui.Result.Link;

public class PMResult extends Result {
	private String citation;
	private String reasons=null;
	private String href;
	private Link pmid;
	
	public PMResult(Result vanilla_tabresult) {
		setId(vanilla_tabresult.getId());
		setLabel(vanilla_tabresult.getLabel());
		setRank(vanilla_tabresult.getRank());
		setScore(vanilla_tabresult.getScore());
	}
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
	}
	public Link getPmid() {
		return pmid;
	}
	public void setPmid(Link pmid) {
		this.pmid = pmid;
	}
	public void setPmid(String href, String text)   { 
		this.pmid = new Link(href,text);
	}
	public String getReasons() {
		return reasons;
	}
	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
	public String getHref() { 
		return href; 
	}
	public void setHref(String url) {
		this.href = url;
	}
}
