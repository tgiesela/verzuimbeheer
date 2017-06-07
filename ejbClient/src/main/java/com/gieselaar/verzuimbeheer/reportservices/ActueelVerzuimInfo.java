package com.gieselaar.verzuimbeheer.reportservices;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;

public class ActueelVerzuimInfo extends InfoBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String achternaam;
	private String voornaam;
	private String afdelingnaam;
	private String burgerservicenummer;
	private Date datumHerstel;
	private int dienstverbandid;
	private Date einddatumcontract;
	private Date einddatumverzuim;
	private Date geboortedatum;
	private __gerelateerdheid gerelateerdheid;
	private int geslacht;
	private Date herstelmeldingsdatum;
	private String herstelopmerkingen;
	private String holdingnaam;
	private Integer afdelingid;
	private BigDecimal percentageHerstel;
	private BigDecimal percentageHerstelAt;
	private Date startdatumcontract;
	private Date startdatumverzuim;
	private Integer verzuimid;
	private Date verzuimmeldingsdatum;
	private String voorletters;
	private String voorvoegsel;
	private Integer werkgeverid;
	private String werkgevernaam;
	private Integer werknemerid;
	private BigDecimal werkweek;
	private Integer holdingid;
	private BigDecimal werkgeverwerkweek;
	private Integer vangnettype;
	private Integer herstelid;
	private Date startperiode;
	private Date eindperiode;
	private BigDecimal verzuimduurinperiode;
	private BigDecimal verzuimherstelduurinperiode;
	private BigDecimal verzuimherstelduurnettoinperiode;
	private BigDecimal verzuimpercentage;
	private String opmerkingen;
	private String cascodeomschrijving;
	private String personeelsnummer;
	private Integer cascode;
	private boolean uitkeringnaarwerknemer;

	@Override
	public boolean validate() throws ValidationException {
		return false;
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

	public Date getDatumHerstel() {
		return datumHerstel;
	}

	public void setDatumHerstel(Date datumHerstel) {
		this.datumHerstel = datumHerstel;
	}

	public Integer getDienstverbandid() {
		return dienstverbandid;
	}

	public void setDienstverbandid(Integer dienstverbandid) {
		this.dienstverbandid = dienstverbandid;
	}

	public Date getEinddatumcontract() {
		return einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public Date getEinddatumverzuim() {
		return einddatumverzuim;
	}

	public void setEinddatumverzuim(Date einddatumverzuim) {
		this.einddatumverzuim = einddatumverzuim;
	}

	public int getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public Date getHerstelmeldingsdatum() {
		return herstelmeldingsdatum;
	}

	public void setHerstelmeldingsdatum(Date herstelmeldingsdatum) {
		this.herstelmeldingsdatum = herstelmeldingsdatum;
	}

	public String getHoldingnaam() {
		return holdingnaam;
	}

	public void setHoldingnaam(String holdingnaam) {
		this.holdingnaam = holdingnaam;
	}

	public BigDecimal getPercentageHerstel() {
		return percentageHerstel;
	}

	public void setPercentageHerstel(BigDecimal percentageHerstel) {
		this.percentageHerstel = percentageHerstel;
	}

	public BigDecimal getPercentageHerstelAt() {
		return percentageHerstelAt;
	}

	public void setPercentageHerstelAt(BigDecimal percentageHerstelAt) {
		this.percentageHerstelAt = percentageHerstelAt;
	}

	public Date getStartdatumcontract() {
		return startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public Date getStartdatumverzuim() {
		return startdatumverzuim;
	}

	public void setStartdatumverzuim(Date startdatumverzuim) {
		this.startdatumverzuim = startdatumverzuim;
	}

	public Integer getVerzuimid() {
		return verzuimid;
	}

	public void setVerzuimid(Integer verzuimid) {
		this.verzuimid = verzuimid;
	}

	public Date getVerzuimmeldingsdatum() {
		return verzuimmeldingsdatum;
	}

	public void setVerzuimmeldingsdatum(Date verzuimmeldingsdatum) {
		this.verzuimmeldingsdatum = verzuimmeldingsdatum;
	}

	public String getVoorletters() {
		return voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public String getWerkgevernaam() {
		return werkgevernaam;
	}

	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}

	public Integer getWerknemerid() {
		return werknemerid;
	}

	public void setWerknemerid(Integer werknemerid) {
		this.werknemerid = werknemerid;
	}

	public BigDecimal getWerkweek() {
		return werkweek;
	}

	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}

	public String getGeslachtafk() {
		return __geslacht.parse(geslacht).toString();
	}

	public Integer getHoldingid() {
		return holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}

	public BigDecimal getWerkgeverwerkweek() {
		return werkgeverwerkweek;
	}

	public void setWerkgeverwerkweek(BigDecimal werkgeverwerkweek) {
		this.werkgeverwerkweek = werkgeverwerkweek;
	}

	public Integer getVangnettype() {
		return vangnettype;
	}

	public void setVangnettype(Integer vangnettype) {
		this.vangnettype = vangnettype;
	}

	public String getVangnettypeomschrijving() {
		return __vangnettype.parse(vangnettype).toString();
	}

	public Integer getHerstelid() {
		return herstelid;
	}

	public void setHerstelid(Integer herstelid) {
		this.herstelid = herstelid;
	}

	public Date getStartperiode() {
		return startperiode;
	}

	public void setStartperiode(Date startperiode) {
		this.startperiode = startperiode;
	}

	public Date getEindperiode() {
		return eindperiode;
	}

	public void setEindperiode(Date eindperiode) {
		this.eindperiode = eindperiode;
	}

	/*
	 * Verzuimduur is duur in dagen vanaf periodebegin tot maximaal
	 * periodeeinde.
	 */
	public BigDecimal getVerzuimduurinperiode() {
		return verzuimduurinperiode;
	}

	public void setVerzuimduurinperiode(BigDecimal verzuimduur) {
		this.verzuimduurinperiode = verzuimduur;
	}

	public BigDecimal getVerzuimherstelduurinperiode() {
		return verzuimherstelduurinperiode;
	}

	public void setVerzuimherstelduurinperiode(
			BigDecimal verzuimherstelduurinperiode) {
		this.verzuimherstelduurinperiode = verzuimherstelduurinperiode;
	}

	public BigDecimal getVerzuimpercentage() {
		return verzuimpercentage;
	}

	public void setVerzuimpercentage(BigDecimal verzuimpercentage) {
		this.verzuimpercentage = verzuimpercentage;
	}

	public BigDecimal getVerzuimherstelduurnettoinperiode() {
		return verzuimherstelduurnettoinperiode;
	}

	public void setVerzuimherstelduurnettoinperiode(
			BigDecimal verzuimherstelduurnettoinperiode) {
		this.verzuimherstelduurnettoinperiode = verzuimherstelduurnettoinperiode;
	}

	public Date getGeboortedatum() {
		return geboortedatum;
	}

	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}

	public __gerelateerdheid getGerelateerdheid() {
		return gerelateerdheid;
	}

	public void setGerelateerdheid(__gerelateerdheid gerelateerdheid) {
		this.gerelateerdheid = gerelateerdheid;
	}
	public String getGerelateerdheidomschrijving() {
		return this.gerelateerdheid.toString();
	}

	public String getHerstelopmerkingen() {
		return herstelopmerkingen;
	}

	public void setHerstelopmerkingen(String herstelopmerkingen) {
		this.herstelopmerkingen = herstelopmerkingen;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	public Integer getAfdelingid() {
		return afdelingid;
	}

	public void setAfdelingid(Integer afdelingid) {
		this.afdelingid = afdelingid;
	}

	public String getCascodeomschrijving() {
		return cascodeomschrijving;
	}

	public void setCascodeomschrijving(String cascodeomschrijving) {
		this.cascodeomschrijving = cascodeomschrijving;
	}

	public String getPersoneelsnummer() {
		return personeelsnummer;
	}

	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}

	public Integer getCascode() {
		return cascode;
	}

	public void setCascode(Integer cascode) {
		this.cascode = cascode;
	}

	public String getOpmerkingen() {
		return opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public boolean isUitkeringnaarwerknemer() {
		return uitkeringnaarwerknemer;
	}

	public void setUitkeringnaarwerknemer(boolean uitkeringnaarwerknemer) {
		this.uitkeringnaarwerknemer = uitkeringnaarwerknemer;
	}

}
