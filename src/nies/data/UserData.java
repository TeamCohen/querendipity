package nies.data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import org.apache.commons.codec.binary.Base64;
//import sun.misc.BASE64Encoder;

public class UserData implements Serializable {
	private static final long serialVersionUID = -8592928875681661490L;
	private static final Logger logger = Logger.getLogger(UserData.class);
	private String username, passHash, email, publishAs;
	private String currentToken; // nuked 28 apr 2011, but leaving field so existing databases stay valid
	private boolean isAdmin=false;
	public UserData(String username, String passHash, String email, boolean isAdmin, String publishAs, String currentToken) {
		this.username = username;
		this.passHash = passHash;
		this.email    = email;
		this.isAdmin  = isAdmin;
		this.publishAs=publishAs;
		this.currentToken=currentToken;
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
	public void setPublishAs(String p) {
		this.publishAs = p;
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
	
	public static String enhash(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
			return Base64.encodeBase64String(md.digest());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Couldn't get SHA algorithm to hash password with!",e);
		} catch (UnsupportedEncodingException e) {
			logger.error("SHA encoding unsupported?",e);
		}
		return null;
	}
	private static int userkey=0;
	public static String newKey() {
		userkey++;
		return String.format("%05d",userkey);
	}
	public static void initializeKeyGenerator(String last) {
		try {
			userkey = Integer.parseInt(last);
		} catch(NumberFormatException e) { 
			logger.error("Can't initialize primary key generator with found key \""+last+"\"",e); 
		}
	}
	public String getCurrentToken() {
		return currentToken;
	}
	public void setCurrentToken(String currentToken) {
		this.currentToken = currentToken;
	}
}
