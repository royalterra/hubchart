package it.hubzilla.hubchart.persistence;


import it.hubzilla.hubchart.OrmException;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class GenericDao {

//	private static final Logger LOG = Logger.getLogger(GenericDao.class);
	
	private GenericDao() {}
	
	public static void updateGeneric(Session ses, Serializable key, Object instance) throws OrmException {
		Object persistent = ses.get(instance.getClass(), key);
		try {
			PropertyUtils.copyProperties(persistent, instance);
			ses.update(persistent);
		} catch (Exception e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	public static Serializable saveGeneric(Session ses, Object transientInstance) throws OrmException {
		try {
			ses.setFlushMode(FlushMode.ALWAYS);
			//ses.evict(transientInstance);//Rimuove dalla cache eventuali altri oggetti con identico id
			Serializable key = ses.save(transientInstance);
			return key;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}

	public static void deleteGeneric(Session ses, Serializable id, Object instance) throws OrmException {
		try {
			@SuppressWarnings("rawtypes")
			Class instanceClass = instance.getClass();
			String qs = "delete from "+instanceClass.getSimpleName()+" where id=:id";
			Query q = ses.createQuery(qs);
			if (id instanceof Integer) {
				q.setParameter("id", id, IntegerType.INSTANCE);
				q.executeUpdate();
				return;
			}
			if (id instanceof String) {
				q.setParameter("id", id, StringType.INSTANCE);
				q.executeUpdate();
				return;
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		throw new OrmException("Delete: id is not Integer nor String type");
	}

	@SuppressWarnings("unchecked")
	public static <S> S findById(Session ses, Class<S> findClass, Serializable key) throws OrmException {
		try {
			S obj = (S)ses.get(findClass, key);
			return obj;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S> List<S> findByProperty(Session ses, Class<S> findClass, String propertyName, Object value) throws OrmException {
		try {
			List<S> listS = null;
			String hql = "from " + findClass.getCanonicalName() + " o"+
					"o." + propertyName + " = :p1";
			Query q = ses.createQuery(hql);
			q.setParameter("p1", value);
			listS = (List<S>) q.list();
			return listS;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <S> List<S> findByClass(Session ses, Class<S> findClass, String orderProperty) throws OrmException {
		try {
			List<S> listS = null;
			String hql = "select o from " + findClass.getCanonicalName() + " as o " +
					"order by o."+orderProperty+" asc";
			Query q = ses.createQuery(hql);
			listS = (List<S>) q.list();
			return listS;
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <S> S findUniqueResult(Session ses, Class<S> findClass, String propertyName, Object value) throws NonUniqueResultException {
		S obj = null;
		String hql = "from " + findClass.getCanonicalName() + " o"+
				"o." + propertyName + " = :p1";
		Query q = ses.createQuery(hql);
		q.setParameter("p1", value);
		obj = (S) q.uniqueResult();
		return obj;
	}

}