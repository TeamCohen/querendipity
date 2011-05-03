package nies.ui;

import java.util.*;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.metadata.PaperCollection;
public class AuthorTab extends Tab {
	protected AuthorTab(String name) { this.title = name; this.displayType = Tab.AUTHOR; }
	public NodeFilter getFilter() { return new NodeFilter("isa=$author"); }
	protected Result processResult(Object result, Result vanilla_tabresult) { 
		YeastResult tabresult = new YeastResult(vanilla_tabresult);
		tabresult.setHref("http://www.yeastgenome.org/cgi-bin/reference/reference.pl?author="
				+tabresult.getLabel()
				+"&search_type=Papers+in+SGD&rm=redirect&submit=Search!");
		tabresult.setHrefsource("SGD papers on yeastgenome.org");
		return tabresult; }
}