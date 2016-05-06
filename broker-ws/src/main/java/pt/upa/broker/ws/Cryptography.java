package pt.upa.broker.ws;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Cryptography {

	public Cryptography() {
	};

	public Key getKey() {
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
		}
		keyGen.init(56);
		return keyGen.generateKey();
	}

	public byte[] Encrypt(byte[] Doc, Key key) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherBytes = cipher.doFinal(Doc);
		return cipherBytes;
	}

	public byte[] Decrypt(byte[] Doc, Key key) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decipherBytes = cipher.doFinal(Doc);
		return decipherBytes;
	}

	public byte[] makeMAC(byte[] bytes, SecretKey key) throws Exception {

		Mac cipher = Mac.getInstance("HmacMD5");
		cipher.init(key);
		byte[] cipherDigest = cipher.doFinal(bytes);

		return cipherDigest;
	}

	public static boolean verifyMAC(byte[] cipherDigest, byte[] bytes,
			SecretKey key) throws Exception {

		Mac cipher = Mac.getInstance("HmacMD5");
		cipher.init(key);
		byte[] cipheredBytes = cipher.doFinal(bytes);
		return Arrays.equals(cipherDigest, cipheredBytes);
	}
	
	 public SecretKey generate() throws Exception {
	        // generate a DES secret key
	        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
	        keyGen.init(56);
	        SecretKey key = keyGen.generateKey();

	        return key;
	    }
}
