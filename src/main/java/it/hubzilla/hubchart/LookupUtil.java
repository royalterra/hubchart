package it.hubzilla.hubchart;

import it.hubzilla.hubchart.model.Languages;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StringType;

public class LookupUtil {

	public static String encodeNetworkType(String networkDescr) {
		if (networkDescr == null) networkDescr = "";
		return AppConstants.NETWORK_TYPES.get(networkDescr.toLowerCase());
	}
	
	public static String encodeRegistrationPolicy(String registration) {
		if (registration == null) registration = "";
		return AppConstants.REGISTRATION_TYPES.get(registration.toLowerCase());
	}
	
	public static String encodeDirectoryMode(String directoryMode) {
		if (directoryMode == null) directoryMode = "";
		return AppConstants.DIRECTORY_MODES.get(directoryMode.toLowerCase());
	}
	
	public static String decodeCountryToFlag(String countryCode) {
		String iconPath = AppConstants.FLAG_ICONS_PATH+"_unknown.png";
		if (countryCode != null) {
			if (countryCode.length() == 2) {
				iconPath = AppConstants.FLAG_ICONS_PATH+countryCode.toUpperCase()+".png";
			}
		}
		return iconPath;
	}
	
	public static String getCountryCode(Languages language) {
		if (language == null) return null;
		String code = language.getCode();
		String countryCode = code.substring(code.indexOf("-")+1);
		return countryCode;
	}
	
	public static String decodeLanguageToFlag(Languages language) {
		String countryCode = getCountryCode(language);
		return decodeCountryToFlag(countryCode);
	}
	
	public static Languages encodeLanguage(Session ses, String langString)
			throws OrmException {
		if (langString == null) return null;
		Languages result = null;
		if (langString.length() > 1) {
			try {
				String hql = "from Languages l where "+
						"l.code like :s1 "+
						"order by l.main desc, l.code asc";
				Query q = ses.createQuery(hql);
				q.setParameter("s1", langString+"%", StringType.INSTANCE);
				q.setMaxResults(1);
				q.setFirstResult(0);
				@SuppressWarnings("unchecked")
				List<Languages> list = q.list();
				if (list != null) {
					if (list.size() > 0) {
						return list.get(0);
					}
				}
			} catch (HibernateException e) {
				throw new OrmException(e.getMessage(), e);
			}
		}
		return result;
	}
	
}
