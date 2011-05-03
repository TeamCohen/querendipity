package nies.validation;

import nies.data.User;
import nies.data.UserView;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public abstract class UsernameValidator extends FieldValidatorSupport {
	Logger logger = Logger.getLogger(UsernameValidator.class);
	protected String idField;
	public String getIdField() {
		return idField;
	}
	public void setIdField(String idField) {
		this.idField = idField;
	}
	public void validate(Object object) throws ValidationException {
		String field    = getFieldName();
		String username = (String) getFieldValue(field, object);
		UserView view   = (UserView) ServletActionContext.getServletContext().getAttribute("userView");
		if (username != null && view.getUserByUsernameMap().containsKey(username)) {
			userExists(object, view, username);
		} else {
			userDoesntExist(object, view, username);
		}
	}
	protected abstract void userExists(Object object, UserView view, String username) throws ValidationException;
	protected abstract void userDoesntExist(Object object, UserView view, String username) throws ValidationException;
}
