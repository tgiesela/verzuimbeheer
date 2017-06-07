package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;

public class TestFactuuritemInfo {

	@Test
	public void test() throws ValidationException  {
		FactuuritemInfo info = new FactuuritemInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Datum niet ingevuld");
		}
		info.setDatum(new Date());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Factuurcategorie niet ingevuld");
		}
		info.setFactuurcategorieid(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werkgever niet ingevuld");
		}
		info.setWerkgeverid(1);
		info.validate();
	}

}
