<%@page import="it.hubzilla.hubchart.model.Hubs"%>
<%@page import="it.hubzilla.hubchart.business.HubBusiness"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Hubchart - Hubzilla grid statistics</title>
	
	<!-- Bootstrap -->
	<link href="css/bootstrap.min.css" rel="stylesheet" />
	<link href="css/font-awesome.min.css" rel="stylesheet" />
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
		<div class="row">
			<div class="col-sm-12">
				<h1><a href="index.jsp"><img src="images/hubchart1-32.png" align="middle" /></a> hubchart</h1>
				<h4>More data</h4>
			</div>
		</div>

		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<h3>newest hubs</h3>
				<%@ include file="jspf/newestTable.jspf" %>
			</div>
		</div>
		
		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<h3>latest expired hubs</h3>
				<%@ include file="jspf/lastExpiredTable.jspf" %>
			</div>
		</div>
				
		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<h3>languages</h3>
				<%@ include file="jspf/languageTable.jspf"%>
			</div>
		</div>
		
		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<script>
    				document.write('<a href="' + document.referrer + '"><img src="images/hz-16.png" align="middle" />&nbsp;Back to main page</a>');
				</script>
			</div>
		</div>
		
		&nbsp;<br />
		<div class="row">
			<div class="panel panel-default">
				<div class="col-sm-6">
					<%@ include file="jspf/registerBox.jspf"%>
				</div>
				<div class="col-sm-6">
					<%@ include file="jspf/hideHubBox.jspf"%>
				</div>
			</div>
		</div>
		
	</div>
	<!-- /container -->
	
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js" type="text/javascript"></script>
	<!-- CanvasJS -->
	<script src="js/jquery.canvasjs.min.js" type="text/javascript"></script>
</body>
</html>

