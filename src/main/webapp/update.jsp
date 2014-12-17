<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.google.appengine.api.blobstore.BlobKey"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.usermanagement.UserInfo"%>
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
				UserInfo usermanager = UserManager.getCachedUserInfo(username);
%>	
	<a href = "/rest/log/logout">sign out</a><br>
		<a href = "/welcome.jsp">go back to home</a><br>
		<h2>Update Profile Information</h2>
		<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="80%" color=#987cb9 SIZE=3>
		<h3>You can upload a new profile picture<br>
		To upload file, click "Choose File" to select image and then click "Submit". <br>
		After you have filled in all other tables, click "update" to update your profile<br>
		To use URL, make sure you put into the table a valid image URL</h3>
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
			<td><input type = "text" name = "email" form = "info" value = "<%= usermanager.getEmail()%>"/></td>
		</tr>
		<tr>
			<td>Hobby</td>
			<td><input type = "text" name = "hobby" form = "info" value = "<%= usermanager.getHobby()%>"></td>
		</tr>
		<tr>
			<td>Club</td>
			<td><input type = "text" name = "club" form = "info" value = "<%= usermanager.getClub()%>"></td>
		</tr>
		<tr>
			<td>School</td>
			<td><input type = "text" name = "school" form = "info" value = "<%= usermanager.getSchool()%>"></td>
		</tr>
		<tr>
			<td>Occupation</td>
			<td><input type = "text" name = "occupation" form = "info" value = "<%= usermanager.getOccupation()%>"></td>
		</tr>
		<tr>
			<td>Motto</td>
			<td><input type = "text" name = "motto" form = "info" value = "<%= usermanager.getMotto()%>"></td>
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
				var userObj = {};
				$.urlParam = function(name){
				    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
				    if (results==null){
				       return null;
				    }
				    else{
				       return results[1] || 0;
				    }
				}
				if ($.urlParam('blobkey') != null) 
					userObj["image"] = userform.elements[0].value;
				if (userform.elements[1].value != "null")
					userObj["email"] = userform.elements[1].value;
				if (userform.elements[2].value != "null")
					userObj["hobby"] = userform.elements[2].value;
				if (userform.elements[3].value != "null")
					userObj["club"] = userform.elements[3].value;
				if (userform.elements[4].value != "null")
					userObj["school"] = userform.elements[4].value;
				if (userform.elements[5].value != "null")
					userObj["occupation"] = userform.elements[5].value;
				if (userform.elements[6].value != "null")
					userObj["motto"] = userform.elements[6].value;
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