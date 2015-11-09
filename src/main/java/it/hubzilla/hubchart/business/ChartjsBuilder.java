package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartjsBuilder {


	private static List<Chart> chartList = new ArrayList<ChartjsBuilder.Chart>();
	
	public void addChart(String elementId, String divLegendId, Integer statId, String chartType) {
		Chart chart = new Chart(elementId, divLegendId, statId, chartType);
		chartList.add(chart);
	}
	
	public String chartLoader() throws OrmException {
		String out = "<script type=\"text/javascript\">"+
			"window.onload = function () {";
		for (Chart chart:chartList) {
			if (chart.chartType.equals(AppConstants.CHART_TYPE_VERSIONS)) {
				out += ChartjsPieBuilder.buildChartScript(chart.getElementId(), chart.getDivLegendId());
			} else {
				out += ChartjsLineBuilder.buildAllChartsScript(chart.getElementId(), chart.getStatId(), chart.getChartType());
			}
		}
	    out += "}"+// /onload function
	    "</script>";
		return out;
	}
	
	
	/* INNER CLASSES */
	
	
	public static class Chart {
		private String elementId;
		private String divLegendId;
		private Integer statId;
		private String chartType;
		
		public Chart(String elementId, String divLegendId, Integer statId, String chartType) {
			this.elementId=elementId;
			this.divLegendId=divLegendId;
			this.statId=statId;
			this.chartType=chartType;
		}
		public String getElementId() {
			return elementId;
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
