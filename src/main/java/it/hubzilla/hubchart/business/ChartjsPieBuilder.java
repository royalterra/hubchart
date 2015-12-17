package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.beans.VersionTagStatBean;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public class ChartjsPieBuilder {

	private static final int MAX_VERSIONS_SHOWN = 10;
	public static final String CHART_RED_COLOUR = "#c60032";
	public static final String CHART_RED_HIGHLIGHT = "#fb7396";
	public static final String CHART_HUBZ_COLOUR = "#43488A";
	public static final String CHART_HUBZ_HIGHLIGHT = "#9da4fb";
	
	protected static String buildChartScript(String elementId,
			String labelDesc, String valueDesc) throws OrmException {
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
				.findVersionTagStatBeans(totalHubs.intValue(), MAX_VERSIONS_SHOWN);
		
		out = "<script type=\"text/javascript\">\r\n"+
			//"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});\r\n"+
			"google.setOnLoadCallback(drawChart);\r\n"+
			"function drawChart() {\r\n"+
				"var data = google.visualization.arrayToDataTable([\r\n"+
					"['"+labelDesc+"','"+valueDesc+"']";
		for (int i = 0; i < vbeanList.size(); i++) {
			VersionTagStatBean vBean = vbeanList.get(i);
			String label = vBean.getNetworkType()+" "+
					vBean.getVersionTag()+" - "+vBean.getPercentage();
			out += ", ['"+label+"',"+vBean.getLiveHubs()+"]";
		}
		out += "]);\r\n"+
				"var options = {"+
					"pieHole: 0.45, "+
					"pieSliceText: 'none',\r\n"+
					"tooltip: {"+
						"text: 'value' "+
					"},\r\n"+
					"chartArea: {"+
						"top: 10, left: 10,"+
						"width: '100%', height: '100%'"+
					"},\r\n"+
					"legend: {"+
						"position: 'right', "+
						"maxLines: "+MAX_VERSIONS_SHOWN+", "+
					"},\r\n"+
					"animation: {"+
						"duration: 2000, "+
						"startup: 'true', "+
						"easing: 'out' "+
					"},\r\n"+
					"slices: {\r\n";
		for (int i = 0; i < vbeanList.size(); i++) {
			VersionTagStatBean vBean = vbeanList.get(i);
			String colour1 = CHART_HUBZ_HIGHLIGHT;
			String colour2 = CHART_HUBZ_COLOUR;
			if (vBean.getNetworkType().equals(AppConstants.NETWORK_TYPE_RED)) {
				colour1 = CHART_RED_HIGHLIGHT;
				colour2 = CHART_RED_COLOUR;
			}
			if (i > 0) out += ", ";
			out += i+": { color: '"+ColourBusiness.getColourShade(colour1, colour2,
					new Double(i), new Double(vbeanList.size()))+"'}\r\n";
		}
		out +=		"}\r\n"+//slices
				"};\r\n"+//options
				"var chart = new google.visualization.PieChart(document.getElementById('"+elementId+"'));\r\n"+
				"chart.draw(data, options);\r\n"+
			"}\r\n"+//function
			"</script>\r\n";
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
		private List<ChartPoint> dataPoints = new ArrayList<ChartjsPieBuilder.ChartPoint>();
		
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
