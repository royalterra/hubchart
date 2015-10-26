package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.PollBusiness;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;

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
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		
		//Job body
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {			
			//Poll queue
			Date pollTime = new Date();
			HubsDao hubsDao = new HubsDao();
			
			//Number of hubs to poll = live hubs count / 20
			Long liveHubsNumber = hubsDao.countLiveHubs(ses, false, false);
			Integer queueSize = new Double(liveHubsNumber/20L).intValue();
			List<Hubs> pollQueue = hubsDao.findPollQueue(ses, queueSize);
			
			//Poll the queue to create persisted stats
			//Hub info is updated accordingly in this method
			PollBusiness.pollHubList(ses, pollQueue, pollTime);
			
			//Print INFO log about failed polls
			String silentHubs = "";
			int silentCount = 0;
			for (Hubs hub:pollQueue) {
				if (hub.getLastSuccessfulPollTime().before(pollTime)) {
					silentHubs += hub.getFqdn()+"; ";
					silentCount++;
				}
			}
			LOG.info(silentCount+" failed polls: "+silentHubs);
			
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
}
