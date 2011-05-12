package nies.ui;

import edu.cmu.lti.util.html.FHtml;
import edu.cmu.pra.data.PMAbsInfor;
import ghirl.graph.NodeFilter;
import nies.actions.search.ModelBasedSearch;

import org.apache.log4j.Logger;
public class PMPaperTab extends Tab {
	static final Logger log = Logger.getLogger(PMPaperTab.class);
	
	protected PMPaperTab(String name) { this.title = name; this.displayType = Tab.PAPER; }
	public NodeFilter getFilter() { 
		return null;//new NodeFilter("isa=$paper"); 
	}

	protected Result processResult(Object result, Result v_tabresult) {
		if (paperCollection == null) throw new IllegalArgumentException(PMPaperTab.class.getSimpleName()+" requires a non-null PaperCollection object.");
		PMResult tabresult = new PMResult(v_tabresult);
		log.debug("label: "+tabresult.getLabel()+" id: "+tabresult.getId());
		String pmid = ModelBasedSearch.getPMIDfromNodeName(tabresult.getId());

		String url=PMAbsInfor.getAbsURL(pmid);

		PMAbsInfor abs=paperCollection.getStruct(pmid);

		if (abs==null){
			log.warn(pmid+" is not found in the paper collection");
//			log.warn("eql("+pmid+",17364011)="+pmid.equals("17364011"));
//			log.warn("cmp("+pmid+",17364011)="+pmid.compareTo("17364011"));
			tabresult.setPmid(url, pmid);
			return tabresult;
		}

		//tabresult.setCitation(abs.getCitation());
//		tabresult.setYear(abs.year);  
//		tabresult.setAuthorsString(abs.vAuthors.join(" "));    	
//		tabresult.setGenesString(abs.vChemicals.join(" "));

		tabresult.setPmid(url, pmid);
		tabresult.setHref(url);
		//tabresult.setHrefsource("www.ncbi.nlm.nih.gov");

		tabresult.setLabel(abs.getCitation());

		//QueryCache qc= ModelBasedSearch.mCache.get(pmi)
		String exp=ModelBasedSearch.mCacheExplanations.get(pmid);
		if (exp!=null) {
			log.debug("Reasons: "+exp);
			tabresult.setReasons(exp);
		} else {
			log.warn("explanation not found for "+pmid);
			log.warn("cache size= "+ModelBasedSearch.mCacheExplanations.size());
		}

		return tabresult;
	}
}