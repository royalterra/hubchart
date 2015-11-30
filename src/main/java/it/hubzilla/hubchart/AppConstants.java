package it.hubzilla.hubchart;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AppConstants {

	// CONFIGURATION
	public static final int HUB_EXPIRATION_DAYS = 7;
	public static final String HIBERNATE_CONFIG_FILE="/hibernate.cfg.xml";
	public static final String QUARTZ_CONFIG_FILE = "/quartz-jobs.xml";
	public static final String APP_PROPERTY_FILE = "/app.properties";
	public static final String TESTING_PROPERTY_FILE = "/testing.properties";
	public static final String SETTINGS_ACCESS_KEY = "accessKey";
	
	// LOG
	public static final int LOG_EXPIRATION_DAYS = 4;
	public static final int VISITOR_EXPIRATION_DAYS = 15;
	public static final String LOG_INFO="INFO";
	public static final String LOG_ERROR="ERROR";
	public static final String LOG_DEBUG="DEBUG";
	public static final String LOG_WARN="WARN";
	
	// IMAGES
	public static final String FLAG_ICONS_PATH = "images/flags_16/";
	public static final String IMAGE_MIME_TYPE = "image/png";
	public static final int HUB_CHART_WIDTH = 380;
	public static final int HUB_CHART_HEIGHT = 160;
	public static final int GLOBAL_CHART_WIDTH = 380;
	public static final int GLOBAL_CHART_HEIGHT = 160;
	
	// STATISTICS CONSTANTS
	public static final String JSON_SITEINFO ="/siteinfo/json";
	
	// FEED CONSTANTS
	public static final String FEED_DEFAULT_TYPE = "rss_2.0";
	//rss_0.9, rss_0.91, rss_0.92, rss_0.93,rss_0.94, rss_1.0, rss_2.0, atom_0.3, atom_1.0
	public static final String FEED_TITLE = "Hubzilla network statistics";
	public static final String FEED_LINK = "http://hubchart.hubzilla.it";
	public static final String FEED_DESCRIPTION = "Daily Hubzilla grid statistics";
	public static final int FEED_DAYS_BEFORE_DELETION = 10;
	
	// FORMATS
	public static final String STRING_SEPARATOR = ";";
	public static final String PATTERN_DATETIME = "dd/MM/yyyy HH:mm";
	public static final String PATTERN_TIMESTAMP = "dd/MM/yyyy HH:mm:ss z";//"dd/MM/yyyy HH:mm";
	public static final String PATTERN_DAY = "dd/MM/yyyy";
	public static final String PATTERN_DAY_SQL = "yyyy-MM-dd";
	public static final String PATTERN_MONTH = "MM/yyyy";
	public static final String PATTERN_CURRENCY = "#0.00";
	public static final long HOUR = 3600000L;
	public static final long DAY = HOUR*24;
	public static final long MONTH = DAY*30; //millisecondi in 30 giorni 1000 * 60 * 60 * 24 * 30;
	public static final long YEAR = DAY*365; 

	public static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat(AppConstants.PATTERN_DAY);
	public static final SimpleDateFormat FORMAT_DAY_SQL = new SimpleDateFormat(AppConstants.PATTERN_DAY_SQL);
	public static final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat FORMAT_DATETIME = new SimpleDateFormat(AppConstants.PATTERN_TIMESTAMP);
	public static final SimpleDateFormat FORMAT_TIMESTAMP = new SimpleDateFormat(AppConstants.PATTERN_TIMESTAMP);
	public static final DecimalFormat FORMAT_CURRENCY = new DecimalFormat(AppConstants.PATTERN_CURRENCY);
	public static final DecimalFormat FORMAT_INTEGER = new DecimalFormat("#0");
	public static Date DATE_FAR_PAST;
	public static Date DATE_FAR_FUTURE;
	static {
		try {
			DATE_FAR_PAST = FORMAT_DAY.parse("01/01/2000");
			DATE_FAR_FUTURE = FORMAT_DAY.parse("01/01/2170");
		} catch (ParseException e) { }
	}
	
	// OPENSHIFT
	public static final String OPENSHIFT_MYSQL_DB_USERNAME = "OPENSHIFT_MYSQL_DB_USERNAME";
	public static final String OPENSHIFT_MYSQL_DB_PASSWORD = "OPENSHIFT_MYSQL_DB_PASSWORD";
	public static final String OPENSHIFT_MYSQL_DB_HOST = "OPENSHIFT_MYSQL_DB_HOST";
	public static final String OPENSHIFT_MYSQL_DB_PORT = "OPENSHIFT_MYSQL_DB_PORT";
	public static final String OPENSHIFT_APP_NAME = "OPENSHIFT_APP_NAME";

	// HUB PAGINATION
	public static final int PAGING_HUBS = 20;
	public static final String PARAM_HUB_FQDN = "hubFqdn";
	public static final String PARAM_HUB_PAGE = "pag";
	public static final String PARAM_HUB_ORDER = "order";
	public static final String PARAM_HUB_ASC = "asc";
	public static final String ORDER_FQDN = "fqdn";
	public static final String ORDER_REGISTRATION = "regs";
	public static final String ORDER_LANG = "lang";
	public static final String ORDER_GEO = "geo";
	public static final String ORDER_CHANNEL = "chan";
	public static final String ORDER_VERSION = "version";
	public static final Map<String, String> ORDER_TYPES = new HashMap<String, String>();
	static {
		ORDER_TYPES.put(ORDER_FQDN, "hub.fqdn");
		ORDER_TYPES.put(ORDER_REGISTRATION, "hub.registrationPolicy");
		ORDER_TYPES.put(ORDER_LANG, "hub.language.language"); 
		ORDER_TYPES.put(ORDER_GEO, "hub.countryCode");
		ORDER_TYPES.put(ORDER_CHANNEL, "activeChannelsLast6Months");
		ORDER_TYPES.put(ORDER_VERSION, "hub.version");
	};
	// LANGUAGE PAGINATION
	public static final int PAGING_LANG = 10;
	public static final String PARAM_LANG_PAGE = "langPag";
	// GEO PAGINATION
	public static final int PAGING_GEO = 10;
	public static final String PARAM_GEO_PAGE = "geoPag";
	
	// LOOKUP: NETWORK TYPE
	public static final String NETWORK_TYPE_UNKNOWN = "--";
	public static final String NETWORK_TYPE_HUBZILLA = "hubzilla";
	public static final String NETWORK_TYPE_RED = "redmatrix";
	public static final String NETWORK_TYPE_RUSTY_RAZORBLADE = "rusty razorblade";
	public static final Map<String, String> NETWORK_TYPES = new HashMap<String, String>();
	static {//key must be lowercase
		NETWORK_TYPES.put("hubzilla", NETWORK_TYPE_HUBZILLA);
		NETWORK_TYPES.put("redmatrix", NETWORK_TYPE_RED);
		NETWORK_TYPES.put("red matrix", NETWORK_TYPE_RED);
		NETWORK_TYPES.put("rusty razorblade", NETWORK_TYPE_RUSTY_RAZORBLADE);
		NETWORK_TYPES.put("rustyrazorblade", NETWORK_TYPE_RUSTY_RAZORBLADE);
	};
	public static final String NETWORK_ICON_UNKNOWN = "images/unknown-16.png";
	public static final String NETWORK_ICON_HUBZILLA = "images/home-16.png";
	public static final String NETWORK_ICON_RED = "images/rm-16.png";
	public static final String NETWORK_ICON_RUSTY_RAZORBLADE = "images/razorblade-16.png";
	public static final Map<String, String> NETWORK_ICONS = new HashMap<String, String>();
	static {
		NETWORK_ICONS.put(NETWORK_TYPE_UNKNOWN, NETWORK_ICON_UNKNOWN);
		NETWORK_ICONS.put(NETWORK_TYPE_HUBZILLA,NETWORK_ICON_HUBZILLA);
		NETWORK_ICONS.put(NETWORK_TYPE_RED,NETWORK_ICON_RED);
		NETWORK_ICONS.put(NETWORK_TYPE_RUSTY_RAZORBLADE,NETWORK_ICON_RUSTY_RAZORBLADE);
	};
	// LOOKUP: REGISTRATION
	public static final String REGISTRATION_OPEN = "OPEN";
	public static final String REGISTRATION_APPROVE = "APPR";
	public static final String REGISTRATION_PRIVATE = "PRIV";
	public static final Map<String, String> REGISTRATION_TYPES = new HashMap<String, String>();
	static {
		REGISTRATION_TYPES.put("true", REGISTRATION_OPEN);
		REGISTRATION_TYPES.put("false", REGISTRATION_PRIVATE);
		REGISTRATION_TYPES.put("register_open", REGISTRATION_OPEN); 
		REGISTRATION_TYPES.put("register_approve", REGISTRATION_APPROVE);
		REGISTRATION_TYPES.put("register_closed", REGISTRATION_PRIVATE);
	};
	public static final Map<String, String> REGISTRATION_DESCRIPTIONS = new HashMap<String, String>();
	static {
		REGISTRATION_DESCRIPTIONS.put(REGISTRATION_OPEN, "Open");
		REGISTRATION_DESCRIPTIONS.put(REGISTRATION_APPROVE, "Approval");
		REGISTRATION_DESCRIPTIONS.put(REGISTRATION_PRIVATE, "Private");
	};
	// LOOKUP: DIRECTORY
	public static final String DIRECTORY_MODE_NORMAL = "NORM";//directory client, we will find a directory
	public static final String DIRECTORY_MODE_SECONDARY = "MIRR";//caching directory or mirror
	public static final String DIRECTORY_MODE_PRIMARY = "MAIN"; //main directory server
	public static final String DIRECTORY_MODE_STANDALONE = "ALON";//"off the grid" or private directory services
	public static final Map<String, String> DIRECTORY_MODES = new HashMap<String, String>();
	static {
		DIRECTORY_MODES.put("directory_mode_normal", DIRECTORY_MODE_NORMAL);
		DIRECTORY_MODES.put("directory_mode_secondary", DIRECTORY_MODE_SECONDARY);
		DIRECTORY_MODES.put("directory_mode_primary", DIRECTORY_MODE_PRIMARY); 
		DIRECTORY_MODES.put("directory_mode_standalone", DIRECTORY_MODE_STANDALONE);
	};
	public static final Map<String, String> DIRECTORY_DESCRIPTIONS = new HashMap<String, String>();
	static {
		DIRECTORY_DESCRIPTIONS.put(DIRECTORY_MODE_NORMAL, "Client");
		DIRECTORY_DESCRIPTIONS.put(DIRECTORY_MODE_SECONDARY, "Mirror");
		DIRECTORY_DESCRIPTIONS.put(DIRECTORY_MODE_PRIMARY, "Main directory");
		DIRECTORY_DESCRIPTIONS.put(DIRECTORY_MODE_STANDALONE, "Private standalone");
	};
	// LOOKUP: CHART_TYPE
	public static final String CHART_TYPE_VERSIONS = "VRSN";//Active channels per hub
	public static final String CHART_TYPE_HUB_CHANNELS = "HCHN";//Active channels per hub
	public static final String CHART_TYPE_TOTAL_CHANNELS = "TCHN";//Total channels
	public static final String CHART_TYPE_TOTAL_HUBS = "THUB"; //Total hubs
	public static final Map<String, String> CHART_TYPE_DESCRIPTIONS = new HashMap<String, String>();
	static {
		CHART_TYPE_DESCRIPTIONS.put(CHART_TYPE_VERSIONS, "Deployed version");
		CHART_TYPE_DESCRIPTIONS.put(CHART_TYPE_HUB_CHANNELS, "Hub channels");
		CHART_TYPE_DESCRIPTIONS.put(CHART_TYPE_TOTAL_CHANNELS, "Grid channels");
		CHART_TYPE_DESCRIPTIONS.put(CHART_TYPE_TOTAL_HUBS, "Grid hubs"); 
	};

}
