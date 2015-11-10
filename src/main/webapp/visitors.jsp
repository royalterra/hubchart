<%@page import="it.hubzilla.hubchart.business.VisitorBusiness"%>
<%@page import="it.hubzilla.hubchart.model.Visitors"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
List<Visitors> visitorList = VisitorBusiness.findVisitors();
request.setAttribute("visitorList", visitorList);
%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Hubchart - Visitor panel</title>
	
	<!-- Bootstrap -->
	<link href="css/bootstrap.min.css" rel="stylesheet" />
	<link href="css/custom.css" rel="stylesheet" />
	<link href="css/font-awesome.min.css" rel="stylesheet" />
	
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
				<h4>Visitor panel</h4>
			</div>
		</div>
		
		<div class="row">
			<div class="col-sm-12">
				<%@ include file="jspf/adminMenu.jspf" %>
			</div>
		</div>
		
		<c:forEach items="${requestScope.visitorList}" var="visitor" varStatus="status">
			<i><c:out value="${visitor.formattedTime}" /></i>
			<b><c:if test="${not empty visitor.countryCode}">
				<c:out value="${visitor.countryCode}" />
			</c:if></b>
			<c:if test="${not empty visitor.ipHash}">
				<c:out value="${visitor.ipHash}" />
			</c:if>
			<b><c:if test="${visitor.newVisitor}">NEW</c:if></b><br/>
		</c:forEach>
		
	</div><!-- /container -->

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="jquery/1.11.1/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
</body>
</html>