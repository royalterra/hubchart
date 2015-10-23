package it.hubzilla.hubchart.beans;

import it.hubzilla.hubchart.AppConstants;


public class NetworkTypeStatBean {

	private String networkTypeName;
	private Integer liveHubs;
	private Integer totalHubs;
	
	public NetworkTypeStatBean(int totalHubs) {
		this.totalHubs=totalHubs;
	}
	
	public Integer getLiveHubs() {
		return liveHubs;
	}
	
	public void setLiveHubs(Integer liveHubs) {
		this.liveHubs = liveHubs;
	}
	
	public String getNetworkTypeName() {
		return networkTypeName;
	}

	public void setNetworkTypeName(String networkTypeName) {
		this.networkTypeName = networkTypeName;
	}

	public Integer getTotalHubs() {
		return totalHubs;
	}

	public void setTotalHubs(Integer totalHubs) {
		this.totalHubs = totalHubs;
	}
	
	public String getPercentage() {
		if (totalHubs != null) {
			Double percentage = ((double)liveHubs/(double)totalHubs)*100;
			String p = AppConstants.FORMAT_INTEGER.format(percentage)+"%";
			return p;
		}
		return null;
	}
}
