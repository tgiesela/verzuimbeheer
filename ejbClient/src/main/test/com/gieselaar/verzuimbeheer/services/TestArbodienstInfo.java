package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;

public class TestArbodienstInfo {

	@Test
	public void test() throws ValidationException {
		ArbodienstInfo info = new ArbodienstInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam niet ingevuld");
		}
		info.setNaam("Naam");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Postadres ontbreekt");
		}
		AdresInfo postAdres = new AdresInfo();
		info.setPostAdres(postAdres);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Vestigingsadres ontbreekt");
		}
		AdresInfo vestigingsAdres = new AdresInfo();
		info.setVestigingsAdres(vestigingsAdres);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Contactpersoon ontbreekt");
		}
		ContactpersoonInfo contact = new ContactpersoonInfo();
		info.setContactpersoon(contact);
		info.validate();
	}

}
