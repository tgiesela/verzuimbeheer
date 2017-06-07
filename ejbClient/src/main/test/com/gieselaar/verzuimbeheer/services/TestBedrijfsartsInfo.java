package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;

public class TestBedrijfsartsInfo {

	@Test
	public void test() throws ValidationException {
		BedrijfsartsInfo info = new BedrijfsartsInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Achternaam niet ingevuld");
		}
		info.setAchternaam("Achternaam");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Arbodienst niet ingevuld");
		}
		info.setArbodienstId(-1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Arbodienst niet ingevuld");
		}
		info.setArbodienstId(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"geslacht moet MAN of VROUW zijn");
		}
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
