package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
public class WerknemerInfo extends InfoBase{

	/**
	 * 
	 */
	public enum __sortcol {
		ACHTERNAAM,
		BSN;
	}
	/*
	 * Error messages (duplicates)
	 */
	private static final String ERRDIENSTVERBANDONTBREEKT = "Dienstverband informatie ontbreekt";
	private static final String ERRBSNONTBREEKT = "Burgerservicenummer niet ingevuld";
	private static final String ERRAFDELINGONTBREEKT = "Afdeling niet ingevuld";
	private static final long serialVersionUID = 6060027018647906958L;
	private String achternaam;
	private __burgerlijkestaat burgerlijkestaat;
	private String burgerservicenummer;
	private String email;
	private Date geboortedatum;
	private __geslacht geslacht;
	private String mobiel;
	private String telefoon;
	private String voornaam;
	private String voorletters;
	private String voorvoegsel;
	private Integer werkgeverid;
	private WerkgeverInfo werkgever;
	private List<DienstverbandInfo> dienstVerbanden;
	private AdresInfo adres;
	private String telefoonPrive;
	private String opmerkingen;
	private boolean arbeidsgehandicapt;
	private BigDecimal werkweek;
	/*
	 * variables that will be initialized after getting the details
	 */
	private boolean detailsPresent = false;
	private ArrayList<AfdelingHasWerknemerInfo> afdelingen = null;
	private ArrayList<WiapercentageInfo> wiaPercentages = null;
	
	public List<AfdelingHasWerknemerInfo> getAfdelingen() {
		return afdelingen;
	}

	public void setAfdelingen(List<AfdelingHasWerknemerInfo> afdelingen) {
		this.afdelingen = (ArrayList<AfdelingHasWerknemerInfo>)afdelingen;
	}

	public List<DienstverbandInfo> getDienstVerbanden() {
		return dienstVerbanden;
	}

	public void setDienstVerbanden(List<DienstverbandInfo> dienstVerbanden) {
		this.dienstVerbanden = dienstVerbanden;
	}
	public static void validateBSN(String burgerservicenummer) throws ValidationException{
		Integer digit;
		Integer som;
		Integer remainder;
		String bsn = burgerservicenummer.trim();
		try{
			if (bsn.length() != 9)
				throw new ValidationException("Burgerservicenummer moet 9 lang zijn");
			if (!bsn.matches("[0-9]+"))
				throw new ValidationException("Burgerservicenummer niet numeriek");
			som = 0;
			for (int i=0; i<8;i++)
			{
				digit = Integer.parseInt(bsn.substring(i,i+1));
				som = som + digit * (9-i);
			}
			digit = Integer.parseInt(bsn.substring(8,8+1));
			som = som - digit;
			remainder = som % 11;
			if (remainder != 0)
				throw new ValidationException("Burgerservicenummer niet geldig");
		}
		catch (NumberFormatException ne){
			throw new ValidationException("Burgerservicenummer niet numeriek");
		}
	}
	private void checkRequiredFields() throws ValidationException{
		if (this.burgerservicenummer == null || this.burgerservicenummer.isEmpty())
			throw new ValidationException(ERRBSNONTBREEKT);
		if (this.getAchternaam() == null || this.achternaam.isEmpty())
			throw new ValidationException("Naam mag niet leeg zijn");
		if (this.geboortedatum == null){
			throw new ValidationException("Geboortedatum niet ingevuld");
		}
		if (this.dienstVerbanden == null || this.dienstVerbanden.isEmpty())
			throw new ValidationException(ERRDIENSTVERBANDONTBREEKT);
		if (this.afdelingen == null || this.afdelingen.isEmpty())
			throw new ValidationException(ERRAFDELINGONTBREEKT);
		if (this.werkgeverid == null || this.werkgeverid.intValue() == -1)
			throw new ValidationException("Logic error: Werkgever is null");
	}
	private void checkGeboortedatum() throws ValidationException{
		/*
		 * Controleer of geboortedatum niet voor de arbitraire datum 1-1-1915 ligt.
		 */
		Calendar firstDate = Calendar.getInstance();
		firstDate.set(Calendar.YEAR,1915);
		firstDate.set(Calendar.MONTH,1);
		firstDate.set(Calendar.DAY_OF_MONTH,1);
		if (this.geboortedatum.before(firstDate.getTime())){
			throw new ValidationException("Geboortedatum ligt te ver in het verleden");
		}
	}
	private void checkActieveDienstverbanden() throws ValidationException{
		/* 
		 * Controle dienstverbanden. Er moet minimaal een dienstverband zijn
		 * en er mag er slechts 1 actief zijn.
		 * 
		 */
		int aantalactief = 0;
		for (DienstverbandInfo dv: this.dienstVerbanden)
		{
			if (dv.getAction() == persistenceaction.DELETE){
				continue;
			}
			if (dv.getStartdatumcontract().after(new Date())){
				/* Startdatum ligt in de toekomst */
			}else{
				if ((dv.getEinddatumcontract() == null ) ||
					(dv.getEinddatumcontract().after(new Date())))
					aantalactief++;
			}
		}
		if (aantalactief > 1)
			throw new ValidationException("meer dan 1 actief dienstverband");
	}
	private void validateDienstverbanden() throws ValidationException{
		Collections.sort(dienstVerbanden, new Dienstverbandcompare());
		DienstverbandInfo vorigdienstverband = dienstVerbanden.get(0);
		DienstverbandInfo volgenddienstverband;
		int i = 1;
		while (i < dienstVerbanden.size()){
			volgenddienstverband = dienstVerbanden.get(i);
			if (volgenddienstverband.getAction() != persistenceaction.DELETE && vorigdienstverband.getAction() != persistenceaction.DELETE){
				if (vorigdienstverband.getEinddatumcontract() == null)
					throw new ValidationException("Oude niet afgesloten dienstverbanden gevonden");
				if (DateOnly.after(volgenddienstverband.getStartdatumcontract(), vorigdienstverband.getEinddatumcontract())){
					/* ok */
				} else {
					throw new ValidationException("Overlappende dienstverbanden");
				}
			}
			i++;
			vorigdienstverband = volgenddienstverband;
		}

	}
	private void checkActieveAfdelingen() throws ValidationException{
		int aantalactief = 0;
		int aantalafdelingen = 0;
		for (AfdelingHasWerknemerInfo ahwi: this.afdelingen)
		{
			if (ahwi.getAction() != persistenceaction.DELETE){
				aantalafdelingen++;
				if (ahwi.getEinddatum() == null)
					aantalactief++;
			}
		}
		if (aantalactief > 1)
			throw new ValidationException("Meer dan 1 niet afgesloten afdeling");
		if (aantalafdelingen == 0)
			throw new ValidationException("Geen afdelingen gevonden bij werknemer");
		
	}
	private void validateAfdelingen() throws ValidationException{
		Collections.sort(afdelingen, new Afdelingcompare());
		if (afdelingen.isEmpty())
			return;

		AfdelingHasWerknemerInfo vorigeafdeling = this.afdelingen.get(0);
		AfdelingHasWerknemerInfo volgendeafdeling;
		int i = 1;
		while (i < afdelingen.size()){
			volgendeafdeling = this.afdelingen.get(i);
			if (volgendeafdeling.getAction() != persistenceaction.DELETE && vorigeafdeling.getAction() != persistenceaction.DELETE){
				if (vorigeafdeling.getEinddatum() == null)
					throw new ValidationException("Oude niet afgesloten afdeling gevonden");
				if (DateOnly.after(volgendeafdeling.getStartdatum(), vorigeafdeling.getEinddatum())){
					/* ok */
				} else {
					throw new ValidationException("Overlappende afdelingen");
				}
			}
			i++;
			vorigeafdeling = volgendeafdeling;
		}
	}
	private void checkActieveAfdelingExists() throws ValidationException{
		/*
		 * er moet ook een afdeling zijn met einddatum null
		 */
		boolean afdfound = false;
		for (AfdelingHasWerknemerInfo afwi:this.getAfdelingen()){
			if (afwi.getAction() == persistenceaction.DELETE){
				/* ok */
			}else{
				if (afwi.getEinddatum() == null){
					afdfound = true;
				}
			}
		}
		if (!afdfound){
			throw new ValidationException("Geen huidige afdeling gevonden bij werknemer");
		}
	}
	private void checkActieveWiaPercentages()throws ValidationException{
		int aantalactief = 0;
		int aantal = 0;
		for (WiapercentageInfo wpi: this.wiaPercentages)
		{
			if (wpi.getAction() != persistenceaction.DELETE){
				aantal++;
				if (wpi.getEinddatum() == null){
					aantalactief++;
				}
			}
		}
		if (aantalactief > 1)
			throw new ValidationException("Meer dan 1 wiaperiodes zonder einddatum");
		if (aantal == 0)
			throw new ValidationException("Geen wiaperiodes gevonden bij werknemer");
	}
	private void checkOverlapWiaPercentages() throws ValidationException{
		Collections.sort(wiaPercentages, new Wiacompare());
		if (wiaPercentages.isEmpty())
			throw new ValidationException("Wia informatie ontbreekt");
		
		WiapercentageInfo vorigwiapercentage = wiaPercentages.get(0);
		WiapercentageInfo volgendwiapercentage;
		int i = 1;
		while (i < wiaPercentages.size()){
			volgendwiapercentage = wiaPercentages.get(i);
			if (volgendwiapercentage.getAction() != persistenceaction.DELETE && vorigwiapercentage.getAction() != persistenceaction.DELETE){
				if (vorigwiapercentage.getEinddatum() == null)
					throw new ValidationException("Oudere niet afgesloten wia periode gevonden");
				if (DateOnly.after(volgendwiapercentage.getStartdatum(), vorigwiapercentage.getEinddatum())){
					/* ok */
				} else{
					throw new ValidationException("Overlappende wiaperiodes");
				}
			}
			i++;
			vorigwiapercentage = volgendwiapercentage;
		}
	}

	@Override
	public boolean validate() throws ValidationException {
		checkRequiredFields();

		validateBSN(this.burgerservicenummer);

		if (this.geslacht != __geslacht.MAN && this.geslacht != __geslacht.VROUW)
			throw new ValidationException("geslacht moet MAN of VROUW zijn");

		checkGeboortedatum();

		checkActieveDienstverbanden();
		
		validateDienstverbanden();

		checkActieveAfdelingen();

		validateAfdelingen();
		
		if (this.getLaatsteDienstverband().getEinddatumcontract() == null){
			checkActieveAfdelingExists();
		}

		if (this.wiaPercentages != null)
		{
			checkActieveWiaPercentages();
			
			checkOverlapWiaPercentages();
		}
		else
			throw new ValidationException("Wia informatie ontbreekt");
		
		return false;
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
		return telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
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

	public List<WiapercentageInfo> getWiaPercentages() {
		return wiaPercentages;
	}

	public void setWiaPercentages(List<WiapercentageInfo> wiaPercentages) {
		this.wiaPercentages = (ArrayList<WiapercentageInfo>)wiaPercentages;
	}

	public boolean isDetailsPresent() {
		return detailsPresent;
	}

	public void setDetailsPresent(boolean detailsPresent) {
		this.detailsPresent = detailsPresent;
	}

	public boolean isActief() {
		boolean actief;
		actief = false;
		if (this.getDienstVerbanden() != null){
			for (DienstverbandInfo di: this.getDienstVerbanden()){
				if ((di.getEinddatumcontract() == null) ||
					(!di.getEinddatumcontract().before(new Date())))
					actief = true;
			}
		}
		return actief;
	}

	public DienstverbandInfo getActiefDienstverband() {
		if (this.getDienstVerbanden() != null){
			for (DienstverbandInfo d : this.getDienstVerbanden())
			{
				if ((d.getEinddatumcontract() == null) ||
					(d.getEinddatumcontract().after(new Date())))
					return d;
			}
		}
		return null;
	}
	public DienstverbandInfo getLaatsteDienstverband() {
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 1, 1);
		Date startdate = cal.getTime();
		int lastone = -1;
			
		if (this.getDienstVerbanden() != null){
			for (int i=0;i<this.getDienstVerbanden().size();i++)
			{
				if (this.getDienstVerbanden().get(i).getStartdatumcontract().after(startdate)){
					startdate = this.getDienstVerbanden().get(i).getStartdatumcontract();
					lastone = i;
				}
			}
		}
		if (lastone >= 0)
			return this.getDienstVerbanden().get(lastone);
		else
			return null;
	}
	public WiapercentageInfo getLaatsteWiapercentage() {
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 1, 1);
		Date startdate = cal.getTime();
		int lastone = -1;
			
		if (this.getWiaPercentages() != null){
			for (int i=0;i<this.getWiaPercentages().size();i++)
			{
				if (this.getWiaPercentages().get(i).getStartdatum().after(startdate)){
					startdate = this.getWiaPercentages().get(i).getStartdatum();
					lastone = i;
				}
			}
		}
		if (lastone >= 0)
			return this.getWiaPercentages().get(lastone);
		else
			return null;
	}
	public AfdelingHasWerknemerInfo getLaatsteAfdeling() {
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 1, 1);
		Date startdate = cal.getTime();
		int lastone = -1;
			
		if (this.getAfdelingen() == null)
			return null;
		for (int i=0;i<this.getAfdelingen().size();i++){
			if ((this.getAfdelingen().get(i).getAction() != persistenceaction.DELETE) &&
				(this.getAfdelingen().get(i).getStartdatum().after(startdate))){
				startdate = this.getAfdelingen().get(i).getStartdatum();
				lastone = i;
			}
		}
		if (lastone >= 0)
			return this.getAfdelingen().get(lastone);
		else
			return null;
	}
	public boolean hasOpenVerzuim(DienstverbandInfo d) {
		DienstverbandInfo dvb;
		if (d == null)
			dvb = this.getActiefDienstverband();
		else
			dvb = d;
		if (dvb.getVerzuimen() != null)
		{
			for (VerzuimInfo vzm: dvb.getVerzuimen()){
				if (vzm.getEinddatumverzuim() == null)
					return true;
			}
			return false;
		}
		else	
			return false;
	}
	public static class Werknemercompare implements Comparator<WerknemerInfo> {

		__sortcol sortcol;

		public Werknemercompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(WerknemerInfo o1, WerknemerInfo o2) {
			switch (sortcol) {
			case ACHTERNAAM:
				return o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam());
			case BSN:
				return o1.getBurgerservicenummer().compareToIgnoreCase(o2.getBurgerservicenummer());
			default:
				throw new VerzuimRuntimeException(
						"Unknown sortcol in WerknemerInfo comparator");
			}

		}
	}
	public static class Dienstverbandcompare implements Comparator<DienstverbandInfo>{
		@Override
		public int compare(DienstverbandInfo o1, DienstverbandInfo o2) {
			if (o1.getStartdatumcontract().before(o2.getStartdatumcontract()))
				return -1;
			else
				if (o1.getStartdatumcontract().after(o2.getStartdatumcontract()))
					return 1;
				else
					return 0;

		}
	}
	public static class Afdelingcompare implements Comparator<AfdelingHasWerknemerInfo>{
		@Override
		public int compare(AfdelingHasWerknemerInfo o1, AfdelingHasWerknemerInfo o2) {
			if (o1.getStartdatum().before(o2.getStartdatum()))
				return -1;
			else
				if (o1.getStartdatum().after(o2.getStartdatum()))
					return 1;
				else
					return 0;

		}
	}
	public static class Wiacompare implements Comparator<WiapercentageInfo>{
		@Override
		public int compare(WiapercentageInfo o1, WiapercentageInfo o2) {
			if (o1.getStartdatum().before(o2.getStartdatum()))
				return -1;
			else
				if (o1.getStartdatum().after(o2.getStartdatum()))
					return 1;
				else
					return 0;

		}
	}
	public static void sort(List<WerknemerInfo> werknemers, __sortcol col){
		Collections.sort(werknemers, new Werknemercompare(col));
	}
}
