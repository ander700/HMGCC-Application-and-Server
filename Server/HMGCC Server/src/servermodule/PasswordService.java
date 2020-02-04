package servermodule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import controller.Server;
import model.ConnectedClient;

public class PasswordService {

	private Server server;
	
	public PasswordService(Server server) {
		this.server = server;
	}
	
	public boolean checkPasswordStrength(ConnectedClient c, String password) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File("rockyou.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equals(password)) {
					System.err.println("Password " + password + " Not Secure Enough");
					reader.close();
					return false;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
