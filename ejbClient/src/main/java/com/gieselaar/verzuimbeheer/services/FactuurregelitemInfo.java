package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;

public class FactuurregelitemInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal bedrag;
	private BigDecimal btwbedrag;
	private BigDecimal btwpercentage;
	private __btwtariefsoort btwcategorie;
	private Integer factuuritemid;
	private Integer factuurid;
	private FactuuritemInfo factuuritem;
	private FactuurcategorieInfo factuurcategorie;
	private FactuurkopInfo factuurkop;
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public BigDecimal getBedrag() {
		return bedrag;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	public BigDecimal getBtwbedrag() {
		return btwbedrag;
	}

	public void setBtwbedrag(BigDecimal btwbedrag) {
		this.btwbedrag = btwbedrag;
	}

	public BigDecimal getBtwpercentage() {
		return btwpercentage;
	}

	public void setBtwpercentage(BigDecimal btwpercentage) {
		this.btwpercentage = btwpercentage;
	}

	public __btwtariefsoort getBtwcategorie() {
		return btwcategorie;
	}

	public void setBtwcategorie(__btwtariefsoort btwcategorie) {
		this.btwcategorie = btwcategorie;
	}

	public Integer getFactuuritemid() {
		return factuuritemid;
	}

	public void setFactuuritemid(Integer factuuritemid) {
		this.factuuritemid = factuuritemid;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

	public FactuuritemInfo getFactuuritem() {
		return factuuritem;
	}

	public void setFactuuritem(FactuuritemInfo factuuritem) {
		this.factuuritem = factuuritem;
	}

	public FactuurcategorieInfo getFactuurcategorie() {
		return factuurcategorie;
	}

	public void setFactuurcategorie(FactuurcategorieInfo factuurcategorie) {
		this.factuurcategorie = factuurcategorie;
	}

	public FactuurkopInfo getFactuurkop() {
		return factuurkop;
	}

	public void setFactuurkop(FactuurkopInfo factuurkop) {
		this.factuurkop = factuurkop;
	}

}
