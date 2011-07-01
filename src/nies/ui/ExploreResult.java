package nies.ui;

import java.util.ArrayList;
import java.util.List;

public class ExploreResult extends Result {
	private List<Entry<String,List<Link>>> forwardAttributes=new ArrayList<Entry<String,List<Link>>>();
	private List<Entry<String,List<Link>>> backwardAttributes=new ArrayList<Entry<String,List<Link>>>();
	public ExploreResult() {}
	public ExploreResult(Result v_tabresult) {
		setLabel(v_tabresult.getLabel());
		setId(v_tabresult.getId());
		setRank(v_tabresult.getRank());
		setScore(v_tabresult.getScore());
	}
	public List<Entry<String, List<Link>>> getForwardAttributes() {
		return forwardAttributes;
	}
	public List<Entry<String, List<Link>>> getBackwardAttributes() {
		return backwardAttributes;
	}
	public void addForwardAttribute(String label, List<Link> values) {
		forwardAttributes.add(new Entry<String,List<Link>>(label,values));
	}
	public void addBackwardAttribute(String label, List<Link> values) {
		backwardAttributes.add(new Entry<String,List<Link>>(label,values));}
}
