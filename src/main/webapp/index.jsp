<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
//PAGE
int pag = 0;
String pagString = request.getParameter(AppConstants.PARAM_HUB_PAGE);
try {
	if (pagString != null) pag = Integer.parseInt(pagString);
} catch (NumberFormatException e) {}
session.setAttribute("pag", pag);
//ORDER BY
String order = AppConstants.ORDER_CHANNEL;
if (request.getParameter("order") != null) order = request.getParameter(AppConstants.PARAM_HUB_ORDER);
session.setAttribute("order", order);
//ORDER ASC
Boolean asc = Boolean.FALSE;
if (request.getParameter("asc") != null) asc = Boolean.parseBoolean(request.getParameter(AppConstants.PARAM_HUB_ASC));
session.setAttribute("asc", asc);
//LANG PAGE
int langPag = 0;
String langPagString = request.getParameter(AppConstants.PARAM_LANG_PAGE);
try {
	if (langPagString != null) langPag = Integer.parseInt(langPagString);
} catch (NumberFormatException e) {}
session.setAttribute("langPag", langPag);
//GEO PAGE
int geoPag = 0;
String geoPagString = request.getParameter(AppConstants.PARAM_GEO_PAGE);
try {
	if (geoPagString != null) geoPag = Integer.parseInt(geoPagString);
} catch (NumberFormatException e) {}
session.setAttribute("geoPag", geoPag);

//Request Scope
StatisticBean gs = PollBusiness.findLatestGlobalStats();
request.setAttribute("gs", gs);
StatisticBean firstGs = PollBusiness.findFirstGlobalStats();
request.setAttribute("firstGs", firstGs);
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
				<a href="https://github.com/redmatrix/hubzilla"><b>hubzilla</b></a> community server<br />
				&nbsp;<br />
				<h3>grid status</h3>
				<%@ include file="jspf/totalBox.jspf" %>
			</div>
			<div class="col-sm-5">
				<div class="text-center"><b>Total active channels</b><br/>
					<%=AppConstants.FORMAT_DAY.format(firstGs.getPollTime()) %> - today</div>
				<div class="text-center">
					<img src="imagecache?statId=${requestScope.gs.id}&type=<%=AppConstants.CHART_TYPE_TOTAL_CHANNELS %>" />
				</div>
				&nbsp;<br />
				<div class="text-center"><b>Total active hubs</b><br/>
					<%=AppConstants.FORMAT_DAY.format(firstGs.getPollTime()) %> - today</div>
				<div class="text-center">
					<img src="imagecache?statId=${requestScope.gs.id}&type=<%=AppConstants.CHART_TYPE_TOTAL_HUBS %>" />
				</div>
			</div>
		</div>

		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<%@ include file="jspf/hubTable.jspf" %>
			</div>
		</div>

		&nbsp;<br />
		<div class="row">
			<div class="col-sm-6">
				<h3>languages</h3>
				<%@ include file="jspf/languageTable.jspf" %>
			</div>
			<div class="col-sm-1">
				&nbsp;
			</div>
			<div class="col-sm-5">
				<h3>location</h3>
				<%@ include file="jspf/geoIpTable.jspf" %>
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
			<div class="panel panel-default">
				<div class="col-sm-6">
					<%@ include file="jspf/registerBox.jspf"%>
				</div>
				<div class="col-sm-6">
					<%@ include file="jspf/hideHubBox.jspf"%>
				</div>
			</div>
		</div>
		
		&nbsp;<br />
		<div class="row">
			<div class="panel panel-default">
				<div class="col-sm-6">					
					<script type="text/javascript" src="http://www.openhub.net/p/623130/widgets/project_basic_stats.js"></script>
				</div>
				<div class="col-sm-6">
					<script type="text/javascript" src="http://www.openhub.net/p/623130/widgets/project_languages.js"></script>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="panel panel-default">

				<div class="panel-body">
					<strong>Credits</strong> <small> <!--Application hosting <a href="https://www.openshift.com">www.openshift.com</a>.-->
						Country-IP mapping <a href="http://ip2nation.com/">ip2nation.com</a>.
						Country flags <a
						href="https://www.gosquared.com/resources/flag-icons/">gosquared.com</a>.
					</small>
				</div>
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

