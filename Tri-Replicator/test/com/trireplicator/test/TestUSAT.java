/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.trireplicator.client.WorkoutSession;
import com.trireplicator.client.WorkoutSession.WorkoutType;
import com.trireplicator.secrets.Constants;
import com.trireplicator.server.Debug;
import com.trireplicator.usat.USATclient;


public class TestUSAT {
	private static final Logger log = Logger.getLogger(TestUSAT.class.getName());

	private static String username = Constants.USER_USAT;
	private static String password = Constants.PASSWORD_USAT;

	@Test
	public void testAddWorkout() throws Exception {

		log.info("============================== START testAddWorkout =======================");
		USATclient usatClient = new USATclient();
		usatClient.setupTrainingLog(username, password);
		List<WorkoutSession> workoutsIn = new ArrayList<WorkoutSession>();
		List<WorkoutSession> workoutsOut = null;
		WorkoutSession session = null;
		session = new WorkoutSession(WorkoutType.Swim, "My swim 1", new Date(), 1000);
//		session = new WorkoutSession(WorkoutType.Swim, "My swim", new Date(System.currentTimeMillis()-1000*60*60*24*10), 1600);
		workoutsIn.add(session);
//		session = new WorkoutSession(WorkoutType.Swim, "My swim 2", new Date(), 2000);
//		workoutsIn.add(session);
//		session = new WorkoutSession(WorkoutType.Swim, "My swim 3", new Date(), 3000);
//		workoutsIn.add(session);
		session = new WorkoutSession(WorkoutType.Bike, "My ride 1", new Date(), 4000);
		workoutsIn.add(session);
//		session = new WorkoutSession(WorkoutType.Run, "My run 1", new Date(), 5000);
//		workoutsIn.add(session);
//		session = new WorkoutSession(WorkoutType.Bike, "My ride 2", new Date(), 6000);
//		workoutsIn.add(session);
//		session = new WorkoutSession(WorkoutType.Run, "My run 2", new Date(), 7000);
//		workoutsIn.add(session);
		try {
			workoutsOut = usatClient.addWorkouts(workoutsIn);
		} catch (Exception e) {
			Assert.fail("Failed to add a workout");
		}
		// TODO - really need to call getWorkout function to test if the workout was indeed added ok
		Assert.assertTrue(workoutsOut != null);
		log.info("The following workouts were added to the USAT site: ");
		Debug.workoutsToString(workoutsOut);
	}

}
