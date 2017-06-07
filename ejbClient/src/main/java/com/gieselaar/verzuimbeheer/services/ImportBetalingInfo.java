package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class ImportBetalingInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date datumbetaling;
	private BigDecimal bedrag;
	private String rekeningnummerBetaler;
	private String bedrijfsnaam;
	private String omschrijving1;
	private String omschrijving2;
	private String omschrijving3;
	private Integer factuurnummer;
	private Integer werkgeverid;
	private Integer holdingid;
	private Integer factuurid;
	private BigDecimal factuurbedrag;
	private FactuurTotaalInfo factuur;
	private __checkresult checkresult = __checkresult.OK;
	private boolean accepted = false;
	
	public Date getDatumbetaling() {
		return datumbetaling;
	}

	public void setDatumbetaling(String datumbetaling) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		this.datumbetaling = df.parse(datumbetaling);
	}

	public void setDatumbetaling(Date datumbetaling){
		this.datumbetaling = datumbetaling;
	}
	public BigDecimal getBedrag() {
		return bedrag;
	}

	public void setBedrag(String bedrag) throws ParseException {
		DecimalFormat df = new DecimalFormat("##0,00");
		df.setParseBigDecimal(true);
		this.bedrag = (BigDecimal)df.parse(bedrag.replaceAll("\\.", ","));
	}

	public void setBedrag(BigDecimal bedrag) throws ParseException {
		this.bedrag = bedrag;
	}
	public String getRekeningnummerBetaler() {
		return rekeningnummerBetaler;
	}

	public void setRekeningnummerBetaler(String rekeningnummerBetaler) {
		this.rekeningnummerBetaler = rekeningnummerBetaler;
	}

	public String getBedrijfsnaam() {
		return bedrijfsnaam;
	}

	public void setBedrijfsnaam(String bedrijfsnaam) {
		this.bedrijfsnaam = bedrijfsnaam;
	}

	public String getOmschrijving1() {
		return omschrijving1;
	}

	public void setOmschrijving1(String omschrijving1) {
		this.omschrijving1 = omschrijving1;
	}

	public String getOmschrijving2() {
		return omschrijving2;
	}

	public void setOmschrijving2(String omschrijving2) {
		this.omschrijving2 = omschrijving2;
	}

	public String getOmschrijving3() {
		return omschrijving3;
	}

	public void setOmschrijving3(String omschrijving3) {
		this.omschrijving3 = omschrijving3;
	}

	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public Integer getFactuurnummer() {
		return factuurnummer;
	}

	public void setFactuurnummer(Integer factuurnummer) {
		this.factuurnummer = factuurnummer;
	}

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

	public BigDecimal getFactuurbedrag() {
		return factuurbedrag;
	}

	public void setFactuurbedrag(BigDecimal factuurbedrag) {
		this.factuurbedrag = factuurbedrag;
	}

	public Integer getHoldingid() {
		return holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}

	public FactuurTotaalInfo getFactuur() {
		return factuur;
	}

	public void setFactuur(FactuurTotaalInfo factuur) {
		this.factuur = factuur;
	}
	
	public __checkresult getCheckresult() {
		return checkresult;
	}

	public void setCheckresult(__checkresult checkresult) {
		this.checkresult = checkresult;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public enum __checkresult {
		NOTSET(0) {
			@Override
			public String toString() {
				return "Ongedefinieerd";
			}
		}, 
		HOLDINGMISMATCH(1) {
			@Override
			public String toString() {
				return "Holding hoort niet bij factuur!";
			}
		},
		WERKGEVERMISMATCH(2) {
			@Override
			public String toString() {
				return "Werkgever hoort niet bij factuur!";
			}
		},
		NOMATCHINGFACTUUR(3) {
			@Override
			public String toString() {
				return "Factuur niet gevonden!";
			}
		},
		AMOUNTMISMATCH(4) {
			@Override
			public String toString() {
				return "Bedragen zijn verschillend.";
			}
		},
		OK(5) {
			@Override
			public String toString() {
				return "";
			}
		},
		ALREADYPAID(6) {
			@Override
			public String toString() {
				return "Factuur is reeds betaald.";
			}
		}, 
		DOUBLEPAYMENT(7){
			@Override
			public String toString() {
				return "Dubbele betaling?";
			}
		};

		private Integer value;

		__checkresult(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __checkresult parse(Integer type) {
			__checkresult checkresult = null; // Default
			for (__checkresult item : __checkresult.values()) {
				if (item.getValue().equals(type)) {
					checkresult = item;
					break;
				}
			}
			return checkresult;
		}
	}

}
