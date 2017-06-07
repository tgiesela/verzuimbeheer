package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the verzuimTODO database table.
 * 
 */
@Entity
public class Todo extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private int activiteit_ID;

    @Temporal( TemporalType.DATE)
	private Date deadlinedatum;

	private int herhalen;

	private String opmerking;

	private int soort;

	private int user;

	private int verzuim_ID;

	private Integer verzuimactiviteit_ID;

    @Temporal( TemporalType.DATE)
	private Date waarschuwingsdatum;
    @Temporal( TemporalType.DATE)
	private Date aanmaakdatum;

    public Todo() {
    }

	public int getActiviteit_ID() {
		return this.activiteit_ID;
	}

	public void setActiviteit_ID(int activiteit_ID) {
		this.activiteit_ID = activiteit_ID;
	}

	public Date getDeadlinedatum() {
		return this.deadlinedatum;
	}

	public void setDeadlinedatum(Date deadlinedatum) {
		this.deadlinedatum = deadlinedatum;
	}

	public int getHerhalen() {
		return this.herhalen;
	}

	public void setHerhalen(int herhalen) {
		this.herhalen = herhalen;
	}

	public String getOpmerking() {
		return this.opmerking;
	}

	public void setOpmerking(String opmerking) {
		this.opmerking = opmerking;
	}

	public int getSoort() {
		return this.soort;
	}

	public void setSoort(int soort) {
		this.soort = soort;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getVerzuim_ID() {
		return this.verzuim_ID;
	}

	public void setVerzuim_ID(int verzuim_ID) {
		this.verzuim_ID = verzuim_ID;
	}

	public Integer getVerzuimactiviteit_ID() {
		return this.verzuimactiviteit_ID;
	}

	public void setVerzuimactiviteit_ID(Integer verzuimactiviteit_ID) {
		this.verzuimactiviteit_ID = verzuimactiviteit_ID;
	}

	public Date getWaarschuwingsdatum() {
		return this.waarschuwingsdatum;
	}

	public void setWaarschuwingsdatum(Date waarschuwingsdatum) {
		this.waarschuwingsdatum = waarschuwingsdatum;
	}

	public Date getAanmaakdatum() {
		return aanmaakdatum;
	}

	public void setAanmaakdatum(Date aanmaakdatum) {
		this.aanmaakdatum = aanmaakdatum;
	}

}