package ratingapp.usermanagement;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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

public class UserManager {
	public UserInfo user = null;
	static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	UserManager(Entity userEntity) {
			this.user = new UserInfo();
			//System.out.println(userEntity);
			if (userEntity.getProperty("gender") == null)
				System.out.println("null");
			//this.user.setGender();
			this.user.setName((String)userEntity.getProperty("name"));
			this.user.setLocation((String)userEntity.getProperty("location"));
			this.user.setEmail((String)userEntity.getProperty("email"));
			this.user.setBirthDate((String)userEntity.getProperty("birthdate"));
			//this.user.setRate((Integer)userEntity.getProperty("rate"));
			//this.user.setRatingPermission(((Boolean)userEntity.getProperty("ratingpermission")).booleanValue());
	}
	
	public static UserManager getUserHandler(String username) throws EntityNotFoundException{
		Key userKey = KeyFactory.createKey("user", username);
		Entity userEntity = datastore.get(userKey);
		return new UserManager(userEntity);
	}
	
	public void updateUserInfo(UserInfo updatedInfo) {
		if (updatedInfo == null) return;
		this.user.setGender(updatedInfo.getGender() == "male" ? true : false);
		this.user.setName(updatedInfo.getName());
		this.user.setLocation(updatedInfo.getLocation());
		this.user.setEmail(updatedInfo.getEmail());
		this.user.setBirthDate(updatedInfo.getBirthDate());
		this.user.setRate(updatedInfo.getRate());
		this.user.setRatingPermission(updatedInfo.isRatingAllowed());
	}
	
	public static boolean registerUser(Map<String, String> attrMap) {
		String password, email, birthdate, location, userName, gender;
		Entity userEntity;
		if (attrMap == null) return false;
		if ((userName = attrMap.get("username"))!= null)
			userEntity = new Entity("user",userName);
		else return false;
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
		return true;
	}
	
	public List<String> getConversationList() {
		// return conversation ID
	     List<String> IDList = new LinkedList<String>();
	    Filter clientFilter_1 = new FilterPredicate("client_1", 
	    										  	FilterOperator.EQUAL, 
	    										  	user.getName());
	    Filter clientFilter_2 = new FilterPredicate("client_2",
	    											FilterOperator.EQUAL,
	    											user.getName());
	    CompositeFilter clientFilter = CompositeFilterOperator.and(clientFilter_1, clientFilter_2);
	    Query query = new Query("conversationlist").setFilter(clientFilter).addSort("date", Query.SortDirection.DESCENDING);
	    for (Entity entity : datastore.prepare(query).asIterable()) {
	    	String ID = (String) entity.getProperty("id");
	    	IDList.add(ID);
	    }
	    return IDList;
	}
}
