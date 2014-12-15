<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.conversationmanagement.MessageReader"%>
<%@ page import="rate.conversationmanagement.Message"%>
<%@ page import="rate.conversationmanagement.ConversationManager"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
String username = (String)pageContext.getSession().getAttribute("user");
String conversationId = (String)request.getParameter("id");
String sender = username;
String talkee = ConversationManager.getSender(conversationId, username);
String infourl = "/personalinfo.jsp?talkee=" + talkee;
List<Message> messageList;
	if (username != null) {
		//UserManager usermanager = UserManager.getUserHandler(username);%>
<a href = "/rest/log/logout">sign out</a><br>
<a href = "/welcome.jsp">go back to home</a><br>
<a href = <%=infourl %>>see <%= talkee %> 's profile</a>
<%
		messageList = MessageReader.getMessageList(conversationId);
		String align = "left";
		for (Message message : messageList) {
			if (message.getSender().equals("admin") && !message.getReceiver().equals(username)) continue;
			align = message.getSender().equals(sender) ? "left" : "center"; 
			out.println("<div align="+ align + "><p>Sender:\t" + message.getSender() 
						+ "\t\tReceiver\t" + message.getReceiver() + "</p></div>");
			out.println("<div align="+ align + "><p>Content: " + message.getBody() + "</p>");
		}
%>
<div align = "left">
<textarea id="message" name="content" rows="3" cols="60"></textarea>
<button id="send">Send</button>
</div>
<%
	}
	else {	%>
<a href = "/login.jsp">sign in</a>
<%	}
%>
<script type="text/javascript">
	$(document).ready(submitEvent);
	function submitEvent() {
		$("#send").click(submitRequest);
	}
	function submitRequest() {
		var msgObj = {
			content : $("#message").val()
		};
		var msgJson = JSON.stringify(msgObj);
		console.log(msgJson);
		// AJAX code to submit form.
		$.ajax({
			type: 'POST',
			url: '/conversation/conversation/send/' + "<%= conversationId %>",
			data: msgJson,
			async: false,
			//dataType: "json",
			contentType: 'application/json',
			success: function(result) {
				var url = window.location.href;    
				$(location).attr('href',url);
			},
			error: function (response) {
				alert("fail");
			}
		});
	}
</script>
</body>
</html>