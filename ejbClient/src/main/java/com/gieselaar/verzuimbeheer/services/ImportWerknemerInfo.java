package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;

public class ImportWerknemerInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String externafdelingsid;
	private String afdelingsnaam;
	private Date datumbeginafdeling;
	private String burgerservicenummer;
	private String achternaam;
	private String voorvoegsel;
	private String voorletters;
	private String voornaam;
	private __geslacht geslacht;
	private __burgerlijkestaat burgerlijkestaat;
	private Date datumindienst;
	private Date datumuitdienst;
	private Date geboortedatum;
	private String personeelsnummer;
	private BigDecimal werkweek;
	private String adres;
	private String postcode;
	private String woonplaats;
	private String telefoonprive;
	private String telefoonwerk;
	private String telefoonmobiel;
	private String email;
	private String functie;
	private __wiapercentage wiapercentage;
	private ImportResult importresult = new ImportResult();
	private int sequencenr;
	
	private __checkresult checkresult = __checkresult.OK;
	private boolean accepted = false;
	
	private static final String DATEFORMAT = "dd-MM-yyyy";
	public Date getDatumbeginafdeling() {
		return datumbeginafdeling;
	}

	public void setDatumbeginafdeling(String datumbeginafdeling) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT);
		this.datumbeginafdeling = df.parse(datumbeginafdeling);
	}

	public void setDatumbeginafdeling(Date datumbeginafdeling){
		this.datumbeginafdeling = datumbeginafdeling;
	}

	public Date getDatumindienst() {
		return datumindienst;
	}

	public void setDatumindienst(String datumindienst) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT);
		this.datumindienst = df.parse(datumindienst);
	}

	public void setDatumindienst(Date datumindienst){
		this.datumindienst = datumindienst;
	}
	
	public Date getDatumuitdienst() {
		return datumuitdienst;
	}

	public void setDatumuitdienst(String datumuitdienst) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT);
		this.datumuitdienst = df.parse(datumuitdienst);
	}

	public void setDatumuitdienst(Date datumuitdienst){
		this.datumuitdienst = datumuitdienst;
	}
	
	public Date getGeboortedatum() {
		return geboortedatum;
	}

	public void setGeboortedatum(Date geboortedatum) {
		this.geboortedatum = geboortedatum;
	}

	public String getExternafdelingsid() {
		return externafdelingsid;
	}

	public void setExternafdelingsid(String externafdelingsid) {
		this.externafdelingsid = externafdelingsid;
	}

	public String getAfdelingsnaam() {
		return afdelingsnaam;
	}

	public void setAfdelingsnaam(String afdelingsnaam) {
		this.afdelingsnaam = afdelingsnaam;
	}

	public String getBurgerservicenummer() {
		return burgerservicenummer;
	}

	public void setBurgerservicenummer(String burgerservicenummer) {
		this.burgerservicenummer = burgerservicenummer;
	}

	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	public String getVoorletters() {
		return voorletters;
	}

	public void setVoorletters(String voorletters) {
		this.voorletters = voorletters;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public __geslacht getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(__geslacht geslacht) {
		this.geslacht = geslacht;
	}

	public __burgerlijkestaat getBurgerlijkestaat() {
		return burgerlijkestaat;
	}

	public void setBurgerlijkestaat(__burgerlijkestaat burgerlijkestaat) {
		this.burgerlijkestaat = burgerlijkestaat;
	}

	public String getPersoneelsnummer() {
		return personeelsnummer;
	}

	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}

	public String getAdres() {
		return adres;
	}

	public void setAdres(String adres) {
		this.adres = adres;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getWoonplaats() {
		return woonplaats;
	}

	public void setWoonplaats(String woonplaats) {
		this.woonplaats = woonplaats;
	}

	public String getTelefoonprive() {
		return telefoonprive;
	}

	public void setTelefoonprive(String telefoonprive) {
		this.telefoonprive = telefoonprive;
	}

	public int getSequencenr() {
		return sequencenr;
	}

	public void setSequencenr(int sequencenr) {
		this.sequencenr = sequencenr;
	}

	public String getTelefoonwerk() {
		return telefoonwerk;
	}

	public void setTelefoonwerk(String telefoonwerk) {
		this.telefoonwerk = telefoonwerk;
	}

	public String getTelefoonmobiel() {
		return telefoonmobiel;
	}

	public void setTelefoonmobiel(String telefoonmobiel) {
		this.telefoonmobiel = telefoonmobiel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFunctie() {
		return functie;
	}

	public void setFunctie(String functie) {
		this.functie = functie;
	}

	public __wiapercentage getWiapercentage() {
		return wiapercentage;
	}

	public void setWiapercentage(__wiapercentage wiapercentage) {
		this.wiapercentage = wiapercentage;
	}

	public BigDecimal getWerkweek() {
		return werkweek;
	}

	public void setWerkweek(String werkweek) throws ParseException {
		DecimalFormat df = new DecimalFormat("##0,00");
		df.setParseBigDecimal(true);
		this.werkweek = (BigDecimal)df.parse(werkweek.replaceAll("\\.", ","));
	}

	public void setWerkweek(BigDecimal werkweek) throws ParseException {
		this.werkweek = werkweek;
	}

	@Override
	public boolean validate() throws ValidationException {
		return false;
	}
	
	public __checkresult getCheckresult() {
		return checkresult;
	}

	public void setCheckresult(__checkresult checkresult) {
		this.checkresult = checkresult;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public ImportResult getImportresult() {
		return importresult;
	}

	public void setImportresult(ImportResult importresult) {
		this.importresult = importresult;
	}

	public enum __checkresult {
		NOTSET(0) {
			@Override
			public String toString() {
				return "Ongedefinieerd";
			}
		}, 
		HOLDINGMISMATCH(1) {
			@Override
			public String toString() {
				return "Holding hoort niet bij factuur!";
			}
		},
		WERKGEVERMISMTACH(2) {
			@Override
			public String toString() {
				return "Werkgever hoort niet bij factuur!";
			}
		},
		NOMATCHINGFACTUUR(3) {
			@Override
			public String toString() {
				return "Factuur niet gevonden!";
			}
		},
		AMOUNTMISMATCH(4) {
			@Override
			public String toString() {
				return "Bedragen zijn verschillend.";
			}
		},
		OK(5) {
			@Override
			public String toString() {
				return "";
			}
		},
		ALREADYPAID(6) {
			@Override
			public String toString() {
				return "Factuur is reeds betaald.";
			}
		}, 
		DOUBLEPAYMENT(7){
			@Override
			public String toString() {
				return "Dubbele betaling?";
			}
		};

		private Integer value;

		__checkresult(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __checkresult parse(Integer type) {
			__checkresult checkresult = null; // Default
			for (__checkresult item : __checkresult.values()) {
				if (item.getValue().equals(type)) {
					checkresult = item;
					break;
				}
			}
			return checkresult;
		}
	}
	public enum __sortcol {
		DATUM,
		BSN;
	}
	public static class Werknemercompare implements Comparator<ImportWerknemerInfo> {

		__sortcol sortcol;

		public Werknemercompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(ImportWerknemerInfo o1, ImportWerknemerInfo o2) {
			switch (sortcol) {
			case DATUM:
				return compareResultBSNDatumSeqnr(o1, o2);
			case BSN:
				return o1.getBurgerservicenummer().compareToIgnoreCase(o2.getBurgerservicenummer());
			default:
				throw new VerzuimRuntimeException(
						"Unknown sortcol in WerknemerInfo comparator");
			}

		}

		private int compareResultBSNDatumSeqnr(ImportWerknemerInfo o1, ImportWerknemerInfo o2) {
			int result;
			if (o1.getImportresult().getResult() > o2.getImportresult().getResult())
				result = 1;
			else
				if (o1.getImportresult().getResult() < o2.getImportresult().getResult())
					result = -1;
				else
					result = 0;
			if (result != 0) 
				return result;
			
			if (o1.getImportresult().isImportok() && o2.getImportresult().isImportok()){
				result = compareBsnDatumSeqnr(o1,o2);
				if (result != 0) 
					return result;
			}
			
			return result;
			
		}

		private int compareBsnDatumSeqnr(ImportWerknemerInfo o1, ImportWerknemerInfo o2) {
			int result;
			result = o1.getBurgerservicenummer().compareTo(o2.getBurgerservicenummer());
			if (result != 0) 
				return result;

			result = o1.getDatumindienst().compareTo(o2.getDatumindienst());
			if (result != 0) 
				return result;

			if (o1.getSequencenr() > o2.getSequencenr())
				result = 1;
			else
				if (o1.getSequencenr() < o2.getSequencenr())
					result = -1;
				else
					result = 0;
			return result;
		}
	}
	
	public static void sort(List<ImportWerknemerInfo> werknemers, __sortcol col){
		Collections.sort(werknemers, new Werknemercompare(col));
	}
}
