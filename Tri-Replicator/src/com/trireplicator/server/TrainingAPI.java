/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.server;

import java.util.Date;
import java.util.List;

import com.trireplicator.shared.TrainingLogException;
import com.trireplicator.shared.WorkoutSession;

/**
 * This is connector to 3rd party systems, such as Trainingpeaks.com, etc.
 * If someone was to add a new connector, say to Garmin site, this interface needs to be implemented
 * 
 * @author Roman Kharkovski, http://kharkovski.blogspot.com
 */
public interface TrainingAPI {
	/**
	 * Initializes the Log facility to be used. This must be called once before any other method 
	 * 
	 * @param username
	 * @param password
	 * @throws TrainingLogException
	 */
	public void setupTrainingLog(String username, String password) throws TrainingLogException;
	
	/**
	 * Test if given credentials allow for successful login into remote system - no other action is taken, except for login test 
	 * 
	 * @param username
	 * @param password
	 * @return True if success, False if failure
	 * @throws TrainingLogException
	 */
	public boolean checkLogin();
	
	/**
	 * Returns a list of all workouts found between the specified dates 
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws TrainingLogException
	 */
	public List<WorkoutSession> getWorkoutsBetweenDates(Date startDate, Date endDate) throws TrainingLogException;
	
	/**
	 * Adds workouts to the log and returns the workouts that have been added to the log.
	 * If workout was not added to the log, it is not returned in the resulting list.
	 * 
	 * @param listOfSessions
	 * @return
	 * @throws TrainingLogException
	 */
	public List<WorkoutSession> addWorkouts(List<WorkoutSession> listOfSessions) throws TrainingLogException;
}
