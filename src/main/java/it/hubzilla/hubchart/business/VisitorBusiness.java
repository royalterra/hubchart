package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Ip2nationCountries;
import it.hubzilla.hubchart.model.Visitors;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.Ip2nationDao;
import it.hubzilla.hubchart.persistence.VisitorsDao;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisitorBusiness {
	
	private static final Logger LOG = LoggerFactory.getLogger(VisitorBusiness.class);
	
	public static void parse(HttpServletRequest request, HttpServletResponse response) {
		//Country code
		String ip = getRemoteAddr(request);
		if (ip == null) return; /* exit silently */
		String countryCode = null;
		try {
			countryCode = ipToCountryCode(ip);
		} catch (OrmException e) {
			LOG.error(e.getMessage(), e);
		}
		if (countryCode == null) return; /* exit silently */
		
		//IP hash
		String ipHash = getMd5ShortenedHash(ip);
		
		//New visitor
		boolean newVisitor = false;
		String value = getCookie(request, "hubchart");
		if (value == null) {
			newVisitor = true;
			setCookie(response, "hubchart", "true");
		}

		addVisitor(countryCode, ipHash, newVisitor);
	}
	
	private static String ipToCountryCode(String ip) throws OrmException{
		String countryCode = null;
		if (ip != null) {
			if (ip.equals("127.0.0.1")) return "--";
			Session ses = HibernateSessionFactory.getSession();
			try {
				Ip2nationCountries c = new Ip2nationDao().findNationCodeByIp(ses, PollBusiness.ipToLong(ip));
				if (c != null) countryCode = c.getCode();
			} catch (OrmException e) {
				throw new OrmException(e.getMessage(), e);
			} finally {
				ses.close();
			}
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
	
	public static void addVisitor(String countryCode, String ipHash, boolean newVisitor) {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			new VisitorsDao().addVisitor(ses, countryCode, ipHash, newVisitor);
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
		if (cookies != null) {
			for(Cookie cookie:cookies){
			    if(cookieName.equals(cookie.getName())){
			        value = cookie.getValue();
			    }
			}
		}
		return value;
	}
	
	private static String getRemoteAddr(HttpServletRequest request) {  
        String addr = request.getRemoteAddr();
        if (addr != null) {
        	if (addr.equals("127.0.0.1")) {
        		addr = request.getHeader("X-Forwarded-For");
        	}
        } else {
        	addr = request.getHeader("X-Forwarded-For");
        }
        if (addr == null || addr.length() == 0 || "unknown".equalsIgnoreCase(addr)) {  
            addr = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }
        if (addr == null || addr.length() == 0 || "unknown".equalsIgnoreCase(addr)) {  
            addr = request.getHeader("WL-Proxy-Client-IP");  
        }
        if (addr == null || addr.length() == 0 || "unknown".equalsIgnoreCase(addr)) {  
            addr = request.getHeader("Proxy-Client-IP");  
        }
        if (addr == null || addr.length() == 0 || "unknown".equalsIgnoreCase(addr)) {  
            addr = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (addr == null || addr.length() == 0 || "unknown".equalsIgnoreCase(addr)) {  
            addr = request.getRemoteAddr();  
        }  
        return addr;  
    }
	
	private static String getMd5ShortenedHash(String string) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(string.getBytes(Charset.forName("UTF8")));
			byte[] resultByte = md.digest();
			result = new String(Hex.encodeHex(resultByte));
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
		}
		if (result != null) {
			if (result.length() > 8) result = result.substring(0, 8);
		}
		return result;
	}
}
