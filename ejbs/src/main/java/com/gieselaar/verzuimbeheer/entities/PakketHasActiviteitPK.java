package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PAKKET_HAS_ACTIVITEIT database table.
 * 
 */
@Embeddable
public class PakketHasActiviteitPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int activiteit_ID;

	private int pakket_ID;

    public PakketHasActiviteitPK() {
    }
	public int getActiviteit_ID() {
		return this.activiteit_ID;
	}
	public void setActiviteit_ID(int activiteit_ID) {
		this.activiteit_ID = activiteit_ID;
	}
	public int getPakket_ID() {
		return this.pakket_ID;
	}
	public void setPakket_ID(int pakket_ID) {
		this.pakket_ID = pakket_ID;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PakketHasActiviteitPK)) {
			return false;
		}
		PakketHasActiviteitPK castOther = (PakketHasActiviteitPK)other;
		return 
			(this.activiteit_ID == castOther.activiteit_ID)
			&& (this.pakket_ID == castOther.pakket_ID);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.activiteit_ID;
		hash = hash * prime + this.pakket_ID;
		
		return hash;
    }
}