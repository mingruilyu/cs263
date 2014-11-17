<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "rate.usermanagement.FacebookLoginRequest" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"type="text/javascript" ></script>
<title>Insert title here</title>
</head>
<body>
	<h1>Choose your login approach:</h1>
	<h2>1.Login to Facebook from GAERatingApp if you first sign up with Facebook</h2><br>
	<a href = "<%=FacebookLoginRequest.getFacebookAuthURL()%>">Redirect to Facebook to login in</a>
	<h2>2.Login locally from this website</h2>
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
		// AJAX code to submit form.
		var userForm = document.getElementById("form");
		var username = userForm.elements[0].value;
		var password = userForm.elements[1].value;
		var url = "/rest/log/directlogin?username=" + username + "&password=" + password;    
		$(location).attr('href',url);
		/*$.ajax({
			type: 'get',
			url: '/rest/login/directlogin',
			data: "username=" + username + "&password=" + password,
			async: false,
			success: function(result) {
				console.log(result);
				if (result == "nouser")  {
					alert("no such user!");
				}
				else if (result == "fblogin"){
					alert("login from facebook!");
				}
				else if (result == "wrongpsw")
					alert("wrong password!");
				else {

				}
			},
			error: function (response) {
				alert("fail");
			}
		});*/
	}
	</script>
	<h2>3.Sign up as a new user</h2>
	<a href = "/signup.html">click here to sign up</a>
</body>
</html>