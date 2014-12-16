<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.usermanagement.UserInfo"%>
<%@ page import="java.util.*" %>

<html>
<head>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script src="http://www.google.com/jsapi"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>welcome</title>
</head>
<body>
<%
String username = (String)pageContext.getSession().getAttribute("user");
	if (username != null) {
		UserInfo usermanager = UserManager.getCachedUserInfo(username);%>
<a href = "/rest/log/logout">sign out</a>
<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>
<h1>Welcome to LET'S DATE, <%=usermanager.getName()%></h1>
<img height="200" width="200" src = "<%=usermanager.getProfileImage()%>">
<p>you login location is</p>
<p id = "location"><%=UserManager.getLocation(username) %></p>

<% 		if (!UserManager.infoCompletionCheck(usermanager)) {%>
<h2>Your infomation is not completed, please complete your profile before you start rating</h2>
<h2>You can be rated by the others only after you upload your profile picture!</h2>
<%		}%>
<table>
		
	<tr><td>gender</td><td><%=usermanager.getGender() %> </td></tr>
	<tr><td>rate</td><td><%=usermanager.getRate() %></td></tr>
<%	if (usermanager.getEmail() != null) {	%>
	<tr><td>email</td><td><%= usermanager.getEmail()%></td></tr>
<%	} 
	if (usermanager.getBirthDate() != null) {	%>
	<tr><td>birthdate</td><td><%=usermanager.getBirthDate() %></td></tr>
<%	} 
	if (usermanager.getHobby() != null) {	%>
	<tr><td>hobby</td><td><%=usermanager.getHobby() %></td></tr>
<%	} 
	if (usermanager.getClub() != null) {	%>
	<tr><td>club</td><td><%=usermanager.getClub() %></td></tr>
<%	} 
	if (usermanager.getSchool() != null) {	%>
	<tr><td>school</td><td><%=usermanager.getSchool() %></td></tr>
<%	} 
	if (usermanager.getMotto() != null) {	%>
	<tr><td>motto</td><td><%=usermanager.getMotto() %></td></tr>
<%	} 
	if (usermanager.getOccupation() != null) {	%>
	<tr><td>occupation</td><td><%=usermanager.getOccupation() %></td></tr>
	<%	} %>
</table>
<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>
	<% List<String> conversationList = UserManager.getConversationList(username);
		// get the conversation list
		if (conversationList.size() == 0) {
			out.println("<h1>You currently have no conversation</h1>");
		}
		else {
			int count = 0;
			out.println("<h1>You have started conversation with " + conversationList.size() + " people</h1>");
			for (String id : conversationList) {
				count ++;
				out.println("<a href = \"/conversation.jsp?id=" + id + "\">" +"conversation" + count + "</a><br>");
			}
		}%>
<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>
<h1>You can also do the following things</h1>
<a href = "/update.jsp">Complete and modify your profile</a><br>
<a href = "/post.jsp">Post your pictures</a><br>
<a href = "/rating.jsp?view=1">Start the rating</a><br>
<%
	}
	else {	%>
<a href = "/login.jsp">sign in</a>
<%	}
%>

</body>
</html>