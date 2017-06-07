package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the FACTUURITEM database table.
 * 
 */
@Entity
@NamedQuery(name="Factuuritem.findAll", query="SELECT f FROM Factuuritem f")
public class Factuuritem extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal bedrag;

	@Temporal(TemporalType.TIMESTAMP)
	private Date datum;

	private int factuurcategorieid;

	private String omschrijving;
	private int userid;
	private Integer werkgeverid;
	private Integer holdingid;

	public Factuuritem() {
	}

	public BigDecimal getBedrag() {
		return this.bedrag;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	public Date getDatum() {
		return this.datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public int getFactuurcategorieid() {
		return this.factuurcategorieid;
	}

	public void setFactuurcategorieid(int factuurcategorieid) {
		this.factuurcategorieid = factuurcategorieid;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
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
		return holdingid;
	}

	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}

}