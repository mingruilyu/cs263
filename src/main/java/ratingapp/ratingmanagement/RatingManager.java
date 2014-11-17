package ratingapp.ratingmanagement;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/rating")
public class RatingManager {

	final static long HIGH_SCORE = 90;
	public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	@Context HttpServletRequest request;
	@Path("/rated")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRated(@QueryParam("offset") String offsetstr) throws EntityNotFoundException {
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
		int offset = Integer.valueOf(offsetstr);
		System.out.println("rated offset = " + offset);
		String username = (String)request.getSession().getAttribute("user");
		Key userRatingHistoryKey = KeyFactory.createKey("user", username);
		Query query = new Query("ratehistory", userRatingHistoryKey);
		List<Entity> userRatingEntityList = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(offset));
		if (userRatingEntityList.size() == 0) {
			offset = 0;
			// it is possible that the offset has exceeded the number of rated entity
			// reset the offset
			userRatingEntityList = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(offset));
			if (userRatingEntityList.size() == 0) {
				//the user has not rated anyone
				return Response.ok().build();
			}
		}
		// get the first entity in the list
		Key ratedEntityKey = KeyFactory.createKey("user", userRatingEntityList.get(0).getKey().getName());
		Entity rateeUserEntity = datastore.get(ratedEntityKey);
		Entity rateeEntity = datastore.get(userRatingEntityList.get(0).getKey());
		RatedUserInfo ratinginfo = new RatedUserInfo(rateeEntity.getKey().getName(), 
													// ratee must have a valid image property
													 (String)rateeUserEntity.getProperty("image"),
													 (Long)rateeEntity.getProperty("rate"),
													 ++ offset);
		return Response.ok(gson.toJson(ratinginfo)).build();
	}
	
	@Path("/unrated")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnRated(@QueryParam("offset") String offsetstr) {
		System.out.println("offset=" + offsetstr);
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
	    int offset;
	    String username = (String)request.getSession().getAttribute("user");
		// it is ensured that all user entity has the image property. if the user has not uploaded the image,
		// the image property would be ""
		Filter imageFilter = new FilterPredicate("image",
											FilterOperator.NOT_EQUAL,
											"");
		//Filter compositeFilter = CompositeFilterOperator.and(imageFilter, nameFilter);
		Query query = new Query("user").setFilter(imageFilter);
		List<Entity> userEntityList = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(0));
		// no user has put their picture to be rated
		if (userEntityList.isEmpty()) return Response.ok().build();
		// query all images the user has rated
		Key userRatingHistoryKey = KeyFactory.createKey("user", username);
		Query ratelistquery = new Query("ratehistory", userRatingHistoryKey);
		List<Entity> userRateEntityList = datastore.prepare(ratelistquery).asList(FetchOptions.Builder.withOffset(0));
		System.out.println("userRateentityList size = " + userRateEntityList.size());
		List<Entity> unratedEntityList = new LinkedList<Entity>();
		// the first pass check how many entities that has not been rated
		boolean flag;
		for(Entity userEntity : userEntityList) {
			flag = false;
			for (Entity ratedEntity : userRateEntityList) {
				if (ratedEntity.getKey().getName().compareTo(userEntity.getKey().getName()) == 0) {
					// means the user has rated the person
					flag = true;
					break;
				}
			}
			// it is the user itself
			if (flag == false && userEntity.getKey().getName().compareTo(username) != 0) {
				unratedEntityList.add(userEntity);
				System.out.println("sessoin user = " + username);
				System.out.println("userEntity name = " + userEntity.getKey().getName());
			}
		}
		System.out.println("userentity = " + unratedEntityList.size());
		if (!unratedEntityList.isEmpty()) {
			offset = Integer.parseInt(offsetstr) % unratedEntityList.size();
			Entity selectedEntity = unratedEntityList.get(offset);
			UnratedUserInfo ratinginfo = new UnratedUserInfo(selectedEntity.getKey().getName(), 
														 	(String)selectedEntity.getProperty("image"),
														 	offset + 1);
			return Response.ok(gson.toJson(ratinginfo)).build();		
		}
		else return Response.ok().build();
	}
	
	@Path("/addrate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addRating(String jsonrate) throws EntityNotFoundException {
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
	    Rate rate = gson.fromJson(jsonrate, Rate.class);
		// it is made sure that no duplicate rate would be added
	    // add the new rate to the rate history 
	    System.out.println("rater = " + rate.rater);
		Key raterKey = KeyFactory.createKey("user", rate.rater);
		Entity rateHistoryEntity = new Entity("ratehistory", rate.ratee, raterKey);
		rateHistoryEntity.setProperty("rate", rate.rate);
		rateHistoryEntity.setProperty("date", new Date(System.currentTimeMillis()));
		datastore.put(rateHistoryEntity);
		//as well as update the rate statistics
		System.out.println("ratee = " + rate.ratee);
		Key rateeRatingStatKey = new KeyFactory.Builder("user", rate.ratee)
											   .addChild("ratestat", rate.ratee)
											   .getKey();
		Entity rateeStatEntity = datastore.get(rateeRatingStatKey);
		long oldrate = (Long)rateeStatEntity.getProperty("rate");
		long ratecount = (Long)rateeStatEntity.getProperty("ratecount");
		long newrate = (oldrate * ratecount + rate.rate) / (++ ratecount);
		rateeStatEntity.setProperty("rate", newrate);
		rateeStatEntity.setProperty("ratecount", ratecount);
		datastore.put(rateeStatEntity);
		
		//conversation check
		Key rateeHistoryKey = new KeyFactory.Builder("user", rate.ratee)
											.addChild("ratehistory", rate.rater)
											.getKey();
		try {
			Entity rateeHistoryEntity = datastore.get(rateeHistoryKey);
			long rateerate = (Long)rateeHistoryEntity.getProperty("rate");
			if (rateerate > HIGH_SCORE && rate.rate > HIGH_SCORE) {
				// we found a match
				Key raterconversationKey = KeyFactory.createKey("user", rate.rater);
				Key rateeconversationKey = KeyFactory.createKey("user", rate.ratee);
				Entity raterconversationEntity = new Entity("conversation", raterconversationKey);
				Entity rateeconversationEntity = new Entity("conversation", rateeconversationKey);
				
				raterconversationEntity.setProperty("talkee", rate.ratee);
				raterconversationEntity.setProperty("date", new Date(System.currentTimeMillis()));
				
				rateeconversationEntity.setProperty("talkee", rate.rater);
				rateeconversationEntity.setProperty("date", new Date(System.currentTimeMillis()));
				
				datastore.put(raterconversationEntity);
				datastore.put(rateeconversationEntity);
				
		        Queue taskqueue = QueueFactory.getDefaultQueue();
		        taskqueue.add(withUrl("/conversation/conversation/messenger"));
			}			
		}
		catch(EntityNotFoundException ex) {
			
		}
		return Response.ok(newrate).build();
	}
}



