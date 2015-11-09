package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.beans.VersionTagStatBean;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;
import it.hubzilla.hubchart.persistence.StatisticsDao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public class ChartjsBuilder {

	public static final String CHART_RED_COLOUR = "#c60032";
	public static final String CHART_RED_HIGHLIGHT = "#fb7396";
	public static final String CHART_HUBZ_COLOUR = "#43488A";
	public static final String CHART_HUBZ_HIGHLIGHT = "#9da4fb";
	public static final String COLOUR_FILL = "rgba(67,72,138,0.2)";
	public static final String COLOUR_STROKE = "rgba(67,72,138,1)";
	public static final DecimalFormat DF = new DecimalFormat("0");
	private static List<Chart> chartList = new ArrayList<ChartjsBuilder.Chart>();
	
	public void addChart(String divElementId, String divLegendId, Integer statId, String chartType) {
		Chart chart = new Chart(divElementId, divLegendId, statId, chartType);
		chartList.add(chart);
	}
	
	public String chartLoader() throws OrmException {
		String out = "<script type=\"text/javascript\">"+
			"window.onload = function () {";
		for (Chart chart:chartList) {
			if (chart.chartType.equals(AppConstants.CHART_TYPE_VERSIONS)) {
				out += buildVersionPieChartsScript(chart.getDivElementId(), chart.getDivLegendId());
			} else {
				out += buildAllLineChartsScript(chart.getDivElementId(), chart.getStatId(), chart.getChartType());
			}
		}
	    out += "}"+// /onload function
	    "</script>";
		return out;
	}
	
	private String buildAllLineChartsScript(String divElementId, Integer statId, String chartType) throws OrmException {
		String out = "Error rendering chart";
		String title = AppConstants.CHART_TYPE_DESCRIPTIONS.get(chartType);
		ChartData cd = new ChartData(false, title);
		Session ses = HibernateSessionFactory.getSession();
		try {
			//Find the correct stat list by chartType
			List<Statistics> statHistory = null;
			Statistics referenceStat = GenericDao.findById(ses, Statistics.class, statId);
			if (referenceStat != null) {
				if (AppConstants.CHART_TYPE_HUB_CHANNELS.equals(chartType)) {	
					statHistory = new StatisticsDao().findByHub(ses, referenceStat.getHub().getId(),
							AppConstants.DATE_FAR_PAST, AppConstants.DATE_FAR_FUTURE);
				}
				if (AppConstants.CHART_TYPE_TOTAL_CHANNELS.equals(chartType) || 
						AppConstants.CHART_TYPE_TOTAL_HUBS.equals(chartType)) {
					statHistory = new StatisticsDao().findGlobalStats(ses,
							AppConstants.DATE_FAR_PAST, AppConstants.DATE_FAR_FUTURE);
				}
				//Create the correct value sequence by chartType
				for (Statistics stat:statHistory) {
					if (AppConstants.CHART_TYPE_HUB_CHANNELS.equals(chartType)) {
						ChartPoint cp = new ChartPoint(
								AppConstants.FORMAT_DAY.format(stat.getPollTime()),
								stat.getPollTime(),
								stat.getActiveChannelsLast6Months());
						cd.getDataPoints().add(cp);
					}
					if (AppConstants.CHART_TYPE_TOTAL_CHANNELS.equals(chartType)) {
						if (stat.getActiveChannelsLast6Months() != null) {
							ChartPoint cp = new ChartPoint(
									AppConstants.FORMAT_DAY.format(stat.getPollTime()),
									stat.getPollTime(),
									stat.getActiveChannelsLast6Months());
							cd.getDataPoints().add(cp);
						}
					}
					if (AppConstants.CHART_TYPE_TOTAL_HUBS.equals(chartType)) {
						if (stat.getActiveHubs() != null) {
							ChartPoint cp = new ChartPoint(
									AppConstants.FORMAT_DAY.format(stat.getPollTime()),
									stat.getPollTime(),
									stat.getActiveHubs());
							cd.getDataPoints().add(cp);
						}
					}
				}
			}
			out = buildLineChartScript(divElementId, title,
					COLOUR_FILL, COLOUR_STROKE, COLOUR_STROKE, COLOUR_STROKE, cd);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return out;
	}
	
	private String buildLineChartScript(String divElementId, String title,
			String fillColor, String strokeColor, String pointColor, String pointHighlightStroke,
			ChartData dataList) {
		String options = 
			    "scaleShowGridLines : false,"+
			    "scaleGridLineColor : \"rgba(0,0,0,.05)\","+
			    "scaleGridLineWidth : 1,"+
			    "scaleShowHorizontalLines: false,"+
			    "scaleShowVerticalLines: false,"+
			    "bezierCurve : true,"+
			    "bezierCurveTension : 0.4,"+
			    "pointDot : true,"+
			    "pointDotRadius : 4,"+
			    "pointDotStrokeWidth : 1,"+
			    "pointHitDetectionRadius : 20,"+
			    "datasetStroke : true,"+
			    "datasetStrokeWidth : 2,"+
			    "datasetFill : true,"+
			    "tooltipTemplate: \""+title+" <%if (label){%><%=label%>: <%}%><%= value %>\"";
		String labels = "";
		String values = "";
		for (int i = 0; i < dataList.getDataPoints().size(); i++) {
			ChartPoint point = dataList.getDataPoints().get(i);
			if (i > 0) {
				labels += ",";
				values += ",";
			}
			labels += "\""+point.getLabel()+"\" ";
			values += DF.format(point.getY());
		}
		String out =
			"var "+divElementId+"Options = {"+options+"};"+
			"var "+divElementId+"Ctx = document.getElementById('"+divElementId+"').getContext('2d');"+
			"var "+divElementId+"Data = {"+
			    "labels: ["+labels+"],"+
			    "datasets: ["+
			        "{"+
			            "label: \""+title+"\", "+
			            "fillColor: \""+fillColor+"\", "+
			            "strokeColor: \""+strokeColor+"\", "+
			            "pointColor: \""+pointColor+"\", "+
			            "pointStrokeColor: \"#fff\", "+
			            "pointHighlightFill: \"#fff\", "+
			            "pointHighlightStroke: \""+pointHighlightStroke+"\", "+
			            "data: ["+values+"]"+
			        "}"+
			    "]"+
			"};"+
			"var "+divElementId+"Chart = new Chart("+divElementId+"Ctx).Line("+divElementId+"Data, "+divElementId+"Options);\r\n";
		return out;
	}
	
	private String buildVersionPieChartsScript(String divElementId, String divLegendId) throws OrmException {
		String out = "Error rendering chart";
		Long totalHubs = 0L;
		Session ses = HibernateSessionFactory.getSession();
		try {
			totalHubs= new HubsDao().countLiveHubs(ses);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		List<VersionTagStatBean> vbeanList = PresentationBusiness
				.findVersionTagStatBeans(totalHubs.intValue());
		
		String options = 
			"segmentShowStroke : true, "+
			"segmentStrokeColor : \"#fff\", "+
			"segmentStrokeWidth : 2, "+
			"percentageInnerCutout : 50, "+ // This is 0 for Pie charts
			"animationSteps : 100, "+
			"animationEasing : \"easeOutBounce\", "+
			"animateRotate : true, "+
			"animateScale : true, "+
			"tooltipTemplate: \"<%if (label){%><%=label%>: <%}%><%= value %>\", "+
			"legendTemplate : \"<ul class=\\\"<%=name.toLowerCase()%>-legend\\\"><% for (var i=0; i<segments.length; i++){%><li><span style=\\\"background-color:<%=segments[i].fillColor%>\\\"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>\"";
		String data = "";
		for (int i = 0; i < vbeanList.size(); i++) {
			VersionTagStatBean vBean = vbeanList.get(i);
			String colour1 = CHART_HUBZ_HIGHLIGHT;
			String colour2 = CHART_HUBZ_COLOUR;
			if (vBean.getNetworkType().equals(AppConstants.NETWORK_TYPE_RED)) {
				colour1 = CHART_RED_HIGHLIGHT;
				colour2 = CHART_RED_COLOUR;
			}
			if (i > 0) data += ",";
			data += "{"+
				"value: "+vBean.getLiveHubs()+","+
				"color: \""+ColourBusiness.getColourShade(colour1, colour2,
						new Double(i), new Double(vbeanList.size()))+"\","+
				/*"highlight: \""+ColourBusiness.getColourShade(CHART_LINE_COLOUR, CHART_HIGHLIGHT_COLOUR,
						vBean.getLiveHubs().doubleValue() ,totalHubs.doubleValue())+"\","+*/
				"label: \""+AppConstants.NETWORK_DESCRIPTIONS.get(vBean.getNetworkType())+" "+vBean.getVersionTag()+"\""+
				"}";
		}
		out = "var "+divElementId+"Data = ["+data+"];"+
				"var "+divElementId+"Options = {"+options+"};"+
				"var "+divElementId+"Ctx = document.getElementById('"+divElementId+"').getContext('2d');"+
				"var "+divElementId+"Doughnut = new Chart("+divElementId+"Ctx).Doughnut("+divElementId+"Data, "+divElementId+"Options);"+
				"document.getElementById('"+divLegendId+"').innerHTML = "+divElementId+"Doughnut.generateLegend();\r\n";
		return out;
	}
	
	
	/* INNER CLASSES */
	
	
	public static class Chart {
		private String divElementId;
		private String divLegendId;
		private Integer statId;
		private String chartType;
		
		public Chart(String divElementId, String divLegendId, Integer statId, String chartType) {
			this.divElementId=divElementId;
			this.divLegendId=divLegendId;
			this.statId=statId;
			this.chartType=chartType;
		}
		public String getDivElementId() {
			return divElementId;
		}
		public String getDivLegendId() {
			return divLegendId;
		}
		public Integer getStatId() {
			return statId;
		}
		public String getChartType() {
			return chartType;
		}
	}
	
	public static class ChartData {
		private String name = "";
		private List<ChartPoint> dataPoints = new ArrayList<ChartjsBuilder.ChartPoint>();
		
		public ChartData(boolean showInLegend, String name) {
			this.name=name;
		}
		public String getName() {
			return name;
		}
		public List<ChartPoint> getDataPoints() {
			return dataPoints;
		}
	}

	public static class ChartPoint {
		private String label = "";
		private Date dateX = null;
		private Integer y = null;
		
		public ChartPoint(String label, Date dateX, Integer y) {
			this.label=label;
			this.dateX=dateX;
			this.y=y;
		}
		
		public String getLabel() {
			return label;
		}
		public Date getDateX() {
			return dateX;
		}
		public Integer getY() {
			return y;
		}
	}
}
