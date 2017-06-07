package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestRolInfo {

	@Test
	public void test() throws ValidationException {
    	RolInfo info = new RolInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Omschrijving niet ingevuld");
		}
		info.setOmschrijving("");;
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Omschrijving niet ingevuld");
		}
		info.setOmschrijving("omschrijving");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Rol identificatie niet ingevuld");
		}
		info.setRolid("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Rol identificatie niet ingevuld");
		}
		info.setRolid("rol");
		info.validate();
	}

}
