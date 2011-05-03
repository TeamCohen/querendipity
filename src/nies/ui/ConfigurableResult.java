package nies.ui;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableResult extends Result {
	public static final int PROSE_WORD_LENGTH=4;
	private List<Entry<String,List<Link>>> attributes=new ArrayList<Entry<String,List<Link>>>();
	
	public ConfigurableResult() {}
	public ConfigurableResult(Result vanilla_tabresult) {
		setLabel(vanilla_tabresult.getLabel());
		setId(vanilla_tabresult.getId());
		setRank(vanilla_tabresult.getRank());
		setScore(vanilla_tabresult.getScore());
	}
	
	public List<Entry<String,List<Link>>> getAttributes() { return attributes; }
	public void addAttribute(String label, List<Link> values) {
		AttributeEntry<String, List<Link>> ae = new AttributeEntry<String,List<Link>>(label, values);
		attributes.add(ae);
		if (values.size() > 0) 
			ae.setProse(isProse(values.get(0).anchorText) || isProse(values.get(values.size()-1).anchorText));
	}
	protected boolean isProse(String value) {
		// we could use regexes here but we need the performance boost of doing it by hand.
		int l = value.length();
		int words = 0; int inword = -1;
		for (int i=0; i<l; i++) {
			if (value.charAt(i) <= '\u0020') { // it's whitespace
				if (inword == 1) {
					words++;
					if (words >= PROSE_WORD_LENGTH) return true;
				}
				inword = 0;
			} else { inword = 1; }
		}
		if (inword == 1) words++;
		return words >= PROSE_WORD_LENGTH;
	}
	public class Link {
		public String href;
		public String anchorText;
		public Link(String a, String h) { href = h; anchorText = a; }
	}
	public class AttributeEntry<K,V> extends Entry<K,V> {
		protected boolean prose=false;
		public AttributeEntry(K k, V val) {
			super(k, val);
		}
		public boolean isProse() { return prose; }
		public void setProse(boolean p) { prose=p; }
	}
}
