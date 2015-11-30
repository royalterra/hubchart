package it.hubzilla.hubchart.beans;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.LookupUtil;
import it.hubzilla.hubchart.model.Statistics;

public class StatisticBean extends Statistics {
	private static final long serialVersionUID = 2166425059847494999L;

	public StatisticBean() {
		super();
	}
	
	public String getLastUpdateString() {
		return AppConstants.FORMAT_TIMESTAMP.format(this.getPollTime());
	}
	
	public String getNetworkTypeIcon() {
		return AppConstants.NETWORK_ICONS.get(this.getHub().getNetworkType());
	}
	
	public String getNetworkTypeDescr() {
		return this.getHub().getNetworkType();
	}
	
	public String getRegistrationPolicyDescr() {
		return AppConstants.REGISTRATION_DESCRIPTIONS.get(this.getHub().getRegistrationPolicy());
	}
	
	public String getDirectoryDescr() {
		return AppConstants.DIRECTORY_DESCRIPTIONS.get(this.getHub().getDirectoryMode());
	}
	
	public String getLanguageFlag() {
		return LookupUtil.decodeLanguageToFlag(this.getHub().getLanguage());
	}
	
	public String getCountryFlag() {
		return LookupUtil.decodeCountryToFlag(this.getHub().getCountryCode());
	}
	
	public String getAverageHubChannels() {
		Double average = null;
		if (this.getActiveHubs() != null) {
			average = new Double(this.getActiveChannelsLast6Months())/new Double(this.getActiveHubs());
			return AppConstants.FORMAT_CURRENCY.format(average);
		} else {
			return null;
		}
	}
	
	public String getBirthDate() {
		if (this.getHub().getCreationTime() != null) {
			return AppConstants.FORMAT_DAY.format(this.getHub().getCreationTime());
		} else {
			return null;
		}
	}
	
	public boolean isHttps() {
		return this.getHub().getBaseUrl().startsWith("https");
	}
	
	public boolean isDirectory() {
		return !this.getHub().getDirectoryMode().equals(AppConstants.DIRECTORY_MODE_NORMAL);
	}
}
