/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.hubzilla.hubchart.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author paolo
 */
@Entity
@Table(name = "languages")
public class Languages extends BaseEntity {
	private static final long serialVersionUID = 7093739642078435584L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
	@Basic(optional = false)
	@Column(name = "code", unique=true, nullable = false, length = 8)
	private String code;
	@Basic(optional = false)
	@Column(name = "country", unique=true, nullable = false, length = 128)
	private String country;
	@Basic(optional = false)
	@Column(name = "language", unique=true, nullable = false, length = 128)
	private String language;
	@Basic(optional = false)
	@Column(name = "main", nullable = false)
	private boolean main;
	
    public Languages() {
    }

    public Languages(Integer id) {
        this.id = id;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}


    public boolean getMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        if (!(object instanceof Languages)) {
            return false;
        }
        Languages other = (Languages) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "Languages[id=" + id + "] ";
        return result;
    }

}
