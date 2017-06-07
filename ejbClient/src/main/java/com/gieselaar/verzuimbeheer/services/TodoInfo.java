package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo.Werknemercompare;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo.__sortcol;

public class TodoInfo extends InfoBase {

	private static final long serialVersionUID = 1L;

	public enum __sortcol {
		DATUMDEADLINE;
	}
	public enum __soort
	{
		AUTOMATISCH(1)	 	{@Override public String toString(){return "Automatisch aangemaakt";}},
		HANDMATIG(2) 		{@Override public String toString(){return "handmatig aangemaakt";}};
		
		private Integer value;
		__soort(Integer value){
			this.value = value;
		}
		public Integer getValue() { return value; }

        public static __soort parse(Integer staat) {
        	__soort soort = null; // Default
            for (__soort item : __soort.values()) {
                if (item.getValue().intValue()==staat.intValue()) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
	public static class Todocompare implements Comparator<TodoInfo> {
		
		__sortcol sortcol;
	
		public Todocompare(__sortcol column) {
			sortcol = column;
		}
	
		@Override
		public int compare(TodoInfo o1, TodoInfo o2) {
			if (sortcol == __sortcol.DATUMDEADLINE){
				return o1.getDeadlinedatum().compareTo(o2.getDeadlinedatum());
			}
			else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in TodoInfo comparator");
			}
		}
	}
	private int activiteitId;
	private Date deadlinedatum;
	private boolean herhalen;
	private String opmerking;
	private __soort soort;
	private int user;
	private int verzuimId;
	private Integer verzuimactiviteitId;
	private Date waarschuwingsdatum;
	private Date aanmaakdatum;
	private VerzuimInfo verzuim;
	private WerknemerInfo werknemer;
	private WerkgeverInfo werkgever;
	private String burgerservicenummer;
	private String achternaam;
	private String werkgevernaam;
	private VerzuimActiviteitInfo verzuimActiviteit;

	public int getActiviteitId() {
		return activiteitId;
	}
	public void setActiviteitId(int activiteitId) {
		this.activiteitId = activiteitId;
	}
	public Date getDeadlinedatum() {
		return deadlinedatum;
	}
	public void setDeadlinedatum(Date deadlinedatum) {
		this.deadlinedatum = deadlinedatum;
	}
	public boolean getHerhalen() {
		return herhalen;
	}
	public void setHerhalen(boolean herhalen) {
		this.herhalen = herhalen;
	}
	public String getOpmerking() {
		return opmerking;
	}
	public void setOpmerking(String opmerking) {
		this.opmerking = opmerking;
	}
	public __soort getSoort() {
		return soort;
	}
	public void setSoort(__soort soort) {
		this.soort = soort;
	}
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public int getVerzuimId() {
		return verzuimId;
	}
	public void setVerzuimId(int verzuimId) {
		this.verzuimId = verzuimId;
	}
	public Integer getVerzuimactiviteitId() {
		return verzuimactiviteitId;
	}
	public void setVerzuimactiviteitId(Integer verzuimactiviteitId) {
		this.verzuimactiviteitId = verzuimactiviteitId;
	}
	public Date getWaarschuwingsdatum() {
		return waarschuwingsdatum;
	}
	public void setWaarschuwingsdatum(Date waarschuwingsdatum) {
		this.waarschuwingsdatum = waarschuwingsdatum;
	}
	public VerzuimInfo getVerzuim() {
		return verzuim;
	}
	public void setVerzuim(VerzuimInfo verzuim) {
		this.verzuim = verzuim;
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
	public String getWerkgevernaam() {
		return werkgevernaam;
	}
	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}
	public VerzuimActiviteitInfo getVerzuimActiviteit() {
		return verzuimActiviteit;
	}
	public void setVerzuimActiviteit(VerzuimActiviteitInfo verzuimActiviteit) {
		this.verzuimActiviteit = verzuimActiviteit;
	}
	public WerknemerInfo getWerknemer() {
		return werknemer;
	}
	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}
	public WerkgeverInfo getWerkgever() {
		return werkgever;
	}
	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
	}
	public Date getAanmaakdatum() {
		return aanmaakdatum;
	}
	public void setAanmaakdatum(Date aanmaakdatum) {
		this.aanmaakdatum = aanmaakdatum;
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.getDeadlinedatum() == null)
			throw new ValidationException("Deadline niet ingevuld");
		if (this.getVerzuimId() <= 0)
			throw new ValidationException("Verzuim niet ingevuld");
		if (this.getActiviteitId() <= 0)
			throw new ValidationException("Activiteit niet ingevuld");
		if (this.getWaarschuwingsdatum() == null)
			throw new ValidationException("Waarschuwingsdatum niet ingevuld");
		return false;
	}
	public static void sort(List<TodoInfo> todos, __sortcol col){
		Collections.sort(todos, new Todocompare(col));
	}

}
