package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the VERZUIMHERSTEL database table.
 * 
 */
@Entity
public class Verzuimherstel extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

    @Temporal( TemporalType.DATE)
	@Column(name="datum_herstel")
	private Date datumHerstel;

    @Temporal( TemporalType.DATE)
	private Date meldingsdatum;

	private int meldingswijze;

	private String opmerkingen;

	@Column(name="percentage_herstel")
	private BigDecimal percentageHerstel;

	@Column(name="percentage_herstel_at")
	private BigDecimal percentageHerstelAt;

	private int user;

	@Column(name="verzuim_id")
	private int verzuimId;

    public Verzuimherstel() {
    }

	public Date getDatumHerstel() {
		return this.datumHerstel;
	}

	public void setDatumHerstel(Date datumHerstel) {
		this.datumHerstel = datumHerstel;
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

	public BigDecimal getPercentageHerstel() {
		return this.percentageHerstel;
	}

	public void setPercentageHerstel(BigDecimal percentageHerstel) {
		this.percentageHerstel = percentageHerstel;
	}

	public BigDecimal getPercentageHerstelAt() {
		return this.percentageHerstelAt;
	}

	public void setPercentageHerstelAt(BigDecimal percentageHerstelAt) {
		this.percentageHerstelAt = percentageHerstelAt;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getVerzuimId() {
		return this.verzuimId;
	}

	public void setVerzuimId(int verzuimId) {
		this.verzuimId = verzuimId;
	}

}