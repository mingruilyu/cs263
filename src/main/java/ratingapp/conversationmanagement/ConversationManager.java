package ratingapp.conversationmanagement;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/conversation")
public class ConversationManager {
	@POST
	@Path("/messenger") 
	public void sendEmail() throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String msgBody = "Hello";

		System.out.println("about to send an email");
		/*try {*/
		Message msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress("ratingapp@gmail.com", "admin"));
		    msg.addRecipient(Message.RecipientType.TO,
		    				 new InternetAddress("davidlvmingrui@gmail.com", "Mr. User"));
		    msg.setSubject("Your Example.com account has been activated");
		    msg.setText(msgBody);
		    Transport.send(msg);
			System.out.println("Sent an email");
/*
		} catch (AddressException e) {
		    // ...
		} catch (MessagingException e) {
		    // ...
		}*/
	} 
}
