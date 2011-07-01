package nies.actions.search;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.Interceptor;

import nies.actions.NiesSupport;
import nies.actions.UserAware;
import nies.data.User;

/**
 * This is a sneaky action which is run twice to get to search.
 * The first time, it builds the search form, collecting input data and user 
 * 		preferences on how to search.
 * The second time, it spoofs the struts execution stack by calling the 
 * 		execute() method on the child Search object and then setting this 
 * 		object as the domain model. The Results JSP can then read the
 * 		appropriate Search action object as if it were at the top of the
 * 		stack -- but only assuming that there are no parameter collisions
 * 		between SearchDispatcher and the Search actions it can dispatch
 * 		to, and only assuming that all Search actions display in the same
 * 		Results JSP/Tiles hierarchy.
 * @author krivard
 *
 */
public class SearchDispatcher extends NiesSupport implements ModelDriven<SearchForm> {
	private static final Logger logger = Logger.getLogger(SearchDispatcher.class);
	/** Must match the selector IDs in searchform.jsp **/
	public static final String ORDERBY_KEYWORD="Keyword", ORDERBY_READING="ReadingRex", ORDERBY_CITATION="CiteRex", ORDERBY_TOPIC="Topic";
	private String orderBy="";
	private SearchForm model=new SearchForm();
	private Map<String,String> searchRoutes = new TreeMap<String,String>();
	private String searcher="";
	
	public SearchDispatcher() {
		searchRoutes.put(ORDERBY_KEYWORD,"Search");
		searchRoutes.put(ORDERBY_READING,"ModelBasedSearch");
		searchRoutes.put(ORDERBY_CITATION, "ModelBasedSearch");
		searchRoutes.put(ORDERBY_TOPIC, "Display");
	}
	
	public void prepare() {}
	
	public String dispatch() {//throws Exception {
		if (orderBy.equals("")) return INPUT;
		searcher = searchRoutes.get(orderBy);
		return SUCCESS;
	}
	
	public SearchForm getModel() { return model; }
	public void setModel(SearchForm f) {model = f;}
	
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getSearcher() { return searcher; }
}
