package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;

public class FactuurcategorieInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private __btwtariefsoort btwcategorie;
	private Integer factuurkopid;
	private boolean isomzet;
	private String omschrijving;
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public __btwtariefsoort getBtwcategorie() {
		return btwcategorie;
	}

	public void setBtwcategorie(__btwtariefsoort btwcategorie) {
		this.btwcategorie = btwcategorie;
	}

	public Integer getFactuurkopid() {
		return factuurkopid;
	}

	public void setFactuurkopid(Integer factuurkopid) {
		this.factuurkopid = factuurkopid;
	}

	public boolean isIsomzet() {
		return isomzet;
	}

	public void setIsomzet(boolean isomzet) {
		this.isomzet = isomzet;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

}
