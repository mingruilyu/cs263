<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="rate.usermanagement.UserManager"%>
<%@ page import="rate.conversationmanagement.MessageReader"%>
<%@ page import="rate.conversationmanagement.Message"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
String username = (String)pageContext.getSession().getAttribute("user");
String conversationId = (String)request.getParameter("id");
String sender = null, receiver = null;
List<Message> messageList;
	if (username != null) {
		//UserManager usermanager = UserManager.getUserHandler(username);%>
<a href = "/rest/log/logout">sign out</a>
<%
		messageList = MessageReader.getMessageList(conversationId);
		
		if (messageList.size() != 0){
			sender = messageList.get(0).getSender();
			receiver = messageList.get(0).getReceiver();
		}
		for (Message message : messageList) {
			out.println("<p>Sender:\t" + message.getSender() + "\t\tReceiver\t" + message.getReceiver() + "</p>");
			out.println("<p>Content: " + message.getBody() + "</p><br>");
		}
%>
<textarea id="message" name="content" rows="3" cols="60"></textarea>
<button id="send">Send</button>
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
			sender : "<%= sender %>",
			receiver : "<%= receiver %>",
			content : $("#message").text()
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
				alert("send successfully");
			},
			error: function (response) {
				alert("fail");
			}
		});
	}
</script>
</body>
</html>