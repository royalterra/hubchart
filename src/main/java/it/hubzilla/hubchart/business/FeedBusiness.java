package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.beans.StatisticBean;
import it.hubzilla.hubchart.model.FeedEntries;
import it.hubzilla.hubchart.persistence.FeedEntriesDao;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class FeedBusiness {
	
	//private static final Logger LOG = LoggerFactory.getLogger(FeedBusiness.class);
	
	public static void createFeedEntry() throws BusinessException {
		StatisticBean global;
		StatisticBean firstGs;
		String overview = "";
		String imageTags = "";
		String hubChart = "";
		String newestHubChart = "";
		try {
			global = PollBusiness.findLatestGlobalStats();
			firstGs = PollBusiness.findFirstGlobalStats();
			if (global != null && firstGs != null) {
				List<StatisticBean> statList = HubBusiness.findStatisticsForPresentation(true, true,
						AppConstants.ORDER_CHANNEL,
						false, //order asc?
						0,//start page
						20);//rows
				overview = createOverview(global);
				imageTags = createImageTags(firstGs, global);
				hubChart = createHubChart(statList);
				List<StatisticBean> newestHubList = HubBusiness.findNewestHubStatistics(0, 5);
				newestHubChart = createNewestHubChart(newestHubList);
			}
		} catch (OrmException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		
		FeedEntries fe = new FeedEntries();
		fe.setTitle("red#matrix network statistics "+AppConstants.FORMAT_DAY_SQL.format(global.getPollTime()));
		fe.setLink(AppConstants.FEED_LINK);
		fe.setDescriptionType("text/html");
		fe.setPublishedDate(global.getPollTime());
		String htmlContent =  "<html>"+overview+
				imageTags+
				hubChart+
				newestHubChart+"</html>";
		fe.setDescriptionValue(htmlContent);
		
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			GenericDao.saveGeneric(ses, fe);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static String createOverview(StatisticBean global) throws OrmException {
		String overview =
				"<h2>Network status</h2>"+
				"<table>"+
				"<tr>"+
					"<td>Active channels (6 months)</td>"+
					"<td><b>"+global.getActiveChannelsLast6Months()+"</b></td>"+
				"</tr>"+
				"<tr>"+
					"<td>Total channels</td>"+
					"<td><b>"+global.getTotalChannels()+"</b></td>"+
				"</tr>"+
				"<tr>"+
					"<td>Total posts</td>"+
					"<td><b>"+global.getTotalPosts()+"</b></td>"+
				"</tr>"+
				"<tr>"+
					"<td>Active hubs</td>"+
					"<td><b>"+global.getActiveHubs()+"</b></td>"+
				"</tr>"+
				"<tr>"+
					"<td>Channels/hub</td>"+
					"<td><b>"+global.getAverageHubChannels()+"</b></td>"+
				"</tr>"+
				"<tr>"+
					"<td>Deployed versions</td>"+
					"<td>"+HubBusiness.printVersionTagStat()+"</td>"+
				"</tr>"+
				"<tr>"+
					"<td>Last update</td>"+
					"<td><b>"+global.getLastUpdateString()+"</b></td>"+
				"</tr>"+
				"</table>";
		return overview;
	}
	
	private static String createHubChart(List<StatisticBean> statList) {
		String hubChart = "<h2>Hubs top 20</h2>"+
				"<table>"+
					"<tr>"+
						"<td><b>Active ch.</b>&nbsp;</td>"+
						"<td><b>Hub</b>&nbsp;</td>"+
						"<td><b>Registration</b>&nbsp;</td>"+
						"<td><b>Version</b>&nbsp;</td>"+
					"</tr>";
		for (StatisticBean stat:statList) {
			hubChart +=
					"<tr>"+
						"<td><b>"+stat.getActiveChannelsLast6Months()+"</b></td>"+
						"<td>"+stat.getHub().getFqdn()+"</td>";
			if (stat.getHub().getRegistrationPolicy().equals(AppConstants.REGISTRATION_PRIVATE)) {
				hubChart += "<td>"+stat.getRegistrationPolicyDescr()+"</td>";
			} else {
				hubChart += "<td><a href='"+stat.getHub().getBaseUrl()+"/register'>"+
						stat.getRegistrationPolicyDescr()+"</a></td>";
			}
			hubChart +=
						"<td>"+stat.getHub().getVersionDescription()+"</td>"+
					"</tr>";
		}
		hubChart += "</table>";
		return hubChart;
	}
	
	private static String createNewestHubChart(List<StatisticBean> statList) {
		String hubChart = "<h2>Newest hubs</h2>"+
				"<table>"+
					"<tr>"+
						"<td><b>Birth date</b>&nbsp;</td>"+
						"<td><b>Hub</b>&nbsp;</td>"+
						"<td><b>Registration</b>&nbsp;</td>"+
						"<td><b>Version</b>&nbsp;</td>"+
					"</tr>";
		for (StatisticBean stat:statList) {
			hubChart +=
					"<tr>"+
						"<td><b>"+stat.getBirthDate()+"</b></td>"+
						"<td>"+stat.getHub().getFqdn()+"</td>";
			if (stat.getHub().getRegistrationPolicy().equals(AppConstants.REGISTRATION_PRIVATE)) {
				hubChart += "<td>"+stat.getRegistrationPolicyDescr()+"</td>";
			} else {
				hubChart += "<td><a href='"+stat.getHub().getBaseUrl()+"/register'>"+
						stat.getRegistrationPolicyDescr()+"</a></td>";
			}
			hubChart +=
						"<td>"+stat.getHub().getVersionDescription()+"</td>"+
					"</tr>";
		}
		hubChart += "</table>";
		return hubChart;
	}
	
	private static String createImageTags(StatisticBean firstGs, StatisticBean global) {
		String imageTags = 
				"<h3>Total active channels "+
				AppConstants.FORMAT_DAY.format(firstGs.getPollTime())+" - "+
				AppConstants.FORMAT_DAY.format(global.getPollTime())+"</h3>"+
				"<img src='"+AppConstants.FEED_LINK+"/imagecache?statId="+
					global.getId()+"&type="+AppConstants.CHART_TYPE_TOTAL_CHANNELS+"' />"+
				"<h3>Total active hubs "+
				AppConstants.FORMAT_DAY.format(firstGs.getPollTime())+" - "+
				AppConstants.FORMAT_DAY.format(global.getPollTime())+"</h3>"+
				"<img src='"+AppConstants.FEED_LINK+"/imagecache?statId="+
					global.getId()+"&type="+AppConstants.CHART_TYPE_TOTAL_HUBS+"' />";
		return imageTags;		
	}
	
	public static void deleteOlderFeedEntries() throws BusinessException {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_WEEK, (-1)*AppConstants.FEED_DAYS_BEFORE_DELETION);
		Date beginDt = cal.getTime();
		
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			new FeedEntriesDao().deleteOlder(ses, beginDt);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}
