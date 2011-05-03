package nies.ui;

import java.util.*;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.metadata.PaperCollection;
public class GeneTab extends Tab {
	protected GeneTab(String name) { this.title = name; this.displayType = Tab.GENE; }
	public NodeFilter getFilter() { return new NodeFilter("isa=$gene"); }
	protected Result processResult(Object result, Result v_tabresult) {
		YeastResult tabresult = new YeastResult(v_tabresult);
		tabresult.setHref("http://www.yeastgenome.org/cgi-bin/locus.pl?locus="
				+tabresult.getLabel());
		tabresult.setHrefsource("yeastgenome.org");
		return tabresult;
	}
}