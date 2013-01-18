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

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SynchronizerService</code>.
 */

public interface SynchronizerServiceAsync {

	void addUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Boolean> callback);

	void checkExistingUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Boolean> callback);

	void removeUserWithCount(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Integer> callback);

	void replicateWorkoutsForAllUsers(AsyncCallback<Integer> callback);

	void setUserActive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Void> callback);

	void setUserInactive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Void> callback);

	void replicateWorkoutsForUser(Long userId, String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, Date startDate,
			Date endDate, AsyncCallback<Integer> callback);

}
