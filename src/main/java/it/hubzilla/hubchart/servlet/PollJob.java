package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.LogBusiness;
import it.hubzilla.hubchart.business.PollBusiness;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;
import it.hubzilla.hubchart.persistence.LogsDao;

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
		LogBusiness.addLog(AppConstants.LOG_INFO, "poll", "<b>STARTED JOB</b>");
		
		boolean full = false;
		String typeParam = (String) jobCtx.getMergedJobDataMap().get("type");
		if (typeParam != null) {
			if (typeParam.equals("full")) {
				full=true;
			}
		}
		//Job body
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {			
			//Poll queue
			Date pollTime = new Date();
			//LogsDao logsDao = new LogsDao();
			HubsDao hubsDao = new HubsDao();
			
			List<Hubs> pollQueue = null;
			if (full) {
				//The full queue
				new LogsDao().addLog(ses, AppConstants.LOG_INFO, "poll", "FULL POLL");
				pollQueue = hubsDao.findPollQueue(ses, Integer.MAX_VALUE);
			} else {
				Long liveHubsNumber = hubsDao.countLiveHubs(ses, true);
				//Number of hubs to poll = live hubs count / 20
				Integer maxQueueSize = new Double(liveHubsNumber/20L).intValue();
				pollQueue = hubsDao.findPollQueue(ses, maxQueueSize);
			}
			
			//Poll the queue to create persisted stats
			//Hub info is updated accordingly in this method
			PollBusiness.pollHubList(ses, pollQueue, pollTime);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		LogBusiness.addLog(AppConstants.LOG_INFO, "poll", "<b>ENDED JOB</b>");
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
}
