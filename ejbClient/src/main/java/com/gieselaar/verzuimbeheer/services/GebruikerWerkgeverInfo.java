package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class GebruikerWerkgeverInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int gebruikerid;
	private int werkgeverid;
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public int getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(int werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public int getGebruikerid() {
		return gebruikerid;
	}

	public void setGebruikerid(int gebruikerid) {
		this.gebruikerid = gebruikerid;
	}

}
