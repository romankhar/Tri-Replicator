/**
 * Tri-Replicator Application
 * 
 * To learn more about the app, visit this blog:
 * http://kharkovski.blogspot.com/2013/01/tri-replicator-free-app-on-google-app.html
 * 
 *  @author Roman Kharkovski, http://kharkovski.blogspot.com
 *  Created: December 19, 2012
 */

package com.trireplicator.util;

import javax.xml.bind.DatatypeConverter;

public class ServerUtils {
	
//	private static final Logger log = Logger.getLogger(ServerUtils.class.getName());

	/**
	 * Used by the security encryption to do Base64 encoding so that encrypted data can be later serialized and stored in the DB, etc.
	 * @param input
	 * @return Base64 encoded representation of the input 
	 */
	public static String encodeByteArrayIntoString(byte[] input) {
		return DatatypeConverter.printBase64Binary(input);
	}

	/**
	 * Used by the security encryption to do Base64 de-coding
	 * @param input
	 * @return Base64 de-coded representation of the input 
	 */
	public static byte[] decodeStringIntoByteArray(String input) {
		return DatatypeConverter.parseBase64Binary(input);
	}

//	public static void badLog(String msg) {
//		if (Utils.BAD_LOG_ENABLED) {
//			log.info("App Version='" + Utils.VERSION + "': " + msg);
//		}
//	}

}
