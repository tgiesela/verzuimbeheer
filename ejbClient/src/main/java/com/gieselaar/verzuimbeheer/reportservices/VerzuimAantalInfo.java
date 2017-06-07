package com.gieselaar.verzuimbeheer.reportservices;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;

public class VerzuimAantalInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer aantalverzuimen;
	private Integer werknemerid;
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}
	public Integer getAantalverzuimen() {
		return aantalverzuimen;
	}
	public void setAantalverzuimen(Integer aantalverzuimen) {
		this.aantalverzuimen = aantalverzuimen;
	}
	public Integer getWerknemerid() {
		return werknemerid;
	}
	public void setWerknemerid(Integer werknemerid) {
		this.werknemerid = werknemerid;
	}

}
