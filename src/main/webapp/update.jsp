<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.blobstore.BlobKey"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="rate.usermanagement.UserManager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"type="text/javascript" ></script>
<title>Insert title here</title>
</head>
<body>		
<%
			String username = (String)pageContext.getSession().getAttribute("user");
			BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
			if (username != null) {
				UserManager usermanager = UserManager.getUserHandler(username);
%>	
	<a href = "/rest/log/logout">sign out</a><br>
		<a href = "/welcome.jsp">go back to home</a>
		<form action = "<%= blobstore.createUploadUrl("/upload") %>"  method="post" enctype="multipart/form-data">
			<input type="file" name="image">
		    <input type="submit" value="Submit">
		</form>
		
		<form action = "/welcome.jsp" method = "post" id = "info"></form>
		<table>
		<tr>
			<td>Profile image</td>
			<td><input type = "text" name = "image" form = "info" value = "<%= "/upload?blobkey=" + request.getParameter("blobkey")%>"></td>
		</tr>
		<tr>
			<td>email</td>
			<td><input type = "text" name = "email" form = "info" value = "<%= usermanager.user.getEmail()%>"/></td>
		</tr>
		<tr>
			<td>birthdate(MM/DD/YYYY)</td>
			<td><input type = "text" name = "birthdate" form = "info" value = "<%= usermanager.user.getBirthDate()%>"/></td>
		</tr>
		<tr>
			<td>Hobby</td>
			<td><input type = "text" name = "hobby" form = "info" value = "<%= usermanager.user.getHobby()%>"></td>
		</tr>
		<tr>
			<td>Club</td>
			<td><input type = "text" name = "club" form = "info" value = "<%= usermanager.user.getClub()%>"></td>
		</tr>
		<tr>
			<td>School</td>
			<td><input type = "text" name = "school" form = "info" value = "<%= usermanager.user.getSchool()%>"></td>
		</tr>
		<tr>
			<td>Occupation</td>
			<td><input type = "text" name = "occupation" form = "info" value = "<%= usermanager.user.getOccupation()%>"></td>
		</tr>
		<tr>
			<td>Motto</td>
			<td><input type = "text" name = "motto" form = "info" value = "<%= usermanager.user.getMotto()%>"></td>
		</tr>
		</table>
		<button id = "update">update</button>
<% } else {%>
<a href = "/login.jsp">sign in</a><br>
<%	}
%>
		<script type="text/javascript">
			$(document).ready(submitEvent);
			function submitEvent() {
				$("#update").click(submitRequest);
			}
			function submitRequest() {
				var userform = document.getElementById("info");
				var userObj;
				$.urlParam = function(name){
				    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
				    if (results==null){
				       return null;
				    }
				    else{
				       return results[1] || 0;
				    }
				}
				if ($.urlParam('blobkey') == null) {
					userObj = {
							email : userform.elements[1].value,
							birthdate : userform.elements[2].value,
							hobby : userform.elements[3].value,
							club : userform.elements[4].value,
							school : userform.elements[5].value,
							occupation : userform.elements[6].value,
							motto : userform.elements[7].value
						};
				}else {
					userObj = {
						image : userform.elements[0].value,
						email : userform.elements[1].value,
						birthdate : userform.elements[2].value,
						hobby : userform.elements[3].value,
						club : userform.elements[4].value,
						school : userform.elements[5].value,
						occupation : userform.elements[6].value,
						motto : userform.elements[7].value
					};
				}
				var userJson = JSON.stringify(userObj);
				console.log(userJson);
				// AJAX code to submit form.
				$.ajax({
					type: 'POST',
					url: '/rest/update',
					data: userJson,
					async: false,
					contentType: 'application/json',
					success: function(result) {
						alert("Update personal information succeeded!");
					},
					error: function (response) {
						alert("Update personal information failed!");
					}
				});
			}
		</script>
</body>
</html>