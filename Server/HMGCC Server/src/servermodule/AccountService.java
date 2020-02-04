package servermodule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import controller.Server;
import controller.TokenDAO;
import controller.UserDAO;
import model.ConnectedClient;
import model.Token;
import model.User;

/**
 * Class used to handle all user account requests on the server.
 * @author Oliver Bowker
 * @version 1.0
 */
public class AccountService {

	private Server server;
	private EmailService email;
	
	/**
	 * Constructor for AccountService class.
	 * @param server = Server object for program.
	 */
	public AccountService(Server server, String email, String password) {
		this.server = server;
		this.email = new EmailService(password);
	}
	
	
	/**
	 * Function used to handle the log in request of a user.
	 * @param c = Connected client that is attempting to log into the server.
	 * @param message = Message recieved from the user.
	 * @return whether or not the client successfully logged in.
	 */
	public String[] login(ConnectedClient c, String message) {
			String[] userDetails;
			String[] ret_arr = new String[2];
			try {
				userDetails = message.split("LOGIN ")[0].split(" ");
				
				String email = userDetails[1].trim();
				String password = userDetails[2].trim();
				UserDAO dao = new UserDAO();
				User user = dao.login(email, hashPassword(password));
				
				// Check if user was found.
				
				if (user != null) {
					// Check if user account activated.
					if (!user.isActive()) {
						ret_arr[0] = "0";
						ret_arr[1] = "Account not Activated";
						return ret_arr;
					}
					
					server.connections.get(findClient(c)).setUser(user);
					server.connections.get(this.findClient(c)).setConnectionStatus("Logged In");
					server.connections.get(this.findClient(c)).setID(user.getUserID());
					
					// Update Key Values
					server.sendMessage("Login Success " + c.getUser().getEncryptionKey(), c, false);
					c.encrypt = new EncryptionService(c.getUser().getEncryptionKey());
					c.setKey(c.getUser().getEncryptionKey());
					ret_arr[0] = "1";
					
					ret_arr[1] = "Signed In";
					return ret_arr;
				} else {
					ret_arr[0] = "0";
					ret_arr[1] = "No Such User Exists";
					return ret_arr;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return ret_arr;
	}
	
	/**
	 * Function used to handle sign up requests of users.
	 * @param c = Client that made the sign up request.
	 * @param message = Message/details the client specified.
	 * @return whether or not the client successfully signed up.
	 */
	public boolean signup(ConnectedClient c, String message) {
		try {
			String[] userDetails = message.split(" ");
			UserDAO dao = new UserDAO();
			User user = dao.addUser(new User(userDetails[1], userDetails[2], hashPassword(userDetails[3]), userDetails[4]));
			try {
				if (user != null) {
					server.connections.get(this.findClient(c)).setUser(user);
					email.sendSignupVerification(c.getUser());
					server.sendMessage("Signup Success", c, true);
					return true;
				}
				else return false;
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return false;
		
	}
	
	public static String hashPassword(String password) {
		   try { 
	            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
	 
	            byte[] messageDigest = md.digest(password.getBytes()); 
	            BigInteger no = new BigInteger(1, messageDigest); 
	            String hashtext = no.toString(16); 
	  
	            // Add preceding 0s to make it 32 bit 
	            while (hashtext.length() < 32) { 
	                hashtext = "0" + hashtext; 
	            }
	            return hashtext; 
	        } 

	        catch (NoSuchAlgorithmException e) { 
	            throw new RuntimeException(e); 
	        } 
	}
	
	/**
	 * Function used to verify a users account/verification code.
	 * @param c = Client that made the verify request.
	 * @param message = Message/details the client sent to the server.
	 * @return whether or not the verification was successful.
	 */
	public boolean verifySignup(ConnectedClient c, String message) {
		try {
			String userToken = message.split(" ")[1].trim();
			TokenDAO tdao = new TokenDAO();
			Token token = tdao.getToken(c.getUser().getEmail());
			int curTime = (int) System.currentTimeMillis();
			
			if (Math.abs(curTime - token.getCreationTime()) < 300000 && token.getTokenID().equals(userToken)) {
				UserDAO udao = new UserDAO();
				c.getUser().setActive(true);
				if(udao.updateUser(c.getUser(), c.getUser().getUserID())) {
					this.server.sendMessage("Verify Success", c, true);
					try {
						// Create users files
						new File(c.getUser().getUserID()).mkdir();
						new File(c.getUser().getUserID() + "/friends.txt").createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
			} else {
				System.err.println("Token Ran Out : " + (curTime - token.getCreationTime()) +"\n User:" + userToken + " - Val:" + token.getTokenID());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean encryptSMS(ConnectedClient c, String message) {
		try {
			String sendersNumber = c.getUser().getPhone();
			String recepNumber = message.split(" ")[1];
			String unencryptedMessage = getWholeMessage(message.split(" "));
			System.out.println("Unecrypted SMS : " + unencryptedMessage);
			String key = checkConversationFileExists(sendersNumber, recepNumber);
			EncryptionService shared = new EncryptionService(key);
			String encryptedMessage = shared.encrypt(unencryptedMessage);
			server.sendMessage(encryptedMessage, c, false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean decryptSMS(ConnectedClient c, String message) {
		try {
			String encryptedMessage = message.split(" ")[1];
			String decryptedMessage = c.encrypt.decrypt(encryptedMessage);
			UserDAO dao = new UserDAO();
			User user = dao.getUserByPhone(decryptedMessage.split(" ")[1]);
			EncryptionService tempEncrypt = new EncryptionService(user.getEncryptionKey());
			String finalString = tempEncrypt.decrypt(getWholeMessage(message.split(" ")));
			server.sendMessage(finalString, c, true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String getWholeMessage(String[] message)  {
		String ret_mes = "";
		for(int i=2; i<message.length; i++) {
			ret_mes += message[i] + " ";
		}
		return ret_mes;
	}
	
	private String checkConversationFileExists(String sendersNumber, String recepNumber) {
		String filePath1 = sendersNumber + recepNumber;
		String filePath2 = recepNumber + sendersNumber;
		String key = "";
		
		try {
			if (new File("conversations/"+ filePath1).exists() || new File("conversations/" + filePath2).exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(new File("conversations/" + filePath1)));
				key = reader.readLine();
				System.out.println("Shared Key : " + key);
				reader.close();
			} else {
				File file1 = new File("conversations/" + filePath1);
				File file2 = new File("conversations/" + filePath2);
				file1.createNewFile();
				file2.createNewFile();
				PrintWriter writer = new PrintWriter(new FileWriter(file1));
				PrintWriter writer2 = new PrintWriter(new FileWriter(file2));
				key = EncryptionService.generateKey();
				writer.write(key);
				writer2.write(key);
				System.out.println("Key Written to file : " + key);
				writer.close();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return key;
	}
	
	public boolean passwordReset(ConnectedClient c, String message) {
		return false;
	}
	
	/**
	 * Function used to get the index of a client in the currently connected clients ArrayList.
	 * @param c = Client which index should be found.
	 * @return index of specified client.
	 */
	private int findClient(ConnectedClient c) {
		for (int i=0; i<server.connections.size(); i++) {
			if (c.equals(server.connections.get(i))) return i;
		}
		return 0;
	}
}
