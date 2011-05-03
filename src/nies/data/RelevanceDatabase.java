package nies.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.sleepycat.bind.serial.SerialSerialKeyCreator;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;

public class RelevanceDatabase {
	private static final Logger logger = Logger.getLogger(nies.data.RelevanceDatabase.class);
	private static final String CLASS_CATALOG   = "java_class_catalog";
	private static final String RELEVANCE_STORE = "relevance_store";
	private static final String QUERY_STORE     = "query_store";
	private static final String QUERY_QUERYSTRING_INDEX = "query_querystring_index";
	private static final String RELEVANCE_QUD_INDEX     = "relevance_qud_index";
	private static final String RELEVANCE_U_INDEX     = "relevance_u_index";
	private Environment env;

	private Database relevanceDB;
	private Database relevanceByQudDB;
	private Database relevanceByUDB;
	private Database queryDB;
	private SecondaryDatabase queryByQuerystringDB;
	private StoredClassCatalog javaCatalog;
	private boolean queryDbEnabled=false;
	public Environment getEnv() {
		return env;
	}
	public Database getRelevanceDB() {
		return relevanceDB;
	}
	public StoredClassCatalog getJavaCatalog() {
		return javaCatalog;
	}

	public RelevanceDatabase (String dir) throws DatabaseException {
		// create environment
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		try {
			File parentDir = new File(dir);
			if (parentDir.exists()) {
				envConfig.setAllowCreate(false);
			} else {
				logger.warn("No relevancy database exists at "+parentDir.getAbsolutePath() +"; creating a new one");
				envConfig.setAllowCreate(true); parentDir.mkdir();
			}
			this.env = new Environment(parentDir, envConfig);
		} catch (DatabaseException f) { throw new IllegalStateException("Error creating new relevancy database", f); }
		
		
		// create class catalog and primary databases
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);
		Database catalogDb = this.env.openDatabase(null, CLASS_CATALOG, dbConfig);
		this.javaCatalog = new StoredClassCatalog(catalogDb);
		this.relevanceDB = this.env.openDatabase(null, RELEVANCE_STORE, dbConfig);
		
		// create secondary databases
		SecondaryConfig relSecConfig = new SecondaryConfig();
		relSecConfig.setTransactional(true);
		relSecConfig.setAllowCreate(true);
		relSecConfig.setSortedDuplicates(true);
		relSecConfig.setKeyCreator(
				new RelevanceByQUDKeyCreator(this.javaCatalog, String.class, RelevanceData.class, QUDTuple.class));
		this.relevanceByQudDB = this.env.openSecondaryDatabase(null, RELEVANCE_QUD_INDEX, relevanceDB, relSecConfig);
		
		SecondaryConfig relSecUConfig = new SecondaryConfig();
		relSecUConfig.setTransactional(true);
		relSecUConfig.setAllowCreate(true);
		relSecUConfig.setSortedDuplicates(true);
		relSecUConfig.setKeyCreator(
				new RelevanceByUKeyCreator(this.javaCatalog, String.class, RelevanceData.class, String.class));
		this.relevanceByUDB = this.env.openSecondaryDatabase(null, RELEVANCE_U_INDEX, relevanceDB, relSecUConfig);
		
		
		try {
			this.queryDB     = this.env.openDatabase(null, QUERY_STORE, dbConfig);
			
			SecondaryConfig secConfig = new SecondaryConfig();
			secConfig.setTransactional(true);
			secConfig.setAllowCreate(true);
			secConfig.setKeyCreator(
					new QueryByQuerystringKeyCreator(this.javaCatalog, String.class, QueryData.class, String.class));
			this.queryByQuerystringDB = this.env.openSecondaryDatabase(null, QUERY_QUERYSTRING_INDEX, queryDB, secConfig);
			this.queryDbEnabled = true;
		} catch(Exception e) {
			logger.error("Couldn't set up the query database:",e);
			logger.error("No query DB; continuing anyway");
		}
	}
	
	public void close() throws DatabaseException {
		logger.debug("Closing relevance...");
		this.relevanceDB.close();
		this.relevanceByQudDB.close();
		this.relevanceByUDB.close();
		if (this.queryDbEnabled) {
			logger.debug("Closing query...");
			this.queryDB.close();
			this.queryByQuerystringDB.close();
		}
		logger.debug("Closing javaCatalog...");
		this.javaCatalog.close();
		logger.debug("Closing env...");
		this.env.close();
		logger.debug("Databases closed.");
	}
	public boolean hasQueryData() {
		return this.queryDbEnabled;
	}
	public Database getQueryDB() {
		return this.queryDB;
	}
	public Database getQueryByQuerystringDB() {
		return this.queryByQuerystringDB;
	}
	public Database getRelevanceByQudDB() {
		return this.relevanceByQudDB;
	}
	public Database getRelevanceByUDB() {
		return this.relevanceByUDB;
	}
	
	public static class QUDTuple implements Serializable {
		private String queryId, user, document;
		public QUDTuple() {}
		public QUDTuple(Query query, User user, String document) {
			this(query.getQid(), user.getUid(), document);
		}
		public QUDTuple(String queryId, String userid, String document) {
			this.queryId = queryId;
			this.user = userid;
			this.document = document;
		}
		public String getQueryId() {
			return queryId;
		}
		public void setQueryId(String queryId) {
			this.queryId = queryId;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}
	}
	
	/**
	 * Key creator to extract the query string (which is unique per queryID)
	 * @author katie
	 *
	 */
	private class QueryByQuerystringKeyCreator extends SerialSerialKeyCreator {
		private QueryByQuerystringKeyCreator(StoredClassCatalog catalog, Class primaryKeyClass, Class valueClass, Class indexKeyClass) {
			super(catalog,primaryKeyClass,valueClass,indexKeyClass);
		}
		public Object createSecondaryKey(Object primarykey, Object value) {
			return ((QueryData)value).getQueryString();
		}
	}
	
	private class RelevanceByQUDKeyCreator extends SerialSerialKeyCreator {
		private RelevanceByQUDKeyCreator(StoredClassCatalog catalog, Class primaryKey, Class value, Class indexKey) {
			super(catalog, primaryKey, value, indexKey);
		}
		public Object createSecondaryKey(Object primary, Object value) {
			RelevanceData data = (RelevanceData) value;
			return new QUDTuple(data.getQueryId(), data.getUser(), data.getDocument());
		}
	}

	private class RelevanceByUKeyCreator extends SerialSerialKeyCreator {
		private RelevanceByUKeyCreator(StoredClassCatalog catalog, Class primaryKey, Class value, Class indexKey) {
			super(catalog, primaryKey, value, indexKey);
		}
		public Object createSecondaryKey(Object primary, Object value) {
			RelevanceData data = (RelevanceData) value;
			return data.getUser();
		}
	}
	
}
