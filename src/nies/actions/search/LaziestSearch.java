package nies.actions.search;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.ui.Tab;

public class LaziestSearch extends Search {
	public String execute() {
		this.runtime_sec = -1;
		this.buildQuery();
		for (int i=0; i<nodetypes.length; i++) {
			Tab tab = Tab.makeTab(nodetypes[i], nodenames[i]);
			this.tabs.add(tab);
		}
		return SUCCESS;
	}
}
