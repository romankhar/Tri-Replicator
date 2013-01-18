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

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import com.trireplicator.server.Debug;
import com.trireplicator.server.SynchronizerServiceImpl;
import com.trireplicator.shared.Utils;
import com.trireplicator.shared.WorkoutSession;


public class TestSynchronizer {
	private static final Logger log = Logger.getLogger(TestSynchronizer.class.getName());

	private static String userUSAT = com.trireplicator.secrets.Constants.USER_USAT;
	private static String pwdUSAT = com.trireplicator.secrets.Constants.PASSWORD_USAT;
	private static String userTP = com.trireplicator.secrets.Constants.USER_TRAININGPEAKS;
	private static String pwdTP = com.trireplicator.secrets.Constants.PASSWORD_TRAININGPEAKS;

	// @Test
	public void testSynchWorkout() throws Exception {

		log.info("============================== START testAddWorkout =======================");
		Date startDate = new Date(Utils.twoDaysAgo());
		Date endDate = new Date();
		List<WorkoutSession> workoutsOut = null;

		SynchronizerServiceImpl server = new SynchronizerServiceImpl();
		try {
			workoutsOut = server.replicateWorkoutsForUserWithList(new Long(0), userTP, pwdTP, userUSAT, pwdUSAT, startDate, endDate);
		} catch (Exception e) {
			log.info("Error when calling synch server");
			Assert.fail("Failed to add a workout");
		}
		Assert.assertTrue(workoutsOut != null);
		log.info("The following workouts were added to the USAT site: ");
		Debug.workoutsToString(workoutsOut);
	}

	@Test
	public void testSynchWorkoutForToday() throws Exception {

		log.info("============================== START testSynchWorkout-for today =======================");
		Date startDate = new Date();
		Date endDate = new Date();
		List<WorkoutSession> workoutsOut = null;

		SynchronizerServiceImpl server = new SynchronizerServiceImpl();
		try {
			workoutsOut = server.replicateWorkoutsForUserWithList(new Long(0), userTP, pwdTP, userUSAT, pwdUSAT, startDate, endDate);
		} catch (Exception e) {
			log.info("Error when calling synch server");
			Assert.fail("Failed to add a workout");
		}
		Assert.assertTrue(workoutsOut != null);
		log.info("The following workouts were added to the USAT site: ");
		Debug.workoutsToString(workoutsOut);
	}
}
