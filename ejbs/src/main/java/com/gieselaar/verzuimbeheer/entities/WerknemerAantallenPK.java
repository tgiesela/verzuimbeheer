package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the OpenVerzuim database table.
 * 
 */
@Embeddable
public class WerknemerAantallenPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer werkgeverid;
	private Integer afdelingid;
	private Integer geslacht;
    @Temporal( TemporalType.TIMESTAMP)
	private Date startdatum;
    @Temporal( TemporalType.TIMESTAMP)
	private Date einddatum;

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public Integer getAfdelingid() {
		return afdelingid;
	}

	public void setAfdelingid(Integer afdelingid) {
		this.afdelingid = afdelingid;
	}

	public Integer getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(Integer geslacht) {
		this.geslacht = geslacht;
	}

	public Date getStartdatum() {
		return startdatum;
	}

	public void setStartdatum(Date startdatum) {
		this.startdatum = startdatum;
	}

	public Date getEinddatum() {
		return einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof WerknemerAantallenPK)) {
			return false;
		}
		WerknemerAantallenPK castOther = (WerknemerAantallenPK)other;
		return 
			(this.afdelingid.intValue() == castOther.afdelingid.intValue())
			&& (this.werkgeverid.intValue() == castOther.werkgeverid.intValue())
			&& (this.geslacht.intValue() == castOther.geslacht.intValue());

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.afdelingid;
		hash = hash * prime + this.werkgeverid;
		hash = hash * prime + this.geslacht;
		return hash;
    }

}