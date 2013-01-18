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

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SecurityTools {

	private static final Logger log = Logger.getLogger(SecurityTools.class.getName());

	/**
	 * For resting purposes one could use these values:
	 */
//	private static String PASSPHRASE = "";
//	private static String KEY = "dc8370fb76f7f18c";
//	public static String CIPHER_INIT_STRING = "DES/ECB/PKCS5Padding";
//	public static String KEY_ALGORYTHM = "DES";
//	public static int KEY_SIZE = 56;
	
	/**
	 * Production settings for security
	 */
	private static String PASSPHRASE = com.trireplicator.secrets.Constants.PASSPHRASE;
	private static String KEY = com.trireplicator.secrets.Constants.SECRET_KEY;
	public static String CIPHER_INIT_STRING = com.trireplicator.secrets.Constants.CIPHER_INIT_STRING;
	public static String KEY_ALGORYTHM = com.trireplicator.secrets.Constants.KEY_ALGORYTHM;
	public static int KEY_SIZE = com.trireplicator.secrets.Constants.KEY_SIZE;
	
	public SecurityTools() {
		super();
	}

	private static SecretKey generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORYTHM);
		keyGenerator.init(KEY_SIZE); 
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	/**
	 * Returns key as String
	 * 
	 * @param key
	 * @return
	 */
	private static String saveKey(SecretKey key) {
		byte[] encoded = key.getEncoded();
		String data = new BigInteger(1, encoded).toString(16);
		return data;
	}

	/**
	 * Restores binary key from String
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static SecretKey loadKey(String keyText) {
		byte[] encoded = new BigInteger(keyText, 16).toByteArray();
		SecretKey key = new SecretKeySpec(encoded, KEY_ALGORYTHM);
		return key;
	}

	public static void setup() throws Exception {
		try {
			SecurityTools instance = new SecurityTools();
			String testText = "test";
			// Now will encrypt and then decrypt the text - if it is not the same- fail setup
			if (!testText.equals(instance.decrypt(PASSPHRASE, instance.encrypt(PASSPHRASE, testText)))) {
				throw new Exception("Error while initializing the encryption engine");
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			log.info("Error during SecurityTools.setup(): " + e.toString());
			throw e;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			log.info("Error during SecurityTools.setup(): " + e.toString());
			throw e;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			log.info("Error during SecurityTools.setup(): " + e.toString());
			throw e;
		}
	}

	public byte[] encrypt(String plaintext) {
		return encrypt(PASSPHRASE, plaintext);
	}

	public String decrypt(byte[] ciphertext) {
		return decrypt(PASSPHRASE, ciphertext);
	}

	public byte[] encrypt(String passphrase, String plaintext) {
		try {
			Cipher desCipherEncrypt = Cipher.getInstance(CIPHER_INIT_STRING);
			desCipherEncrypt.init(Cipher.ENCRYPT_MODE, loadKey(KEY));
			byte[] result = desCipherEncrypt.doFinal(plaintext.getBytes());
			return result;
		} catch (Exception e) {
			log.info("Error during ecryption: " + e.toString());
			throw new RuntimeException();
		}
	}

	public String decrypt(String passphrase, byte[] ciphertext) {
		String result = null;
		try {
			Cipher desCipherDecrypt = Cipher.getInstance(CIPHER_INIT_STRING);
			desCipherDecrypt.init(Cipher.DECRYPT_MODE, loadKey(KEY));
			result = new String(desCipherDecrypt.doFinal(ciphertext));
			return result;
		} catch (Exception e) {
			log.info("Error during decryption: " + e.toString());
			throw new RuntimeException();
		}
	}

	public void simpleExample() {

		try {
			setup();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String text = "Here goes my secret text - for now in open format";
		System.out.println("Text: " + text);

		byte[] textEncrypted = encrypt(text);
		System.out.println("Text Encryted: " + textEncrypted);

		String textDecrypted = decrypt(textEncrypted);
		System.out.println("Text Decryted: " + new String(textDecrypted));

	}

	public void generateAndShowKeyExample() {

		try {

			setup();
			SecretKey newKey = generateKey();
			String textKey = saveKey(newKey);
			System.out.println(textKey);

		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		SecurityTools tool = new SecurityTools();
		SecurityTools.setup();
		tool.simpleExample();
		tool.generateAndShowKeyExample();
	}
}