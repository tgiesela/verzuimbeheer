package com.gieselaar.verzuimbeheer.reportservices;

import java.io.Serializable;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;

import java.math.BigDecimal;
import java.util.Date;

public class WerknemerVerzuimInfo extends InfoBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int aantalverzuimen;
	private String achternaam;
	private String voornaam;
	private int afdelingid;
	private String afdelingnaam;
	private String burgerservicenummer;
	private int dienstverbandid;
	private Date einddatumcontract;
	private Date geboortedatum;
	private int geslacht;
	private int holdingid;
	private String holdingnaam;
	private Date startdatumcontract;
	private String voorletters;
	private String voorvoegsel;
	private int werkgeverid;
	private String werkgevernaam;
	private BigDecimal werkgeverwerkweek;
	private int werknemerid;
	private BigDecimal werkweek;

	public String getAchternaam() {
		return this.achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public int getAfdelingid() {
		return this.afdelingid;
	}

	public void setAfdelingid(int afdelingid) {
		this.afdelingid = afdelingid;
	}

	public String getAfdelingnaam() {
		return this.afdelingnaam;
	}

	public void setAfdelingnaam(String afdelingnaam) {
		this.afdelingnaam = afdelingnaam;
	}

	public String getBurgerservicenummer() {
		return this.burgerservicenummer;
	}

	public void setBurgerservicenummer(String burgerservicenummer) {
		this.burgerservicenummer = burgerservicenummer;
	}

	public int getDienstverbandid() {
		return this.dienstverbandid;
	}

	public void setDienstverbandid(int dienstverbandid) {
		this.dienstverbandid = dienstverbandid;
	}

	public Date getEinddatumcontract() {
		return this.einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public int getGeslacht() {
		return this.geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public int getHoldingid() {
		return this.holdingid;
	}

	public void setHoldingid(int holdingid) {
		this.holdingid = holdingid;
	}

	public String getHoldingnaam() {
		return this.holdingnaam;
	}

	public void setHoldingnaam(String holdingnaam) {
		this.holdingnaam = holdingnaam;
	}

	public Date getStartdatumcontract() {
		return this.startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public String getVoorletters() {
		return this.voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public int getWerkgeverid() {
		return this.werkgeverid;
	}

	public void setWerkgeverid(int werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public String getWerkgevernaam() {
		return this.werkgevernaam;
	}

	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}

	public BigDecimal getWerkgeverwerkweek() {
		return this.werkgeverwerkweek;
	}

	public void setWerkgeverwerkweek(BigDecimal werkgeverwerkweek) {
		this.werkgeverwerkweek = werkgeverwerkweek;
	}

	public int getWerknemerid() {
		return this.werknemerid;
	}

	public void setWerknemerid(int werknemerid) {
		this.werknemerid = werknemerid;
	}

	public BigDecimal getWerkweek() {
		return this.werkweek;
	}

	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}

	public int getAantalverzuimen() {
		return aantalverzuimen;
	}

	public void setAantalverzuimen(int aantalverzuimen) {
		this.aantalverzuimen = aantalverzuimen;
	}

	public Date getGeboortedatum() {
		return geboortedatum;
	}

	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

}
