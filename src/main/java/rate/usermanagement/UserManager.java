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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.stream.JsonReader;

public class UserManager {
	public UserInfo user = null;
	static public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	static MemcacheService memCache = MemcacheServiceFactory.getMemcacheService();
	public UserManager(Entity userEntity) throws EntityNotFoundException {
			this.user = new UserInfo();
			if (userEntity.getProperty("gender") == null)
				System.out.println("null");
			this.user.setName((String)userEntity.getKey().getName());

			String username = userEntity.getKey().getName();
				this.user.setEmail((String)userEntity.getProperty("email"));
				memCache.put(username + "email", (String)userEntity.getProperty("email"));
				this.user.setBirthDate((String)userEntity.getProperty("birthdate"));
				memCache.put(username + "birthdate", (String)userEntity.getProperty("birthdate"));
				this.user.setProfileImage((String)userEntity.getProperty("image"));
				memCache.put(username + "image", (String)userEntity.getProperty("image"));
				this.user.setHobby((String)userEntity.getProperty("hobby"));
				memCache.put(username + "hobby", (String)userEntity.getProperty("hobby"));
				this.user.setClub((String)userEntity.getProperty("club"));
				memCache.put(username + "club", (String)userEntity.getProperty("club"));
				this.user.setOccupation((String)userEntity.getProperty("occupation"));
				memCache.put(username + "occupation", (String)userEntity.getProperty("occupation"));
				this.user.setMotto((String)userEntity.getProperty("motto"));
				memCache.put(username + "motto", (String)userEntity.getProperty("motto"));
				this.user.setSchool((String)userEntity.getProperty("school"));
				memCache.put(username + "school", (String)userEntity.getProperty("school"));
			// get the rate information from the datastore
			Key rateKey = new KeyFactory.Builder("user", this.user.getName())
										.addChild("ratestat", this.user.getName()).getKey();
			Entity rateStatEntity = datastore.get(rateKey);
			this.user.setRate((Long)rateStatEntity.getProperty("rate"));
			memCache.put(username + "rate", (Long)rateStatEntity.getProperty("rate"));
	}
	
	public static UserManager getUserHandler(String username) throws EntityNotFoundException{
		Key userKey = KeyFactory.createKey("user", username);
		Entity userEntity = datastore.get(userKey);
		// deposit the logged in user info to the mem cache
		memCache.put(username + "gender", (Boolean)userEntity.getProperty("gender"));
		return new UserManager(userEntity);
	}
	
	public static boolean infoCompletionCheck(UserInfo user) {
		// true if everything is completed
		return (user.getBirthDate() != null && user.getClub() != null
				&& user.getEmail() != null && user.getMotto() != null
				&& user.getOccupation() != null && user.getProfileImage() != null
				&& user.getSchool() != null);
	}
	
	public static UserInfo getCachedUserInfo(String username) throws EntityNotFoundException {
		UserInfo user = new UserInfo();
		user.setName(username);
		String location, email, image, birthdate, school, motto, club, hobby, occupation;
		Boolean gender;
		Long rate;
		if((gender = (Boolean)memCache.get(username + "gender") != null))
			user.setGender(gender);
		else return getUserHandler(username).user;
		if((email = (String)memCache.get(username + "email")) != null)
			user.setEmail(email);
		//else return getUserHandler(username).user;
		if ((image = (String)memCache.get(username + "image")) != null)
			user.setProfileImage(image);
		//else return getUserHandler(username).user;
		if ((rate = (Long)memCache.get(username + "rate")) != null)
			user.setRate((Long)rate);
		//else return getUserHandler(username).user;
		if ((birthdate = (String)memCache.get(username + "birthdate")) != null)
			user.setBirthDate(birthdate);
		//else return getUserHandler(username).user;
		if ((club = (String)memCache.get(username + "club")) != null)
			user.setClub(club);
		//else return getUserHandler(username).user;
		if ((hobby = (String)memCache.get(username + "hobby")) != null)
			user.setHobby(hobby);
		//else return getUserHandler(username).user;
		if ((occupation = (String)memCache.get(username + "occupation")) != null)
			user.setOccupation(occupation);
		//else return getUserHandler(username).user;
		if ((motto = (String)memCache.get(username + "motto")) != null)
			user.setMotto(motto);
		//else return getUserHandler(username).user;
		if ((school = (String)memCache.get(username + "school")) != null)
			user.setSchool(school);
		//else return getUserHandler(username).user;
		return user;
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
	
	public static List<String> getConversationList(String user) {
		// return conversation ID
	    List<String> conversationList = new LinkedList<String>();
	    Key userConversationKey = KeyFactory.createKey("user", user);
	    Query query = new Query("conversationlist", userConversationKey).addSort("date", Query.SortDirection.DESCENDING);
	    for (Entity entity : datastore.prepare(query).asIterable()) {
	    	String conversationId = (String) entity.getProperty("threadid");
	    	conversationList.add(conversationId);
	    }
	    return conversationList;
	}
	
	public static String getLocation(String username) throws EntityNotFoundException {
		return (String)memCache.get(username + "city");
	}
}
