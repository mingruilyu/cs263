package rate.conversationmanagement;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.gson.stream.JsonReader;
/**
 * This class manages operations of conversations between two users
 * @author Mingrui Lyu
 * @version 1.0
 */
@Path("/conversation")
public class ConversationManager {
	public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final static long HIGH_SCORE = 80;
	final static int TOKEN_LENGTH = 10;
	@Context HttpServletRequest request;
	/**
	 * This method get one of the speaker in a conversation thread. This speaker is
	 * different from the the speaker specified by the input parameter "receiver".
	 * In order to do this, table "conversationthread" is queried to get the two speakers
	 * that involve in the conversation specified by "token", the conversation id.
	 * One of the two speaker that is different from the "receiver".
	 * @param token the unique id of the conversation.
	 * @param receiver one of the speaker of the conversation
	 * @return the other speaker who is different from the "receiver"
	 * @throws EntityNotFoundException if the corresponding conversation thread is not found.
	 */
	public static String getSender(String token, String receiver) throws EntityNotFoundException {
		Key tokenKey = KeyFactory.createKey("conversationthread", token);
		Entity conversationEntity = datastore.get(tokenKey);
		String talker1 = (String)conversationEntity.getProperty("talker1");
		String talker2 = (String)conversationEntity.getProperty("talker2");
		if (receiver.equals(talker1)) return talker2;
		else return talker1;
	}
	/**
	 * This method is a REST GET API. It initializes a conversation between two speakers that rate
	 * each other with high score. 
	 * 1.	It first checks the "conversationthread" table. If the conversation
	 * specified by the token already exist in the table and it has a field "activated" that is true,
	 * it means the conversation thread has already been initialized. Otherwise, put the name of two
	 * speakers to the conversationthread's corresponding fields. 
	 * 
	 * 2.	It then adds the conversation to the conversation list of each speaker. The conversation
	 * list is contained in "conversationlist" table.
	 * 
	 * 3.	At last, it adds the first message sent by the system to the corresponding conversation
	 * thread. All message of the same conversation thread have the same parent key "threadid" and 
	 * are stored in the "conversation" table.
	 * 
	 * @param token the unique id of a conversation
	 * @return a redirect response that causes the previous page jumps to the login page.
	 * @throws EntityNotFoundException if any of the keys is not found in the corresponding table
	 */
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
	/**
	 * This method generate a 10-digit random conversation id.
	 * @return a string of the conversation id
	 */
	static public String generateConversationToken() {
		StringBuilder token = new StringBuilder();
		int digit;
		for (int i = 0; i < TOKEN_LENGTH; i ++) {
			digit = (int) (Math.random() * 10);
			token.append(String.valueOf(digit));
		}
		return token.toString();
	}
	/**
	 * This method is a REST POST API. It does the following thins in sequence:
	 * 1.	it checks whether the new rate is over 80. If it does not, do nothing.
	 * 2.	it checks if the current ratee has already rated the current rater before
	 * with a high score. If it does not, then put a task into the task queue
	 * to warn the current ratee that someone wants to date with him. If it does, then
	 * put a task into the task queue to send the ratee an activation link.
	 * @throws EntityNotFoundException if the rate history of the ratee is not found.
	 */
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
		
		if (rate < HIGH_SCORE) return; // if the rate is less than high score, do nothing
		try {
			//Try to get the ratee's rate history, see if ratee has also rated the rater
			Key rateeHistoryKey = new KeyFactory.Builder("user", ratee)
												.addChild("ratehistory", rater)
												.getKey();
			// the ratee should get the activation email
			Entity rateeHistoryEntity = datastore.get(rateeHistoryKey);
			long rateerate = (Long)rateeHistoryEntity.getProperty("rate");
			if (rateerate >= HIGH_SCORE) {	// we found a match
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
			// this means that the ratee has not rated the rater
			// the ratee should get the email alert
			Queue taskqueue = QueueFactory.getDefaultQueue();
	        taskqueue.add(withUrl("/rating/messenger/alert")
	        			  .param("email", email)
	        			  .param("receiver", ratee));
		}
	}
	/**
	 * This method is a REST POST API. It adds a new message to the conversation.
	 * 1.	it extracts the message body out of the Json string.
	 * 2.	Query the "conversationthread" table to get the name of two speakers,
	 * using conversation id.
	 * 3.	Fill in the message header with the sender and receiver and put the Message
	 * entity into the datastore.
	 * 
	 * @param jsonstring the Json string that contains the message body
	 * @param conversationToken the unique id of the conversation
	 * @return a redirect link that cause the previous page to jump to the conversation page
	 * @throws IOException if the JsonReader fails to read the Json string.
	 * @throws EntityNotFoundException if the conversation if not found in the "conversationthread" table
	 */
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
	/**
	 * This method get all the messages of a conversation thread.
	 * It queries the "conversation" table with the unique conversation
	 * id as the key.
	 * @param conversationId the unique conversation id
	 * @return a linked list of the Message objects.
	 */
	public static List<Message> getMessageList(String conversationId) { 
		List<Message> messageList = new LinkedList<Message>();
		Key conversationThreadKey = KeyFactory.createKey("threadid", conversationId);
	    Query query = new Query("conversation", conversationThreadKey).addSort("date", Query.SortDirection.ASCENDING);
	    for (Iterator<Entity> iterator = datastore.prepare(query).asIterator(); iterator.hasNext(); ) {
	    	Entity entity = iterator.next();
	    	Message message = new Message();
	    	message.setSender((String)entity.getProperty("sender"));
	    	message.setReceiver((String)entity.getProperty("receiver"));
	    	message.setDate((Date)entity.getProperty("date"));
	    	message.setBody((String)entity.getProperty("content"));
	    	messageList.add(message);
	    }
	    return messageList;
    }
}
