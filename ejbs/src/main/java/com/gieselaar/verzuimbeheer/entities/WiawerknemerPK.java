package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the wiawerknemer database table.
 * 
 */
@Embeddable
public class WiawerknemerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int werknemer_ID;

    @Temporal( TemporalType.DATE)
	private java.util.Date startdatum;

    public WiawerknemerPK() {
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
		if (!(other instanceof WiawerknemerPK)) {
			return false;
		}
		WiawerknemerPK castOther = (WiawerknemerPK)other;
		return 
			(this.werknemer_ID == castOther.werknemer_ID)
			&& this.startdatum.equals(castOther.startdatum);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.werknemer_ID;
		hash = hash * prime + this.startdatum.hashCode();
		
		return hash;
    }
}