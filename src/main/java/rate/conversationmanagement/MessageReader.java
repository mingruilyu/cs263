package rate.conversationmanagement;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import rate.conversationmanagement.Message;
public class MessageReader {
		static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
