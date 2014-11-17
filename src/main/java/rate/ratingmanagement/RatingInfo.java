package rate.ratingmanagement;

class UnratedUserInfo {
	String name;
	String image;
	long offset;
	UnratedUserInfo(String name, String image, int offset) {
		this.image = image;
		this.name = name;
		this.offset = offset;
	}
}

class RatedUserInfo {
	String name;
	String image;
	long rate;
	int offset;
	RatedUserInfo(String name, String image, long rate, int offset) {
		this.image = image;
		this.name = name;
		this.rate = rate;
		this.offset = offset;
	}
}

class Rate {
	String rater;
	String ratee;
	long rate;
}