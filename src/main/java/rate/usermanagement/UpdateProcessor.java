package rate.usermanagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.stream.JsonReader;
/**
 * This class handles the user information update requests.
 * @author Mingrui Lyu
 *
 */
@Path("/update")
public class UpdateProcessor {
	MemcacheService memCache = MemcacheServiceFactory.getMemcacheService();
	/**
	 * This method is a REST POST API. It receives update information 
	 * then invokes getMap() method that extracts attributes and put
	 * the attributes into map. It then uses the attribute map to reset
	 * the properties of the user entity and put it back to the datastore,
	 * as well as synchronize the memcache.   
	 * @param request the Http request
	 * @param jsonstring the Json string contains the update information
	 * @return a success response
	 * @throws IOException if the JsonReader fails to read the Json string
	 * @throws EntityNotFoundException if datastore does not contain the specified user
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response infoUpdate(@Context HttpServletRequest request, String jsonstring) 
			throws IOException, EntityNotFoundException{
		Map<String, String> map = getMap(jsonstring);
		Key userKey = KeyFactory.createKey("user", (String)request.getSession().getAttribute("user"));
		Entity userEntity = UserManager.datastore.get(userKey);
		for(String attr : map.keySet()) {
			userEntity.setProperty(attr, map.get(attr));
			memCache.put(userKey.getName() + attr, map.get(attr));
		}
		UserManager.datastore.put(userEntity);
		return Response.ok().build();
	}
	/**
	 * This method is called by infoUpdate to interpret the Json string.
	 * It put the value of corresponding field into an attribute map through
	 * attribute pairs. The attribute pairs take the form of (attribute, value).
	 * @param jsonstring the Json string contains the update information
	 * @return an attribute map that takes the attribute name as key, 
	 * and the attribute value as value.
	 * @throws IOException if the JsonReader fails to read the Json string.
	 */
	private static Map<String, String> getMap(String jsonstring) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(jsonstring));
		reader.beginObject();
		String nametoken;
		Map<String, String> map = new HashMap<String, String>();
		while(reader.hasNext()) {
			nametoken = reader.nextName();
			if (nametoken.compareTo("email") == 0)
				map.put("email", reader.nextString());
			else if (nametoken.compareTo("gender") == 0) 
				map.put("gender", reader.nextString());
			else if (nametoken.compareTo("image") == 0)
				map.put("image", reader.nextString());
			else if (nametoken.compareTo("hobby") == 0)
				map.put("hobby", reader.nextString());
			else if (nametoken.compareTo("club") == 0)
				map.put("club", reader.nextString());
			else if (nametoken.compareTo("school") == 0)
				map.put("school", reader.nextString());
			else if (nametoken.compareTo("occupation") == 0)
				map.put("occupation", reader.nextString());
			else if (nametoken.compareTo("motto") == 0)
				map.put("motto", reader.nextString());
			else 
				reader.nextString();
		}
		reader.close();
		return map;
	}
}
