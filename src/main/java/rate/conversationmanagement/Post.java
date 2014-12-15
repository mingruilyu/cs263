package rate.conversationmanagement;

import java.util.Date;

public class Post {
	String image;
	String contents;
	Date date;
	String poster;
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public Post() {
	}
	public Post(String image, String contents, Date date) {
		this.image = image;
		this.contents = contents;
		this.date = date;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
