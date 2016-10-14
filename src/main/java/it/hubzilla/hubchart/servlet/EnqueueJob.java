package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.LogBusiness;
import it.hubzilla.hubchart.business.SettingsBusiness;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;
import it.hubzilla.hubchart.persistence.LogsDao;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		LogBusiness.addLog(AppConstants.LOG_INFO, "enqueue", "<b>STARTED JOB</b>");
		
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
		String exceptionMsg = null;
		try {
			LogsDao logsDao = new LogsDao();
			HubsDao hubsDao = new HubsDao();
		
			Set<Hubs> hubsToPoll = new HashSet<Hubs>();
			//Find live hubs to poll
			List<Hubs> liveHubsToPoll = hubsDao.findLiveHubs(ses, false);
			logsDao.addLog(ses, AppConstants.LOG_INFO, "enqueue", "Live hubs to poll: "+liveHubsToPoll.size());
			//Find dead hubs to check if really dead
			List<Hubs> deadHubsToPoll = hubsDao.findDeadHubsToCheck(ses, afterDeathCheckDays, true);
			logsDao.addLog(ses, AppConstants.LOG_INFO, "enqueue", "Dead hubs to poll: "+deadHubsToPoll.size());
			//Find new hubs to poll
			List<Hubs> newHubsToPoll = hubsDao.findNewHubs(ses, false);
			logsDao.addLog(ses, AppConstants.LOG_INFO, "enqueue", "New hubs to poll: "+newHubsToPoll.size()+" (live or dead)");
			hubsToPoll.addAll(liveHubsToPoll);
			hubsToPoll.addAll(newHubsToPoll);
			hubsToPoll.addAll(deadHubsToPoll);
			logsDao.addLog(ses, AppConstants.LOG_INFO, "enqueue", "Total hubs to poll: "+hubsToPoll.size());
			
			//Find last queue number
			BigInteger number = hubsDao.findLastPollQueueNumber(ses);
			
			//Enqueue found hubs
			for(Hubs hub:hubsToPoll) {
				number=number.add(new BigInteger("1"));
				hub.setPollQueue(number);
				GenericDao.updateGeneric(ses, hub.getId(), hub);
			}
			Long hubsCount = hubsDao.countHubsToPoll(ses);
			SettingsBusiness.setPollCount(ses, hubsCount.intValue());
			
			trn.commit();
		} catch (OrmException e) {
			exceptionMsg = e.getClass().getSimpleName()+" "+e.getMessage();
			trn.rollback();
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
			if (exceptionMsg != null) LogBusiness.addLog(AppConstants.LOG_ERROR, "poll",
					"<b>"+exceptionMsg+"</b>");
		}
				
		LogBusiness.addLog(AppConstants.LOG_INFO, "enqueue", "<b>ENDED JOB</b>");
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
}
