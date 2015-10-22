package it.hubzilla.hubchart.beans;

import it.hubzilla.hubchart.AppConstants;


public class VersionTagStatBean {

	private String versionTag;
	private Integer liveHubs;
	private Integer totalHubs;
	
	public VersionTagStatBean(int totalHubs) {
		this.totalHubs=totalHubs;
	}
	
	public Integer getLiveHubs() {
		return liveHubs;
	}
	
	public void setLiveHubs(Integer liveHubs) {
		this.liveHubs = liveHubs;
	}
	
	public String getVersionTag() {
		return versionTag;
	}
	
	public void setVersionTag(String versionTag) {
		this.versionTag = versionTag;
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
