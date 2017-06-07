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
public class FactuurTotaal extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Copy from Factuur
	 */
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
	
	/*
	 * End Copy from factuur
	 */
	
	private BigDecimal somitembedrag;
	private BigDecimal somitembtwbedraglaag;
	private BigDecimal somitembtwbedraghoog;
	private BigDecimal somuurkosten;
	private BigDecimal somvastekosten;
	private BigDecimal somkilometerkosten;
	private BigDecimal somoverigekostensecretariaat;
	private BigDecimal somcasemanagementkosten;
	private BigDecimal somoverigekostenbezoek;
	private BigDecimal somsecretariaatskosten;
	private Integer vasttariefhuisbezoeken;

	public FactuurTotaal() {
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

	public BigDecimal getSomitembedrag() {
		return somitembedrag;
	}

	public void setSomitembedrag(BigDecimal sombedrag) {
		this.somitembedrag = sombedrag;
	}

	public BigDecimal getSomitembtwbedraghoog() {
		return somitembtwbedraghoog;
	}

	public void setSomitembtwbedraghoog(BigDecimal somitembtwbedraghoog) {
		this.somitembtwbedraghoog = somitembtwbedraghoog;
	}

	public BigDecimal getSomitembtwbedraglaag() {
		return somitembtwbedraglaag;
	}

	public void setSomitembtwbedraglaag(BigDecimal somitembtwbedraglaag) {
		this.somitembtwbedraglaag = somitembtwbedraglaag;
	}

	public BigDecimal getSomuurkosten() {
		return somuurkosten;
	}

	public void setSomuurkosten(BigDecimal somuurkosten) {
		this.somuurkosten = somuurkosten;
	}

	public BigDecimal getSomvastekosten() {
		return somvastekosten;
	}

	public void setSomvastekosten(BigDecimal somvastekosten) {
		this.somvastekosten = somvastekosten;
	}

	public BigDecimal getSomkilometerkosten() {
		return somkilometerkosten;
	}

	public void setSomkilometerkosten(BigDecimal somkilometerkosten) {
		this.somkilometerkosten = somkilometerkosten;
	}

	public BigDecimal getSomoverigekostensecretariaat() {
		return somoverigekostensecretariaat;
	}

	public void setSomoverigekostensecretariaat(
			BigDecimal somoverigekostensecretariaat) {
		this.somoverigekostensecretariaat = somoverigekostensecretariaat;
	}

	public BigDecimal getSomcasemanagementkosten() {
		return somcasemanagementkosten;
	}

	public void setSomcasemanagementkosten(BigDecimal somcasemanagementkosten) {
		this.somcasemanagementkosten = somcasemanagementkosten;
	}

	public BigDecimal getSomoverigekostenbezoek() {
		return somoverigekostenbezoek;
	}

	public void setSomoverigekostenbezoek(BigDecimal somoverigekostenbezoek) {
		this.somoverigekostenbezoek = somoverigekostenbezoek;
	}

	public BigDecimal getSomsecretariaatskosten() {
		return somsecretariaatskosten;
	}

	public void setSomsecretariaatskosten(BigDecimal somsecretariaatskosten) {
		this.somsecretariaatskosten = somsecretariaatskosten;
	}

	public Integer getVasttariefhuisbezoeken() {
		return vasttariefhuisbezoeken;
	}

	public void setVasttariefhuisbezoeken(Integer vasttariefhuisbezoeken) {
		this.vasttariefhuisbezoeken = vasttariefhuisbezoeken;
	}

	public Integer getTariefid() {
		return this.tariefid;
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