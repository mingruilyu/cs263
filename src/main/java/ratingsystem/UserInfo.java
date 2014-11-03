package ratingsystem;

public class UserInfo {
	String name;
	boolean gender; // true = male
	String location;
	String email;
	String birthdate;
	int rate; 
	boolean ratingpermission;// true if being rated is allowed
	String image; // profile image URL
	public enum propertyno {
		name(1), 
		gender(2), 
		location(3), 
		email(4), 
		birthdate(5), 
		rating(6),
		image(7),
		ratepermission(8);
		private Integer no;
		propertyno(int no) {
			this.no = no;
		}
	}
	public void setRatingPermission(boolean permission) {
		this.ratingpermission = permission;
		return;
	}
	public boolean isRatingAllowed() {
		return ratingpermission;
	}
	public void setGender(boolean gender) {
		this.gender = gender;
		return;
	}
	public String getGender() { 
		return this.gender ? "male" : "female";
	}
	public void setLocation(String location) {
		this.location = location;
		return;
	}
	public String getLocation() {
		return this.location;
	}
	public void setBirthDate(String birthdate) {
		this.birthdate = birthdate;
		return;
	}
	public String getBirthDate() {
		return this.birthdate;
	}

	public void setName(String name) {
		this.name = name;
		return;
	}
	public String getName() {
		return this.name;
	}
	public void setEmail(String email) {
		this.email = email;
		return;
	}
	public String getEmail() {
		return this.email;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public int getRate() {
		return this.rate;
	}
	public void setProfileImage(String url) {
		this.image = url;
	}
	public String getProfileImage() {
		return image;
	}
	
}
