package com.gieselaar.verzuimbeheer.services;

import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class VerzuimActiviteitInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int activiteitId;
	private Date datumactiviteit;
	private Date datumdeadline;
	private String opmerkingen;
	private int tijdbesteed;
	private int user;
	private int verzuimId;

	public int getActiviteitId() {
		return activiteitId;
	}

	public void setActiviteitId(int activiteitId) {
		this.activiteitId = activiteitId;
	}

	public Date getDatumactiviteit() {
		return datumactiviteit;
	}

	public void setDatumactiviteit(Date datumactiviteit) {
		this.datumactiviteit = datumactiviteit;
	}

	public Date getDatumdeadline() {
		return datumdeadline;
	}

	public void setDatumdeadline(Date datumdeadline) {
		this.datumdeadline = datumdeadline;
	}

	public String getOpmerkingen() {
		return opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public int getTijdbesteed() {
		return tijdbesteed;
	}

	public void setTijdbesteed(int tijdbesteed) {
		this.tijdbesteed = tijdbesteed;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getVerzuimId() {
		return verzuimId;
	}

	public void setVerzuimId(int verzuimId) {
		this.verzuimId = verzuimId;
	}

	@Override
	public boolean validate() throws ValidationException {
		if (this.getDatumactiviteit() == null)
			throw new ValidationException("Datum activiteit niet ingevuld");
		if (this.getDatumactiviteit().after(new Date()))
			throw new ValidationException("Datum activiteit ligt in de toekomst");
		if (this.getActiviteitId() <= 0)
			throw new ValidationException("Activiteit niet ingevuld");
		if (this.getVerzuimId() <= 0)
			throw new ValidationException("Verzuim niet ingevuld");

		return false;
	}

}
