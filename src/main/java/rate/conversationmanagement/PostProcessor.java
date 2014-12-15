package rate.conversationmanagement;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.gson.stream.JsonReader;
@Path("/post")
public class PostProcessor {
	static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	@Context HttpServletRequest request;
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/postnew")
	public Response PostNewComments(String comments) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(comments));
		reader.beginObject();
		String nametoken, image = null, contents = null;
		while(reader.hasNext()) {
			nametoken = reader.nextName();
			if (nametoken.compareTo("image") == 0)
				image = reader.nextString();
			else if (nametoken.compareTo("contents") == 0) 
				contents = reader.nextString();
			else 
				reader.nextString();
		}
		Key key = KeyFactory.createKey("user", (String)request.getSession().getAttribute("user"));
		Entity newPost = new Entity("post", key);
		if (!image.endsWith("null"))
			newPost.setProperty("image", image);
		if (contents != null)
			newPost.setProperty("contents", contents);
		newPost.setProperty("date", new Date());
		datastore.put(newPost);
		return Response.ok().build(); 
	}
	
	public static List<Post> getPost(String user) {
		// get the post of the owner
		Key postKey = KeyFactory.createKey("user", user);
	    Query query = new Query("post", postKey).addSort("date", Query.SortDirection.ASCENDING);
	    List<Post> postList = new LinkedList<Post>();
	    for (Iterator<Entity> iterator = datastore.prepare(query).asIterator(); iterator.hasNext(); ) {
	    	Entity entity = iterator.next();
	    	Post post = new Post();
	    	post.setContents((String)entity.getProperty("contents"));
	    	post.setImage((String)entity.getProperty("image"));
	    	post.setDate((Date)entity.getProperty("date"));
	    	postList.add(post);
	    }
	    return postList;
	}
}
