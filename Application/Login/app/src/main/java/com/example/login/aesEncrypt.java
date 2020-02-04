package com.example.login;/*
	Class to run AES encryption/Decryption for HMGCCProject2018 at MMU
	Written by Kyran Pottle
*/
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class aesEncrypt {
	private static SecretKeySpec secretKey;
	private static byte[] key;
	aesEncrypt(String _key) {
		MessageDigest sha = null;
		try {
        	key = _key.getBytes("UTF-8");	//Get key from input. Store for later use
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);	//Hash the key using SHA-1
            key = Arrays.copyOf(key, 16);	//Store key for later use, limiting the array to 16 characters
            secretKey = new SecretKeySpec(key, "AES");	//Create a secret key from the input
        }
        catch (NoSuchAlgorithmException e) {	e.printStackTrace();	}
        catch (UnsupportedEncodingException e) {	e.printStackTrace();	}
	}
	public String encrypt(String plaintext){
        try{
        	Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");	//Set Cipher's mode to AES
        	c.init(Cipher.ENCRYPT_MODE, secretKey);	//Initialise using secretKey
        	return Base64.getEncoder().encodeToString(c.doFinal(plaintext.getBytes("UTF-8")));	//Encrypt plaintext and return outcome
        }
        	catch (Exception e){	System.out.println("Error while encrypting: " + e.toString());	}	//Error
        return null;	//Only return null if error while encrypting
    }
	public String decrypt(String ciphertext){
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");	//Set Cipher's mode to AES
			cipher.init(Cipher.DECRYPT_MODE, secretKey);	//Initialise using secretKey
			return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));	//Decrypt ciphertext and return outcome
		}
		catch (Exception e){	System.out.println("Error while decrypting: " + e.toString());	}	//Error
		return null;	//Only return null if error decrypting
	}
}
