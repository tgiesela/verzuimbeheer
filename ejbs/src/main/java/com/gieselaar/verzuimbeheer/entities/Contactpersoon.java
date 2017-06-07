package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the CONTACTPERSOON database table.
 * 
 */
@Entity
public class Contactpersoon extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String achternaam;
	private String emailadres;
	private int geslacht;
	private String mobiel;
	private String telefoon;
	private String voorletters;
	private String voornaam;
	private String voorvoegsel;

    public Contactpersoon() {
    }

	public String getAchternaam() {
		return this.achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public String getEmailadres() {
		return this.emailadres;
	}

	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}

	public int getGeslacht() {
		return this.geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public String getMobiel() {
		return this.mobiel;
	}

	public void setMobiel(String mobiel) {
		this.mobiel = mobiel;
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

	public String getVoorvoegsel() {
		return this.voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

}