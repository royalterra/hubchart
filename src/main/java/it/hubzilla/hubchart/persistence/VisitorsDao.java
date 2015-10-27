package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Visitors;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;

public class VisitorsDao {

	public List<Visitors> findVisitors(Session ses) throws OrmException {
		List<Visitors> result = null;
		try {
			Query q = ses.createQuery("from Visitors v "+
					"order by v.id desc");
			@SuppressWarnings("unchecked")
			List<Visitors> list = q.list();
			if (list != null) {
				result = list;
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public void addVisitor(Session ses, String countryCode, boolean newVisitor) throws OrmException {
		Visitors v = new Visitors();
		v.setCountryCode(countryCode);
		v.setNewVisitor(newVisitor);
		v.setTime(new Date());
		GenericDao.saveGeneric(ses, v);
	}
	
	public void deleteVisitors(Session ses, Date fromDate) throws OrmException {
		try {
			Query q = ses.createQuery("delete from Visitors "+
					"where time < :dt1");
			q.setParameter("dt1", fromDate, DateType.INSTANCE);
			q.executeUpdate();
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
}
