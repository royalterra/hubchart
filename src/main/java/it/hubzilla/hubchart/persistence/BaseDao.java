package it.hubzilla.hubchart.persistence;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface BaseDao<T> {
	
	public void update(Session ses, T instance) throws HibernateException;
	
	public Serializable save(Session ses, T transientInstance) throws HibernateException;
	
	public void delete(Session ses, T instance) throws HibernateException;
	
}
