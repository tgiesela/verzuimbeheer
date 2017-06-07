package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;

public class VerzuimInfo extends InfoBase {

	/**
	 * 
	 */
	public enum __sortcol {
		STARTDATUM;
	}
	private static final long serialVersionUID = 8219704524965354571L;
	private int cascode;
	private int dienstverbandId;
	private Date einddatumverzuim;
	private int gebruiker;
	private __gerelateerdheid gerelateerdheid;
	private boolean ketenverzuim;
	private String medischekaart;
	private Date meldingsdatum;
	private Date startdatumverzuim;
	private __vangnettype vangnettype;
	private __verzuimtype verzuimtype;
	private __meldingswijze meldingswijze;
	private String opmerkingen;
	private DienstverbandInfo dienstverband;
	private WerknemerInfo werknemer;
	private List<VerzuimHerstelInfo> verzuimherstellen;
	private List<VerzuimMedischekaartInfo> verzuimmedischekaarten;
	private List<VerzuimDocumentInfo> verzuimdocumenten;
	private List<VerzuimActiviteitInfo> verzuimactiviteiten;
	private List<TodoInfo> todos;
	private boolean uitkeringnaarwerknemer;

	public List<VerzuimMedischekaartInfo> getVerzuimmedischekaarten() {
		return verzuimmedischekaarten;
	}

	public void setVerzuimmedischekaarten(
			List<VerzuimMedischekaartInfo> verzuimmedischekaarten) {
		this.verzuimmedischekaarten = verzuimmedischekaarten;
	}

	public List<VerzuimDocumentInfo> getVerzuimdocumenten() {
		return verzuimdocumenten;
	}

	public void setVerzuimdocumenten(List<VerzuimDocumentInfo> verzuimdocumenten) {
		this.verzuimdocumenten = verzuimdocumenten;
	}

	public List<VerzuimActiviteitInfo> getVerzuimactiviteiten() {
		return verzuimactiviteiten;
	}

	public void setVerzuimactiviteiten(
			List<VerzuimActiviteitInfo> verzuimactiviteiten) {
		this.verzuimactiviteiten = verzuimactiviteiten;
	}

	public WerknemerInfo getWerknemer() {
		return werknemer;
	}

	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}

	public enum __gerelateerdheid {
		NVT(0) {
			@Override
			public String toString() {
				return "N.v.t";
			}
		},
		SPORT(1) {
			@Override
			public String toString() {
				return "Sport";
			}
		},
		VERKEER(2) {
			@Override
			public String toString() {
				return "Verkeer";
			}
		},
		ARBEID(3) {
			@Override
			public String toString() {
				return "Arbeid";
			}
		};

		private Integer value;

		__gerelateerdheid(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __gerelateerdheid parse(Integer type) {
			__gerelateerdheid gerelateerdheid = null; // Default
			for (__gerelateerdheid item : __gerelateerdheid.values()) {
				if (item.getValue().equals(type)) {
					gerelateerdheid = item;
					break;
				}
			}
			return gerelateerdheid;
		}
	}

	public enum __vangnettype {
		ORGAANDONATIE(1) {
			@Override
			public String toString() {
				return "Orgaan Donatie";
			}
		}, /* ZW */
		NVT(2) {
			@Override
			public String toString() {
				return "N.v.t.";
			}
		},
		WIA(3) {
			@Override
			public String toString() {
				return "WIA";
			}
		}, /* ZW */
		ZWANGERSCHAP(4) {
			@Override
			public String toString() {
				return "Zwangerschapsverlof";
			}
		}, /* WAZO */
		PLEEGZORG(5) {
			@Override
			public String toString() {
				return "Pleegzorg";
			}
		}, /* WAZO */
		ADOPTIE(6) {
			@Override
			public String toString() {
				return "Adoptie";
			}
		}, /* WAZO */
		OVERIG(7) {
			@Override
			public String toString() {
				return "Overig";
			}
		}, /* ZW */
		ZIEKDOORZWANGERSCHAP(8) {
			@Override
			public String toString() {
				return "Ziek door zwangerschap";
			}
		}; /* ZW */

		private Integer value;

		__vangnettype(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __vangnettype parse(Integer type) {
			__vangnettype vangnettype = null; // Default
			for (__vangnettype item : __vangnettype.values()) {
				if (item.getValue().equals(type)) {
					vangnettype = item;
					break;
				}
			}
			return vangnettype;
		}
	}

	public enum __verzuimtype {
		VERZUIM(1) {
			@Override
			public String toString() {
				return "Verzuim";
			}
		},
		PREVENTIEF(2) {
			@Override
			public String toString() {
				return "Preventief";
			}
		};

		private Integer value;

		__verzuimtype(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __verzuimtype parse(Integer type) {
			__verzuimtype verzuimtype = null; // Default
			for (__verzuimtype item : __verzuimtype.values()) {
				if (item.getValue().intValue() == type.intValue()) {
					verzuimtype = item;
					break;
				}
			}
			return verzuimtype;
		}
	}

	public enum __meldingswijze {
		TELEFOON(1) {
			@Override
			public String toString() {
				return "Telefoon";
			}
		},
		EMAIL(2) {
			@Override
			public String toString() {
				return "Email";
			}
		},
		FAX(3) {
			@Override
			public String toString() {
				return "Fax";
			}
		},
		BRIEF(4) {
			@Override
			public String toString() {
				return "Brief";
			}
		},
		INTERNET(5) {
			@Override
			public String toString() {
				return "Internet";
			}
		};

		private Integer value;

		__meldingswijze(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __meldingswijze parse(Integer wijze) {
			__meldingswijze meldingswijze = null; // Default
			for (__meldingswijze item : __meldingswijze.values()) {
				if (item.getValue().intValue() == wijze.intValue()) {
					meldingswijze = item;
					break;
				}
			}
			return meldingswijze;
		}
	}

	public int getCascode() {
		return cascode;
	}

	public void setCascode(int cascode) {
		this.cascode = cascode;
	}

	public int getDienstverbandId() {
		return dienstverbandId;
	}

	public void setDienstverbandId(int dienstverbandId) {
		this.dienstverbandId = dienstverbandId;
	}

	public Date getEinddatumverzuim() {
		return einddatumverzuim;
	}

	public void setEinddatumverzuim(Date einddatumverzuim) {
		this.einddatumverzuim = einddatumverzuim;
	}

	public int getGebruiker() {
		return gebruiker;
	}

	public void setGebruiker(int gebruiker) {
		this.gebruiker = gebruiker;
	}

	public __gerelateerdheid getGerelateerdheid() {
		return gerelateerdheid;
	}

	public void setGerelateerdheid(__gerelateerdheid gerelateerdheid) {
		this.gerelateerdheid = gerelateerdheid;
	}

	public boolean getKetenverzuim() {
		return ketenverzuim;
	}

	public void setKetenverzuim(boolean ketenverzuim) {
		this.ketenverzuim = ketenverzuim;
	}

	public String getMedischekaart() {
		return medischekaart;
	}

	public void setMedischekaart(String medischekaart) {
		this.medischekaart = medischekaart;
	}

	public Date getMeldingsdatum() {
		return meldingsdatum;
	}

	public void setMeldingsdatum(Date meldingsdatum) {
		this.meldingsdatum = meldingsdatum;
	}

	public Date getStartdatumverzuim() {
		return startdatumverzuim;
	}

	public void setStartdatumverzuim(Date startdatumverzuim) {
		this.startdatumverzuim = startdatumverzuim;
	}

	public __vangnettype getVangnettype() {
		return vangnettype;
	}

	public void setVangnettype(__vangnettype vangnettype) {
		this.vangnettype = vangnettype;
	}

	public __verzuimtype getVerzuimtype() {
		return verzuimtype;
	}

	public void setVerzuimtype(__verzuimtype verzuimtype) {
		this.verzuimtype = verzuimtype;
	}

	public __meldingswijze getMeldingswijze() {
		return meldingswijze;
	}

	public void setMeldingswijze(__meldingswijze meldingswijze) {
		this.meldingswijze = meldingswijze;
	}

	public String getOpmerkingen() {
		return opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public DienstverbandInfo getDienstverband() {
		return dienstverband;
	}

	public void setDienstverband(DienstverbandInfo dienstverband) {
		this.dienstverband = dienstverband;
	}

	public List<VerzuimHerstelInfo> getVerzuimherstellen() {
		return verzuimherstellen;
	}

	public void setVerzuimherstellen(List<VerzuimHerstelInfo> verzuimherstellen) {
		this.verzuimherstellen = verzuimherstellen;
	}

	public List<TodoInfo> getTodos() {
		return todos;
	}

	public void setTodos(List<TodoInfo> todos) {
		this.todos = todos;
	}

	public boolean isUitkeringnaarwerknemer() {
		return uitkeringnaarwerknemer;
	}

	public void setUitkeringnaarwerknemer(boolean uitkeringnaarwerknemer) {
		this.uitkeringnaarwerknemer = uitkeringnaarwerknemer;
	}

	@Override
	public boolean validate() throws ValidationException {
		if ((this.getWerknemer() == null) || (this.getDienstverband() == null))
			throw new ValidationException(
					"Werknemer en/of dienstverband ontbreekt");
		if ((this.werknemer.getGeslacht() == __geslacht.MAN) &&
			(this.vangnettype == __vangnettype.ZWANGERSCHAP || 
			 this.vangnettype == __vangnettype.ZIEKDOORZWANGERSCHAP))
			throw new ValidationException(
					"Werknemer is man en vangnettype is zwangerschap");

		if ((getEinddatumverzuim() != null) &&
			(this.getStartdatumverzuim().after(getEinddatumverzuim())))
			throw new ValidationException(
					"Startdatum verzuim ligt na einddatum verzuim");

		if (this.getCascode() <= 0) {
			throw new ValidationException(
					"Reden verzuim (Cascode) niet ingevuld");
		}
		if ((this.dienstverband.getEinddatumcontract() != null) &&
			(this.getStartdatumverzuim().after(this.dienstverband.getEinddatumcontract()))){
			throw new ValidationException(
					"Startdatum verzuim ligt na einddatum dienstverband.");
		}
		return false;
	}

	public BigDecimal getHerstelpercentage() {
		Date datumHerstel;
		BigDecimal percentage = new BigDecimal(0);
		datumHerstel = this.getStartdatumverzuim();

		if (this.getVerzuimherstellen() == null)
			return percentage;
		else
			for (VerzuimHerstelInfo h : this.getVerzuimherstellen()) {
				if ((this.getId() == h.getVerzuimId()) &&
					(h.getDatumHerstel().after(datumHerstel) || 
					 h.getDatumHerstel().equals(datumHerstel))) {
					datumHerstel = h.getDatumHerstel();
					percentage = h.getPercentageHerstel();
				}
			}
		return percentage;
	}

	/*
	 * LET OP!! Soortgelijke controles zitten ook in VerzuimBean om de gebruiker
	 * vooraf te kunnen informeren
	 */
	public boolean checkIsKetenverzuim(VerzuimInfo info,
			List<VerzuimInfo> alleVerzuimen) throws ValidationException {
		/*
		 * Hier wordt bepaald of een verzuim een ketenverzuim is. De flag voor
		 * ketenverzuim wordt hier aangezet (indien van toepassing). We doen dit
		 * alleen voor nieuwe verzuimen. Dus de einddatum moet nog leeg zijn.
		 */
		if (info.getVerzuimtype() == __verzuimtype.PREVENTIEF)
			return false;

		if (info.getEinddatumverzuim() != null)
			return false;

		info.setKetenverzuim(false);
		Calendar datumKetenverzuim = Calendar.getInstance();
		datumKetenverzuim.setTime(info.getStartdatumverzuim());
		datumKetenverzuim.add(Calendar.DATE, -28);
		if (alleVerzuimen != null){
			for (VerzuimInfo oudverzuim : alleVerzuimen) {
				if (!info.getId().equals(oudverzuim.getId())) {
					if (oudverzuim.getStartdatumverzuim().after(
							info.getStartdatumverzuim())
							|| oudverzuim.getVerzuimtype() == __verzuimtype.PREVENTIEF){
						/* die vergeten we dan maar */
					} else {
						if (oudverzuim.getEinddatumverzuim() != null) {
							if (oudverzuim.getEinddatumverzuim().after(
									datumKetenverzuim.getTime())) {
								/*
								 * De hersteldatum van dit verzuim ligt binnen 4
								 * weken van het onderhavige verzuim
								 */
								info.setKetenverzuim(true);
								return true;
							}
						} else { /*
								 * Het andere verzuim staat nog open, dat zou
								 * niet voor moeten komen!
								 */
							throw new ValidationException(
									"meerdere open verzuimen!");
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isFrequentverzuim(VerzuimInfo verzuim,
			List<VerzuimInfo> alleVerzuimen) {
		// Controleer of er een meer dan 3 verzuimen zijn in de afgelopen 12
		// maanden.
		int aantalperjaar = 1;
		Calendar startdatumJaar = Calendar.getInstance();
		startdatumJaar.add(Calendar.YEAR, -1);
		if (alleVerzuimen != null)
			for (VerzuimInfo oudverzuim : alleVerzuimen) {
				if ((verzuim.getId().intValue()  != oudverzuim.getId().intValue()) &&
				   ((oudverzuim.getVerzuimtype() != __verzuimtype.PREVENTIEF) &&
					(oudverzuim.getStartdatumverzuim().after(startdatumJaar.getTime())))) {
					/*
					 * De hersteldatum van dit verzuim ligt binnen 12
					 * maanden van het onderhavige verzuim
					 */
					aantalperjaar++;
				}
			}
		if (aantalperjaar >= 3)
			return true;
		return false;
	}

	public void checkOverlap(VerzuimInfo info, List<VerzuimInfo> alleVerzuimen)
			throws ValidationException {
		if (alleVerzuimen != null) {
			for (VerzuimInfo oudverzuim : alleVerzuimen) {
				if (info.getId().equals(oudverzuim.getId())){
					/* Skip */
				} else {
					/*
					 * info.Startdatum mag niet tussen startdatum en einddatum
					 * ander verzuim liggen
					 * 
					 * info.Einddatum mag niet tussen startdatum en einddatum
					 * ander verzuim liggen
					 * 
					 * als ander verzuim nog open is, mag info.startdatum EN
					 * info.einddatum niet na de startdatum liggen
					 * 
					 * info.Startdatum mag niet voor de startdatum liggen als de
					 * info.Einddatum na de startdatum ligt
					 */
					if (oudverzuim.getEinddatumverzuim() == null) { /*
																	 * Open
																	 * verzuim
																	 */
						checkOverlapOpenverzuim(info, oudverzuim);
					} else {
						/* dit is een afgesloten verzuim */
						checkOverlapAfgeslotenverzuim(info, oudverzuim);
					}
				}
			}
		}
	}

	private void checkOverlapAfgeslotenverzuim(VerzuimInfo info, VerzuimInfo oudverzuim) throws ValidationException {
		if (info.getStartdatumverzuim().after(
				oudverzuim.getStartdatumverzuim())
				&& info.getStartdatumverzuim().before(
						oudverzuim.getEinddatumverzuim())){
				throw new ValidationException(
						"Startdatum overlapt met afgesloten verzuim");
			} else {
				if (info.getEinddatumverzuim() != null){
					if (info.getEinddatumverzuim().after(
							oudverzuim.getStartdatumverzuim())
							&& info.getEinddatumverzuim().before(
									oudverzuim.getEinddatumverzuim()))
						throw new ValidationException(
								"Einddatum overlapt met afgesloten verzuim");
					if (info.getStartdatumverzuim().before(
							oudverzuim.getStartdatumverzuim())
							&& info.getEinddatumverzuim().after(oudverzuim.getStartdatumverzuim())){
						throw new ValidationException(
								"Einddatum overlapt met afgesloten verzuim");
					}
				}
			}
	}

	private void checkOverlapOpenverzuim(VerzuimInfo info, VerzuimInfo oudverzuim) throws ValidationException {
		if (info.getStartdatumverzuim().after(
				oudverzuim.getStartdatumverzuim()))
			throw new ValidationException(
					"Startdatum overlapt met open verzuim");
		else if (info.getEinddatumverzuim() != null) {
			if (info.getEinddatumverzuim().after(
					oudverzuim.getStartdatumverzuim()))
				throw new ValidationException(
						"Einddatum overlapt met open verzuim");
		} else {
			throw new ValidationException(
					"Meerdere open verzuimen!");
		}
	}

	public static class Verzuimcompare implements Comparator<VerzuimInfo> {

		__sortcol sortcol;

		public Verzuimcompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(VerzuimInfo o1, VerzuimInfo o2) {
			if (sortcol == __sortcol.STARTDATUM) {
				return o1.getStartdatumverzuim().compareTo(
						o2.getStartdatumverzuim());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in VerzuimInfo comparator");
			}

		}
	}
	public static void sort(List<VerzuimInfo> verzuimen, __sortcol col){
		Collections.sort(verzuimen, new Verzuimcompare(col));
	}
	public static void sortreverse(List<VerzuimInfo> verzuimen, __sortcol col){
		Collections.sort(verzuimen, Collections.reverseOrder(new Verzuimcompare(col)));
	}

}
