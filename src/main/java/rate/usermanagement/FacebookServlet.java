package rate.usermanagement;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;


import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

import rate.usermanagement.FacebookLoginRequest;
@Path("/facebook")
public class FacebookServlet {
	@Path("login")
	@GET
	public Response login(@QueryParam("code")String code, 
						@Context HttpServletRequest request) 
			throws IOException, EntityNotFoundException {
		String token = FacebookLoginRequest.getFacebookAccessToken(code);
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		User thisuser = facebookClient.fetchObject("me", User.class);
		// check if the user has existed, if not register it
		Key userKey = KeyFactory.createKey("user", thisuser.getName());
		try {
			UserManager.datastore.get(userKey);
		} catch(EntityNotFoundException ex) {
			Map<String, String> attrMap = new HashMap<String, String>();
			attrMap.put("username", thisuser.getName());
			attrMap.put("email", thisuser.getEmail());
			attrMap.put("birthdate", thisuser.getBirthday());
			attrMap.put("gender", thisuser.getGender());
			attrMap.put("image", "https://graph.facebook.com/" + thisuser.getId() + "/picture?type=large");
			if (!UserManager.registerUser(attrMap))
				return Response.seeOther(URI.create("/error.html")).build();
		}
		return LogProcessor.facebookLogin(request, thisuser.getName());
	}
}
