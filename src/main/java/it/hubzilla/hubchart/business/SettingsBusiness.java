package it.hubzilla.hubchart.business;

import org.hibernate.Session;
import org.hibernate.Transaction;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Settings;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.SettingsDao;

public class SettingsBusiness {

	public static String getAccessKey() throws OrmException {
		return getValue(AppConstants.SETTINGS_ACCESS_KEY);
	}
	
	public static void setAccessKey(String value) throws OrmException {
		setValue(AppConstants.SETTINGS_ACCESS_KEY, value);
	}
	
	public static Integer getPollCount(Session ses) throws OrmException, NumberFormatException {
		Settings s = new SettingsDao().findByName(ses, AppConstants.SETTINGS_POLL_COUNT);
		if (s != null) {
			String valueS = s.getValue();
			if (valueS != null) {
				return Integer.parseInt(valueS);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public static void setPollCount(Session ses, Integer value) throws OrmException {
		new SettingsDao().saveOrUpdateValueByName(ses, AppConstants.SETTINGS_POLL_COUNT, value.toString());
	}
	
	public static String getValue(String name) throws OrmException {
		String result = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			Settings s = new SettingsDao().findByName(ses, name);
			if (s != null) result = s.getValue();
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static void setValue(String name, String value) throws OrmException {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			new SettingsDao().saveOrUpdateValueByName(ses, name, value);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}
