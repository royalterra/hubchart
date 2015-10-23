package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.UrlException;
import it.hubzilla.hubchart.beans.CountryStatBean;
import it.hubzilla.hubchart.beans.LanguageStatBean;
import it.hubzilla.hubchart.beans.NetworkTypeStatBean;
import it.hubzilla.hubchart.beans.StatisticBean;
import it.hubzilla.hubchart.beans.VersionTagStatBean;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Languages;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubDao;
import it.hubzilla.hubchart.persistence.StatisticsDao;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HubBusiness {
	
	private static final int MAX_VERSIONS_SHOWN = 4;
	
	//private static final Logger LOG = LoggerFactory.getLogger(HubBusiness.class);
	private static HubDao hubDao = new HubDao();
	private static StatisticsDao statisticsDao = new StatisticsDao();
	
	public static Integer addHub(String baseUrl) throws BusinessException, OrmException,
			MalformedURLException {
		Integer id = null;
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Date pollTime = new Date();
			//Check uniqueness
			Hubs hub = hubDao.findByFqdn(ses, baseUrl);
			if (hub != null) throw new BusinessException(baseUrl+" is a known hub and cannot be added");
			
			//Doesn't exist, a new one is created
			hub = new Hubs();
			hub.setBaseUrl(baseUrl);
			hub.setFqdn(new URL(baseUrl).getHost());
			hub.setNetworkType(AppConstants.NETWORK_TYPE_UNKNOWN);
			hub.setLastSuccessfulPollTime(AppConstants.DATE_FAR_PAST);
			hub.setCreationTime(pollTime);
			hub.setIdLastHubStats(0);
			hub.setDeleted(false);
			hub.setHidden(false);
			id = (Integer) GenericDao.saveGeneric(ses, hub);
			
			//First poll
			retrieveStats(ses, hub, pollTime);
			
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
		return id;
	}
	
	public static Integer reviveHub(String baseUrl) throws BusinessException, OrmException,
			MalformedURLException {
		Integer id = null;
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Date pollTime = new Date();
			//Exists
			Hubs hub = hubDao.findByFqdn(ses, baseUrl);
			if (hub == null) throw new BusinessException(baseUrl+" is not a known hub");
			if (hub.getDeleted()) {
				retrieveStats(ses, hub, pollTime);
			} else {
				//It's ok, a live hub doesn't need to be revived
			}
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
		return id;
	}
	private static void retrieveStats(Session ses, Hubs hub, Date pollTime) throws OrmException {
		//First poll
		Statistics stats = new Statistics();
		stats.setHub(hub);
		stats.setPollTime(pollTime);
		try {
			stats = PollBusiness.retrieveTransientStats(ses, hub, pollTime);
		} catch (UrlException e) {/* ignore errors from http requests */}
		Integer idStats = (Integer) GenericDao.saveGeneric(ses, stats);
		hub.setIdLastHubStats(idStats);
		GenericDao.updateGeneric(ses, hub.getId(), hub);
	}

	public static List<StatisticBean> findLatestStatisticBeans(
			boolean filterExpired, boolean filterHidden,
			int page, int pageSize, String orderBy
			) throws OrmException {
		List<StatisticBean> result = new ArrayList<StatisticBean>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Statistics> sList = statisticsDao.findLatest(ses,
					filterHidden, page, pageSize, orderBy);
			for (Statistics s:sList) {
				StatisticBean bean = new StatisticBean();
				PropertyUtils.copyProperties(bean, s);
				result.add(bean);
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<StatisticBean> findStatisticsForPresentation(
			boolean filterExpired, boolean filterHidden,
			String orderCode, boolean isAsc, int page, int pageSize)
					throws OrmException{
		String orderBy = AppConstants.ORDER_TYPES.get(orderCode);
		if (orderBy != null) {
			if (isAsc) {
				orderBy += " asc";
			} else {
				orderBy += " desc";
			}
		}
		return findLatestStatisticBeans(filterExpired, filterHidden,
				page, pageSize, orderBy);
	}
	
	public static Integer findStatisticsForPresentationCount(
			boolean filterExpired, boolean filterEmptyStats, boolean filterHidden)
					throws OrmException{
		List<StatisticBean> sbList = findLatestStatisticBeans(filterExpired, filterHidden,
				0, Integer.MAX_VALUE, null);
		return sbList.size();
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
			result = hubDao.findAll(ses, filterExpired, filterHidden);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<StatisticBean> findNewestHubStatistics(int offset, int pageSize) throws OrmException {
		List<StatisticBean> result = new ArrayList<StatisticBean>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Statistics> statList = statisticsDao.findByNewestHub(ses, offset, pageSize);
			for (Statistics stat:statList) {
				StatisticBean bean = new StatisticBean();
				PropertyUtils.copyProperties(bean, stat);
				result.add(bean);
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
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
			Long count = hubDao.countLiveHiddenHubs(ses);
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
			result = hubDao.findDirectories(ses);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
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
			List<Object[]> list = hubDao.countLiveHubsByCountry(ses, offset, pageSize);
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
			List<Object[]> list = hubDao.countLiveHubsByCountry(ses, 0, Integer.MAX_VALUE);
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
			List<Object[]> list = hubDao.countLiveHubsByLanguage(ses, offset, pageSize);
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
			List<Object[]> list = hubDao.countLiveHubsByLanguage(ses, 0, Integer.MAX_VALUE);
			result = list.size();
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static String printVersionTagStat() throws OrmException {
		String result ="--";
		StatisticBean gs = PollBusiness.findLatestGlobalStats();
		if (gs != null) {
			List<VersionTagStatBean> stats = findVersionTagStatBeans(gs.getActiveHubs());
			if (stats != null) {
				if (stats.size() > 0) {
					result = "";
					int max = MAX_VERSIONS_SHOWN;
					if (stats.size() < MAX_VERSIONS_SHOWN) max = stats.size();
					for (int i=0; i<max; i++) {
						result += "<b>"+stats.get(i).getVersionTag()+"</b> ("+stats.get(i).getPercentage()+") ";
					}
				}
			}
		}
		return result;
	}
	
	public static List<VersionTagStatBean> findVersionTagStatBeans(Integer totalHubs) throws OrmException {
		List<VersionTagStatBean> result = new ArrayList<VersionTagStatBean>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubDao.countLiveVersionTags(ses);
			for (Object[] obj:list) {
				try {
					VersionTagStatBean vts = new VersionTagStatBean(totalHubs);
					vts.setLiveHubs(((Long)obj[0]).intValue());
					vts.setVersionTag((String)obj[1]);
					result.add(vts);
				} catch (Exception e) {/*ignore cast and nullpointer*/}
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static String printNetworkTypeStat() throws OrmException {
		String result ="--";
		StatisticBean gs = PollBusiness.findLatestGlobalStats();
		if (gs != null) {
			List<NetworkTypeStatBean> stats = findNetworkTypeStatBeans(gs.getActiveHubs());
			if (stats != null) {
				if (stats.size() > 0) {
					result = "";
					int max = MAX_VERSIONS_SHOWN;
					if (stats.size() < MAX_VERSIONS_SHOWN) max = stats.size();
					for (int i=0; i<max; i++) {
						String networkTypeIcon = AppConstants.NETWORK_ICONS.get(stats.get(i).getNetworkTypeName());
						result += "&nbsp;<img src='"+networkTypeIcon+
								"' title='"+stats.get(i).getNetworkTypeName()+
								"'/> "+stats.get(i).getPercentage()+" ";
					}
				}
			}
		}
		return result;
	}
	
	public static List<NetworkTypeStatBean> findNetworkTypeStatBeans(Integer totalHubs) throws OrmException {
		List<NetworkTypeStatBean> result = new ArrayList<NetworkTypeStatBean>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubDao.countLiveNetworkTypes(ses);
			for (Object[] obj:list) {
				try {
					NetworkTypeStatBean vts = new NetworkTypeStatBean(totalHubs);
					vts.setLiveHubs(((Long)obj[0]).intValue());
					vts.setNetworkTypeName((String)obj[1]);
					result.add(vts);
				} catch (Exception e) {/*ignore cast and nullpointer*/}
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
}
