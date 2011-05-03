package nies.actions;

import java.util.Map;

import nies.data.User;
import nies.data.UserData;
import nies.data.UserView;
import nies.data.ApplicationDataController;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.sleepycat.je.DatabaseException;

/** Handles user creation, retrieval, updates, and deletes */
public class UserCrud extends NiesSupport implements Preparable, ModelDriven<User> {
	private static final Logger logger = Logger.getLogger(UserCrud.class);
	private static final String TASK_CREATE="Create", TASK_EDIT="Edit", TASK_RETRIEVE="Retrieve";
	protected  String name = "UserCrud";
	private UserView view;
	private String task;
	
	private String newpassword, newpassword2;
	private User user;

	public User getUser() { return user; }
	public String getTask() { return task; }
	
	public String getNewpassword() {
		return newpassword;
	}
	public String getNewpassword2() {
		return newpassword2;
	}
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
	public void setNewpassword2(String newpassword2) {
		this.newpassword2 = newpassword2;
	}
	public void setTask(String task) { this.task = task; }
	
	public User getModel() { return this.user; }
	
	/* Struts action methods: */
	
	/** Action method for deleting a user */
	public String delete() {
		loadUser(false);
		ApplicationDataController userController = (ApplicationDataController) ServletActionContext.getServletContext().getAttribute("dataController");
		try {
			userController.deleteUser(user);
			this.addActionMessage("User '"+user.getUsername()+"' deleted.");
			return LIST;
		} catch(Exception e) {
			logger.error("Problem deleting user "+this.user.getUsername()+": ",e);
			this.addActionError("Couldn't delete user '"+this.user.getUsername()+"' from database.");
			this.addActionMessage(e.toString());
		}
		return ERROR;
	}
	
	/** Action method for saving a new user */
	public String saveCreate() { 
		return this.save(); 
	}
	
	/** Action method for saving changes to an existing user */
	public String saveEdit() { 
		loadUser(false);
		return this.save(); 
	}
	
	/** Utility method for saving user data in the context of an Action method */
	public String save() {
		ApplicationDataController userController = (ApplicationDataController) ServletActionContext.getServletContext().getAttribute("dataController");
		if (newpassword.length() > 0) user.setPassHash(UserData.enhash(newpassword));
		try {
			userController.saveUser(user);
			this.addActionMessage("User '"+this.user.getUsername()+"' saved.");
			return SUCCESS;
		} catch(Exception d) {
			logger.error("Problem saving user "+this.user.getUsername()+": ",d);
			this.addActionError("Couldn't save user '"+this.user.getUsername()+"' to database.");
			this.addActionMessage(d.toString());
		}
		return SUCCESS;
	}
	
	/** Action method for getting the create user form */
	public String create() {
		this.task = TASK_CREATE;
		logger.debug("Serving create action");
		return INPUT;
	}
	
	/** Action method for getting the edit user form */
	public String edit() {
		this.task = TASK_EDIT;
		logger.debug("Serving edit action");
		loadUser(true);
		return INPUT;
	}
	
	/** Action method for viewing a user */
	public String retrieve() {
		this.task = TASK_RETRIEVE;
		logger.debug("Serving retrieve action");
		loadUser(true);
		return SUCCESS;
	}
	
	protected User findUser() {
		User u = null;
		if (user != null) {
			if (user.getUid() != null && user.getUid().length() > 0) {
				u = (User) this.view.getUserMap().get(this.user.getUid());
			} else if (user.getUsername() != null && user.getUsername().length() > 0) {
				u = (User) this.view.getUserByUsernameMap().get(this.user.getUsername());
			} else
				logger.debug("Both uid and username are null :(");
		} else logger.debug("user is null?");
		if (u == null) {
			// TODO: remove action error here?
			this.addActionError("No user found for:"+user.toString());
			logger.debug("No matching user found.");
		}
		return u;
	}
	
	protected void loadUser(boolean overwrite) {
		User u = findUser();
		if (u != null) User.loadUser(u, user, overwrite);
		logger.debug("User loaded:\n"+user.toString());
	}
	
	public void validate() {
		if (TASK_CREATE.equals(task) && this.newpassword != null 
				&& !this.newpassword.equals(this.newpassword2)) {
			this.addFieldError("newpassword", getText("error.password.match"));
		}
	}
	
	public void prepare() {
		view = (UserView) ServletActionContext.getServletContext().getAttribute("userView");
		logger.debug("Prepare: Initializing user model to blank object");
		user = new User();
	}
}
