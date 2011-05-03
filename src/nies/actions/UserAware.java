package nies.actions;

import com.opensymphony.xwork2.interceptor.Interceptor;

import nies.data.User;

/** Provides ability for authentication interceptors to notify actions of the logged-in user */
public interface UserAware {
	public void setUser(User u, Interceptor i);
}
