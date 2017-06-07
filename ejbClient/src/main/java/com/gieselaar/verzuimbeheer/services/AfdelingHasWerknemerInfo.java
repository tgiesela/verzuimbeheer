package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class AfdelingHasWerknemerInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int afdelingId;
	private int werknemerId;
	private Integer werkgeverId = null;
	private java.util.Date startdatum;
	private java.util.Date einddatum;
	private AfdelingInfo afdeling;
	@Override
	public boolean validate() throws ValidationException {
		if (this.getStartdatum() == null)
			throw new ValidationException("Ingangsdatum afdeling niet ingevuld");
		if ((this.getEinddatum() != null) && (this.getEinddatum().before(this.getStartdatum())))
			throw new ValidationException("Einddatum afdeling ligt voor ingangsdatum");
		return false;
	}
	public int getAfdelingId() {
		return afdelingId;
	}
	public void setAfdelingId(int afdelingId) {
		this.afdelingId = afdelingId;
	}
	public int getWerknemerId() {
		return werknemerId;
	}
	public void setWerknemerId(int werknemerId) {
		this.werknemerId = werknemerId;
	}
	public java.util.Date getStartdatum() {
		return startdatum;
	}
	public void setStartdatum(java.util.Date startdatum) {
		this.startdatum = startdatum;
	}
	public java.util.Date getEinddatum() {
		return einddatum;
	}
	public void setEinddatum(java.util.Date einddatum) {
		this.einddatum = einddatum;
	}
	public AfdelingInfo getAfdeling() {
		return afdeling;
	}
	public void setAfdeling(AfdelingInfo afdeling) {
		this.afdeling = afdeling;
	}
	public Integer getWerkgeverId() {
		return werkgeverId;
	}
	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}

}
