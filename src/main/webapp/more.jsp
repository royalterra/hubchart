<%@page import="it.hubzilla.hubchart.model.Hubs"%>
<%@page import="it.hubzilla.hubchart.business.HubBusiness"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
List<Hubs> recentlyExpiredList = HubBusiness.findRecentlyExpiredHubs(0, 20);
request.setAttribute("recentlyExpiredList", recentlyExpiredList);
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
				<h4>Secondary statistics</h4>
			</div>
		</div>
		
		<div class="row">
			<div class="col-sm-12">
				<%@ include file="jspf/adminMenu.jspf" %>
			</div>
		</div>

		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<h3>latest expired hubs</h3>
				
				
				<table class="table table-condensed" style="border-collapse: collapse">
					<thead>
						<tr>
							<th>
								Hub
							</th>
							<th>
								Last seen
							</th>
							<th>
								Version
							</th>
							<th>
								Default language
							</th>
							<th>
								Geo
							</th>
						</tr>
					</thead>
				
					<tbody>
						<c:forEach items="${requestScope.recentlyExpiredList}" var="hub" varStatus="status">
						<tr>
							<td>
								<span title="${hub.name}">
									<b><c:out value="${hub.fqdn}" /></b>
								</span>
							</td>
							<td>
								<c:out value="${hub.lastSuccessfulPollTime}" />
							</td>
							<td>
								<span title="${hub.version}" style="font-size: 0.75em;">
									<c:out value="${hub.versionDescription}" escapeXml="false" />
								</span>
							</td>
							<td>
								<c:if test="${not empty hub.language}">
									<b><c:out value="${hub.language.language}" /></b>
								</c:if>
							</td>
							<td>
								<c:if test="${not empty hub.countryCode}">
									<c:out value="${hub.countryCode}" />
								</c:if>
							</td>
				
						</tr>
						</c:forEach>
					</tbody>
				</table>
				
				
			</div>
		</div>
		
	</div>
	<!-- /container -->
	
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="jquery/1.11.1/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js"></script>
</body>
</html>

