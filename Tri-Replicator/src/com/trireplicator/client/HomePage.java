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


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
//import org.spacetimeresearch.gwt.addthis.client.AddThisWidget;
import com.trireplicator.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HomePage implements EntryPoint {
	/**
	 * When this is set to FALSE we are not checking any backend connections - TESTING only
	 */ 
	private static final boolean REQUIRE_REAL_LOGIN = true;
	
	/**
	 * The message displayed to the user when the server cannot be reached or returns an error.
	 */
	private static String USER_LOGIN_ERROR_MSG = "Sorry, it seems like something did not work right. Here is what you can do:</br></br>(1) check that you entered your Trainingpeaks.com and USAT NCC usernames and passwords correctly</br></br>(2) Check your network connection or </br></br>(3) Reload this page by pressing F5 and try again... </br></br>If none of the above works, please close this window and click on the 'Help' link.";

	private static final String HELP_HTML_TEXT = 
			"<h3>Automatic replication</h3><p>Once you register for this app on 'Sign-Up' page, your workouts from Trainingpeaks.com site will be automatically replicated every few hours into the USAT NCC site. The data of your registration on this app is the date when your workouts start being replicated. For example, if you register today and five days later you add new workout(s) to Trainingpeaks.com dating back (no earlier than today), those new workouts will be automatically replicated. In case you need to replicate workouts dating before your registration date, please go to the 'Replicate Now' link on the main menu.</p><p>Keep in mind that USAT NCC site does not accept workouts older than about 8 days. Once you registered in this system, you really do not need to do anything, other than keep adding your workouts to the Trainingpeaks.com site. The rest will be done for you. I hope this will save you few hours over the entire 3 months of the NCC period.</p>";

	private static final String ABOUT_HTML_TEXT = 
			"<p>Version: "+Utils.VERSION+"</p><h3>What is Tri-Replicator?</h3><p>I have built this application to avoid typing my workouts twice - once in the Trainingpeaks.com, and once in the USAT NCC site. I decided to make it available for FREE to other triathletes. I do not plan on using any data collected in this application for commercial purposes (or any purpose, other than copying data from Trainingpeaks.com to USAT NCC site).</p><h3>Who built it?</h3><p>Development and design was done by <a href=\"http://kharkovski.blogspot.com/p/about-roman-kharkovski.html\">Roman Kharkovski</a>. More details on how this application was built can be found <a href=\"http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html\">in my Blog post here</a>. If you would like to support this application, use it for next year NCC and see new features, you can donate here: <form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_blank\"> <input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\"> <input type=\"hidden\" name=\"hosted_button_id\" value=\"AYSSNBC3MHQJ6\"> <input type=\"image\" src=\"https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\"> <img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\"> </form></p><h3>Open Source</h3><p>There are many different websites used by athletes to log their training data. Not everyone uses Trainingpeaks. It would be nice to be able to automatically copy data from those other websites into the USAt NCC, just like you can do now with this application for Trainingpeaks. I can not possibly develop connectors to all of those sites. This is why I decided to make this application Open Source. Now anyone can enhance the functionality of this application, add new connectors, features, etc. If you are interested to become a contributor to this project, please <a href=\"http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html\">visit this page for more information</a>.</p><h3>Privacy rules</h3> <ul> <li>I will never resell or transfer your personal data to any 3rd party.</li> <li>Your passwords are stored in the private Google database with encryption protected by the security of the Google App Engine.</li> <li>Access to this site is possible only via secure SSL connection with encryption.</li> <li>After the end of the USAT National Challenge Competition by the end of winter 2013 all of your personal data will be automatically deleted from this database. If you decide to use this application for the 2013-2014 NCC, you will have to register again in November 2013.</li> <li>I will not send you any emails, unless you ask me to.</li> <li>Each user can only see his own data, but nobody else's data.</li> </ul> <h3>Disclaimer</h3> <p>Since this is a free service, your use of this website means that you have accepted the Terms and Conditions. I do not provide any guarantee of any service and are not liable nor responsible for any and all loss or damage to you or your data. You can use this application at your own risk. This service is provided AS IS without any warranties.</p>";
	
	private static final Boolean LOGGED_IN = true;
	private static final Boolean LOGGED_OUT = false;
	private boolean isUserLoggedIn = LOGGED_OUT;

	private TextBox userNameUSAT = new TextBox();
	private PasswordTextBox passwordUSAT = new PasswordTextBox();
	private TextBox userNameTP = new TextBox();
	private PasswordTextBox passwordTP = new PasswordTextBox();

	/**
	 * Overall main page space
	 */
	// private DockLayoutPanel page = new DockLayoutPanel(Unit.EM);
	private DockPanel page = new DockPanel();

	/**
	 * The page that goes inside of the center of the main docking space - all others will be shown - added/removed from here
	 */
	VerticalPanel centerSpace = new VerticalPanel();

	/**
	 * Login page and its resources
	 */
	VerticalPanel loginPage = new VerticalPanel();
	// This button is shown on the login page itself
	final Button loginButton = new Button("Sign Up / Login");

	/**
	 * Main menu in the middle of the screen and its resources
	 */
	HorizontalPanel mainMenu = new HorizontalPanel();
	HorizontalPanel headerPanel = new HorizontalPanel();

	VerticalPanel aboutPage = new VerticalPanel();
	VerticalPanel deleteUserPage = new VerticalPanel();
	VerticalPanel confirmAccountDeletionPage = new VerticalPanel();
	VerticalPanel replicateWorkoutPage = new VerticalPanel();
	VerticalPanel automatedReplicationPage = new VerticalPanel();
	VerticalPanel confirmReplicationResultsPage = new VerticalPanel();
	VerticalPanel logoutPage = new VerticalPanel();
	VerticalPanel helpPage = new VerticalPanel();
	VerticalPanel historyPage = new VerticalPanel();

	/**
	 * This is global wait window - no buttons, just like a hour glass in windows
	 * Indicates that something is going on on the server and user must wait 
	 */
	private DialogBox waitWindow = new DialogBox();

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final SynchronizerServiceAsync remoteService = GWT.create(SynchronizerService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		isUserLoggedIn = LOGGED_OUT;
		page.setStyleName("cw-DockPanel");
		page.setSpacing(6);
		page.setHorizontalAlignment(DockPanel.ALIGN_LEFT);

		// Create all pages
		createLoginPage();
		createAboutPage();
		createDeleteUserPage();
		createMainMenuPage();
		createConfirmAccountDeletedPage();
		createReplicateWorkoutPage();
		createconfirmReplicationResultsPage();
		createLogoutPage();
		createAutomatedReplicationPage();
		createHelpPage();
		createHistoryPage();
		createHeader();

		// page.setStyleName("cw-DockPanel");
		// page.setSpacing(4);
		// page.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		// page.addNorth(header(), 4);
		// page.addSouth(footer(), 4);
		// page.addWest(leftNavigation(), 10);
		// page.add(centerSpace());

		page.add(headerPanel, DockPanel.NORTH);
		page.add(createFooter(), DockPanel.SOUTH);
		page.add(createRightSide(), DockPanel.EAST);
		page.add(createLeftNavigation(), DockPanel.WEST);
		page.add(mainMenu, DockPanel.NORTH);
		page.add(createSecondFooter(), DockPanel.SOUTH);

		// First page to be added is the login page
		// centerSpace.add(loginPage);
		page.add(centerSpace, DockPanel.CENTER);
		
		// This is to replace entire body of the html page
//		RootLayoutPanel.get().add(page); 
		
		// This will only be inserted into the specific div on the html page
		RootPanel.get("MainContent").add(page); 

	}

	private void createHistoryPage() {
		HTML text = new HTML("Here is the history of your workouts that have been replicated");
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.add(text);
//		CellTable table = new CellTable();
		// scrollPanel.add(table);

		historyPage.add(scrollPanel);
	}

	private void createHelpPage() {
		// Frame frame = new Frame("Help.html");
		// frame.setHeight(heightOfHTMLPages);
		// frame.setWidth(widthOfHTMLPages);

		HTML text = new HTML(HELP_HTML_TEXT);
		helpPage.add(text);
		helpPage.add(new HTML(
				"<h3>Need additional help?</h3><p>If you have questions on how to use this application, please visit <a href=\"https://www.facebook.com/groups/488217264563704/\">Tri-Replicator Group on Facebook</a> . If you do not find what you need, feel free to post your questions there. If none of the above helps, please contact: <a href=\"http://kharkovski.blogspot.com\">Roman Kharkovski</a> via email.</p><h3>Support this application</h3><p>If you would like to support this application and see new features, you can donate whatever amount you like (as long as it is positive amount :-). <form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_blank\"> <input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\"> <input type=\"hidden\" name=\"hosted_button_id\" value=\"AYSSNBC3MHQJ6\"> <input type=\"image\" src=\"https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif\" border=\"0\" name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\"> <img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/en_US/i/scr/pixel.gif\" width=\"1\" height=\"1\"> </form> </p>"));
	}

	private void createAutomatedReplicationPage() {
		HTML text = new HTML(HELP_HTML_TEXT);
		automatedReplicationPage.add(text);
	}

	private void createLogoutPage() {
		logoutPage.add(new HTML("You are now logged out"));
	}

	private void createconfirmReplicationResultsPage() {
		HTML text = new HTML("Your workouts have been successfully replicated");
		confirmReplicationResultsPage.add(text);
		// TODO - need to show the list of workouts that have been replicated
	}

	private void createReplicateWorkoutPage() {
		HTML title = new HTML("</br></br><h2>Would you like to replicate workouts for this user now?</h1></br>");
		replicateWorkoutPage.add(title);

		HorizontalPanel dialogHPanel = new HorizontalPanel();
		final DateBox startDateBox = new DateBox();
		final DateBox endDateBox = new DateBox();
		dialogHPanel.add(new Label("Start date:"));
		dialogHPanel.add(startDateBox);
		dialogHPanel.add(new Label("End date:"));
		dialogHPanel.add(endDateBox);
		replicateWorkoutPage.add(dialogHPanel);

		HTML text = new HTML(
				"</br></br>Please note that this system automatically replicates your workouts from trainingpeaks.com into USAT NCC site every few hours. However the start date for those workouts is the date that you regestered in this system. If you already used this site to replicate workouts (either manually or automatically), we will not add those workouts second time. You can do as many replications as you want, but we keep history of replicated workouts and will not send the same workout to the USAT NCC site twice. If you need to clean your history, please delete your account and re-register again.");
		replicateWorkoutPage.add(text);

		final Button replicateButton = new Button("Yes, replicate workouts now.");
		// Create a handler for the Button
		replicateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				replicateButton.setEnabled(false);
				showWaitWindow("Please wait while we are replicating your workouts...");
				remoteService.replicateWorkoutsForUser(new Long(0), userNameTP.getText(), passwordTP.getText(), userNameUSAT.getText(),
						passwordUSAT.getText(), startDateBox.getValue(), endDateBox.getValue(), new AsyncCallback<Integer>() {
							public void onFailure(Throwable caught) {
								replicateButton.setEnabled(true);
								closeWaitWindow();
								// Show the RPC error message to the user
								showErrorBox(
										"Opps...",  
										"For some reason we could not replicate your workouts, perhaps remote servers are not available. But do not worry, we have regular replication setup, so your workouts will probably get replicated in a couple of hours anyway. You do not have to do it manually...");
							}

							@Override
							public void onSuccess(Integer result) {
								replicateButton.setEnabled(true);
								closeWaitWindow();
								if (result > 0) {
									showMessageOnMainScreen("Replication successful...", "We have replicated <b>"+result+"</b> workouts. Feel free to double check the <a href=\"http://www.race-tracker.net/usat/admin/showmyresults.cfm\">USAT NCC site</a> to make sure all these workouts are shown on your account");
//									cleanCenterSpace();
//									centerSpace.add(confirmReplicationResultsPage);
								} else {
									showMessageOnMainScreen("Replication successful...", "But it does not seem like there were any new workouts to be replicated.");
								}
							}
						});
			}
		});

		dialogHPanel.add(replicateButton);
		// replicateWorkoutPage.setCellHorizontalAlignment(replicateButton, HasHorizontalAlignment.ALIGN_CENTER);
	}

	private void createConfirmAccountDeletedPage() {
		HTML text = new HTML(
				"</br></br>Your account has been deleted.</br></br>If you would like to resume replication of your data from Trainingpeaks.com to the USAT NCC site, please register again.");
		confirmAccountDeletionPage.add(text);
	}

	private void cleanCenterSpace() {
		centerSpace.clear();
	}

	private void createMainMenuPage() {

		int loginColumn = 0;
		int replicateOneUserColumn = 1;
		int replicateAllColumn = 2;
		int deleteColumn = 3;
		int logoutColumn = 4;
		int aboutColumn = 5;
		int helpColumn = 6;

		int lastColumn = helpColumn;

		// Create a grid
		Grid menuGrid = new Grid(2, lastColumn + 1);
		mainMenu.add(menuGrid);

		// ------------------------------------------------ Define login button
		Image loginImage = new Image("images/symbol_check.png");
		loginImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, loginColumn, loginImage);
		Anchor login = new Anchor("Sign Up / Login");
		menuGrid.setWidget(1, loginColumn, login);
		class LoginClickHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				cleanCenterSpace();
				centerSpace.add(loginPage);
				loginButton.setVisible(true);
				loginButton.setEnabled(true);
			}
		}
		LoginClickHandler loginHandler = new LoginClickHandler();
		loginImage.addClickHandler(loginHandler);
		login.addClickHandler(loginHandler);

		// ------------------------------------------------ Define logout button
		Image logoutImage = new Image("images/symbol_stop.png");
		logoutImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, logoutColumn, logoutImage);
		Anchor logout = new Anchor("Logout");
		menuGrid.setWidget(1, logoutColumn, logout);

		class LogoutClickHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				if (isUserLoggedIn) {
					cleanCenterSpace();
					logout();
					centerSpace.add(logoutPage);
					isUserLoggedIn = LOGGED_OUT;
				} else {
					showMessageOnMainScreen("Opps...", "Please login or register before using this feature.");
				}
			}
		}
		LogoutClickHandler logoutHandler = new LogoutClickHandler();
		logoutImage.addClickHandler(logoutHandler);
		logout.addClickHandler(logoutHandler);

		// ------------------------------------------------ Define About page
		Image aboutImage = new Image("images/symbol_construction.png");
		aboutImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, aboutColumn, aboutImage);
		Anchor about = new Anchor("About");
		menuGrid.setWidget(1, aboutColumn, about);

		class AboutClickHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				cleanCenterSpace();
				centerSpace.add(aboutPage);
			}
		}
		AboutClickHandler aboutHandler = new AboutClickHandler();
		aboutImage.addClickHandler(aboutHandler);
		about.addClickHandler(aboutHandler);

		// ------------------------------------------------ Define Help page
		Image helpImage = new Image("images/symbol_help.png");
		helpImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, helpColumn, helpImage);
		Anchor help = new Anchor("Help");
		menuGrid.setWidget(1, helpColumn, help);

		class HelpClickHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				cleanCenterSpace();
				centerSpace.add(helpPage);
			}
		}
		HelpClickHandler helpHandler = new HelpClickHandler();
		helpImage.addClickHandler(helpHandler);
		help.addClickHandler(helpHandler);

		// ------------------------------------------------ Define "delete user account" button
		Image deleteImage = new Image("images/symbol_delete.png");
		deleteImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, deleteColumn, deleteImage);
		Anchor delete = new Anchor("Delete account");
		menuGrid.setWidget(1, deleteColumn, delete);

		class DeleteButtonHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				if (isUserLoggedIn) {
					cleanCenterSpace();
					centerSpace.add(deleteUserPage);
				} else {
					showMessageOnMainScreen("Opps...", "Please login or register before using this feature.");
				}
			}
		}
		DeleteButtonHandler deleteHandler = new DeleteButtonHandler();
		deleteImage.addClickHandler(deleteHandler);
		delete.addClickHandler(deleteHandler);

		// ------------------------------------------------ Define "replicate now" button
		Image replicateOneUserImage = new Image("images/symbol_add.png");
		replicateOneUserImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, replicateOneUserColumn, replicateOneUserImage);
		Anchor replicateOneUser = new Anchor("Replicate now");
		menuGrid.setWidget(1, replicateOneUserColumn, replicateOneUser);

		class ReplicateOneUserButtonHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				if (isUserLoggedIn) {
					cleanCenterSpace();
					centerSpace.add(replicateWorkoutPage);
				} else {
					showMessageOnMainScreen("Opps...", "Please login or register before using this feature.");
				}
			}
		}
		ReplicateOneUserButtonHandler replicateOneUserHandler = new ReplicateOneUserButtonHandler();
		replicateOneUserImage.addClickHandler(replicateOneUserHandler);
		replicateOneUser.addClickHandler(replicateOneUserHandler);

		// ------------------------------------------------ Define "Setup automatic replication"
		Image replicateAutoImage = new Image("images/symbol_refresh.png");
		replicateAutoImage.getElement().getStyle().setCursor(Cursor.POINTER);
		menuGrid.setWidget(0, replicateAllColumn, replicateAutoImage);
		Anchor replicateAuto = new Anchor("Scheduled replication");
		menuGrid.setWidget(1, replicateAllColumn, replicateAuto);

		class ReplicateAutoHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				if (isUserLoggedIn) {
					cleanCenterSpace();
					centerSpace.add(automatedReplicationPage);
				} else {
					showMessageOnMainScreen("Opps...", "Please login or register before using this feature.");
				}
			}
		} 
		ReplicateAutoHandler replicateAutoHandler = new ReplicateAutoHandler();
		replicateAutoImage.addClickHandler(replicateAutoHandler);
		replicateAuto.addClickHandler(replicateAutoHandler);

		// ---------------------------------------------------- Now lets align all cells in the middle
		HTMLTable.CellFormatter formatter = menuGrid.getCellFormatter();
		for (int i = 0; i < menuGrid.getRowCount(); i++) {
			for (int j = 0; j < menuGrid.getColumnCount(); j++) {
				formatter.setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER);
			}
		}

	}

	private void createDeleteUserPage() {

		HTML askConfirmation = new HTML(
				"</br></br>Are you sure you would like to <b><u>delete</u></b> your account data and stop automatic replication between Trainingpeaks.com and USAT NCC sites?</br></br>");
		askConfirmation.setStyleName("errorText");
		deleteUserPage.add(askConfirmation);

		final Button yesButton = new Button("Yes");
		final Button noButton = new Button("No");
		yesButton.setWidth("12em");
		noButton.setWidth("12em");

		HorizontalPanel dialogHPanel = new HorizontalPanel();
		dialogHPanel.setSpacing(10);
		dialogHPanel.add(yesButton);
		dialogHPanel.add(noButton);
		deleteUserPage.add(dialogHPanel);

		yesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Delete the account here
				remoteService.removeUserWithCount(userNameTP.getText(), passwordTP.getText(), userNameUSAT.getText(),
						passwordUSAT.getText(), new AsyncCallback<Integer>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								showErrorBox("Opps...",
										"For some reason we could not delete your account, or perhaps server is no longer available. </br></br> Please try again later...");
							}

							public void onSuccess(Integer result) {
								if (result > 0) {
									// Go to the confirmation page about the account deletion
									cleanCenterSpace();
									centerSpace.add(confirmAccountDeletionPage);
								} else {
									showErrorBox("Opps...",
											"For some reason we could not delete your account. </br></br> Please try again later or contact site administrator for assistance...");
								}
							}
						});
			}
		});

		// Add a handler for no Button
		noButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				cleanCenterSpace();
				// centerSpace.add(mainMenu);
			}
		});
	}

	private void createAboutPage() {
//		Frame frame = new Frame("About.html");
//		frame.setHeight(heightOfHTMLPages);
//		frame.setWidth(widthOfHTMLPages);
//		aboutPage.add(frame);
		
		aboutPage.add(new HTML(ABOUT_HTML_TEXT));
	}

	private Widget createSecondFooter() {
		VerticalPanel navigation = new VerticalPanel();
		// HTML text = new HTML("");
		// navigation.add(text);
		return navigation;
	}

	@SuppressWarnings("unused")
	private Widget createSecondHeader() {
		VerticalPanel navigation = new VerticalPanel();
		// HTML text = new HTML("");
		// navigation.add(text);
		return navigation;
	}

	private Widget createRightSide() {
		VerticalPanel navigation = new VerticalPanel();
		// HTML text = new HTML("");
		// navigation.add(text);
		return navigation;
	}

	private Widget createLeftNavigation() {
		VerticalPanel navigation = new VerticalPanel();
		// navigation.add(new HTML("navigation goes here."));
		return navigation;
	}

	private Widget createFooter() {
		/*
		 * <p></p> <br/> <br/> <br/> <br/> <div id="Footer"> <p>Contact: <a href="http://kharkovski.blogspot.com" style="text-align: right;">Roman Kharkovski</a> </div>
		 */
		HorizontalPanel footerPanel = new HorizontalPanel();
		footerPanel.add(new HTML(""));
		return footerPanel;
	}

	private void createHeader() {
		/*
		 * <div id="Menu"> <!-- <a href="/" style="padding: 0px"><img src="Logo-Small.png" width="163" height="35" alt="Logo goes here"/></a> --> <a href="/" style="padding-right: 20px">Home</a> <a
		 * href="About.html" style="padding-right: 20px">About</a> <a href="About.html" style="padding-right: 20px">Logout</a> </div> <br> <a href="/" style="padding: 0px;text-align: center;"><img
		 * src="images/banner.png" alt="Logo goes here"/></a> <br>
		 */
		// headerPanel.add(new Hyperlink("Home","/"));

		HorizontalPanel hPanelLeft = new HorizontalPanel();
		HorizontalPanel hPanelRight = new HorizontalPanel();
		hPanelLeft
				.add(new HTML(
						"<b><font size=\"16px\">Tri-Replicator</font></b> by <a href=\"http://kharkovski.blogspot.com\">Roman Kharkovski</a>, member of the <a href=\"http://www.pittsburghtriathlonclub.com/\">Pittsburgh Triathlon Club</a>"));
//		hPanelRight.add(new HTML(socialButtons));

//		String pubId = "https://tri-replicator.appspot.com/";
//		AddThisWidget addThisWidget = new AddThisWidget(pubId, "Check out this Tri-Replicator - a free app to those who are participating in USAT NCC this winter."); 
//		hPanelRight.add(addThisWidget); 

		headerPanel.add(hPanelLeft);
		headerPanel.add(hPanelRight);
	}

	/**
	 * Initial login dialog.
	 */
	public void createLoginPage() {

		setDefaultUserNames();

		// Create a grid
		Grid grid = new Grid(6, 6);

		// Add images to the grid

		// grid.setWidget(row, column, xx)
		Image usatImage = new Image("images/usat_ncc.png");
		grid.setWidget(0, 1, usatImage);
		grid.setWidget(1, 1, new Anchor("http://www.racetracker.ca/usat", "http://www.racetracker.ca/usat"));
		grid.setWidget(2, 0, new HTML("USAT NCC user name:"));
		grid.setWidget(2, 1, userNameUSAT);
		final Label errorLabelNameUSAT = new Label();
		errorLabelNameUSAT.addStyleName("errorText");
		grid.setWidget(3, 1, errorLabelNameUSAT);
		grid.setWidget(4, 0, new HTML("USAT NCC password:"));
		grid.setWidget(4, 1, passwordUSAT);
		final Label errorLabelPasswordUSAT = new Label();
		errorLabelPasswordUSAT.addStyleName("errorText");
		grid.setWidget(5, 1, errorLabelPasswordUSAT);

		Image tpImage = new Image("images/trainingpeaks.png");
		grid.setWidget(0, 4, tpImage);

		grid.setWidget(1, 4, new Anchor("http://trainingpeaks.com", "http://trainingpeaks.com"));
		grid.setWidget(2, 3, new HTML("Trainingpeaks user name:"));
		grid.setWidget(2, 4, userNameTP);
		final Label errorLabelNameTP = new Label();
		errorLabelNameTP.addStyleName("errorText");
		grid.setWidget(3, 4, errorLabelNameTP);
		grid.setWidget(4, 3, new HTML("Trainingpeaks password:"));
		grid.setWidget(4, 4, passwordTP);
		final Label errorLabelPasswordTP = new Label();
		errorLabelPasswordTP.addStyleName("errorText");
		grid.setWidget(5, 4, errorLabelPasswordTP);

		loginPage.add(grid);

		loginButton.addStyleName("sendButton");
		// sendButton.setLayoutData(layoutData);
		loginPage.add(loginButton);
		loginPage.setCellHorizontalAlignment(loginButton, HasHorizontalAlignment.ALIGN_CENTER);

		// Set proper Tab indexes
		userNameUSAT.setTabIndex(1);
		passwordUSAT.setTabIndex(2);
		userNameTP.setTabIndex(3);
		passwordTP.setTabIndex(4);
		loginButton.setTabIndex(5);

		// Focus the cursor on the name field when the app loads
		userNameUSAT.setFocus(true);
		userNameUSAT.selectAll();

		// ------------------------------------------------------------------------------------------
		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		// dialogBox.setSize("120em", "20em");
		dialogBox.setAnimationEnabled(true);
		final Button agreeButton = new Button("Agree to terms and conditions and register my account");
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		agreeButton.getElement().setId("agreeButton");
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		// dialogVPanel.add(new HTML("<b>Are you sure you would like to update the USAT NCC data?</b>"));
		// dialogVPanel.add(textToServerLabel);
		// dialogVPanel.add(new HTML("<br>Your user account has not been found in our database. Looks like this is your first time using this application.</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		HorizontalPanel dialogHPanel = new HorizontalPanel();
		dialogHPanel.add(agreeButton);
		dialogHPanel.add(closeButton);
		dialogVPanel.add(dialogHPanel);
		dialogBox.setWidget(dialogVPanel);
		agreeButton.setVisible(true);
		agreeButton.setEnabled(true);
		closeButton.setVisible(true);
		closeButton.setEnabled(true);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				loginButton.setEnabled(true);
				userNameUSAT.setFocus(true);
			}
		});

		// --------------------------- Create a handler for the loginButton
		class LoginButtonHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				loginIntoAccounts();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// Do nothing here
					// loginIntoAccounts();
				}
			}

			/**
			 * Perform login procedure
			 */
			private void loginIntoAccounts() {

				agreeButton.setVisible(true);
				agreeButton.setEnabled(true);
				closeButton.setVisible(true);
				closeButton.setEnabled(true);

				// First, we validate the input.
				errorLabelNameUSAT.setText("");
				errorLabelNameTP.setText("");
				errorLabelPasswordTP.setText("");
				errorLabelPasswordUSAT.setText("");

				String userNameUSAT2server = userNameUSAT.getText();
				if (!FieldVerifier.isValidName(userNameUSAT2server)) {
					errorLabelNameUSAT.setText(FieldVerifier.nameErrorText(userNameUSAT2server));
					return;
				}

				String passwordUSAT2server = passwordUSAT.getText();
				if (!FieldVerifier.isValidPassword(passwordUSAT2server)) {
					errorLabelPasswordUSAT.setText(FieldVerifier.passwordErrorText(passwordUSAT2server));
					return;
				}

				String userNameTP2server = userNameTP.getText();
				if (!FieldVerifier.isValidName(userNameTP2server)) {
					errorLabelNameTP.setText(FieldVerifier.nameErrorText(userNameTP2server));
					return;
				}

				String passwordTP2server = passwordTP.getText();
				if (!FieldVerifier.isValidPassword(passwordTP2server)) {
					errorLabelPasswordTP.setText(FieldVerifier.passwordErrorText(passwordTP2server));
					return;
				}

				// Then, we send the input to the server.
				loginButton.setEnabled(false);
				serverResponseLabel.setText("");

				showWaitWindow("Validating your credentials...");
				remoteService.checkExistingUser(userNameTP2server, passwordTP2server, userNameUSAT2server, passwordUSAT2server,
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								closeWaitWindow();
								isUserLoggedIn = LOGGED_OUT;
								if (REQUIRE_REAL_LOGIN) {
									// Show the RPC error message to the user
									dialogBox.setText("Opps...");
									serverResponseLabel.addStyleName("serverResponseLabelError");
									serverResponseLabel.setHTML(USER_LOGIN_ERROR_MSG);
									agreeButton.setVisible(false);
									dialogBox.center();
									closeButton.setFocus(true);
								} else {
									closeWaitWindow();
									// This will execute only for debug purposes - should never happen in real deployment
									dialogBox.hide();
								}
							}

							public void onSuccess(Boolean result) {
								closeWaitWindow();
								serverResponseLabel.removeStyleName("serverResponseLabelError");
								if (result) {
									// Do not need to show the dialog box for existing users
									serverResponseLabel.setHTML("This user is already registered in the system");
									isUserLoggedIn = LOGGED_IN;
									dialogBox.hide();
									cleanCenterSpace();
									showMessageOnMainScreen("Login successful",
											loginConfirmationText(userNameTP.getText(), userNameUSAT.getText()));
								} else {
									dialogBox.setText("New user registration");
									serverResponseLabel.addStyleName("serverResponseLabelError");
									serverResponseLabel
											.setHTML("<b>Seems like you are new here. Would you like to register and start automatically replicating your workouts every day?</b> </br> </br>You would not have to login into this application again as replication will happen automatically in the future until the end of the USAT Challenge.</br></br>");
									isUserLoggedIn = LOGGED_OUT;
									dialogBox.center();
									agreeButton.setVisible(true);
									agreeButton.setFocus(true);
								}
							}
						});
			}
		}

		// When user presses "agree and register button" we need to do all the work
		agreeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				// need to proceed to user registration in the system here and call remote method
				// temporarily show him work in progress window with no buttons
				serverResponseLabel
						.setText("Please wait while we are checking your usernames and passwords against both Trainingpeaks.com and USAT NCC sites and adding you to the system...");
				agreeButton.setVisible(false);
				closeButton.setVisible(false);
				dialogBox.center();

				// Since user has agreed to be registered as a new user, lets do so now
				remoteService.addUser(userNameTP.getText(), passwordTP.getText(), userNameUSAT.getText(), passwordUSAT.getText(),
						new AsyncCallback<Boolean>() {
							public void onFailure(Throwable caught) {
								dialogBox.hide();
								isUserLoggedIn = LOGGED_OUT;
								dialogBox.setText("Opps...");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(USER_LOGIN_ERROR_MSG);
								dialogBox.center();
								agreeButton.setVisible(false);
								closeButton.setVisible(true);
								closeButton.setFocus(true);
							}

							public void onSuccess(Boolean result) {
								serverResponseLabel.removeStyleName("serverResponseLabelError");
								if (result) {
									// Do not need to show the dialog box for existing users
									serverResponseLabel.setHTML("This user is now registered in the system");
									isUserLoggedIn = LOGGED_IN;
									dialogBox.hide();
									cleanCenterSpace();
									showMessageOnMainScreen("Login successful",
											loginConfirmationText(userNameTP.getText(), userNameUSAT.getText()));
								} else {
									dialogBox.setText("New user registration failed");
									serverResponseLabel.addStyleName("serverResponseLabelError");
									serverResponseLabel
											.setHTML("Sorry, we could not register you in the system. Perhaps you entered wrong user name(s) or password(s), or perhaps TrainingPeaks.com or USAT NCC sites experience problems and we are not able to validate your credentials. Please, re-enter your data and try again...");
									isUserLoggedIn = LOGGED_OUT;
									dialogBox.center();
									agreeButton.setVisible(false);
									closeButton.setVisible(true);
									closeButton.setFocus(true);
								}
							}
						});
				if (!isUserLoggedIn) {
					loginButton.setEnabled(true);
					userNameUSAT.setFocus(true);
				}
			}
		});

		// Add a handler for login screen
		LoginButtonHandler loginHandler = new LoginButtonHandler();
		loginButton.addClickHandler(loginHandler);

		// We do not need to react on the Enter key
		// userNameUSAT.addKeyUpHandler(handler);
	}

	/**
	 * This shows Yes/No dialog on the screen
	 * 
	 * @param string
	 *            to be shown in the dialog
	 * @return False if NO, True if YES
	 */
	@SuppressWarnings("unused")
	private void yesNoDialog(String string) {

		// final DialogBox dialogBox = new DialogBox();
		// dialogBox.setAnimationEnabled(true);
		// dialogBox.setText("Please confirm your request...");
		//
		// final Button yesButton = new Button("Yes");
		// final Button noButton = new Button("No");
		// yesButton.setWidth("12em");
		// noButton.setWidth("12em");
		//
		// yesButton.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// answerFromYNDialog = true;
		// dialogBox.hide();
		// }
		// });
		//
		// // Add a handler for no Button
		// noButton.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// answerFromYNDialog = false;
		// dialogBox.hide();
		// }
		// });
		//
		// final HTML textToShow = new HTML(string);
		// textToShow.addStyleName("errorText");
		// VerticalPanel dialogVPanel = new VerticalPanel();
		// dialogVPanel.addStyleName("dialogVPanel");
		// dialogVPanel.add(textToShow);
		// HorizontalPanel dialogHPanel = new HorizontalPanel();
		// dialogHPanel.setSpacing(10);
		//
		// dialogHPanel.add(yesButton);
		// dialogHPanel.add(noButton);
		// dialogVPanel.add(dialogHPanel);
		// dialogBox.setWidget(dialogVPanel);
		// dialogBox.center();
	}

	/**
	 * This shows simple error dialog on the screen - as popup window
	 * 
	 */
	private void showErrorBox(String title, String message) {

		final DialogBox dialogBox = new DialogBox();
		dialogBox.setAnimationEnabled(true);
		dialogBox.setText(title);

		final Button closeButton = new Button("OK");
		closeButton.setWidth("12em");

		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		final HTML textToShow = new HTML(message);
		textToShow.addStyleName("errorText");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(textToShow);
		dialogVPanel.setSpacing(10);

		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		dialogBox.center();
		closeButton.setFocus(true);
	}

	/**
	 * This shows error message on the main screen and removes all other content from that screen
	 */
	private void showMessageOnMainScreen(String header, String message) {

		HTML title = new HTML("<b>" + header + "</b>");
		HTML text = new HTML(message);

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("dialogVPanel");
		vPanel.add(title);
		vPanel.add(text);
		cleanCenterSpace();
		centerSpace.add(vPanel);
	}

	/**
	 * This method logs out the user and hides some mavigational elements from the menu
	 */
	private void logout() {
		isUserLoggedIn = LOGGED_OUT;
		setEmptyUserNames();
	}

	private String loginConfirmationText(String nameTP, String nameUSAT) {
		return "You are now logged in as Trainingpeaks user <b>" + userNameTP.getText() + "</b> and USAT NCC user <b>"
				+ userNameUSAT.getText() + "</b>.";
	}

	private void showWaitWindow(String message) {
		waitWindow.setAnimationEnabled(true);
		waitWindow.setText("Hang on, we are working hard...");

		HTML textToShow = new HTML(message);
		textToShow.addStyleName("errorText");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(textToShow);
		dialogVPanel.setSpacing(10);

		waitWindow.setWidget(dialogVPanel);
		waitWindow.center();
	}

	private void closeWaitWindow() {
		waitWindow.hide();
	}

	private void setDefaultUserNames() {
		setEmptyUserNames();
		
		// TODO - must remove this or replace with something else to avoid public exposure of the private account info
//		userNameTP.setText("xxx");
//		userNameUSAT.setText("xxx");
//		passwordTP.setText("xxx");
//		passwordUSAT.setText("xxx"); 

	}

	private void setEmptyUserNames() {
		userNameTP.setText("");
		userNameUSAT.setText("");
		passwordTP.setText("");
		passwordUSAT.setText("");
	}
}
