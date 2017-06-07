package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestOeInfo {

	@Test
	public void test() throws ValidationException {
    	OeInfo info = new OeInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam van hiërarchische eenheid mag niet leeg zijn");
		}
		info.setNaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam van hiërarchische eenheid mag niet leeg zijn");
		}
		info.setNaam("oenaam");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Hierarchisch niveau mag niet leeg zijn");
		}
		info.setOeniveau(new OeNiveauInfo());
		info.getOeniveau().setOeniveau(2);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Bovenliggende eenheid mag niet leeg zijn!");
		}
		info.getOeniveau().setOeniveau(1);
		info.validate();
	}

}
