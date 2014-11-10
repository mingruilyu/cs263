package ratingapp.usermanagement;
public class UserInfo {
	String name;
	String password;
	boolean gender; // true = male
	String location;
	String email;
	String birthdate;
	int rate; 
	boolean ratingpermission;// true if being rated is allowed
	String image; // profile image URL
	
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
	public boolean getBooleanGender() {
		return this.gender;
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
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
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
