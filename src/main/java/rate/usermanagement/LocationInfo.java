package rate.usermanagement;
/**
 * This class contains the location information of the current user
 * @author Mingrui Lyu
 * @version 1.0
 */
public class LocationInfo {
	double latitude;
	double longitude;
	String city;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
}
