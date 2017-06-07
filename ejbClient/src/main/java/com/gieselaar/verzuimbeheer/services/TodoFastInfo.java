package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__sortcol;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo.Werknemercompare;

public class TodoFastInfo extends InfoBase {

	private static final long serialVersionUID = 1L;

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
	private Date startdatumverzuim;
	private String burgerservicenummer;
	private String achternaam;
	private String werkgevernaam;
	private Integer werknemerid;

	public enum __sortcol {
		DATUMDEADLINE;
	}
	public static class TodoFastcompare implements Comparator<TodoFastInfo> {
		
		__sortcol sortcol;
	
		public TodoFastcompare(__sortcol column) {
			sortcol = column;
		}
	
		@Override
		public int compare(TodoFastInfo o1, TodoFastInfo o2) {
			if (sortcol == __sortcol.DATUMDEADLINE){
				return o1.getDeadlinedatum().compareTo(o2.getDeadlinedatum());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in TodoFastInfo comparator");
			}
		}
	}

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
	public Date getAanmaakdatum() {
		return aanmaakdatum;
	}
	public void setAanmaakdatum(Date aanmaakdatum) {
		this.aanmaakdatum = aanmaakdatum;
	}
	public Date getStartdatumverzuim() {
		return startdatumverzuim;
	}
	public void setStartdatumverzuim(Date startdatumverzuim) {
		this.startdatumverzuim = startdatumverzuim;
	}
	public Integer getWerknemerid() {
		return werknemerid;
	}
	public void setWerknemerid(Integer werknemerid) {
		this.werknemerid = werknemerid;
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
	public static void sort(List<TodoFastInfo> todos, __sortcol col){
		Collections.sort(todos, new TodoFastcompare(col));
	}

}
