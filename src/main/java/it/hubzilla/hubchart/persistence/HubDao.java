package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Hubs;

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

	public boolean isUnique(Session ses, String baseUrl) throws OrmException {
		String baseUrlHttp = baseUrl.replaceAll("https:", "http:");
		String baseUrlHttps = baseUrl.replaceAll("http:", "https:");
		String baseUrlNoWww = baseUrl.replaceAll("://www.", "://");
		try {
			Query q = ses.createQuery("from Hubs h where "+
					"h.baseUrl like :s1 or h.baseUrl like :s2 or h.baseUrl like :s3");
			q.setParameter("s1", baseUrlHttp, StringType.INSTANCE);
			q.setParameter("s2", baseUrlHttps, StringType.INSTANCE);
			q.setParameter("s3", baseUrlNoWww, StringType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			if (list == null) return true;
			if (list.size() == 0) return true;
			return false;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public Hubs findByBaseUrl(Session ses, String baseUrl) throws OrmException {
		Hubs result = null;
		String baseUrlHttp = baseUrl.replaceAll("https:", "http:");
		String baseUrlHttps = baseUrl.replaceAll("http:", "https:");
		try {
			Query q = ses.createQuery("from Hubs h where "+
					"h.baseUrl like :s1 or h.baseUrl like :s2 "+
					"order by h.deleted asc");
			q.setParameter("s1", baseUrlHttp, StringType.INSTANCE);
			q.setParameter("s2", baseUrlHttps, StringType.INSTANCE);
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
	
	public List<Hubs> findExpired(Session ses) throws OrmException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.HUB_EXPIRATION_DAYS);
		Date lastValidDate = cal.getTime();
		List<Hubs> result = null;
		try {
			String hql = "from Hubs h where "+
					"h.lastSuccessfulPollTime < :dt1";
			Query q = ses.createQuery(hql);
			q.setParameter("dt1", lastValidDate, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public Long countHiddenHubs(Session ses) throws OrmException {
		try {
			String hql = "select count(id) from Hubs h where "+
				"h.deleted = :b1 and "+
				"h.hidden = :b2 "+
				"order by h.lastSuccessfulPollTime asc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
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
	
	public List<Object[]> countHubsByCountry(Session ses, int offset, int pageSize) throws OrmException {
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.countryCode, h.countryName from Hubs h where "+
					"h.deleted = :b1 and "+
					"h.countryCode is not null "+
					"group by h.countryCode, h.countryName "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
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
	
	public List<Object[]> countHubsByLanguage(Session ses, int offset, int pageSize) throws OrmException {
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.language from Hubs h where "+
					"h.deleted = :b1 "+
					"group by h.language "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
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
	
	public List<Object[]> countVersionTags(Session ses) throws OrmException {
		List<Object[]> result = null;
		try {
			String hql = "select count(h.id) as liveHubs, h.versionTag from Hubs h where "+
					"h.deleted = :b1 and "+
					"h.versionTag is not null and "+
					"h.versionTag != :s1 "+
					"group by h.versionTag "+
					"order by liveHubs desc";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			q.setParameter("s1", "", StringType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
}
