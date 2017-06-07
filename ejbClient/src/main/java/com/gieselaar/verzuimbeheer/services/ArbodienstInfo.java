package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class ArbodienstInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String naam;
	private String telefoonnummer;
	private AdresInfo postAdres;
	private AdresInfo vestigingsAdres;
	private ContactpersoonInfo contactpersoon;

	public ArbodienstInfo(){
		super();
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public AdresInfo getPostAdres() {
		return this.postAdres;
	}

	public void setPostAdres(AdresInfo postAdres) {
		this.postAdres = postAdres;
	}

	public AdresInfo getVestigingsAdres() {
		return this.vestigingsAdres;
	}

	public void setVestigingsAdres(AdresInfo vestigingsAdres) {
		this.vestigingsAdres = vestigingsAdres;
	}

	public ContactpersoonInfo getContactpersoon() {
		return contactpersoon;
	}

	public void setContactpersoon(ContactpersoonInfo contactpersoon) {
		this.contactpersoon = contactpersoon;
	}

	public String getTelefoonnummer() {
		return telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
	}

	@Override
	public boolean validate() throws ValidationException {
		if ((this.getNaam() == null) || (this.getNaam().isEmpty()))
			throw new ValidationException("Naam niet ingevuld");
		if (this.getPostAdres() == null)
			throw new ValidationException("Postadres ontbreekt");
		if (this.getVestigingsAdres() == null)
			throw new ValidationException("Vestigingsadres ontbreekt");
		if (this.getContactpersoon() == null)
			throw new ValidationException("Contactpersoon ontbreekt");
		return false;
	}

}
