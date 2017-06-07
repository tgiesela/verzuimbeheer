package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the VERZUIMMEDISCHEKAART database table.
 * 
 */
@Entity
public class Verzuimmedischekaart extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

    @Lob()
	private String medischekaart;

	private int openbaar;

	private int user;

	private int verzuim_ID;

    @Temporal( TemporalType.DATE)
	private Date wijzigingsdatum;

    public Verzuimmedischekaart() {
    }

	public String getMedischekaart() {
		return this.medischekaart;
	}

	public void setMedischekaart(String medischekaart) {
		this.medischekaart = medischekaart;
	}

	public int getOpenbaar() {
		return this.openbaar;
	}

	public void setOpenbaar(int openbaar) {
		this.openbaar = openbaar;
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

	public Date getWijzigingsdatum() {
		return this.wijzigingsdatum;
	}

	public void setWijzigingsdatum(Date wijzigingsdatum) {
		this.wijzigingsdatum = wijzigingsdatum;
	}

}