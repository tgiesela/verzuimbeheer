package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class FactuurkopInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String omschrijving;
	private int prioriteit;
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public int getPrioriteit() {
		return prioriteit;
	}

	public void setPrioriteit(int prioriteit) {
		this.prioriteit = prioriteit;
	}

}
