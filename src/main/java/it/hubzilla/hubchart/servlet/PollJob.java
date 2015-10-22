package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.FeedBusiness;
import it.hubzilla.hubchart.business.PollBusiness;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubDao;
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

public class PollJob implements Job {
	
	private final Logger LOG = LoggerFactory.getLogger(PollJob.class);
	private HubDao hubDao = new HubDao();
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		
		//Job body
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			
			//Poll and save
			Date pollTime = new Date();
			List<Hubs> activeHubs = hubDao.findAll(ses, false, false);//include expired and hidden hubs
			List<Statistics> statList = PollBusiness.pollHubList(ses, activeHubs, pollTime);
			for (Statistics stat:statList) {
				Integer idStats = (Integer) GenericDao.saveGeneric(ses, stat);
				stat.getHub().setIdLastHubStats(idStats);
				GenericDao.updateGeneric(ses, stat.getHub().getId(), stat.getHub());
			}
			
			//Mark hubs as deleted if unresponsive for more than HUB_DELETION_DAYS
			List<Hubs> expiredHubs = hubDao.findExpired(ses);
			for (Hubs hub:expiredHubs) {
				PollBusiness.markUnresponsiveAsDeleted(ses, hub);
			}
			
			//Remove deleted hubs from activeHubs list
			List<Hubs> cleanHubs = hubDao.findAll(ses, false, false);
			int deletedCount = activeHubs.size()-cleanHubs.size();
			if (deletedCount > 0) LOG.info(deletedCount+" expired hubs have been deleted");
			activeHubs = cleanHubs;
			
			//INFO log about non-responsive
			String nonResponsive = "";
			int nonResponsiveCount = 0;
			for (Hubs actHub:activeHubs) {
				boolean found = false;
				for (Statistics stat:statList) {
					if (stat.getHub().getId() == actHub.getId()) found = true;
				}
				if (!found) {
					nonResponsive += actHub.getFqdn()+" ";
					nonResponsiveCount++;
				}
			}
			LOG.info(nonResponsiveCount+" non responsive hubs: "+nonResponsive);
			
			//Aggregate and save
			Statistics global = createGlobalStats(ses, activeHubs);
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
			LOG.info("Generating feed entry");
			FeedBusiness.createFeedEntry();
			LOG.info("Removing old feed entries");
			FeedBusiness.deleteOlderFeedEntries();
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		}
		
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
