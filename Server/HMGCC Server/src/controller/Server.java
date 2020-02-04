package controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import GUIWidgets.LogContainer;
import javafx.application.Platform;
import model.ConnectedClient;
import servermodule.AccountService;
import servermodule.EncryptionService;
import servermodule.FileTransferService;
import servermodule.PasswordService;

/**
 * Class used to handle server connections, disconnects and receiving of requests.
 * @author Oliver Bowker
 * @version 1.0
 */
public class Server {
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ServerSocket sock;
	
	public ArrayList<ConnectedClient> connections = new ArrayList<ConnectedClient>();
	
	private AccountService accountService;
	private FileTransferService fileTransferService;
	private PasswordService passwordService;
	
	public LogContainer logs;
	
	/**
	 * Enum to contain all kinds of requests user can make to server, except connection and
	 * disconnection requests.
	 */
	public enum RequestType {
		LOGIN, SIGNUP, VERIFY, FILETRANSFER, PASSWORDCHECK, PASSRESET, SMSD, SMSE;
	}
	
	/**
	 * Constructor for Server class.
	 * @param logs = GUI widget where information about requests are displayed.
	 */
	public Server(String email, String password) {
		this.accountService = new AccountService(this, email, password);
		this.fileTransferService = new FileTransferService(this);
		this.passwordService = new PasswordService(this);
	}
	
	/**
	 * Function run when server first starts, a loop is done and threaded methods are used to handle all clients.
	 * @throws IOException - Any issues with disconnecting clients etc.
	 */
	public synchronized void startServer() throws IOException {
		File file = new File("conversations/");
		if (!file.exists()) file.mkdir();
		
		
		Thread t = new Thread("Main Server Thread") {
			public void run() {
				try {
					sock = new ServerSocket(8192, 100);
					while (true) {
						ConnectedClient client = new ConnectedClient(waitForConnection());
						connections.add(client);
						sendMessage(client.getKey(), client, false);
						whileConnected(client);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Server Closed");
				} finally {
					try {
						if (in != null) in.close();
						if (out != null) out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		};
		t.start();
	}
	
	/**
	 * Function used to wait for a user to connect.
	 * @return a new Socket object for the Server to communicate with the newly connected client.
	 */
	private Socket waitForConnection() {
		try {
			System.out.println("Waiting for Connection...");
			Socket conn = this.sock.accept();
			System.out.println("User connected from " + conn.getInetAddress().getHostName());
			return conn;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Function used to receive message from the client, as well as log the client in to their account and disconnect the client..
	 * @param c = The client that this thread is serving.
	 */
	private synchronized void whileConnected(ConnectedClient c) {
		Thread t = new Thread("Client Thread") {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						logs.update(c.sock.getInetAddress().getHostName() + " : " + c.sock.getInetAddress().getHostAddress() + " connected.");						
					}
				});
				
				do {
					try {
						String mes = c.in.readObject().toString();
						System.out.println(mes);
						String message = c.encrypt.decrypt(mes);
						System.out.println(message);
						@SuppressWarnings("unused")
						// Outcome holds type of request made as well as if the requests executed 
						// successfully, may be expanded to hold more data in the future.
						String[] outcome = parseMessage(c, message);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				} while(true);
				
				handleDisconnect(c);
				
			}
		};
		t.start();
	}
	
	/**
	 * Function used to parse the users message and carry out the correct service.
	 * @param c = Client that sent the message/request.
	 * @param message = Message/request the client sent to the server.
	 * @return an array of data about the request.
	 */
	private String[] parseMessage(ConnectedClient c, String message) {
		String[] ret_arr = new String[3];
		try {
			System.out.println("Decrypted Message : " + message);
			String[] details;
			String opType = message.split(" ")[0].toUpperCase();
	
			
			switch(opType) {
				case "LOGIN":
					ret_arr[0] = Integer.toString(RequestType.LOGIN.ordinal());
					details = this.accountService.login(c, message);
					ret_arr[1] = details[0];
					ret_arr[2] = details[1];
					break;
				case "SIGNUP":
					ret_arr[0] = Integer.toString(RequestType.SIGNUP.ordinal());
					ret_arr[1] = (this.accountService.signup(c, message)) ? "1" : "0";
					break;
				case "VERIFY":
					ret_arr[0] = Integer.toString(RequestType.VERIFY.ordinal());
					ret_arr[1] = (this.accountService.verifySignup(c, message)) ? "1" : "0";
					break;
				case "FILETRANSFER":
					ret_arr[0] = Integer.toString(RequestType.FILETRANSFER.ordinal());
					details = this.fileTransferService.requestFileTransfer(c, message);
					ret_arr[1] = details[0];
					ret_arr[2] = details[1];
					break;
				case "PASSWORDCHECK":
					this.passwordService.checkPasswordStrength(c, message.split(" ")[1]);
				case "PASSRESET":
					ret_arr[0] = Integer.toString(RequestType.PASSRESET.ordinal());
					ret_arr[1] = (this.accountService.passwordReset(c, message)) ? "1" : "0";
					break;
				case "SMSD":
					ret_arr[0] = Integer.toString(RequestType.SMSD.ordinal());
					ret_arr[1] = (this.accountService.decryptSMS(c, message)) ? "1" : "0";
					break;
				case "SMSE":
					ret_arr[0] = Integer.toString(RequestType.SMSE.ordinal());
					ret_arr[1] = (this.accountService.encryptSMS(c, message)) ? "1" : "0";
					break;
				default:
					Platform.runLater(new Runnable() {
						public void run() {
							logs.update("Unrecognised Command " + opType + ".");
						}
					});
					return null;
			}
			
			Platform.runLater(new Runnable() {
				public void run() {
					logs.update(c, ret_arr);				
				}
			});
		} catch (Exception e) {
			//Nothing
		}
		return ret_arr;
	}
	
	/**
	 * Function used to send messages back to connected users.
	 * @param message = The message that will be sent to the client.
	 * @param c = Client to send the message to.
	 */
	public synchronized void sendMessage(String message, ConnectedClient c, boolean encrypt) {
		Thread t = new Thread("Server Send Thread") {
			public void run() {
				try {
					if (encrypt) c.out.writeObject(c.encrypt.encrypt(message));
					else c.out.writeObject(message);
					c.out.flush();
					System.out.println("SERVER - " + message);
				} catch (IOException e) {
					// Client has Disconnected.
					handleDisconnect(c);
				}				
			}
		};
		t.start();
	}
	
	/**
	 * Function that handles a client disconnecting from the server.
	 * @param c = Client that has disconnected.
	 * @throws IOException - Caused when there is an error closing resources.
	 */
	public void handleDisconnect(ConnectedClient c) {
		try {
			c.in.close();
			c.out.close();
			c.sock.close();
		} catch (IOException e) {
			// Thrown when stream/socket is all ready closed.
		} finally {
			this.connections.remove(c);
			Platform.runLater(new Runnable() {
				public void run() {
					logs.update(c.sock.getInetAddress().getHostName() + " : " + c.sock.getInetAddress().getHostAddress() + " disconnected.");					
				}
			});
			System.out.println("Client at : " + c.sock.getInetAddress().getHostAddress() + " disconnected.");
		}
	}
}
