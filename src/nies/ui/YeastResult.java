package nies.ui;

import java.util.ArrayList;
import java.util.List;

import nies.ui.Result.Link;

public class YeastResult extends Result {
	private String citation, authorsString, genesString, year;
	private String href="", hrefsource="";
	private List<Link> authors, genes;
	private Link pmid;
	
	public YeastResult() {}
	public YeastResult(Result vanilla_tabresult) {
		setId(vanilla_tabresult.getId());
		setLabel(vanilla_tabresult.getLabel());
		setRank(vanilla_tabresult.getRank());
		setScore(vanilla_tabresult.getScore());
	}
	
	public String getHref()     { return this.href;      }
	public String getHrefsource() { return this.hrefsource; }
	public String getAuthorsString()  { return this.authorsString;   }
	public String getGenesString()    { return this.genesString;     }
	public Link getPmid()     { return this.pmid;      }
	public String getCitation() { return this.citation;  }
	public List<Link> getAuthors() { return this.authors; }
	public List<Link> getGenes()   { return this.genes; }
	public String getYear() { return year; }
	
	public void setYear(String year) { this.year = year; }
	public void setAuthorsString(String authors)   { this.authorsString = authors;   }
	public void setGenesString(String genes)       { this.genesString = genes;       }
	public void setCitation(String citation) { this.citation = citation; }
	public void setHref(String href)         { this.href = href;         }
	public void setHrefsource(String hrefsource) { this.hrefsource = hrefsource; }

	public void setPmid(String href, String text)   { 
		this.pmid = new Link(href,text);
	}
	public void addAuthor(String href, String text) {
		if (this.authors == null) this.authors = new ArrayList<Link>();
		this.authors.add(new Link(href,text));
	}
	public void addGene(String href, String text) {
		if (this.genes == null) this.genes = new ArrayList<Link>();
		this.genes.add(new Link(href,text));
	}
}
