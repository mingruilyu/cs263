<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.usermanagement.UserInfo"%>
<%@ page import="rate.conversationmanagement.PostProcessor"%>
<%@ page import="rate.conversationmanagement.Post"%>
<%@ page import="com.google.appengine.api.blobstore.BlobKey"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
</head>
<body>
<%
String username = (String)pageContext.getSession().getAttribute("user");
	if (username != null) {
		String talkee = request.getParameter("talkee");
		UserInfo usermanager = UserManager.getCachedUserInfo(talkee);%>
<a href = "/rest/log/logout">sign out</a><br>
<a href = "/welcome.jsp">go back to home</a>
<h2><%=talkee %>'s infomation</h2>
<p><%=usermanager.getName() %> </p>
<img height="200" width="200" src = "<%=usermanager.getProfileImage()%>">
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
<h2><%=talkee %>'s posts</h2>
<% 	
	List<Post> postList = PostProcessor.getPost(talkee);
		for (Post post : postList) {
			if (post.getContents() != null) { %>
<p>Date: <%=post.getDate() %></p>
<p><%=post.getContents() %></p>
<%			
			} 
			if (post.getImage() != null) { %>
<img src = "<%=post.getImage()%>" height = "200" width = "200"><br>
<%
			}
		}
	}
	else {	%>
<a href = "/login.jsp">sign in</a>
<%}%>
</body>
</html>