package nies.actions;

import com.opensymphony.xwork2.ActionSupport;

/** Holds a URL to support an arbitrary redirect handled in the JSP. */
public class Go extends NiesSupport {
	protected String name = "Go";
	private String url;
	public String getUrl() { return this.url; }
	public void setUrl(String u) { this.url = u; }
}
