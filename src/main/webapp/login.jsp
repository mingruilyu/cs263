<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "facebook.facebook.LoginRequest" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h1>Choose your login approach:</h1>
	<h2>1.Login to Facebook from GAERatingApp if you first sign up with Facebook</h2><br>
	<a href = "<%=LoginRequest.getFacebookAuthURL()%>">Redirect to Facebook to login in</a>
	<h2>2.Login locally from this website</h2>
	<form action = "/locallogin" method = "get">
		<div><input type = "text" name = "username"/>username</div>
		<div><input type = "password" name = "password">password</div>
		<div><input type = "submit"></div>
	</form>
	<h2>3.Sign up as a new user</h2>
	<a href = "/signup.html">click here to sign up</a>
</body>
</html>