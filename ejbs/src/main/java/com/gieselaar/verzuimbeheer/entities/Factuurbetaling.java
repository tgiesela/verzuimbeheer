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
@NamedQuery(name="Factuurbetaling.findAll", query="SELECT f FROM Factuurbetaling f")
public class Factuurbetaling extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer factuurid;
	@Temporal(TemporalType.TIMESTAMP)
	private Date datum;
	private BigDecimal bedrag;
	private String rekeningnummerbetaler;

	public Factuurbetaling() {
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

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

	public String getRekeningnummerbetaler() {
		return rekeningnummerbetaler;
	}

	public void setRekeningnummerbetaler(String rekeningnummerbetaler) {
		this.rekeningnummerbetaler = rekeningnummerbetaler;
	}


}