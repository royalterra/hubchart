package it.hubzilla.hubchart.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author paolo
 */
@Entity
@Table(name = "ip2nation")
public class Ip2nation extends BaseEntity {
	private static final long serialVersionUID = 6797303299266138820L;
	@Id
	@Basic(optional = false)
	@Column(name = "ip", nullable = false)
	private Long ip;
	@Basic(optional = false)
	@Column(name = "country", nullable = false, length = 2)
	private String country;

	public Ip2nation() {
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (ip != null ? ip.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Ip2nation)) {
			return false;
		}
		Ip2nation other = (Ip2nation) object;
		if ((this.ip == null && other.ip != null)
				|| (this.ip != null && !this.ip.equals(other.ip))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Ip2nation[ip=" + ip + "]";
	}

}
