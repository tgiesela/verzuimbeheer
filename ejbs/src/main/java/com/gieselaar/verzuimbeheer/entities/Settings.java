package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the AFDELING database table.
 * 
 */
@Entity
public class Settings extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

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
	
    public Settings() {
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

	
}