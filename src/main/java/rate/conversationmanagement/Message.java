package rate.conversationmanagement;

import java.util.Date;
/**
 * This class represents a new message
 * @author Mingrui Lyu
 * @version 1.0
 */
public class Message {
	String sender;
	String receiver;
	Date date;
	String body;
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSender() {
		return this.sender;
	}
	public String getReceiver() {
		return this.receiver;
	}
	public Date getDate() {
		return this.date;
	}
	public String getBody() {
		return this.body;
	}
}
