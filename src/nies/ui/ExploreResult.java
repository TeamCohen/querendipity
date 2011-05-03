package nies.ui;

import java.util.ArrayList;
import java.util.List;

public class ExploreResult extends Result {
	private List<Entry<String,List<String>>> forwardAttributes=new ArrayList<Entry<String,List<String>>>();
	private List<Entry<String,List<String>>> backwardAttributes=new ArrayList<Entry<String,List<String>>>();
	public ExploreResult() {}
	public ExploreResult(Result v_tabresult) {
		setLabel(v_tabresult.getLabel());
		setId(v_tabresult.getId());
		setRank(v_tabresult.getRank());
		setScore(v_tabresult.getScore());
	}
	public List<Entry<String, List<String>>> getForwardAttributes() {
		return forwardAttributes;
	}
	public List<Entry<String, List<String>>> getBackwardAttributes() {
		return backwardAttributes;
	}
	public void addForwardAttribute(String label, List<String> values) {
		forwardAttributes.add(new Entry<String,List<String>>(label,values));
	}
	public void addBackwardAttribute(String label, List<String> values) {
		backwardAttributes.add(new Entry<String,List<String>>(label,values));}
}
