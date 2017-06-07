 package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class GebruikerInfo extends InfoBase{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public enum __status
	{
		INACTIVE(1),
		ACTIVE(2),
		BLOCKED(3);
		
		private int value;
		__status(int value){
			this.value = value;
		}
		public int getValue() { return value; }

        public static __status parse(int id) {
        	__status status = null; // Default
            for (__status item : __status.values()) {
                if (item.getValue()==id) {
                    status = item;
                    break;
                }
            }
            return status;
        }
	}
	String 		name;
	__status	status;
	private ArrayList<RolInfo> rollen = null;
	private String achternaam;
	private String emailadres;
	private int inlogfouten;
	private Date laatstepoging;
	private String passwordhash;
	private String tussenvoegsel;
	private String voornaam;
	private String newPassword;
	private boolean aduser;
	private String domainname;
	private List<GebruikerWerkgeverInfo> werkgevers;
	private boolean alleklanten;

	public GebruikerInfo() {
		super();
		inlogfouten = 0;
		name = "";
		status = __status.INACTIVE;
		newPassword = null;
	}
	public int getInlogfouten() {
		return inlogfouten;
	}
	public void setInlogfouten(int inlogfouten) {
		this.inlogfouten = inlogfouten;
	}
	public Date getLaatstepoging() {
		return laatstepoging;
	}
	public void setLaatstepoging(Date laatstepoging) {
		this.laatstepoging = laatstepoging;
	}
	public String getPasswordhash() {
		return passwordhash;
	}
	public void setPasswordhash(String passwordhash) {
		this.passwordhash = passwordhash;
	}
	public String getTussenvoegsel() {
		return tussenvoegsel;
	}
	public void setTussenvoegsel(String tussenvoegsel) {
		this.tussenvoegsel = tussenvoegsel;
	}
	public String getVoornaam() {
		return voornaam;
	}
	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}
	public List<RolInfo> getRollen() {
		return rollen;
	}
	public void setRollen(List<RolInfo> rollen) {
		this.rollen = (ArrayList<RolInfo>)rollen;
	}
	public void setStatus(__status status) {
		this.status = status;
	}
	public __status getStatus() {
		return status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.getAchternaam() == null)
			throw new ValidationException("Achternaam is niet ingevuld");
		if (this.getAchternaam().isEmpty())
			throw new ValidationException("Achternaam is niet ingevuld");
		if (this.getEmailadres() == null)
			throw new ValidationException("Emailadres is niet ingevuld");
		if (this.getEmailadres().isEmpty())
			throw new ValidationException("Emailadres is niet ingevuld");
		if (this.getName() == null)
			throw new ValidationException("Gebruikersnaam is niet ingevuld");
		if (this.getName().isEmpty())
			throw new ValidationException("Gebruikersnaam is niet ingevuld");
		if ((this.aduser) && (this.getDomainname().isEmpty()))
			throw new ValidationException("Domainname niet ingevuld voor AD user");
		return false;
	}
	public String getAchternaam() {
		return achternaam;
	}
	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}
	public String getEmailadres() {
		return emailadres;
	}
	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public List<GebruikerWerkgeverInfo> getWerkgevers() {
		return werkgevers;
	}
	public void setWerkgevers(List<GebruikerWerkgeverInfo> werkgevers) {
		this.werkgevers = werkgevers;
	}
	public boolean isAlleklanten() {
		return alleklanten;
	}
	public void setAlleklanten(boolean alleklanten) {
		this.alleklanten = alleklanten;
	}
	public boolean isAduser() {
		return aduser;
	}
	public void setAduser(boolean aduser) {
		this.aduser = aduser;
	}
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
}
