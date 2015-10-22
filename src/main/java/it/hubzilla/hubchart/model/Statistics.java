/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.hubzilla.hubchart.model;

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
@Table(name = "statistics")
public class Statistics extends BaseEntity {
	private static final long serialVersionUID = 2082192059768219131L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
	@Column(name = "poll_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date pollTime;
	@Column(name = "total_channels")
	private Integer totalChannels;
	@Column(name = "active_channels_last_month")
	private Integer activeChannelsLastMonth;
	@Column(name = "active_channels_last_6_months")
	private Integer activeChannelsLast6Months;
	@Column(name = "total_posts")
	private Integer totalPosts;
	@Column(name = "active_hubs")
	private Integer activeHubs;
    @JoinColumn(name = "id_hub", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Hubs hub;
    
    public Statistics() {
    }

    public Statistics(Integer id) {
        this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPollTime() {
		return pollTime;
	}

	public void setPollTime(Date pollTime) {
		this.pollTime = pollTime;
	}

	public Integer getTotalChannels() {
		return totalChannels;
	}

	public void setTotalChannels(Integer totalChannels) {
		this.totalChannels = totalChannels;
	}

	public Integer getActiveChannelsLastMonth() {
		return activeChannelsLastMonth;
	}

	public void setActiveChannelsLastMonth(Integer activeChannelsLastMonth) {
		this.activeChannelsLastMonth = activeChannelsLastMonth;
	}

	public Integer getActiveChannelsLast6Months() {
		return activeChannelsLast6Months;
	}

	public void setActiveChannelsLast6Months(Integer activeChannelsLast6Months) {
		this.activeChannelsLast6Months = activeChannelsLast6Months;
	}

	public Integer getTotalPosts() {
		return totalPosts;
	}

	public void setTotalPosts(Integer totalPosts) {
		this.totalPosts = totalPosts;
	}

	public Integer getActiveHubs() {
		return activeHubs;
	}

	public void setActiveHubs(Integer activeHubs) {
		this.activeHubs = activeHubs;
	}

	public Hubs getHub() {
		return hub;
	}

	public void setHub(Hubs hub) {
		this.hub = hub;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Statistics)) {
            return false;
        }
        Statistics other = (Statistics) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "Statistics[id=" + id + "] ";
        return result;
    }

}
