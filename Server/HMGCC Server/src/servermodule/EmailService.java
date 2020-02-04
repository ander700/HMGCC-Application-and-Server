package servermodule;

import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import controller.TokenDAO;
import model.Token;
import model.User;

/**
 * Class used to send verification email's to users, as well as handling any other email related tasks.
 * @author Oliver Bowker
 * @version 1.0
 */
public class EmailService {

	private String password;
	private String passHash = "8246725E2E398AEFBBE1899448F32373848D5C7D8C02301F2D415B01F08444E1A1DAC8994288C776A9836480AC78AF1CBA3563E3122D706351FE66AC091E43A8";
	
	public EmailService(String password) {
		this.password = password;
	}
	
	/**
	 * Function used to send a verification code to the user.
	 * @param user = User to send email to.
	 * @return whether or not the email was sent successfully.
	 */
	public boolean sendSignupVerification(User user)  {
		TokenDAO dao = new TokenDAO();
		Token token = new Token(user.getEmail(), (int) System.currentTimeMillis());
		
		if (!AccountService.hashPassword(password).toUpperCase().equals(this.passHash)) {
			System.out.println("Password Hashes - " + AccountService.hashPassword(password) + " : " + this.passHash);
			return false;
		}
		
		try {
			// If a token already exists for user, remove old token and add a new one.
			if (!this.checkTokenLimit(user.getEmail())) dao.addNewToken(token);
			else {
				dao.removeToken(user.getEmail());
				dao.addNewToken(token);
			}
		} catch (SQLException e1) {
			// Already got token.
			return false;
		}
		
		final String password = this.password;
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		
		Session session = Session.getInstance(props,
				new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("hmgccproject2018@gmail.com", password);
					}
				});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("hmgccproject2018@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, 
					InternetAddress.parse(user.getEmail()));
			message.setSubject("Verification Code");
			message.setContent("<div style=\"background-color:#800080;width:300px;height:200px;border-radius:18px;color:white;padding:5px 10px;\">"
								+ "<p style=\"width:100%;border-bottom:1px solid #ffccff;font-size:22px;\">Hey HMGCC your Authentication Code is :</p>"
								+ "<p style=\"font-size:38px;\">" + token.getTokenID() + "</p>"
							+ "</div>", "text/html; charset=utf-8");
			Transport.send(message);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Function used to check of there is already a token in the database for a specified email.
	 * @param email = Address to check for duplicate records in database. 
	 * @return whether or not there is already a Token for the specified email.
	 */
	private boolean checkTokenLimit(String email) {
		try {
			TokenDAO tdao = new TokenDAO();
			Token t = tdao.getToken(email);
			if (t != null) return true; // If token exists.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
