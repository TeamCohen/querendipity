package nies.ui;

import java.util.ArrayList;
import java.util.List;

public class Result {

	private String id, label, rank, score;
	private String relevanceMark;
	
	public class Link {
		private String href, text;
		public String getHref() { return href; }
		public String getText() { return text; }
		public void setHref(String href) { this.href = href; }
		public void setText(String text) { this.text = text; }
		public Link() {}
		public Link(String href, String text) { this.setHref(href); this.setText(text); }
	}
	
	public String getLabel()   { return this.label;    }
	public String getRank()     { return this.rank;      }
	public String getScore()    { return this.score;     }
	public String getRelevanceMark() { return relevanceMark; }
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setRelevanceMark(String mark) { this.relevanceMark = mark; }
	public void setLabel(String r)           { this.label = r;          }
	public void setRank  (String r)          { this.rank   = r;          }
	public void setScore (String s)          { this.score  = s;          }
	
	public Result () {}
}
