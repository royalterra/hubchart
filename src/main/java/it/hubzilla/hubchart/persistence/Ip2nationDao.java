package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Ip2nationCountries;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.LongType;

public class Ip2nationDao {

	public Ip2nationCountries findNationCodeByIp(Session ses, Long ipAsLong) throws OrmException {
		try {
			String hql = "select c from Ip2nationCountries c, Ip2nation i where "+
					"c.code = i.country and "+
					"i.ip < :l1 "+
					"order by i.ip desc";
			Query q = ses.createQuery(hql);
			q.setFirstResult(0);
			q.setMaxResults(1);
			q.setParameter("l1", ipAsLong, LongType.INSTANCE);
			@SuppressWarnings("rawtypes")
			List list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					return (Ip2nationCountries) list.get(0);
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return null;
	}
	
}
