package nies.actions;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

/** Utility superclass for all NIES Actions. */
public class NiesSupport extends ActionSupport {
	private static final Logger logger = Logger.getLogger(NiesSupport.class);
	private static final long serialVersionUID = 1L;
	public static final String ADVANCED_SUCCESS="advancedSuccess";
	public static final String LIST="list";

	public static final String STOP_EDGES = "nies.stopEdges",
	                           DEFAULT_QUERY = "nies.defaultQuery",
	                           USING_STOP_EDGES = "nies.usingStopEdges",
	                           USING_DEFAULT_QUERY = "nies.usingDefaultQuery";
	
	public static final String TAB_TIMEOUT = "nies.tabTimeout";
	
//	protected String name="NiesSupport";

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}

	protected int makePositiveInt(String number, String name) { return makeInt(number, name); }
	protected int makeInt(String number, String name) {
		if (number.length() < 1) return -1;
		int ret;
		try {
			ret = Integer.parseInt(number);
			return ret;
		} catch (NumberFormatException e) { 
			logger.warn("Wrong format for "+name+" (received '"+number+"')", e);
			return -1;
		}
	
	}
	
}
