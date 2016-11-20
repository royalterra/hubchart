package it.hubzilla.hubchart.business;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.LookupUtil;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.UrlException;
import it.hubzilla.hubchart.beans.StatisticBean;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Languages;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.LogsDao;
import it.hubzilla.hubchart.persistence.StatisticsDao;

public class PollBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(PollBusiness.class);

	/** Return persisted statistics list for the provided hubList */
	public static List<Statistics> pollHubList(Session ses, List<Hubs> hubList, Date pollTime)
			throws OrmException {
		List<Statistics> statList = new ArrayList<Statistics>();
		LogsDao logsDao = new LogsDao();
		int count = 1;
		for (Hubs hub:hubList) {
			try {
				Statistics stat = retrieveTransientStats(ses, hub, pollTime);//Not responding -> exception
				//Save the stats
				if (stat != null) {
					Integer idStats = (Integer) GenericDao.saveGeneric(ses, stat);
					statList.add(stat);
					hub.setIdLastHubStats(idStats);
					hub.setLastSuccessfulPollTime(pollTime);
					logsDao.addLog(ses, AppConstants.LOG_INFO, "poll", count+"/"+hubList.size()+" <b>"+hub.getFqdn()+"</b> <b>OK</b>");
				} else {
					logsDao.addLog(ses, AppConstants.LOG_ERROR, "poll", count+"/"+hubList.size()+" <b>"+hub.getFqdn()+"</b> Exception: statistics are empty ");
				}
			} catch (UrlException e) {
				String dead = "";
				if (hub.getLastSuccessfulPollTime() != null) dead = ("<b>last seen "+AppConstants.FORMAT_DAY.format(hub.getLastSuccessfulPollTime())+"</b>");
				logsDao.addLog(ses, AppConstants.LOG_ERROR, "poll", count+"/"+hubList.size()+" <b>"+hub.getFqdn()+"</b> "+e.getMessage()+" "+dead);
			}
			
			//Always update the hub info after poll (successful or not)
			hub.setPollQueue(null);
			GenericDao.updateGeneric(ses, hub.getId(),  hub);
			count++;
		}
		logsDao.addLog(ses, AppConstants.LOG_ERROR, "poll", "Successful polls: "+statList.size()+"/"+hubList.size());
		return statList;
	}
	
	public static Statistics retrieveTransientStats(Session ses, Hubs hub, Date pollTime)
			throws UrlException, OrmException {
		//Throws an error if the hub is not responsive
		Statistics stats = null;
		
		//Poll siteinfo/json service
		String hubPollUrl = hub.getBaseUrl() + AppConstants.JSON_SITEINFO;
		String hubJsonResp = null;
		try{
			hubJsonResp = Poller.getJsonResponseFromUrl(hubPollUrl);
			if (hubJsonResp != null) {
				stats = parseHubJsonToTransientEntity(ses, hub, hubJsonResp, pollTime);
			} else {
				throw new UrlException("ThreadedPoller returned null result: "+hub.getBaseUrl());
			}
		} catch (JsonParsingException e) {
			throw new UrlException("JsonParsingException: "+hub.getBaseUrl()+"\r\n"+e.getMessage(), e);
		}
		return stats;
	}

	/* POLL USING INPUTSTREAMREADER */
//	public static String getJsonResponseFromUrl(String url) throws IOException {
//		String jsonText = null;
//		InputStream is = new URL(url).openStream();
//		try {
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//			StringBuilder sb = new StringBuilder();
//			int cp;
//			while ((cp = rd.read()) != -1) {
//				sb.append((char) cp);
//			}
//			jsonText = sb.toString();
//		} finally {
//			is.close();
//		}
//		return jsonText;
//	}
	
	
//	private static Statistics parseDiasporaToTransientEntity(Session ses, Statistics stats,
//			String responseBody, Date pollTime)
//				throws OrmException, JsonParsingException {
//		// Handle json response
//		JsonReader jsonReader = Json.createReader(new StringReader(responseBody));
//		JsonObject jo = jsonReader.readObject();
//		jsonReader.close();
//		Hubs hub = stats.getHub();
//		
//		// Hub name
//		hub.setName(jo.getString("name"));
//		// Hub IP
//		String ip = null;
//		try {
//			URL url = new URL(hub.getBaseUrl());
//			InetAddress address = InetAddress.getByName(url.getHost());
//			ip = address.getHostAddress();
//			hub.setIpAddress(ip);
//		} catch (Exception e) { }
//		// Country
//		if (ip != null) {
//			Ip2nationCountries country = null;
//			country = nationDao.findNationCodeByIp(ses, ipToLong(ip));
//			if (country != null) {
//				hub.setCountryCode(country.getCode());
//				hub.setCountryName(country.getCountry());
//			}
//		}
//		// Network type
//		String networkType = LookupUtil.encodeNetworkType(jo.getString("network"));
//		hub.setNetworkType(networkType);// "redmatrix",
//		// Version #
//		hub.setVersion(jo.getString("version"));
//		// Registration policy
//		Boolean reg = jo.getBoolean("registrations_open");
//		if (reg == null) reg = Boolean.FALSE;
//		String registrationPolicy = LookupUtil.encodeRegistrationPolicy(reg.toString());
//		hub.setRegistrationPolicy(registrationPolicy);
//		
//		//total_users
//		Integer totalUsers = null;
//		try {
//			try {
//				totalUsers = jo.getInt("total_users");
//			} catch (Exception e) {
//				String totalUsersString = jo.getString("total_users");
//				totalUsers = Integer.parseInt(totalUsersString);
//			}
//		} catch (Exception e) {/* Any exception is discarded */}
//		stats.setTotalChannels(totalUsers);
//		//active_users_halfyear
//		Integer activeUsersHalfyear = null;
//		try {
//			try {
//				activeUsersHalfyear = jo.getInt("active_users_halfyear");
//			} catch (Exception e) {
//				String activeUsersHalfyearString = jo.getString("active_users_halfyear");
//				activeUsersHalfyear = Integer.parseInt(activeUsersHalfyearString);
//			}
//		} catch (Exception e) {/* Any exception is discarded */}
//		stats.setActiveChannelsLast6Months(activeUsersHalfyear);
//		//active_users_monthly
//		Integer activeUsersMonthly = null;
//		try {
//			try {
//				activeUsersMonthly = jo.getInt("active_users_monthly");
//			} catch (Exception e) {
//				String activeUsersMonthlyString = jo.getString("active_users_monthly");
//				activeUsersMonthly = Integer.parseInt(activeUsersMonthlyString);
//			}
//		} catch (Exception e) {/* Any exception is discarded */}
//		stats.setActiveChannelsLastMonth(activeUsersMonthly);
//		//local_posts
//		Integer localPosts = null;
//		try {
//			try {
//				localPosts = jo.getInt("local_posts");
//			} catch (Exception e) {
//				String localPostsString = jo.getString("local_posts");
//				localPosts = Integer.parseInt(localPostsString);
//			}
//		} catch (Exception e) {/* Any exception is discarded */}
//		stats.setTotalPosts(localPosts);
//		return stats;
//	}
	
	private static Statistics parseHubJsonToTransientEntity(Session ses, Hubs hub,
			String responseBody, Date pollTime)
				throws OrmException, JsonParsingException {
		Statistics stats = new Statistics();
		stats.setActiveChannelsLast6Months(0);
		stats.setActiveChannelsLastMonth(0);
		stats.setActiveHubs(1);
		stats.setTotalChannels(0);
		stats.setTotalPosts(0);
		stats.setHub(hub);
		stats.setPollTime(pollTime);
		
		// Handle json response
		JsonReader jsonReader = Json.createReader(new StringReader(responseBody));
		JsonObject jo = jsonReader.readObject();
		jsonReader.close();
		
		// Hub name
		String name = hub.getFqdn();
		try {
			name = jo.getString("site_name");
		} catch (Exception e) { }
		hub.setName(name);
		// Hub IP
		String ip = null;
		try {
			URL url = new URL(hub.getBaseUrl());
			InetAddress address = InetAddress.getByName(url.getHost());
			ip = address.getHostAddress();
			hub.setIpAddress(ip);
		} catch (Exception e) { }
		// Network type
		try {
			String platform = jo.getString("platform");
			if (platform != null) {
				if (platform.length() <32) {
					hub.setNetworkType(platform);
				} else {
					hub.setNetworkType(platform.substring(0, 31));
				}
				platform = platform.toLowerCase();
			}
			//Attempt network type name normalization
			String networkType = AppConstants.NETWORK_TYPES.get(platform);
			if (networkType != null) hub.setNetworkType(networkType);
		} catch (Exception e) { }
		// Version
		try {
			String version = jo.getString("version");
			if (version != null) {
				if (version.length() >= AppConstants.VERSION_SIZE) version = version.substring(0, AppConstants.VERSION_SIZE);
				hub.setVersion(version);
			}
		} catch (Exception e) { }
		// Version tag
		try {
			String versionTag = jo.getString("version_tag");
			if (versionTag != null) {
				if (versionTag.length() >= AppConstants.VERSION_SIZE) versionTag = versionTag.substring(0, AppConstants.VERSION_SIZE);
				hub.setVersionTag(versionTag);
			}
		} catch (Exception e) { }
		// Registration policy
		try {
			String registrationPolicy = LookupUtil.encodeRegistrationPolicy(jo.getString("register_policy"));
			hub.setRegistrationPolicy(registrationPolicy);
		} catch (Exception e) { }
		// Is invitation only?
		boolean invite = false;
		try {
			Integer inviteInt = jo.getInt("invitation_only");
			if (inviteInt != null) {
				if (inviteInt > 0) invite = true;
			}
		} catch (Exception e1) {
			try {
				String inviteString = jo.getString("invitation_only").toString();
				if (inviteString != null) {
					if (inviteString.equals("1")) invite = true;
				}
			} catch (Exception e2) {/* Any exception is discarded */}
		}
		stats.getHub().setInvitationOnly(invite);
		// Directory mode
		try {
			String directoryMode = LookupUtil.encodeDirectoryMode(jo.getString("directory_mode"));
			hub.setDirectoryMode(directoryMode);
		} catch (Exception e) { }
		// Language object
		try {
			Languages language = LookupUtil.encodeLanguage(ses, jo.getString("language"));
			hub.setLanguage(language);
		} catch (Exception e) { }
		// Plugins
		try {
			String plugins = "";
			JsonArray pluginsArray = jo.getJsonArray("plugins");
			if (pluginsArray != null) {
				List<JsonValue> list = pluginsArray.getValuesAs(JsonValue.class);
				for (JsonValue jv:list) {
					if (plugins.length() > 0) plugins += AppConstants.STRING_SEPARATOR;
					String plugin = jv.toString().replaceAll("\"", "");
					plugins += plugin;
				}
			}
			hub.setPlugins(plugins);
		} catch (Exception e) { }
		//channels_total
		Integer channelsTotal = null;
		try {
			try {
				channelsTotal = jo.getInt("channels_total");
			} catch (Exception e) {
				String channelsTotalString = jo.getString("channels_total");
				channelsTotal = Integer.parseInt(channelsTotalString);
			}
		} catch (Exception e) {/* Any exception is discarded */}
		stats.setTotalChannels(channelsTotal);
		//channels_active_halfyear
		Integer channelsActiveHalfyear = null;
		try {
			try {
				channelsActiveHalfyear = jo.getInt("channels_active_halfyear");
			} catch (Exception e) {
				String channelsActiveHalfyearString = jo.getString("channels_active_halfyear");
				channelsActiveHalfyear = Integer.parseInt(channelsActiveHalfyearString);
			}
		} catch (Exception e) {/* Any exception is discarded */}
		stats.setActiveChannelsLast6Months(channelsActiveHalfyear);
		//channels_active_monthly
		Integer channelsActiveMonthly = null;
		try {
			try {
				channelsActiveMonthly = jo.getInt("channels_active_monthly");
			} catch (Exception e) {
				String channelsActiveMonthlyString = jo.getString("channels_active_monthly");
				channelsActiveMonthly = Integer.parseInt(channelsActiveMonthlyString);
			}
		} catch (Exception e) {/* Any exception is discarded */}
		stats.setActiveChannelsLastMonth(channelsActiveMonthly);
		//local_posts
		Integer localPosts = null;
		try {
			try {
				localPosts = jo.getInt("local_posts");
			} catch (Exception e) {
				String localPostsString = jo.getString("local_posts");
				localPosts = Integer.parseInt(localPostsString);
			}
		} catch (Exception e) {/* Any exception is discarded */}
		stats.setTotalPosts(localPosts);
		//ADMIN
		try {
			JsonArray adminArray = jo.getJsonArray("admin");
			if (adminArray != null) {
				if (adminArray.size() > 0) {
					JsonObject obj = adminArray.getJsonObject(0);
					String adminName = obj.getString("name");
					stats.getHub().setAdminName(adminName);
					String adminAddress = obj.getString("address");
					stats.getHub().setAdminAddress(adminAddress);
					String adminChannel = obj.getString("channel");
					stats.getHub().setAdminChannel(adminChannel);
				}
			}
		} catch (Exception e) {/* Any exception is discarded */}
		// Is hidden?
		boolean hidden = false;
		try {
			Integer hiddenInt = jo.getInt("hide_in_statistics");
			if (hiddenInt != null) {
				if (hiddenInt > 0) hidden = true;
			}
		} catch (Exception e1) {
			try {
				String hiddenString = jo.getString("hide_in_statistics").toString();
				if (hiddenString != null) {
					if (hiddenString.equals("1")) hidden = true;
				}
			} catch (Exception e2) {/* Any exception is discarded */}
		}
		stats.getHub().setHidden(hidden);
		return stats;
	}

	public static Long ipToLong(String addr) {
		String[] addrArray = addr.split("\\.");
		long num = 0;
		for (int i = 0; i < addrArray.length; i++) {
			int power = 3 - i;
			num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
		}
		return num;
	}

	public static StatisticBean findLatestGlobalStats() throws OrmException {
		StatisticBean result = new StatisticBean();
		Session ses = HibernateSessionFactory.getSession();
		try {
			Statistics s = new StatisticsDao().findLastGlobalStats(ses);
			if (s != null) {
				result = new StatisticBean();
				PropertyUtils.copyProperties(result, s);
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<Statistics> findGlobalStats(Date beginDt, Date endDt) throws OrmException {
		List<Statistics> result = new ArrayList<Statistics>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			result = new StatisticsDao().findGlobalStats(ses, beginDt, endDt);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static StatisticBean findFirstGlobalStats() throws OrmException {
		StatisticBean result = new StatisticBean();
		Session ses = HibernateSessionFactory.getSession();
		try {
			Statistics s = new StatisticsDao().findFirstGlobalStats(ses);
			if (s != null) {
				result = new StatisticBean();
				PropertyUtils.copyProperties(result, s);
			}
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new OrmException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

}
