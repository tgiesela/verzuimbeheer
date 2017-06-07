package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestPakketInfo {

	@Test
	public void test() throws ValidationException {
    	PakketInfo info = new PakketInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam pakket mag niet leeg zijn");
		}
		info.setNaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam pakket mag niet leeg zijn");
		}
		info.setNaam("pakketnaam");
		info.validate();
	}

}
