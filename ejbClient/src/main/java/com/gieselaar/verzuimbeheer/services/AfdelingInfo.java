package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class AfdelingInfo extends InfoBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 239320832935877805L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private String afdelingsid;
	private String naam;
	private ContactpersoonInfo contactpersoon;
	private Integer werkgeverId;

	public AfdelingInfo() {
		super();
	}

	public Integer getWerkgeverId() {
		return werkgeverId;
	}

	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}

	public String getAfdelingsid() {
		return afdelingsid;
	}

	public void setAfdelingsid(String afdelingsid) {
		this.afdelingsid = afdelingsid;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public ContactpersoonInfo getContactpersoon() {
		return contactpersoon;
	}

	public void setContactpersoon(ContactpersoonInfo contactpersoon) {
		this.contactpersoon = contactpersoon;
	}

	@Override
	public boolean validate() throws ValidationException {
		if ((this.getNaam() == null) || (this.getNaam().isEmpty()))
			throw new ValidationException("Afdelingnaam niet ingevuld");
		/*
		 * De controle op de invulling van de werkgever id wordt hier niet
		 * meer gedaan. Dat gebeurt in de WerkgeverBean.
		 */
		return false;
	}
}
