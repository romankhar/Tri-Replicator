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

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trireplicator.client.SynchronizerService;
import com.trireplicator.client.TrainingLogException;


/**
 * This servlet needs to be called periodically by the cron job of Google App Engine It will run replication between Trainingpeaks.com and USAT site for all registered users
 * 
 * @author Roman Kharkovski
 * 
 */
@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(CronServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("--- CronServlet.doGet() - will start replication of all workouts for all users");

		String action = null;
		String defaultAction = "replicateAllWorkouts";
		if (req != null) {
			action = (String) req.getParameter("action");
		}
		if (action == null)
			action = defaultAction;
		log.info("ACTION=" + action);

		if (action.equals(defaultAction)) {
			replicateAllWorkouts(resp);
		} else {
			log.info("CronServlet - There was nothing for this servlet to do?????????????");
		}
	}

	private void replicateAllWorkouts(HttpServletResponse resp) {
		SynchronizerService server = new SynchronizerServiceImpl();

		try {
			int i = server.replicateWorkoutsForAllUsers();
			String info = "Finished replicating workouts for "+i+" users."; 
			resp.getWriter().println(info);
			log.info(info);
		} catch (TrainingLogException e) {
			log.info("Error replicating workouts for all users: "+e.toString());
		} catch (IOException e) {
			log.info("Error writing to the output");
			}
	}
}