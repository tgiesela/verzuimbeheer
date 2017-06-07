package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;


public class UitvoeringsinstituutInfo extends InfoBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String naam;
	private String telefoonnummer;
	private AdresInfo vestigingsadres;
	private AdresInfo postadres;

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public AdresInfo getVestigingsadres() {
		return vestigingsadres;
	}

	public void setVestigingsadres(AdresInfo vestigingsadres) {
		this.vestigingsadres = vestigingsadres;
	}

	public AdresInfo getPostadres() {
		return postadres;
	}

	public void setPostadres(AdresInfo postadres) {
		this.postadres = postadres;
	}

	public String getTelefoonnummer() {
		return telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
	}

	@Override
	public boolean validate() throws ValidationException {
		if (this.getNaam() == null || this.getNaam().isEmpty())
			throw new ValidationException("Naam is niet ingevuld");
		if (this.getPostadres() == null || this.getPostadres().isEmtpy())
			throw new ValidationException("Postadres ontbreekt");
		if (this.getVestigingsadres() == null || this.getVestigingsadres().isEmtpy())
			throw new ValidationException("Vestigingsadres ontbreekt");
		return false;
	}

}
