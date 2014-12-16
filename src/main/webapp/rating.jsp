<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"type="text/javascript" ></script>
<script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false"></script>
<script src="http://www.google.com/jsapi"></script>
</head>
<body>
<%	String username = (String)pageContext.getSession().getAttribute("user");
	if (username != null) { %>
<a href = "/welcome.jsp">go back to home</a><br>
<a href = "/rest/log/logout">sign out</a><br>
<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>
<font size = "5"><strong><a id = "switchlink"></a></strong></font><br>
<p id = "displayrate"></p>
<h2>People I have rated</h2>
<div id = "ratedlist"></div>
<div id="googleMap" style="width:1200px;height:1200px;"></div>	
<% 	} else {%>
<a href = "/login.jsp">sign in</a><br>
<%	} %>
	<script type="text/javascript">
	$.urlParam = function(name){
	    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
	    if (results==null){
	       return null;
	    }
	    else{
	       return results[1] || 0;
	    }
	}
	$(document).ready(loadInfo);
	var view = parseInt($.urlParam("view"));
	var ratee;

	function printList(list){
		var print = "";
		for (i = 0; i < list.length; i ++) {
			print += '<table>';
			print += '<tr><td>Name:</td><td>' + list[i].name + '</td></tr>';
			print += '<tr><td>My rate:</td><td>' + list[i].myrate + '</td></tr>';
			print += '<tr><td>Overall rate:</td><td>' + list[i].totalrate + '</td></tr>';
			print += '<tr><td>current location:</td><td>' + list[i].location.city + '</td></tr></table>';
			print += '<img src = ' + list[i].image + ' height = 200 width = 200>' + '<br><br>';
			print += '<HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="100%" color=#987cb9 SIZE=3>';
		}
		$("#ratedlist").append(print);
	}
	
	function loadInfo() {
		if (view == 0)  {
			getUnratedList();
			$("#switchlink").text("see the people I have rated");
			$("#switchlink").attr("href", "/rating.jsp?view=1");
		}
		else {
			getRatedList();
			$("#switchlink").text("see the people I have not rated");
			$("#switchlink").attr("href", "/rating.jsp?view=0");
		}
	}

	function initializeUnratedMap(list) {
		console.log(list);
		var bounds = new google.maps.LatLngBounds();
		var currentlatitude = google.loader.ClientLocation.latitude;
		var currentlongitude = google.loader.ClientLocation.longitude;
		
		var myCenter = new google.maps.LatLng(currentlatitude, currentlongitude);
		var mapProp = {
		  center:myCenter,
		  zoom:1,
		  mapTypeId:google.maps.MapTypeId.ROADMAP
		  };

		var map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
		// place the user's own marker
		var marker;
		var myMarkerCenter=new google.maps.LatLng(currentlatitude, currentlongitude);
		marker = new google.maps.Marker({
				  position: myMarkerCenter,
				  map: map,
				  animation: google.maps.Animation.DROP
				  });
		marker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
		
		var i;
		
		for (i = 0; i < list.length; i ++) {
			var markerCenter=new google.maps.LatLng(list[i].location.latitude, list[i].location.longitude);
			bounds.extend(markerCenter);
			marker = new google.maps.Marker({
				  position: markerCenter,
				  map: map,
				  animation: google.maps.Animation.BOUNCE
				  });
			
			google.maps.event.addListener(marker, 'click', (function(marker, i) {
				var rateInfo = "<p>" + list[i].name + "</p>" + 
							"<img src = '" + list[i].image + "' height = '100' width = '100'><br>" +
							"<a href = " + "/torate.jsp?ratee=" + list[i].name + ">go to rate him / her</a>";
	            return function() {
	            	var infoWindow = new google.maps.InfoWindow({
	            		content: rateInfo,
	            		maxWidth: 200
	            	});
	                infoWindow.open(map, marker);
	            }
	        })(marker, i));
			map.fitBounds(bounds);
		}
	}
	
	function getRatedList(){
		$.ajax({
			type: 'GET',
			url: '/rating/rating/ratedlist',
			async: false,
			dataType: 'json',
			success: function(result) {
				if (result == null)	
					$("#displayrate").text("you have not rated anyone!");
				else {
					printList(result);
				}
			},
			error: function (response) {
				alert("fail");
			}
		});
	}
	function getUnratedList(){
		$.ajax({
			type: 'GET',
			url: '/rating/rating/unratedlist',
			async: false,
			dataType: 'json',
			success: function(result) {
				if (result == null)	
					$("#displayrate").text("you have rated all people!");
				else {
					initializeUnratedMap(result);
				}
			},
			error: function (response) {
				alert("fail");
			}
		});
	}
	</script>
</body>
</html>