package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.beans.StatisticBean;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;
import it.hubzilla.hubchart.persistence.StatisticsDao;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;

public class StatisticBusiness {

	private static StatisticsDao statisticsDao = new StatisticsDao();
	
	public static StatisticBean findLastStatisticBeanByFqdn(String fqdn) throws OrmException {
		StatisticBean result = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			Hubs hub = new HubsDao().findByFqdn(ses, fqdn);
			Statistics stat = statisticsDao.findLastStatsByHub(ses, hub.getId());
			result = new StatisticBean();
			PropertyUtils.copyProperties(result, stat);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (MalformedURLException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
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
	
}
