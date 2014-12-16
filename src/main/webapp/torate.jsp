<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.usermanagement.UserInfo"%>
<%@ page import="rate.postmanagement.PostProcessor"%>
<%@ page import="rate.postmanagement.Post"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<title>Insert title here</title>
</head>
<body>
<%
	String username = (String)pageContext.getSession().getAttribute("user");
	UserInfo rateemanager = null;
	String ratee = null;
	if (username != null) {	%>
<a href = "/rest/log/logout">sign out</a><br>
<a href = "/welcome.jsp">go back to home</a><br><br>
<% 	
		ratee = request.getParameter("ratee");
		if (ratee != null) {
			rateemanager = UserManager.getCachedUserInfo(ratee);%>
<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>
<img src = <%= rateemanager.getProfileImage() %> height = "200" width = "200">
<table>
	<tr><td>name</td><td><%=rateemanager.getName() %> </td></tr>
	<tr><td>gender</td><td><%=rateemanager.getGender() %> </td></tr>
<%	if (rateemanager.getEmail() != null) {	%>
	<tr><td>email</td><td><%= rateemanager.getEmail()%></td></tr>
<%	} 
	if (rateemanager.getBirthDate() != null) {	%>
	<tr><td>birthdate</td><td><%=rateemanager.getBirthDate() %></td></tr>
<%	} 
	if (rateemanager.getHobby() != null) {	%>
	<tr><td>hobby</td><td><%=rateemanager.getHobby() %></td></tr>
<%	} 
	if (rateemanager.getClub() != null) {	%>
	<tr><td>club</td><td><%=rateemanager.getClub() %></td></tr>
<%	} 
	if (rateemanager.getSchool() != null) {	%>
	<tr><td>school</td><td><%=rateemanager.getSchool() %></td></tr>
<%	} 
	if (rateemanager.getMotto() != null) {	%>
	<tr><td>motto</td><td><%=rateemanager.getMotto() %></td></tr>
<%	} 
	if (rateemanager.getOccupation() != null) {	%>
	<tr><td>occupation</td><td><%=rateemanager.getOccupation() %></td></tr>
	<%	} %>
</table>
<p><strong><font color = "red">
	You can rate <%= ratee %> from 0 to 100. <br>
	The minimum increment is 20 points.<br>
	you two can only start conversation <br>
	if you both rate each other with over 80(80 included)<br>
	<%= ratee %> won't not be able to see your rate</font></strong></p>
<input id = "rateselect" type = "number" name = "rate" min = "0" max = "100" step = "20" value = "60">
<button id ="submitrate">rate</button>
<h2><%= rateemanager.getName()%>'s Posts</h2>
<% 	
			List<Post> postList = PostProcessor.getPost(rateemanager.getName());
			for (Post post : postList) {
				if (post.getContents() != null) { %>
<p><strong>Date: <%=post.getDate() %></strong></p>
<p><%=post.getContents() %></p>
<%				} 
				if (post.getImage() != null) { %>
<img src = "<%=post.getImage()%>" height = "200" width = "200"><br><br>
<%				}
			}
		}
	}
	else {	%>
<a href = "/login.jsp">sign in</a>
<%	}%>

	<script type="text/javascript">
	$(document).ready(function() {
		$("#submitrate").click(submitRate);
	});
	
	function submitRate() {
		var rateObj = {
			rater : "<%= (String)session.getAttribute("user") %>",
			ratee : "<%= ratee%>",
			rate  : $("#rateselect").val()
		};
		console.log(rateObj);
		$.ajax({
			type: 'POST',
			url: '/rating/rating/addrate',
			data: JSON.stringify(rateObj),
			async: false,
			contentType: "application/json",
			success: function(result) {
				var url = "/rating.jsp?view=1";
				$(location).attr('href',url);
			},
			error: function (response) {
				alert("fail");
			}
		});
	}
	</script>
</body>
</html>