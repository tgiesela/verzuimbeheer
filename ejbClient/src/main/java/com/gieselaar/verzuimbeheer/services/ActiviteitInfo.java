package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;

public class ActiviteitInfo extends InfoBase{
	/**
	 * 
	 */
	public enum __sortcol {
		NAAM;
	}
	private static final long serialVersionUID = 4894651595201554675L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private int deadlineperiode;
	private __periodesoort deadlineperiodesoort;
	private __meldingsoort deadlinestartpunt;
	private Integer deadlinewaarschuwmoment;
	private __periodesoort deadlinewaarschuwmomentsoort;
	private String duur;
	private boolean ketenverzuim;
	private String naam;
	private boolean normaalverzuim;
	private Integer plannaactiviteit;
	private Integer repeteeraantal;
	private Integer repeteerperiode;
	private __periodesoort repeteerperiodesoort;
	private boolean vangnet;
	private __vangnettype vangnettype;
	private boolean verwijdernaherstel;
	private String omschrijving;
	public enum __periodesoort
	{
		DAGEN(1),
		WEKEN(2),
		MAANDEN(3);
		
		private int value;
		__periodesoort(int value){
			this.value = value;
		}
		public int getValue() { return value; }

        public static __periodesoort parse(int id) {
        	__periodesoort soort = DAGEN; // Default
            for (__periodesoort item : __periodesoort.values()) {
                if (item.getValue()==id) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
	public enum __meldingsoort
	{
		ZIEKMELDING(1) {@Override public String toString(){return "aanvang (keten)verzuim";}},
		GEDEELTELIJKHERSTEL(2) {@Override public String toString(){return "gedeeltelijk herstel";}},
		VOLLEDIGHERSTEL(3) {@Override public String toString(){return "volledig herstel";}},
		NIEUWEZIEKMELDING(4) {@Override public String toString(){return "aanvang verzuim";}};
		
		private Integer value;
		__meldingsoort(Integer value){
			this.value = value;
		}
		public Integer getValue() { return value; }

        public static __meldingsoort parse(Integer id) {
        	__meldingsoort soort = null; // Default
            for (__meldingsoort item : __meldingsoort.values()) {
                if (item.getValue().intValue() == id.intValue()) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
	public ActiviteitInfo() {
		super();
		this.deadlineperiodesoort = __periodesoort.DAGEN;
		this.deadlinewaarschuwmomentsoort = __periodesoort.DAGEN;
		this.repeteerperiodesoort = __periodesoort.DAGEN;
		this.deadlinestartpunt = __meldingsoort.ZIEKMELDING;
		this.deadlineperiode = 0;
		this.deadlinewaarschuwmoment = 0;
		this.repeteeraantal = 0;
		this.deadlinewaarschuwmoment = 0;
		this.repeteerperiode = 0;
	}
	@Override
	public boolean validate() throws ValidationException{
		if ((this.getNaam() == null) || (this.getNaam().isEmpty()))
			throw new ValidationException("Naam activiteit mag niet leeg zijn"); 
    	
    	return true;
	}
	public int getDeadlineperiode() {
		return deadlineperiode;
	}
	public void setDeadlineperiode(int deadlineperiode) {
		this.deadlineperiode = deadlineperiode;
	}
	public __periodesoort getDeadlineperiodesoort() {
		return deadlineperiodesoort;
	}
	public void setDeadlineperiodesoort(__periodesoort deadlineperiodesoort) {
		this.deadlineperiodesoort = deadlineperiodesoort;
	}
	public __meldingsoort getDeadlinestartpunt() {
		return deadlinestartpunt;
	}
	public void setDeadlinestartpunt(__meldingsoort deadlinestartpunt) {
		this.deadlinestartpunt = deadlinestartpunt;
	}
	public Integer getDeadlinewaarschuwmoment() {
		return deadlinewaarschuwmoment;
	}
	public void setDeadlinewaarschuwmoment(Integer deadlinewaarschuwmoment) {
		this.deadlinewaarschuwmoment = deadlinewaarschuwmoment;
	}
	public __periodesoort getDeadlinewaarschuwmomentsoort() {
		return deadlinewaarschuwmomentsoort;
	}
	public void setDeadlinewaarschuwmomentsoort(__periodesoort deadlinewaarschuwmomentsoort) {
		this.deadlinewaarschuwmomentsoort = deadlinewaarschuwmomentsoort;
	}
	public String getDuur() {
		return duur;
	}
	public void setDuur(String duur) {
		this.duur = duur;
	}
	public boolean isKetenverzuim() {
		return ketenverzuim;
	}
	public void setKetenverzuim(boolean ketenverzuim) {
		this.ketenverzuim = ketenverzuim;
	}
	public String getNaam() {
		return naam;
	}
	public void setNaam(String naam) {
		this.naam = naam;
	}
	public boolean isNormaalverzuim() {
		return normaalverzuim;
	}
	public void setNormaalverzuim(boolean normaalverzuim) {
		this.normaalverzuim = normaalverzuim;
	}
	public Integer getPlannaactiviteit() {
		return plannaactiviteit;
	}
	public void setPlannaactiviteit(Integer plannaactiviteit) {
		this.plannaactiviteit = plannaactiviteit;
	}
	public Integer getRepeteeraantal() {
		return repeteeraantal;
	}
	public void setRepeteeraantal(Integer repeteeraantal) {
		this.repeteeraantal = repeteeraantal;
	}
	public Integer getRepeteerperiode() {
		return repeteerperiode;
	}
	public void setRepeteerperiode(Integer repeteerperiode) {
		this.repeteerperiode = repeteerperiode;
	}
	public __periodesoort getRepeteerperiodesoort() {
		return repeteerperiodesoort;
	}
	public void setRepeteerperiodesoort(__periodesoort repeteerperiodesoort) {
		this.repeteerperiodesoort = repeteerperiodesoort;
	}
	public boolean isVangnet() {
		return vangnet;
	}
	public void setVangnet(boolean vangnet) {
		this.vangnet = vangnet;
	}
	public __vangnettype getVangnettype() {
		return vangnettype;
	}
	public void setVangnettype(__vangnettype vangnettype) {
		this.vangnettype = vangnettype;
	}
	public String getOmschrijving() {
		return omschrijving;
	}
	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}
	public boolean isVerwijdernaherstel() {
		return verwijdernaherstel;
	}
	public void setVerwijdernaherstel(boolean verwijdernaherstel) {
		this.verwijdernaherstel = verwijdernaherstel;
	}
	@Override 
	public String toString(){
		return this.getNaam();
	}
	public static class Activiteitcompare implements Comparator<ActiviteitInfo> {

		__sortcol sortcol;

		public Activiteitcompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(ActiviteitInfo o1, ActiviteitInfo o2) {
			if (sortcol == __sortcol.NAAM) {
				return o1.getNaam().compareToIgnoreCase(o2.getNaam());
			} else {
				throw new VerzuimRuntimeException(
						"Unknown sortcol in ActiviteitInfo comparator");
			}

		}
	}
	public static void sort(List<ActiviteitInfo> activiteiten, __sortcol col){
		Collections.sort(activiteiten, new Activiteitcompare(col));
	}
}
