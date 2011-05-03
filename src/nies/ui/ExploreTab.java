package nies.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

public class ExploreTab extends Tab {
	private static final Logger logger = Logger.getLogger(ExploreTab.class);
	private Graph graph;
	protected ExploreTab(String name) {
		this.title=name;
		this.displayType=Tab.EXPLORE;
		try {
			graph = (Graph) ServletActionContext.getServletContext().getAttribute("theGraph");
		} catch (NullPointerException e) {
			logger.error("No graph...!");
		}
	}
	public void setGraph(Graph g) { this.graph = g; }
	protected Result processResult(Object graphid, Result v_tabresult) {
		GraphId node = (GraphId) graphid;
		ExploreResult tabresult = new ExploreResult(v_tabresult);
		tabresult.setLabel(graph.getTextContent(node));
		Set<String> all_links = graph.getEdgeLabels(node);
		for (String link : all_links) {
			ArrayList<String> values = new ArrayList<String>();
			Distribution d_values = graph.walk1(node,link);
			for(Iterator it = d_values.iterator(); it.hasNext();) {
				values.add(graph.getTextContent((GraphId) it.next()));
			}
			if (link.endsWith("Inverse")) {
				tabresult.addBackwardAttribute(link, values);
			} else {
				tabresult.addForwardAttribute(link, values);
			}
		}
		return tabresult;
	}

}
