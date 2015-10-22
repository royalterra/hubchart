package it.hubzilla.hubchart.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author paolo
 */
@Entity
@Table(name = "image_cache")
public class ImageCache extends BaseEntity {
	private static final long serialVersionUID = -8856767042315582630L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)	
	@Lob
	@Column(name="image", nullable=false, columnDefinition="mediumblob")
	private byte[] image;
	@Basic(optional = false)
	@Column(name = "mime_type", nullable=false, length = 128)
	private String mimeType;
	@Column(name = "title", length = 128)
	private String title;
	@Basic(optional = false)
	@Column(name = "chart_type", nullable=false, length = 4)
	private String chartType;
	@Basic(optional = false)
	@Column(name = "id_stat", nullable=false)
	private Integer idStat;
	@Basic(optional = false)
	@Column(name = "update_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;
	
	public ImageCache() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Integer getIdStat() {
		return idStat;
	}

	public void setIdStat(Integer idStat) {
		this.idStat = idStat;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ImageCache)) {
			return false;
		}
		ImageCache other = (ImageCache) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ImageCache[id=" + id + "]";
	}

}
