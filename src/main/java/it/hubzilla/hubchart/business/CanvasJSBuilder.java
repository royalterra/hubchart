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

public class CanvasJSBuilder {

	public static final String TYPE_SPLINE = "spline";
	public static final String CHART_LINE_COLOUR = "#43488A";
	public static final DecimalFormat DF = new DecimalFormat("0");
	private static List<Chart> chartList = new ArrayList<CanvasJSBuilder.Chart>();
	
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
		ChartData cd = new ChartData(TYPE_SPLINE, false, title);
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
			List<ChartData> cdList = new ArrayList<CanvasJSBuilder.ChartData>();
			cdList.add(cd);
			out = buildSingleChartScript(divElementId, title, cdList);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return out;
	}
	
	private String buildSingleChartScript(String divElementId, String title,
			List<ChartData> dataList) {
		String out = "var "+divElementId+"Div = new CanvasJS.Chart(\""+divElementId+"\", {"+
					"title:{ text: \""+title+"\"},"+
					"animationEnabled: true,"+
					"toolTip: { shared: true},"+ 
					"legend:{ horizontalAlign :\"center\"},"+
					"data: [";
		for (int i = 0; i<dataList.size(); i++) {
			if (i > 0) out += ",";
			ChartData data = dataList.get(i);
			out += "{"+
				"type: \""+data.getType()+"\","+
				"showInLegend: "+data.getShowInLegend()+","+
				"name: \""+data.getName()+"\","+
				"color: \""+CHART_LINE_COLOUR+"\","+
				"dataPoints: [";
			for (int j=0; j<data.dataPoints.size(); j++) {
				ChartPoint point = data.getDataPoints().get(j);
				if (j > 0) out += ",";
				out += "{label: \""+point.getLabel()+"\","+
					"x: new Date("+point.getDateX().getTime()+"),"+
					"y: "+DF.format(point.getY())+
					"}";
			}
			out += "]"+
				"}";
		}
		out +="]"+
	        	"});"+// /var chart
	        	divElementId+"Div.render();";
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
		private String type = CanvasJSBuilder.TYPE_SPLINE;
		private boolean showInLegend = true;
		private String name = "";
		private List<ChartPoint> dataPoints = new ArrayList<CanvasJSBuilder.ChartPoint>();
		
		public ChartData(String type, boolean showInLegend, String name) {
			this.type=type;
			this.showInLegend=showInLegend;
			this.name=name;
		}
		public String getType() {
			return type;
		}
		public boolean getShowInLegend() {
			return showInLegend;
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
