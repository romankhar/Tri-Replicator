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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * This is the main interface of the application - all external calls must be made to this API
 */
@RemoteServiceRelativePath("synchronizer")
public interface SynchronizerService extends RemoteService {

	/**
	 * Will run replication for all users in the system It will read list of users from the local database and for each user try to do the replication from TP into USAT site In the process it will
	 * lookup already replicated workouts and skip them (uses replicateWorkoutsForUser() for that purpose)
	 * 
	 * The starting date for replication is the date when the user has registered in the system.
	 * 
	 * @return number of successful users who replicated their workouts
	 */
	public int replicateWorkoutsForAllUsers() throws TrainingLogException;

	/**
	 * Adds new user to the internal database
	 * 
	 * @param nameTP
	 * @param passwordTP
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @return True if the user has been added (it checks for the ability to login into remote sites), False - if user has not been added (or can not login into remote site)
	 * @throws TrainingLogException
	 */
	public boolean addUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException;

	/**
	 * This method imports all workouts from the Trainingpeaks.com into USAT site for the specified date range Returns list of workouts that were added to USAT or exception if an error happened.
	 * 
	 * @param userId
	 *            - optionally could be zero, in which case name, etc. are used to find the user. If ID != 0, then we will find it by reading the database
	 * @param nameTP
	 * @param passwordTP
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @param startDate
	 * @param endDate
	 * @return List of workouts
	 * @throws TrainingLogException
	 */
	public int replicateWorkoutsForUser(Long userId, String nameTP, String passwordTP, String nameUSAT,
			String passwordUSAT, Date startDate, Date endDate) throws TrainingLogException;

	public void setUserActive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException;

	public void setUserInactive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException;

	/**
	 * Removes all users with given names from the database
	 * 
	 * @param nameTP
	 * @param passwordTP
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @return How many users were removed
	 * @throws TrainingLogException
	 */
	public int removeUserWithCount(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException;

	/**
	 * Checks if the given user is registered in the system - this does not test if he can login into TP or USAT site.
	 * 
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @param nameTP
	 * @param passwordTP
	 * @return true - user already exists, false - user does not exist or password is incorrect
	 */
	public boolean checkExistingUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT);


}
