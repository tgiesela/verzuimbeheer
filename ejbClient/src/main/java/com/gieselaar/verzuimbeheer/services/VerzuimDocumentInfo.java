package com.gieselaar.verzuimbeheer.services;

import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class VerzuimDocumentInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date aanmaakdatum;
	private int aanmaakuser;
	private String documentnaam;
	private String omschrijving;
	private String padnaam;
	private int verzuimId;

	public Date getAanmaakdatum() {
		return aanmaakdatum;
	}

	public void setAanmaakdatum(Date aanmaakdatum) {
		this.aanmaakdatum = aanmaakdatum;
	}

	public int getAanmaakuser() {
		return aanmaakuser;
	}

	public void setAanmaakuser(int aanmaakuser) {
		this.aanmaakuser = aanmaakuser;
	}

	public String getDocumentnaam() {
		return documentnaam;
	}

	public void setDocumentnaam(String documentnaam) {
		this.documentnaam = documentnaam;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public String getPadnaam() {
		return padnaam;
	}

	public void setPadnaam(String padnaam) {
		this.padnaam = padnaam;
	}

	public int getVerzuimId() {
		return verzuimId;
	}

	public void setVerzuimId(int verzuimId) {
		this.verzuimId = verzuimId;
	}

	@Override
	public boolean validate() throws ValidationException {
		if (this.getDocumentnaam() == null  || this.getDocumentnaam().isEmpty())
			throw new ValidationException("Documentnaam niet ingevuld");
		if (this.getPadnaam() == null || this.getPadnaam().isEmpty())
			throw new ValidationException("Document niet ingevuld");
		if (this.getOmschrijving() == null)
			throw new ValidationException("Documentomschrijving niet ingevuld");
		return false;
	}

}
