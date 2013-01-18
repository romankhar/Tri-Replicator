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

import java.util.List;

import com.trireplicator.client.WorkoutSession;


public class Debug {

	public static String workoutsToString(List<WorkoutSession> workouts) {
		
		if ((workouts == null) || (workouts.size() == 0))
			return "-empty list of workouts-";
		
		String result = "";
		for (int i = 0; i < workouts.size(); i++) {
			result = result + "--- Workout [" + i + "]='" + workouts.get(i).toString() + "'";
		}
		return result;
	}
}
