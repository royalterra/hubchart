package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.StatisticsDao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public class ChartjsBuilder {

	public static final String CHART_LINE_COLOUR = "#43488A";
	public static final String COLOUR_FILL = "rgba(151,187,205,0.2)";
	public static final String COLOUR_STROKE = "rgba(151,187,205,1)";
	public static final String COLOUR_POINT = "rgba(151,187,205,1)";
	public static final DecimalFormat DF = new DecimalFormat("0");
	private static List<Chart> chartList = new ArrayList<ChartjsBuilder.Chart>();
	
	public void addChart(String divElementId, Integer statId, String chartType) {
		Chart chart = new Chart(divElementId, statId, chartType);
		chartList.add(chart);
	}
	
	public String chartLoader() throws OrmException {
		String out = "<script type=\"text/javascript\">"+
			"window.onload = function () {";
		for (Chart chart:chartList) {
			out += buildChartScript(chart.getDivElementId(), chart.getStatId(), chart.getChartType());
		}
	    out += "}"+// /onload function
	    "</script>";
		return out;
	}
	
	private String buildChartScript(String divElementId, Integer statId, String chartType) throws OrmException {
		String out = "Error rendering table";
		String title = "";
		if (AppConstants.CHART_TYPE_HUB_CHANNELS.equals(chartType)) {
			title = "Hub channels";
		}
		if (AppConstants.CHART_TYPE_TOTAL_CHANNELS.equals(chartType)) {
			title = "Grid channels";
		}
		if (AppConstants.CHART_TYPE_TOTAL_HUBS.equals(chartType)) {
			title = "Grid hubs";
		}
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
			out = buildSingleChartScript(divElementId, title,
					COLOUR_FILL, COLOUR_STROKE, COLOUR_POINT, cd);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return out;
	}
	
	private String buildSingleChartScript(String divElementId, String title,
			String fillColor, String strokeColor, String pointColor,
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
			    "tooltipTemplate: \""+title+" on <%if (label){%><%=label%>: <%}%><%= value %>\"";
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
			            "label: \""+title+"\","+
			            "fillColor: \""+fillColor+"\","+
			            "strokeColor: \""+strokeColor+"\","+
			            "pointColor: \""+pointColor+"\","+
			            "pointStrokeColor: \"#fff\","+
			            "pointHighlightFill: \"#fff\","+
			            "pointHighlightStroke: \"rgba(151,187,205,1)\","+
			            "data: ["+values+"]"+
			        "}"+
			    "]"+
			"};"+
			"var "+divElementId+"Chart = new Chart("+divElementId+"Ctx).Line("+divElementId+"Data, "+divElementId+"Options);";
		return out;
	}
	
	public static class Chart {
		private String divElementId;
		private Integer statId;
		private String chartType;
		
		public Chart(String divElementId, Integer statId, String chartType) {
			this.divElementId=divElementId;
			this.statId=statId;
			this.chartType=chartType;
		}
		public String getDivElementId() {
			return divElementId;
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
