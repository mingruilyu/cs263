<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceException" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"type="text/javascript" ></script>
<script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false"></script>
<script src="http://www.google.com/jsapi"></script>
<title>Insert title here</title>
</head>
<body>

<script type="text/javascript">
$(document).ready(localization);
function localization() {
		var loc = {};
		if(google.loader.ClientLocation) {
	        loc.latitude = google.loader.ClientLocation.latitude + Math.random() / 100;
	        loc.longitude = google.loader.ClientLocation.longitude + Math.random() / 100;
	        loc.city = google.loader.ClientLocation.address.city;
	    }
		$.ajax({
			type: 'POST',
			url: '/rest/log/location',
			data: JSON.stringify(loc),
			async: false,
			contentType: "application/json",
			success: function (response) {
				console.log("localization succeed");
				$(location).attr('href',"/welcome.jsp");
			},
			error: function (response) {
				alert("register location fail");
			}
		});
	}
	</script>
</body>
</html>