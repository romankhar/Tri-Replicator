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
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.trireplicator.db.AdminEvents;
import com.trireplicator.db.DatabaseAccess;
import com.trireplicator.util.SecurityTools;

/**
 * This class is used to initialize the application when the JVM is first started by GAE
 * @author Roman Kharkovski
 */
public class ServletListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(ServletListener.class.getName());
	// Last day to enter results would be the March 15, 2013 (months start with 0 for January)
	private static GregorianCalendar lastDayOfChallengeUpdates = new GregorianCalendar(2013, 02, 15);

	public void contextInitialized(ServletContextEvent event) {
		// This will be invoked as part of a warmup request, or the first user
		// request if no warmup request was invoked.
		log.info("--- ServletListener.contextInitialized() - started");

		// Log the fact that the server is being started
		DatabaseAccess database = new DatabaseAccess();
//		database.addAdminEvent(new AdminEvents("Server start", "Starting server..."));

		// Initialize encryption library and provider
		try {
			SecurityTools.setup();
		} catch (Exception e) { 
			e.printStackTrace();
			log.info("Error while initializing security: "+e.toString());
			database.addAdminEvent(new AdminEvents("Server start", "Error during security init: "+e.toString()));
			throw new RuntimeException("Error while configuring application security", e);
		}


		// First we need to check if the deadline for replication has passed as the USAT NCC challenge only runs till end of winter 2013
		if (pastDeadline()) {
			throw new RuntimeException("It is now past the deadline for the USAT NCC challenge. Exiting application.");
		}

		database.addAdminEvent(new AdminEvents("Server start", "Server started OK."));
		log.info("--- ServletListener.contextInitialized() - completed OK");
	}

	public void contextDestroyed(ServletContextEvent event) {
		// App Engine does not currently invoke this method.
	}

	/**
	 * This check if we are past end of the USAT challenge
	 * @return True if we are past the deadline, False if we are good to go
	 */
	public boolean pastDeadline() {
		Date today = new Date();
		log.info("Today's date = "+today.toString());
		log.info("Last day to replicate workouts = "+lastDayOfChallengeUpdates.getTime().toString());
		if (today.after(lastDayOfChallengeUpdates.getTime())) {
			// We are not yet past the deadline for updates to USAT site
			log.log(Level.WARNING, "We are now past the deadline, should not proceed with normal business.");
			return true;
		}
		// TODO - need to cleanup user accounts from the database

		// We are now past the last day of allowed updates to the USAT site - can not do any replication after this date
		log.log(Level.WARNING, "We are not past the deadline, so can proceed as normal");
		return false;
	}

}
