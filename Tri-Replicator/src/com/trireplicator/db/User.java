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

import com.trireplicator.util.SecurityTools;
import com.trireplicator.util.ServerUtils;


/**
 * This class is used to store all users in the local database
 * 
 * @author Roman Kharkovski, http://kharkovski.blogspot.com
 */
@Entity
public class User {  

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String nameTP;
	// This really needs to be stored as byte[] and not as a String...
	private String encryptedPasswordTP;
	private String nameUSAT;
	// This really needs to be stored as byte[] and not as a String...
	private String encryptedPasswordUSAT;
	private Date registrationDate;
	private Date lastVisitDate;
	private Boolean isActive;
	// TODO - this really needs to have the relationship with the Workouts database table, but for now it did not work properly
	// @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	// private ArrayList<Workouts> workouts = new ArrayList<Workouts>();

	@Transient
	private static final Logger log = Logger.getLogger(User.class.getName());
	@Transient
	public static final String tableName = "User";
	@Transient
	private SecurityTools security = null;

	public User() {
		super();
		security = new SecurityTools();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getLastVisitDate() {
		return lastVisitDate;
	}

	public void setLastVisitDate(Date lastVisitDate) {
		this.lastVisitDate = lastVisitDate;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getNameTP() {
		return nameTP;
	}

	public void setNameTP(String nameTP) {
		this.nameTP = nameTP;
	}

	public void setPlainPasswordTP(String plainText) {
		setEncryptedPasswordTP(new String(plain2encrypted(plainText)));
	}

	public void setPlainPasswordUSAT(String plainText) {
		setEncryptedPasswordUSAT(new String(plain2encrypted(plainText)));
	}

	public String getPlainPasswordTP() {
		return encrypted2plain(ServerUtils.decodeStringIntoByteArray(getEncryptedPasswordTP()));
	}

	public String getPlainPasswordUSAT() {
		 return encrypted2plain(ServerUtils.decodeStringIntoByteArray(getEncryptedPasswordUSAT()));
	}

	public String encrypted2plain(byte[] data) {
		String result = null;
		try {
			result = security.decrypt(data);
		} catch (Exception e) {
			e.printStackTrace();
			String error = "Unable to decrypt user password: " + e.toString();
			log.info(error);
			throw new RuntimeException(error);
		}
		return result;
	}

	public String plain2encrypted(String plainText) {
		String result = null;
		try {
			result = ServerUtils.encodeByteArrayIntoString(security.encrypt(plainText));
		} catch (Exception e) {
			e.printStackTrace();
			String error = "Unable to encrypt user password. Error is: " + e.toString();
			log.info(error);
			throw new RuntimeException(error);
		}
		return result;
	}

	public String getNameUSAT() {
		return nameUSAT;
	}

	public void setNameUSAT(String nameUSAT) {
		this.nameUSAT = nameUSAT;
	}

	public String getEncryptedPasswordTP() {
		return encryptedPasswordTP;
	}

	public void setEncryptedPasswordTP(String encryptedPasswordTP) {
		this.encryptedPasswordTP = encryptedPasswordTP;
	}

	public String getEncryptedPasswordUSAT() {
		return encryptedPasswordUSAT;
	}

	public void setEncryptedPasswordUSAT(String encryptedPasswordUSAT) {
		this.encryptedPasswordUSAT = encryptedPasswordUSAT;
	}

	public String toString() {
		String result;

		result = "--- User: ID='" + getUserId().toString() + "' nameUSAT='" + getNameUSAT() + "' nameTP='" + getNameTP()
				+ "' registrationDate='" + getRegistrationDate().toString() + "' lastVisitDate='" + getLastVisitDate().toString()
				+ "' isActive='" + isActive() + "'";

		// Could print workouts too
		// Iterator<Workouts> iterator = workouts.iterator();
		// while (iterator.hasNext()) {
		// Workouts workout = (Workouts) iterator.next();
		// result.concat(workout.toString());
		// }
		return result;
	}

}
