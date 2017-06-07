package com.gieselaar.verzuimbeheer.entities;

import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBase;

@Entity
public class WerknemerAantallen extends EntityBase {

	@EmbeddedId
	private WerknemerAantallenPK id;
	private Integer werkgeverId;
	private Integer afdelingId;
	private int geslacht;
	private Integer aantalwerknemers;
	private BigDecimal totaalurenwerknemers;
	public WerknemerAantallenPK getId() {
		return id;
	}
	public void setId(WerknemerAantallenPK id) {
		this.id = id;
	}
	/*
	public Integer getWerkgeverId() {
		return werkgeverId;
	}
	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}
	public Integer getAfdelingId() {
		return afdelingId;
	}
	public void setAfdelingId(Integer afdelingId) {
		this.afdelingId = afdelingId;
	}
	public int getGeslacht() {
		return geslacht;
	}
	public void setGeslacht(int geslacht) {
		this.geslacht = geslacht;
	}
	*/
	public Integer getAantalwerknemers() {
		return aantalwerknemers;
	}
	public void setAantalwerknemers(Integer aantalwerknemers) {
		this.aantalwerknemers = aantalwerknemers;
	}
	public BigDecimal getTotaalurenwerknemers() {
		return totaalurenwerknemers;
	}
	public void setTotaalurenwerknemers(BigDecimal totaalurenwerknemers) {
		this.totaalurenwerknemers = totaalurenwerknemers;
	}

}
