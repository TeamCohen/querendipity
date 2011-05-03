package nies.data;

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.sleepycat.collections.StoredSortedKeySet;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.DatabaseException;

import nies.metadata.NiesConfig;

/**
 * A Tomcat servlet which manages read-write data storage for Users and 
 * Relevance Feedback.
 * 
 * <p>Because ApplicationDataController is a servlet, it uses its
 *  <code>init()</code> function to open both databases.  At this time it also
 *  adds default users to the user DB if they do not already exist.  This allows
 *  us to guarantee access to the site if all users are accidentally wiped,
 *  although it does mean that we have hard-coded passwords unencrypted in the
 *  source.   We are not pretending a high level of security here. Likewise, the
 *  servlet's <code>destroy()</code> method closes the database properly, to
 *  avoid corruption.
 * </p>
 * 
 * <p>Uses the following .properties variables:</p>
 * <ul><li><code>nies.userdb</code> - The directory enclosing the user database</li>
 *     <li><code>nies.relevancedb</code> - The directory enclosing the relevance database</li>
 *  </ul>
 *  
 * @author katie
 *
 */
public class ApplicationDataController extends HttpServlet {
	private static final long   serialVersionUID = -42742755959068770L;
	private static final Logger logger = Logger.getLogger(ApplicationDataController.class);
	UserDatabase udb;
	UserView     uview;
	RelevanceDatabase rdb;
	RelevanceView     rview;
	QueryView qview;
	
	public UserView getUview() {
		return uview;
	}

	public RelevanceView getRview() {
		return rview;
	}
	
	public QueryView getQview() {
		return qview;
	}

	public void setupDatabases() {

		String path = NiesConfig.getProperty("nies.userdb");
	    logger.info("Setting up user database at "+path);
		try {
			this.udb  = new UserDatabase(path);
			this.uview = new UserView(udb); 
		} catch (Exception e) { 
			logger.error("Problem starting up user database: ",e); 
			return;
		} 
		if (this.udb != null) {
			try {
				test();
				UserData.initializeKeyGenerator(getLastKey(this.uview));
			} catch (Exception e)         { logger.error("Problem adding test records to the database:",e); }
		    logger.info("User database and uview setup complete.");
		} else logger.warn("User database not set up!");
		
	    path = NiesConfig.getProperty("nies.relevancedb");
	    logger.info("Setting up relevance database at "+path);
	    try {
	    	this.rdb = new RelevanceDatabase(path);
	    	this.rview = new RelevanceView(rdb);
	    	RelevanceData.keyGenerator.initializeKeyGenerator(getLastKey(this.rview));
	    	this.qview = new QueryView(rdb);
	    	QueryData.keyGenerator.initializeKeyGenerator(getLastKey(this.qview));
	    } catch(DatabaseException e) {
	    	logger.error("Problem starting up relevancy database: ",e); 
	    	return;
	    }
	    if (rview != null) logger.info("Relevancy database and rview setup complete.");
	    else logger.warn("Relevancy database not set up!");

	}


	/** Sets up the user and relevance databases, tests the user database, and 
	 * saves references to the user- and relevance- views and to itself in the servlet context. */
	public void init() {
		ServletContext servletContext = getServletContext();
		
		setupDatabases();

	    if (this.uview != null) servletContext.setAttribute("userView", uview);
	    if (this.rview != null) servletContext.setAttribute("relView",  rview);
	    
	    servletContext.setAttribute("dataController",   this);
	}
	private String getLastKey(DataView view) {
		String last = ((StoredSortedKeySet<String>) view.getPrimaryKeyMap().keySet()).last();
    	if (last == null) return "0";
    	return last;
	}
	
	public void destroy() {
		logger.debug("Calling destroy() on ApplicationDataController servlet...");
		try {
			this.close();
		} catch(DatabaseException e) { logger.error("Problem shutting down database: ",e); }
		Runtime r = Runtime.getRuntime();
		logger.info("Memory left in Java runtime: "+ Math.round(((double) r.freeMemory()) / 1024 / 1024) +"MB");
	}
	
	public void close() throws DatabaseException { 
		if (udb != null) udb.close(); 
		if (rdb != null) rdb.close(); 
	}
	
	public void test() throws Exception {
		TransactionRunner runner = new TransactionRunner(udb.getEnv());
		runner.run(new AddTestUsers());
	}
	
	/** Add a user with the specified name, password, and email address.
	 * 
	 * @param username Any string
	 * @param pass The plaintext password -- this will be hashed before storage
	 * @param email
	 * @throws DatabaseException
	 * @throws Exception
	 */
	public void addUser(String username, String pass, String email) throws DatabaseException, Exception {
		TransactionRunner runner = new TransactionRunner(udb.getEnv());
		runner.run(new AddUser(username, pass, email));
	}
	
	/** Save updated data for the specified user.
	 * 
	 * @param user A User object already in the database
	 * @throws DatabaseException
	 * @throws Exception
	 */
	public void saveUser(User user) throws DatabaseException, Exception {
		TransactionRunner runner = new TransactionRunner(udb.getEnv());
		runner.run(new SaveUser(user));
	}
	
	/** Get the User object for this username and password/token.  If the 
	 * provided password does not match that stored for the user (i.e. their
	 * hashes don't match), the <code>
	 * passwordToken</code> is checked against 
	 * <code>User.getCurrentToken()</code>.
	 * 
	 * @param username
	 * @param passwordToken The plaintext password or session token.
	 * @return A User object if authentication succeeds; null otherwise.
	 */
	public User authenticateUser(String username, String passwordToken) {
		logger.debug("Authenticating "+username);
		if (username == null || !uview.getUserByUsernameMap().containsKey(username)) return null;
		User candidate = uview.getUserByUsernameMap().get(username);
		if (candidate.getPassHash().equals(UserData.enhash(passwordToken))) {
			candidate.updateToken();
			return candidate;
		}
		if (candidate.getCurrentToken().equals(passwordToken)) {
			return candidate;
		}
		return null;
	}
	
	/** Remove the specified User object from the database. */
	public void deleteUser(User user) throws DatabaseException, Exception {
		TransactionRunner runner = new TransactionRunner(udb.getEnv());
		runner.run(new DeleteUser(user));
	}
	
	/** Save (add) the specified Relevance to the database. */
	public void saveRF(Relevance r) throws DatabaseException, Exception {
		TransactionRunner runner = new TransactionRunner(rdb.getEnv());
		runner.run(new SaveRF(r));
	}
	
	public Query getQuery(String query, String queryParams) {
		String lookup = QueryData.makeQueryLookupString(query, queryParams);
		if(qview.getQueryByQuerystringMap().containsKey(lookup)) {
			return qview.getQueryByQuerystringMap().get(lookup);
		}
		return null;
	}
	public void saveQuery(Query q) throws DatabaseException, Exception {
		TransactionRunner runner = new TransactionRunner(rdb.getEnv());
		runner.run(new SaveQuery(q));
	}
	
	/** Transaction to add test users to the database. */
	private class AddTestUsers implements TransactionWorker {
		public void doWork() {
			logger.debug("TransactionWorker AddTestUsers");
			Map<String, User> users = uview.getUserMap();
			if (users.isEmpty()) {
				User u = new User(UserData.newKey(),"Harry Q. Bovik",UserData.enhash("insecure"),"bovik@cs.cmu.edu");
				users.put(u.getUid(), u);
				u = new User(UserData.newKey(),"Administrator",UserData.enhash("invisible ribosome"),"krivard@andrew.cmu.edu");
				users.put(u.getUid(), u);

				int nusers=0;
				for (Object user : uview.getUserEntrySet()) nusers++;
				logger.info("Added "+nusers+" test users");
			} else {
				logger.info("No test users added; database already nonempty");
			}
		}
	}
	
	/** Transaction to add a user to the database. */
	private class AddUser implements TransactionWorker {
		private String username, password, email;
		public AddUser(String username, String pass, String email) {
			this.username=username;
			this.password=pass;
			this.email = email;
		}
		public void doWork() {
			logger.debug("TransactionWorker AddUser");
			Map<String, User> users = uview.getUserMap();
			String key =UserData.newKey(); 
			users.put(key,
					  new User(key, username, UserData.enhash(password), email));
			logger.info("Added user "+username);
		}
	}
	
	/** Transaction to save new data about the user to the database.
	 * This class currently removes the object in the database matching the 
	 * specified user's ID and then adds the (updated) object back in.
	 * @author katie
	 *
	 */
	private class SaveUser implements TransactionWorker {
		private User user;
		public SaveUser(User user) {
			this.user = user;
		}
		public void doWork() {
			logger.debug("TransactionWorker SaveUser:\n"+this.user.toString());
			Map<String, User> usermap = uview.getUserMap();
			if (user.getUid() != null) {
				// then it's an existing user and we should update it
				if (usermap.containsKey(user.getUid())) {
					usermap.remove(user.getUid());
					logger.debug("Existing copy removed");
				} else {
					logger.error("User has id "+user.getUid()+" but there is no such user in the db.  Assigning a new number...");
					user.setUid(null);
				}
			}
			if (user.getUid() == null) { // hack for escape out of last condition
				user.setUid(UserData.newKey());
			}
			logger.debug("Putting new copy...");
			usermap.put(user.getUid(), user);
			logger.debug("Done.");
		}
	}
	
	/** Removes a user from the database */
	private class DeleteUser implements TransactionWorker {
		private User user;
		public DeleteUser(User user) {
			this.user = user;
		}
		public void doWork() {
			logger.debug("TransactioNWorker DeleteUser:\n"+this.user.toString());
			Map<String,User> usermap = uview.getUserMap();
			if (user.getUid() != null) {
				if (usermap.containsKey(user.getUid())) {
					usermap.remove(user.getUid());
					logger.debug("User removed");
				} else logger.error("No matching user exists");
			} else logger.error("User lacks a valid ID");
			logger.debug("Done.");
		}
	}
	
	/** Adds a relevance feedback entry to the database */
	private class SaveRF implements TransactionWorker {
		private Relevance r;
		public SaveRF(Relevance r) { this.r = r; }
		public void doWork() {
			logger.debug("Saving relevance "+r.toString());
			Map<String, Relevance> rels = rview.getRelevanceMap();
			rels.put(r.getRid(), r);
			logger.info("Relevance saved.");
		}
	}
	
	private class SaveQuery implements TransactionWorker {
		private Query q;
		public SaveQuery(Query q) { this.q = q; }
		public void doWork() {
			if (logger.isDebugEnabled()) logger.debug("Saving query "+q.toString());
			Map<String,Query> queries = qview.getQueryMap();
			if (q.getQid() != null) {
				// then it's an existing query and we should update it -- remove and replace
				if (queries.containsKey(q.getQid())) {
					queries.remove(q.getQid());
				} else {
					logger.error("Query has id "+q.getQid()+" but there is no such query in the db.  Assigning a new number...");
					q.setQid(null);
				}
			}
			if (q.getQid() == null) { // then it's either a new query or had an invalid number
				q.setQid(QueryData.keyGenerator.newKey());
			}
			queries.put(q.getQid(), q);
			logger.info("Query saved.");
		}
	}
}
