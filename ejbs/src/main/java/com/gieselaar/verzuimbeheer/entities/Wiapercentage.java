package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the WIAPERCENTAGE database table.
 * 
 */
@Entity
public class Wiapercentage extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="code_wia_percentage")
	private int codeWiaPercentage;

    @Temporal( TemporalType.DATE)
	private Date einddatum;

    @Temporal( TemporalType.DATE)
	private Date startdatum;

	private Integer werknemer_ID;

    public Wiapercentage() {
    }

	public int getCodeWiaPercentage() {
		return this.codeWiaPercentage;
	}

	public void setCodeWiaPercentage(int codeWiaPercentage) {
		this.codeWiaPercentage = codeWiaPercentage;
	}

	public Date getEinddatum() {
		return this.einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public Date getStartdatum() {
		return this.startdatum;
	}

	public void setStartdatum(Date startdatum) {
		this.startdatum = startdatum;
	}

	public Integer getWerknemer_ID() {
		return this.werknemer_ID;
	}

	public void setWerknemer_ID(Integer werknemer_ID) {
		this.werknemer_ID = werknemer_ID;
	}

}