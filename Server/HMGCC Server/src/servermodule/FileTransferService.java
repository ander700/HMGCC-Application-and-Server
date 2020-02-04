package servermodule;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import controller.Server;
import model.ConnectedClient;

public class FileTransferService {

	private Server server;
	
	public enum FileType {
		TXT, PNG, JPG;
	}
	
	public FileTransferService(Server server) {
		this.server = server;
	}
	
	public String[] requestFileTransfer(ConnectedClient c, String message) {
		String[] userDetails = message.split(" ");
		String transferType = userDetails[1];
		String fileName = userDetails[2];
		String[] ret_arr = new String[2]; 
		
		if (!this.checkExtension(fileName)) {
			ret_arr[0] = "0";
			ret_arr[1] = "File Type not Supported for file " + fileName + ".";
			return ret_arr;
		}
		
		if (c.getUser() == null) {
			ret_arr[0] = "0";
			ret_arr[1] = "Not Logged In";
			this.server.sendMessage("ERROR",c, false);
			return ret_arr;
		}
		
		if (transferType.toUpperCase().trim().equals("TO")) {
			if (fileName.endsWith(".txt")) {
				if(this.recieveTXTFrom(c, fileName)) {
					ret_arr[0] = "1";
					ret_arr[1] = "File " + fileName + " Successfully Transffered to Server.";
					return ret_arr;
				} else {
					ret_arr[0] = "0";
					ret_arr[1] = "File " + fileName + " Failed to be Transffered to the Server.";
					return ret_arr;
				}
			} else if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
				if (this.recieveIMGFrom(c, fileName)) {
					ret_arr[0] = "1";
					ret_arr[1] = "File " + fileName + " Successfully Transffered to Server.";
					return ret_arr;
				} else {
					ret_arr[0] = "0";
					ret_arr[1] = "File " + fileName + " Failed to be Transffered to the Server.";
					return ret_arr;
				}
			}
		}
		else if (transferType.toUpperCase().trim().equals("FROM")) this.transferTo(c, fileName);
		else {
			ret_arr[0] = "0";
			ret_arr[1] = "Unsupported Transfer Type " + transferType + ".";
			return ret_arr;
		}
		
		return ret_arr;
	}
	
	private boolean checkExtension(String file) {
		for (FileType ext : FileType.values()) {
			String extension = ext.toString();
			if (file.toUpperCase().endsWith("." + extension)) {
				System.out.println("File of type " + extension + ".");
				return true;
			}
		}
		return false;
	}
	
	private boolean transferTo(ConnectedClient c, String filePath) {
		try {
			File file = new File("logo.png");
			byte[] bytes = Files.readAllBytes(file.toPath());
			this.server.sendMessage(bytes.toString(), c, true);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean recieveIMGFrom(ConnectedClient c, String filePath) {
		try {			
			File file = new File(filePath);
			String extension = this.getExtension(filePath);

			if (!this.overwriteFileConfirmation(file, c)) return false;
			
			System.out.println("Image Transfer Started");
			
	        byte[] sizeAr = new byte[4];
	        c.in.read(sizeAr);
	        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

	        byte[] imageAr = new byte[size];
	        c.in.readFully(imageAr);
	        for (int i=0; i<imageAr.length; i++) {
	        	System.out.print(imageAr[i]);
	        }

	        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

	        System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
	        ImageIO.write(image, extension, file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}
	
	private boolean recieveTXTFrom(ConnectedClient c, String filePath) {
		
		File file = new File(filePath);
		if (!this.overwriteFileConfirmation(file, c)) return false;
		
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file, true));
			String fileContents = c.in.readObject().toString();
			while (!fileContents.equals("END")) {
				writer.println(fileContents);
				fileContents = c.in.readObject().toString();
			}
			writer.close();
			System.out.println("File Recieved");
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String getExtension(String filePath) {
		for (FileType ext : FileType.values()) {
			if (filePath.toUpperCase().endsWith(ext.toString())) return ext.toString().toLowerCase();
		}
		return null;
	}
	
	private boolean overwriteFileConfirmation(File file, ConnectedClient c) {
		try {
			if (file.exists()) {
				server.sendMessage("FILE EXISTS", c, true);
				String userConf = c.in.readObject().toString();
				System.out.println("Recieved User Confirmation of " + userConf + ".");
				if (!userConf.toUpperCase().startsWith("Y")) return false;
				if (!file.delete()) return false;
				System.out.println("Deleted File");
				if (!file.createNewFile()) return false;
				server.sendMessage("Start Sending", c, true);
				return true;
			} else {
				if (!file.createNewFile()) return false;
				server.sendMessage("Continue", c, true);
				return true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
