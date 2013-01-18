/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.usat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.trireplicator.server.TrainingAPI;
import com.trireplicator.shared.TrainingLogException;
import com.trireplicator.shared.WorkoutSession;
import com.trireplicator.shared.WorkoutSession.WorkoutType;

/**
 * This class allows one to get and put data into the USAT NCC website: http://www.race-tracker.net/usat/admin/add_indresults.cfm
 * The USAT site does not have an API, so we are using screen scraping technology and HTMLUnit to interact with the site - 
 * this could be broken easily when USAT changes their site layout...
 * 
 * @author Roman Kharkovski, http://kharkovski.blogspot.com
 */
public class USATclient implements TrainingAPI {
	private static final Logger log = Logger.getLogger(USATclient.class.getName());

	public String hostName = "http://www.race-tracker.net";
	public String entryURL = hostName + "/usat/admin/add_indresults.cfm";
	public String logoutUrl = hostName + "/usat/admin/logout.cfm";
	private String usatUser = "userID"; // just a dummy value
	private String usatPassword = "password"; // just a dummy value
	private WebClient webClient; 
	HtmlPage afterLoginPage = null;
	HtmlPage enterWorkoutPage = null;
	HtmlPage confirmWorkoutPage = null;
	HtmlPage reviewWorkoutsPage = null;

	@Override
	public void setupTrainingLog(String username, String password) throws TrainingLogException {
		log.info("--- setupTrainingLog()");
		setPassword(password);
		setUsername(username);
	}

	/**
	 * -------------------------------------------------------------------------------------------- This is the main method in this class - it takes list of sessions and adds them to the site
	 * iterating over several pages of the site and using HTMLUnit in the process of doing so
	 * 
	 */
	@Override
	public List<WorkoutSession> addWorkouts(List<WorkoutSession> inputSessions) throws TrainingLogException {
		log.info("--- addWorkouts()");
		// Return null if input is empty
		if ((inputSessions == null) || (inputSessions.size() == 0)) {
			log.info("Nothing to add to the list of workouts as it is empty");
			return null;
		}

		// This will be a collection of all workouts that made it to the USAT site successfully
		List<WorkoutSession> addedSessions = new ArrayList<WorkoutSession>();

		// Prepare input content for entry into the site.
		// USAT page can only accept one swim, one bike and one run workout per page
		Iterator<WorkoutSession> workoutIterator = inputSessions.iterator();

		// Start browsing the USAT site
		createBrowser();
		doLoginPage();

		while (workoutIterator.hasNext()) {
			WorkoutSession session = workoutIterator.next();
			try {
				if (session.isValidWorkout()) {
					doStartEnterResultsPage();
					if (doEnterResultsPage(session)) {
						if (doConfirmResultsPage())
							addedSessions.add(session);
					}
				} else {
					log.info("The workout was found to be invalid (some values out of range, etc.). Workout = " + session.toString());
				}
			} catch (Exception e) {
				// If there was an error with one of the workouts - skip it and move on to the next workout
				// TODO - probably need to differentiate on the kind of error and abort the whole thing in some cases?
				log.info("There was an error adding the workout");
			}
		}

		// After all workouts have been added, we can log out
		doLogoutPage();
		destroyBrowser();
		return addedSessions;
	}

	private void createBrowser() {
		log.info("--- Creating new webClient");
		// set the log level: "trace", "debug", "info", "warn", "error", or "fatal"
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "fatal");
		System.getProperties().put("org.apache.http.wire", "error");
		// Initialize the web client for HtmlUtil framework
		webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
	}

	private void destroyBrowser() {
		log.info("--- destroyBrowser()");
		webClient.closeAllWindows();
	}

	private void doLoginPage() throws TrainingLogException {
		HtmlPage loginPage = null;

		log.info("--- doLoginPage");
		try {
			loginPage = webClient.getPage(entryURL);
			// log.info(loginPage.asText());
		} catch (FailingHttpStatusCodeException e1) {
			e1.printStackTrace();
			throw new TrainingLogException("Can not open USAT login page", e1);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			throw new TrainingLogException("Wrong URL for USAT site when getting login page", e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new TrainingLogException("Network error when getting USAT login page", e1);
		}

		// Find the form on the page so we can submit it later
		List<HtmlForm> formsList = loginPage.getForms();
		HtmlForm form = formsList.get(0);
		// Find input fields in the form and assign values to those fields
		HtmlTextInput userName = form.getInputByName("username");
		userName.setValueAttribute(usatUser);
		HtmlPasswordInput password = form.getInputByName("password");
		password.setValueAttribute(usatPassword);
		HtmlRadioButtonInput loginType = form.getInputByValue("athlete");
		loginType.setChecked(true);
		HtmlSubmitInput submitLogin = form.getInputByValue("Login");

		// Now submit the form by clicking the button and get back the second page.
		// this is what needs to be sent to the server in HTTP POST request:
		// logintype=athlete&username=user111&password=passwd111
		afterLoginPage = null;
		try {
			afterLoginPage = submitLogin.click();
			if (afterLoginPage.asText().contains("incorrect login"))
				throw new TrainingLogException("Can not login into the USAT NCC site - please check your user ID and password");
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new TrainingLogException("Network error - Can not login into the USAT NCC site - please check your user ID and password",
					e1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TrainingLogException("Login page - unknown error occured on USAT NCC site", e);
		}
	}

	private void doStartEnterResultsPage() throws TrainingLogException {
		log.info("--- doStartEnterResultsPage()");
		// Now need to click on "Enter Results" link
		HtmlAnchor enterResultsLink = afterLoginPage.getAnchorByText("Enter Results");
		log.info("HTML link = " + enterResultsLink.asXml());
		try {
			enterWorkoutPage = enterResultsLink.click();
		} catch (IOException e1) {
			e1.printStackTrace();
			String errorMsg = "There is a problem with USAT site - after Login page";
			log.info(errorMsg);
			throw new TrainingLogException(errorMsg, e1);
		}
	}

	private void doLogoutPage() {
		log.info("--- doLogoutPage()");

		try {
			@SuppressWarnings("unused")
			HtmlPage logoutPage = webClient.getPage(logoutUrl);
			// log.info(logoutPage.asText());
		} catch (FailingHttpStatusCodeException e1) {
			e1.printStackTrace();
			log.info("Can not logout due to an error with USAT site");
			// throw new TrainingLogException("Can not logout due to an error with USAT site", e1);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			log.info("Wrong URL for USAT site when getting logout page");
			// throw new TrainingLogException("Wrong URL for USAT site when getting logout page", e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			log.info("Network error when getting USAT logout page");
			// throw new TrainingLogException("Network error when getting USAT logout page", e1);
		}
	}

	/**
	 * This method adds three workouts to the website (one of each kind - swim, bike, run) and returns the next page to be browsed and also updates the list of sessions that were added in the
	 * addedSessions parameter
	 * 
	 * @param enterWorkoutPage
	 * @param swimSession
	 * @param bikeSession
	 * @param runSession
	 * @param addedSessions
	 * @return
	 * @throws TrainingLogException
	 */

	private boolean doEnterResultsPage(WorkoutSession session) throws TrainingLogException {
		log.info("--- doEnterResultsPage()");
		if (session == null)
			return false;

		GregorianCalendar calendar = new GregorianCalendar();
		if (enterWorkoutPage == null)
			return false;
		String pageText = enterWorkoutPage.asText();

		// First need to check if the result falls in the correct month
		if (!pageText.contains(new SimpleDateFormat("MMMMM").format(session.getWorkoutDate()))) {
			log.info("*********** The workout falls outside of the allowed date range: " + session.toString());
			return false;
		}

		// Find the form on the page so we can submit it later
		HtmlForm formAdd = enterWorkoutPage.getForms().get(0);
		HtmlSubmitInput submitResultsNext = formAdd.getInputByValue(" Next > ");
		HtmlSelect dateField = null;
		HtmlTextInput distance = null;
		HtmlRadioButtonInput runType = null;
		HtmlRadioButtonInput swimType = null;

		// Swimming data
		if (session.getWorkoutType() == WorkoutType.Swim) {
			distance = formAdd.getInputByName("swimd");
			distance.setValueAttribute(Long.toString(session.getWorkoutDistanceYards())); // minimum yards is 50
			swimType = formAdd.getInputByName("swimtype");
			swimType.setDefaultChecked(true); // this sets it to yards
			dateField = formAdd.getSelectByName("swimdate");
		}

		// Biking data
		if (session.getWorkoutType() == WorkoutType.Bike) {
			distance = formAdd.getInputByName("biked");
			distance.setValueAttribute(Double.toString(session.getWorkoutDistanceMiles())); // always in miles only
			dateField = formAdd.getSelectByName("bikedate");
		}

		// Running data
		if (session.getWorkoutType() == WorkoutType.Run) {
			distance = formAdd.getInputByName("rund");
			distance.setValueAttribute(Double.toString(session.getWorkoutDistanceMiles())); // always in miles only
			runType = formAdd.getInputByName("runtype");
			runType.setDefaultChecked(true); // this sets it to regular "run" (as opposed to Elliptical or x-country skiing)
			dateField = formAdd.getSelectByName("rundate");
		}

		if (dateField == null)
			// this would mean that the workout is of none of the types specified above, so we abort
			return false;
		calendar.setTime(session.getWorkoutDate());
		String dayOfTheMonth = Long.toString(calendar.get(Calendar.DAY_OF_MONTH));
		Iterator<HtmlOption> datesIterator = dateField.getOptions().iterator();
		boolean dateFound = false;
		while (datesIterator.hasNext()) {
			HtmlOption htmlOption = (HtmlOption) datesIterator.next();
			if (htmlOption.asText().contentEquals(dayOfTheMonth)) {
				// If at least one date corresponding to the workout date is found in options, we are OK to proceed
				dateFound = true;
			}
			// log.info(htmlOption.asText());
		}
		dateField.setSelectedAttribute(dayOfTheMonth, true);

		if (!dateFound) {
			log.info("We have not found a date for the workout in the allowed date range, hence exit without saving anything. Workout = "
					+ session.toString());
			return false;
		}
		// System.getProperties().put("org.apache.http.wire", "trace");
		confirmWorkoutPage = null;
		try {
			// this sends us to the confirmation page, assuming all goes well
			confirmWorkoutPage = submitResultsNext.click();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new TrainingLogException("There was a problem with USAT NCC site when adding workouts", e1);
		} catch (Exception e) {
			// Something went wrong - not sure what...
			e.printStackTrace();
			throw new TrainingLogException("The workout could not be added - perhaps it has a wrong date or something else went wrong", e);
		}

		if (confirmWorkoutPage == null) {
			log.info("Error while adding workout to USAT site - can not confirm workout entry");
			return false;
		}
		if (confirmWorkoutPage.asText().contains("Please verify your entry before saving")) {
			// Confirmation page needs to have this text to make sure workout is being added correctly
			return true;
		} else {
			log.info("Could not confirm that the workout was added - reason is unknown. Workout = " + session.toString());
			return false;
		}
	}

	/**
	 * Returns TRUE if confirmation successful and FALSE if not
	 * 
	 * @return
	 * @throws TrainingLogException
	 */
	private boolean doConfirmResultsPage() throws TrainingLogException {
		log.info("--- doConfirmResultsPage()");
		// log.info(confirmWorkoutPage.asText());
		// Now confirm and submit the results
		HtmlForm formConfirm = confirmWorkoutPage.getForms().get(0);
		HtmlSubmitInput submitConfirmation = formConfirm.getInputByValue("Save results >");
		try {
			reviewWorkoutsPage = submitConfirmation.click();
			log.info(" SHOW CONFIRMED RESULTS PAGE");
			// log.info(reviewWorkoutsPage.asXml());
			// log.info(reviewWorkoutsPage.asText());

			if (reviewWorkoutsPage.asText().contains("Results added successfully")) {
				// we are good to proceed
				return true;
			} else {
				// There was an error adding results
				log.info("doConfirmResultsPage: Could not confirm that the workout was added");
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			log.info("doConfirmResultsPage: Could not confirm that the workout was added - an IO error occured");
			throw new TrainingLogException("There is a problem with USAT site - on confirm workouts page", e1);
		}
	}

	@Override
	public List<WorkoutSession> getWorkoutsBetweenDates(Date startDate, Date endDate) throws TrainingLogException {
		// TODO not implemented yet
		return null;
	}

	public String getUsername(String username) {
		return usatUser;
	}

	public void setUsername(String username) {
		usatUser = username;
	}

	public String getPassword() {
		return usatPassword;
	}

	public void setPassword(String password) {
		usatPassword = password;
	}

	@Override
	public boolean checkLogin() {
		createBrowser();

		try {
			doLoginPage();
			doLogoutPage();
			destroyBrowser();
			return true;
		} catch (TrainingLogException e) {
			e.printStackTrace();
			destroyBrowser();
			return false;
		}
	}

}
