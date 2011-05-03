package nies.actions;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import nies.data.UserView;

import com.opensymphony.xwork2.ActionSupport;

/** Provides acces to the full list of users in the system. */
public class UserList extends NiesSupport {
	private static final long serialVersionUID = -8111180243650924939L;
	private static final Logger logger = Logger.getLogger(UserList.class);
	protected  String name = "UserList";
	private Set users;
	public Set getUsers() { return users; }
	public void setUsers(Set users) { this.users = users; }
	public String getNusers() {
		if (users != null)
			return String.valueOf(users.size());
		return "(null set)";
	}
	public String execute() { 
		logger.debug("Serving list users action");
		UserView view = (UserView) ServletActionContext.getServletContext().getAttribute("userView");
		this.users = view.getUserEntrySet();
		logger.debug("User table loaded");
		return SUCCESS; 
	}
}
