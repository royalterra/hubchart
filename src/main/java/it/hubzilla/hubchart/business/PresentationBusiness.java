package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.beans.NetworkTypeStatBean;
import it.hubzilla.hubchart.beans.StatisticBean;
import it.hubzilla.hubchart.beans.VersionTagStatBean;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.HubsDao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

public class PresentationBusiness {
	
	private static final int MAX_VERSIONS_SHOWN = 4;
	private static HubsDao hubsDao = new HubsDao();
	
	public static String printVersionTagStats() throws OrmException {
		String result ="--";
		StatisticBean gs = PollBusiness.findLatestGlobalStats();
		if (gs != null) {
			List<VersionTagStatBean> stats = findVersionTagStatBeans(gs.getActiveHubs());
			if (stats != null) {
				if (stats.size() > 0) {
					result = "";
					int max = MAX_VERSIONS_SHOWN;
					if (stats.size() < MAX_VERSIONS_SHOWN) max = stats.size();
					for (int i=0; i<max; i++) {
						result += "<b>"+stats.get(i).getVersionTag()+"</b> ("+stats.get(i).getPercentage()+") ";
					}
				}
			}
		}
		return result;
	}
	
	public static List<VersionTagStatBean> findVersionTagStatBeans(Integer totalHubs) throws OrmException {
		List<VersionTagStatBean> result = new ArrayList<VersionTagStatBean>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubsDao.countLiveVersionTags(ses);
			for (Object[] obj:list) {
				try {
					VersionTagStatBean vts = new VersionTagStatBean(totalHubs);
					vts.setLiveHubs(((Long)obj[0]).intValue());
					vts.setVersionTag((String)obj[1]);
					result.add(vts);
				} catch (Exception e) {/*ignore cast and nullpointer*/}
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static String printNetworkTypeStats() throws OrmException {
		String result ="--";
		StatisticBean gs = PollBusiness.findLatestGlobalStats();
		if (gs != null) {
			List<NetworkTypeStatBean> stats = findNetworkTypeStatBeans(gs.getActiveHubs());
			if (stats != null) {
				if (stats.size() > 0) {
					result = "";
					int max = MAX_VERSIONS_SHOWN;
					if (stats.size() < MAX_VERSIONS_SHOWN) max = stats.size();
					for (int i=0; i<max; i++) {
						String networkTypeIcon = AppConstants.NETWORK_ICONS.get(stats.get(i).getNetworkTypeName());
						result += "&nbsp;<img src='"+networkTypeIcon+
								"' title='"+stats.get(i).getNetworkTypeName()+
								"'/> "+stats.get(i).getPercentage()+" ";
					}
				}
			}
		}
		return result;
	}
	
	public static List<NetworkTypeStatBean> findNetworkTypeStatBeans(Integer totalHubs) throws OrmException {
		List<NetworkTypeStatBean> result = new ArrayList<NetworkTypeStatBean>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Object[]> list = hubsDao.countLiveNetworkTypes(ses);
			for (Object[] obj:list) {
				try {
					NetworkTypeStatBean vts = new NetworkTypeStatBean(totalHubs);
					vts.setLiveHubs(((Long)obj[0]).intValue());
					vts.setNetworkTypeName((String)obj[1]);
					result.add(vts);
				} catch (Exception e) {/*ignore cast and nullpointer*/}
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static String printPluginList(Hubs hub) {
		String result ="";
		String plugins = hub.getPlugins();
		if (plugins != null) {
			if (plugins.length() > 0) {
				String[] pluginArray = plugins.split(AppConstants.STRING_SEPARATOR);
				for (String plugin:pluginArray) {
					if (plugin.length() > 0) {
						plugin += "&bull;"+plugin+" ";
					}
				}
			}
		}
		return result;
	}
	
}
