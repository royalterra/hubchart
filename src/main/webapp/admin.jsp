<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%

%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Hubchart - Hubzilla grid statistics</title>
	
	<!-- Bootstrap -->
	<link href="css/bootstrap.min.css" rel="stylesheet" />
	<link href="css/custom.css" rel="stylesheet" />
	<link href="images/hz-16.png" rel="shortcut icon" type="image/png" />
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
				<img src="images/logo_hubzilla_400.png" align="middle" /><br />
				&nbsp;<br />
				Administration panel<br />
			</div>
		</div>
		
		<div class="row">
			<form action="/forcejob">
				<div class="col-sm-1">
					<img src="images/home-32.png" align="middle" />
				</div>
				<div class="col-sm-5">
					Force hub list <b>polling</b>
					<input type="hidden" name="name" value="poll"></input>
				</div>
				<div class="col-sm-5">
					Access key
					<input type="text" name="accessKey"></input>
				</div>
				<div class="col-sm-1">
					<button type="submit" title="Go"></button>
				</div>
			</form>
		</div>
			
		
	</div>
	<!-- /container -->

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="jquery/1.11.1/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
</body>
</html>