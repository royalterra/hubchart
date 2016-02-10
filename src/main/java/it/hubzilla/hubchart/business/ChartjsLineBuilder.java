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
	
	public static final String COLOUR_STROKE = "#43488A";
	public static final DecimalFormat DF = new DecimalFormat("0");
	
	protected static String buildAllChartsScript(String elementId, Integer statId, String chartType,
			String labelDesc, String valueDesc) throws OrmException {
		String out = "Error rendering chart";
		ChartData cd = new ChartData(false, "Chart");
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
			out = buildLineChartScript(elementId, cd, labelDesc, valueDesc);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return out;
	}
	
	private static String buildLineChartScript(String elementId,
			ChartData dataList, String labelDesc, String valueDesc) {
		String out = "<script type=\"text/javascript\">\r\n"+
			//"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});"+
			"google.setOnLoadCallback(drawChart);\r\n"+
			"function drawChart() {\r\n"+
				"var data = google.visualization.arrayToDataTable([\r\n"+
					"['"+labelDesc+"','"+valueDesc+"']";
		for (int i = 0; i < dataList.getDataPoints().size(); i++) {
			ChartPoint point = dataList.getDataPoints().get(i);
			try {
				out += ", ['"+point.getLabel()+"',"+DF.format(point.getY())+"]";
			} catch (IllegalArgumentException e) {/*Cannot format Y as a number*/
				System.out.println(e.getMessage()+": '"+point.getY()+"'");
				e.printStackTrace();
			}
		}
		out +=	"]);\r\n"+
				"var options = {"+
					"curveType: 'function',\r\n"+
					"chartArea: {"+
						"top: 10, left: 50,"+
						"width: '100%', height: '100%'"+
					"},\r\n"+
					"animation: {"+
						"duration: 2000, "+
						"startup: 'true', "+
						"easing: 'out' "+
					"},\r\n"+
					"series: {\r\n"+
						"0: { color: '"+COLOUR_STROKE+"' }"+
					"}\r\n"+
				"};\r\n"+
				"var chart = new google.visualization.LineChart(document.getElementById('"+elementId+"'));\r\n"+
				"chart.draw(data, options);\r\n"+
			"}\r\n"+
		"</script>\r\n";	
		return out;
	}
	
}
