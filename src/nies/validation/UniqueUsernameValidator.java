package nies.validation;

import nies.data.User;
import nies.data.UserView;

import com.opensymphony.xwork2.validator.ValidationException;

public class UniqueUsernameValidator extends UsernameValidator {
	protected void userExists(Object object, UserView view, String username) throws ValidationException {
		String uid = (String) getFieldValue(idField, object);
		logger.debug("Validating username for uid "+uid);
		if (!((User)(view.getUserByUsernameMap().get(username))).getUid().equals(uid)) {
			addFieldError(getFieldName(),object);
			logger.debug("Validation error: username exists with uid "+((User)(view.getUserByUsernameMap().get(username))).getUid());
		}
	}

	protected void userDoesntExist(Object object, UserView view, String username)
			throws ValidationException {}
}
