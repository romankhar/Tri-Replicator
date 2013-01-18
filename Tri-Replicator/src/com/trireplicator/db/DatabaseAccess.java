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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.trireplicator.client.MoreThanOneUserFound;
import com.trireplicator.client.UserNotFound;


public class DatabaseAccess {

	private static final Logger log = Logger.getLogger(DatabaseAccess.class.getName());

	public List<User> listUsers() {
		EntityManager em = EMFService.get().createEntityManager();
		// Read the existing entries
		Query q = em.createQuery("select u from " + User.tableName + " u");
		@SuppressWarnings("unchecked")
		List<User> users = q.getResultList();
		em.close();
		return users;
	}

	public void removeUser(long id) {
		log.info("Remove user");
		EntityManager em = EMFService.get().createEntityManager();
		try {
			User user = em.find(User.class, id);
			em.remove(user);
		} finally {
			em.close();
		}
	}

	/**
	 * This will actually remove all users with given names, even if passwords are different, but it will check for password match for at least one of those users Return value is how many users
	 * deleted
	 */
	public int removeUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT) {
		log.info("--- removeUser()");
		EntityManager em = EMFService.get().createEntityManager();
		User user = new User();
		Query q = em
				.createQuery("delete from "
						+ User.tableName
						+ " u where (u.nameUSAT = :nameUSAT) and (u.nameTP = :nameTP) and (u.encryptedPasswordUSAT = :passwordUSAT) and (u.encryptedPasswordTP = :passwordTP)");
		q.setParameter("nameUSAT", nameUSAT);
		q.setParameter("nameTP", nameTP);
		q.setParameter("passwordUSAT", user.plain2encrypted(passwordUSAT));
		q.setParameter("passwordTP", user.plain2encrypted(passwordTP));

		int i = q.executeUpdate();
		log.info("Deleted '" + i + "' users from the database");
		em.close();
		return i;
	}

	public boolean addUser(User user) {
		synchronized (this) {
			log.info("Add user");
			// First we need to check if the user already exists
			if (!checkExistingUser(user.getNameUSAT(), user.getPlainPasswordUSAT(), user.getNameTP(), user.getPlainPasswordTP())) {
				// Now we can add new user
				EntityManager em = EMFService.get().createEntityManager();
				em.persist(user);
				em.close();
				return true;
			} else {
				// The user with given user names and passwords is already in the system, so we do nothing
				log.info("The user with given attributes is already registered in the system");
			}
		}
		return false;
	}

	public List<User> getUsersByUSATName(String name) {
		log.info("getUsersByUSATName");
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select u from " + User.tableName + " u where u.nameUSAT = :name");
		q.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<User> users = q.getResultList();
		em.close();
		return users;
	}

	public boolean checkExistingUser(String nameUSAT, String passwordUSAT, String nameTP, String passwordTP) {

		Long userId = null;
		try {
			// We do not really need to know the user ID, but need to knof if the user was found
			userId = findUser(nameTP, passwordTP, nameUSAT, passwordUSAT, false);
		} catch (UserNotFound e1) {
			return false;
		} catch (MoreThanOneUserFound e) {
			// This should never happen
			log.log(Level.WARNING, "More than 1 user found in the database: nameUSAT='" + nameUSAT + "' nameTP='" + nameTP + "'");
			return true;
		}

		// Now we know that the login is successful
		if (userId != null) {
			return true;
		} else {
			return false;
		}
	}

	public Long findUser(String nameTP, String passwordTP, String nameUSAT, String passwordUSAT, boolean ignoreMultiple)
			throws UserNotFound, MoreThanOneUserFound {
		log.info("--- findUser()");
		EntityManager em = EMFService.get().createEntityManager();
		User user = new User();

		Query q = em
				.createQuery("select u from "
						+ User.tableName
						+ " u where (u.nameUSAT = :nameUSAT) and (u.nameTP = :nameTP) and (u.encryptedPasswordUSAT = :passwordUSAT) and (u.encryptedPasswordTP = :passwordTP)");
		q.setParameter("nameUSAT", nameUSAT);
		q.setParameter("nameTP", nameTP);
		q.setParameter("passwordUSAT", user.plain2encrypted(passwordUSAT));
		q.setParameter("passwordTP", user.plain2encrypted(passwordTP));
		@SuppressWarnings("unchecked")
		List<User> users = q.getResultList();
		em.close();

		// Check if we did not find any users
		if ((users == null) || (users.size() == 0)) {
			String error = "User with given names is not found in the system: nameUSAT='" + nameUSAT + "' nameTP='" + nameTP + "'";
			log.info(error);
			throw new UserNotFound(error);
		}

		// Check if we found multiple users
		if ((!ignoreMultiple) && (users.size() > 1)) {
			String error = "More than 1 user with given names is found in the system: nameUSAT='" + nameUSAT + "' nameTP='" + nameTP + "'";
			log.log(Level.WARNING, error);
			throw new MoreThanOneUserFound(error);
		}

		// At this point we only have 1 user found
		return users.get(0).getUserId();
	}

	public List<Workout> findWorkoutsForUser(Long id) {
		log.info("getWorkoutsForUser: userId=" + id.toString());
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select w from " + Workout.tableName + " w where w.userId = :userId");
		q.setParameter("userId", id);
		@SuppressWarnings("unchecked")
		List<Workout> workouts = q.getResultList();
		em.close();
		return workouts;
	}

	public void addWorkout(Workout workout) {
		synchronized (this) {
			log.info("Add workout");
			EntityManager em = EMFService.get().createEntityManager();
			em.persist(workout);
			em.close();
		}

	}

	/**
	 * Finds workout in the database
	 * 
	 * @param workout
	 * @return WorkoutId if the workout is found and ZERO (0) if it is not found
	 */
	public Long findWorkout(Workout workout) {
		log.info("--- findWorkout()");
		EntityManager em = EMFService.get().createEntityManager();

		Query q = em.createQuery("select w from " + Workout.tableName + " w where (w.userId = :userId) and "
				+ "(w.workout.workoutType = :workoutType) and "
				+ "(w.workout.workoutName = :workoutName) and (w.workout.workoutDate = :workoutDate) and "
				+ "(w.workout.workoutDistanceYards = :workoutDistanceYards)");
		q.setParameter("userId", workout.getUserId());
		q.setParameter("workoutType", workout.getWorkout().getWorkoutType());
		q.setParameter("workoutName", workout.getWorkout().getWorkoutName());
		q.setParameter("workoutDate", workout.getWorkout().getWorkoutDate());
		q.setParameter("workoutDistanceYards", workout.getWorkout().getWorkoutDistanceYards());
		@SuppressWarnings("unchecked")
		List<Workout> workouts = q.getResultList();
		em.close();

		// Check if we did not find any users
		if ((workouts == null) || (workouts.size() == 0)) {
			String error = "Workout with given parameters is not found in the system";
			log.info(error);
			return new Long(0);
		}

		return workouts.get(0).getId();
	}

	public void addAdminEvent(AdminEvents event) {
		log.info("Add admin event");
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(event);
		em.close();
	}

	public int deleteAllWorkouts() {
		log.info("deleteAllWorkouts !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("delete from " + Workout.tableName + " w");
		int i = q.executeUpdate();
		em.close();
		log.info("Deleted '" + i + "' rows from WORKOUTS");
		return i;
	}

	public int deleteAllUsers() {
		log.info("deleteAllUsers !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("delete from " + User.tableName + " u");
		int i = q.executeUpdate();
		em.close();
		log.info("Deleted '" + i + "' rows from USERS");
		return i;
	}

	public int deleteAllAdminEvents() {
		log.info("delete all admin events !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("delete from " + AdminEvents.tableName + " a");
		int i = q.executeUpdate();
		em.close();
		log.info("Deleted '" + i + "' rows from AdminEvents");
		return i;
	}
}
