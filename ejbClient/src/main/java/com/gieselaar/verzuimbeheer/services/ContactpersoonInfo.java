package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class ContactpersoonInfo extends InfoBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2204129727800401319L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private String achternaam;
	private String emailadres;
	private __geslacht geslacht;
	private String mobiel;
	private String telefoon;
	private String voorletters;
	private String voornaam;
	private String voorvoegsel;

	public ContactpersoonInfo() {
		super();
	}

	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public String getEmailadres() {
		return emailadres;
	}

	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}

	public __geslacht getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(__geslacht geslacht) {
		this.geslacht = geslacht;
	}

	public String getMobiel() {
		return mobiel;
	}

	public void setMobiel(String mobiel) {
		this.mobiel = mobiel;
	}

	public String getTelefoon() {
		return telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	public String getVoorletters() {
		return voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	public boolean isEmpty(){
		boolean naamempty = (this.getAchternaam() == null) || (this.getAchternaam().isEmpty());
		boolean voornaamempty = (this.getVoornaam() == null) || (this.getVoornaam().isEmpty());
		boolean voorlettersempty = (this.getVoorletters() == null) || (this.getVoorletters().isEmpty());

		return naamempty && voornaamempty && voorlettersempty;  
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.isEmpty())
			return true;
		else{
			if (this.getAchternaam().isEmpty())
				throw new ValidationException("Achternaam niet ingevuld");
			if (this.geslacht != __geslacht.MAN && this.geslacht != __geslacht.VROUW)
				throw new ValidationException("geslacht moet MAN of VROUW zijn");
		}
		return false;
	}
}
