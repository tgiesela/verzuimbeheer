package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the CASCODE database table.
 * 
 */
@Entity
public class Cascode extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private int actief;

	private String cascode;

	private int cascodegroep;

	private String omschrijving;

	private int vangnettype;

    public Cascode() {
    }

	public int getActief() {
		return this.actief;
	}

	public void setActief(int actief) {
		this.actief = actief;
	}

	public String getCascode() {
		return this.cascode;
	}

	public void setCascode(String cascode) {
		this.cascode = cascode;
	}

	public int getCascodegroep() {
		return this.cascodegroep;
	}

	public void setCascodegroep(int cascodegroep) {
		this.cascodegroep = cascodegroep;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public int getVangnettype() {
		return this.vangnettype;
	}

	public void setVangnettype(int vangnettype) {
		this.vangnettype = vangnettype;
	}

}