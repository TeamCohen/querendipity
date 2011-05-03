package nies.validation;

import nies.data.User;
import nies.data.UserView;

import com.opensymphony.xwork2.validator.ValidationException;

public class UsernameExistsValidator extends UsernameValidator {

	protected void userExists(Object object, UserView view, String username)
			throws ValidationException {}

	protected void userDoesntExist(Object object, UserView view, String username)
			throws ValidationException {
		addFieldError(getFieldName(),object);
		logger.debug("Validation failure: Couldn't find user with name "+username);
	}

}
