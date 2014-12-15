<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.conversationmanagement.PostProcessor"%>
<%@ page import="rate.conversationmanagement.Post"%>
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
	UserManager rateemanager = null;
	String ratee = null;
	if (username != null) {	%>
<a href = "/rest/log/logout">sign out</a><br>
<a href = "/welcome.jsp">go back to home</a>
<% 	
		ratee = request.getParameter("ratee");
		if (ratee != null) {
			rateemanager = UserManager.getUserHandler(ratee);%>
<img src = <%= rateemanager.user.getProfileImage() %> height = "200" width = "200">
<table>
	<tr><td>name</td><td><%=rateemanager.user.getName() %> </td></tr>
	<tr><td>gender</td><td><%=rateemanager.user.getGender() %> </td></tr>
	<tr><td>birthdate</td><td><%=rateemanager.user.getBirthDate() %></td></tr>
	<tr><td>rate</td><td><%=rateemanager.user.getRate() %></td></tr>
</table>
<p>You can rate <%= ratee %> from 0 to 100. <br>
	The minimum increment is 20 points.<br>
	you two can only start conversation <br>
	if you both rate each other with over 80(80 included)<br>
	<%= ratee %> won't not be able to see your rate</p>
<input id = "rateselect" type = "number" name = "rate" min = "0" max = "100" step = "20" value = "60"><br>
<button id ="submitrate">rate</button>
<h2>His/Her Posts</h2>
<% 	
			List<Post> postList = PostProcessor.getPost(rateemanager.user.getName());
			for (Post post : postList) {
				if (post.getContents() != null) { %>
<p>Date: <%=post.getDate() %></p>
<p><%=post.getContents() %></p>
<%				} 
				if (post.getImage() != null) { %>
<img src = "<%=post.getImage()%>" height = "200" width = "200"><br>
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