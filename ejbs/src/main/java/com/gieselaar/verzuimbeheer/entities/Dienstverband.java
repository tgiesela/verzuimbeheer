package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the DIENSTVERBAND database table.
 * 
 */
@Entity
public class Dienstverband extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

    @Temporal( TemporalType.DATE)
	private Date einddatumcontract;

	private String functie;

	private String personeelsnummer;

    @Temporal( TemporalType.DATE)
	private Date startdatumcontract;

	private int werkgever_ID;

	private int werknemer_ID;

	private BigDecimal werkweek;

    public Dienstverband() {
    }

	public Date getEinddatumcontract() {
		return this.einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public String getFunctie() {
		return this.functie;
	}

	public void setFunctie(String functie) {
		this.functie = functie;
	}

	public String getPersoneelsnummer() {
		return this.personeelsnummer;
	}

	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}

	public Date getStartdatumcontract() {
		return this.startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public int getWerkgever_ID() {
		return this.werkgever_ID;
	}

	public void setWerkgever_ID(int werkgever_ID) {
		this.werkgever_ID = werkgever_ID;
	}

	public int getWerknemer_ID() {
		return this.werknemer_ID;
	}

	public void setWerknemer_ID(int werknemer_ID) {
		this.werknemer_ID = werknemer_ID;
	}

	public BigDecimal getWerkweek() {
		return this.werkweek;
	}

	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}

}