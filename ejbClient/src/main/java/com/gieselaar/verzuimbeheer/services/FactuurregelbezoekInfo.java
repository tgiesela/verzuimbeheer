package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class FactuurregelbezoekInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal casemanagementkosten;
	private Integer factuurid;
	private BigDecimal kilometerkosten;
	private BigDecimal kilometertarief;
	private BigDecimal overigekosten;
	private BigDecimal uurkosten;
	private BigDecimal uurtarief;
	private BigDecimal vastekosten;
	private Integer werkzaamhedenid;
	private WerkzaamhedenInfo werkzaamheden;
	private OeInfo oe;
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public BigDecimal getCasemanagementkosten() {
		return casemanagementkosten;
	}

	public void setCasemanagementkosten(BigDecimal casemanagementkosten) {
		this.casemanagementkosten = casemanagementkosten;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

	public BigDecimal getKilometerkosten() {
		return kilometerkosten;
	}

	public void setKilometerkosten(BigDecimal kilometerkosten) {
		this.kilometerkosten = kilometerkosten;
	}

	public BigDecimal getKilometertarief() {
		return kilometertarief;
	}

	public void setKilometertarief(BigDecimal kilometertarief) {
		this.kilometertarief = kilometertarief;
	}

	public BigDecimal getOverigekosten() {
		return overigekosten;
	}

	public void setOverigekosten(BigDecimal overigekosten) {
		this.overigekosten = overigekosten;
	}

	public BigDecimal getUurkosten() {
		return uurkosten;
	}

	public void setUurkosten(BigDecimal uurkosten) {
		this.uurkosten = uurkosten;
	}

	public BigDecimal getUurtarief() {
		return uurtarief;
	}

	public void setUurtarief(BigDecimal uurtarief) {
		this.uurtarief = uurtarief;
	}

	public BigDecimal getVastekosten() {
		return vastekosten;
	}

	public void setVastekosten(BigDecimal vastekosten) {
		this.vastekosten = vastekosten;
	}

	public Integer getWerkzaamhedenid() {
		return werkzaamhedenid;
	}

	public void setWerkzaamhedenid(Integer werkzaamhedenid) {
		this.werkzaamhedenid = werkzaamhedenid;
	}

	public WerkzaamhedenInfo getWerkzaamheden() {
		return werkzaamheden;
	}

	public void setWerkzaamheden(WerkzaamhedenInfo werkzaamheden) {
		this.werkzaamheden = werkzaamheden;
	}

	public OeInfo getOe() {
		return oe;
	}

	public void setOe(OeInfo oe) {
		this.oe = oe;
	}

}
