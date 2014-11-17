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
<%@ page import="java.util.*" %>

<html>
<head>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script>
  $(function() {
    $( "#dialog" ).dialog();
  });
  </script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
String username = (String)pageContext.getSession().getAttribute("user");
	if (username != null) {
		UserManager usermanager = UserManager.getUserHandler(username);%>
<a href = "/rest/log/logout">sign out</a>
<h1>Welcome to Rating App, <%=usermanager.user.getName()%></h1>
<img src = "<%=usermanager.user.getProfileImage()%>">
<table>
	<tr><td>gender</td><td><%=usermanager.user.getGender() %> </td></tr>
	<tr><td>location</td><td><%= usermanager.user.getLocation()%></td></tr>
	<tr><td>email</td><td><%= usermanager.user.getEmail()%></td></tr>
	<tr><td>birthdate</td><td><%=usermanager.user.getBirthDate() %></td></tr>
	<tr><td>rate</td><td><%=usermanager.user.getRate() %></td></tr>
</table>
	<% List<String> conversationList = usermanager.getConversationList();
		// get the conversation list
		if (conversationList.size() == 0) {
			out.println("<h1>You currently have no conversation</h1>");
		}
		else {
			int count = 0;
			for (String id : conversationList) {
				count ++;
				out.println("<a href = \"/conversation.jsp?id=" + id + "\">" +"conversation" + count + "</a>");
			}
		}%>
<h1>You can also do the following things</h1>
<a href = "/update.jsp">modify your profile</a><br>
<a href = "/rating.jsp?view=1">start the rating</a><br>
<%
	}
	else {	%>
<a href = "/login.jsp">sign in</a>
<%	}
%>
<script type="text/javascript">

</script>
</body>
</html>