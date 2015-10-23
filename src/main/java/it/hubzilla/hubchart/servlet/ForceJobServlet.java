package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.SettingsBusiness;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceJobServlet extends HttpServlet {

	private static final long serialVersionUID = 8068195193843175753L;
	private final Logger LOG = LoggerFactory.getLogger(ForceJobServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jobName = request.getParameter("name");
		String jobGroup = request.getParameter("group");
		String accessKey = request.getParameter(AppConstants.SETTINGS_ACCESS_KEY);
		
		if (jobGroup==null) jobGroup = "hubzilla";
		if (jobName==null || accessKey == null) {
			jobName="";
			accessKey="";
		}
		if (jobName.equals("") || jobName.equals("")) {
			LOG.debug("ForceJobServlet has been stopped because some parameters are missing");
			return;
		}
		
		//Access key verification
		try {
			String dbKey = SettingsBusiness.getAccessKey();
			if (dbKey == null) {
				throw new ServletException("No accessKey has been defined. Please run the installer.");
			}
			if (!dbKey.equals(accessKey)) {
				throw new ServletException("accessKey is wrong");
			}
		} catch (OrmException e) {
			throw new ServletException(e.getMessage(), e);
		}
		
		//Force job
		try {
			Scheduler scheduler;
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			JobKey jobKey = new JobKey(jobName, jobGroup);
			scheduler.triggerJob(jobKey);
			//String urlWithSessionID = arg1.encodeRedirectURL("../");
			//arg1.sendRedirect(urlWithSessionID);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e);
		}
		PrintWriter writer = response.getWriter();
		response.setContentType("text/html");
		writer.println("<html><head>");
		writer.println("<link type='text/css' rel='stylesheet' href='style/standard.css'>");
		writer.println("</head><body>");
		writer.println("Started job '"+jobName+"' group '"+jobGroup+"'<br />");
		writer.println("<a href='#' onClick='history.go(-1)'>Back</a>");
		writer.println("</body></html>");
	}

	
}
