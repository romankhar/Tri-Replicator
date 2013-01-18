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

import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.trireplicator.client.Utils;


/**
 * This table is designed to keep track of admin events with the server - stop times, start times, etc.
 * 
 * @author Roman
 *  
 */
@Entity 
public class AdminEvents {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventId;
	private String eventName;
	private Date date;
	private String comments;
	private String version;

	@Transient
	private static final Logger log = Logger.getLogger(AdminEvents.class.getName());
	@Transient
	public static final String tableName = "AdminEvents";

	public AdminEvents() {
		super();
		setVersion(Utils.VERSION);
		setDate(new Date());
	}

	public AdminEvents(String eventName, String comments) {
		super();
		setEventName(eventName); 
		setDate(new Date());
		setComments(comments);
		setVersion(Utils.VERSION);
	}

	public String toString() {
		String result;

		result = "--- Event: ID='" + getEventId().toString() + "' name='" + getEventName() + "' date='" + getDate().toString()
				+ "' comments='" + getComments() + "'";
		return result;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
