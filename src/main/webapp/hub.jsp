<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.hubzilla.hubchart.beans.*"%>
<%@ page import="it.hubzilla.hubchart.business.*"%>
<%@ page import="it.hubzilla.hubchart.*"%>
<%@ page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
	//FQDN
	String fqdn = "";
	if (request.getParameter(AppConstants.PARAM_HUB_FQDN) != null) fqdn = request.getParameter(AppConstants.PARAM_HUB_FQDN);
	StatisticBean stat = StatisticBusiness.findLastStatisticBeanByFqdn(fqdn);
	session.setAttribute("stat", stat);
	//Tables
	ChartjsBuilder builder = ChartjsBuilder.getInstance();
	builder.clearCharts();
	builder.addChart("hubChart", null, stat.getId(), AppConstants.CHART_TYPE_HUB_CHANNELS);
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
<link href="css/custom.css" rel="stylesheet" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->

<!-- Chart scripts -->
<script src="js/Chart.min.js" type="text/javascript"></script>
<%=builder.chartLoader()%>
</head>
<body>

<div class="container">
	<div class="row">
		<div class="col-sm-12">
			<h1>
				<img src="images/hubchart1-32.png" align="middle" /> hubchart
			</h1>
			&nbsp;<br />
			<h3><c:out value="${stat.hub.fqdn}" /> status</h3>
		</div>
	</div>
	<div class="row">
		<!-- content -->
		<div class="col-sm-5">
			<c:if test="${not empty stat.hub.info}">
				<c:out value="${stat.hub.info}" />
				<br />&nbsp;<br />
			</c:if>
			<c:if test="${not empty stat.totalChannels}">
				<table class="table table-condensed small"
					style="width: 100%; border-collapse: collapse">
					<thead>
						<tr>
							<td colspan="2"><h4>channels and activity</h4></td>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>Active channels (6 months)</td>
							<td class="text-right"><b><c:out value="${stat.activeChannelsLast6Months}" /></b></td>
						</tr>
						<tr>
							<td>Total channels</td>
							<td class="text-right"><b><c:out value="${stat.totalChannels}" /></b></td>
						</tr>
						<!--Active last month: <b><c:out value="${stat.activeChannelsLastMonth}"/></b><br /-->
						<tr>
							<td>Total posts</td>
							<td class="text-right"><b><c:out value="${stat.totalPosts}" /></b></td>
						</tr>
					</tbody>
				</table>
			</c:if>
			<table class="table table-condensed small"
				style="width: 100%; border-collapse: collapse">
				<thead>
					<tr>
						<td colspan="2"><h4>general information</h4></td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Name</td>
						<td>
							<a href="${stat.hub.baseUrl}" target="_blank"><b><c:out value="${stat.hub.name}" /></b>&nbsp;<img src="images/external-link-icon.png" /></a>
						</td>
					</tr>
					<tr>
						<td>Domain</td>
						<td>
							<b><c:out value="${stat.hub.fqdn}" /></b>&nbsp; 
							<c:if test='${stat.https}'>
								<img src="images/lock-16.png" />
								<b>SSL</b>
							</c:if>
						</td>
					</tr>
					<c:if test="${not empty stat.hub.registrationPolicy}">
						<tr>
							<td>Registrations</td>
							<td>
								<b>${stat.registrationPolicyDescr}</b>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty stat.hub.networkType}">
						<tr>
							<td>Network</td>
							<td>
								<img src="${stat.networkTypeIcon}" border="0" />
								<b>${stat.networkTypeDescr}</b>
							</td>
						</tr>
					</c:if>
					<tr>
						<td>Version</td>
						<td><b><c:out value="${stat.hub.versionTag}" /></b> (<c:out value="${stat.hub.version}" />)</td>
					</tr>
					<c:if test="${not empty stat.hub.language}">
						<tr>
							<td>Default language</td>
							<td>
								<img src="${stat.languageFlag}" />&nbsp;<b><c:out value="${stat.hub.language.language}" /></b>
							</td>
						</tr>
					</c:if>
					<tr>
						<td>Server location</td>
						<td>
							<img src="${stat.countryFlag}" />&nbsp;<b><c:out value="${stat.hub.countryName}" /></b>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="col-sm-7">
			<c:if test='${not empty stat.activeChannelsLast6Months}'>
				<h4>active channels history</h4>
				<canvas id="hubChart" style="height: 300px; width: 100%;"></canvas>
			</c:if>
			<table class="table table-condensed small"
				style="width: 100%; border-collapse: collapse">
				<tbody>
					<c:if test='${not empty stat.hub.adminChannel}'>
						<tr>
							<td>Administrator</td>
							<td><a href='${stat.hub.adminChannel}'>${stat.hub.adminChannel}</a>
							</td>
						</tr>
					</c:if>
					<c:if test='${not empty stat.hub.plugins}'>
						<tr>
							<td>Plugins</td>
							<td>${stat.hub.formattedPlugins}<br />
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty stat.hub.directoryMode}">
						<c:if test="${stat.directory}">
							<tr>
								<td>Directory mode</td>
								<td><b>${stat.directoryDescr}</b></td>
							</tr>
						</c:if>
					</c:if>
					<tr>
						<td>Last poll</td>
						<td>${stat.hub.lastSuccessfulPollTime}</td>
					</tr>
				</tbody>
			</table>
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
	
</div>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="js/bootstrap.min.js" type="text/javascript"></script>
</body>
</html>