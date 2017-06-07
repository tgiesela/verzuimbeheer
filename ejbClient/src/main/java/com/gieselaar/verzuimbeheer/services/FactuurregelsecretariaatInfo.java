package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class FactuurregelsecretariaatInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer factuurid;
	private BigDecimal overigekosten;
	private BigDecimal secretariaatskosten;
	private BigDecimal uurtarief;
	private Integer weeknummer;
	private Integer werkzaamhedenid;
	private WerkzaamhedenInfo werkzaamheden;
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public Integer getFactuurid() {
		return factuurid;
	}

	public void setFactuurid(Integer factuurid) {
		this.factuurid = factuurid;
	}

	public BigDecimal getOverigekosten() {
		return overigekosten;
	}

	public void setOverigekosten(BigDecimal overigekosten) {
		this.overigekosten = overigekosten;
	}

	public BigDecimal getSecretariaatskosten() {
		return secretariaatskosten;
	}

	public void setSecretariaatskosten(BigDecimal secretariaatskosten) {
		this.secretariaatskosten = secretariaatskosten;
	}

	public BigDecimal getUurtarief() {
		return uurtarief;
	}

	public void setUurtarief(BigDecimal uurtarief) {
		this.uurtarief = uurtarief;
	}

	public Integer getWeeknummer() {
		return weeknummer;
	}

	public void setWeeknummer(Integer weeknummer) {
		this.weeknummer = weeknummer;
	}

	public Integer getWerkzaamhedenid() {
		return werkzaamhedenid;
	}

	public void setWerkzaamhedenid(Integer werkzaamhedenid) {
		this.werkzaamhedenid = werkzaamhedenid;
	}

	public WerkzaamhedenInfo getWerkzaamheden() {
		return werkzaamheden;
	}

	public void setWerkzaamheden(WerkzaamhedenInfo werkzaamheden) {
		this.werkzaamheden = werkzaamheden;
	}


}
