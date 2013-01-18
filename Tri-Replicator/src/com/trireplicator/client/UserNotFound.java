/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.client;

/**
 * Indicates that the user is not found in the system (in the repository of existing users)
 * 
 * @author Roman
 * 
 */
public class UserNotFound extends Exception {

	private static final long serialVersionUID = -3340468946801978432L;

	public UserNotFound() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserNotFound(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserNotFound(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserNotFound(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
