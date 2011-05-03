package nies.ui;

import ghirl.graph.NodeFilter;

/** Not yet implemented. */
public class TermTab extends Tab {
	protected TermTab(String name) { this.title = name; this.displayType = Tab.TERM; }
	public NodeFilter getFilter() { return new NodeFilter("isa=$TERM"); }
	protected Result processResult(Object result, Result tabresult) { 
		return tabresult;
	}
}
