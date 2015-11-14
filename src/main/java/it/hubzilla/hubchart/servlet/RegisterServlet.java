package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.LookupUtil;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.HubBusiness;
import it.hubzilla.hubchart.business.LogBusiness;
import it.hubzilla.hubchart.business.StatisticBusiness;
import it.hubzilla.hubchart.model.Hubs;
import it.hubzilla.hubchart.model.Statistics;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = -2723999233245743020L;

	private final Logger LOG = LoggerFactory.getLogger(RegisterServlet.class);
	
	public static final String BASE_URL_PARAM = "baseUrl"; 
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "";
		boolean success = false;
		String baseUrlParam = request.getParameter(BASE_URL_PARAM);
		//Look for hub baseUrl and che
		String baseUrl = null;
		String pollUrl = null;
		Integer hubId = null;
		try {
			if (baseUrlParam != null) {
				baseUrlParam = HubBusiness.cleanBaseUrl(baseUrlParam);
				pollUrl = baseUrlParam+AppConstants.JSON_SITEINFO;
				if (urlExists(pollUrl)) {
					baseUrl = baseUrlParam;
				}
			}
			
			LogBusiness.addLog(AppConstants.LOG_DEBUG, "register", "baseUrl = "+baseUrl);
			LogBusiness.addLog(AppConstants.LOG_DEBUG, "register", "response from pollUrl = "+pollUrl);

			if (baseUrl != null) {
				try {
					//Revive or add hub
					Hubs hub;
					try {
						//First: check if it can be revived
						hub = HubBusiness.attemptToReviveHub(baseUrl);
						if (hub != null) LogBusiness.addLog(AppConstants.LOG_DEBUG, "register", "baseUrl = "+baseUrl);
						message = hub.getFqdn()+" has been marked as live.<br />"
								+ "It will be included in global statistics within 24 hours.";
					} catch (Exception e) {
						LogBusiness.addLog(AppConstants.LOG_DEBUG, "register", e.getMessage());
						//Second: If it cannot be revived then it must be added
						hub = HubBusiness.addHub(baseUrl);
						message = hub.getFqdn()+" has been correctly registered.<br />"
								+ "It will be included in global statistics within 24 hours.";
					}
					Statistics stats = StatisticBusiness.findLastStatisticBeanByFqdn(hub.getFqdn());
					if (stats == null) {
						message += " NO STATISTICS have been saved.";
					} else {
						message += " Stats data: time "+ AppConstants.FORMAT_DATETIME.format(stats.getPollTime())+
								" channels (6 months) "+stats.getActiveChannelsLast6Months();
					}
					hubId = hub.getId();
					success = true;
				} catch (BusinessException e) {
					message = e.getMessage();
				} catch (OrmException e) {
					message = e.getMessage();
					LOG.error(message, e);
				}
			} else {
				message = "The hub base URL is incorrect";
			}
		} catch (ProtocolException e) {
			message = e.getMessage();
			LOG.debug(e.getMessage(), e);
		} catch (IOException e) {
			message = e.getMessage();
			LOG.debug(e.getMessage(), e);
		}
		LogBusiness.addLog(AppConstants.LOG_INFO, "register", message);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
				"<html> \n" + "<head> \n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> \n" + "<title>Hubzilla hub registration</title> \n" +
				"<meta name='viewport' content='width=device-width, initial-scale=1'>"+
				"<link href='css/bootstrap.min.css' rel='stylesheet'>"+
				"<link href='css/custom.css' rel='stylesheet' />"+
				"<link href='images/hubchart1-16.png' rel='shortcut icon' type='image/png' />"+
				"</head> \n" + "<body> \n" +
				"<div class='container'>"+
				"<h1><a href='index.jsp'><img src='images/hubchart1-32.png' align='middle' /></a> hubchart</h1>"+
				"&nbsp;<br />");
		if (success) {
			out.println("<p>"+message+"</p>");
			try {
				out.println("<p>");
				Hubs hub = HubBusiness.findHubById(hubId);
				out.println("Name: "+hub.getName()+"<br />");
				out.println("Base URL: "+hub.getBaseUrl()+"<br />");
				out.println("Network: <img src='"+
						AppConstants.NETWORK_ICONS.get(hub.getNetworkType())+
						"' border='0'/> "+
						AppConstants.NETWORK_DESCRIPTIONS.get(hub.getNetworkType())+"<br />");
				out.println("Server location: <img src='"+LookupUtil.decodeCountryToFlag(hub.getCountryCode())+"' /> "+
						hub.getCountryName()+"<br />");
				out.println("Registration: "+AppConstants.REGISTRATION_DESCRIPTIONS
						.get(hub.getRegistrationPolicy())+"<br />");
				out.println("Version: "+hub.getVersion()+"<br />");
				out.println("</p>");
			} catch (OrmException e) {
				out.println("<p style='color:#c60032;'>ERROR: "+e.getMessage()+"</p>");
			}
		} else {
			out.println("<p style='color:#c60032;'>ERROR: "+message+"</p>");
			out.println("<p>You must provide a correct and working base URL in the form <br />"+
					"<code>https://&lt;domain&gt;</code><br />"+
					"<code>http://&lt;domain&gt;</code><br />"+
					"<code>http(s)://&lt;domain&gt;/&lt;base_dir&gt;</code><br /><br />"+
					"Please check the <b>http</b> or <b>https</b> prefix!<br /><br />"+
					"If you find a bug please contact the author</a>.<br /><br />"
				);
		}
		out.println("<a href='index.jsp'><b>&lt;- back</b></a>");
		out.println("</div><!-- /container -->" );
		out.println("</body> \n" + "</html>" );
	}

	private boolean urlExists(String urlString) throws ProtocolException, IOException {
		if (urlString == null) return false;
		try {
			final URL url = new URL(urlString);
			int responseCode = 0;
			if (urlString.startsWith("https")) {
				// SSL
				HttpsURLConnection hc = (HttpsURLConnection) url.openConnection();
				hc.setRequestMethod("HEAD");
				responseCode = hc.getResponseCode();
			} else {
				// NO SSL
				HttpURLConnection hc = (HttpURLConnection) url.openConnection();
				hc.setRequestMethod("HEAD");
				responseCode = hc.getResponseCode();
			}
			return (responseCode == 200);
		}
		catch (MalformedURLException e) {
			LOG.debug(e.getMessage(), e);
		}
		return false;
	}
	
}
