package it.hubzilla.hubchart.servlet;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.UrlException;
import it.hubzilla.hubchart.business.HubBusiness;
import it.hubzilla.hubchart.business.LogBusiness;
import it.hubzilla.hubchart.business.ThreadedPoller;
import it.hubzilla.hubchart.model.Hubs;

public class DiscoverJob implements Job {
	
	private final Logger LOG = LoggerFactory.getLogger(DiscoverJob.class);
	private static String SERVER_LIST_SUFFIX = "/sitelist/json?start=0&limit=1000000";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "<b>STARTED JOB</b>");
		//Get all known hubs
		Map<String,Hubs> knownHubMap = new HashMap<String, Hubs>();
		List<Hubs> hubToCheckList = null;
		try {
			hubToCheckList = HubBusiness.findDirectories();
			List<Hubs> knownHubList = HubBusiness.findAllHubs(false, false);//include expired and hidden hubs
			for (Hubs hub:knownHubList) {
				String fqdnNoWww = HubBusiness.stripLeadingWww(hub.getFqdn());
				knownHubMap.put(fqdnNoWww, hub);
			}
			if (hubToCheckList == null) hubToCheckList = new ArrayList<Hubs>();
			if (hubToCheckList.size() == 0) {
				//If no directory is known, then all hubs are polled
				hubToCheckList = knownHubList;
			}
		} catch (OrmException e) {
			LogBusiness.addLog(AppConstants.LOG_ERROR, "discover",
					e.getClass().getSimpleName()+" "+e.getMessage());
			throw new JobExecutionException(e);
		}
		
		try {
			int cycleNum = 0;
			//Cycle:
			//1) Parse known hubs to get new urls
			//2) Create corresponding Hub objects
			//3) New Hub objects become the new list to parse
			do {
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "<b>Iteration "+cycleNum+"</b>");
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "Saved hubs are now "+knownHubMap.size());
				List<String> newUrlList = retrieveAndFilterNewUrls(hubToCheckList, knownHubMap);
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "Found "+newUrlList.size()+" new URLs");
				hubToCheckList = registerHubs(newUrlList);
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "Saved "+hubToCheckList.size()+" new hubs");
				cycleNum++;
				//Repeat 3 times or until there are no new urls to add 
			} while ((hubToCheckList.size() > 0) && (cycleNum <= 3));
			LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "Iterations finished");
		} catch (OrmException e) {
			LogBusiness.addLog(AppConstants.LOG_ERROR, "discover",
					e.getClass().getSimpleName()+" "+e.getMessage());
			throw new JobExecutionException(e);
		}
		
		LogBusiness.addLog(AppConstants.LOG_INFO, "discover", "<b>ENDED JOB</b>");
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private List<String> retrieveAndFilterNewUrls(Collection<Hubs> hubToCheckList, Map<String, Hubs> knownHubMap) {
		List<String> newUrlList = new ArrayList<String>();
		int count=1;
		//Scan known hubs to find new hubs they're connected with
		for (Hubs knownHub:hubToCheckList) {
			try {
				String directory = "";
				if (knownHub.getDirectoryMode() != null) {
					directory = "<b>"+AppConstants.DIRECTORY_DESCRIPTIONS.get(knownHub.getDirectoryMode())+"</b>";
				}
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", count+"/"+hubToCheckList.size()+
						" Retrieving hubs from <i>"+knownHub.getFqdn()+"</i> "+directory);
				String jsonUrl = knownHub.getBaseUrl()+SERVER_LIST_SUFFIX;
				//String responseBody = PollBusiness.getJsonResponseFromUrl(jsonUrl);
				ThreadedPoller tp = new ThreadedPoller();
				tp.launchPollingThread(jsonUrl);
				while(!tp.isFinished()) {
					Thread.sleep(500);
				}
				String responseBody = tp.getResult();
				if (responseBody == null) {
					throw new UrlException("ThreadedPoller returned null result: "+knownHub.getBaseUrl());
				}
				
				// Handle json response
				JsonReader jsonReader = null;
				JsonObject jo = null;
				try {
					jsonReader = Json.createReader(new StringReader(responseBody));
					jo = jsonReader.readObject();
				} catch (JsonParsingException e) {
					throw new UrlException(e.getMessage(), e);
				} finally {
					if (jsonReader != null) jsonReader.close();
				}
				//Parse array objects
				JsonArray entriesArray = jo.getJsonArray("entries");
				if (entriesArray != null) {
					List<JsonObject> list = entriesArray.getValuesAs(JsonObject.class);
					for (JsonObject jo2:list) {
						try {
							String url = jo2.getString("url");
							url = HubBusiness.cleanBaseUrl(url);
							String fqdn = new URL(url).getHost();
							String fqdnNoWww = HubBusiness.stripLeadingWww(fqdn);
							//add only those not in the Map
							Hubs foundHub = knownHubMap.get(fqdnNoWww);
							if (foundHub == null) {
								//Not in known map
								if (!newUrlList.contains(url)) {
									//Not in new URLs list
									newUrlList.add(url);
								}
							}
						} catch (Exception e) {}
					}
				}
			} catch (UrlException e) {/* ignore wrong URLs */}
			catch (InterruptedException e) {/* ignore wrong URLs */}
			catch (ExecutionException e) {/* ignore wrong URLs */}
			count++;
		}
		return newUrlList;
	}
	
	private List<Hubs> registerHubs(List<String> urlList) throws OrmException {
		List<Hubs> newHubList = new ArrayList<Hubs>();
		int count = 0;
		for(String url:urlList) {
			count++;
			Hubs hub = null;
			String message = "";
			try {
				hub = HubBusiness.addHub(url);
			} catch (Exception e) {
				//UrlException, BusinessException and OrmException => exit with error message
				message = url+" could not be added. "+e.getMessage();
				LOG.error(message, e);
			}
			if (hub != null) {
				newHubList.add(hub);
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", count+"/"+urlList.size()+" saved <b>"+hub.getFqdn()+"</b>");
			} else {
				LogBusiness.addLog(AppConstants.LOG_INFO, "discover", count+"/"+urlList.size()+" "+url+" "+message);
			}
		}
		return newHubList;
	}
	
}
