package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the GEBRUIKER_ROL database table.
 * 
 */
@Embeddable
public class GebruikerRolPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int gebruikerid;

	private int rolid;

    public GebruikerRolPK() {
    }
	public int getGebruikerid() {
		return this.gebruikerid;
	}
	public void setGebruikerid(int gebruikerid) {
		this.gebruikerid = gebruikerid;
	}
	public int getRolid() {
		return this.rolid;
	}
	public void setRolid(int rolid) {
		this.rolid = rolid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GebruikerRolPK)) {
			return false;
		}
		GebruikerRolPK castOther = (GebruikerRolPK)other;
		return 
			(this.gebruikerid == castOther.gebruikerid)
			&& (this.rolid == castOther.rolid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.gebruikerid;
		hash = hash * prime + this.rolid;
		
		return hash;
    }
}