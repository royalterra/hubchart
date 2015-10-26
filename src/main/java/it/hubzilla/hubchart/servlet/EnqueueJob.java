package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubDao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnqueueJob implements Job {
	
	private final Logger LOG = LoggerFactory.getLogger(EnqueueJob.class);
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		
		//param: afterDeathCheckDays[]
		String afterDeathCheckDaysParam = (String) jobCtx.getMergedJobDataMap().get("afterDeathCheckDays");
		if (afterDeathCheckDaysParam == null) throw new JobExecutionException("afterDeathCheckDays is undefined");
		if (afterDeathCheckDaysParam.equals("")) throw new JobExecutionException("afterDeathCheckDays is undefined");
		String[] afterDeathCheckDaysS = afterDeathCheckDaysParam.split(AppConstants.STRING_SEPARATOR);
		int[] afterDeathCheckDays = new int[afterDeathCheckDaysS.length];
		for (int i=0; i<afterDeathCheckDaysS.length; i++) {
			afterDeathCheckDays[i] = Integer.parseInt(afterDeathCheckDaysS[i]);
		}
		
		//Job body
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			HubDao hubDao = new HubDao();
			
			List<Hubs> hubsToPoll = new ArrayList<Hubs>();
			//Find live hubs to poll
			List<Hubs> liveHubsToPoll = hubDao.findLiveAndNewHubs(ses, true);
			LOG.info("Live hubs to poll: "+liveHubsToPoll.size());
			//Find dead hubs to check if really dead
			List<Hubs> deadHubsToPoll = hubDao.findDeadHubsToCheck(ses, afterDeathCheckDays, true);
			LOG.info("Dead hubs to poll: "+deadHubsToPoll.size());
			hubsToPoll.addAll(liveHubsToPoll);
			hubsToPoll.addAll(deadHubsToPoll);
			
			//Find last queue number
			BigInteger number = hubDao.findLastPollQueueNumber(ses);
			
			//Enqueue found hubs
			for(Hubs hub:hubsToPoll) {
				number=number.add(new BigInteger("1"));
				hub.setPollQueue(number);
				GenericDao.updateGeneric(ses, hub.getId(), hub);
			}
			
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
