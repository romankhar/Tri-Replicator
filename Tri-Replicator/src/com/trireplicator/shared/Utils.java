/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.shared;

import java.util.logging.Logger;

/**
 * Static Utility class with useful stuff for client and server
 * 
 * @author Roman Kharkovski, http://kharkovski.blogspot.com
 */
public class Utils {

	/**
	 * Version number of the software
	 */
	public static final String VERSION = "1.16";
	
	/**
	 * This can be used to subtract one day from System.currentTimeMillis()
	 */ 
	public static long ONE_DAY = 1000 * 60 * 60 * 24;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	

	/**
	 * Converts meters to yards
	 * @param meters
	 * @return yards
	 */
	public static Double meters2Yards(Double meters) {
		if (meters == null)
			return new Double(0.0);

		return meters / 0.9144;
	}

	public static long approximateMeters2Yards(Double meters) {
		return meters2Yards(meters).longValue();
	}

	public static double yards2miles(Double yards) {
		return yards / 1760;
	}

	public static long oneDayAgo() {
		return System.currentTimeMillis() - ONE_DAY;
	}

	public static long twoDaysAgo() {
		return System.currentTimeMillis() - ONE_DAY * 2;
	}

	public static long fefteenDaysAgo() {
		return System.currentTimeMillis() - ONE_DAY * 15;
	}

	public static long oneDayAhead() {
		return System.currentTimeMillis() + ONE_DAY;
	}

}
