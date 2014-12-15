package rate.ratingmanagement;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
@Path("/messenger")
public class Messenger {
	final static String ADD_CONVERSATION_LINK = "http://lyumingrui1.appspot.com/conversation/conversation/add";
	@Context HttpServletRequest request;
	@POST
	@Path("/alert") 
	public void sendAlertEmail() throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String receiver = request.getParameter("receiver");
		String email = request.getParameter("email");
		//String email = "davidlvmingrui@gmail.com";
		String msgBody = "Hello," + receiver + "\n"
					   + "You just got someone who is interested in you!\n"
					   + "Start rating to find out who is that!"; 
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("davidlvmingrui@gmail.com", "LET'S DATE"));
		msg.addRecipient(Message.RecipientType.TO,
						 new InternetAddress(email, receiver));
		msg.setSubject("ATTENTION! SOMEONE IS WATCHING YOU!");
		msg.setText(msgBody);
		Transport.send(msg);
	}
	
	@POST
	@Path("/activation") 
	public void sendActicationEmail() throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String receiver = request.getParameter("receiver");
		String email = request.getParameter("email");
		//String email = "davidlvmingrui@gmail.com";
		String token = request.getParameter("token");
		String msgBody = "Hello," + receiver + "\n"
					   + "You just got someone who is also interested in you!\n"
					   + "copy the following link to start the conversation!" 
					   + ADD_CONVERSATION_LINK + "/" + token;
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("davidlvmingrui@gmail.com", "LET'S DATE"));
		msg.addRecipient(Message.RecipientType.TO,
						 new InternetAddress(email, receiver));
		msg.setSubject("ATTENTION! YOU HAVE GOT A MATCH!");
		msg.setText(msgBody);
		Transport.send(msg);
	}
}
