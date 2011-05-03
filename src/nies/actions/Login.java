package nies.actions;

import java.util.Map;

import javax.servlet.ServletContext;

import nies.data.ApplicationDataController;
import nies.data.User;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;

/** Manages logging in and out */
public class Login extends NiesSupport implements SessionAware, ServletContextAware {
	private static final Logger logger = Logger.getLogger(Login.class);
	protected  String name = "Login/Logout";
	private String username;
	private String password;
	private Map<String,Object> sessionMap;
	private ApplicationDataController adc;
	/** Action method for logging in. */
	public String login() { 
		User u = adc.authenticateUser(username, password);
		if (u == null) {
			if (username == null || !adc.getUview().getUserByUsernameMap().containsKey(username)) {
				   addActionMessage("Unknown user "+username+".");
			} else addActionMessage("Incorrect password for user "+username+".");
			return INPUT;
		}
		sessionMap.put(Constants.USER,      u.getUsername());
		sessionMap.put(Constants.USERTOKEN, u.getCurrentToken());
                logger.debug("User "+u.getUsername()+" logged in");
		return SUCCESS; 
	}
	
	/** Action method for logging out. */
	public String logout() {
		String username = (String) sessionMap.remove(Constants.USER);
		sessionMap.remove(Constants.USERTOKEN);
                logger.debug("User "+username+" logged out");
		return SUCCESS;
	}
	
	public void setSession(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
	public void setServletContext(ServletContext sc) {
		this.adc = (ApplicationDataController) sc.getAttribute("dataController");
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
