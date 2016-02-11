<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	//PAGE
	int pag = 0;
	String pagString = request.getParameter(AppConstants.PARAM_HUB_PAGE);
	try {
		if (pagString != null) pag = Integer.parseInt(pagString);
	} catch (NumberFormatException e) {	}
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
	} catch (NumberFormatException e) {	}
	session.setAttribute("langPag", langPag);
	//GEO PAGE
	int geoPag = 0;
	String geoPagString = request.getParameter(AppConstants.PARAM_GEO_PAGE);
	try {
		if (geoPagString != null) geoPag = Integer.parseInt(geoPagString);
	} catch (NumberFormatException e) {
	}
	session.setAttribute("geoPag", geoPag);

	//VisitorBusiness.parse(request, response);
	
	//Request Scope
	StatisticBean gs = PollBusiness.findLatestGlobalStats();
	request.setAttribute("gs", gs);
	StatisticBean firstGs = PollBusiness.findFirstGlobalStats();
	request.setAttribute("firstGs", firstGs);
	//Tables
	ChartjsBuilder builder = ChartjsBuilder.getInstance();
	builder.clearCharts();
	builder.addChart("versionChart", null, AppConstants.CHART_TYPE_VERSIONS);
	builder.addChart("gridHubsChart", gs.getId(), AppConstants.CHART_TYPE_TOTAL_HUBS);
	builder.addChart("gridChannelsChart", gs.getId(), AppConstants.CHART_TYPE_TOTAL_CHANNELS);
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Hubchart - Hubzilla grid statistics</title>
<link href="images/hubchart1-16.png" rel="shortcut icon"
	type="image/png" />
<link href="feed" rel="alternate" type="application/rss+xml"
	title="Hubzilla Statistics feed" />

<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" />
<link href="css/font-awesome.min.css" rel="stylesheet" />
<link href="css/custom.css" rel="stylesheet" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->

<!-- Chart scripts -->
<%=builder.chartLoader()%>
</head>
<body>

	<div class="container">
		<div class="row">
			<div class="col-sm-6">
				<h1>
					<img src="images/hubchart1-32.png" align="middle" /> hubchart <!--img src="images/beta.png" align="bottom" /-->
				</h1>
				<a href="http://hubzilla.org"><b>hubzilla - community server</b></a>
				grid statistics<br /> &nbsp;<br />
				<h3>grid status</h3>
				<%@ include file="jspf/totalBox.jspf"%>
			</div>
			<div class="col-sm-6">
				&nbsp;
				<h4>deployed versions</h4>
				<div class="col-sm-12">
					<div id="versionChart" style="height: 210px; width: 100%;"></div>
				</div>
			</div>
		</div>

		&nbsp;<br />
		<div class="row">
			<div class="col-sm-6">
				<h4>active hubs</h4>
				<div id="gridHubsChart" style="height: 210px; width: 100%;"></div>
			</div>
			<div class="col-sm-6">
				<h4>active channels</h4>
				<div id="gridChannelsChart" style="height: 210px; width: 100%;"></div>
			</div>
		</div>
		
		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<%@ include file="jspf/hubTable.jspf"%>
			</div>
		</div>

		&nbsp;<br />
		<div class="row">
			<div class="col-sm-12">
				<a href="more.jsp"><img src="images/hz-16.png" align="middle" />&nbsp;More data</a>
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

		<div class="row">
			<div class="panel panel-default">

				<div class="panel-body">
					<strong>Credits</strong> <small> <!--Application hosting <a href="https://www.openshift.com">www.openshift.com</a>.-->
						Country flags <a
						href="https://www.gosquared.com/resources/flag-icons/">gosquared.com</a>.
					</small>
				</div>
			</div>
		</div>
		
	</div>
	<!-- /container -->

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="js/bootstrap.min.js" type="text/javascript"></script>
</body>
</html>

