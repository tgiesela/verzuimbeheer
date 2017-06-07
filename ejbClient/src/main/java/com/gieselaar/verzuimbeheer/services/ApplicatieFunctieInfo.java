package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class ApplicatieFunctieInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String functieId;
	private String functieomschrijving;

	public ApplicatieFunctieInfo(){
		super();
	}
	public String getFunctieId() {
		return functieId;
	}

	public void setFunctieId(String functieId) {
		this.functieId = functieId;
	}

	public String getFunctieomschrijving() {
		return functieomschrijving;
	}

	public void setFunctieomschrijving(String functieomschrijving) {
		this.functieomschrijving = functieomschrijving;
	}

	@Override
	public boolean validate() throws ValidationException {
		if ((this.functieomschrijving == null) || (this.functieomschrijving.isEmpty()))
			throw new ValidationException("Functie omschrijving niet ingevuld");
		if ((this.functieId == null) || (this.functieId.isEmpty()))
			throw new ValidationException("Functie identificatie niet ingevuld");
		return false;
	}

}
