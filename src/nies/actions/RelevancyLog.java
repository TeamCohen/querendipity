package nies.actions;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;

import nies.data.Relevance;
import nies.data.RelevanceData;
import nies.data.RelevanceView;

import com.opensymphony.xwork2.Preparable;
import com.sleepycat.collections.StoredSortedValueSet;

/** Provides access to the full log of relevancy data. */
public class RelevancyLog extends NiesSupport implements Preparable, ServletContextAware {
	private static final Logger logger = Logger.getLogger(RelevancyLog.class);
	protected  String name = "RelevancyLog";
	private StoredSortedValueSet<Relevance> relevancies;
	private RelevanceView relevanceView;
	private int pagesize=100;
	private int pagenum=1;
	private int nrelevancies;
	public String execute() {
		return SUCCESS;
	}
	public void prepare() {
		relevancies = relevanceView.getRelevanceSet();
		nrelevancies = relevancies.size();
	}

	public void setServletContext(ServletContext context) {
		this.relevanceView = (RelevanceView)(context.getAttribute("relView"));
	}
	public int getNrelevancies() { return nrelevancies; }
	
	public int getPagesize() {
		return pagesize;
	}
	public int getPagenum() {
		return pagenum;
	}
	public int getNpages() {
		return (int) Math.ceil((double) nrelevancies/ (double) pagesize);
	}
	public Collection<Relevance> getRelevancies() {
		logger.debug("Getting relevancies...");
		if (this.pagesize < 0) {
			logger.debug("Showing all relevances in log");
			return this.relevancies;
		}
		logger.debug("Getting page "+this.pagenum+" of size "+this.pagesize+" from log...");
		if (relevancies == null || relevancies.first() == null) return new ArrayList<Relevance>();
		try {
			int fromindex, toindex;
			int fromavailable, toavailable;
			fromindex = (pagenum-1)*pagesize+1;
			toindex   = pagenum*pagesize;
			fromavailable = Integer.parseInt(relevancies.first().getRid());
			toavailable   = Integer.parseInt(relevancies.last().getRid());
			fromindex = Math.max(fromindex, fromavailable);
			toindex   = Math.min(toindex, toavailable);
			String key = RelevanceData.keyGenerator.getKey(fromindex);
			logger.debug("Getting from ("+key+")...");
			Relevance from = relevanceView.getRelevanceMap().get(key);
			logger.debug("..."+from.toString());
			key = RelevanceData.keyGenerator.getKey(toindex);
			logger.debug("Getting to   ("+key+")...");
			Relevance to   = relevanceView.getRelevanceMap().get(key);
			logger.debug("..."+to.toString());
			logger.debug("Showing relevance log from "+from.toString()+" to "+to.toString());
			return this.relevancies.subSet(from,true,to,true);
		} catch (Exception e) {
			logger.debug("Problem fetching relevancies",e);
			logger.debug("Showing all relevances in log");
			return this.relevancies;
		}
	}
	public void setPagesize(int pagesize) {
			this.pagesize = Math.max(1, pagesize);
	}
	public void setPagenum(int pagenum) {
		this.pagenum = Math.max(1, pagenum);
	}
}
