package com.gieselaar.verzuimbeheer.services;

import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class RolInfo extends InfoBase {

	private static final long serialVersionUID = 1L;
	private String omschrijving;
	private String rolid;
	private List<ApplicatieFunctieInfo> applicatiefuncties;

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public String getRolid() {
		return rolid;
	}

	public void setRolid(String rolid) {
		this.rolid = rolid;
	}

	public List<ApplicatieFunctieInfo> getApplicatiefuncties() {
		return applicatiefuncties;
	}

	public void setApplicatiefuncties(List<ApplicatieFunctieInfo> applicatiefuncties) {
		this.applicatiefuncties = applicatiefuncties;
	}

	@Override
	public boolean validate() throws ValidationException {
		if (this.omschrijving == null || this.omschrijving.isEmpty())
			throw new ValidationException("Omschrijving niet ingevuld");
		if (this.rolid == null || this.rolid.isEmpty())
			throw new ValidationException("Rol identificatie niet ingevuld");
		return false;
	}

}
