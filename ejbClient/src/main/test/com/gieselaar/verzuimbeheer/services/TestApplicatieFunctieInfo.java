package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;

public class TestApplicatieFunctieInfo {

	@Test
	public void test() throws ValidationException {
		ApplicatieFunctieInfo info = new ApplicatieFunctieInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Functie omschrijving niet ingevuld");
		}
		info.setFunctieomschrijving("Omschrijving van de functie");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Functie identificatie niet ingevuld");
		}
		info.setFunctieId("FunctieId");
		info.validate();
	}

}
