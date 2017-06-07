package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the FACTUURKOP database table.
 * 
 */
@Entity
@NamedQuery(name="Factuurkop.findAll", query="SELECT f FROM Factuurkop f")
public class Factuurkop extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String omschrijving;
	private int prioriteit;

	public Factuurkop() {
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public int getPrioriteit() {
		return this.prioriteit;
	}

	public void setPrioriteit(int prioriteit) {
		this.prioriteit = prioriteit;
	}

}