package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the DOCUMENTTEMPLATE database table.
 * 
 */
@Entity
public class Documenttemplate extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String naam;

	private String omschrijving;

	private String padnaam;

    public Documenttemplate() {
    }

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public String getPadnaam() {
		return this.padnaam;
	}

	public void setPadnaam(String padnaam) {
		this.padnaam = padnaam;
	}

}