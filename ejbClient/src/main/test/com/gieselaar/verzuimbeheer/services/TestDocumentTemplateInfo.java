package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo.__sortcol;

public class TestDocumentTemplateInfo {

	@Test
	public void test() throws ValidationException {
		DocumentTemplateInfo info = new DocumentTemplateInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam mag niet leeg zijn");
		}
		info.setNaam("Documentnaam");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Documentnaam niet ingevuld");
		}
		info.setPadnaam("C:\\Pad");
		info.validate();
		
		DocumentTemplateInfo info2 = new DocumentTemplateInfo();
		info2.setNaam("A Document");
		info2.setPadnaam("C:\\Pad2");
		
		ArrayList<DocumentTemplateInfo> documenten = new ArrayList<>();
		documenten.add(info);
		documenten.add(info2);
		DocumentTemplateInfo.sort(documenten, __sortcol.NAAM);
		assertEquals(documenten.get(0).getNaam(),"A Document");
		assertEquals(documenten.get(1).getNaam(),"Documentnaam");
	}

}
