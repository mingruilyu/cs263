package facebook.facebook;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

public class FacebookServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String token = (String) request.getParameter("token");
		PrintWriter writer = response.getWriter();
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
		List<User> users = myFriends.getData();
		User thisuser = facebookClient.fetchObject("me", User.class);
		writer.print("<table><tr><th>Photo</th><th>Name</th><th>Id</th></tr>");
		//writer.print("<p>My lastname is " + user.getLastName() + "</p>");

		//writer.print("<p>My id is " + user.getLastName() + "</p>");
		
		writer.print("<p>My name is " + thisuser.getName() + "</p>");
		writer.print("<p>My gender is " + thisuser.getGender() + "</p>");
		writer.print("<p>My link is " + thisuser.getLink() + "</p>");
		writer.print("<p>My Locale is " + thisuser.getLocale() + "</p>");
		writer.print("<p>My id is " + thisuser.getId() + "</p>");
		writer.print("<p>My email is " + thisuser.getEmail() + "</p>");
		writer.print("<p>My birthday is " + thisuser.getBirthday() + "</p>");
		writer.print("<p>My hometown is " + thisuser.getHometown() + "</p>");
		writer.print("<img src=\"https://graph.facebook.com/" + thisuser.getId() + "/picture?type=large\"/>");
		for(Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
	        User user = (User)iterator.next();
	        writer.print("<tr><td>"	+ user.getName() + "</td><td>" + user.getId() + "</td><td>" + 
	        		"<img src=\"https://graph.facebook.com/" + user.getId() + "/picture?type=large\"/>"
					+ "</td></tr>");
	    }
		writer.print("</table>");
		writer.close();
	}
}
