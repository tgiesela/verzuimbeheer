package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;

public class VerzuimHerstelInfo extends InfoBase {

	/**
	 * 
	 */
	public enum __sortcol {
		HERSTELDATUM;
	}
	private static final long serialVersionUID = 1L;
	private int verzuimId;
	private Date datumHerstel;
	private Date meldingsdatum;
	private String opmerkingen;
	private BigDecimal percentageHerstel;
	private BigDecimal percentageHerstelAT;
	private int user;
	private VerzuimInfo verzuim;
	private __meldingswijze meldingswijze;

	public Date getDatumHerstel() {
		return datumHerstel;
	}

	public void setDatumHerstel(Date datumHerstel) {
		this.datumHerstel = datumHerstel;
	}

	public Date getMeldingsdatum() {
		return meldingsdatum;
	}

	public void setMeldingsdatum(Date meldingsdatum) {
		this.meldingsdatum = meldingsdatum;
	}

	public __meldingswijze getMeldingswijze() {
		return meldingswijze;
	}

	public void setMeldingswijze(__meldingswijze meldingswijze) {
		this.meldingswijze = meldingswijze;
	}

	public String getOpmerkingen() {
		return opmerkingen;
	}

	public void setOpmerkingen(String opmerkingen) {
		this.opmerkingen = opmerkingen;
	}

	public BigDecimal getPercentageHerstel() {
		return percentageHerstel;
	}

	public void setPercentageHerstel(BigDecimal percentageHerstel) {
		this.percentageHerstel = percentageHerstel;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getVerzuimId() {
		return verzuimId;
	}

	public void setVerzuimId(int verzuimId) {
		this.verzuimId = verzuimId;
	}

	public BigDecimal getPercentageHerstelAT() {
		return percentageHerstelAT;
	}

	public void setPercentageHerstelAT(BigDecimal percentageHerstelAT) {
		this.percentageHerstelAT = percentageHerstelAT;
	}

	public VerzuimInfo getVerzuim() {
		return verzuim;
	}

	public void setVerzuim(VerzuimInfo verzuim) {
		this.verzuim = verzuim;
	}
	@Override
	public boolean validate() throws ValidationException {
		if (getPercentageHerstel() == null || getPercentageHerstelAT() == null)
			throw new ValidationException("Herstelpercentage LW en Herstelpecentage AT kunnen niet beide leeg zijn");
		if (getPercentageHerstel().compareTo(new BigDecimal(100)) > 0)
			throw new ValidationException("Herstelpercentage LW mag maximaal 100% zijn");
		if (getPercentageHerstel().compareTo(new BigDecimal(0)) < 0)
			throw new ValidationException("Herstelpercentage LW moet groter of gelijk aan 0% zijn");
		if (getPercentageHerstelAT().compareTo(new BigDecimal(100)) > 0)
			throw new ValidationException("Herstelpercentage AT mag maximaal 100% zijn");
		if (getPercentageHerstelAT().compareTo(new BigDecimal(0)) < 0)
			throw new ValidationException("Herstelpercentage AT moet groter of gelijk aan 0% zijn");
		if (getMeldingswijze() == null){
			throw new ValidationException("Meldingswijze moet ingevuld zijn");
		}
		return false;
	}

	public static class Verzuimherstelcompare implements Comparator<VerzuimHerstelInfo> {
	
		__sortcol sortcol;
	
		public Verzuimherstelcompare(__sortcol column) {
			sortcol = column;
		}
	
		@Override
		public int compare(VerzuimHerstelInfo o1, VerzuimHerstelInfo o2) {
			if (sortcol == __sortcol.HERSTELDATUM) {
				return o1.getDatumHerstel().compareTo(o2.getDatumHerstel());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in VerzuimHerstelInfo comparator");
			}
	
		}
	}
	public static void sort(List<VerzuimHerstelInfo> herstellen, __sortcol col){
		Collections.sort(herstellen, new Verzuimherstelcompare(col));
	}
}
