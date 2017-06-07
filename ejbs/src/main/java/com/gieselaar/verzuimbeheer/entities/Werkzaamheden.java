package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the WERKZAAMHEDEN database table.
 * 
 */
@Entity
@NamedQuery(name="Werkzaamheden.findAll", query="SELECT w FROM Werkzaamheden w")
public class Werkzaamheden extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal aantalkm;

	@Temporal(TemporalType.DATE)
	private Date datum;
	private Integer filiaalid;
	private int geslacht;
	private BigDecimal overigekosten;
	private String personeelsnummer;
	private String persoon;
	private int soortverzuim;
	private int soortwerkzaamheden;
	private BigDecimal uren;
	private int urgentie;
	private int userid;
	private Integer werkgeverid;
	private Integer holdingid;
	private String woonplaats;
	private String omschrijving;

	public Werkzaamheden() {
	}

	public BigDecimal getAantalkm() {
		return this.aantalkm;
	}

	public void setAantalkm(BigDecimal aantalkm) {
		this.aantalkm = aantalkm;
	}

	public Date getDatum() {
		return this.datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public Integer getFiliaalid() {
		return this.filiaalid;
	}

	public void setFiliaalid(Integer filiaalid) {
		this.filiaalid = filiaalid;
	}

	public int getGeslacht() {
		return this.geslacht;
	}

	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}

	public BigDecimal getOverigekosten() {
		return this.overigekosten;
	}

	public void setOverigekosten(BigDecimal overigekosten) {
		this.overigekosten = overigekosten;
	}

	public String getPersoneelsnummer() {
		return this.personeelsnummer;
	}

	public void setPersoneelsnummer(String personeelsnummer) {
		this.personeelsnummer = personeelsnummer;
	}

	public String getPersoon() {
		return this.persoon;
	}

	public void setPersoon(String persoon) {
		this.persoon = persoon;
	}

	public int getSoortverzuim() {
		return this.soortverzuim;
	}

	public void setSoortverzuim(int soortverzuim) {
		this.soortverzuim = soortverzuim;
	}

	public int getSoortwerkzaamheden() {
		return this.soortwerkzaamheden;
	}

	public void setSoortwerkzaamheden(int soortwerkzaamheden) {
		this.soortwerkzaamheden = soortwerkzaamheden;
	}

	public BigDecimal getUren() {
		return this.uren;
	}

	public void setUren(BigDecimal uren) {
		this.uren = uren;
	}

	public int getUrgentie() {
		return this.urgentie;
	}

	public void setUrgentie(int urgentie) {
		this.urgentie = urgentie;
	}

	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Integer getWerkgeverid() {
		return this.werkgeverid;
	}

	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public Integer getHoldingid() {
		return this.holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}

	public String getWoonplaats() {
		return this.woonplaats;
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

}