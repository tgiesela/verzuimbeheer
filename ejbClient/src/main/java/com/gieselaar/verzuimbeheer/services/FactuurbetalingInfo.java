package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class FactuurbetalingInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer factuurid;
	private Date datum;
	private BigDecimal bedrag;
	private String rekeningnummerbetaler;
	
	@Override
	public boolean validate() throws ValidationException {
		if (this.datum == null){
			throw new ValidationException("Datum niet ingevuld");
		}
		if (this.factuurid == null){
			throw new ValidationException("FactuurId niet ingevuld");
		}
		if (this.bedrag == null){
			throw new ValidationException("Bedrag niet ingevuld");
		}
		if (this.bedrag.compareTo(BigDecimal.ZERO) == 0){
			throw new ValidationException("Bedrag niet ingevuld(=0)");
		}
		return false;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}
	public BigDecimal getBedrag() {
		return bedrag;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public String getRekeningnummerbetaler() {
		return rekeningnummerbetaler;
	}

	public void setRekeningnummerbetaler(String rekeningnummerbetaler) {
		this.rekeningnummerbetaler = rekeningnummerbetaler;
	}


}
