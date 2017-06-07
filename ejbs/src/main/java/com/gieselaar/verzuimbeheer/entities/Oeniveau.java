package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the OENIVEAU database table.
 * 
 */
@Entity
@NamedQuery(name="Oeniveau.findAll", query="SELECT o FROM Oeniveau o")
public class Oeniveau extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer oeniveau;
	private String naam;
	private Integer parentoeniveau_id;
	
	public Oeniveau() {
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Integer getOeniveau() {
		return this.oeniveau;
	}

	public void setOeniveau(Integer oeniveau) {
		this.oeniveau = oeniveau;
	}

	public Integer getParentoeniveauId() {
		return parentoeniveau_id;
	}

	public void setParentoeniveauId(Integer parentoeniveau_id) {
		this.parentoeniveau_id = parentoeniveau_id;
	}

}