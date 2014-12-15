package rate.ratingmanagement;

import rate.usermanagement.LocationInfo;

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

class RatedUserInfo {
	String name;
	String image;
	long totalrate;
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

class Rate {
	String rater;
	String ratee;
	long rate;
}