package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class BedrijfsartsInfo extends InfoBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String achternaam;
	private Integer arbodienstId;
	private String email;
	private __geslacht geslacht;
	private String telefoon;
	private String voorletters;
	private String voornaam;

	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public Integer getArbodienstId() {
		return arbodienstId;
	}

	public void setArbodienstId(Integer arbodienstId) {
		this.arbodienstId = arbodienstId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public __geslacht getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(__geslacht geslacht) {
		this.geslacht = geslacht;
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

	@Override
	public boolean validate() throws ValidationException {
		if ((this.getAchternaam() == null) || (this.getAchternaam().isEmpty()))
			throw new ValidationException("Achternaam niet ingevuld");
		if ((this.getArbodienstId() == null) ||(this.getArbodienstId() <= 0))
			throw new ValidationException("Arbodienst niet ingevuld");
		if (this.geslacht != __geslacht.MAN && this.geslacht != __geslacht.VROUW)
			throw new ValidationException("geslacht moet MAN of VROUW zijn");
		return false;
	}

}
