<!DOCTYPE html>
<%@page import="iTrade.User"%>
<html>
<%
	String username = (String) request.getSession()
			.getAttribute("user");
%>
<head>
<script type="text/javascript" src="index.js"></script>
<link rel="stylesheet" href="index.css">
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script type="text/javascript" src="sha/sha.js"></script>
</head>
<body onload="ifunds();">
<div id="status" style="text-align: center; width: 100%"></div>
</br>
	<%
		if (username == null) {
	%>
	<div style="margin: auto; position: relative; width: 600px; font-size: 16pt; text-align: center; color: white;">
		Username: <input class="create" id="user" type="text"> Password: <input
			class="create" id="pass" type="password"> <input
			class="create" type="submit" value="Sign In" onclick="login();">
	</div>
		<br>

	<div id="container"
		style="position: relative; height: 90%; width: 90%; margin: auto; border-radius: 10px;">
		<div
			style="width: 100%; position: relative; display: flex; align-items: center; background: cadetblue;">
			<div class="tabs"  style="background-color: #F1AB00;" onclick="funds();">Funds</div>
			<div class="tabs" style="background-color: #CD1E10;" onclick="brokers();">Brokers</div>
			<div class="tabs" style="background-color: #007E3A;" onclick="equity();">Equity</div>
			<div class="tabs" style="background-color: #FADF00;" onclick="userData();">Sign Up</div>
		</div>
		<div id="brokers"></div>
	</div>
	<%
		} else {
	%>
	<div style="margin: auto; position: relative; width: 25%; text-align: center; font-size: 16pt; color: white;">
		Hello,
		<%=username%>
		<input class="create" type="submit" value="Sign Out"
			onclick="logout();">
	</div>
	<div id="balance" class="hidden">
		<%=User.getBalance(request)%></div>
			<br>

	<div id="container"
		style="position: relative; height: 90%; width: 90%; margin: auto; border-radius: 10px;">
		<div
			style="width: 100%; position: relative; display: flex; align-items: center; background: cadetblue;">
			<div class="tabs" style="background-color: #F1AB00;" onclick="funds();">Funds</div>
			<div class="tabs" style="background-color: #CD1E10;" onclick="brokers();">Brokers</div>
			<div class="tabs" style="background-color: #007E3A;" onclick="equity();">Equity</div>
			<div class="tabs" style="background-color: #FADF00;" onclick="userData();">Transactions</div>
		</div>
		<div id="brokers"></div>
	</div>
	<%
		}
	%>
</body>

</html>