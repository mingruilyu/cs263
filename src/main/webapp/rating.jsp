<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"type="text/javascript" ></script>
</head>
<body>
<%String username = (String)pageContext.getSession().getAttribute("user");
if (username != null) {
	UserManager usermanager = UserManager.getUserHandler(username);%>
	<a href = "/welcome.jsp">go back to home</a>
	<a href = "/rest/log/logout">sign out</a><br>
	<a id = "switchlink"></a><br>
	<img id = "image">
	<p id = "name"></p>
	<p id = "rate"></p>
	<p id = "displayrate">you need to rate before you can see the rate for the user!</p>
	<input id = "rateselect" type = "number" name = "rate" min = "0" max = "100" step = "5" value = "50">
	<button id ="submitrate">rate</button>
	<button id ="next">next</button>
<% } else {%>
<a href = "/login.jsp">sign in</a><br>
<%	}
%>
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
	var offset = 0;
	var ratee;
	getNext();
	
	function loadInfo() {
		if (view == 0)  {
			$("#switchlink").text("see the people I have rated");
			$("#switchlink").attr("href", "/rating.jsp?view=1");
			$("#submitrate").click(submitRate);
		}
		else {
			$("#switchlink").text("see the people I have not rated");
			$("#switchlink").attr("href", "/rating.jsp?view=0");
			$("#submitrate").hide();
			$("#rateselect").hide();
			$("#rate").hide();
			$("#displayrate").hide();
		}
		// events
		$("#next").click(getNext);
	}
	
	function submitRate() {
		var rateObj = {
			rater : "<%= (String)session.getAttribute("user") %>",
			ratee : $("#name").text(),
			rate  : $("#rateselect").attr("value")
		};
		$.ajax({
			type: 'POST',
			url: '/rating/rating/addrate',
			data: JSON.stringify(rateObj),
			async: false,
			contentType: "application/json",
			//dataType: "json",
			success: function(result) {
				alert("the average rate of this person is " + result);
				$("#rate").text(result.rate);
				$("#rate").show();
			},
			error: function (response) {
				alert("fail");
			}
		});
	}
	
	function getNext() {
		// AJAX code to submit form.
		console.log(offset);
		if (view == 0) getNextUnrated();
		else getNextRated();
	}
	
	function getNextRated(){
		$.ajax({
			type: 'GET',
			url: '/rating/rating/rated',
			data: "offset=" + offset,
			async: false,
			//dataType: "json",
			dataType: 'json',
			success: function(result) {
				//console.log(result.name);
				console.log(result);
				ratee = result.name;
				$("#image").attr("src", result.image);
				$("#name").text(result.name);
				$("#rate").text(result.rate);
				offset = parseInt(result.offset);
				console.log("offset =" + result);
			},
			error: function (response) {
				alert("fail");
			}
		});
	}

	function getNextUnrated(){
		console.log(offset);
		$.ajax({
			type: 'GET',
			url: '/rating/rating/unrated',
			data: "offset=" + offset,
			async: false,
			dataType: 'json',
			success: function(result) {
				if (result == null) {
					$("#displayrate").text("you have rated all the users!");
					$("#rateselect").hide();
					$("#image").hide();
					$("#name").hide();
					$("#rate").hide();
					$("#submitrate").hide();
					$("#next").hide();
				}					
				else {
					console.log(result.name);
					offset = parseInt(result.offset);
					$("#image").attr("src", result.image);
					$("#name").text(result.name);
					$("#submitrate").show();
					$("#rateselect").show();
					$("#rate").show();
					$("#displayrate").show();
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