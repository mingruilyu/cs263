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
/**
 * This class handles all email sending requests.
 * @author Mingrui Lyu
 * @version 1.0
 *
 */
@Path("/messenger")
public class Messenger {
	final static String ADD_CONVERSATION_LINK = "http://lyumingrui1.appspot.com/conversation/conversation/add";

	@Context HttpServletRequest request;
	@POST
	@Path("/alert")
	/**
	 * This method is a REST POST api. It is responsible for sending out a warning email
	 * to the user when some one has rated him over 80
	 * @throws UnsupportedEncodingException if the mail address is encoded in the wrong way
	 * @throws MessagingException if the message is not successfully sent
	 */
	public void sendAlertEmail() throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String receiver = request.getParameter("receiver");
		String email = request.getParameter("email");
		String msgBody = "Hello," + receiver + "\n"
					   + "You just got someone who wants to date with you!\n"
					   + "Start rating to find out who is that!"; 
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("davidlvmingrui@gmail.com", "LET'S DATE"));
		msg.addRecipient(Message.RecipientType.TO,
						 new InternetAddress(email, receiver));
		msg.setSubject("ATTENTION! SOMEONE WANTS TO DATE WITH YOU!");
		msg.setText(msgBody);
		Transport.send(msg);
	}
	
	@POST
	@Path("/activation") 
	/**
	 * This method is a REST POST api. It is responsible for sending out a warning email
	 * to the user when someone he rated before also rated him over 80 
	 * @throws UnsupportedEncodingException if the mail address is encoded in the wrong way
	 * @throws MessagingException if the message is not successfully sent
	 */
	public void sendActicationEmail() throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		String receiver = request.getParameter("receiver");
		String email = request.getParameter("email");
		String token = request.getParameter("token");
		String msgBody = "Hello," + receiver + "\n"
					   + "You just got someone who is also interested in you!\n"
					   + "Use the following link to activate the conversation!\n"
					   + ADD_CONVERSATION_LINK + "/" + token;
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("davidlvmingrui@gmail.com", "LET'S DATE"));
		msg.addRecipient(Message.RecipientType.TO,
						 new InternetAddress(email, receiver));
		msg.setSubject("ATTENTION! YOU HAVE GOT A DATE!");
		msg.setText(msgBody);
		Transport.send(msg);
	}
}
