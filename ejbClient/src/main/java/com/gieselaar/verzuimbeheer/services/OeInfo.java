package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;

public class OeInfo extends InfoBase {
	/**
	 * 
	 */
	public enum __sortcol {
		NAAM;
	}
	private static final long serialVersionUID = 1L;
	private String naam;
	private Integer parentoeid;
	private Integer werkgeverId;
	private OeNiveauInfo oeniveau;

	@Override
	public boolean validate() throws ValidationException {
		if (this.getNaam() == null || this.getNaam().isEmpty()){
			throw new ValidationException("Naam van hiërarchische eenheid mag niet leeg zijn");
		}
		if (this.getOeniveau() == null){
			throw new ValidationException("Hierarchisch niveau mag niet leeg zijn");
		}
		if ((this.getParentoeId() == null) && (this.getOeniveau().getOeniveau() != 1)){
			throw new ValidationException("Bovenliggende eenheid mag niet leeg zijn!");
		}
		return false;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Integer getParentoeId() {
		return parentoeid;
	}

	public void setParentoeId(Integer parentoeid) {
		this.parentoeid = parentoeid;
	}

	public Integer getWerkgeverId() {
		return werkgeverId;
	}

	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}

	public OeNiveauInfo getOeniveau() {
		return oeniveau;
	}

	public void setOeniveau(OeNiveauInfo oeniveau) {
		this.oeniveau = oeniveau;
	}
	public static class Oecompare implements Comparator<OeInfo> {

		__sortcol sortcol;

		public Oecompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(OeInfo o1, OeInfo o2) {
			if (sortcol == __sortcol.NAAM) {
				return o1.getNaam().compareToIgnoreCase(o2.getNaam());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in OeInfo comparator");
			}

		}
	}
	public static void sort(List<OeInfo> oes, __sortcol col){
		Collections.sort(oes, new Oecompare(col));
	}

}
