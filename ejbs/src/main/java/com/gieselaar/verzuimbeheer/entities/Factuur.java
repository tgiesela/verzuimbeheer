package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the FACTUUR database table.
 * 
 */
@Entity
@NamedQuery(name="Factuur.findAll", query="SELECT f FROM Factuur f")
public class Factuur extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.DATE)
	private Date aanmaakdatum;
	private BigDecimal aansluitkosten;
	private Integer aansluitkostenperiode;
	private Integer aantalmedewerkers;
	private BigDecimal abonnementskosten;
	private Integer abonnementskostenperiode;
	private BigDecimal btwpercentagehoog;
	private BigDecimal btwpercentagelaag;
	private Integer factuurnr;
	private Integer factuurstatus;
	private Integer jaar;
	private Integer maand;
	private BigDecimal maandbedragsecretariaat;
	private String omschrijvingfactuur;
	private String pdflocation;

	@Temporal(TemporalType.DATE)
	private Date peildatumaansluitkosten;

	private Integer werkgeverid;
	private Integer tariefid;
	private Integer holdingid;

	public Factuur() {
	}

	public Date getAanmaakdatum() {
		return aanmaakdatum;
	}

	public void setAanmaakdatum(Date aanmaakdatum) {
		this.aanmaakdatum = aanmaakdatum;
	}

	public BigDecimal getAansluitkosten() {
		return aansluitkosten;
	}

	public void setAansluitkosten(BigDecimal aansluitkosten) {
		this.aansluitkosten = aansluitkosten;
	}

	public Integer getAansluitkostenperiode() {
		return aansluitkostenperiode;
	}

	public void setAansluitkostenperiode(Integer aansluitkostenperiode) {
		this.aansluitkostenperiode = aansluitkostenperiode;
	}

	public Integer getAantalmedewerkers() {
		return aantalmedewerkers;
	}

	public void setAantalmedewerkers(Integer aantalmedewerkers) {
		this.aantalmedewerkers = aantalmedewerkers;
	}

	public BigDecimal getAbonnementskosten() {
		return abonnementskosten;
	}

	public void setAbonnementskosten(BigDecimal abonnementskosten) {
		this.abonnementskosten = abonnementskosten;
	}

	public Integer getAbonnementskostenperiode() {
		return abonnementskostenperiode;
	}

	public void setAbonnementskostenperiode(Integer abonnementskostenperiode) {
		this.abonnementskostenperiode = abonnementskostenperiode;
	}

	public BigDecimal getBtwpercentagehoog() {
		return btwpercentagehoog;
	}

	public void setBtwpercentagehoog(BigDecimal btwpercentagehoog) {
		this.btwpercentagehoog = btwpercentagehoog;
	}

	public BigDecimal getBtwpercentagelaag() {
		return btwpercentagelaag;
	}

	public void setBtwpercentagelaag(BigDecimal btwpercentagelaag) {
		this.btwpercentagelaag = btwpercentagelaag;
	}

	public Integer getFactuurnr() {
		return factuurnr;
	}

	public void setFactuurnr(Integer factuurnr) {
		this.factuurnr = factuurnr;
	}

	public Integer getFactuurstatus() {
		return factuurstatus;
	}

	public void setFactuurstatus(Integer factuurstatus) {
		this.factuurstatus = factuurstatus;
	}

	public Integer getJaar() {
		return jaar;
	}

	public void setJaar(Integer jaar) {
		this.jaar = jaar;
	}

	public Integer getMaand() {
		return maand;
	}

	public void setMaand(Integer maand) {
		this.maand = maand;
	}

	public BigDecimal getMaandbedragsecretariaat() {
		return maandbedragsecretariaat;
	}

	public void setMaandbedragsecretariaat(BigDecimal maandbedragsecretariaat) {
		this.maandbedragsecretariaat = maandbedragsecretariaat;
	}

	public String getOmschrijvingfactuur() {
		return omschrijvingfactuur;
	}

	public void setOmschrijvingfactuur(String omschrijvingfactuur) {
		this.omschrijvingfactuur = omschrijvingfactuur;
	}

	public String getPdflocation() {
		return pdflocation;
	}

	public void setPdflocation(String pdflocation) {
		this.pdflocation = pdflocation;
	}

	public Date getPeildatumaansluitkosten() {
		return peildatumaansluitkosten;
	}

	public void setPeildatumaansluitkosten(Date peildatumaansluitkosten) {
		this.peildatumaansluitkosten = peildatumaansluitkosten;
	}

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public Integer getTariefid() {
		return tariefid;
	}

	public void setTariefid(Integer tariefid) {
		this.tariefid = tariefid;
	}

	public Integer getHoldingid() {
		return holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}


}