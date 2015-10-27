package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Logs;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;

public class LogsDao {

	public List<Logs> findLogs(Session ses) throws OrmException {
		List<Logs> result = null;
		try {
			Query q = ses.createQuery("from Logs l "+
					"order by l.id ");
			@SuppressWarnings("unchecked")
			List<Logs> list = q.list();
			if (list != null) {
				result = list;
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public void addLog(Session ses, String level, String message) throws OrmException {
		Logs log = new Logs();
		log.setLevel(level);
		log.setMessage(message);
		log.setTime(new Date());
		GenericDao.saveGeneric(ses, log);
	}
	
	public void deleteLogs(Session ses, Date fromDate) throws OrmException {
		try {
			Query q = ses.createQuery("delete from Logs "+
					"where time < :dt1");
			q.setParameter("dt1", fromDate, DateType.INSTANCE);
			q.executeUpdate();
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
}
