package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class OeNiveauInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer oeniveau;
	private String naam;
	private Integer parentoeniveauId;
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Integer getOeniveau() {
		return oeniveau;
	}

	public void setOeniveau(Integer oeniveau) {
		this.oeniveau = oeniveau;
	}

	public Integer getParentoeniveauId() {
		return parentoeniveauId;
	}

	public void setParentoeniveauId(Integer parentoeniveauId) {
		this.parentoeniveauId = parentoeniveauId;
	}

}
