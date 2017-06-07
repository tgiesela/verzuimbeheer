package com.gieselaar.verzuimbeheer.entities;

import com.gieselaar.verzuimbeheer.entities.VActueelVerzuimPK;
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
@Table(name="V_ACTUEELVERZUIM")
public class VActueelverzuim extends EntityBase implements Serializable {

	@EmbeddedId
	private VActueelVerzuimPK id;
	private static final long serialVersionUID = 1L;
	private String achternaam;
	private String voornaam;
	private String afdelingnaam;
	private String burgerservicenummer;

	@Temporal(TemporalType.DATE)
	@Column(name="datum_herstel")
	private Date datumHerstel;

	private int dienstverbandid;

	@Temporal(TemporalType.DATE)
	private Date einddatumcontract;

	@Temporal(TemporalType.DATE)
	private Date einddatumverzuim;
	private int geslacht;

	@Temporal(TemporalType.DATE)
	private Date herstelmeldingsdatum;

	private String herstelopmerkingen;
	
	private String holdingnaam;
	private Integer holdingid;

	private int afdelingid;

	@Column(name="percentage_herstel")
	private BigDecimal percentageHerstel;

	@Column(name="percentage_herstel_at")
	private BigDecimal percentageHerstelAt;

	@Temporal(TemporalType.DATE)
	private Date startdatumcontract;

	@Temporal(TemporalType.DATE)
	private Date startdatumverzuim;

	private int verzuimid;

	@Temporal(TemporalType.DATE)
	private Date verzuimmeldingsdatum;

	@Temporal(TemporalType.DATE)
	private Date geboortedatum;

	private String voorletters;
	
	private String voorvoegsel;

	private int werkgeverid;

	private String werkgevernaam;

	private int werknemerid;

	private BigDecimal werkweek;

	private Integer vangnettype;
	private Integer gerelateerdheid;
	private BigDecimal werkgeverwerkweek;
	private String opmerkingen;
	private String cascodeomschrijving;
	private String personeelsnummer;
	private Integer cascode;
	
	private int herstelid;
	
	private int uitkeringnaarwerknemer;

	public VActueelverzuim() {
		super();
	}   
	public VActueelVerzuimPK getId() {
		return this.id;
	}

	public void setId(VActueelVerzuimPK OpenVerzuimPK) {
		this.id = OpenVerzuimPK;
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
	public int getDienstverbandid() {
		return dienstverbandid;
	}
	public void setDienstverbandid(int dienstverbandid) {
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
	public Integer getHoldingid() {
		return holdingid;
	}
	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}
	public int getAfdelingid() {
		return afdelingid;
	}
	public void setAfdelingid(int afdelingid) {
		this.afdelingid = afdelingid;
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
	public int getVerzuimid() {
		return verzuimid;
	}
	public void setVerzuimid(int verzuimid) {
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
	public int getWerkgeverid() {
		return werkgeverid;
	}
	public void setWerkgeverid(int werkgeverid) {
		this.werkgeverid = werkgeverid;
	}
	public String getWerkgevernaam() {
		return werkgevernaam;
	}
	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}
	public int getWerknemerid() {
		return werknemerid;
	}
	public void setWerknemerid(int werknemerid) {
		this.werknemerid = werknemerid;
	}
	public BigDecimal getWerkweek() {
		return werkweek;
	}
	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}
	public Integer getVangnettype() {
		return vangnettype;
	}
	public void setVangnettype(Integer vangnettype) {
		this.vangnettype = vangnettype;
	}
	public BigDecimal getWerkgeverwerkweek() {
		return werkgeverwerkweek;
	}
	public void setWerkgeverwerkweek(BigDecimal werkgeverwerkweek) {
		this.werkgeverwerkweek = werkgeverwerkweek;
	}
	public int getHerstelid() {
		return herstelid;
	}
	public void setHerstelid(int herstelid) {
		this.herstelid = herstelid;
	}
	public String getVoorvoegsel() {
		return voorvoegsel;
	}
	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}
	public String getHerstelopmerkingen() {
		return herstelopmerkingen;
	}
	public void setHerstelopmerkingen(String herstelopmerkingen) {
		this.herstelopmerkingen = herstelopmerkingen;
	}
	public Date getGeboortedatum() {
		return geboortedatum;
	}
	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}
	public Integer getGerelateerdheid() {
		return gerelateerdheid;
	}
	public void setGerelateerdheid(Integer gerelateerdheid) {
		this.gerelateerdheid = gerelateerdheid;
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
	public int getUitkeringnaarwerknemer() {
		return uitkeringnaarwerknemer;
	}
	public void setUitkeringnaarwerknemer(int uitkeringnaarwerknemer) {
		this.uitkeringnaarwerknemer = uitkeringnaarwerknemer;
	}
   
}
