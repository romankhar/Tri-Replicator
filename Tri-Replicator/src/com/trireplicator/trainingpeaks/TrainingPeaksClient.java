/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.trainingpeaks;

import java.io.StringReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.mortbay.log.Log;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.trireplicator.client.TrainingLogException;
import com.trireplicator.client.Utils;
import com.trireplicator.client.WorkoutSession;
import com.trireplicator.client.WorkoutSession.WorkoutType;
import com.trireplicator.server.TrainingAPI;

/**
 * This class allows one to get and put data into the Trainingpeaks.com site using their external APIs
 */
public class TrainingPeaksClient implements TrainingAPI {
	private static final Logger log = Logger.getLogger(TrainingPeaksClient.class.getName());

	private static String TRAININGPEAKS_HOST = "https://www.trainingpeaks.com";
	private static String TRAININGPEAKS_SERVICE_PATH = "tpwebservices/service.asmx";
	private static String TRAININGPEAKS_GET_WORKOUTS = "GetWorkoutsForAthlete";

	private WebResource service;
	private String servicePath;
	private String username;
	private String password;

	@Override
	public List<WorkoutSession> getWorkoutsBetweenDates(Date startDate, Date endDate) throws TrainingLogException {
		String functionPath = TRAININGPEAKS_GET_WORKOUTS;
		List<WorkoutSession> listOfWorkouts = new ArrayList<WorkoutSession>();
		log.info("--- getWorkoutsBetweenDates() for user '" + getUsername() + "' password ='"+getPassword()+"', startDate="+startDate.toString()+" endDate="+endDate.toString());

		if (startDate.after(endDate)) {
			Log.info("Start date can not be older than the end date.");
			throw new TrainingLogException("Start date can not be older than the end date.");
		}

		initializeJerseyClient();

		// GET
		// /tpwebservices/service.asmx/GetWorkoutsForAthlete?username=string&password=string&startDate=string&endDate=string

		// date must be in this format: "10/28/2012 9:01:26 PM"
		// refer to
		// http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
		String startDateString = new SimpleDateFormat("MM/dd/yyyy").format(startDate) + " 00:00:00 AM";
		String endDateString = new SimpleDateFormat("MM/dd/yyyy").format(endDate) + " 23:59:59 PM";

		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("username", getUsername());
		params.add("password", getPassword());
		params.add("startDate", startDateString);
		params.add("endDate", endDateString);

		String serverResponse = "";
		// Get XML
		try {
			serverResponse = service.path(servicePath).path(functionPath).queryParams(params).accept(MediaType.TEXT_XML).get(String.class);
			log.info(serverResponse);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Could not get the list of workouts from the server. Please check username and password. Error="+e.toString();
			log.info(msg);
			throw new TrainingLogException(msg, e);
		}

		// Now need to read the resulting XML output into the formatted list of
		// workouts
		try {
			listOfWorkouts = readWorkoutsFromXML(serverResponse);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new TrainingLogException(
					"JAXB XML error - Could not parse the list of workouts from the server. Please check if you are using correct VERSION of the Trainingpeaks api.",
					e);
		}

		return listOfWorkouts;
	}

	/**
	 * This method parses XML generated by Trainingpeaks.com into the list of workouts using JAXB
	 * 
	 * @param serverResponse
	 * @return
	 * @throws JAXBException
	 */
	private List<WorkoutSession> readWorkoutsFromXML(String inputString) throws JAXBException {
		// This is the return variable for the method
		List<WorkoutSession> listOfSessions = new ArrayList<WorkoutSession>();
		// This is a single workout session in internal simplified format
		// without any extra stuff from Trainingpeaks
		WorkoutSession session;
		// This is a list of all workouts in the complete Trainingpeaks format
		// List<Workout> workoutList = new ArrayList<Workout>();
		// This is JAXB structure that we will be reading
		ArrayOfWorkout arrayOfWorkout = new ArrayOfWorkout();

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(ArrayOfWorkout.class);
		// Marshaller m = context.createMarshaller();
		Unmarshaller um = context.createUnmarshaller();
		arrayOfWorkout = (ArrayOfWorkout) um.unmarshal(new StringReader(inputString));
		List<Workout> list = (ArrayList<Workout>) arrayOfWorkout.getWorkout();
		for (Workout workout : list) {
			// System.out.println("****************** Workout: " +
			// workout.getTitle() + " TYPE = " +
			// workout.getWorkoutTypeDescription()+" DISTANCE = "+workout.getDistanceInMeters()+" DAY="+workout.getWorkoutDay());
			session = new WorkoutSession();
			session.setWorkoutDate(workout.getWorkoutDay().toGregorianCalendar().getTime());
			session.setWorkoutDistanceYards(Utils.approximateMeters2Yards(workout.getDistanceInMeters()));
			session.setWorkoutName(workout.getTitle());
			session.setWorkoutType(convertWorkoutCode(workout.getWorkoutTypeDescription()));
			listOfSessions.add(session);
		}
		return listOfSessions;
	}

	private WorkoutType convertWorkoutCode(String workoutCode) {

		if (workoutCode.contains("Swim"))
			return WorkoutType.Swim;

		if (workoutCode.contains("Bike"))
			return WorkoutType.Bike;

		if (workoutCode.contains("Run"))
			return WorkoutType.Run;

		return WorkoutType.Other;
	}

	public List<WorkoutSession> generateFakes() {
		List<WorkoutSession> listOfWorkouts = new ArrayList<WorkoutSession>();

		listOfWorkouts.add(new WorkoutSession(WorkoutType.Bike, "new bike name", new Date(), 2000));
		return listOfWorkouts;
	}

	public String getTrainingPeaksApiVersion() {
		String version = null;
		initializeJerseyClient();

		String functionPath = "Version";

		// Get XML
		version = service.path(servicePath).path(functionPath).accept(MediaType.TEXT_XML).get(String.class);

		// The above returns:
		// <?xml VERSION="1.0" encoding="utf-8"?>
		// <string
		// xmlns="http://www.trainingpeaks.com/TPWebServices/">1.1.0</string>
		return version;
	}

	private void initializeJerseyClient() {
		// Initialize Jersey REST client session to be used to call
		// TrainingPeaks
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		service = client.resource(getBaseURI());
		// http://www.trainingpeaks.com/tpwebservices/service.asmx?op=Version
		servicePath = TRAININGPEAKS_SERVICE_PATH;
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(TRAININGPEAKS_HOST).build();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setupTrainingLog(String username, String password) throws TrainingLogException {
		setPassword(password);
		setUsername(username);
	}

	@Override
	public List<WorkoutSession> addWorkouts(List<WorkoutSession> listOfSessions) throws TrainingLogException {
		// TODO - not implemented for now
		return null;
	}

	@Override
	public boolean checkLogin() {
		// Lets assume that dates are all today
		String startDateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date()) + " 00:00:00 AM";
		String endDateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date()) + " 23:59:59 PM";

		initializeJerseyClient();
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("username", getUsername());
		params.add("password", getPassword());
		params.add("startDate", startDateString);
		params.add("endDate", endDateString);

		try {
			String serverResponse = service.path(servicePath).path(TRAININGPEAKS_GET_WORKOUTS).queryParams(params)
					.accept(MediaType.TEXT_XML).get(String.class);
			if (serverResponse == null) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.info("Could not get the list of workouts from the server. Likely Login error.", e);
			return false;
		}
		return true;
	}

}