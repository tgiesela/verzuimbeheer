package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestVerzuimActiviteitInfo {

	@Test
	public void test() throws ValidationException {
    	VerzuimActiviteitInfo info = new VerzuimActiviteitInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Datum activiteit niet ingevuld");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		info.setDatumactiviteit(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Datum activiteit ligt in de toekomst");
		}
		info.setDatumactiviteit(new Date());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Activiteit niet ingevuld");
		}
		info.setActiviteitId(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Verzuim niet ingevuld");
		}
		info.setVerzuimId(1);
		info.validate();
	}

}
