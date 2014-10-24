<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="facebook.facebook.LoginRequest" %>
<%@ page import="com.restfb.Connection" %>
<%@ page import="com.restfb.DefaultFacebookClient" %>
<%@ page import="com.restfb.FacebookClient" %>
<%@ page import="com.restfb.types.User" %>
<%@ page import="java.io.PrintWriter" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login Success</title>
</head>
<body>
<% 
	String code = request.getParameter("code");
	//out.println(code);
	String token = LoginRequest.getFacebookAccessToken(code);
	if (token == null) 
		response.sendRedirect("/loginfail");
	else 
		response.sendRedirect("/facebook?token=" + token);
%>

<h>Login to Facebook Successfully!</h>
</body>
</html>