package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.UrlException;
import it.hubzilla.hubchart.beans.CountryStatBean;
import it.hubzilla.hubchart.beans.LanguageStatBean;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Languages;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;
import it.hubzilla.hubchart.persistence.LogsDao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class HubBusiness {
	
	//private static final Logger LOG = LoggerFactory.getLogger(HubBusiness.class);
	private static HubsDao hubsDao = new HubsDao();
	
	public static Hubs addHub(String baseUrl) throws BusinessException, OrmException,
			MalformedURLException {
		Hubs result = null;
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Date pollTime = new Date();
			//Check uniqueness
			Hubs hub = hubsDao.findByBaseUrlFqdn(ses, baseUrl);
			if (hub != null) throw new BusinessException(baseUrl+" is a known hub and cannot be added");
			
			//Doesn't exist, a new one is created
			hub = new Hubs();
			hub.setBaseUrl(baseUrl);
			hub.setFqdn(new URL(baseUrl).getHost());
			hub.setNetworkType(AppConstants.NETWORK_TYPE_UNKNOWN);
			hub.setLastSuccessfulPollTime(null);
			hub.setCreationTime(pollTime);
			hub.setIdLastHubStats(0);
			hub.setHidden(false);
			Integer id = (Integer) GenericDao.saveGeneric(ses, hub);
			
			//First poll
			try {
				retrieveStats(ses, hub, pollTime);
			} catch (UrlException e) {
				//Exceptions are only logged
				new LogsDao().addLog(ses, AppConstants.LOG_INFO, "hub", hub.getFqdn()+": "+e.getMessage());
			}
			result = GenericDao.findById(ses, Hubs.class, id);
					
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new OrmException(e.getMessage(), e);
		} catch (BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static Hubs attemptToReviveHub(String baseUrl) throws BusinessException, OrmException,
			MalformedURLException {
		Hubs result = null;
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Date pollTime = new Date();
			//Exists
			Hubs hub = hubsDao.findByBaseUrlFqdn(ses, baseUrl);
			if (hub == null) throw new BusinessException(baseUrl+" is not a known hub");
			try {
				retrieveStats(ses, hub, pollTime);
			} catch (UrlException e) {
				//Exceptions are only logged
				new LogsDao().addLog(ses, AppConstants.LOG_INFO, "hub", hub.getFqdn()+": "+e.getMessage());
			}
			result = hub;
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new OrmException(e.getMessage(), e);
		} catch (BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	private static void retrieveStats(Session ses, Hubs hub, Date pollTime) throws OrmException, UrlException {
		//First poll
		Statistics stats = new Statistics();
		stats.setHub(hub);
		stats.setPollTime(pollTime);
		stats = PollBusiness.retrieveTransientStats(ses, hub, pollTime);
		Integer idStats = (Integer) GenericDao.saveGeneric(ses, stats);
		hub.setIdLastHubStats(idStats);
		hub.setLastSuccessfulPollTime(pollTime);
		GenericDao.updateGeneric(ses, hub.getId(), hub);
	}

	public static Hubs findHubById(Integer id) throws OrmException {
		Hubs result = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			result = GenericDao.findById(ses, Hubs.class, id);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<Hubs> findAllHubs(boolean filterExpired, boolean filterHidden) throws OrmException {
		List<Hubs> result = new ArrayList<Hubs>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			result = hubsDao.findAll(ses, filterExpired, filterHidden);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	public static List<Hubs> findRecentlyExpiredHubs(int offset, int pageSize) throws OrmException {
		List<Hubs> result = new ArrayList<Hubs>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Hubs> statList = hubsDao.findRecentlyExpiredHub(ses, offset, pageSize);
			result = statList;
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static Integer countHiddenHubs() throws OrmException {
		Integer result = 0;
		Session ses = HibernateSessionFactory.getSession();
		try {
			Long count = hubsDao.countLiveHiddenHubs(ses);
			if (count != null) result = count.intValue();
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<Hubs> findDirectories() throws OrmException {
		List<Hubs> result = new ArrayList<Hubs>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			result = hubsDao.findDirectories(ses);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	
	public static String stripLeadingWww(String fqdn) {
		String fqdnNoWww = fqdn.replaceAll("^www\\.", "");
		return fqdnNoWww;
	}
	
	public static String cleanBaseUrl(String baseUrl) {
		baseUrl = baseUrl.trim().replaceAll("/$", "");//Clean from spaces and trailing /
		baseUrl = baseUrl.replaceAll(":80$", "");
		baseUrl = baseUrl.replaceAll(":80/", "/");
		baseUrl = baseUrl.replaceAll(":443$", "");
		baseUrl = baseUrl.replaceAll(":443/", "/");
		return baseUrl;
	}
	
	public static List<CountryStatBean> findCountryStatBeans(Integer page, Integer pageSize) throws OrmException {
		List<CountryStatBean> result = new ArrayList<CountryStatBean>();
		if (page == null) page = 0;
		if (pageSize == null) pageSize = 0;
		int offset = page*pageSize;
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubsDao.countLiveHubsByCountry(ses, offset, pageSize);
			for (Object[] obj:list) {
				try {
					CountryStatBean cs = new CountryStatBean();
					cs.setLiveHubs(((Long)obj[0]).intValue());
					cs.setCountryCode((String)obj[1]);
					cs.setCountryName((String)obj[2]);
					result.add(cs);
				} catch (Exception e) {/*ignore cast and nullpointer*/}
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static Integer findCountryStatCount() throws OrmException {
		Session ses = HibernateSessionFactory.getSession();
		Integer result = -1;
		try {
			List<Object[]> list = hubsDao.countLiveHubsByCountry(ses, 0, Integer.MAX_VALUE);
			result = list.size();
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<LanguageStatBean> findLanguageStatBeans(Integer page, Integer pageSize) throws OrmException {
		List<LanguageStatBean> result = new ArrayList<LanguageStatBean>();
		if (page == null) page = 0;
		if (pageSize == null) pageSize = 0;
		int offset = page*pageSize;
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubsDao.countLiveHubsByLanguage(ses, offset, pageSize);
			for (Object[] obj:list) {
				try {
					LanguageStatBean cs = new LanguageStatBean();
					cs.setLiveHubs(((Long)obj[0]).intValue());
					cs.setLanguage((Languages)obj[1]);
					result.add(cs);
				} catch (Exception e) {/*ignore cast and nullpointer*/}
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static Integer findLanguageStatCount() throws OrmException {
		Integer result = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubsDao.countLiveHubsByLanguage(ses, 0, Integer.MAX_VALUE);
			result = list.size();
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
}
