package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class CascodeGroepInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String naam;
	private String omschrijving;

	public String getNaam() {
		return naam;
	}
	public void setNaam(String naam) {
		this.naam = naam;
	}
	public String getOmschrijving() {
		return omschrijving;
	}
	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.naam == null || this.naam.isEmpty()){
			throw new ValidationException("Naam mag niet leeg zijn");
		}
		return false;
	}

}
