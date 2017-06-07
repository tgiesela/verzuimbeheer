package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestWerkgeverInfo {

	@Test
	public void test() throws ValidationException {
		WerkgeverInfo info = new WerkgeverInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam werkgever mag niet leeg zijn");
		}
		info.setNaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam werkgever mag niet leeg zijn");
		}
		info.setNaam("werkgever");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Startdatum mag niet leeg zijn");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 10);
		info.setStartdatumcontract(cal.getTime());
		info.setEinddatumcontract(new Date());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Startdatum moet voor einddatum liggen");
		}
		info.setEinddatumcontract(null);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Vestigingsadres is verplicht");
		}
		AdresInfo vestigingsadres = new AdresInfo();
		info.setVestigingsAdres(vestigingsadres);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Vestigingsadres is verplicht");
		}
		vestigingsadres.setStraat("Binnenweg");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werkweek is verplicht");
		}
		info.setWerkweek(new BigDecimal(38));
		info.validate();
		
		WerkgeverInfo info2 = new WerkgeverInfo();
		info2.setNaam("Eerste werkgever");
		
		ArrayList<WerkgeverInfo> werkgevers = new ArrayList<>();
		werkgevers.add(info);
		werkgevers.add(info2);
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		assertEquals(werkgevers.get(0),info2);
		assertEquals(werkgevers.get(1), info);
	}

}
