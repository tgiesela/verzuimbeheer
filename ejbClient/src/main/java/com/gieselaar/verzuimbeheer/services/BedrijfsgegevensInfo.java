package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class BedrijfsgegevensInfo extends InfoBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1625073206456588221L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private String naam;
	private String telefoonnummer;
	private String mobiel;
	private String fax;
	private AdresInfo vestigingsAdres;
	private AdresInfo postAdres;
	private String emailadres;
	private String website;
	private String kvknr;
	private String bankrekening;
	private String btwnummer;

	public BedrijfsgegevensInfo() {
		super();
	}

	public AdresInfo getVestigingsAdres() {
		return vestigingsAdres;
	}

	public void setVestigingsAdres(AdresInfo vestigingsAdres) {
		this.vestigingsAdres = vestigingsAdres;
	}

	public AdresInfo getPostAdres() {
		return postAdres;
	}

	public void setPostAdres(AdresInfo postAdres) {
		this.postAdres = postAdres;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getTelefoonnummer() {
		return telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
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

	@Override
	public boolean validate() throws ValidationException {
		return false;
	}
}
