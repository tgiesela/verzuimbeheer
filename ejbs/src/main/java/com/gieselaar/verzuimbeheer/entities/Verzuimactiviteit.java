package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the VERZUIMACTIVITEIT database table.
 * 
 */
@Entity
public class Verzuimactiviteit extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private int activiteit_ID;

    @Temporal( TemporalType.TIMESTAMP)
	private Date datumactiviteit;

    @Temporal( TemporalType.DATE)
	private Date datumdeadline;

	private String opmerkingen;

	private int tijdbesteed;

	private int user;

	private int verzuim_ID;

    public Verzuimactiviteit() {
    }

	public int getActiviteit_ID() {
		return this.activiteit_ID;
	}

	public void setActiviteit_ID(int activiteit_ID) {
		this.activiteit_ID = activiteit_ID;
	}

	public Date getDatumactiviteit() {
		return this.datumactiviteit;
	}

	public void setDatumactiviteit(Date datumactiviteit) {
		this.datumactiviteit = datumactiviteit;
	}

	public Date getDatumdeadline() {
		return this.datumdeadline;
	}

	public void setDatumdeadline(Date datumdeadline) {
		this.datumdeadline = datumdeadline;
	}

	public String getOpmerkingen() {
		return this.opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public int getTijdbesteed() {
		return this.tijdbesteed;
	}

	public void setTijdbesteed(int tijdbesteed) {
		this.tijdbesteed = tijdbesteed;
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

}