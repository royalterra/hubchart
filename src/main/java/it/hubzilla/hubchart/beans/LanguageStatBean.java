package it.hubzilla.hubchart.beans;

import it.hubzilla.hubchart.LookupUtil;
import it.hubzilla.hubchart.model.Languages;


public class LanguageStatBean {

	private Languages language;
	private Integer liveHubs;
	
	public String getCountryFlag() {
		return LookupUtil.decodeLanguageToFlag(this.language);
	}
	
	public Languages getLanguage() {
		return language;
	}
	public void setLanguage(Languages language) {
		this.language = language;
	}
	public Integer getLiveHubs() {
		return liveHubs;
	}
	public void setLiveHubs(Integer liveHubs) {
		this.liveHubs = liveHubs;
	}

}
