package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the BTW database table.
 * 
 */
@Entity
@NamedQuery(name="Btw.findAll", query="SELECT b FROM Btw b")
public class Btw extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private int btwtariefsoort;

	@Temporal(TemporalType.DATE)
	private Date einddatum;

	@Temporal(TemporalType.DATE)
	private Date ingangsdatum;

	private BigDecimal percentage;

	public Btw() {
	}

	public int getBtwtariefsoort() {
		return this.btwtariefsoort;
	}

	public void setBtwtariefsoort(int btwtariefsoort) {
		this.btwtariefsoort = btwtariefsoort;
	}

	public Date getEinddatum() {
		return this.einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public Date getIngangsdatum() {
		return this.ingangsdatum;
	}

	public void setIngangsdatum(Date ingangsdatum) {
		this.ingangsdatum = ingangsdatum;
	}

	public BigDecimal getPercentage() {
		return this.percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

}