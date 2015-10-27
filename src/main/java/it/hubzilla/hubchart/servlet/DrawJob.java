package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.FeedBusiness;
import it.hubzilla.hubchart.business.LogBusiness;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;
import it.hubzilla.hubchart.persistence.ImageCacheDao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawJob implements Job {
	
	private final Logger LOG = LoggerFactory.getLogger(DrawJob.class);
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		LogBusiness.addLog(AppConstants.LOG_INFO, "draw", "<b>STARTED JOB</b>");
		
		//Job body
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			HubsDao hubsDao = new HubsDao();
			
			//Find live hubs
			List<Hubs> liveHubsList = hubsDao.findLiveHubs(ses, false, false);
			
			//Aggregate and save
			Statistics global = createGlobalStats(ses, liveHubsList);
			GenericDao.saveGeneric(ses, global);
			//Clear cache
			new ImageCacheDao().clearCache(ses);
			
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		// Generate RSS feed
		try {
			LogBusiness.addLog(AppConstants.LOG_INFO, "draw", "Generating feed entry");
			FeedBusiness.createFeedEntry();
			LogBusiness.addLog(AppConstants.LOG_INFO, "draw", "Removing old feed entries");
			FeedBusiness.deleteOlderFeedEntries();
			LogBusiness.addLog(AppConstants.LOG_INFO, "draw", "Removing old log entries");
			LogBusiness.deleteOldLogs();
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		}
		
		LogBusiness.addLog(AppConstants.LOG_INFO, "draw", "<b>ENDED JOB</b>");
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private Statistics createGlobalStats(Session ses, List<Hubs> activeHubs) 
			throws OrmException {
		Statistics global = new Statistics();
		global.setTotalChannels(0);
		global.setActiveChannelsLastMonth(0);
		global.setActiveChannelsLast6Months(0);
		global.setTotalPosts(0);
		global.setActiveHubs(0);
		global.setPollTime(new Date());
		global.setActiveHubs(activeHubs.size());
		for (Hubs hub:activeHubs) {
			if (hub.getIdLastHubStats() != null) {
				Statistics stat = GenericDao.findById(ses, Statistics.class, hub.getIdLastHubStats());
				if (stat != null) addToGlobal(global, stat);
			}
		}
		return global;
	}
	
	private void addToGlobal(Statistics global, Statistics stat) {
		if (stat.getTotalChannels() != null)
				global.setTotalChannels(global.getTotalChannels()+stat.getTotalChannels());
		if (stat.getActiveChannelsLastMonth() != null)
				global.setActiveChannelsLastMonth(global.getActiveChannelsLastMonth()+stat.getActiveChannelsLastMonth());
		if (stat.getActiveChannelsLast6Months() != null)
				global.setActiveChannelsLast6Months(global.getActiveChannelsLast6Months()+stat.getActiveChannelsLast6Months());
		if (stat.getTotalPosts() != null)
				global.setTotalPosts(global.getTotalPosts()+stat.getTotalPosts());
	}

}
