package nies.actions;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.util.Distribution;
import ghirl.util.TreeDistribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.sleepycat.collections.StoredSortedValueSet;

import nies.actions.search.Search;
import nies.data.Relevance;
import nies.data.RelevanceData;
import nies.data.RelevanceView;
import nies.data.User;
import nies.exceptions.BadConfigurationError;
import nies.metadata.Init;
import nies.metadata.NiesConfig;
import nies.ui.ConfigurableTab;
import nies.ui.Result;
import nies.ui.Tab;

public class PositiveDocuments extends NiesSupport implements ServletContextAware,UserAware,Preparable {
	public static final String NIES_POSITIVEDOCUMENTS_TAB_PROP="nies.positivedocuments.tab";
	private static final Logger logger = Logger.getLogger(PositiveDocuments.class);
	private RelevanceView relevanceView;
	private int nrelevancies;
	private List<Relevance> relevancies;
	private User user;
	private int pagesize=100;
	private int pagenum=1;
	private String tabname;
	private List<Tab> tabs;
	private Graph graph;
	
	public void prepare() {
		tabname = NiesConfig.getProperty(NIES_POSITIVEDOCUMENTS_TAB_PROP);
	}
	
	public String execute() {
		if (tabname == null) {
			this.addActionError("Blame the admin: Not configured for showing positive documents. (missing the tab configuration name under "+NIES_POSITIVEDOCUMENTS_TAB_PROP+")");
		}
		
		Collection<Relevance> rset = relevanceView.getRelevanceByUMap().duplicates(user.getUid());
		relevancies = new ArrayList<Relevance>(rset.size());
		relevancies.addAll(rset);
		Collections.sort(relevancies, Relevance.ALPHA_BY_QUERYTERMS);
		nrelevancies = relevancies.size();
		this.pagenum = Math.max(Math.min(pagenum, (int) Math.ceil((double)nrelevancies/pagesize)), 1);
		this.relevancies = paginateRelevancies();
		nrelevancies = relevancies.size();
		
		tabs = new ArrayList<Tab>();
		arborateRelevancies();
		if (this.hasActionErrors()) return ERROR;
		return SUCCESS;
	}
	
	protected List<Relevance> paginateRelevancies() {
		logger.debug("Getting relevancies...");
		if (this.pagesize < 0) {
			logger.debug("Showing all relevances in log");
			return this.relevancies;
		}
		logger.debug("Getting page "+this.pagenum+" of size "+this.pagesize+" from log...");
		if (relevancies == null) return new ArrayList<Relevance>();
		try {
			int fromindex, toindex;
			fromindex = (pagenum-1)*pagesize+1;
			toindex   = pagenum*pagesize;
			fromindex = Math.max(fromindex, 0);
			toindex   = Math.min(toindex, nrelevancies);
			return this.relevancies.subList(fromindex, toindex);
		} catch (Exception e) {
			logger.debug("Problem fetching relevancies",e);
			logger.debug("Showing all relevances in log");
			return this.relevancies;
		}
	}
	
	protected void arborateRelevancies() {
		String currentQuery = "";
		ConfigurableTab openTab = null;
		Distribution openDistribution = null;
		try {
			for (Relevance r:relevancies) {
				if (!r.getType().equals(Relevance.PROMOTE))
					logger.warn("Non-promote relevance in positive documents relevance set: "+r.toString());
				String query = r.getQuery();
				if (!currentQuery.equals(query)) {
					if (openTab != null) 
						finishTab(openTab,openDistribution);
					openTab = (ConfigurableTab) Tab.makeTab(Tab.CONFIGURABLE, this.tabname);
					openTab.setTitle(query);
					openDistribution = new TreeDistribution();
					tabs.add(openTab);
					currentQuery = query;
				}
				openDistribution.add(1.0, GraphId.fromString(r.getDocument()));
			}
		} catch (BadConfigurationError e) {
			this.addActionError(e.getMessage());
		}
		if (openTab != null) finishTab(openTab,openDistribution);
	}
	
	protected void finishTab(ConfigurableTab t, Distribution d) {
		logger.info("Setting graph on tab "+t.getTitle()+" to "+graph);
		t.setGraph(graph);
		t.init(d, d.size(), d.size(), 1.0);

		for(Result r : t.getResults()) {
			r.setRelevanceMark(Relevance.PROMOTE);
		}
	}

	public void setServletContext(ServletContext context) {
		this.relevanceView = (RelevanceView)(context.getAttribute("relView"));
		this.graph = (Graph) context.getAttribute(Init.SERVLETCONTEXT_GRAPH);
	}
	public List<Relevance> getRelevancies() {
		return this.relevancies;
	}
	public void setRelevancies(ArrayList<Relevance> relevancies) {
		this.relevancies = relevancies;
	}
	@Override
	public void setUser(User u, Interceptor i) {
		user = u;
	}

	public int getNrelevancies() {
		return nrelevancies;
	}

	public User getUser() {
		return user;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public List<Tab> getTabs() {
		return tabs;
	}

}
