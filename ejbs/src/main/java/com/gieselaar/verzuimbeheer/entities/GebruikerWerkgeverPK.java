package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * The primary key class for the GEBRUIKER_ROL database table.
 * 
 */
@Embeddable
public class GebruikerWerkgeverPK implements Serializable{
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int gebruikerid;

	private int werkgeverid;

    public GebruikerWerkgeverPK() {
    }
	public int getGebruikerid() {
		return this.gebruikerid;
	}
	public void setGebruikerid(int gebruikerid) {
		this.gebruikerid = gebruikerid;
	}

	public int getWerkgeverid() {
		return werkgeverid;
	}
	public void setWerkgeverid(int werkgeverid) {
		this.werkgeverid = werkgeverid;
	}
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GebruikerWerkgeverPK)) {
			return false;
		}
		GebruikerWerkgeverPK castOther = (GebruikerWerkgeverPK)other;
		return 
			(this.gebruikerid == castOther.gebruikerid)
			&& (this.werkgeverid == castOther.werkgeverid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gebruikerid;
		hash = hash * prime + this.werkgeverid;
		
		return hash;
    }
}
