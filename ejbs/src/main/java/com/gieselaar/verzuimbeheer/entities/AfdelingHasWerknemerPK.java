package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the AFDELING_HAS_WERKNEMER database table.
 * 
 */
@Embeddable
public class AfdelingHasWerknemerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int afdeling_ID;

	private int werknemer_ID;

    @Temporal( TemporalType.DATE)
	private java.util.Date startdatum;

    public AfdelingHasWerknemerPK() {
    }
	public int getAfdeling_ID() {
		return this.afdeling_ID;
	}
	public void setAfdeling_ID(int afdeling_ID) {
		this.afdeling_ID = afdeling_ID;
	}
	public int getWerknemer_ID() {
		return this.werknemer_ID;
	}
	public void setWerknemer_ID(int werknemer_ID) {
		this.werknemer_ID = werknemer_ID;
	}
	public java.util.Date getStartdatum() {
		return this.startdatum;
	}
	public void setStartdatum(java.util.Date startdatum) {
		this.startdatum = startdatum;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AfdelingHasWerknemerPK)) {
			return false;
		}
		AfdelingHasWerknemerPK castOther = (AfdelingHasWerknemerPK)other;
		return 
			(this.afdeling_ID == castOther.afdeling_ID)
			&& (this.werknemer_ID == castOther.werknemer_ID)
			&& this.startdatum.equals(castOther.startdatum);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.afdeling_ID;
		hash = hash * prime + this.werknemer_ID;
		hash = hash * prime + this.startdatum.hashCode();
		
		return hash;
    }
}