package rate.usermanagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.stream.JsonReader;
/**
 * This class handles the user signup requests
 * @author Mingrui Lyu
 * @version 1.0
 */
@Path("/signupprocessor")
public class SignUpProcessor {
	/**
	 * this static field refers to the GAE datastore low-level api
	 */
	static public DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	@POST
	@Path("/test")
	@Consumes(MediaType.APPLICATION_JSON)
	/**
	 * This is a REST POST API. it receives the user sign up requests and
	 * invoke parseJson() method that interpret the Json string. It then
	 * adds the UserInfo returned from parJson() to the datastore by calling
	 * addUser(UserInfo) methods.   
	 * @param jsonstring
	 * @return a success response if the user is added to the datastore,
	 * a failure response if the adding user operation fails
	 * @throws IOException if the JsonReader fails to read the Json string
	 */
	public Response InfoArchive(String jsonstring) throws IOException {
		UserInfo user = new UserInfo();
		parseJson(jsonstring, user);
		if (addUser(user))
			return Response.ok("1", MediaType.TEXT_PLAIN).status(201).build();
		return Response.ok("2", MediaType.TEXT_PLAIN).status(210).build();
	}
	/**
	 * This method parse the Json string and extracts the user signup information.
	 * It then fills in the corresponding fields of user with the signup information.
	 * @param jsonstring the Json string to be parsed
	 * @param user the user whose corresponding fields will be filled 
	 * @throws IOException if the JsonReader fails to read the Json string
	 */
	public static void parseJson(String jsonstring, UserInfo user) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(jsonstring));
		reader.beginObject();
		String nametoken;
		/* username, password, email, gender, birthdate, location */
		while (reader.hasNext()) {
			nametoken = reader.nextName();
			if (nametoken.compareTo("username") == 0)
				user.setName(reader.nextString());
			else if (nametoken.compareTo("password") == 0)
				user.setPassword(reader.nextString());
			else if (nametoken.compareTo("email") == 0)
				user.setEmail(reader.nextString());
			else if (nametoken.compareTo("gender") == 0) {
				if (reader.nextString().equals("male"))
					user.setGender(false);
				else user.setGender(true);
				}
			else if (nametoken.compareTo("birthdate") == 0)
				user.setBirthDate(reader.nextString());
			else reader.nextString();
		}
	    reader.close();
	}
	/**
	 * This method add a user specified by the paramater UserInfo. It first checks whether
	 * the user has already existed in the datastore. It then puts all attributes of the user
	 * into an attribute map and will only call registerUser(attrMap) if the user does not
	 * exist in the datastore.
	 * @param user
	 * @return true if add the user successfully; false if the user already exists
	 * or the registerUser(attrMap) fails to register the user.
	 */
	static public boolean addUser(UserInfo user) {
		// check if the user has existed
		if (user == null) return false;
		Key userkey = KeyFactory.createKey("user", user.getName());
		try {
			UserManager.datastore.get(userkey);
		}catch (EntityNotFoundException ex){
			Map<String, String> attrMap = new HashMap<String, String>();
			attrMap.put("username", user.getName());
			attrMap.put("email", user.getEmail());
			attrMap.put("birthdate", user.getBirthDate());
			attrMap.put("gender", user.getGender());
			attrMap.put("password", user.getPassword());
			attrMap.put("image", "");
			return registerUser(attrMap);
		}
		return false;
	}
	/**
	 * This method registers the user with user information pairs provided in the attrMap
	 * by storing the user entity to the datastore.
	 * @param attrMap attribute map that includes pairs of information in form of (attribute, value)
	 * @return true if the user has been successfully registered.
	 */
	public static boolean registerUser(Map<String, String> attrMap) {
		String password, email, birthdate, location, userName, gender, image;
		Entity userEntity;
		if (attrMap == null)
			return false;

		if ((userName = attrMap.get("username")) != null)
			userEntity = new Entity("user", userName);
		else
			return false;

		if ((image = attrMap.get("image")) != null)
			userEntity.setProperty("image", image);
		if ((password = attrMap.get("password")) != null)
			userEntity.setProperty("password", password);
		if ((email = attrMap.get("email")) != null)
			userEntity.setProperty("email", email);
		if ((gender = attrMap.get("gender")) != null) {
			if (gender.equals("male"))
				userEntity.setProperty("gender", true);
			else
				userEntity.setProperty("gender", false);
		}
		if ((birthdate = attrMap.get("birthdate")) != null)
			userEntity.setProperty("birthdate", birthdate);
		if ((location = attrMap.get("location")) != null)
			userEntity.setProperty("location", location);
		datastore.put(userEntity);
		// initialize the rating statistics upon registration
		Key userKey = KeyFactory.createKey("user", userName);
		Entity userRatingStatEntity = new Entity("ratestat", userName, userKey);
		userRatingStatEntity.setProperty("rate", 0);
		userRatingStatEntity.setProperty("ratecount", 0);
		datastore.put(userRatingStatEntity);
		return true;
	}
}
