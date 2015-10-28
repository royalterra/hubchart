package it.hubzilla.hubchart.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author paolo
 */
@Entity
@Table(name = "visitors")
public class Visitors extends BaseEntity {
	private static final long serialVersionUID = 8956323505860039108L;

	private static final SimpleDateFormat LOG_TIMESTAMP = new SimpleDateFormat("MM-dd HH:mm:ss");
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	@Column(name = "country_code", length = 16)
	private String countryCode;
	@Column(name = "ip_hash", length = 32)
	private String ipHash;
	@Basic(optional = false)
	@Column(name = "new_visitor", nullable=false)
	private boolean newVisitor;

	public String getFormattedTime() {
		String result = "";
		if (time != null) result = LOG_TIMESTAMP.format(time);
		return result;
	}
	
	public Visitors() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isNewVisitor() {
		return newVisitor;
	}

	public void setNewVisitor(boolean newVisitor) {
		this.newVisitor = newVisitor;
	}

	public String getIpHash() {
		return ipHash;
	}

	public void setIpHash(String ipHash) {
		this.ipHash = ipHash;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Visitors)) {
			return false;
		}
		Visitors other = (Visitors) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Logs[id=" + id + "]";
	}

}
