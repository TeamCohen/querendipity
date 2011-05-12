package nies.actions.search;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.ui.Tab;

public class LaziestSearch extends Search {
	public String execute() {
		this.runtime_sec = -1;
		this.buildQuery();
		for (int i=0; i<tabconfigs.length; i++) {
			Tab tab = Tab.makeTab(tabconfigs[i], graph);
			this.tabs.add(tab);
		}
		return SUCCESS;
	}
}
