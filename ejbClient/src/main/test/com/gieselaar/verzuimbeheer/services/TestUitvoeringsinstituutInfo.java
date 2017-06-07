package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;

public class TestUitvoeringsinstituutInfo {

	@Test
	public void test() throws ValidationException {
		UitvoeringsinstituutInfo info = new UitvoeringsinstituutInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam is niet ingevuld");
		}
		info.setNaam("Naam");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Postadres ontbreekt");
		}
		AdresInfo postAdres = new AdresInfo();
		info.setPostadres(postAdres);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Postadres ontbreekt");
		}
		info.getPostadres().setStraat("Binnenweg");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Vestigingsadres ontbreekt");
		}
		AdresInfo vestigingsAdres = new AdresInfo();
		info.setVestigingsadres(vestigingsAdres);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Vestigingsadres ontbreekt");
		}
		info.getVestigingsadres().setStraat("Binnenweg");
		info.validate();
	}

}
