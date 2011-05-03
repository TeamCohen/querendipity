package nies.interceptors;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import nies.actions.NiesSupport;
import nies.actions.UserAware;
import nies.data.ApplicationDataController;
import nies.data.User;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/* Adapted from Struts 2 In Action by Don Brown et al, 2008 */

/**
 * This interceptor provides authentication for the secure actions of the application.
 * It does two things.  First, it checks the session scope map to see if there's user 
 * object present, which indicates that the current user is already logged in.  If this
 * object is not present, the interceptor alters the workflow of the request by returning 
 * a login control string that causes the request to forward to the login page.
 * 
 * If the user object is present in the session map, then the interceptor injects the user
 * object into the action by calling the setUser method, and then allows the processing of 
 * the request to continue.  
 * 
 * Adapted from Struts 2 In Action by Don Brown et al, 2008
 */

public class AuthenticationInterceptor implements Interceptor {
	private static final Logger logger = Logger.getLogger(AuthenticationInterceptor.class);

	public void destroy() { }

	public void init() { }

	public String concludeAuthenticated(ActionInvocation actionInvocation) throws Exception {
		return actionInvocation.invoke();
	}
	public String concludeUnauthenticated(ActionInvocation actionInvocation) throws Exception {
		return Action.LOGIN;
	}
	public String intercept( ActionInvocation actionInvocation ) throws Exception {

	    Action action = ( Action ) actionInvocation.getAction();
//	    if (action instanceof NiesSupport) { //eww ewww ewwww
//			logger.debug("Checking auth status for '"+((NiesSupport)action).getName()+"' action...");
//	    } else {
	    	logger.debug("Checking auth status for "+action.toString()+" "+actionInvocation.getInvocationContext().getName());
//	    }
		/* Get the session map from the invocation context ( the ActionContext actually )
		 * and check for the user object.  Note, we are not going directly to the Servlet
		 * API's session object, even though this is most likely the map being returned 
		 * by this code; we need to keep our Struts 2 components cleanly separated from the 
		 * Servlet API so that our testing can be as simple as faking a map, rather than
		 * faking Servlet API objects.
		 */
		Map session                   = actionInvocation.getInvocationContext().getSession();
		ApplicationDataController adc = (ApplicationDataController) ServletActionContext.getServletContext().getAttribute("dataController");
		
		/* As a side effect of the structure of this sample application, we might have
		 * stale tokens.  By stale, we mean that a perhaps a user logged in under a different
		 * version of this app, then came to this version.  This would mean that the
		 * User token stored in the session would belong to a different package, as per
		 * our chapter based package structure.  We will handle this by purging any stale
		 * tokens before doing the true work of this interceptor.
		 */		
		purgeStaleTokens(session);
		
		String username  = (String) session.get( nies.actions.Constants.USER );
		String token     = (String) session.get( nies.actions.Constants.USERTOKEN);
		User user        = adc.authenticateUser(username, token);
		/* 
		 * If user doesn't exist return the LOGIN control string.  This will cause the 
		 * execution of this action to stop and the request will return the globally defined
		 * login result.
		 */
		if (user == null) {
			logger.debug("User not authenticated");
			String oldActionName = actionInvocation.getInvocationContext().getName();
			if (!oldActionName.startsWith("Login") &&
				!oldActionName.equals("Header")) session.put(nies.actions.Constants.LASTACTION, oldActionName);
		    return this.concludeUnauthenticated(actionInvocation);
		} 
		
		
		/*
		 * If the user is logged in, get the action, inject the user, then continue the 
		 * execution of this request by invoking the rest of the interceptor stack, and ultimately,
		 * the action. 
		 */
		else {
			logger.debug("User authenticated");
		    
		    if (action instanceof UserAware) {
		        ((UserAware)action).setUser(user, this);
		    }
		    
		    /*
		     * We just return the control string from the invoke method.  If we wanted, we could hold the string for
		     * a few lines and do some post processing.  When we do return the string, execution climbs back out of the 
		     * recursive hole, through the higher up interceptors, and finally arrives back at the actionInvocation itself,
		     * who then fires the result based upon the result string returned.
		     */
		    return concludeAuthenticated(actionInvocation);
		}

	}
	
	private void purgeStaleTokens (Map session ){
		// TODO: check USERTOKEN against the AppDaCon (or whatever) and retire any stale logins.
	}
	
}
