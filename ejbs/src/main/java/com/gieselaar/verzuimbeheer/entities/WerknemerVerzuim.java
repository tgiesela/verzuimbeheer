package com.gieselaar.verzuimbeheer.entities;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBase;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OpenVerzuim
 *
 */
@Entity
@Table(name="V_WERKNEMERVERZUIM")
public class WerknemerVerzuim extends EntityBase implements Serializable {

	@EmbeddedId
	private WerknemerVerzuimPK id;
	private static final long serialVersionUID = 1L;

	private int aantalverzuimen;
	private String achternaam;
	private String voornaam;
	private String afdelingnaam;
	private String burgerservicenummer;
	@Temporal(TemporalType.DATE)
	private Date einddatumcontract;
	@Temporal(TemporalType.DATE)
	private Date geboortedatum;
	private int geslacht;
	private int holdingid;
	private String holdingnaam;
	@Temporal(TemporalType.DATE)
	private Date startdatumcontract;
	private String voorletters;
	private String voorvoegsel;
	private String werkgevernaam;
	private BigDecimal werkgeverwerkweek;
	private BigDecimal werkweek;
	
	public WerknemerVerzuim() {
		super();
	}

	public WerknemerVerzuimPK getId() {
		return id;
	}

	public void setId(WerknemerVerzuimPK id) {
		this.id = id;
	}

	public int getAantalverzuimen() {
		return aantalverzuimen;
	}

	public void setAantalverzuimen(int aantalverzuimen) {
		this.aantalverzuimen = aantalverzuimen;
	}

	public String getAchternaam() {
		return achternaam;
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
		return id.getAfdelingid();
	}

	public void setAfdelingid(int afdelingid) {
		this.id.setAfdelingid(afdelingid);
	}

	public String getAfdelingnaam() {
		return afdelingnaam;
	}

	public void setAfdelingnaam(String afdelingnaam) {
		this.afdelingnaam = afdelingnaam;
	}

	public String getBurgerservicenummer() {
		return burgerservicenummer;
	}

	public void setBurgerservicenummer(String burgerservicenummer) {
		this.burgerservicenummer = burgerservicenummer;
	}

	public int getDienstverbandid() {
		return id.getDienstverbandid();
	}

	public void setDienstverbandid(int dienstverbandid) {
		this.id.setDienstverbandid(dienstverbandid);
	}

	public Date getEinddatumcontract() {
		return einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public Date getGeboortedatum() {
		return geboortedatum;
	}

	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}

	public int getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public int getHoldingid() {
		return holdingid;
	}

	public void setHoldingid(int holdingid) {
		this.holdingid = holdingid;
	}

	public String getHoldingnaam() {
		return holdingnaam;
	}

	public void setHoldingnaam(String holdingnaam) {
		this.holdingnaam = holdingnaam;
	}

	public Date getStartdatumcontract() {
		return startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public String getVoorletters() {
		return voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	public int getWerkgeverid() {
		return id.getWerkgeverid();
	}

	public void setWerkgeverid(int werkgeverid) {
		this.id.setWerkgeverid(werkgeverid);
	}

	public String getWerkgevernaam() {
		return werkgevernaam;
	}

	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}

	public BigDecimal getWerkgeverwerkweek() {
		return werkgeverwerkweek;
	}

	public void setWerkgeverwerkweek(BigDecimal werkgeverwerkweek) {
		this.werkgeverwerkweek = werkgeverwerkweek;
	}

	public int getWerknemerid() {
		return id.getWerknemerid();
	}

	public void setWerknemerid(int werknemerid) {
		this.id.setWerknemerid(werknemerid);
	}

	public BigDecimal getWerkweek() {
		return werkweek;
	}

	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}   
   
}
