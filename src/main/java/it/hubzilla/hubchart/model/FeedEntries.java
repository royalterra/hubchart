package it.hubzilla.hubchart.model;

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
@Table(name = "feed_entries")
public class FeedEntries extends BaseEntity {
	private static final long serialVersionUID = -8856767042315582630L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "title", length = 1024, nullable = false)
	private String title;
	@Basic(optional = false)
	@Column(name = "link", length = 1024, nullable = false)
	private String link;
	@Basic(optional = false)
	@Column(name = "published_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishedDate;
	@Basic(optional = false)
	@Column(name = "description_type", length = 256, nullable = false)
	private String descriptionType;
	@Basic(optional = false)
	@Column(name = "description_value", length = 65535, nullable = false)
	private String descriptionValue;

	
	public FeedEntries() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getDescriptionType() {
		return descriptionType;
	}

	public void setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
	}

	public String getDescriptionValue() {
		return descriptionValue;
	}

	public void setDescriptionValue(String descriptionValue) {
		this.descriptionValue = descriptionValue;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FeedEntries)) {
			return false;
		}
		FeedEntries other = (FeedEntries) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RegisteredHubs[id=" + id + "]";
	}

}
