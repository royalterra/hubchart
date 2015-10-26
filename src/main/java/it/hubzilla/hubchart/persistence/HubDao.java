package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Hubs;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

public class HubDao {
	
	public Hubs findByFqdn(Session ses, String baseUrl) throws OrmException, MalformedURLException {
		Hubs result = null;
		URL url = new URL(baseUrl);
		String fqdn = url.getHost();
		String baseUrlNoWww = baseUrl.replaceAll("://www.", "://");
		URL urlNoWww = new URL(baseUrlNoWww);
		String fqdnNoWww = urlNoWww.getHost();
		try {
			Query q = ses.createQuery("from Hubs h where "+
					"(h.fqdn like :s1 or h.fqdn like :s2) "+
					"order by h.deleted asc");
			q.setParameter("s1", fqdn, StringType.INSTANCE);
			q.setParameter("s2", fqdnNoWww, StringType.INSTANCE);
			q.setMaxResults(1);
			q.setFirstResult(0);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					result = list.get(0);
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Hubs> findAll(Session ses, boolean filterExpired, boolean filterHidden) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Hubs> result = null;		
		try {
			String hql = "from Hubs h where h.deleted = :b1 ";
			if (filterExpired || filterHidden) hql += "and ";
			if (filterExpired) hql += "h.lastSuccessfulPollTime > :dt1 ";
			if (filterExpired && filterHidden) hql += "and ";
			if (filterHidden) hql += "h.hidden = :b2 ";
			hql += "order by h.lastSuccessfulPollTime asc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			if (filterExpired) q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			if (filterHidden) q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Hubs> findLiveHubs(Session ses, boolean excludeEnqueued) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Hubs> result = null;		
		try {
			String hql = "from Hubs h where h.deleted = :b1 and ";
			if (excludeEnqueued) hql += "h.pollQueue is null and ";
			hql += "(h.lastSuccessfulPollTime > :dt1 or h.creationTime > :dt2) "+
					"order by h.lastSuccessfulPollTime asc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			q.setParameter("dt2", lastValidDate, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Hubs> findDeadHubsToCheck(Session ses, int[] checkDays, boolean excludeEnqueued) throws OrmException {
		Calendar cal = new GregorianCalendar();
		List<Hubs> result = new ArrayList<Hubs>();
		for (int i=0; i < checkDays.length; i++) {
			int days = checkDays[i];
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_MONTH, (-1)*days);
			Date endDt = cal.getTime();
			cal.add(Calendar.DAY_OF_MONTH, (-1)*(days+1));
			Date startDt = cal.getTime();
			String hql = "from Hubs h where h.deleted = :b1 and ";
			if (excludeEnqueued) hql += "h.pollQueue is null and ";
			hql += "(h.lastSuccessfulPollTime > :dt1 or h.lastSuccessfulPollTime < :dt2)  "+
					"order by h.lastSuccessfulPollTime asc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("dt1", startDt, TimestampType.INSTANCE);
			q.setParameter("dt2", endDt, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					result.addAll(list);
				}
			}
		}
		return result;
	}
	
	public List<Hubs> findPollQueue(Session ses, int queueSize) throws OrmException {
		List<Hubs> result = null;
		try {
			String hql = "from Hubs h where "+
					"h.pollQueue is not null "+
					"order by h.pollQueue asc";
			Query q = ses.createQuery(hql);
			q.setMaxResults(queueSize);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
//	public List<Hubs> findExpired(Session ses) throws OrmException {
//		Calendar cal = new GregorianCalendar();
//		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
//		Date lastValidDate = cal.getTime();
//		List<Hubs> result = null;
//		try {
//			String hql = "from Hubs h where "+
//					"h.lastSuccessfulPollTime < :dt1";
//			Query q = ses.createQuery(hql);
//			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
//			@SuppressWarnings("unchecked")
//			List<Hubs> list = q.list();
//			result = list;
//		} catch (HibernateException e) {
//			throw new OrmException(e.getMessage(), e);
//		}
//		return result;
//	}
	
	public List<Hubs> findDirectories(Session ses) throws OrmException {
		List<Hubs> result = null;
		try {
			String hql = "from Hubs h where "+
					"(h.directoryMode = :s1 or h.directoryMode = :s2 ) "+
					"order by h.lastSuccessfulPollTime asc";
			Query q = ses.createQuery(hql);
			q.setParameter("s1", AppConstants.DIRECTORY_MODE_PRIMARY, StringType.INSTANCE);
			q.setParameter("s2", AppConstants.DIRECTORY_MODE_SECONDARY, StringType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public Long countLiveHubs(Session ses, boolean onlyHidden, boolean onlyPublic) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		try {
			String hql = "select count(id) from Hubs h where ";
			if (onlyHidden || onlyPublic) hql += "h.hidden = :b2 and ";
			hql += "h.lastSuccessfulPollTime > :dt1 and "+
				"h.deleted = :b1 "+
				"order by h.lastSuccessfulPollTime asc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			if (onlyHidden) q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
			if (onlyPublic) q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			q.setParameter("dt2", lastValidDate, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Object> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					if (list.get(0) instanceof Long) {
						return (Long) list.get(0);
					}
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return null;
	}
	
	public List<Object[]> countLiveHubsByCountry(Session ses, int offset, int pageSize) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.countryCode, h.countryName from Hubs h where "+
					"(h.lastSuccessfulPollTime > :dt1 or h.creationTime > :dt2) and "+
					"h.deleted = :b1 and "+
					"h.countryCode is not null "+
					"group by h.countryCode, h.countryName "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			q.setParameter("dt2", lastValidDate, TimestampType.INSTANCE);
			q.setFirstResult(offset);
			q.setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Object[]> countLiveHubsByLanguage(Session ses, int offset, int pageSize) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.language from Hubs h where "+
					"(h.lastSuccessfulPollTime > :dt1 or h.creationTime > :dt2) and "+
					"h.deleted = :b1 "+
					"group by h.language "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			q.setParameter("dt2", lastValidDate, TimestampType.INSTANCE);
			q.setFirstResult(offset);
			q.setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Object[]> countLiveVersionTags(Session ses) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.versionTag from Hubs h where "+
					"(h.lastSuccessfulPollTime > :dt1 or h.creationTime > :dt2) and "+
					"h.deleted = :b1 and "+
					"h.versionTag is not null and "+
					"h.versionTag != :s1 "+
					"group by h.versionTag "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("s1", "", StringType.INSTANCE);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			q.setParameter("dt2", lastValidDate, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Object[]> countLiveNetworkTypes(Session ses) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.networkType from Hubs h where "+
					"(h.lastSuccessfulPollTime > :dt1 or h.creationTime > :dt2) and "+
					"h.deleted = :b1 and "+
					"h.networkType is not null and "+
					"h.networkType != :s1 "+
					"group by h.networkType "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("s1", AppConstants.NETWORK_TYPE_UNKNOWN, StringType.INSTANCE);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			q.setParameter("dt2", lastValidDate, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public BigInteger findLastPollQueueNumber(Session ses) throws OrmException {
		BigInteger result = null;
		try {
			String hql = "select max(pollQueue) from Hubs h";
			Query q = ses.createQuery(hql);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					if (list.get(0).length > 0) {
						result = (BigInteger) list.get(0)[0];
					}
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
}
