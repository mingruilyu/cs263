package ratingapp.usermanagement;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/login")
public class LoginInProcessor {
	public static Response facebookLogin(HttpServletRequest request, String username) {
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null)
			session.setAttribute("user", username);
		return Response.seeOther(URI.create("/welcome.jsp")).build();
	}
	@GET
	@Path("directlogin")
	//@Produces(MediaType.APPLICATION_JSON)
	public Response directLogin(@QueryParam("username")String username,
								@QueryParam("password")String password,
								@Context HttpServletRequest request) throws EntityNotFoundException, IOException {
		//System.out.println(username + "\t" + password);
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
		/*switch (check) {
		case -1: return Response.ok("fblogin", MediaType.TEXT_PLAIN).build();
		case -2: return Response.ok("wrongpsw", MediaType.TEXT_PLAIN).build();
		case -3: return Response.ok("nouser", MediaType.TEXT_PLAIN).build();
		}*/
		switch (check) {
		case -1: return Response.seeOther(URI.create("/login.jsp?error=1")).build();
		case -2: return Response.seeOther(URI.create("/login.jsp?error=2")).build();
		case -3: return Response.seeOther(URI.create("/login.jsp?error=3")).build();
		default:
			//UserManager usermanager = new UserManager(userEntity);
			HttpSession session = request.getSession();
			//session.setAttribute("usermanager", usermanager);
			if (session.getAttribute("user") == null)
				session.setAttribute("user", username);
			//GsonBuilder gsonbuilder = new GsonBuilder();
	        //Gson gson = gsonbuilder.create();
			//System.out.println("test");entity(gson.toJson(usermanager))
			return Response.seeOther(URI.create("/welcome.jsp")).build();
		}
		//return Response.ok("login", MediaType.TEXT_PLAIN).status(201).build();
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
