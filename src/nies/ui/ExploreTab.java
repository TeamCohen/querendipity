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
		Graph g = null;
		try {
			g = (Graph) ServletActionContext.getServletContext().getAttribute("theGraph");
		} catch (NullPointerException e) {
			logger.error("No graph...!");
		}
		this.construct(name,g);
	}
	protected ExploreTab(String name, Graph g) {
		construct(name,g);
	}
	protected void construct(String name, Graph g) {

		this.title=name;
		this.displayType=Tab.EXPLORE;
		this.graph = g;
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
