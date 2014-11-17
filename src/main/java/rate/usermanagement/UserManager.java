package rate.usermanagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.gson.stream.JsonReader;

public class UserManager {
	public UserInfo user = null;
	static public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public UserManager(Entity userEntity) throws EntityNotFoundException {
			this.user = new UserInfo();
			if (userEntity.getProperty("gender") == null)
				System.out.println("null");
			//this.user.setGender();
			this.user.setName((String)userEntity.getKey().getName());
			this.user.setLocation((String)userEntity.getProperty("location"));
			this.user.setEmail((String)userEntity.getProperty("email"));
			this.user.setBirthDate((String)userEntity.getProperty("birthdate"));
			//if (userEntity.getProperty("image") != null)
			this.user.setProfileImage((String)userEntity.getProperty("image"));
			//this.user.setRatingPermission(((Boolean)userEntity.getProperty("ratingpermission")).booleanValue());
			// get the rate information from the datastore
			//Key rateKey = KeyFactory.createKey("ratestat", this.user.getName());
			Key rateKey = new KeyFactory.Builder("user", this.user.getName())
										.addChild("ratestat", this.user.getName()).getKey();
			Entity rateStatEntity = datastore.get(rateKey);
			this.user.setRate((Long)rateStatEntity.getProperty("rate"));
	}
	
	public static UserManager getUserHandler(String username) throws EntityNotFoundException{
		Key userKey = KeyFactory.createKey("user", username);
		Entity userEntity = datastore.get(userKey);
		return new UserManager(userEntity);
	}
	
	public static boolean registerUser(Map<String, String> attrMap) {
		String password, email, birthdate, location, userName, gender, image;
		Entity userEntity;
		if (attrMap == null) return false;

		if ((userName = attrMap.get("username"))!= null)
			userEntity = new Entity("user",userName);
		else return false;
		
		// check whether the user has existed in the datastore
		Key userKey = KeyFactory.createKey("user", userName);
		try {
			datastore.get(userKey);
			return true;
		}
		catch(EntityNotFoundException ex) {
			if ((image = attrMap.get("image"))!= null) 
				userEntity.setProperty("image", image);
			if ((password = attrMap.get("password"))!= null) 
				userEntity.setProperty("password", password);
			if ((email = attrMap.get("email"))!= null)
				userEntity.setProperty("email", email);
			if ((gender = attrMap.get("gender"))!= null) {
				if (gender.compareTo("male") == 0)
					userEntity.setProperty("gender", true);
				else userEntity.setProperty("gender", false);
			}
			if ((birthdate = attrMap.get("birthdate"))!= null)
				userEntity.setProperty("birthdate", birthdate);
			if ((location = attrMap.get("location"))!= null)
				userEntity.setProperty("location", location);
			datastore.put(userEntity);
			// initialize the rating statistics upon register
			//Key rateKey = KeyFactory.createKey("user", userName);
			Entity userRatingStatEntity = new Entity("ratestat", userName, userKey);
			userRatingStatEntity.setProperty("rate", 0);
			userRatingStatEntity.setProperty("ratecount", 0);
			datastore.put(userRatingStatEntity);
		}
		return true;
	}
	
	public List<String> getConversationList() {
		// return conversation ID
	    List<String> conversationList = new LinkedList<String>();
	    Key userConversationKey = KeyFactory.createKey("user", user.getName());
	    Query query = new Query("conversationlist", userConversationKey).addSort("date", Query.SortDirection.DESCENDING);
	    for (Entity entity : datastore.prepare(query).asIterable()) {
	    	String conversationId = (String) entity.getProperty("threadid");
	    	conversationList.add(conversationId);
	    }
	    return conversationList;
	}

}
