package rate.usermanagement;

public class UserInfo {
	String name;
	String password;
	boolean gender; // true = male
	String email;
	String birthdate;
	long rate; 
	String image; // profile image URL
	String hobby;
	String club;
	String motto;
	String school;
	String occupation;
	
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getHobby() {
		return hobby;
	}
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
	public String getClub() {
		return club;
	}
	public void setClub(String club) {
		this.club = club;
	}
	public String getMotto() {
		return motto;
	}
	public void setMotto(String motto) {
		this.motto = motto;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public void setGender(boolean gender) {
		this.gender = gender;
		return;
	}
	public String getGender() { 
		return this.gender ? "female" : "male";
	}
	public boolean getBooleanGender() {
		return this.gender;
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
	public void setRate(long rate) {
		this.rate = rate;
	}
	public long getRate() {
		return this.rate;
	}
	public void setProfileImage(String url) {
		this.image = url;
	}
	public String getProfileImage() {
		if (image == "") {
			return "http://blogdailyherald.com/wp-content/uploads/2014/10/wallpaper-for-facebook-profile-photo.jpg";
		}
		else return image;
	}
	
}
