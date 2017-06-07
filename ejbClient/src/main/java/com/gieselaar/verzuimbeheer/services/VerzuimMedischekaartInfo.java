package com.gieselaar.verzuimbeheer.services;

import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class VerzuimMedischekaartInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String medischekaart;
	private boolean openbaar;
	private int user;
	private int verzuimId;
	private Date wijzigingsdatum;
	@Override
	public boolean validate() throws ValidationException {
		if (this.getVerzuimId() <= 0)
			throw new ValidationException("Verzuim niet ingevuld");
		return false;
	}
	public String getMedischekaart() {
		return medischekaart;
	}
	public void setMedischekaart(String medischekaart) {
		this.medischekaart = medischekaart;
	}
	public boolean getOpenbaar() {
		return openbaar;
	}
	public void setOpenbaar(boolean openbaar) {
		this.openbaar = openbaar;
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
	public Date getWijzigingsdatum() {
		return wijzigingsdatum;
	}
	public void setWijzigingsdatum(Date wijzigingsdatum) {
		this.wijzigingsdatum = wijzigingsdatum;
	}
}
