package nies.data;

import org.apache.log4j.Logger;

public class User implements Comparable<User> {
	private static final Logger logger = Logger.getLogger(User.class);
	private String uid="", username="", passHash="", email="";
	private boolean isAdmin=false;
	private String currentToken="";
	public User() {}
	public User(String uid, String username, String passHash, String email) {
		this(uid,username,passHash,email,false,""); 
	}
	public User(String uid, String username, String passHash, String email, boolean isAdmin, String currentToken) {
		this.uid      = uid;
		this.username = username;
		this.passHash = passHash;
		this.email    = email;
		this.isAdmin  = isAdmin;
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
	public String getCurrentToken() {
		// TODO: Placeholder for token update
		return passHash;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public void setCurrentToken(String currentToken) {
//		this.currentToken = currentToken;
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
		// set CurrentToken to something clever
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
	}
	@Override
	public int compareTo(User o) {
		return this.uid.compareTo(o.getUid());
	}
}
