package nies.ui;

public class ExternalTab extends Tab {
	String url;
	public ExternalTab(String name) { this.title = name; this.displayType = Tab.EXTERNAL; }
	public String getUrl() { return url; }
	public void setUrl(String u) { url = u; }
}
