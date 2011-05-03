package nies.actions.search;

import org.apache.log4j.Logger;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.ui.Tab;

public class LazySearch extends Search {
	private static final Logger logger = Logger.getLogger(LazySearch.class);
	@Override
	protected void tabulateResults(Distribution allResults) {
		logger.debug("Tabulating results...");
		boolean defaultYet = (this.selectedTab != null);
		logger.debug("Tab selection is "+ (!defaultYet ? "NOT" : "") + " already set.");
		for (int i=0; i<nodetypes.length; i++) {
			int page    = pages[i];
			Tab tab = Tab.makeTab(nodetypes[i], nodenames[i]);
			NodeFilter filter = tab.getFilter();//new NodeFilter("isa=$"+type);
			Distribution these = allResults;
			if (filter != null) these = filter.filter(this.graph, allResults);
//			tab.init(these, page, this.maxResults, this.paperCollection);
			tab.nresults = these.size();
			if (!defaultYet && tab.nresults > 0) {
				this.selectedTab = nodenames[i];
				defaultYet = true;
			}
			
			this.tabs.add(tab);
		}
		
		this.nresults = allResults.size();
		logger.debug("Results tabulated.");
	}
}
