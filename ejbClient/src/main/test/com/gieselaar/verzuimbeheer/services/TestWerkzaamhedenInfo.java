package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;

public class TestWerkzaamhedenInfo {

	@Test
	public void test() throws ValidationException {
		WerkzaamhedenInfo info = new WerkzaamhedenInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Datum moet ingevuld zijn");
		}
		info.setDatum(new Date());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werkgever niet ingevuld");
		}
		info.setWerkgeverid(-1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werkgever niet ingevuld");
		}
		info.setWerkgeverid(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Onbekend type werkzaamheden");
		}
		info.setSoortwerkzaamheden(__werkzaamhedensoort.CASEMANAGEMENT);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Uren niet ingevuld");
		}
		info.setUren(new BigDecimal(0));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Uren niet ingevuld");
		}
		info.setUren(new BigDecimal(-1));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Uren < 0 niet toegestaan");
		}
		info.setUren(new BigDecimal(1));
		info.setSoortwerkzaamheden(__werkzaamhedensoort.SECRETARIAAT);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Een korte omschrijving van de secretariaatswerkzaamheden is verplicht");
		}
		info.setOmschrijving("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Een korte omschrijving van de secretariaatswerkzaamheden is verplicht");
		}
		info.setSoortwerkzaamheden(__werkzaamhedensoort.HUISBEZOEK);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam bezochte persoon niet ingevuld");
		}
		info.setPersoon("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam bezochte persoon niet ingevuld");
		}
		info.setPersoon("persoon");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Woonplaats bezochte persoon niet ingevuld");
		}
		info.setWoonplaats("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Woonplaats bezochte persoon niet ingevuld");
		}
		info.setWoonplaats("woonplaats");
		info.validate();
	}

}
