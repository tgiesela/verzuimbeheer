package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the GEBRUIKER_ROL database table.
 * 
 */
@Entity
@Table(name="GEBRUIKER_ROL")
public class GebruikerRol extends com.gieselaar.verzuimbeheer.entitiesutils.EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private GebruikerRolPK id;

    public GebruikerRol() {
    }

	public GebruikerRolPK getId() {
		return this.id;
	}

	public void setId(GebruikerRolPK id) {
		this.id = id;
	}
	
}