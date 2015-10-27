package it.hubzilla.hubchart.model;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.business.PresentationBusiness;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author paolo
 */
@Entity
@Table(name = "hubs")
public class Hubs extends BaseEntity {
	private static final long serialVersionUID = -298878866265848096L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "base_url", unique=true, nullable = false, length = 512)
	private String baseUrl;
	@Basic(optional = false)
	@Column(name = "fqdn", unique=true, nullable = false, length = 512)
	private String fqdn;
	@Column(name = "info", length = 1024)
	private String info;
	@Column(name = "plugins", length = 2048)
	private String plugins;
	@Column(name = "name", length = 256)
	private String name;
	@Column(name = "ip_address", length = 64)
	private String ipAddress;
	@Column(name = "country_code", length = 4)
	private String countryCode;
	@Column(name = "country_name", length = 256)
	private String countryName;
	@Basic(optional = false)
	@Column(name = "network_type", nullable = false, length = 32)
	private String networkType;
	@Column(name = "registration_policy", length = 4)
	private String registrationPolicy;
	@Column(name = "directory_mode", length = 4)
	private String directoryMode;
	@Column(name = "version", length = 32)
	private String version;
	@Column(name = "version_tag", length = 32)
	private String versionTag;
	@Basic(optional = false)
	@Column(name = "hidden", nullable = false)
	private boolean hidden;
	@Basic(optional = false)
	@Column(name = "last_successful_poll_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastSuccessfulPollTime;
	@Basic(optional = false)
	@Column(name = "creation_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;
	@Basic(optional = false)
	@Column(name = "id_last_hub_stats", nullable = false)
	private Integer idLastHubStats;
	@Column(name = "admin_name", length = 512)
	private String adminName;
	@Column(name = "admin_address", length = 512)
	private String adminAddress;
	@Column(name = "admin_channel", length = 512)
	private String adminChannel;
    @JoinColumn(name = "id_language", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Languages language;
	@Column(name = "poll_queue", nullable = true)
	private BigInteger pollQueue;

	public Hubs() {
	}

	public String getVersionDescription() {
		//Icon
		String result = "<img src='"+AppConstants.NETWORK_ICONS.get(this.networkType)+"' "+
				"border='0' title='"+this.networkType+"' />";
		//Adds VersionTag if possible
		if (this.versionTag != null) {
			if (this.versionTag.length() > 0) {
				result += "<b>"+this.versionTag+"</b>";
			}
		}
		//Date
		Date update = null;
		if (this.version != null) {
			if (this.version.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}.*")) {//[0-9]{2}/[0-9]{2}/[0-9]{4}
				//this.version Starts with a date
				try {
					String date = this.version.substring(0,10);
					update = AppConstants.FORMAT_DAY_SQL.parse(date);
					result += " ("+AppConstants.FORMAT_DAY.format(update)+")";
				} catch (ParseException e) { /* do nothing */}
			} else {
				//doesn't start with a date
				result += this.version;
			}
		}
		return result;
	}
	
	public String getFormattedPlugins() {
		return PresentationBusiness.printPluginList(this);
	}
		
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getPlugins() {
		return plugins;
	}

	public void setPlugins(String plugins) {
		this.plugins = plugins;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getRegistrationPolicy() {
		return registrationPolicy;
	}

	public void setRegistrationPolicy(String registrationPolicy) {
		this.registrationPolicy = registrationPolicy;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getDirectoryMode() {
		return directoryMode;
	}

	public void setDirectoryMode(String directoryMode) {
		this.directoryMode = directoryMode;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Integer getIdLastHubStats() {
		return idLastHubStats;
	}

	public void setIdLastHubStats(Integer idLastHubStats) {
		this.idLastHubStats = idLastHubStats;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminAddress() {
		return adminAddress;
	}

	public void setAdminAddress(String adminAddress) {
		this.adminAddress = adminAddress;
	}

	public String getAdminChannel() {
		return adminChannel;
	}

	public void setAdminChannel(String adminChannel) {
		this.adminChannel = adminChannel;
	}

	public Languages getLanguage() {
		return language;
	}

	public void setLanguage(Languages language) {
		this.language = language;
	}

	public String getVersionTag() {
		return versionTag;
	}

	public void setVersionTag(String versionTag) {
		this.versionTag = versionTag;
	}
	
	public Date getLastSuccessfulPollTime() {
		return lastSuccessfulPollTime;
	}

	public void setLastSuccessfulPollTime(Date lastSuccessfulPollTime) {
		this.lastSuccessfulPollTime = lastSuccessfulPollTime;
	}

	public BigInteger getPollQueue() {
		return pollQueue;
	}

	public void setPollQueue(BigInteger pollQueue) {
		this.pollQueue = pollQueue;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Hubs)) {
			return false;
		}
		Hubs other = (Hubs) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Hubs[id=" + id + "]";
	}

}
