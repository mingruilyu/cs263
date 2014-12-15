package rate.ratingmanagement;


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

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
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
	public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	@Context HttpServletRequest request;	
	@Path("/ratedlist")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRatedlist() throws EntityNotFoundException {
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
		String username = (String)request.getSession().getAttribute("user");
		Key userRatingHistoryKey = KeyFactory.createKey("user", username);
		Query query = new Query("ratehistory", userRatingHistoryKey);
		List<Entity> userRatingEntityList = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(0));
		if (userRatingEntityList.size() == 0) {
			//the user has not rated anyone
			return Response.ok().build();
		}
		// get the first entity in the list
		List<RatedUserInfo> ratedList = new LinkedList<RatedUserInfo>();
		for (Entity rateeEntity : userRatingEntityList) {
			Key ratedEntityKey = KeyFactory.createKey("user", rateeEntity.getKey().getName());
			Key rateeLocationKey = KeyFactory.createKey("loginlocation", rateeEntity.getKey().getName());
			Key rateeRatingStatKey = new KeyFactory.Builder("user", rateeEntity.getKey().getName())
			   										.addChild("ratestat", rateeEntity.getKey().getName())
			   										.getKey();
			Key raterRatingHistoryKey = new KeyFactory.Builder("user", username)
													.addChild("ratehistory", rateeEntity.getKey().getName())
													.getKey();
			Entity rateeUserEntity = datastore.get(ratedEntityKey);
			Entity rateeLocationEntity = datastore.get(rateeLocationKey);
			Entity rateeStatEntity = datastore.get(rateeRatingStatKey);
			Entity raterRatingEntity = datastore.get(raterRatingHistoryKey);
			RatedUserInfo ratinginfo = new RatedUserInfo(rateeEntity.getKey().getName(), 
					// ratee must have a valid image property
					 (String)rateeUserEntity.getProperty("image"));
			ratinginfo.setLatitude((Double)rateeLocationEntity.getProperty("latitude"));
			ratinginfo.setLongitude((Double)rateeLocationEntity.getProperty("longitude"));
			ratinginfo.setTotalRate((Long)rateeStatEntity.getProperty("rate"));
			ratinginfo.setMyRate((Long)raterRatingEntity.getProperty("rate"));
			ratedList.add(ratinginfo);
		}
		return Response.ok(gson.toJson(ratedList)).build();
	}
	
	
	@Path("/unratedlist")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnRated() throws EntityNotFoundException {
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
	    String username = (String)request.getSession().getAttribute("user");
		// it is ensured that all user entity has the image property. if the user has not uploaded the image,
		// the image property would be ""
		Filter imageFilter = new FilterPredicate("image",
											FilterOperator.NOT_EQUAL,
											"");
		Query query = new Query("user").setFilter(imageFilter);
		List<Entity> userEntityList = datastore.prepare(query).asList(FetchOptions.Builder.withOffset(0));
		// no user has put their picture to be rated
		if (userEntityList.isEmpty()) return Response.ok().build();
		// query all images the user has rated
		Key userRatingHistoryKey = KeyFactory.createKey("user", username);
		Query ratelistquery = new Query("ratehistory", userRatingHistoryKey);
		List<Entity> userRateEntityList = datastore.prepare(ratelistquery).asList(FetchOptions.Builder.withOffset(0));
		List<Entity> unratedEntityList = new LinkedList<Entity>();
		// the first pass check how many entities that has not been rated
		boolean flag;
		for(Entity userEntity : userEntityList) {
			flag = false;
			// check if the user is in the ratedlist of the session user
			for (Entity ratedEntity : userRateEntityList) {
				if (ratedEntity.getKey().getName().compareTo(userEntity.getKey().getName()) == 0) {
					// means the user has rated the person
					flag = true;
					break;
				}
			}
			// if the user is not in the ratedlist & it is not the session user himself,
			// this is possible because that user himself would not be in the ratedlist
			if (flag == false && userEntity.getKey().getName().compareTo(username) != 0)
				unratedEntityList.add(userEntity);
		}
		List<UnratedUserInfo> unratedUserInfoList = new LinkedList<UnratedUserInfo>();
		for (Entity rateeEntity : unratedEntityList) {
			// get the unrated ratee's location
			Key rateeLocationKey = KeyFactory.createKey("loginlocation", rateeEntity.getKey().getName());
			Entity rateeLocationEntity = datastore.get(rateeLocationKey);
			UnratedUserInfo unratedUserInfo = new UnratedUserInfo(rateeEntity.getKey().getName(), 
				 											(String)rateeEntity.getProperty("image"));
			unratedUserInfo.setLatitude((Double)rateeLocationEntity.getProperty("latitude"));
			unratedUserInfo.setLongitude((Double)rateeLocationEntity.getProperty("longitude"));
			unratedUserInfoList.add(unratedUserInfo);
		}
		if (unratedUserInfoList.isEmpty()) 
			return Response.ok().build();
		else 
			return Response.ok(gson.toJson(unratedUserInfoList)).build();
	}
	
	@Path("/addrate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addRating(String jsonrate) throws EntityNotFoundException {
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
	    Rate rate = gson.fromJson(jsonrate, Rate.class);

	    // add to the rater's rate history
		Key raterKey = KeyFactory.createKey("user", rate.rater);
		Entity rateHistoryEntity = new Entity("ratehistory", rate.ratee, raterKey);
		rateHistoryEntity.setProperty("rate", rate.rate);
		rateHistoryEntity.setProperty("date", new Date());
		datastore.put(rateHistoryEntity);
		
		//Update the rate statistics
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
		
		//email check
		Queue taskqueue = QueueFactory.getDefaultQueue();
        taskqueue.add(withUrl("/conversation/conversation/check")
        			  .param("ratee", rate.ratee)
        			  .param("rater", rate.rater)
        			  .param("rate", String.valueOf(rate.rate)));
        
		return Response.ok(newrate).build();
	}
}



