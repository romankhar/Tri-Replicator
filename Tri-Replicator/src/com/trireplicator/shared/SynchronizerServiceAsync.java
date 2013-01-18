/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 * @author Roman Kharkovski, http://kharkovski.blogspot.com
 * Created: Jan 18, 2013
 */

package com.trireplicator.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * 
 * @author Roman Kharkovski, http://kharkovski.blogspot.com
 */
public interface SynchronizerServiceAsync {

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#addUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	void addUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#checkExistingUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	void checkExistingUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#removeUserWithCount(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	void removeUserWithCount(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Integer> callback);

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#replicateWorkoutsForAllUsers()
	 */
	void replicateWorkoutsForAllUsers(AsyncCallback<Integer> callback);

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#replicateWorkoutsForUser(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	void replicateWorkoutsForUser(Long userId, String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, Date startDate,
			Date endDate, AsyncCallback<Integer> callback);

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#setUserActive(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	void setUserActive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Void> callback);

	/**
	 * 
	 * @see com.trireplicator.shared.SynchronizerService#setUserInactive(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	void setUserInactive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, AsyncCallback<Void> callback);

}
