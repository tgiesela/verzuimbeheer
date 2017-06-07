package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the FACTUURCATEGORIE database table.
 * 
 */
@Entity
@NamedQuery(name="Factuurcategorie.findAll", query="SELECT f FROM Factuurcategorie f")
public class Factuurcategorie extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private int btwcategorie;
	private Integer factuurkopid;
	private int isomzet;
	private String omschrijving;

	public Factuurcategorie() {
	}

	public int getBtwcategorie() {
		return this.btwcategorie;
	}

	public void setBtwcategorie(int btwcategorie) {
		this.btwcategorie = btwcategorie;
	}

	public Integer getFactuurkopid() {
		return this.factuurkopid;
	}

	public void setFactuurkopid(Integer factuurkopid) {
		this.factuurkopid = factuurkopid;
	}

	public int getIsomzet() {
		return this.isomzet;
	}

	public void setIsomzet(int isomzet) {
		this.isomzet = isomzet;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

}