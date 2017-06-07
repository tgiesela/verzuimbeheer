package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the GEBRUIKER database table.
 * 
 */
@Entity
public class Gebruiker extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	private int id;

	private String achternaam;

	private int alleklanten;

	private String emailadres;

	private String gebruikersnaam;

	private int inlogfouten;

    @Temporal( TemporalType.TIMESTAMP)
	private Date laatstepoging;

	private String passwordhash;

	private int status;

	private String tussenvoegsel;

	private String voornaam;

	private int aduser;
	
	private String domainname;
	
	//uni-directional many-to-many association to Rol
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="GEBRUIKER_ROL"
		, joinColumns={
			@JoinColumn(name="gebruikerid")
			}
		, inverseJoinColumns={
			@JoinColumn(name="rolid")
			}
		)
	private List<Rol> rols;

	//uni-directional many-to-many association to Werkgever
/*
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="GEBRUIKER_WERKGEVER"
		, joinColumns={
			@JoinColumn(name="gebruiker_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="werkgever_id")
			}
		)
	private List<Werkgever> werkgevers;
*/
    public Gebruiker() {
    }

//	public int getId() {
//		return this.id;
//	}

//	public void setId(int id) {
//		this.id = id;
//	}

	public String getAchternaam() {
		return this.achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public int getAlleklanten() {
		return this.alleklanten;
	}

	public void setAlleklanten(int alleklanten) {
		this.alleklanten = alleklanten;
	}

	public String getEmailadres() {
		return this.emailadres;
	}

	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}

	public String getGebruikersnaam() {
		return this.gebruikersnaam;
	}

	public void setGebruikersnaam(String gebruikersnaam) {
		this.gebruikersnaam = gebruikersnaam;
	}

	public int getInlogfouten() {
		return this.inlogfouten;
	}

	public void setInlogfouten(int inlogfouten) {
		this.inlogfouten = inlogfouten;
	}

	public Date getLaatstepoging() {
		return this.laatstepoging;
	}

	public void setLaatstepoging(Date laatstepoging) {
		this.laatstepoging = laatstepoging;
	}

	public String getPasswordhash() {
		return this.passwordhash;
	}

	public void setPasswordhash(String passwordhash) {
		this.passwordhash = passwordhash;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTussenvoegsel() {
		return this.tussenvoegsel;
	}

	public void setTussenvoegsel(String tussenvoegsel) {
		this.tussenvoegsel = tussenvoegsel;
	}

	public String getVoornaam() {
		return this.voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public List<Rol> getRols() {
		return this.rols;
	}

	public void setRols(List<Rol> rols) {
		this.rols = rols;
	}
/*	
	public List<Werkgever> getWerkgevers() {
		return this.werkgevers;
	}

	public void setWerkgevers(List<Werkgever> werkgevers) {
		this.werkgevers = werkgevers;
	}
*/

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public int getAduser() {
		return aduser;
	}

	public void setAduser(int aduser) {
		this.aduser = aduser;
	}	
}