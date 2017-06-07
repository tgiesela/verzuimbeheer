package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the VERZUIMDOCUMENT database table.
 * 
 */
@Entity
public class Verzuimdocument extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

    @Temporal( TemporalType.TIMESTAMP)
	private Date aanmaakdatum;

	private int aanmaakuser;

	private String documentnaam;

	private String omschrijving;

	private String padnaam;

	@Column(name="verzuim_id")
	private int verzuimId;

    public Verzuimdocument() {
    }

	public Date getAanmaakdatum() {
		return this.aanmaakdatum;
	}

	public void setAanmaakdatum(Date aanmaakdatum) {
		this.aanmaakdatum = aanmaakdatum;
	}

	public int getAanmaakuser() {
		return this.aanmaakuser;
	}

	public void setAanmaakuser(int aanmaakuser) {
		this.aanmaakuser = aanmaakuser;
	}

	public String getDocumentnaam() {
		return this.documentnaam;
	}

	public void setDocumentnaam(String documentnaam) {
		this.documentnaam = documentnaam;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public String getPadnaam() {
		return this.padnaam;
	}

	public void setPadnaam(String padnaam) {
		this.padnaam = padnaam;
	}

	public int getVerzuimId() {
		return this.verzuimId;
	}

	public void setVerzuimId(int verzuimId) {
		this.verzuimId = verzuimId;
	}

}