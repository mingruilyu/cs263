<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "rate.usermanagement.FacebookLoginRequest" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"type="text/javascript" ></script>
<script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false"></script>
<title>Insert title here</title>
</head>
<body>
	<h1>Welcome to LET'S DATE application! Choose your login approach:</h1>
	<h2>1.Login  from Facebook if you first sign up with Facebook</h2><br>
	<a href = "<%=FacebookLoginRequest.getFacebookAuthURL()%>">Redirect to Facebook to login in</a>
	<h2>2.Login locally from this website</h2>
<%	String error = request.getParameter("error");
	if (error!= null) {
	if (error.equals("1")) {
%>
<font color = "red">Please login from Facebook!</font>
<%} else if (error.equals("2")){ %>
<font color = "red">Wrong password!</font>
<% } else if (error.equals("3")) {%>
<font color = "red">User does not exist! Please signup first!</font>
<%} }%>
	<form action = "/rest/log/directlogin" method = "get" id = "form">
		<div><input type = "text" name = "username"/>username</div>
		<div><input type = "password" name = "password"/>password</div>
	</form>
	<button id = "submitbutton">submit</button>
	<script type="text/javascript">
	$(document).ready(submitEvent);
	function submitEvent() {
		$("#submitbutton").click(submitRequest);
	}
	
	function submitRequest() {
		var userForm = document.getElementById("form");
		var username = userForm.elements[0].value;
		var password = userForm.elements[1].value;
		var url = "/rest/log/directlogin?username=" + username + "&password=" + password;
		$(location).attr('href',url);
	}
	</script>
	<h2>3.Sign up as a new user</h2>
	<a href = "/signup.html">click here to sign up</a>
</body>
</html>