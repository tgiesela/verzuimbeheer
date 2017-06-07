package com.gieselaar.verzuimbeheer.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBase;

/**
 * The persistent class for the GEBRUIKER_ROL database table.
 * 
 */
@Entity
@Table(name="GEBRUIKER_WERKGEVER")
public class GebruikerWerkgever extends EntityBase {
	@EmbeddedId
	private GebruikerWerkgeverPK id;
	
	public GebruikerWerkgever(){
		
	}

	public GebruikerWerkgeverPK getId() {
		return id;
	}

	public void setId(GebruikerWerkgeverPK id) {
		this.id = id;
	}

}
