package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class WerkzaamhedenInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal aantalkm;
	private Date datum;
	private Integer filiaalid;
	private __geslacht geslacht;
	private BigDecimal overigekosten;
	private String personeelsnummer;
	private String persoon;
	private __verzuimsoort soortverzuim;
	private __werkzaamhedensoort soortwerkzaamheden;
	private BigDecimal uren;
	private __huisbezoekurgentie urgentie;
	private int userid;
	private Integer werkgeverid;
	private Integer holdingid;
	private String woonplaats;
	private String omschrijving;
	
	private void checkRequiredFields() throws ValidationException{
		if (this.getDatum() == null)
			throw new ValidationException("Datum moet ingevuld zijn");
		if (this.getWerkgeverid() == null || this.getWerkgeverid().equals(-1)){
			throw new ValidationException("Werkgever niet ingevuld");
		}
		if (this.soortwerkzaamheden == null || __werkzaamhedensoort.parse(this.soortwerkzaamheden.getValue()) == null){
			throw new ValidationException("Onbekend type werkzaamheden");
		}
		if (this.getUren() == null){
			throw new ValidationException("Uren niet ingevuld");
		}
		if (this.getUren().equals(BigDecimal.ZERO)){
			throw new ValidationException("Uren niet ingevuld");
		}
		if (this.getUren().compareTo(BigDecimal.ZERO) < 0){
			throw new ValidationException("Uren < 0 niet toegestaan");
		}
	
	}
	private void checkHuisbezoek() throws ValidationException{
		if (this.soortwerkzaamheden.equals(__werkzaamhedensoort.HUISBEZOEK)){
			if (this.getPersoon()== null || this.getPersoon().isEmpty()){
				throw new ValidationException("Naam bezochte persoon niet ingevuld");
			}
			if (this.getWoonplaats() == null || this.getWoonplaats().isEmpty()){
				throw new ValidationException("Woonplaats bezochte persoon niet ingevuld");
			}
		}
	}
	@Override
	public boolean validate() throws ValidationException {
		checkRequiredFields();
		if ((this.soortwerkzaamheden.equals(__werkzaamhedensoort.SECRETARIAAT)) &&
			(this.getOmschrijving() == null || this.getOmschrijving().isEmpty())){
			throw new ValidationException("Een korte omschrijving van de secretariaatswerkzaamheden is verplicht");
		}
		if (this.soortwerkzaamheden.equals(__werkzaamhedensoort.VERLOF) ||
			this.soortwerkzaamheden.equals(__werkzaamhedensoort.ZIEK) ||
			this.soortwerkzaamheden.equals(__werkzaamhedensoort.OVERWERK)){
			/* ok */
		}else{
			checkHuisbezoek();
		}
		return false;
	}

	public BigDecimal getAantalkm() {
		return aantalkm;
	}

	public void setAantalkm(BigDecimal aantalkm) {
		this.aantalkm = aantalkm;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public Integer getFiliaalid() {
		return filiaalid;
	}

	public void setFiliaalid(Integer filiaalid) {
		this.filiaalid = filiaalid;
	}

	public __geslacht getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(__geslacht geslacht) {
		this.geslacht = geslacht;
	}

	public BigDecimal getOverigekosten() {
		return overigekosten;
	}

	public void setOverigekosten(BigDecimal overigekosten) {
		this.overigekosten = overigekosten;
	}

	public String getPersoneelsnummer() {
		return personeelsnummer;
	}

	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}

	public String getPersoon() {
		return persoon;
	}

	public void setPersoon(String persoon) {
		this.persoon = persoon;
	}

	public __verzuimsoort getSoortverzuim() {
		return soortverzuim;
	}

	public void setSoortverzuim(__verzuimsoort soortverzuim) {
		this.soortverzuim = soortverzuim;
	}

	public __werkzaamhedensoort getSoortwerkzaamheden() {
		return soortwerkzaamheden;
	}

	public void setSoortwerkzaamheden(__werkzaamhedensoort soortwerkzaamheden) {
		this.soortwerkzaamheden = soortwerkzaamheden;
	}

	public BigDecimal getUren() {
		return uren;
	}

	public void setUren(BigDecimal uren) {
		this.uren = uren;
	}

	public __huisbezoekurgentie getUrgentie() {
		return urgentie;
	}

	public void setUrgentie(__huisbezoekurgentie urgentie) {
		this.urgentie = urgentie;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Integer getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public Integer getHoldingid() {
		return holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}

	public String getWoonplaats() {
		return woonplaats;
	}

	public void setWoonplaats(String woonplaats) {
		this.woonplaats = woonplaats;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public enum __verzuimsoort {
		ONBEKEND(0) {
			@Override
			public String toString() {
				return "Onbekend";
			}
		},
		GRIJS(1) {
			@Override
			public String toString() {
				return "Grijs verzuim";
			}
		},
		MEDISCH(2) {
			@Override
			public String toString() {
				return "Medische klachten";
			}
		},
		PSYCHISCH(3) {
			@Override
			public String toString() {
				return "Psychische klachten";
			}
		},
		WERKGERELATEERD(4) {
			@Override
			public String toString() {
				return "Werkgerelateerd";
			}
		},
		NIETTHUIS(5) {
			@Override
			public String toString() {
				return "Niet thuis";
			}
		};

		private Integer value;

		__verzuimsoort(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __verzuimsoort parse(Integer type) {
			__verzuimsoort soort = null; // Default
			for (__verzuimsoort item : __verzuimsoort.values()) {
				if (item.getValue().intValue() == type.intValue()) {
					soort = item;
					break;
				}
			}
			return soort;
		}
	}
	public enum __werkzaamhedensoort {
		HUISBEZOEK(1) {
			@Override
			public String toString() {
				return "Huisbezoek";
			}
		},
		SECRETARIAAT(2) {
			@Override
			public String toString() {
				return "Secretariaat werkzaamheden";
			}
		},
		CASEMANAGEMENT(3) {
			@Override
			public String toString() {
				return "Case management";
			}
		},
		VERLOF(4) {
			@Override
			public String toString() {
				return "Vakantie/verlof";
			}
		},
		OVERWERK(5) {
			@Override
			public String toString() {
				return "Overwerk";
			}
		},
		ZIEK(6) {
			@Override
			public String toString() {
				return "Ziek";
			}
		},
		DOKTERBEZOEK(7) {
			@Override
			public String toString() {
				return "Dokter/tandarts";
			}
		};

		private Integer value;

		__werkzaamhedensoort(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __werkzaamhedensoort parse(Integer type) {
			__werkzaamhedensoort soort = null; // Default
			for (__werkzaamhedensoort item : __werkzaamhedensoort.values()) {
				if (item.getValue().intValue() == type.intValue()) {
					soort = item;
					break;
				}
			}
			return soort;
		}
	}

	public enum __huisbezoekurgentie {
		NVT(-1) {
			@Override
			public String toString() {
				return "Nvt";
			}
		},
		STANDAARD(1) {
			@Override
			public String toString() {
				return "Huisbezoek Standaard";
			}
		},
		ZELFDEDAG(2) {
			@Override
			public String toString() {
				return "Huisbezoek zelfde dag";
			}
		},
		SPOEDBEZOEK(3) {
			@Override
			public String toString() {
				return "Spoedbezoek";
			}
		},
		SPOEDBEZOEKZELFDEDAG(4) {
			@Override
			public String toString() {
				return "Spoedbezoek zelfde dag";
			}
		},
		HUISBEZOEKZATERDAG(5) {
			@Override
			public String toString() {
				return "Huisbezoek zaterdag";
			}
		},
		TELEFONISCHECONTROLE(6) {
			@Override
			public String toString() {
				return "Telefonische controle";
			}
		};

		private Integer value;

		__huisbezoekurgentie(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __huisbezoekurgentie parse(Integer type) {
			__huisbezoekurgentie soort = null; // Default
			for (__huisbezoekurgentie item : __huisbezoekurgentie.values()) {
				if (item.getValue().intValue() == type.intValue()) {
					soort = item;
					break;
				}
			}
			return soort;
		}
	}

}
