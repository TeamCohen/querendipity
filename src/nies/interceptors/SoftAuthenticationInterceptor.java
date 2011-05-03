package nies.interceptors;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Allows action to invoke regardless of authentication status; notifies UserAware actions if in fact the user is authed.
 * @author katie
 *
 */
public class SoftAuthenticationInterceptor extends AuthenticationInterceptor {
	public String concludeAuthenticated(ActionInvocation actionInvocation) throws Exception {
		return actionInvocation.invoke();
	}
	public String concludeUnauthenticated(ActionInvocation actionInvocation) throws Exception {
		return actionInvocation.invoke();
	}
}
