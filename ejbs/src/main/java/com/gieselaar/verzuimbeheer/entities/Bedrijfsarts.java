package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the BEDRIJFSARTS database table.
 * 
 */
@Entity
public class Bedrijfsarts extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String achternaam;

	private Integer arbodienst_ID;

	private String email;

	private int geslacht;

	private String telefoon;

	private String voorletters;

	private String voornaam;

    public Bedrijfsarts() {
    }

	public String getAchternaam() {
		return this.achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public Integer getArbodienst_ID() {
		return this.arbodienst_ID;
	}

	public void setArbodienst_ID(Integer arbodienst_ID) {
		this.arbodienst_ID = arbodienst_ID;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getGeslacht() {
		return this.geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public String getTelefoon() {
		return this.telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	public String getVoorletters() {
		return this.voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public String getVoornaam() {
		return this.voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

}