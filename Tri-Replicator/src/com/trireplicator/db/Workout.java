/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.db;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.trireplicator.client.WorkoutSession;


/**
 * Workout is the holder for the purpose of storing the WorkoutSession + userId + unique ID in the database
 * @author Roman
 *
 */  
@Entity
public class Workout {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Embedded
	@OneToOne(cascade = CascadeType.ALL)
	private WorkoutSession workout;

	// TODO - this really needs to have the relationship with the Users database, but for now it did not work properly
	// @ManyToOne
	// private Users user;
	private Long userId;
	@Transient
	public static final String tableName = "Workout";

	public Long getId() {
		return id;
	}

	public Workout() {
		super();
	}

	public Workout(WorkoutSession workout, Long userId) {
		super();
		this.workout = workout;
		this.userId = userId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WorkoutSession getWorkout() {
		return workout;
	}

	public void setWorkout(WorkoutSession workout) {
		this.workout = workout;
	}

	public String toString() {
		String sessionAsString = "-empty workout-";
		if (getWorkout() != null)
			sessionAsString = getWorkout().toString();
		return "------ WORKOUT: ID='" + getId() + "' WorkoutSession='" + sessionAsString + "'";
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
