package rate.usermanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

@Path("/log")
public class LogProcessor {
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();	
	@Context HttpServletRequest request;
	public static Response facebookLogin(HttpServletRequest request, String username) {
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null)
			session.setAttribute("user", username);
		return Response.seeOther(URI.create("/location.jsp")).build();
	}

	@Path("logout") 
	@GET
	public Response logout(){
		request.getSession().invalidate();
		
		// write the locaiton in memcache to datastore
		return Response.seeOther(URI.create("/login.jsp")).build();
	}
	
	@POST
	@Path("location")
	public Response registerLocation(String jsonstring) throws IOException {
		LocationInfo location = new LocationInfo();
		JsonReader reader = new JsonReader(new StringReader(jsonstring));
		reader.beginObject();
		String nametoken;
		while(reader.hasNext()) {
			nametoken = reader.nextName();
			if (nametoken.compareTo("latitude") == 0)
				location.setLatitude(reader.nextDouble());
			else if (nametoken.compareTo("longitude") == 0) 
				location.setLongitude(reader.nextDouble());
			else if (nametoken.compareTo("city") == 0)
				location.setCity(reader.nextString());
			else 
				reader.nextString();
		}
		reader.close();
		String username = (String) request.getSession().getAttribute("user");
		Key locationKey = KeyFactory.createKey("loginlocation", username);
    	Entity locationEntity = new Entity(locationKey);
    	locationEntity.setProperty("latitude", location.getLatitude());
    	locationEntity.setProperty("longitude", location.getLongitude());
    	locationEntity.setProperty("city", location.getCity());
    	UserManager.datastore.put(locationEntity);
    	
    	// put the location info into memcache
    	MemcacheService memCache = MemcacheServiceFactory.getMemcacheService();
    	memCache.put(username + "city", location.getCity());
		memCache.put(username + "latitude", location.getLatitude());
		memCache.put(username + "longitude", location.getLongitude());
        return Response.ok().build();
	}
	
	
	@GET
	@Path("directlogin")
	public Response directLogin(@QueryParam("username")String username,
								@QueryParam("password")String password) 
								throws EntityNotFoundException, IOException {
		Key userKey = KeyFactory.createKey("user", username);
		Entity userEntity = null;
		int check;
		try{
			userEntity = UserManager.datastore.get(userKey);
			check = checkLogin(username, password, userEntity);
		}
		catch(EntityNotFoundException ex) {
			check = -3;
		}		
		switch (check) {
		case -1: return Response.seeOther(URI.create("/login.jsp?error=1")).build(); // login from facebook
		case -2: return Response.seeOther(URI.create("/login.jsp?error=2")).build(); // wrong password
		case -3: return Response.seeOther(URI.create("/login.jsp?error=3")).build(); // user does not exist
		default:
			HttpSession session = request.getSession();
			if (session.getAttribute("user") == null)
				session.setAttribute("user", username);
			else {
				session.invalidate();
				session.setAttribute("user", username);
			}
			return Response.seeOther(URI.create("/location.jsp")).build();
		}
	}
	private int checkLogin(String username, String password, Entity userEntity) {
			String psw = (String)userEntity.getProperty("password");
			if (psw == null) // need to login from facebook
				return -1;
			else if (password.compareTo(psw) == 0) // password match
				return 0;
			else return -2; // password not match
	}
}
