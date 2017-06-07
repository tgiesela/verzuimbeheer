package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the WERKNEMER database table.
 * 
 */
@Entity
public class Werknemer extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String achternaam;
	private int arbeidsgehandicapt;
	private int burgerlijkestaat;
	private String burgerservicenummer;
	private String email;
    @Temporal( TemporalType.DATE)
	private Date geboortedatum;
	private int geslacht;
	private String mobiel;
	private String opmerkingen;
	private String telefoon;
	private String telefoonprive;
	private String voorletters;
	private String voornaam;
	private String voorvoegsel;
    @Temporal( TemporalType.DATE)
	private Date wijzigingsdatum;
	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="adres_ID")
	private Adres adres;

	private Integer werkgever_ID;
    public Werknemer() {
    }

	public String getAchternaam() {
		return this.achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public int getArbeidsgehandicapt() {
		return this.arbeidsgehandicapt;
	}

	public void setArbeidsgehandicapt(int arbeidsgehandicapt) {
		this.arbeidsgehandicapt = arbeidsgehandicapt;
	}

	public int getBurgerlijkestaat() {
		return this.burgerlijkestaat;
	}

	public void setBurgerlijkestaat(int burgerlijkestaat) {
		this.burgerlijkestaat = burgerlijkestaat;
	}

	public String getBurgerservicenummer() {
		return this.burgerservicenummer;
	}

	public void setBurgerservicenummer(String burgerservicenummer) {
		this.burgerservicenummer = burgerservicenummer;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getGeboortedatum() {
		return this.geboortedatum;
	}

	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}

	public int getGeslacht() {
		return this.geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public String getMobiel() {
		return this.mobiel;
	}

	public void setMobiel(String mobiel) {
		this.mobiel = mobiel;
	}

	public String getOpmerkingen() {
		return this.opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public String getTelefoon() {
		return this.telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	public String getTelefoonprive() {
		return this.telefoonprive;
	}

	public void setTelefoonprive(String telefoonprive) {
		this.telefoonprive = telefoonprive;
	}

	public String getVoorletters() {
		return this.voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public String getVoornaam() {
		return this.voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getVoorvoegsel() {
		return this.voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}
/*
	public int getWerkgever_ID() {
		return this.werkgever_ID;
	}

	public void setWerkgever_ID(int werkgever_ID) {
		this.werkgever_ID = werkgever_ID;
	}
*/
	public Adres getAdres() {
		return adres;
	}

	public void setAdres(Adres adres) {
		this.adres = adres;
	}

	public Integer getWerkgever_ID() {
		return werkgever_ID;
	}

	public void setWerkgever_ID(Integer werkgever_ID) {
		this.werkgever_ID = werkgever_ID;
	}

	public Date getWijzigingsdatum() {
		return wijzigingsdatum;
	}

	public void setWijzigingsdatum(Date wijzigingsdatum) {
		this.wijzigingsdatum = wijzigingsdatum;
	}

}