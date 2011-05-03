package nies.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import nies.data.User;
import nies.metadata.NiesConfig;

import com.opensymphony.xwork2.interceptor.Interceptor;

public class ReadingHistory extends NiesSupport implements UserAware {
	private static final Logger logger = Logger.getLogger(ReadingHistory.class);
	public static final String READING_HISTORY_DIRECTORY_PROP="nies.readinghistory";
	protected static final String TASK_VIEW="view",TASK_EDIT="edit",TASK_SAVE="save";
	protected User user;
	protected String readingHistory;
	protected File rhist;
	protected String task;
	
	protected boolean init() {
		String dirname = NiesConfig.getProperty(READING_HISTORY_DIRECTORY_PROP);
		if (dirname==null) {
			this.addActionError("Reading history storage directory wasn't configured! Blame the admin.");
			return false;
		}
		File rhistdir = new File(dirname);
		if (!rhistdir.exists()) {
			this.addActionError("Reading history storage directory configured but does not exist! Blame the admin.");
			return false;
		}
		rhist = new File(rhistdir,user.getUsername());
		return true;
	}
	
	public String view() throws IOException {
		task = TASK_VIEW;
		if (!init()) return ERROR;
		if (!rhist.exists()) {
			logger.info("No reading history currently exists; making a blank one");
			readingHistory = "";
			return SUCCESS;
		}
		BufferedReader reader = new BufferedReader(new FileReader(rhist));
		StringBuilder sb = new StringBuilder();
		for (String line; (line = reader.readLine())!= null; sb.append(line).append("\n"));
		reader.close();
		readingHistory = sb.toString();
		return SUCCESS;
	}
	
	public String edit() throws IOException { 
		String ret = view();
		task = TASK_EDIT;
		return ret; }
	
	public String save() throws IOException {
		task = TASK_SAVE;
		if (!init()) return ERROR;
		BufferedWriter writer = new BufferedWriter(new FileWriter(rhist));
		logger.debug("Writing "+(readingHistory == null ? "null" : readingHistory.length())+" chars.");
		writer.write(readingHistory);
		writer.close();
		return SUCCESS;
	}
	
	@Override
	public void setUser(User u, Interceptor i) {
		user = u;
	}

	public User getUser() {
		return user;
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

}
