<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.hubzilla.hubchart.business.SettingsBusiness"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
String accessKey = SettingsBusiness.getAccessKey();
request.setAttribute("accessKey", accessKey);
%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Hubchart - Administration panel</title>
	
	<!-- Bootstrap -->
	<link href="css/bootstrap.min.css" rel="stylesheet" />
	<link href="css/custom.css" rel="stylesheet" />
	<link href="images/hubchart1-16.png" rel="shortcut icon" type="image/png" />
	<link href="feed" rel="alternate" type="application/rss+xml" title="Hubzilla Statistics feed" />
	
	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->
</head>
<body>

	<div class="container">
		&nbsp;<br />
		<div class="row">
			<div class="col-sm-7">
				<h1><img src="images/hubchart1-32.png" align="middle" /> hubchart</h1>
				<h4>Administration panel</h4>
			</div>
		</div>
		
		<c:if test="${not empty accessKey}">
		
			&nbsp;<br/>
			&nbsp;<br/>
			<div class="row">
				<form action="forcejob" method="post">
					<div class="col-sm-4">
						<img src="images/home-32.png" align="middle" />
						Launch hub network <b>discovery</b>
						<input type="hidden" name="name" value="discover"></input>
					</div>
					<div class="col-sm-8">
						Access key
						<input type="text" name="accessKey"></input>
						<button type="submit">Go!</button>
					</div>
				</form>
			</div>
			
			<div class="row">
				<form action="forcejob" method="post">
					<div class="col-sm-4">
						<img src="images/home-32.png" align="middle" />
						<b>Enqueue</b> hubs to be polled
						<input type="hidden" name="name" value="enqueue"></input>
					</div>
					<div class="col-sm-8">
						Access key
						<input type="text" name="accessKey"></input>
						<button type="submit">Go!</button>
					</div>
				</form>
			</div>
			
			<div class="row">
				<form action="forcejob" method="post">
					<div class="col-sm-4">
						<img src="images/home-32.png" align="middle" />
						Launch <b>polling</b> of <i>all</i> enqueued hubs
						<input type="hidden" name="name" value="fullPoll"></input>
					</div>
					<div class="col-sm-8">
						Access key
						<input type="text" name="accessKey"></input>
						<button type="submit">Go!</button>
					</div>
				</form>
			</div>
			
			<div class="row">
				<form action="forcejob" method="post">
					<div class="col-sm-4">
						<img src="images/home-32.png" align="middle" />
						<b>Draw</b> graphics and feed with latest stats
						<input type="hidden" name="name" value="draw"></input>
					</div>
					<div class="col-sm-8">
						Access key
						<input type="text" name="accessKey"></input>
						<button type="submit">Go!</button>
					</div>
				</form>
			</div>
		</c:if>
		
		<c:if test="${empty accessKey}">
			<form action="install" method="post">
				Define an access key for administration<br/>
				<i>(it can be read/modified in the settings database table)</i><br/>
				<input type="text" name="accessKey"></input><br/>
				Insert a seed hub <b>base url</b> to start network discovery<br/>
				<input type="text" name="seedHub" value="https://"></input><br/>
				<button type="submit">Save</button>
			</form>
		</c:if>
	</div>
	<!-- /container -->

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="jquery/1.11.1/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
</body>
</html>