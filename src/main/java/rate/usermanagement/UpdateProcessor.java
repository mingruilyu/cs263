package rate.usermanagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

@Path("/update")
public class UpdateProcessor {
	MemcacheService memCache = MemcacheServiceFactory.getMemcacheService();
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
			else if (nametoken.compareTo("birthdate") == 0)
				map.put("birthdate", reader.nextString());
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
