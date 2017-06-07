package com.gieselaar.verzuimbeheer.entities;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the verzuimTODO database table.
 * 
 */
@Entity
public class TodoFast{
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

   	@Id 
   	Integer id;
	private int activiteit_ID;
    @Temporal( TemporalType.DATE)
	private Date deadlinedatum;
	private int herhalen;
	private String opmerking;
	private int soort;
	private int user;
	private int verzuim_ID;
	private Integer verzuimactiviteit_ID;
    @Temporal( TemporalType.DATE)
	private Date startdatumverzuim;
	private String burgerservicenummer;
	private String achternaam;
	private String werkgevernaam;
	private Long version;
	private Integer werknemerid;

    public Integer getId() {
		return id;
	}

	public Date getStartdatumverzuim() {
		return startdatumverzuim;
	}

	public String getBurgerservicenummer() {
		return burgerservicenummer;
	}

	public String getAchternaam() {
		return achternaam;
	}

	public String getWerkgevernaam() {
		return werkgevernaam;
	}

	@Temporal( TemporalType.DATE)
	private Date waarschuwingsdatum;
    @Temporal( TemporalType.DATE)
	private Date aanmaakdatum;

    public TodoFast() {
    }

	public int getActiviteit_ID() {
		return this.activiteit_ID;
	}

	public Date getDeadlinedatum() {
		return this.deadlinedatum;
	}

	public int getHerhalen() {
		return this.herhalen;
	}

	public String getOpmerking() {
		return this.opmerking;
	}

	public int getSoort() {
		return this.soort;
	}

	public int getUser() {
		return this.user;
	}

	public int getVerzuim_ID() {
		return this.verzuim_ID;
	}

	public Integer getVerzuimactiviteit_ID() {
		return this.verzuimactiviteit_ID;
	}

	public Date getWaarschuwingsdatum() {
		return this.waarschuwingsdatum;
	}

	public Date getAanmaakdatum() {
		return aanmaakdatum;
	}

	public Long getVersion() {
		return version;
	}

	public Integer getWerknemerid() {
		return werknemerid;
	}

}