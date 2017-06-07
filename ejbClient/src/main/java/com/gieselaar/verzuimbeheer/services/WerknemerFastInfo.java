package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
public class WerknemerFastInfo extends InfoBase{

	/**
	 * 
	 */
	public enum __sortcol {
		ACHTERNAAM,
		BSN,
		WERKGEVERNAAM;
	}
	private static final long serialVersionUID = 6060027018647906958L;
	private String achternaam;
	private __burgerlijkestaat burgerlijkestaat;
	private String burgerservicenummer;
	private String email;
	private Date geboortedatum;
	private __geslacht geslacht;
	private String mobiel;
	private String telefoonWerk;
	private String voornaam;
	private String voorletters;
	private String voorvoegsel;
	private Integer werkgeverid;
	private WerkgeverInfo werkgever;
	private AdresInfo adres;
	private String telefoonPrive;
	private String opmerkingen;
	private boolean arbeidsgehandicapt;
	private BigDecimal werkweek;
	private Date einddatumcontract;
	private Date startdatumcontract;
	private String werkgevernaam;
	private String personeelsnummer;
	private String afdelingnaam;
	Integer vzmcnt;
   	Integer openvzm;
	/*
	 * variables that will be initialized after getting the details
	 */
	private boolean detailsPresent = false;
	
	public Date getEinddatumcontract() {
		return einddatumcontract;
	}
	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public String getWerkgevernaam() {
		return werkgevernaam;
	}

	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}

	public Integer getVzmcnt() {
		return vzmcnt;
	}

	public void setVzmcnt(Integer vzmcnt) {
		this.vzmcnt = vzmcnt;
	}
	public Integer getOpenvzm() {
		if (vzmcnt > 0){
			return openvzm;
		}else{
			return 0;
		}
	}
	public void setOpenvzm(Integer openvzm) {
		this.openvzm = openvzm;
	}
	public String getTelefoonWerk() {
		return telefoonWerk;
	}

	public void setTelefoonWerk(String telefoonWerk) {
		this.telefoonWerk = telefoonWerk;
	}

	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public __burgerlijkestaat getBurgerlijkestaat() {
		return burgerlijkestaat;
	}

	public void setBurgerlijkestaat(__burgerlijkestaat burgerlijkestaat) {
		this.burgerlijkestaat = burgerlijkestaat;
	}

	public String getBurgerservicenummer() {
		return burgerservicenummer;
	}

	public void setBurgerservicenummer(String burgerservicenummer) {
		this.burgerservicenummer = burgerservicenummer;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getGeboortedatum() {
		return geboortedatum;
	}

	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}

	public __geslacht getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(__geslacht geslacht) {
		this.geslacht = geslacht;
	}

	public String getMobiel() {
		return mobiel;
	}

	public void setMobiel(String mobiel) {
		this.mobiel = mobiel;
	}

	public String getTelefoon() {
		return telefoonWerk;
	}

	public void setTelefoon(String telefoon) {
		this.telefoonWerk = telefoon;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public AdresInfo getAdres() {
		return adres;
	}

	public void setAdres(AdresInfo adres) {
		this.adres = adres;
	}

	public String getTelefoonPrive() {
		return telefoonPrive;
	}

	public void setTelefoonPrive(String telefoonPrive) {
		this.telefoonPrive = telefoonPrive;
	}

	public String getOpmerkingen() {
		return opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public boolean isArbeidsgehandicapt() {
		return arbeidsgehandicapt;
	}

	public void setArbeidsgehandicapt(boolean arbeidsgehandicapt) {
		this.arbeidsgehandicapt = arbeidsgehandicapt;
	}

	public BigDecimal getWerkweek() {
		return werkweek;
	}

	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}

	public WerkgeverInfo getWerkgever() {
		return werkgever;
	}

	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
	}

	public String getVoorletters() {
		return voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public boolean isDetailsPresent() {
		return detailsPresent;
	}

	public void setDetailsPresent(boolean detailsPresent) {
		this.detailsPresent = detailsPresent;
	}

	public boolean isActief() {
		boolean actief = false;
		if (this.getEinddatumcontract() == null || this.getEinddatumcontract().after(new Date()))
			actief = true;
		return actief;
	}

	public DienstverbandInfo getActiefDienstverband() {
		return null;
	}
	public DienstverbandInfo getLaatsteDienstverband() {
		return null;
	}
	public boolean hasOpenVerzuim() {
		if ((this.getVzmcnt() > 0) && (this.getOpenvzm() > 0))
			return true;
		return false;
	}
	public Date getStartdatumcontract() {
		return startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}
	public String getPersoneelsnummer() {
		return personeelsnummer;
	}
	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}
	public String getAfdelingnaam() {
		return afdelingnaam;
	}
	public void setAfdelingnaam(String afdelingnaam) {
		this.afdelingnaam = afdelingnaam;
	}
	public static class Werknemercompare implements Comparator<WerknemerFastInfo> {

		__sortcol sortcol;

		public Werknemercompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(WerknemerFastInfo o1, WerknemerFastInfo o2) {
			switch (sortcol) {
			case ACHTERNAAM:
				return o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam());
			case BSN:
				return o1.getBurgerservicenummer().compareToIgnoreCase(o2.getBurgerservicenummer());
			case WERKGEVERNAAM:
				return compareWerkgevernaam(o1,o2);
			default:
				throw new VerzuimRuntimeException(
						"Unknown sortcol in WerknemerInfo comparator");
			}
		}

		private int compareWerkgevernaam(WerknemerFastInfo o1, WerknemerFastInfo o2) {
			int i;
			i = o1.getWerkgevernaam().compareToIgnoreCase(o2.getWerkgevernaam());
			if (i != 0) 
				return i;
			
			i = o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam());
			if (i != 0) 
				return i;

			return i;
		}
	}
	public static void sort(List<WerknemerFastInfo> werknemers, __sortcol col){
		Collections.sort(werknemers, new Werknemercompare(col));
	}
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}
}
