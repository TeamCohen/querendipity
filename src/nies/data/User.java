package nies.data;

import org.apache.log4j.Logger;

public class User implements Comparable<User> {
	private static final Logger logger = Logger.getLogger(User.class);
	private String uid="", username="", passHash="", email="";
	private boolean isAdmin=false;
	private String publishAs="";
	// currentToken nuked on 28 Apr 2011; leaving the field in 
	// so as not to invalidate existing user databases
	private String currentToken="";
	public User() {}
	public User(String uid, String username, String passHash, String email, String publishAs) {
		this(uid,username,passHash,email,false,publishAs,""); 
	}
	public User(String uid, String username, String passHash, String email, boolean isAdmin, String publishAs, String currentToken) {
		this.uid      = uid;
		this.username = username;
		this.passHash = passHash;
		this.email    = email;
		this.isAdmin  = isAdmin;
		this.publishAs=publishAs;
		this.currentToken=currentToken;
	}
	public String getUid() {
		return uid;
	}
	public String getUsername() {
		return username;
	}
	public String getPassHash() {
		return passHash;
	}
	public String getEmail() {
		return email;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public String getPublishAs() {
		return publishAs;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public void setPublishAs(String publishAs) {
		this.publishAs=publishAs;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassHash(String passHash) {
		this.passHash = passHash;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void updateToken() {
		logger.debug("Updating token");
		currentToken = String.valueOf((username + System.currentTimeMillis()).hashCode());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("\tUsername: ");
		sb.append(this.username);
		sb.append("\n\tEmail: ");
		sb.append(this.email);
		sb.append("\n\tUid: ");
		sb.append(this.uid);
		return sb.toString();
	}
	public static void loadUser(User fromDb, User local) {
		loadUser(fromDb, local, true);
	}
	
	public static void loadUser(User fromDb, User local, boolean overwriteLocals) {
		if (overwriteLocals || local.getUid().length()      == 0) local.setUid(fromDb.getUid());
		if (overwriteLocals || local.getUsername().length() == 0) local.setUsername(fromDb.getUsername());
		if (overwriteLocals || local.getPassHash().length() == 0) local.setPassHash(fromDb.getPassHash());
		if (overwriteLocals || local.getEmail().length()    == 0) local.setEmail(fromDb.getEmail());
		if (overwriteLocals || local.getPublishAs().length()== 0) local.setPublishAs(fromDb.getPublishAs());
		if (overwriteLocals || local.getCurrentToken().length()== 0) local.setCurrentToken(fromDb.getCurrentToken());
	}
	private void setCurrentToken(String ct) {
		currentToken = ct;
		
	}
	@Override
	public int compareTo(User o) {
		return this.uid.compareTo(o.getUid());
	}
	public String getCurrentToken() {
		return currentToken;
	}
}
