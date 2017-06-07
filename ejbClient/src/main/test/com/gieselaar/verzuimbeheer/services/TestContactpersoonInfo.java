package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;

public class TestContactpersoonInfo {

	@Test
	public void test() throws ValidationException  {
		ContactpersoonInfo info = new ContactpersoonInfo();
		info.validate(); /* Empty contactpersoon toegestaan */
		
		info.setAchternaam("");
		info.setVoornaam("Test");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Achternaam niet ingevuld");
		}
		info.setAchternaam("Achternaam");
		info.setGeslacht(__geslacht.ONBEKEND);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"geslacht moet MAN of VROUW zijn");
		}
		info.setGeslacht(__geslacht.MAN);
		info.validate();

	}
}
