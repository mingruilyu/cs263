package ratingsystem;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@Path("/user/{username}")
public class UserManager {
	private UserInfo user = null;
	UserManager(String userName) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key userkey = KeyFactory.createKey("userinfo", userName);
		Entity userEntity = datastore.get(userkey);
		this.user = new UserInfo();
		this.user.setGender(((Boolean)userEntity.getProperty("gender")).booleanValue());
		this.user.setName((String)userEntity.getProperty("name"));
		this.user.setLocation((String)userEntity.getProperty("location"));
		this.user.setEmail((String)userEntity.getProperty("email"));
		this.user.setBirthDate((String)userEntity.getProperty("birthdate"));
		this.user.setRate((Integer)userEntity.getProperty("rate"));
		this.user.setRatingPermission(((Boolean)userEntity.getProperty("ratingpermission")).booleanValue());
	}
	@GET
	public static Response getUser(@PathParam("username")String userName) throws EntityNotFoundException {
		UserManager usermanager = new UserManager(userName);
		if (usermanager.user != null)
			return Response.status(200).entity(usermanager).build();
		else return Response.status(400).build();
	}
	
	@Context
	@POST
	public void updateUserInfo(@Context HttpServletRequest request) {
		if (request == null) return;
		String type = request.getParameter("type");
		String value = request.getParameter("value");
		switch(Integer.getInteger(type)) {
		case UserInfo.propertyno.name.ordinal():
		case "gender":
		case "email":
		}
	}
	
	@Path("/conversationlist")
	@GET
	public Response getConversationList(@PathParam("username")String username) {
		// return conversation ID
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key conversationKey = KeyFactory.createKey("ConversationList", username);
	    Query query = new Query(conversationKey).addSort("date", Query.SortDirection.DESCENDING);
	    List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
	}
}
