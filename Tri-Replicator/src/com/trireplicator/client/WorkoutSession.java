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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

 
@Embeddable
public class WorkoutSession implements Serializable {

	private static final long serialVersionUID = 6167913802767169351L;

	public enum WorkoutType { 
		Swim, Bike, Run, Other
	};  

	private WorkoutType workoutType;
	private String workoutName;
	private Date workoutDate;
	private long workoutDistanceYards;

	public WorkoutSession() {
		super();
	}

	public WorkoutSession(WorkoutType type, String name, Date date, long distanceYards) {
		setWorkoutDate(date);
		setWorkoutDistanceYards(distanceYards);
		setWorkoutName(name);
		setWorkoutType(type); 
	}

	public WorkoutType getWorkoutType() {
		return workoutType;
	}

	public void setWorkoutType(WorkoutType workoutType) {
		this.workoutType = workoutType;
	}

	public String getWorkoutName() {
		return workoutName;
	}

	public void setWorkoutName(String workoutName) {
		this.workoutName = workoutName;
	}

	public Date getWorkoutDate() {
		return workoutDate;
	}

	public void setWorkoutDate(Date workoutDate) {
		this.workoutDate = workoutDate;
	}

	public long getWorkoutDistanceYards() {
		return workoutDistanceYards;
	}

	public void setWorkoutDistanceYards(long workoutDistanceYards) {
		this.workoutDistanceYards = workoutDistanceYards;
	}

	public String toString() {
		return "WorkoutSession: name='" + getWorkoutName() + "', type='" + getWorkoutType() + "', date='" + getWorkoutDate()
				+ "', distance (yards) ='" + getWorkoutDistanceYards() + "'";
	}

	public double getWorkoutDistanceMiles() {
		return Utils.yards2miles((double) getWorkoutDistanceYards());
	}

	/**
	 * This method validates if the workout is reasonably valid and returns TRUE if it is It checks for distances, dates, etc.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean isValidWorkout() {
		// It must be this year
		if (getWorkoutDate().getYear() != new Date().getYear())
			return false;

		// It can not be dated future date
		if (getWorkoutDate().after(new Date()))
			return false;

		// Check for other conditions
		switch (getWorkoutType()) {
		case Swim:
			if (getWorkoutDistanceYards() > 50000)
				return false;
			if (getWorkoutDistanceYards() < 50)
				return false;
			break;

		case Bike:
			if (getWorkoutDistanceMiles() > 500.00)
				return false;
			if (getWorkoutDistanceMiles() < 0.0)
				return false;
			break;

		case Run:
			if (getWorkoutDistanceMiles() > 500.00)
				return false;
			if (getWorkoutDistanceMiles() < 0.0)
				return false;
			break;

		default:
			break;
		}

		// Seems like after all checks and balances the workout is valid
		return true;
	}

}
