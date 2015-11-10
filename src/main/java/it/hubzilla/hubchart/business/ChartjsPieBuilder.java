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
	
	protected static String buildChartScript(String elementId, String divLegendId) throws OrmException {
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
				"label: \""+AppConstants.NETWORK_DESCRIPTIONS.get(vBean.getNetworkType())+" "+
						vBean.getVersionTag()+" - "+
						vBean.getPercentage()+"\""+
				"}";
		}
		out = "var "+elementId+"Data = ["+data+"];"+
				"var "+elementId+"Options = {"+options+"};"+
				"var "+elementId+"Ctx = document.getElementById('"+elementId+"').getContext('2d');"+
				"var "+elementId+"Doughnut = new Chart("+elementId+"Ctx).Doughnut("+elementId+"Data, "+elementId+"Options);"+
				"document.getElementById('"+divLegendId+"').innerHTML = "+elementId+"Doughnut.generateLegend();\r\n";
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
