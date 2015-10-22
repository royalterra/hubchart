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
@Table(name = "ip2nationCountries")
public class Ip2nationCountries extends BaseEntity {
	private static final long serialVersionUID = -8094495121765157116L;
	@Id
	@Basic(optional = false)
	@Column(name = "code", nullable = false, length = 4)
	private String code;
	@Basic(optional = false)
	@Column(name = "iso_code_2", nullable = false, length = 2)
	private String isoCode2;
	@Column(name = "iso_code_3", length = 3)
	private String isoCode3;
	@Basic(optional = false)
	@Column(name = "iso_country", nullable = false, length = 255)
	private String isoCountry;
	@Basic(optional = false)
	@Column(name = "country", nullable = false, length = 255)
	private String country;
	@Basic(optional = false)
	@Column(name = "lat", nullable = false)
	private Float lat;
	@Basic(optional = false)
	@Column(name = "lon", nullable = false)
	private Float lon;

	public Ip2nationCountries() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIsoCode2() {
		return isoCode2;
	}

	public void setIsoCode2(String isoCode2) {
		this.isoCode2 = isoCode2;
	}

	public String getIsoCode3() {
		return isoCode3;
	}

	public void setIsoCode3(String isoCode3) {
		this.isoCode3 = isoCode3;
	}

	public String getIsoCountry() {
		return isoCountry;
	}

	public void setIsoCountry(String isoCountry) {
		this.isoCountry = isoCountry;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getLat() {
		return lat;
	}

	public void setLat(Float lat) {
		this.lat = lat;
	}

	public Float getLon() {
		return lon;
	}

	public void setLon(Float lon) {
		this.lon = lon;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (code != null ? code.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Ip2nationCountries)) {
			return false;
		}
		Ip2nationCountries other = (Ip2nationCountries) object;
		if ((this.code == null && other.code != null)
				|| (this.code != null && !this.code.equals(other.code))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Ip2nationCountries[code=" + code + "]";
	}

}
