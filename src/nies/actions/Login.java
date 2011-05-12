package nies.actions;

import java.util.ArrayList;
import java.util.List;
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
	private List<String> users=new ArrayList<String>();
	
	
	/** Action method for logging in. */
	public String login() { 
		if (username.length() == 0) return INPUT;
		User u = null;
		try {
			u = adc.authenticateUser(username, password);
		} catch(Exception e) { 
			addActionError("Problem authenticating user"); 
			return ERROR; 
		}
		if (u == null) {
			if (username == null || !adc.getUview().getUserByUsernameMap().containsKey(username)) {
				logger.debug("Unknown user "+username);
			    addActionMessage("Unknown user "+username+".");
			} else {
				logger.debug("Incorrect password");
				addActionMessage("Incorrect password for user "+username+".");
			}
			return INPUT;
		}
		sessionMap.put(Constants.USER,      u.getUsername());
		sessionMap.put(Constants.USERTOKEN, u.getCurrentToken());
                logger.debug("User "+u.getUsername()+" logged in with token "+u.getCurrentToken());
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
		logger.debug("Setting username set on "+this);
		for (String u : adc.getUview().getUserByUsernameMap().keySet()) {
			users.add(u);
		}
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
	
	public List<String> getUsers() { 
		logger.debug("Returning username set on "+this);
		return this.users; 
	}
	
}
