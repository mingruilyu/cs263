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
<%@ page import="ratingapp.usermanagement.UserManager" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
	String username = (String)pageContext.getSession().getAttribute("user");
	//System.out.println(request.getAttribute("usermanager"));
	if (username != null) {
		UserManager usermanager = UserManager.getUserHandler(username);
%>
		<a href = "/logout">sign out</a>
		<h>Welcome to Rating App, <%= user %></h>
		<h1>You can do the following things:</h1>
		<h2>1.	</h2>
<%
	}
	else { %>
		<a href = "/login.jsp">sign in</a>
	   	
	<%}
%>
</body>
</html>