package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the WERKGEVER_HAS_PAKKET database table.
 * 
 */
@Embeddable
public class WerkgeverHasPakketPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int werkgever_ID;

	private int pakket_ID;

    public WerkgeverHasPakketPK() {
    }
	public int getWerkgever_ID() {
		return this.werkgever_ID;
	}
	public void setWerkgever_ID(int werkgever_ID) {
		this.werkgever_ID = werkgever_ID;
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
		if (!(other instanceof WerkgeverHasPakketPK)) {
			return false;
		}
		WerkgeverHasPakketPK castOther = (WerkgeverHasPakketPK)other;
		return 
			(this.werkgever_ID == castOther.werkgever_ID)
			&& (this.pakket_ID == castOther.pakket_ID);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.werkgever_ID;
		hash = hash * prime + this.pakket_ID;
		
		return hash;
    }
}