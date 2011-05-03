package nies.actions.search;

import java.util.List;

/**
 * ModelBasedSearchForm is the name of the action that is triggered if
someone tries to do ModelBasedSearch and hasn't selected a model, typed a query, etc.  

If ModelBasedSearch.execute() returns INPUT instead of SUCCESS, 
ModelBasedSearchForm is triggered, and the user is shown the page ModelBasedSearch.jsp.  

ModelBasedSearchForm has no associated java class; 
it's just in the struts.xml definition file.

 * @author nlao
 *
 */
public class ModelBasedSearchForm extends ModelBasedSearch {
	public String execute() {
		return SUCCESS;
	}
	String model;
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getModel() {
		return model;
	}
	

}
