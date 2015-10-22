package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleClassLoadHelper;
import org.quartz.xml.ValidationException;
import org.quartz.xml.XMLSchedulingDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class StartSchedulerServlet extends GenericServlet{
	private static final long serialVersionUID = -284746908142861897L;
	
	private final Logger LOG = LoggerFactory.getLogger(StartSchedulerServlet.class);
			
	@Override
	public void init() throws ServletException {
		LOG.info("Instanziata StartSchedulerServlet");
		initQuartz();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info("Instanziata StartSchedulerServlet con configurazione xml");
		initQuartz();
	}

	private void initQuartz() throws ServletException {
		try {
			InputStream xmlStream = this.getClass().getResourceAsStream(AppConstants.QUARTZ_CONFIG_FILE);
			if(xmlStream!=null){
				LOG.debug("Created InputStream from "+AppConstants.QUARTZ_CONFIG_FILE);
				Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
				LOG.debug("DefaultScheduler instantiated");
				XMLSchedulingDataProcessor xmlProcessor =
						new XMLSchedulingDataProcessor(new SimpleClassLoadHelper());
				String systemId = AppConstants.QUARTZ_CONFIG_FILE;
				xmlProcessor.processStreamAndScheduleJobs(xmlStream, systemId, scheduler);
					//xmlProcessor.processFileAndScheduleJobs(absPath, scheduler);
				LOG.debug("xmlProcessor scheduled");
			} else {
				throw new ServletException(AppConstants.QUARTZ_CONFIG_FILE + " does NOT exists");
			}
			//scheduler.start();
		} catch (SchedulerException e) {
			LOG.error("Errore Scheduler",e);
		} catch (ParserConfigurationException e) {
			LOG.error("Errore Scheduler",e);
		} catch (ParseException e) {
			LOG.error("Errore Scheduler",e);
		} catch (ClassNotFoundException e) {
			LOG.error("Errore Scheduler",e);
		} catch (IOException e) {
			LOG.error("Errore Scheduler",e);
		} catch (XPathException e) {
			LOG.error("Errore Scheduler",e);
		} catch (SAXException e) {
			LOG.error("Errore Scheduler",e);
		} catch (ValidationException e) {
			LOG.error("Errore Scheduler",e);
		}
		
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		this.initQuartz();
		
	}


}
