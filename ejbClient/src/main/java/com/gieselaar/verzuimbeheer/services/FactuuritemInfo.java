package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class FactuuritemInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal bedrag;

	private Date datum;
	private int factuurcategorieid;
	private String omschrijving;
	private int userid;
	private Integer werkgeverid;
	private Integer holdingid;
	
	@Override
	public boolean validate() throws ValidationException {
		if (this.datum == null){
			throw new ValidationException("Datum niet ingevuld");
		}
		if (this.factuurcategorieid <= 0){
			throw new ValidationException("Factuurcategorie niet ingevuld");
		}
		if ((this.getWerkgeverid() == null) || (this.getWerkgeverid() <= 0)){
			throw new ValidationException("Werkgever niet ingevuld");
		}
		return false;
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

	public int getFactuurcategorieid() {
		return factuurcategorieid;
	}

	public void setFactuurcategorieid(int factuurcategorieid) {
		this.factuurcategorieid = factuurcategorieid;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public Integer getHoldingid() {
		return holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}

}
