package rate.usermanagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


//import ratingsystem.UserInfo;
@Path("/signupprocessor")
public class SignUpProcessor {
	
	@POST
	@Path("/test")
	@Consumes(MediaType.APPLICATION_JSON)
	
	public Response InfoArchive(String jsonstring) throws IOException {
		UserInfo user = new UserInfo();
		parseJson(jsonstring, user);
		if (addUser(user))
			return Response.ok("1", MediaType.TEXT_PLAIN).status(201).build();
		return Response.ok("2", MediaType.TEXT_PLAIN).status(210).build();
	}
	
	
	private static void parseJson(String jsonstring, UserInfo user) throws IOException {
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
	
	static private boolean addUser(UserInfo user) {
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
			if (UserManager.registerUser(attrMap)) return true;
			else return false;
		}
		return false;
	}
}
