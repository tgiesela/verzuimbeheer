package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;


/**
 * The persistent class for the FACTUURREGELITEM database table.
 * 
 */
@Entity
@NamedQuery(name="Factuurregelitem.findAll", query="SELECT f FROM Factuurregelitem f")
public class Factuurregelitem extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal bedrag;
	private BigDecimal btwbedrag;
	private Integer factuuritemid;
	private Integer factuurid;
	private Integer btwcategorie;
	private BigDecimal btwpercentage;

	public Factuurregelitem() {
	}

	public BigDecimal getBedrag() {
		return bedrag;
	}

	public void setBedrag(BigDecimal bedrag) {
		this.bedrag = bedrag;
	}

	public BigDecimal getBtwbedrag() {
		return btwbedrag;
	}

	public void setBtwbedrag(BigDecimal btwbedrag) {
		this.btwbedrag = btwbedrag;
	}

	public Integer getBtwcategorie() {
		return btwcategorie;
	}

	public void setBtwcategorie(Integer btwcategorie) {
		this.btwcategorie = btwcategorie;
	}

	public BigDecimal getBtwpercentage() {
		return btwpercentage;
	}

	public void setBtwpercentage(BigDecimal btwpercentage) {
		this.btwpercentage = btwpercentage;
	}

	public Integer getFactuuritemid() {
		return factuuritemid;
	}

	public void setFactuuritemid(Integer factuuritemid) {
		this.factuuritemid = factuuritemid;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

}