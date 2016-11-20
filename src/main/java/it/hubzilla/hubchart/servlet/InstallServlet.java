package it.hubzilla.hubchart.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.HubBusiness;
import it.hubzilla.hubchart.business.LogBusiness;
import it.hubzilla.hubchart.business.SettingsBusiness;
import it.hubzilla.hubchart.model.Hubs;

@WebServlet("/install")
public class InstallServlet extends HttpServlet {
	private static final long serialVersionUID = 3013147604613167496L;

	private final Logger LOG = LoggerFactory.getLogger(InstallServlet.class);
	
	public static final String PARAM_SEED_HUB = "seedHub"; 
	
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
		String baseUrl = request.getParameter(PARAM_SEED_HUB);
		String accessKey = request.getParameter(AppConstants.SETTINGS_ACCESS_KEY);
		
		//Non empty parameters
		if (baseUrl == null || accessKey == null) {
			baseUrl="";
			accessKey="";
		}
		if (baseUrl.equals("") || accessKey.equals("")) {
			throw new ServletException("accessKey and baseUrl must be defined.");
		}
		
		//Access key should have no value on database. Or if it's present it must coincide with the given value
		try {
			String dbKey = SettingsBusiness.getAccessKey();
			if (dbKey != null) {
				if (!dbKey.equals(accessKey)) {
					throw new ServletException("accessKey already exists. This software has already been installed.");
				}
			}
		} catch (OrmException e) {
			throw new ServletException(e.getMessage(), e);
		}
		//Set accessKey
		try {
			SettingsBusiness.setAccessKey(accessKey);
		} catch (OrmException e) {
			throw new ServletException(e.getMessage(), e);
		}
		message += "Access key has been correctly set.<br/><br/>";
		
		//Hub list should be empty
		boolean emptyHubList = true;
		try {
			List<Hubs> hubList = HubBusiness.findAllHubs(true, false);
			if (hubList != null) {
				if (hubList.size() > 0) {
					message += "The first hub has already been defined.";
					emptyHubList = false;
				}
			}
		} catch (OrmException e) {
			throw new ServletException(e.getMessage(), e);
		}
		//Set the seed
		Hubs hub = null;
		if (emptyHubList) {
			//Look for hub baseUrl
			String pollUrl = null;
			try {
				if (baseUrl != null) {
					String baseUrlParam = HubBusiness.cleanBaseUrl(baseUrl);
					pollUrl = baseUrlParam+AppConstants.JSON_SITEINFO;
					if (urlExists(pollUrl)) {
						baseUrl = baseUrlParam;
					}
				}
				LOG.debug("baseUrl = "+baseUrl);
				LOG.debug("response from pollUrl = "+pollUrl);
				
				if (baseUrl != null) {
					try {
						//Revive or add hub
						hub = HubBusiness.addHub(baseUrl);
						message += "Your hub have been correctly registered.<br />"
								+ "It will be included in global statistics within 24 hours.";
						success = true;
					} catch (Exception e) {
						//UrlException, BusinessException and OrmException => exit with error message
						LogBusiness.addLog(AppConstants.LOG_DEBUG, "install", e.getMessage());
						message = baseUrl+" could not be added.<br />"+
								"Cause: "+e.getMessage();
						LOG.error(message, e);
					}
				} else {
					message = "The hub base URL is incorrect. Please re-submit this page.";
				}
			} catch (ProtocolException e) {
				message = e.getMessage();
				LOG.debug(e.getMessage(), e);
			} catch (IOException e) {
				message = e.getMessage();
				LOG.debug(e.getMessage(), e);
			}
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
				"<html> \n" + "<head> \n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> \n" + "<title>Hubzilla hub registration</title> \n" +
				"<meta name='viewport' content='width=device-width, initial-scale=1'>"+
				"<link href='css/bootstrap.min.css' rel='stylesheet'>"+
				"</head> \n" + "<body> \n" +
				"<div class='container'>"+
				"<h1><img src='images/hubchart2-32.png' align='middle' /> hubchart</h1>"+
				"&nbsp;<br />");
		if (success) {
			out.println("<p>"+message+"</p>");
			if (hub != null) {
				out.println("<p>");
				out.println("Name: "+hub.getName()+"<br />");
				out.println("Base URL: "+hub.getBaseUrl()+"<br />");
				String icon = AppConstants.NETWORK_ICONS.get(hub.getNetworkType());
				if (icon == null) icon = AppConstants.NETWORK_ICON_UNKNOWN;
				out.println("Network: <img src='"+icon+"' border='0'/> "+hub.getNetworkType()+"<br />");
				//out.println("Server location: <img src='"+LookupUtil.decodeCountryToFlag(hub.getCountryCode())+"' /> "+
				//		hub.getCountryName()+"<br />");
				out.println("Registration: "+AppConstants.REGISTRATION_DESCRIPTIONS
						.get(hub.getRegistrationPolicy())+"<br />");
				out.println("Version: "+hub.getVersion()+"<br />");
				out.println("</p>");
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
