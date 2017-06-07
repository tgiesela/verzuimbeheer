package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the APPLICATIEFUNCTIE database table.
 * 
 */
@Entity
public class Applicatiefunctie extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

//	@Column(name="functie_id")
	private String functie_id;

	private String functieomschrijving;

    public Applicatiefunctie() {
    }

	public String getFunctieId() {
		return this.functie_id;
	}

	public void setFunctieId(String functieId) {
		this.functie_id = functieId;
	}

	public String getFunctieomschrijving() {
		return this.functieomschrijving;
	}

	public void setFunctieomschrijving(String functieomschrijving) {
		this.functieomschrijving = functieomschrijving;
	}

}