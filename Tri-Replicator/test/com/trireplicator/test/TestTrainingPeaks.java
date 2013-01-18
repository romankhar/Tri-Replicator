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

import org.junit.Assert;
import org.junit.Test;

import com.trireplicator.server.Debug;
import com.trireplicator.shared.TrainingLogException;
import com.trireplicator.shared.WorkoutSession;
import com.trireplicator.trainingpeaks.TrainingPeaksClient;


public class TestTrainingPeaks {
	private static String username = com.trireplicator.secrets.Constants.USER_TRAININGPEAKS;
	private static String password = com.trireplicator.secrets.Constants.PASSWORD_TRAININGPEAKS;

	@Test
	public void testFakeWorkouts() {
		TrainingPeaksClient tpClient = new TrainingPeaksClient();

		List<WorkoutSession> workouts = tpClient.generateFakes();
		Assert.assertEquals("new bike name", workouts.get(0).getWorkoutName());
		Debug.workoutsToString(workouts);

	}

	@Test
	public void testTrainingpeaksVersion() {
		TrainingPeaksClient tpClient = new TrainingPeaksClient();
		String version = tpClient.getTrainingPeaksApiVersion();
		Assert.assertTrue(version.contains("http://www.trainingpeaks.com/TPWebServices"));
		Assert.assertTrue(version.contains("1.1.0"));
		System.out.println(version);
	}

	@Test
	public void testGetWorkoutsForDates() {
		TrainingPeaksClient tpClient = new TrainingPeaksClient();
		try {
			tpClient.setupTrainingLog(username, password);
		} catch (TrainingLogException e) {
			Assert.fail("Can not setup training peaks client");
			e.printStackTrace();
		}

		// Today's date minus 4 days
		Date startDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 10);
		// Today's date
		Date endDate = new Date();

		List<WorkoutSession> listOfWorkouts = null;
		try {
			listOfWorkouts = tpClient.getWorkoutsBetweenDates(startDate, endDate);
		} catch (TrainingLogException e) {
			Assert.fail("Could not get the list of workoutsa from server");
			e.printStackTrace();
		}
		Assert.assertTrue(listOfWorkouts.size() > 0);
		Debug.workoutsToString(listOfWorkouts);
	}
}