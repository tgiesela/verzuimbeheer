package com.gieselaar.verzuimbeheer.services;

import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class PakketInfo extends InfoBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4894651595201554675L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private String naam;
	private String omschrijving;
	private List<ActiviteitInfo> activiteiten;
	
	public PakketInfo() {
		super();
	}
    @Override
	public boolean validate() throws ValidationException{
		if (this.getNaam() == null || this.getNaam().isEmpty())
			throw new ValidationException("Naam pakket mag niet leeg zijn"); 
    	
    	return true;
	}

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

	public List<ActiviteitInfo> getAktiviteiten() {
		return activiteiten;
	}

	public void setActiviteiten(List<ActiviteitInfo> activiteiten) {
		this.activiteiten = activiteiten;
	}
}
