package nies.actions;

import org.apache.log4j.Logger;

import nies.data.User;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.Interceptor;

/** Holds user information to be conditionally displayed in the header for all pages. */
public class Header extends NiesSupport implements UserAware, ModelDriven {
	private static final Logger logger = Logger.getLogger(Header.class);
	protected  String name = "Header";
	private User user;
	public void setUser(User u, Interceptor i) {
		if (i != null) {
			this.user = u;
			logger.debug("Set user '"+u.getUsername()+"' for Header action");
		}
	}
	
	public Object getModel() {
		return this.user;
	}
	public boolean isLoggedIn() {
		return this.user != null;
	}

}
