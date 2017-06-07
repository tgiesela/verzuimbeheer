package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;


/**
 * The persistent class for the FACTUURREGELBEZOEK database table.
 * 
 */
@Entity
@NamedQuery(name="Factuurregelbezoek.findAll", query="SELECT f FROM Factuurregelbezoek f")
public class Factuurregelbezoek extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal casemanagementkosten;
	private Integer factuurid;
	private BigDecimal kilometerkosten;
	private BigDecimal kilometertarief;
	private BigDecimal overigekosten;
	private BigDecimal uurkosten;
	private BigDecimal uurtarief;
	private BigDecimal vastekosten;
	private Integer werkzaamhedenid;

	public Factuurregelbezoek() {
	}

	public BigDecimal getCasemanagementkosten() {
		return casemanagementkosten;
	}

	public void setCasemanagementkosten(BigDecimal casemanagementkosten) {
		this.casemanagementkosten = casemanagementkosten;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

	public BigDecimal getKilometerkosten() {
		return kilometerkosten;
	}

	public void setKilometerkosten(BigDecimal kilometerkosten) {
		this.kilometerkosten = kilometerkosten;
	}

	public BigDecimal getKilometertarief() {
		return kilometertarief;
	}

	public void setKilometertarief(BigDecimal kilometertarief) {
		this.kilometertarief = kilometertarief;
	}

	public BigDecimal getOverigekosten() {
		return overigekosten;
	}

	public void setOverigekosten(BigDecimal overigekosten) {
		this.overigekosten = overigekosten;
	}

	public BigDecimal getUurkosten() {
		return uurkosten;
	}

	public void setUurkosten(BigDecimal uurkosten) {
		this.uurkosten = uurkosten;
	}

	public BigDecimal getUurtarief() {
		return uurtarief;
	}

	public void setUurtarief(BigDecimal uurtarief) {
		this.uurtarief = uurtarief;
	}

	public BigDecimal getVastekosten() {
		return vastekosten;
	}

	public void setVastekosten(BigDecimal vastekosten) {
		this.vastekosten = vastekosten;
	}

	public Integer getWerkzaamhedenid() {
		return werkzaamhedenid;
	}

	public void setWerkzaamhedenid(Integer werkzaamhedenid) {
		this.werkzaamhedenid = werkzaamhedenid;
	}


}