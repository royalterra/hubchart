package it.hubzilla.hubchart.beans;

import it.hubzilla.hubchart.LookupUtil;


public class CountryStatBean {

	private String countryCode;
	private String countryName;
	private Integer liveHubs;
	
	public String getCountryFlag() {
		return LookupUtil.decodeCountryToFlag(this.countryCode);
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public Integer getLiveHubs() {
		return liveHubs;
	}
	public void setLiveHubs(Integer liveHubs) {
		this.liveHubs = liveHubs;
	}
	
}
