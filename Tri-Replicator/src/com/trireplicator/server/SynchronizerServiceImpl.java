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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.trireplicator.client.MoreThanOneUserFound;
import com.trireplicator.client.SynchronizerService;
import com.trireplicator.client.TrainingLogException;
import com.trireplicator.client.UserNotFound;
import com.trireplicator.client.WorkoutSession;
import com.trireplicator.client.WorkoutSession.WorkoutType;
import com.trireplicator.db.AdminEvents;
import com.trireplicator.db.DatabaseAccess;
import com.trireplicator.db.User;
import com.trireplicator.db.Workout;
import com.trireplicator.trainingpeaks.TrainingPeaksClient;
import com.trireplicator.usat.USATclient;

public class SynchronizerServiceImpl extends RemoteServiceServlet implements SynchronizerService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -154079900192955118L;
	private static final Logger log = Logger.getLogger(SynchronizerServiceImpl.class.getName());

	@Override
	public int replicateWorkoutsForUser(Long userId, String nameTP, String passwordTP, String nameUSAT, String passwordUSAT,
			Date startDate, Date endDate) throws TrainingLogException {

		List<WorkoutSession> workoutsAdded = replicateWorkoutsForUserWithList(userId, nameTP, passwordTP, nameUSAT, passwordUSAT,
				startDate, endDate);
		if (workoutsAdded == null) {
			return 0;
		} else {
			return workoutsAdded.size();
		}
	}

	public List<WorkoutSession> replicateWorkoutsForUserWithList(Long userId, String nameTP, String passwordTP, String nameUSAT,
			String passwordUSAT, Date startDate, Date endDate) throws TrainingLogException {
		List<WorkoutSession> workoutsAdded = null;
		new DatabaseAccess().addAdminEvent(new AdminEvents("replicateWorkoutsForUser", "nameTP='"+nameTP+"' nameUSAT='"+nameUSAT+"' startDate="+startDate.toString()+"' endDate='"+endDate.toString()+"'"));

		log.info("----------------------- First we need to get workouts from Trainingpeaks");
		List<WorkoutSession> workoutsFromTP = null;

		TrainingPeaksClient tpClient = new TrainingPeaksClient();
		try {
			tpClient.setupTrainingLog(nameTP, passwordTP);
			workoutsFromTP = tpClient.getWorkoutsBetweenDates(startDate, endDate);
			log.info("Workouts obtained from TP server: " + Debug.workoutsToString(workoutsFromTP));
		} catch (TrainingLogException e) {
			e.printStackTrace();
			String error = "Could not get the list of workouts from a Trainingpeaks.com server for user: " + nameTP;
			log.info(error);
			return null;
		}

		log.info("----------------------- Remove workouts that have already been replicated by looking at replication history");
		List<WorkoutSession> filteredWorkoutsFromTP = filterOutAlreadyReplicatedWorkouts(userId, nameTP, passwordTP, nameUSAT,
				passwordUSAT, workoutsFromTP);

		log.info("----------------------- Now add those workouts to the USAT site");
		USATclient usatClient = new USATclient();
		try {
			usatClient.setupTrainingLog(nameUSAT, passwordUSAT);
			workoutsAdded = usatClient.addWorkouts(filteredWorkoutsFromTP);
		} catch (Exception e) {
			e.printStackTrace();
			String error = "Could not add workouts to USAT server";
			log.info(error);
			return null;
		}
		log.info("The following workouts were added to the USAT site: ");
		log.info(Debug.workoutsToString(workoutsAdded));

		log.info("----------------------- Add workouts to the database for future reference");
		saveWorkouts(userId, nameTP, passwordTP, nameUSAT, passwordUSAT, workoutsAdded);
		return workoutsAdded;
	}

	@Override
	public int replicateWorkoutsForAllUsers() throws TrainingLogException {
		log.info("--- replicateWorkoutsForAllUsers()");
		int count = 0;
		long startTime = System.currentTimeMillis();

		DatabaseAccess database = new DatabaseAccess();
		Iterator<User> userIterator = database.listUsers().iterator();
		while (userIterator.hasNext()) {
			User user = (User) userIterator.next();
			// This will cause replication from the date of the registration until today
			// Already replicated workouts will be filtered out in the process, so no worries
//			log.info("---------- Version:"+Utils.VERSION+": replicateWorkoutsFor all users - found user: id="+user.getUserId()+" nameTP="+ user.getNameTP()+" passwordTP='"+user.getPlainPasswordTP()+"' nameUSAT='"+
//					user.getNameUSAT()+"' passwordUSAT='"+user.getPlainPasswordUSAT());
			List<WorkoutSession> workouts = replicateWorkoutsForUserWithList(user.getUserId(), user.getNameTP(), user.getPlainPasswordTP(),
					user.getNameUSAT(), user.getPlainPasswordUSAT(), user.getRegistrationDate(), new Date());
			if ((workouts != null) && (workouts.size() > 0)) {
				log.info("Replication for user '" + user.getNameTP() + "' completed successfully with " + workouts.size()
						+ " workouts replicated");
				count++;
			} else {
				log.info("Replication for user '" + user.getNameTP()
						+ "' DID NOT complete successfully. Perhaps there were no new workouts to be added");
			}
		}

		// Since all is done, return the number of users for whom the workouts were replicated
		long replicationTimeSecs = new Double((System.currentTimeMillis() - startTime) / 1000).longValue();
		long replicationTimeMins = new Double(replicationTimeSecs / 60).longValue();
		log.info("Finished replicating all user workouts for " + count + " users. It took " + replicationTimeSecs + " seconds ("
				+ replicationTimeMins + " min) to complete");
		new DatabaseAccess().addAdminEvent(new AdminEvents("Replication completed", "Replication of workouts completed successfully for " + count + " users. It took " + replicationTimeSecs + " seconds ("
				+ replicationTimeMins + " min) to complete."));
		return count;
	}

	/**
	 * This method looks into the local database of workouts that have been already replicated in the past and removes them from the output It also removes workouts that wont need to be copied into
	 * USAT site (such as all non-bike, non-swim, non-run
	 * 
	 * @param userId
	 *            - if it is != 0, then we will use it to lookup DB, otherwise will use names and passwords below and incur additional database lookup access
	 * @param nameTP
	 * @param passwordTP
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @param workouts
	 * @return
	 */
	private List<WorkoutSession> filterOutAlreadyReplicatedWorkouts(Long userId, String nameTP, String passwordTP, String nameUSAT,
			String passwordUSAT, List<WorkoutSession> workouts) {
		log.info("---filterOutAlreadyReplicatedWorkouts()");
		List<WorkoutSession> filteredWorkouts = new ArrayList<WorkoutSession>();
		if ((workouts == null) || (workouts.size() == 0)) {
			// There is nothing to do if there are no workouts
			return filteredWorkouts;
		}

		// First we need to remove workouts that are not swim, bike or run
		List<WorkoutSession> properWorkouts = new ArrayList<WorkoutSession>();
		Iterator<WorkoutSession> iterator = workouts.iterator();
		while (iterator.hasNext()) {
			WorkoutSession session = (WorkoutSession) iterator.next();
			if ((session.getWorkoutType() != WorkoutType.Swim) && (session.getWorkoutType() != WorkoutType.Bike)
					&& (session.getWorkoutType() != WorkoutType.Run)) {
				// Do nothing as this workout wont be needed for replication into the USAT site anyway
				log.info("This kind of workout does not need to be replicated into USAT: '" + session.getWorkoutType()+"'");
			} else {
				properWorkouts.add(session);
			}
		}
		// If there is nothing to check against the database, lets just return empty list
		if (properWorkouts.size() == 0) {
			log.info("No workouts of proper type found in the input list, nothing to replicate then");
			return filteredWorkouts;
		}

		// Now we can check the database for existing workouts and remove those
		DatabaseAccess database = new DatabaseAccess();

		// In case the userId was passed down to this method, we dont need to lookup the database for it
		if ((userId == null) || (userId == 0)) {
			try {
				userId = database.findUser(nameTP, passwordTP, nameUSAT, passwordUSAT, true);
				// TODO - in Java 7 I could do multi-catch statement
			} catch (UserNotFound e) {
				e.printStackTrace();
				log.log(Level.SEVERE, "Workouts were found in the TrainingPeaks, but user is not in the database. This should never happen");
				return filteredWorkouts;
			} catch (MoreThanOneUserFound e) {
				e.printStackTrace();
				log.log(Level.SEVERE, "Workouts were found in the TrainingPeaks, but user is not in the database. This should never happen");
				return filteredWorkouts; 
			}
		}

		Iterator<WorkoutSession> iterator1 = properWorkouts.iterator();
		while (iterator1.hasNext()) {
			WorkoutSession session = (WorkoutSession) iterator1.next();
			Long workoutId = database.findWorkout(new Workout(session, userId));
			if (workoutId != 0) {
				// Do nothing as this workout is already replicated earlier
				log.info("Workout was already replicated: "+session.toString());
			} else {
				// The workout is not recorded in the local DB, so lets replicate it
				log.info("Workout was not yet replicated: "+session.toString());
				filteredWorkouts.add(session);
			}
		}

		return filteredWorkouts;
	}

	private void saveWorkouts(Long userId, String nameTP, String passwordTP, String nameUSAT, String passwordUSAT,
			List<WorkoutSession> workoutsAdded) {
		log.info("--- saveWorkouts()");
		if ((workoutsAdded == null) || (workoutsAdded.size() == 0)) {
			// There is nothing to do if there are no workouts
			log.info("no workouts need to be added");
			return;
		}
		DatabaseAccess database = new DatabaseAccess();

		// If userId was passed to this method, we do not need to read it from the database
		if ((userId == null) || (userId == 0)) {
			try {
				userId = database.findUser(nameTP, passwordTP, nameUSAT, passwordUSAT, true);
				// TODO - in Java 7 I could do multi-catch
			} catch (UserNotFound e) {
				e.printStackTrace();
				log.log(Level.SEVERE,
						"Workouts were found and replicated, but could not be saved in the database. This should never happen");
				return;
			} catch (MoreThanOneUserFound e) {
				e.printStackTrace();
				log.log(Level.SEVERE,
						"Workouts were found and replicated, but could not be saved in the database. This should never happen");
				return;
			}
		}
		Iterator<WorkoutSession> iterator = workoutsAdded.iterator();
		while (iterator.hasNext()) {
			database.addWorkout(new Workout((WorkoutSession) iterator.next(), userId));
		}
	}

	@Override
	public boolean addUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException {
		User user = new User();

		// Before we add new user, lets check if we can actually login into USAT site
		if (!checkUSATLogin(nameUSAT, passwordUSAT)) {
			log.info("Unable to login user '" + nameUSAT + "' into the USAT site and therefore can not add new user.");
			return false;
		}

		// Before we add new user, lets check if we can actually login into TP site
		if (!checkTPLogin(nameTP, passwordTP)) {
			log.info("Unable to login user '" + nameTP + "' into the Trainingpeaks site and therefore can not add new user.");
			return false;
		}

		// Finally we can add new user
		DatabaseAccess database = new DatabaseAccess();

		// By default new users are created active
		user.setActive(true);
		user.setLastVisitDate(new Date());
		user.setRegistrationDate(new Date());
		user.setNameTP(nameTP);
		user.setPlainPasswordTP(passwordTP);
		user.setNameUSAT(nameUSAT);
		user.setPlainPasswordUSAT(passwordUSAT);

		if (database.addUser(user)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkExistingUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) {

		DatabaseAccess database = new DatabaseAccess();
		database.addAdminEvent(new AdminEvents("Check Existing User", "nameTP='"+nameTP+"' nameUSAT='"+nameUSAT+"'"));
		return database.checkExistingUser(escapeHtml(nameUSAT), escapeHtml(passwordUSAT), escapeHtml(nameTP), escapeHtml(passwordTP));
	}

	/**
	 * Checks if the given user is able to login into USAT site
	 * 
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @param nameTP
	 * @param passwordTP
	 * @return true - login successful, false - unsuccessful
	 */
	public boolean checkUSATLogin(String nameUSAT, String passwordUSAT) {
		TrainingAPI client = new USATclient();
		try {
			client.setupTrainingLog(nameUSAT, passwordUSAT);
			return client.checkLogin();
		} catch (TrainingLogException e) {
			return false;
		}
	}

	/**
	 * Checks if the given user is able to login into TP site
	 * 
	 * @param nameUSAT
	 * @param passwordUSAT
	 * @param nameTP
	 * @param passwordTP
	 * @return true - login successful, false - unsuccessful
	 */
	public boolean checkTPLogin(String nameTP, String passwordTP) {
		TrainingAPI client = new TrainingPeaksClient();
		try {
			client.setupTrainingLog(nameTP, passwordTP);
			return client.checkLogin();
		} catch (TrainingLogException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int removeUserWithCount(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException {
		log.info("--- removeUser()");
		DatabaseAccess database = new DatabaseAccess();
		int i = database.removeUser(nameTP, passwordTP, nameUSAT, passwordUSAT);
		return i;
	}

	public List<Workout> viewProcessedWorkoutsForUser(String userTP, String passwordTP, String userUSAT, String passwordUSAT)
			throws TrainingLogException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserActive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserInactive(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) throws TrainingLogException {
		// TODO Auto-generated method stub

	}

	/**
	 * Escape an html string. Escaping data received from the client helps to prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

}
