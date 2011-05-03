package nies.ui.vocabulary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.graph.PathSearcher;
import ghirl.util.Distribution;
import ghirl.util.TreeDistribution;

/**
 * Useful GHIRL superclass to apply a PathSearcher and NodeFilter in series.
 * TODO: Move to GHIRL-2.0; it really doesn't belong here.
 * 
 * @author katie
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Transformer {

	protected Graph graph;
	protected PathSearcher selector; 
	protected NodeFilter filter; 
	
	public Distribution transform(Distribution d) {
		if (selector != null) {
			if (filter != null)
				return filter.filter(graph, selector.search(d));
			return selector.search(d);
		}
		if (filter != null)
			return filter.filter(graph, d);
		return d;
	}
	public Distribution transform(GraphId i) {
		if (selector != null) {
			if (filter != null) 
				return filter.filter(graph, selector.search(i));
			return selector.search(i);
		}
		if (filter != null) 
			if (! filter.accept(graph, i)) return new TreeDistribution();
		return new TreeDistribution(i);
	}
	/**
	 * @return the selector
	 */
	public PathSearcher getSelector() { return selector; }
	/**
	 * @return the filter
	 */
	public NodeFilter getFilter() { return filter; }
	/**
	 * @param selector the selector to set
	 */
	public void setSelector(PathSearcher selector) { this.selector = selector; }
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(NodeFilter filter) { this.filter = filter; }
}
