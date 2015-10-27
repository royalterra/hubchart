package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Ip2nationCountries;
import it.hubzilla.hubchart.model.Visitors;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.Ip2nationDao;
import it.hubzilla.hubchart.persistence.VisitorsDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisitorBusiness {
	
	private static final Logger LOG = LoggerFactory.getLogger(VisitorBusiness.class);
	
	public static void parse(HttpServletRequest request, HttpServletResponse response) {
		//Country code
		String ip = request.getRemoteAddr();
		if (ip == null) return; /* exit silently */
		String countryCode = null;
		try {
			countryCode = ipToCountryCode(ip);
		} catch (OrmException e) {
			LOG.error(e.getMessage(), e);
		}
		if (countryCode == null) return; /* exit silently */
		
		//New visitor
		boolean newVisitor = false;
		String value = getCookie(request, "hubchart");
		if (value == null) {
			newVisitor = true;
			setCookie(response, "hubchart", "true");
		}

		addVisitor(countryCode, newVisitor);
	}
	
	private static String ipToCountryCode(String ip) throws OrmException{
		String countryCode = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			Ip2nationCountries c = new Ip2nationDao().findNationCodeByIp(ses, PollBusiness.ipToLong(ip));
			if (c != null) countryCode = c.getCode();
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return countryCode;
	}
	
	public static List<Visitors> findVisitors() throws OrmException {
		List<Visitors> result = new ArrayList<Visitors>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Visitors> list = new VisitorsDao().findVisitors(ses);
			if (list != null) result = list;
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static void addVisitor(String countryCode, boolean newVisitor) {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			new VisitorsDao().addVisitor(ses, countryCode, newVisitor);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			//throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public static void deleteOldVisitors() {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.VISITOR_EXPIRATION_DAYS);
		Date fromDate = cal.getTime();
		try {
			new VisitorsDao().deleteVisitors(ses, fromDate);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			//throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static void setCookie(HttpServletResponse response, String cookieName, String value) {
		Cookie cookie = new Cookie(cookieName, value);
		response.addCookie(cookie);
	}
	private static String getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		String value = null;
		for(Cookie cookie : cookies){
		    if(cookieName.equals(cookie.getName())){
		        value = cookie.getValue();
		    }
		}
		return value;
	}
}
