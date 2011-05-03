package nies.ui;

import java.util.*;

import ghirl.graph.NodeFilter;
import ghirl.util.Distribution;
import nies.metadata.PaperCollection;
public class PaperTab extends Tab {
	protected PaperTab(String name) { this.title = name; this.displayType = Tab.PAPER; }
	public NodeFilter getFilter() { return new NodeFilter("isa=$paper"); }
	protected Result processResult(Object result, Result v_tabresult) {
		YeastResult tabresult = new YeastResult(v_tabresult);
		String resultString = tabresult.getLabel();
		
		String theCitation = this.paperCollection.getCitation(resultString);
    	if(theCitation == null || theCitation.trim().length() == 0){
    		theCitation = "[No citation found for this paper]";
    	} else theCitation = theCitation.trim();
    	tabresult.setCitation(theCitation);
    	String[] tokens = theCitation.split("[()]");
    	if (tokens.length > 1) tabresult.setYear(tokens[1]);
    	
    	String theAuthors = "[No authors found for this paper]";
    	StringBuilder builder = new StringBuilder();
    	String delim = "", comma = ", "; 
    	String author;
    	String hrefTemplate = "http://www.yeastgenome.org/cgi-bin/reference/reference.pl?search_type=Papers+in+SGD&rm=redirect&submit=Search!&author=";
    	List authors=paperCollection.getAuthors(resultString);
    	if (null != authors) {
	    	for (Object oauthor : authors) {
	    		author = (String) oauthor;
				builder.append(delim + "<a href=\""
						+ hrefTemplate + author + "\" target=\"_blank\">" + author + "</a>");
				tabresult.addAuthor(hrefTemplate+author, author);
				delim = comma;
	    	}
	    	if (comma.equals(delim)) theAuthors = builder.toString();
    	}
    	tabresult.setAuthorsString(theAuthors);
    	
    	String theGenes = "[No genes found for this paper]";
    	builder = new StringBuilder();
    	delim = ""; String gene;
    	hrefTemplate = "http://www.yeastgenome.org/cgi-bin/locus.pl?locus=";
    	if (null != this.paperCollection.getGenes(resultString)) {
	    	for (Object ogene : this.paperCollection.getGenes(resultString)) {
	    		gene = (String) ogene;
	    		builder.append(delim + "<a href=\'" + hrefTemplate + gene + "\' target=\'_blank\'>" + gene + "</a>");
	    		tabresult.addGene(hrefTemplate+gene,gene);
	    		delim = comma;
	    	}
	    	if (comma.equals(delim)) theGenes = builder.toString();
    	}
    	tabresult.setGenesString(theGenes);
    	
    	tabresult.setPmid("http://www.yeastgenome.org/cgi-bin/reference/reference.pl?pmid=" + resultString, resultString);
    	tabresult.setHref("http://www.yeastgenome.org/cgi-bin/reference/reference.pl?pmid=" + resultString);
    	tabresult.setHrefsource("yeastgenome.org");
    	
    	return tabresult;
	}
}