package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;

public class StatisticsDao {
	
	public List<Statistics> findLatest(Session ses, boolean filterHidden,
			int page, int pageSize, String orderBy
			) throws OrmException {
		List<Statistics> result = null;
		if (orderBy == null) orderBy="";
		if (orderBy.equals("")) orderBy="activeChannelsLast6Months desc"; 
		try {
			String hql="from Statistics hs where ";
			if (filterHidden) hql += "hs.hub.hidden = :b3 and ";
			hql += "hs.id = hs.hub.idLastHubStats order by hs."+orderBy;
			Query q = ses.createQuery(hql);
			if (filterHidden) q.setParameter("b3", Boolean.FALSE, BooleanType.INSTANCE);
			q.setFirstResult(page*pageSize);
			q.setMaxResults(pageSize);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public List<Statistics> findByHub(Session ses, Integer hubId, Date beginDt, Date endDt) throws OrmException {
		try {
			Query q = ses.createQuery("from Statistics hs where "+
					"hs.hub.id = :id1 and "+
					"hs.pollTime >= :dt1 and "+
					"hs.pollTime <= :dt2 "+
					"order by hs.pollTime asc");
			q.setParameter("id1", hubId, IntegerType.INSTANCE);
			q.setParameter("dt1", beginDt, TimestampType.INSTANCE);
			q.setParameter("dt2", endDt, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
			return list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public List<Statistics> findByHub(Session ses, Integer hubId) throws OrmException {
		try {
			Query q = ses.createQuery("from Statistics hs where "+
					"hs.hub.id = :id1 ");
			q.setParameter("id1", hubId, IntegerType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
			return list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public List<Statistics> findByNewestHub(Session ses, int offset, int pageSize) throws OrmException {
		List<Statistics> result = new ArrayList<Statistics>();
		try {
			String hql = "from Hubs where "
					+ "lastSuccessfulPollTime is not null "
					+ "and registrationPolicy is not null "
					+ "and hidden = :b1 "
					+ "order by creationTime desc";
			Query q = ses.createQuery(hql);
			q.setFirstResult(offset);
			q.setMaxResults(pageSize);
			q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Hubs> list = q.list();
			for (Hubs hub:list) {
				Statistics stat = findLastStatsByHub(ses, hub.getId());
				result.add(stat);
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public Statistics findOldestStatsByHub(Session ses, Integer hubId) throws OrmException {
		try {
			Query q = ses.createQuery("from Statistics hs where "+
					"hs.hub.id = :id1 "+
					"order by hs.pollTime asc");
			q.setParameter("id1", hubId, IntegerType.INSTANCE);
			q.setMaxResults(1);
			q.setFirstResult(0);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					return list.get(0);
				}
			}
			return null;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public Statistics findLastStatsByHub(Session ses, Integer hubId) throws OrmException {
		try {
			Query q = ses.createQuery("from Statistics hs where "+
					"hs.hub.id = :id1 "+
					"order by hs.pollTime desc");
			q.setParameter("id1", hubId);
			q.setMaxResults(1);
			q.setFirstResult(0);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					return list.get(0);
				}
			}
			return null;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public Statistics findLastGlobalStats(Session ses) throws OrmException {
		Statistics result = null;
		try {
			Query q = ses.createQuery("from Statistics gs where "+
					"gs.hub is null "+
					"order by gs.pollTime desc");
			q.setFirstResult(0);
			q.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
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
	
	public List<Statistics> findGlobalStats(Session ses, Date beginDt, Date endDt) throws OrmException {
		try {
			Query q = ses.createQuery("from Statistics gs where "+
					"gs.hub is null and "+
					"gs.pollTime >= :dt1 and "+
					"gs.pollTime <= :dt2 "+
					"order by gs.pollTime asc");
			q.setParameter("dt1", beginDt, TimestampType.INSTANCE);
			q.setParameter("dt2", endDt, TimestampType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
			return list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public Statistics findFirstGlobalStats(Session ses) throws OrmException {
		Statistics result = null;
		try {
			Query q = ses.createQuery("from Statistics gs where "+
					"gs.hub is null "+
					"order by gs.pollTime asc");
			q.setFirstResult(0);
			q.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<Statistics> list = q.list();
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
}
