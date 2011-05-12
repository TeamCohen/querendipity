package nies.actions.search;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ModelDriven;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.ui.Tab;

public class SearchTab extends Search {
	private static final Logger logger = Logger.getLogger(SearchTab.class);
	private int selectedtab_i=-1;
	private Tab tab;
	public void setTabi(int i) {
		this.selectedtab_i = i;
	}
	public int getTabi() { return this.selectedtab_i; }
	public Tab getSelectedTab() { return this.tab; }
	
	@Override
	protected void tabulateResults(Distribution allResults) {
		logger.debug("Tabulating results...");
		if (selectedtab_i < 0) {
			this.addActionError("No tab ID specified by rendering engine -- check backend code!");
			logger.error("Can't do TabSearch without &tabi=n; quitting");
			return;
		}
		
		int page    = pages[selectedtab_i];
		tab = Tab.makeTab(this.tabconfigs[selectedtab_i], graph);
		NodeFilter filter = tab.getFilter();
		Distribution these = allResults;
		if (filter != null) these = filter.filter(this.graph, allResults);
		tab.init(these, page, this.maxResults, this.paperCollection);
		
		this.nresults = allResults.size();
		logger.debug("Results tabulated.");
	}
}
