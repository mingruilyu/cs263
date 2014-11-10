package ratingapp.usermanagement;

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

import ratingapp.usermanagement.FacebookLoginRequest;
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
			if (!UserManager.registerUser(attrMap))
				return Response.seeOther(URI.create("/error.html")).build();
		}
		return LoginInProcessor.facebookLogin(request, thisuser.getName());
	}
	/*public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String token = (String) request.getParameter("token");
		PrintWriter writer = response.getWriter();
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
		List<User> users = myFriends.getData();
		User thisuser = facebookClient.fetchObject("me", User.class);
		writer.print("<table><tr><th>Photo</th><th>Name</th><th>Id</th></tr>");
		//writer.print("<p>My lastname is " + user.getLastName() + "</p>");

		//writer.print("<p>My id is " + user.getLastName() + "</p>");
		
		writer.print("<p>My name is " + thisuser.getName() + "</p>");
		writer.print("<p>My gender is " + thisuser.getGender() + "</p>");
		writer.print("<p>My link is " + thisuser.getLink() + "</p>");
		writer.print("<p>My Locale is " + thisuser.getLocale() + "</p>");
		writer.print("<p>My id is " + thisuser.getId() + "</p>");
		writer.print("<p>My email is " + thisuser.getEmail() + "</p>");
		writer.print("<p>My birthday is " + thisuser.getBirthday() + "</p>");
		writer.print("<p>My hometown is " + thisuser.getHometown() + "</p>");
		writer.print("<img src=\"https://graph.facebook.com/" + thisuser.getId() + "/picture?type=large\"/>");
		for(Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
	        User user = (User)iterator.next();
	        writer.print("<tr><td>"	+ user.getName() + "</td><td>" + user.getId() + "</td><td>" + 
	        		"<img src=\"https://graph.facebook.com/" + user.getId() + "/picture?type=large\"/>"
					+ "</td></tr>");
	    }
		writer.print("</table>");
		writer.close();
	}*/
}
