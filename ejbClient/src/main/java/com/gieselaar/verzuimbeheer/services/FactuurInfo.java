package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;

public class FactuurInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date aanmaakdatum;
	private BigDecimal aansluitkosten;
	private __tariefperiode aansluitkostenperiode;
	private Integer aantalmedewerkers;
	private BigDecimal abonnementskosten;
	private __tariefperiode abonnementskostenperiode;
	private BigDecimal btwpercentagehoog;
	private BigDecimal btwpercentagelaag;
	private Integer factuurnr;
	private __factuurstatus factuurstatus;
	private Integer jaar;
	private Integer maand;
	private BigDecimal maandbedragsecretariaat;
	private String omschrijvingfactuur;
	private String pdflocation;
	private Date peildatumaansluitkosten;
	private Integer werkgeverid;
	private List<FactuurregelbezoekInfo> factuurregelbezoeken;
	private List<FactuurregelitemInfo> factuurregelitems;
	private List<FactuurregelsecretariaatInfo> factuurregelsecretariaat;
	private List<FactuurbetalingInfo> factuurbetalingen;
	private Integer tariefid;
	private Integer holdingid;
	
	public FactuurInfo(){
		aansluitkosten          = BigDecimal.ZERO;
		abonnementskosten       = BigDecimal.ZERO;
		btwpercentagehoog       = BigDecimal.ZERO;
		btwpercentagelaag       = BigDecimal.ZERO;
		maandbedragsecretariaat = BigDecimal.ZERO;
		aantalmedewerkers		= 0;
	}
	@Override
	public boolean validate() throws ValidationException {
		return false;
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

	public __tariefperiode getAansluitkostenperiode() {
		return aansluitkostenperiode;
	}

	public void setAansluitkostenperiode(__tariefperiode aansluitkostenperiode) {
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

	public __tariefperiode getAbonnementskostenperiode() {
		return abonnementskostenperiode;
	}

	public void setAbonnementskostenperiode(__tariefperiode abonnementskostenperiode) {
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

	public __factuurstatus getFactuurstatus() {
		return factuurstatus;
	}

	public void setFactuurstatus(__factuurstatus factuurstatus) {
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

	public List<FactuurregelbezoekInfo> getFactuurregelbezoeken() {
		return factuurregelbezoeken;
	}

	public void setFactuurregelbezoeken(
			List<FactuurregelbezoekInfo> factuurregelbezoeken) {
		this.factuurregelbezoeken = factuurregelbezoeken;
	}

	public List<FactuurregelitemInfo> getFactuurregelitems() {
		return factuurregelitems;
	}

	public void setFactuurregelitems(List<FactuurregelitemInfo> factuurregelitems) {
		this.factuurregelitems = factuurregelitems;
	}

	public List<FactuurregelsecretariaatInfo> getFactuurregelsecretariaat() {
		return factuurregelsecretariaat;
	}

	public void setFactuurregelsecretariaat(
			List<FactuurregelsecretariaatInfo> factuurregelsecretariaat) {
		this.factuurregelsecretariaat = factuurregelsecretariaat;
	}
	
	public List<FactuurbetalingInfo> getFactuurbetalingen() {
		return factuurbetalingen;
	}
	public void setFactuurbetalingen(List<FactuurbetalingInfo> factuurbetalingen) {
		this.factuurbetalingen = factuurbetalingen;
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

	public enum __factuurstatus
	{
		AANGEMAAKT(1) {@Override public String toString(){return "aangemaakt";}},
		VERZONDEN(2) {@Override public String toString(){return "verzonden";}},
		VERWIJDERD(3) {@Override public String toString(){return "verwijderd";}};
		
		private Integer value;
		__factuurstatus(Integer value){
			this.value = value;
		}
		public Integer getValue() { return value; }

        public static __factuurstatus parse(Integer id) {
        	__factuurstatus soort = null; // Default
            for (__factuurstatus item : __factuurstatus.values()) {
                if (item.getValue().intValue() == id.intValue()) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
}
