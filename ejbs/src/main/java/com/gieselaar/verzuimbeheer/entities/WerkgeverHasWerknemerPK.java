package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the werkgever_has_werknemer database table.
 * 
 */
@Embeddable
public class WerkgeverHasWerknemerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int werkgever_ID;

	private int werknemer_ID;

    public WerkgeverHasWerknemerPK() {
    }
	public int getWerkgever_ID() {
		return this.werkgever_ID;
	}
	public void setWerkgever_ID(int werkgever_ID) {
		this.werkgever_ID = werkgever_ID;
	}
	public int getWerknemer_ID() {
		return this.werknemer_ID;
	}
	public void setWerknemer_ID(int werknemer_ID) {
		this.werknemer_ID = werknemer_ID;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof WerkgeverHasWerknemerPK)) {
			return false;
		}
		WerkgeverHasWerknemerPK castOther = (WerkgeverHasWerknemerPK)other;
		return 
			(this.werkgever_ID == castOther.werkgever_ID)
			&& (this.werknemer_ID == castOther.werknemer_ID);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.werkgever_ID;
		hash = hash * prime + this.werknemer_ID;
		
		return hash;
    }
}