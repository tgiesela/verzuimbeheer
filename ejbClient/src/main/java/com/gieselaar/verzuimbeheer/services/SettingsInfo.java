package com.gieselaar.verzuimbeheer.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class SettingsInfo extends InfoBase {
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
	protected static final Logger log = Logger.getLogger(SettingsInfo.class);
	private String smtpmailfromaddress;
	private String smtpmailhost;
	private String smtpmailuser;
	private String smtpmailpassword;
	private String factuurfolder; 
	private Integer todoforinformatiekaart;
	private String factuurmailtextbody;
	private String bccemailaddress;
	private Integer todoforafsluitendienstverband;
	private String servershare;
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public SettingsInfo() {
		super();
	}

	public String getSmtpmailfromaddress() {
		return smtpmailfromaddress;
	}

	public void setSmtpmailfromaddress(String smtpmailfromaddress) {
		this.smtpmailfromaddress = smtpmailfromaddress;
	}

	public String getSmtpmailhost() {
		return smtpmailhost;
	}

	public void setSmtpmailhost(String smtpmailhost) {
		this.smtpmailhost = smtpmailhost;
	}

	public String getSmtpmailuser() {
		return smtpmailuser;
	}

	public void setSmtpmailuser(String smtpmailuser) {
		this.smtpmailuser = smtpmailuser;
	}

	public String getSmtpmailpassword() {
		return smtpmailpassword;
	}

	public void setSmtpmailpassword(String smtpmailpassword) {
		this.smtpmailpassword = smtpmailpassword;
	}

	public String getFactuurfolder() {
		return factuurfolder;
	}

	public void setFactuurfolder(String factuurfolder) {
		this.factuurfolder = factuurfolder;
	}

	public Integer getTodoforinformatiekaart() {
		return todoforinformatiekaart;
	}

	public void setTodoforinformatiekaart(Integer todoforinformatiekaart) {
		this.todoforinformatiekaart = todoforinformatiekaart;
	}

	public String getFactuurmailtextbody() {
		return factuurmailtextbody;
	}

	public void setFactuurmailtextbody(String factuurmailtextbody) {
		this.factuurmailtextbody = factuurmailtextbody;
	}

	public String getBccemailaddress() {
		return bccemailaddress;
	}

	public void setBccemailaddress(String bccemailaddress) {
		this.bccemailaddress = bccemailaddress;
	}

	public Integer getTodoforafsluitendienstverband() {
		return todoforafsluitendienstverband;
	}

	public void setTodoforafsluitendienstverband(Integer todoforafsluitendienstverband) {
		this.todoforafsluitendienstverband = todoforafsluitendienstverband;
	}
	public String getServershare() {
		return servershare;
	}
	public void setServershare(String servershare){
		this.servershare = servershare;
	}
	
	@Override
	public boolean validate() throws ValidationException {
		if (this.bccemailaddress == null || this.bccemailaddress.isEmpty()){
			/* Nothing to do */
		}else{
			Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(this.bccemailaddress);
			if (!matcher.find()){	
				throw new ValidationException("Ongeldig bccemailaddress");
			}
		}
		if (this.smtpmailfromaddress == null || this.smtpmailfromaddress.isEmpty()){
			/* Nothing to do */
		}else{
			Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(this.smtpmailfromaddress);
			if (!matcher.find()){	
				throw new ValidationException("Ongeldig smtpmailfromaddress");
			}
		}
		return false;
	}

}
