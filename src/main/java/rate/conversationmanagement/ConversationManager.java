package rate.conversationmanagement;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.gson.stream.JsonReader;

@Path("/conversation")
public class ConversationManager {
	public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final static long HIGH_SCORE = 80;
	final static int TOKEN_LENGTH = 10;
	@Context HttpServletRequest request;
	
	public static String getSender(String token, String receiver) throws EntityNotFoundException {
		Key tokenKey = KeyFactory.createKey("conversationthread", token);
		Entity conversationEntity = datastore.get(tokenKey);
		String talker1 = (String)conversationEntity.getProperty("talker1");
		String talker2 = (String)conversationEntity.getProperty("talker2");
		if (receiver.equals(talker1)) return talker2;
		else return talker1;
	}
	
	@GET
	@Path("/add/{token}")
	public Response initializeConversation(@PathParam("token") String token) throws EntityNotFoundException {
		// this means the ratee has activated the conversation, mark the ratee's conversation thread as activated
		Key tokenKey = KeyFactory.createKey("conversationthread", token);
		Entity conversationEntity = datastore.get(tokenKey);
		if ((Boolean)conversationEntity.getProperty("activated"))
			return Response.seeOther(URI.create("/login.jsp")).build();
		conversationEntity.setProperty("activated", true);
		String talker1 = (String)conversationEntity.getProperty("talker1");
		String talker2 = (String)conversationEntity.getProperty("talker2");
		datastore.put(conversationEntity);
		
		// add two talker's conversation list
		Key talker1ConversationListKey = KeyFactory.createKey("user", talker1);
		Key talker2ConversationListKey = KeyFactory.createKey("user", talker2);
		Entity talker1ConversationListEntity = new Entity("conversationlist", talker2, talker1ConversationListKey);
		Entity talker2ConversationListEntity = new Entity("conversationlist", talker1, talker2ConversationListKey);
		talker1ConversationListEntity.setProperty("threadid", token);
		talker1ConversationListEntity.setProperty("date", new Date());
		talker2ConversationListEntity.setProperty("threadid", token);
		talker2ConversationListEntity.setProperty("date", new Date());
		datastore.put(talker1ConversationListEntity);
		datastore.put(talker2ConversationListEntity);
		
		//send system warning message to each other 
		String systemWarning = "You can start talking with ";
		Key conversationThreadKey = KeyFactory.createKey("threadid", token);
		Entity message1Entity = new Entity("conversation", conversationThreadKey);
		message1Entity.setProperty("sender", "admin");
		message1Entity.setProperty("receiver", talker2);
		message1Entity.setProperty("date", new Date());
		message1Entity.setProperty("content", systemWarning + talker1);
		
		Entity message2Entity = new Entity("conversation", conversationThreadKey);
		message2Entity.setProperty("sender", "admin");
		message2Entity.setProperty("receiver", talker1);
		message2Entity.setProperty("date", new Date());
		message2Entity.setProperty("content", systemWarning + talker2);
		
		datastore.put(message1Entity);
		datastore.put(message2Entity);
		return Response.seeOther(URI.create("/login.jsp")).build();
	}

	static public String generateConversationToken() {
		StringBuilder token = new StringBuilder();
		int digit;
		for (int i = 0; i < TOKEN_LENGTH; i ++) {
			digit = (int) (Math.random() * 10);
			token.append(String.valueOf(digit));
		}
		return token.toString();
	}
	
	@Path("/check")
	@POST
	public void conversationCheck() throws EntityNotFoundException {
		String ratee = request.getParameter("ratee");
		String rater = request.getParameter("rater");
		long rate = Long.parseLong(request.getParameter("rate"));
		
		//Get the ratee's email
		Key rateeKey = KeyFactory.createKey("user", ratee);
		Entity rateeEntity = datastore.get(rateeKey);
		String email = (String)rateeEntity.getProperty("email");
		
		//Try to get the ratee's rate history, see if ratee has also rated the rater
		Key rateeHistoryKey = new KeyFactory.Builder("user", ratee)
											.addChild("ratehistory", rater)
											.getKey();
		try {
			// the ratee should get the activation email
			Entity rateeHistoryEntity = datastore.get(rateeHistoryKey);
			long rateerate = (Long)rateeHistoryEntity.getProperty("rate");
			if (rateerate >= HIGH_SCORE && rate >= HIGH_SCORE) {	// we found a match
				// Generated conversation token, add to the conversationthread
				String conversationToken = generateConversationToken();
				Entity conversationThreadEntity = new Entity("conversationthread", conversationToken);
				conversationThreadEntity.setProperty("activated", false);
				conversationThreadEntity.setProperty("talker1", rater);
				conversationThreadEntity.setProperty("talker2", ratee);
				datastore.put(conversationThreadEntity);
				
				// send the ratee activation email
				Queue taskqueue = QueueFactory.getDefaultQueue();
		        taskqueue.add(withUrl("/rating/messenger/activation")
		        			  .param("email", email)
		        			  .param("receiver", ratee)
		        			  .param("token", conversationToken));
			}
		}
		catch(EntityNotFoundException ex) {
			// the ratee should get the email alert
			Queue taskqueue = QueueFactory.getDefaultQueue();
	        taskqueue.add(withUrl("/rating/messenger/alert")
	        			  .param("email", email)
	        			  .param("receiver", ratee));
		}
	}
		
	@Path("/send/{token}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessage(String jsonstring, @PathParam("token") String conversationToken) 
			throws IOException, EntityNotFoundException{
		Key conversationThreadKey = KeyFactory.createKey("threadid", conversationToken);
		Entity newMessageEntity = new Entity("conversation", conversationThreadKey);
		
		// get the json message
		JsonReader reader = new JsonReader(new StringReader(jsonstring));
		reader.beginObject();
		String nametoken;
		while (reader.hasNext()) {
			nametoken = reader.nextName();
			if (nametoken.compareTo("content") == 0)
				newMessageEntity.setProperty("content", reader.nextString());
			else reader.nextString();
		}
	    reader.close();
	    
	    // get the receiver and sender
		Key conversationKey = KeyFactory.createKey("conversationthread", conversationToken);
		Entity conversationEntity = datastore.get(conversationKey);
		String talker1 = (String) conversationEntity.getProperty("talker1");
		String talker2 = (String) conversationEntity.getProperty("talker2");
		String sender = (String)request.getSession().getAttribute("user");
		String receiver;
		if (sender.equals(talker1))
			receiver = talker2;
		else receiver = talker1;
		newMessageEntity.setProperty("sender", sender);
		newMessageEntity.setProperty("receiver", receiver);
	    newMessageEntity.setProperty("date", new Date());
	    datastore.put(newMessageEntity);
	    
	    return Response.seeOther(URI.create("/conversation.jsp?id=" + conversationToken)).build();
	}
}
