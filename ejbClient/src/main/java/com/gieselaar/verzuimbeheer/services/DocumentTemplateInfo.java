package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;

public class DocumentTemplateInfo extends InfoBase {
	/**
	 * 
	 */
	public enum __sortcol {
		NAAM;
	}
	private static final long serialVersionUID = 1L;
	private String naam;
	private String omschrijving;
	private String padnaam;

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public String getPadnaam() {
		return padnaam;
	}

	public void setPadnaam(String padnaam) {
		this.padnaam = padnaam;
	}

	@Override
	public boolean validate() throws ValidationException {
		if ((this.getNaam() == null) || (this.getNaam().isEmpty()))
			throw new ValidationException("Naam mag niet leeg zijn");
		if ((this.getPadnaam() == null) || (this.getPadnaam().isEmpty()))
			throw new ValidationException("Documentnaam niet ingevuld");
		
		return false;
	}
	public static class Templatecompare implements Comparator<DocumentTemplateInfo> {

		__sortcol sortcol;

		public Templatecompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(DocumentTemplateInfo o1, DocumentTemplateInfo o2) {
			if (sortcol == __sortcol.NAAM) {
				return o1.getNaam().compareToIgnoreCase(o2.getNaam());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in DocumentTemplateInfo comparator");
			}

		}
	}
	public static void sort(List<DocumentTemplateInfo> cascodes, __sortcol col){
		Collections.sort(cascodes, new Templatecompare(col));
	}

}
