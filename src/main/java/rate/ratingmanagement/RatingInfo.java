package rate.ratingmanagement;

import rate.usermanagement.LocationInfo;
/**
 * This class is used when transmitting back the list of 
 * unrated user information back to the frontend
 * @author Mingrui Lyu
 * @version 1.0
 */
class UnratedUserInfo {
	String name;
	String image;
	LocationInfo location = new LocationInfo();
	UnratedUserInfo(String name, String image) {
		this.image = image;
		this.name = name;
	}
	public void setCity(String city) {
		location.setCity(city);
	}
	public void setLatitude(double latitude) {
		location.setLatitude(latitude);
	}
	public void setLongitude(double longitude) {
		location.setLongitude(longitude);
	}	
}
/**
 * This class is used when transmitting back the list of 
 * rated user information back to the frontend
 * @author Mingrui Lyu
 * @version 1.0
 */
class RatedUserInfo {
	String name;
	String image;
	/**
	 * average rate of all people that have rated him
	 */
	long totalrate;
	/**
	 * the current user's rate
	 */
	long myrate;
	LocationInfo location = new LocationInfo();
	RatedUserInfo(String name, String image) {
		this.image = image;
		this.name = name;
	}
	public void setTotalRate(Long totalRate) {
		this.totalrate = totalRate;
	}
	public void setMyRate(Long myRate) {
		this.myrate = myRate;
	}
	public void setCity(String city) {
		location.setCity(city);
	}
	public void setLatitude(double latitude) {
		location.setLatitude(latitude);
	}
	public void setLongitude(double longitude) {
		location.setLongitude(longitude);
	}	
}
/**
 * This class is used to transmit back and forth the
 * rating information.
 * @author Mingrui Lyu
 * @version 1.0
 */
class Rate {
	String rater;
	String ratee;
	long rate;
}