package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Settings;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StringType;

public class SettingsDao {

	public Settings findByName(Session ses, String name) throws OrmException {
		Settings s = null;
		try {
			Query q = ses.createQuery("from Settings s where "+
					"s.name = :s1 ");
			q.setParameter("s1", name, StringType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Settings> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					s = list.get(0);
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return s;
	}
	
	public Serializable saveOrUpdateValueByName(Session ses, String name, String value) throws OrmException {
		Settings s = findByName(ses, name);
		Serializable id = null;
		if (s == null) {
			s = new Settings();
			s.setName(name);
			s.setValue(value);
			id = GenericDao.saveGeneric(ses, s);
		} else {
			s.setValue(value);
			GenericDao.updateGeneric(ses, s.getId(), s);
			id = s.getId();
		}
		return id;
	}
}
