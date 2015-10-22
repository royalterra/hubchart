package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.BusinessException;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.UrlException;
import it.hubzilla.hubchart.business.HubBusiness;
import it.hubzilla.hubchart.business.PollBusiness;
import it.hubzilla.hubchart.model.Hubs;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CrawlerJob implements Job {
	
	private final Logger LOG = LoggerFactory.getLogger(CrawlerJob.class);
	private static String SERVER_LIST_SUFFIX = "/sitelist/json?start=0&limit=1000000";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//Get all known hubs
		Map<String,Hubs> knownHubMap = new HashMap<String, Hubs>();
		List<Hubs> hubToCheckList = null;
		try {
			hubToCheckList = HubBusiness.findDirectories();
			List<Hubs> knownHubList = HubBusiness.findAllHubs(false, false);//include expired and hidden hubs
			for (Hubs hub:knownHubList) knownHubMap.put(hub.getBaseUrl(), hub);
			if (hubToCheckList == null) hubToCheckList = new ArrayList<Hubs>();
			if (hubToCheckList.size() == 0) {
				hubToCheckList = knownHubList;
			}
		} catch (OrmException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		try {
			int cycleNum = 0;
			//Cycle:
			//1) Parse known hubs to get new urls
			//2) Create corresponding Hub objects
			//3) New Hub objects become the new list to parse
			do {
				LOG.debug("Cycle "+cycleNum);
				LOG.debug("Saved hubs are now "+knownHubMap.size());
				List<String> newUrlList = retrieveAndFilterNewUrls(hubToCheckList, knownHubMap);
				LOG.debug("Found "+newUrlList.size()+" new URLs");
				hubToCheckList = registerHubs(newUrlList);
				LOG.debug("Saved "+hubToCheckList.size()+" new hubs");
				cycleNum++;
				//Repeat 3 times or until there are no new urls to add 
			} while ((hubToCheckList.size() > 0) && (cycleNum <= 3));
			LOG.debug("Cycles finished");
		} catch (OrmException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private List<String> retrieveAndFilterNewUrls(Collection<Hubs> hubToCheckList, Map<String, Hubs> knownHubMap) {
		List<String> newUrlList = new ArrayList<String>();
		int count=0;
		for (Hubs knownHub:hubToCheckList) {
			try {
				LOG.debug(count+"/"+hubToCheckList.size()+" Retrieving hubs from "+knownHub.getBaseUrl());
				String jsonUrl = knownHub.getBaseUrl()+SERVER_LIST_SUFFIX;
				String responseBody = PollBusiness.getJsonResponseFromUrl(jsonUrl);
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
							//add only those not in the Map
							Hubs foundHub = knownHubMap.get(url);
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
			count++;
		}
		return newUrlList;
	}
	
	private List<Hubs> registerHubs(List<String> urlList) throws OrmException {
		List<Hubs> newHubList = new ArrayList<Hubs>();
		for(String url:urlList) {
			try {
				LOG.debug(newHubList.size()+"/"+urlList.size()+" Saving "+url);
				Integer hubId = HubBusiness.initHub(url, false);
				Hubs hub = HubBusiness.findHubById(hubId);
				newHubList.add(hub);
			} catch (BusinessException e) {/* ignore if URL is already registered */
			} catch (MalformedURLException e) {/* ignore if URL is malformed */
			}
		}
		return newHubList;
	}
	
}
