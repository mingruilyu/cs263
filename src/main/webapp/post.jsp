<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.usermanagement.UserInfo"%>
<%@ page import="rate.postmanagement.PostProcessor"%>
<%@ page import="rate.postmanagement.Post"%>
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
		BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
		UserInfo usermanager = UserManager.getCachedUserInfo(username);%>
<a href = "/rest/log/logout">sign out</a><br>
<a href = "/welcome.jsp">go back to home</a>
<% 	
	List<Post> postList = PostProcessor.getPost(username);
	for (Post post : postList) {
		if (post.getContents() != null) {
		 %>
<p><strong>Date: <%=post.getDate() %></strong></p>
<p><%=post.getContents() %></p>
<%			
		} 
		if (post.getImage() != null) { %>
<img src = "<%=post.getImage()%>" height = "200" width = "200"><br><br>
<%
		}
	}
%>
<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>
<h2>Post new comments</h2>
<% 
	String blobkey;
	if ((blobkey = request.getParameter("blobkey")) != null) { %>
<img src = "<%="/upload?blobkey=" + blobkey%>" height = "200" width = "200">
<%	
	} 
%>
<form action = "<%= blobstore.createUploadUrl("/upload") %>"  method="post" enctype="multipart/form-data">
	<input type="file" name="image">
	<input type="submit" value="Submit">
</form>
<textarea id="content" rows="3" cols="60"></textarea>
<button id = "publish">publish</button>
<%}
	else {	%>
<a href = "/login.jsp">sign in</a>
<%}%>
<script type="text/javascript">
			$(document).ready(publish);
			function publish() {
				$("#publish").click(publishRequest);
			}
			function publishRequest() {
				var publication;
				$.urlParam = function(name){
				    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
				    if (results==null){
				       return null;
				    }
				    else{
				       return results[1] || 0;
				    }
				}
				publication = {
					image : "/upload?blobkey=" + $.urlParam('blobkey'),
					contents : $("#content").val()
				};
				if (publication.image == null && publication.text == null)
					alert("Image and content cannot be both null!");
				else {
					var userJson = JSON.stringify(publication);
					console.log(userJson);
					// AJAX code to submit form.
					$.ajax({
						type: 'POST',
						url: '/post/post/postnew',
						data: userJson,
						async: false,
						//dataType: "json",
						contentType: 'application/json',
						success: function(result) {
							alert("Posted new status!");
							var url = "/post.jsp";    
							$(location).attr('href',url);
						},
						error: function (response) {
							alert("fail");
						}
					});
				}
			}
		</script>
</body>
</html>