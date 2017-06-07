package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TariefInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal aansluitkosten;
	private __tariefperiode aansluitkostenPeriode;
	private BigDecimal abonnement;
	private __tariefperiode abonnementPeriode;
	private Date datumAansluitkosten;
	private Date einddatum;
	private BigDecimal huisbezoekTarief;
	private BigDecimal huisbezoekZaterdagTarief;
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
	private boolean vasttariefhuisbezoeken;
	private Integer werkgeverId;
	private Integer holdingId;
	private String werkgevernaam;
	
	public TariefInfo(){
		aansluitkosten                   = new BigDecimal(0);
		abonnement                       = new BigDecimal(0);
		huisbezoekTarief                 = new BigDecimal(0);
		huisbezoekZaterdagTarief         = new BigDecimal(0);
		kmTarief                         = new BigDecimal(0);
		maandbedragSecretariaat          = new BigDecimal(0);
		secretariaatskosten              = new BigDecimal(0);
		sociaalbezoekTarief              = new BigDecimal(0);
		spoedbezoekTarief                = new BigDecimal(0);
		spoedbezoekZelfdedagTarief       = new BigDecimal(0);
		standaardHuisbezoekTarief        = new BigDecimal(0);
		telefonischeControleTarief       = new BigDecimal(0);
		uurtariefNormaal                 = new BigDecimal(0);
		uurtariefWeekend                 = new BigDecimal(0);
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.getHoldingId() == null && this.getWerkgeverId() == null)
			throw new ValidationException("Zowel werkgever als holding zijn niet ingevuld!");
		if ((this.getHoldingId() != null) &&
			(this.getHoldingId().equals(-1)))
			throw new ValidationException("Holding niet ingevuld!");
		if ((this.getWerkgeverId() == null ||
			 this.getWerkgeverId().equals(-1)) && this.getHoldingId() == null)
			throw new ValidationException("Werkgever niet ingevuld!");
		if (this.getHoldingId() != null && this.getWerkgeverId() != null)
			throw new ValidationException("Zowel werkgever als holding zijn ingevuld!");
		if (this.getAansluitkostenPeriode() == null){
			throw new ValidationException("Aansluitkostenperiode afwezig");
		}
		if (this.getAbonnementPeriode() == null){
			throw new ValidationException("Abonnementperiode afwezig");
		}
		if (this.getIngangsdatum() == null){
			throw new ValidationException("Ingangsdatum niet ingevuld");
		}
		return false;
	}

	public BigDecimal getAansluitkosten() {
		return aansluitkosten;
	}

	public void setAansluitkosten(BigDecimal aansluitkosten) {
		this.aansluitkosten = aansluitkosten;
	}

	public __tariefperiode getAansluitkostenPeriode() {
		return aansluitkostenPeriode;
	}

	public void setAansluitkostenPeriode(__tariefperiode aansluitkostenPeriode) {
		this.aansluitkostenPeriode = aansluitkostenPeriode;
	}

	public BigDecimal getAbonnement() {
		return abonnement;
	}

	public void setAbonnement(BigDecimal abonnement) {
		this.abonnement = abonnement;
	}

	public __tariefperiode getAbonnementPeriode() {
		return abonnementPeriode;
	}

	public void setAbonnementPeriode(__tariefperiode abonnementPeriode) {
		this.abonnementPeriode = abonnementPeriode;
	}

	public Date getDatumAansluitkosten() {
		return datumAansluitkosten;
	}

	public void setDatumAansluitkosten(Date datumAansluitkosten) {
		this.datumAansluitkosten = datumAansluitkosten;
	}

	public Date getEinddatum() {
		return einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public BigDecimal getHuisbezoekTarief() {
		return huisbezoekTarief;
	}

	public void setHuisbezoekTarief(BigDecimal huisbezoekTarief) {
		this.huisbezoekTarief = huisbezoekTarief;
	}

	public BigDecimal getHuisbezoekZaterdagTarief() {
		return huisbezoekZaterdagTarief;
	}

	public void setHuisbezoekZaterdagTarief(BigDecimal huisbezoekZaterdagTarief) {
		this.huisbezoekZaterdagTarief = huisbezoekZaterdagTarief;
	}

	public Date getIngangsdatum() {
		return ingangsdatum;
	}

	public void setIngangsdatum(Date ingangsdatum) {
		this.ingangsdatum = ingangsdatum;
	}

	public BigDecimal getKmTarief() {
		return kmTarief;
	}

	public void setKmTarief(BigDecimal kmTarief) {
		this.kmTarief = kmTarief;
	}

	public BigDecimal getMaandbedragSecretariaat() {
		return maandbedragSecretariaat;
	}

	public void setMaandbedragSecretariaat(BigDecimal maandbedragSecretariaat) {
		this.maandbedragSecretariaat = maandbedragSecretariaat;
	}

	public String getOmschrijvingFactuur() {
		return omschrijvingFactuur;
	}

	public void setOmschrijvingFactuur(String omschrijvingFactuur) {
		this.omschrijvingFactuur = omschrijvingFactuur;
	}

	public BigDecimal getSecretariaatskosten() {
		return secretariaatskosten;
	}

	public void setSecretariaatskosten(BigDecimal secretariaatskosten) {
		this.secretariaatskosten = secretariaatskosten;
	}

	public BigDecimal getSociaalbezoekTarief() {
		return sociaalbezoekTarief;
	}

	public void setSociaalbezoekTarief(BigDecimal sociaalbezoekTarief) {
		this.sociaalbezoekTarief = sociaalbezoekTarief;
	}

	public BigDecimal getSpoedbezoekTarief() {
		return spoedbezoekTarief;
	}

	public void setSpoedbezoekTarief(BigDecimal spoedbezoekTarief) {
		this.spoedbezoekTarief = spoedbezoekTarief;
	}

	public BigDecimal getSpoedbezoekZelfdedagTarief() {
		return spoedbezoekZelfdedagTarief;
	}

	public void setSpoedbezoekZelfdedagTarief(BigDecimal spoedbezoekZelfdedagTarief) {
		this.spoedbezoekZelfdedagTarief = spoedbezoekZelfdedagTarief;
	}

	public BigDecimal getStandaardHuisbezoekTarief() {
		return standaardHuisbezoekTarief;
	}

	public void setStandaardHuisbezoekTarief(BigDecimal standaardHuisbezoekTarief) {
		this.standaardHuisbezoekTarief = standaardHuisbezoekTarief;
	}

	public BigDecimal getTelefonischeControleTarief() {
		return telefonischeControleTarief;
	}

	public void setTelefonischeControleTarief(BigDecimal telefonischeControleTarief) {
		this.telefonischeControleTarief = telefonischeControleTarief;
	}

	public BigDecimal getUurtariefNormaal() {
		return uurtariefNormaal;
	}

	public void setUurtariefNormaal(BigDecimal uurtariefNormaal) {
		this.uurtariefNormaal = uurtariefNormaal;
	}

	public BigDecimal getUurtariefWeekend() {
		return uurtariefWeekend;
	}

	public void setUurtariefWeekend(BigDecimal uurtariefWeekend) {
		this.uurtariefWeekend = uurtariefWeekend;
	}

	public boolean getVasttariefhuisbezoeken() {
		return vasttariefhuisbezoeken;
	}

	public void setVasttariefhuisbezoeken(boolean vasttariefhuisbezoeken) {
		this.vasttariefhuisbezoeken = vasttariefhuisbezoeken;
	}

	public Integer getWerkgeverId() {
		return werkgeverId;
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

	public String getWerkgevernaam() {
		return werkgevernaam;
	}

	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}

	public enum __tariefperiode {
		MAAND(1) {
			@Override
			public String toString() {
				return "Maand";
			}
		},
		JAAR(2) {
			@Override
			public String toString() {
				return "Jaar";
			}
		};

		private Integer value;

		__tariefperiode(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __tariefperiode parse(Integer type) {
			__tariefperiode periode = null; // Default
			for (__tariefperiode item : __tariefperiode.values()) {
				if (item.getValue().intValue() == type.intValue()) {
					periode = item;
					break;
				}
			}
			return periode;
		}
	}
}
