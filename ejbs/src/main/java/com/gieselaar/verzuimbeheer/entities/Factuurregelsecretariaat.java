package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;


/**
 * The persistent class for the FACTUURREGELSECRETARIAAT database table.
 * 
 */
@Entity
@NamedQuery(name="Factuurregelsecretariaat.findAll", query="SELECT f FROM Factuurregelsecretariaat f")
public class Factuurregelsecretariaat extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer factuurid;
	private BigDecimal overigekosten;
	private BigDecimal secretariaatskosten;
	private BigDecimal uurtarief;
	private Integer weeknummer;
	private Integer werkzaamhedenid;

	public Factuurregelsecretariaat() {
	}

	public int getFactuurid() {
		return this.factuurid;
	}

	public void setFactuurid(int factuurid) {
		this.factuurid = factuurid;
	}

	public BigDecimal getOverigekosten() {
		return this.overigekosten;
	}

	public void setOverigekosten(BigDecimal overigekosten) {
		this.overigekosten = overigekosten;
	}

	public BigDecimal getSecretariaatskosten() {
		return this.secretariaatskosten;
	}

	public void setSecretariaatskosten(BigDecimal secretariaatskosten) {
		this.secretariaatskosten = secretariaatskosten;
	}

	public BigDecimal getUurtarief() {
		return this.uurtarief;
	}

	public void setUurtarief(BigDecimal uurtarief) {
		this.uurtarief = uurtarief;
	}

	public int getWeeknummer() {
		return this.weeknummer;
	}

	public void setWeeknummer(int weeknummer) {
		this.weeknummer = weeknummer;
	}

	public int getWerkzaamhedenid() {
		return this.werkzaamhedenid;
	}

	public void setWerkzaamhedenid(int werkzaamhedenid) {
		this.werkzaamhedenid = werkzaamhedenid;
	}

}