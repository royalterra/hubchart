package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.FeedEntries;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.TimestampType;

public class FeedEntriesDao {
	
	public List<FeedEntries> findLatest(Session ses) throws OrmException {
		List<FeedEntries> result = null;
		try {
			Query q = ses.createQuery("from FeedEntries fe order by fe.publishedDate desc");
			@SuppressWarnings("unchecked")
			List<FeedEntries> list = q.list();
			result = list;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public Integer deleteOlder(Session ses, Date beginDt) throws OrmException {
		Integer result = 0;
		try {
			Query q = ses.createQuery("delete from FeedEntries where "+
					"publishedDate <= :dt1");
			q.setParameter("dt1", beginDt, TimestampType.INSTANCE);
			result = new Integer(q.executeUpdate());
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
}
