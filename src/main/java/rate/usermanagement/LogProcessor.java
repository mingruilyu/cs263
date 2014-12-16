package rate.usermanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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
import com.google.gson.stream.JsonReader;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
/**
 * This class handles all the login and logout requests.
 * @author Mingrui Lyu
 * @version 1.0
 */
@Path("/log")
public class LogProcessor {
	@Context HttpServletRequest request;
	
	static final String APPID = "732083976882116";
	static final String APPSECRET = "eacc38539438a41c127ec7e1c6662994";
	static final String AUTH_URL = "https://www.facebook.com/dialog/oauth";
	// static String redirectUrl = "http://lyumingrui1.appspot.com/rest/log/facebooklogin";
	static final String REDIRECT_URL = "http://localhost:8080/rest/log/facebooklogin";
	static final String TOKEN_URL = "https://graph.facebook.com/oauth/access_token";
	static final String PERMISSION = "email,user_friends,user_birthday,user_hometown";
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	/**
	 * This method gets the Facebook login authentication url address
	 * @return String the link of Facebook login authentication page
	 */
	public static String getFacebookAuthURL() {
		return AUTH_URL + "?client_id=" + APPID + "&redirect_uri=" + REDIRECT_URL
				+ "&scope=" + PERMISSION + "&auth_type=reauthenticate";
	}
	/**
	 * This method gets the Facebook login token
	 * @param faceCode the Facebook login token can be obtained by providing application ID,
	 * application secret code and facecode.
	 * @return the token required to log into the application through Facebook 
	 * @throws IOException the exception is thrown if the buffered reader fails to read the response stream
	 */
	static public String getFacebookAccessToken(String faceCode)
			throws IOException {
		String token = null;
		if (faceCode != null && !"".equals(faceCode)) {
			String newUrl = TOKEN_URL + "?client_id=" + APPID + "&redirect_uri="
					+ REDIRECT_URL + "&client_secret=" + APPSECRET + "&code="
					+ faceCode;
			GenericUrl redirecturl = new GenericUrl(newUrl);
			HttpRequestFactory requestFactory = HTTP_TRANSPORT
					.createRequestFactory();
			HttpRequest request = requestFactory.buildGetRequest(redirecturl);
			HttpResponse response = request.execute();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null)
				result.append(line);
			String temptoken = StringUtils.removeStart(result.toString(),
					"access_token=");
			token = temptoken.substring(0, temptoken.indexOf('&'));
		}
		return token;
	}
	/**
	 * This method is a REST GET API. 
	 * It terminates the current session and redirect to login page.
	 * @return redirect response
	 */

	@Path("logout")
	@GET
	public Response logout() {
		request.getSession().invalidate();
		return Response.seeOther(URI.create("/login.jsp")).build();
	}
	/**
	 * This method register the login user's location, including the longitude and latitude,
	 * as well as the login city.
	 * @param jsonstring the Json stream 
	 * @return success response
	 * @throws IOException this exception is thrown if the JsonReader cannot start
	 * reading the Json stream
	 */
	@POST
	@Path("location")
	public Response registerLocation(String jsonstring) throws IOException {
		LocationInfo location = new LocationInfo();
		JsonReader reader = new JsonReader(new StringReader(jsonstring));
		reader.beginObject();
		String nametoken;
		while (reader.hasNext()) {
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
	/**
	 * This method handles the login request from facebook. It will check whether
	 * the current user is in the datastore. If not, register the user and start
	 * the new session.
	 * @param code
	 * @param request Http servlet request 
	 * @return login success response
	 * @throws IOException
	 * @throws EntityNotFoundException
	 */
	@Path("facebooklogin")
	@GET
	public Response login(@QueryParam("code")String code, 
						@Context HttpServletRequest request) 
			throws IOException, EntityNotFoundException {
		String token = LogProcessor.getFacebookAccessToken(code);
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		User thisuser = facebookClient.fetchObject("me", User.class);
		// check if the user has existed, if not register it
		Key userKey = KeyFactory.createKey("user", thisuser.getName());
		try {
			UserManager.datastore.get(userKey);
		} catch(EntityNotFoundException ex) {
			Map<String, String> attrMap = new HashMap<String, String>();
			attrMap.put("username", thisuser.getName().replaceAll("\\s",""));
			attrMap.put("email", thisuser.getEmail());
			attrMap.put("birthdate", thisuser.getBirthday());
			attrMap.put("gender", thisuser.getGender());
			attrMap.put("image", "https://graph.facebook.com/" + thisuser.getId() + "/picture?type=large");
			if (!SignUpProcessor.registerUser(attrMap))
				return Response.seeOther(URI.create("/error.html")).build();
		}
		HttpSession session = request.getSession();
		String username = thisuser.getName().replaceAll("\\s","");
		if (session.getAttribute("user") == null)
			session.setAttribute("user", username);
		else {
			session.invalidate();
			session = request.getSession();
			session.setAttribute("user", username);
		}
		return Response.seeOther(URI.create("/location.jsp")).build();
	}
	
	/**
	 * This method is a REST GET API. It handles all direct login requests(not from facebook).
	 * It does check on the username and password. 
	 * @param username
	 * @param password
	 * @return If the user sign up through facebook, a redirect response to the login page
	 * with parameter error set to 1. If the user does not exist, a redirect response is returned 
	 * to the login page with parameter error set to 3. If the user exist but the password is wrong
	 * a redirect response is returned to the login page with paramerter error set to 2.
	 * 
	 * @throws EntityNotFoundException
	 * @throws IOException
	 */
	@GET
	@Path("directlogin")
	public Response directLogin(@QueryParam("username") String username,
								@QueryParam("password") String password)
			throws EntityNotFoundException, IOException {
		Key userKey = KeyFactory.createKey("user", username);
		Entity userEntity = null;
		int check;
		try {
			userEntity = UserManager.datastore.get(userKey);
			check = checkLogin(password, userEntity);
		} catch (EntityNotFoundException ex) {
			check = -3;
		}
		switch (check) {
		case -1:
			// need to login from facebook
			return Response.seeOther(URI.create("/login.jsp?error=1")).build(); 
		case -2:
			// wrong password
			return Response.seeOther(URI.create("/login.jsp?error=2")).build(); 
		case -3:
			// user does not exist
			return Response.seeOther(URI.create("/login.jsp?error=3")).build(); 
		default:
			HttpSession session = request.getSession();
			if (session.getAttribute("user") == null)
				session.setAttribute("user", username);
			else {
				session.invalidate();
				session = request.getSession();
				session.setAttribute("user", username);
			}
			return Response.seeOther(URI.create("/location.jsp")).build();
		}
	}
	
	/**
	 * This method checks the password against the existing user's password.
	 * @param password the password with which the user trying to login
	 * @param userEntity the user record stored in the datastore
	 * @return -1 if the user need to login from facebook
	 * -2 if the password is wrong, 0 if the password is correct.
	 */
	private int checkLogin(String password, Entity userEntity) {
		String psw = (String) userEntity.getProperty("password");
		if (psw == null) // need to login from facebook
			return -1;
		else if (password.equals(psw)) // password match
			return 0;
		else return -2; // password not match
	}
}
