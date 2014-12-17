package rate.usermanagement;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
/**
 * This class contains static methods that operation on the registered user 
 * @author Mingrui Lyu
 * @version 1.0
 */
public class UserManager {
	/**
	 * this static field refers to the GAE datastore low-level api
	 */
	static public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	/**
	 * this static field refers to the GAE memcache low-level api
	 */
	static MemcacheService memCache = MemcacheServiceFactory.getMemcacheService();
	/**
	 * This method create a new UserInfo from the userEntity
	 * 1.	it fetches the user entity specified by the "username" from the datastore.
	 * 2.	it uses all parameters to initialize corresponding fields of the newly
	 * created UserInfo object.
	 * 3.	For each of the field of the UserInfo, it also put a copy into memcache 
	 * in form of (username + fieldname, value). So memcache will cache all the 
	 * user information that has already been referred to in the previous operations.
	 * @param username the name of the current user
	 * @return the newly created UserInfo with all the fields filled 
	 * @throws EntityNotFoundException if the rate statistics of this user if not found
	 */

	public static UserInfo createUser(String username) throws EntityNotFoundException {
		UserInfo user = new UserInfo();
		Key userKey = KeyFactory.createKey("user", username);
		Entity userEntity = datastore.get(userKey);
		// deposit the logged in user info to the mem cache
		memCache.put(username + "name", (Boolean)userEntity.getProperty("name"));
		user.setName(username);
		
		memCache.put(username + "gender", (Boolean)userEntity.getProperty("gender"));
		user.setGender((Boolean)userEntity.getProperty("gender"));
		
		memCache.put(username + "password", (String)userEntity.getProperty("password"));

		user.setEmail((String) userEntity.getProperty("email"));
		memCache.put(username + "email",
				(String) userEntity.getProperty("email"));
		
		user.setBirthDate((String) userEntity.getProperty("birthdate"));
		memCache.put(username + "birthdate",
				(String) userEntity.getProperty("birthdate"));
		
		user.setProfileImage((String) userEntity.getProperty("image"));
		memCache.put(username + "image",
				(String) userEntity.getProperty("image"));
		
		user.setHobby((String) userEntity.getProperty("hobby"));
		memCache.put(username + "hobby",
				(String) userEntity.getProperty("hobby"));
		
		user.setClub((String) userEntity.getProperty("club"));
		memCache.put(username + "club", (String) userEntity.getProperty("club"));
		
		user.setOccupation((String) userEntity.getProperty("occupation"));
		memCache.put(username + "occupation",
				(String) userEntity.getProperty("occupation"));
		
		user.setMotto((String) userEntity.getProperty("motto"));
		memCache.put(username + "motto",
				(String) userEntity.getProperty("motto"));
		
		user.setSchool((String) userEntity.getProperty("school"));
		memCache.put(username + "school",
				(String) userEntity.getProperty("school"));
		
		// get the rate information from the datastore
		Key rateKey = new KeyFactory.Builder("user", user.getName())
				.addChild("ratestat", user.getName()).getKey();
		Entity rateStatEntity = datastore.get(rateKey);
		user.setRate((Long) rateStatEntity.getProperty("rate"));
		memCache.put(username + "rate",
				(Long) rateStatEntity.getProperty("rate"));
		
		return user;
	}
	/**
	 * This method check the completion of the user information, including email, birthdate,
	 * motto, occupation, club, hobby, school.
	 * @param user the user that is checked
	 * @return true if all information is completed. false if any of the required field is
	 * not filled.
	 */
	public static boolean infoCompletionCheck(UserInfo user) {
		// true if everything is completed
		return (user.getBirthDate() != null && user.getHobby() != null && user.getClub() != null
				&& user.getEmail() != null && user.getMotto() != null
				&& user.getOccupation() != null && !user.getProfileImage().equals(UserInfo.DEFAULT_IMAGE)
				&& user.getSchool() != null);
	}
	/**
	 * This method tries to create a UserInfo from the cache information. 
	 * If the user information is not previously loaded into cache, it will automatically
	 * invoke createUser(username) method to get a new UserInfo
	 * @param username the name of the current user
	 * @return the newly created UserInfo
	 * @throws EntityNotFoundException  
	 */
	public static UserInfo getCachedUserInfo(String username) throws EntityNotFoundException {
		UserInfo user = new UserInfo();
		user.setName(username);
		String password, email, image, birthdate, school, motto, club, hobby, occupation;
		Boolean gender;
		Long rate;
		// there are two situations where memcache get a null value
		// 1.	the key exist in the memcache, and its value is null
		// 2.	the key does not exist in the memcache
		// if password information is in memcache, so will be the rest information of this user
		if((password = (String)memCache.get(username + "password")) != null)
			user.setPassword(password);
		else return createUser(username);
		if((gender = (Boolean)memCache.get(username + "gender") != null))
			user.setGender(gender);
		if((email = (String)memCache.get(username + "email")) != null)
			user.setEmail(email);
		if ((image = (String)memCache.get(username + "image")) != null)
			user.setProfileImage(image);
		if ((rate = (Long)memCache.get(username + "rate")) != null)
			user.setRate((Long)rate);
		if ((birthdate = (String)memCache.get(username + "birthdate")) != null)
			user.setBirthDate(birthdate);
		if ((club = (String)memCache.get(username + "club")) != null)
			user.setClub(club);
		if ((hobby = (String)memCache.get(username + "hobby")) != null)
			user.setHobby(hobby);
		if ((occupation = (String)memCache.get(username + "occupation")) != null)
			user.setOccupation(occupation);
		if ((motto = (String)memCache.get(username + "motto")) != null)
			user.setMotto(motto);
		if ((school = (String)memCache.get(username + "school")) != null)
			user.setSchool(school);
		return user;
	}
	
	/**
	 * This method get the conversation list of the user. It queries the "conversationlist"
	 * table to get all conversation threads that has the user as their parent key. The 
	 * conversations are sorted into the descending order of the date.
	 * @param user the name of the current user, the owner of the conversation list
	 * @return a list of conversation thread id.
	 */
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
	/**
	 * This method get the location of the current user.
	 * @param username the user whose location we want to get
	 * @return the city of the user
	 */
	public static String getLocation(String username) {
		return (String)memCache.get(username + "city");
	}
}
