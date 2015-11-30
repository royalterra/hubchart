package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.ChartjsBuilder.ChartData;
import it.hubzilla.hubchart.business.ChartjsBuilder.ChartPoint;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.StatisticsDao;

import java.text.DecimalFormat;
import java.util.List;

import org.hibernate.Session;

public class ChartjsLineBuilder {
	
	public static final String COLOUR_FILL = "rgba(67,72,138,0.2)";
	public static final String COLOUR_STROKE = "rgba(67,72,138,1)";
	public static final DecimalFormat DF = new DecimalFormat("0");
	
	protected static String buildAllChartsScript(String elementId, Integer statId, String chartType) throws OrmException {
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
			out = buildLineChartScript(elementId, title,
					COLOUR_FILL, COLOUR_STROKE,
					COLOUR_STROKE, COLOUR_STROKE,
					cd);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return out;
	}
	
	private static String buildLineChartScript(String elementId, String title,
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
			if (point.getY() != null) {
				if (i > 0) {
					labels += ",";
					values += ",";
				}
				labels += "\""+point.getLabel()+"\" ";
				values += DF.format(point.getY());
			}
		}
		String out =
			"var "+elementId+"Options = {"+options+"};"+
			"var "+elementId+"Ctx = document.getElementById('"+elementId+"').getContext('2d');"+
			"var "+elementId+"Data = {"+
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
			"var "+elementId+"Chart = new Chart("+elementId+"Ctx).Line("+elementId+"Data, "+elementId+"Options);\r\n";
		return out;
	}
	
}
