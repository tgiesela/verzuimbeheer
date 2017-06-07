package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the BEDRIJFSGEGEVENS database table.
 * 
 */
@Entity
public class Bedrijfsgegevens extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String naam;
	private String telefoon;
	private String mobiel;
	private String fax;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="vestigingsadres_ID")
	private Adres vestigingsadres;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="postadres_ID")
	private Adres postadres;

	private String emailadres;
	private String website;
	private String kvknr;
	private String bankrekening;
	private String btwnummer;

    public Bedrijfsgegevens() {
    }

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getTelefoon() {
		return this.telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	public Adres getPostadres() {
		return postadres;
	}

	public void setPostadres(Adres postadres) {
		this.postadres = postadres;
	}

	public Adres getVestigingsadres() {
		return vestigingsadres;
	}

	public void setVestigingsadres(Adres vestigingsadres) {
		this.vestigingsadres = vestigingsadres;
	}

	public String getEmailadres() {
		return emailadres;
	}

	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getKvknr() {
		return kvknr;
	}

	public void setKvknr(String kvknr) {
		this.kvknr = kvknr;
	}

	public String getBankrekening() {
		return bankrekening;
	}

	public void setBankrekening(String bankrekening) {
		this.bankrekening = bankrekening;
	}

	public String getBtwnummer() {
		return btwnummer;
	}

	public void setBtwnummer(String btwnummer) {
		this.btwnummer = btwnummer;
	}

	public String getMobiel() {
		return mobiel;
	}

	public void setMobiel(String mobiel) {
		this.mobiel = mobiel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

}