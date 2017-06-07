package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TARIEF database table.
 * 
 */
@Entity
public class Tarief extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal aansluitkosten;

	private int aansluitkostenPeriode;

	private BigDecimal abonnement;

	private int abonnementPeriode;

	@Temporal(TemporalType.DATE)
	private Date datumAansluitkosten;

	@Temporal(TemporalType.DATE)
	private Date einddatum;

	private BigDecimal huisbezoekTarief;

	private BigDecimal huisbezoekZaterdagTarief;

	@Temporal(TemporalType.DATE)
	private Date ingangsdatum;

	private BigDecimal kmTarief;

	private BigDecimal maandbedragSecretariaat;

	private String omschrijvingFactuur;

	private BigDecimal secretariaatskosten;

	private BigDecimal sociaalbezoekTarief;

	private BigDecimal spoedbezoekTarief;

	private BigDecimal spoedbezoekZelfdedagTarief;

	private BigDecimal standaardHuisbezoekTarief;

	private BigDecimal telefonischeControleTarief;

	private BigDecimal uurtariefNormaal;

	private BigDecimal uurtariefWeekend;

	private int vasttariefhuisbezoeken;

	private Integer werkgeverId;
	private Integer holdingId;

	public Tarief() {
	}

	public BigDecimal getAansluitkosten() {
		return this.aansluitkosten;
	}

	public void setAansluitkosten(BigDecimal aansluitkosten) {
		this.aansluitkosten = aansluitkosten;
	}

	public int getAansluitkostenPeriode() {
		return this.aansluitkostenPeriode;
	}

	public void setAansluitkostenPeriode(int aansluitkostenPeriode) {
		this.aansluitkostenPeriode = aansluitkostenPeriode;
	}

	public BigDecimal getAbonnement() {
		return this.abonnement;
	}

	public void setAbonnement(BigDecimal abonnement) {
		this.abonnement = abonnement;
	}

	public int getAbonnementPeriode() {
		return this.abonnementPeriode;
	}

	public void setAbonnementPeriode(int abonnementPeriode) {
		this.abonnementPeriode = abonnementPeriode;
	}

	public Date getDatumAansluitkosten() {
		return this.datumAansluitkosten;
	}

	public void setDatumAansluitkosten(Date datumAansluitkosten) {
		this.datumAansluitkosten = datumAansluitkosten;
	}

	public Date getEinddatum() {
		return this.einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public BigDecimal getHuisbezoekTarief() {
		return this.huisbezoekTarief;
	}

	public void setHuisbezoekTarief(BigDecimal huisbezoekTarief) {
		this.huisbezoekTarief = huisbezoekTarief;
	}

	public BigDecimal getHuisbezoekZaterdagTarief() {
		return this.huisbezoekZaterdagTarief;
	}

	public void setHuisbezoekZaterdagTarief(BigDecimal huisbezoekZaterdagTarief) {
		this.huisbezoekZaterdagTarief = huisbezoekZaterdagTarief;
	}

	public Date getIngangsdatum() {
		return this.ingangsdatum;
	}

	public void setIngangsdatum(Date ingangsdatum) {
		this.ingangsdatum = ingangsdatum;
	}

	public BigDecimal getKmTarief() {
		return this.kmTarief;
	}

	public void setKmTarief(BigDecimal kmTarief) {
		this.kmTarief = kmTarief;
	}

	public BigDecimal getMaandbedragSecretariaat() {
		return this.maandbedragSecretariaat;
	}

	public void setMaandbedragSecretariaat(BigDecimal maandbedragSecretariaat) {
		this.maandbedragSecretariaat = maandbedragSecretariaat;
	}

	public String getOmschrijvingFactuur() {
		return this.omschrijvingFactuur;
	}

	public void setOmschrijvingFactuur(String omschrijvingFactuur) {
		this.omschrijvingFactuur = omschrijvingFactuur;
	}

	public BigDecimal getSecretariaatskosten() {
		return this.secretariaatskosten;
	}

	public void setSecretariaatskosten(BigDecimal secretariaatskosten) {
		this.secretariaatskosten = secretariaatskosten;
	}

	public BigDecimal getSociaalbezoekTarief() {
		return this.sociaalbezoekTarief;
	}

	public void setSociaalbezoekTarief(BigDecimal sociaalbezoekTarief) {
		this.sociaalbezoekTarief = sociaalbezoekTarief;
	}

	public BigDecimal getSpoedbezoekTarief() {
		return this.spoedbezoekTarief;
	}

	public void setSpoedbezoekTarief(BigDecimal spoedbezoekTarief) {
		this.spoedbezoekTarief = spoedbezoekTarief;
	}

	public BigDecimal getSpoedbezoekZelfdedagTarief() {
		return this.spoedbezoekZelfdedagTarief;
	}

	public void setSpoedbezoekZelfdedagTarief(BigDecimal spoedbezoekZelfdedagTarief) {
		this.spoedbezoekZelfdedagTarief = spoedbezoekZelfdedagTarief;
	}

	public BigDecimal getStandaardHuisbezoekTarief() {
		return this.standaardHuisbezoekTarief;
	}

	public void setStandaardHuisbezoekTarief(BigDecimal standaardHuisbezoekTarief) {
		this.standaardHuisbezoekTarief = standaardHuisbezoekTarief;
	}

	public BigDecimal getTelefonischeControleTarief() {
		return this.telefonischeControleTarief;
	}

	public void setTelefonischeControleTarief(BigDecimal telefonischeControleTarief) {
		this.telefonischeControleTarief = telefonischeControleTarief;
	}

	public BigDecimal getUurtariefNormaal() {
		return this.uurtariefNormaal;
	}

	public void setUurtariefNormaal(BigDecimal uurtariefNormaal) {
		this.uurtariefNormaal = uurtariefNormaal;
	}

	public BigDecimal getUurtariefWeekend() {
		return this.uurtariefWeekend;
	}

	public void setUurtariefWeekend(BigDecimal uurtariefWeekend) {
		this.uurtariefWeekend = uurtariefWeekend;
	}

	public int getVasttariefhuisbezoeken() {
		return this.vasttariefhuisbezoeken;
	}

	public void setVasttariefhuisbezoeken(int vasttariefhuisbezoeken) {
		this.vasttariefhuisbezoeken = vasttariefhuisbezoeken;
	}

	public Integer getWerkgeverId() {
		return this.werkgeverId;
	}

	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}

	public Integer getHoldingId() {
		return holdingId;
	}

	public void setHoldingId(Integer holdingId) {
		this.holdingId = holdingId;
	}

}