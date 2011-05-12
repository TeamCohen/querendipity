package nies.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;

import nies.data.ApplicationDataController;
import nies.data.User;
import nies.metadata.NiesConfig;

import com.opensymphony.xwork2.interceptor.Interceptor;
import com.sleepycat.je.DatabaseException;

public class Profile extends NiesSupport implements UserAware,ServletContextAware {
	private static final Logger logger = Logger.getLogger(Profile.class);
	public static final String READING_HISTORY_DIRECTORY_PROP="nies.readinghistory";
	protected static final String TASK_VIEW="view", TASK_VIEWRO="view-ro", TASK_EDIT="edit",TASK_SAVE="save";
	protected User loggedInUser;
	protected User viewingUser;
	protected String readingHistory;
	protected File rhist;
	protected String task;
	protected String username;
	
	protected ApplicationDataController adc;
	
	protected void init() {
		if (username != loggedInUser.getUsername()) {
			User u = adc.getUview().getUserByUsernameMap().get(username);
			if (u != null) viewingUser = u;
			else this.addActionMessage("No user named '"+username+"'.");
		}
		String dirname = NiesConfig.getProperty(READING_HISTORY_DIRECTORY_PROP);
		if (dirname==null) {
			this.addActionError("Reading history storage directory wasn't configured! Blame the admin.");
		}
		File rhistdir = new File(dirname);
		if (!rhistdir.exists()) {
			this.addActionError("Reading history storage directory configured but does not exist! Blame the admin.");
		}
		rhist = new File(rhistdir,viewingUser.getUsername());
	}
	
	public String view() throws IOException {
		task = TASK_VIEW;
		if (username != this.loggedInUser.getUsername()) task = TASK_VIEWRO;
		
		init();
		if (this.hasActionErrors()) return ERROR;
		if (!rhist.exists()) {
			logger.info("No reading history currently exists; making a blank one");
			readingHistory = "";
			return SUCCESS;
		}
		BufferedReader reader = new BufferedReader(new FileReader(rhist));
		StringBuilder sb = new StringBuilder();
		for (String line; (line = reader.readLine())!= null; sb.append("\n").append(line));
		reader.close();
		readingHistory = sb.substring(1);
		logger.debug(readingHistory.length()+" chars of reading history read");
		return SUCCESS;
	}
	
	public String edit() throws IOException { 
		String ret = view();
		if (username == loggedInUser.getUsername()) task = TASK_EDIT;
		return ret; 
	}
	
	public String save() throws Exception {
		task = TASK_SAVE;
		if (username != loggedInUser.getUsername()) {
			this.addActionError("You're not allowed to edit users other than yourself. Logged-in user is '"
					+loggedInUser.getUsername()+"', requested user is '"+username+"'.");
			return ERROR;
		}
		init();
		if (this.hasActionErrors()) return ERROR;
		BufferedWriter writer = new BufferedWriter(new FileWriter(rhist));
		logger.debug("Writing "+(readingHistory == null ? "null" : readingHistory.length())+" chars.");
		writer.write(readingHistory);
		writer.close();
		try {
			logger.debug("Saving user publish-as information...");
			adc.saveUser(loggedInUser);
		} catch (Exception e) {
			addActionError("Problem while saving publish-as information to user database.");
			logger.error("Problem saving publish-as information to user database:",e);
			throw(e);
		}
		return SUCCESS;
	}
	
	@Override
	public void setUser(User u, Interceptor i) {
		loggedInUser = u;
		viewingUser = u;
		username = u.getUsername();
	}

	public User getProfile() {
		return viewingUser;
	}

	public String getReadingHistory() {
		return readingHistory;
	}

	public void setReadingHistory(String readingHistory) {
		this.readingHistory = readingHistory;
	}

	public String getTask() {
		return task;
	}

	@Override
	public void setServletContext(ServletContext sc) {
		adc = (ApplicationDataController) sc.getAttribute("dataController");
	}

	public String getU() {
		return username;
	}

	public void setU(String username) {
		this.username = username;
	}

}
