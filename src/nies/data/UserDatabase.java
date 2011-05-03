package nies.data;

import java.io.File;

import org.apache.log4j.Logger;

import com.sleepycat.bind.serial.SerialSerialKeyCreator;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.*;

public class UserDatabase {
	private static final Logger logger = Logger.getLogger(nies.data.UserDatabase.class);
	private static final String CLASS_CATALOG = "java_class_catalog";
	private static final String USER_STORE    = "user_store";
	private static final String USER_USERNAME_INDEX = "user_username_index";
	private Environment env;

	private Database userDB;
	private SecondaryDatabase userByUsernameDB;
	private StoredClassCatalog javaCatalog;
	public Environment getEnv() {
		return env; 
	}
	public SecondaryDatabase getUserByUsernameDB() {
		return userByUsernameDB;
	}
	public Database getUserDB() {
		return userDB;
	}
	public StoredClassCatalog getJavaCatalog() {
		return javaCatalog;
	}

	public UserDatabase (String dir) throws DatabaseException {
		// create environment
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		try {
			File parentDir = new File(dir);
			if (parentDir.exists()) {
				envConfig.setAllowCreate(false);
			} else {
				logger.warn("No user database exists at "+parentDir.getAbsolutePath() +"; creating a new one");
				envConfig.setAllowCreate(true); parentDir.mkdir();
			}
			this.env = new Environment(parentDir, envConfig);
		
		
			// create class catalog and primary databases
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setTransactional(true);
			dbConfig.setAllowCreate(true);
			Database catalogDb = this.env.openDatabase(null, CLASS_CATALOG, dbConfig);
			this.javaCatalog = new StoredClassCatalog(catalogDb);
			this.userDB = this.env.openDatabase(null, USER_STORE, dbConfig);
			
			// create secondary indices
			SecondaryConfig secConfig = new SecondaryConfig();
			secConfig.setTransactional(true);
			secConfig.setAllowCreate(true);
			secConfig.setKeyCreator(new UsersByUsernameKeyCreator(this.javaCatalog, String.class, UserData.class, String.class));
			this.userByUsernameDB = this.env.openSecondaryDatabase(null, USER_USERNAME_INDEX, this.userDB, secConfig);
		

		} catch (DatabaseException f) { 
			throw new IllegalStateException("Problem creating User database!",f);			
		}
	}
	
	public void close() throws DatabaseException {
		logger.debug("Closing userByUsername...");
		try { this.userByUsernameDB.close(); }
		catch(Exception e) { logger.error("Problem closing userByUsername", e); }
		logger.debug("Closing user...");
		try { this.userDB.close(); }
		catch(Exception e) { logger.error("Problem closing user", e); }
		logger.debug("Closing javaCatalog...");
		try { this.javaCatalog.close(); }
		catch(Exception e) { logger.error("Problem closing javaCatalog", e); }
		logger.debug("Closing env...");
		try { this.env.close(); }
		catch(Exception e) { logger.error("Problem closing environment", e); }
		logger.debug("Databases closed.");
	}
	
	/**** private classes ****/
	private class UsersByUsernameKeyCreator extends SerialSerialKeyCreator {
		private UsersByUsernameKeyCreator(StoredClassCatalog catalog, Class primaryKeyClass, Class valueClass, Class indexKeyClass) {
			super(catalog,primaryKeyClass,valueClass,indexKeyClass);
		}

		public Object createSecondaryKey(Object primarykey, Object value) {
			return ((UserData)value).getUsername();
		}
	}
}
