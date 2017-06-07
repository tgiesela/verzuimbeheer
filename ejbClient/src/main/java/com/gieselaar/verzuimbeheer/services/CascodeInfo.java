package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;

public class CascodeInfo extends InfoBase {

	/**
	 * 
	 */
	public enum __sortcol {
		NAAM;
	}

	private static final long serialVersionUID = 1L;
	private String cascode;
	private int cascodegroep;
	private String omschrijving;
	private boolean actief;
	private __vangnettype vangnettype;
	public String getCascode() {
		return cascode;
	}
	public void setCascode(String cascode) {
		this.cascode = cascode;
	}
	public int getCascodegroep() {
		return cascodegroep;
	}
	public void setCascodegroep(int cascodegroep) {
		this.cascodegroep = cascodegroep;
	}
	public String getOmschrijving() {
		return omschrijving;
	}
	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}
	public boolean isActief() {
		return actief;
	}
	public void setActief(boolean actief) {
		this.actief = actief;
	}
	public __vangnettype getVangnettype() {
		return vangnettype;
	}
	public void setVangnettype(__vangnettype vangnettype) {
		this.vangnettype = vangnettype;
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.vangnettype == null){
			throw new ValidationException("Vangnettype mag niet leeg zijn");
		}
		if (this.cascode == null || this.cascode.isEmpty()){
			throw new ValidationException("Cascode mag niet leeg zijn");
		}
		if (this.omschrijving == null || this.omschrijving.isEmpty()){
			throw new ValidationException("Omschrijving mag niet leeg zijn");
		}
		return false;
	}
	public static class Cascodecompare implements Comparator<CascodeInfo> {
		__sortcol sortcol;
		public Cascodecompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(CascodeInfo o1, CascodeInfo o2) {
			if (sortcol == __sortcol.NAAM) {
				return o1.getOmschrijving().compareToIgnoreCase(o2.getOmschrijving());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in CascodeInfo comparator");
			}

		}
	}
	public static void sort(List<CascodeInfo> cascodes, __sortcol col){
		Collections.sort(cascodes, new Cascodecompare(col));
	}
}
