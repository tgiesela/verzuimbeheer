package com.gieselaar.verzuimbeheer.reportservices;

import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;

public class WerknemerAantallenInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer werkgeverId;
	private Integer afdelingId;
	private __geslacht geslacht;
	private Integer aantalwerknemers;
	private BigDecimal totaalurenwerknemers;
	private Date peildatum;
	private Date startdatum;
	private Date einddatum;
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}
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
	public __geslacht getGeslacht() {
		return geslacht;
	}
	public void setGeslacht(__geslacht geslacht) {
		this.geslacht = geslacht;
	}
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
	public Date getPeildatum() {
		return peildatum;
	}
	public void setPeildatum(Date peildatum) {
		this.peildatum = peildatum;
	}
	public void setStartdatum(Date startdatum) {
		this.startdatum = startdatum;
	}
	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

}
