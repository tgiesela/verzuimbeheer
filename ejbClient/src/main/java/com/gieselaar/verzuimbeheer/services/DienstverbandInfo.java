package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class DienstverbandInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -367576966389515752L;

	private Date einddatumcontract;
	private String functie;
	private String personeelsnummer;
	private Date startdatumcontract;
	private WerknemerInfo werknemer;
	private List<VerzuimInfo> verzuimen;
	private WerkgeverInfo werkgever;
	private Integer werkgeverId;
	private Integer werknemerId;
	private BigDecimal werkweek;
	
	@Override
	public boolean validate() throws ValidationException {
		Calendar firstPossibleDate = Calendar.getInstance();
		firstPossibleDate.set(Calendar.YEAR, 1950);
		firstPossibleDate.set(Calendar.MONTH, 1);
		firstPossibleDate.set(Calendar.DAY_OF_MONTH,1);
		
		if (this.getStartdatumcontract() == null)
			throw new ValidationException("Ingangsdatum niet ingevuld");
		if ((this.getEinddatumcontract() != null) && 
			(this.getEinddatumcontract().before(this.getStartdatumcontract())))
				throw new ValidationException("Einddatum ligt voor ingangsdatum");
		if (this.getStartdatumcontract().before(firstPossibleDate.getTime()))
			throw new ValidationException("Ingangsdatum dienstverband ligt voor 1950!");
		
		if (this.getWerkgeverId() == null || this.getWerkgeverId().intValue() == -1)
			throw new ValidationException("Werkgever niet ingevuld");
		if (this.getWerkweek() == null)
			throw new ValidationException("Werkweek niet ingevuld");
		return false;
	}
	public Date getEinddatumcontract() {
		return einddatumcontract;
	}
	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}
	public String getFunctie() {
		return functie;
	}
	public void setFunctie(String functie) {
		this.functie = functie;
	}
	public String getPersoneelsnummer() {
		return personeelsnummer;
	}
	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}
	public WerknemerInfo getWerknemer() {
		return werknemer;
	}
	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}
	public List<VerzuimInfo> getVerzuimen() {
		return verzuimen;
	}
	public void setVerzuimen(List<VerzuimInfo> verzuimen) {
		this.verzuimen = verzuimen;
	}
	public WerkgeverInfo getWerkgever() {
		return werkgever;
	}
	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
	}
	public Date getStartdatumcontract() {
		return startdatumcontract;
	}
	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}
	public Integer getWerkgeverId() {
		return werkgeverId;
	}
	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}
	public BigDecimal getWerkweek() {
		return werkweek;
	}
	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}
	public Integer getWerknemerId() {
		return werknemerId;
	}
	public void setWerknemerId(Integer werknemerId) {
		this.werknemerId = werknemerId;
	}

}
