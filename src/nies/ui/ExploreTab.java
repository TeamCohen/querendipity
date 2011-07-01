package nies.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;

import nies.ui.Result.Link;

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
		Set<String> all_edges = graph.getEdgeLabels(node);
		for (String edge : all_edges) {
			ArrayList<Link> values = new ArrayList<Link>();
			Distribution d_values = graph.walk1(node,edge);
			for(Iterator it = d_values.iterator(); it.hasNext();) { GraphId destNode = (GraphId) it.next();
				values.add(tabresult.new Link(destNode.toString(), graph.getTextContent(destNode)));
			}
			if (edge.endsWith("Inverse")) {
				tabresult.addBackwardAttribute(edge, values);
			} else {
				tabresult.addForwardAttribute(edge, values);
			}
		}
		return tabresult;
	}

}
