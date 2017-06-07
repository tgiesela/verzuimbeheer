package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the VERZUIM database table.
 * 
 */
@Entity
public class Verzuim extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private int cascode;

	private int dienstverband_ID;

    @Temporal( TemporalType.DATE)
	private Date einddatumverzuim;

	private int gebruiker;

	private int gerelateerdheid;

	private int ketenverzuim;

    @Temporal( TemporalType.DATE)
	private Date meldingsdatum;

	private int meldingswijze;

	private String opmerkingen;

    @Temporal( TemporalType.DATE)
	private Date startdatumverzuim;

	private int vangnettype;

	private int verzuimtype;
	
	private Integer uitkeringnaarwerknemer;

    public Verzuim() {
    }

	public int getCascode() {
		return this.cascode;
	}

	public void setCascode(int cascode) {
		this.cascode = cascode;
	}

	public int getDienstverband_ID() {
		return this.dienstverband_ID;
	}

	public void setDienstverband_ID(int dienstverband_ID) {
		this.dienstverband_ID = dienstverband_ID;
	}

	public Date getEinddatumverzuim() {
		return this.einddatumverzuim;
	}

	public void setEinddatumverzuim(Date einddatumverzuim) {
		this.einddatumverzuim = einddatumverzuim;
	}

	public int getGebruiker() {
		return this.gebruiker;
	}

	public void setGebruiker(int gebruiker) {
		this.gebruiker = gebruiker;
	}

	public int getGerelateerdheid() {
		return this.gerelateerdheid;
	}

	public void setGerelateerdheid(int gerelateerdheid) {
		this.gerelateerdheid = gerelateerdheid;
	}

	public int getKetenverzuim() {
		return this.ketenverzuim;
	}

	public void setKetenverzuim(int ketenverzuim) {
		this.ketenverzuim = ketenverzuim;
	}

	public Date getMeldingsdatum() {
		return this.meldingsdatum;
	}

	public void setMeldingsdatum(Date meldingsdatum) {
		this.meldingsdatum = meldingsdatum;
	}

	public int getMeldingswijze() {
		return this.meldingswijze;
	}

	public void setMeldingswijze(int meldingswijze) {
		this.meldingswijze = meldingswijze;
	}

	public String getOpmerkingen() {
		return this.opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public Date getStartdatumverzuim() {
		return this.startdatumverzuim;
	}

	public void setStartdatumverzuim(Date startdatumverzuim) {
		this.startdatumverzuim = startdatumverzuim;
	}

	public int getVangnettype() {
		return this.vangnettype;
	}

	public void setVangnettype(int vangnettype) {
		this.vangnettype = vangnettype;
	}

	public int getVerzuimtype() {
		return this.verzuimtype;
	}

	public void setVerzuimtype(int verzuimtype) {
		this.verzuimtype = verzuimtype;
	}

	public Integer getUitkeringnaarwerknemer() {
		return uitkeringnaarwerknemer;
	}

	public void setUitkeringnaarwerknemer(Integer uitkeringnaarwerknemer) {
		this.uitkeringnaarwerknemer = uitkeringnaarwerknemer;
	}

}