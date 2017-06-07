package com.gieselaar.verzuimbeheer.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class WerknemerFast {
   	@Id Integer id;
   	private String Achternaam;
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
    @Temporal( TemporalType.DATE)
	private Date einddatumcontract;
    @Temporal( TemporalType.DATE)
	private Date startdatumcontract;
    public BigDecimal getWerkweek() {
		return werkweek;
	}
	private String werkgevernaam;
	private Integer werkgever_ID;
	private BigDecimal werkweek;
	private String personeelsnummer;
	private String afdelingnaam;
	private Long version;
	Integer vzmcnt;
   	Integer openvzm;
	public Integer getId() {
		return id;
	}
	public String getAchternaam() {
		return Achternaam;
	}
	public int getArbeidsgehandicapt() {
		return arbeidsgehandicapt;
	}
	public int getBurgerlijkestaat() {
		return burgerlijkestaat;
	}
	public String getBurgerservicenummer() {
		return burgerservicenummer;
	}
	public String getEmail() {
		return email;
	}
	public Date getGeboortedatum() {
		return geboortedatum;
	}
	public int getGeslacht() {
		return geslacht;
	}
	public String getMobiel() {
		return mobiel;
	}
	public String getOpmerkingen() {
		return opmerkingen;
	}
	public String getTelefoon() {
		return telefoon;
	}
	public String getTelefoonprive() {
		return telefoonprive;
	}
	public String getVoorletters() {
		return voorletters;
	}
	public String getVoornaam() {
		return voornaam;
	}
	public String getVoorvoegsel() {
		return voorvoegsel;
	}
	public Date getWijzigingsdatum() {
		return wijzigingsdatum;
	}
	public Integer getWerkgever_ID() {
		return werkgever_ID;
	}
	public Date getEinddatumcontract() {
		return einddatumcontract;
	}
	public Date getStartdatumcontract() {
		return startdatumcontract;
	}
	public String getWerkgevernaam() {
		return werkgevernaam;
	}
	public Integer getVzmcnt() {
		return vzmcnt;
	}
	public Integer getOpenvzm() {
		return openvzm;
	}
	public String getPersoneelsnummer() {
		return personeelsnummer;
	}
	public Long getVersion() {
		return version;
	}
	public String getAfdelingnaam() {
		return afdelingnaam;
	}
	public void setAfdelingnaam(String afdelingnaam) {
		this.afdelingnaam = afdelingnaam;
	}
}
