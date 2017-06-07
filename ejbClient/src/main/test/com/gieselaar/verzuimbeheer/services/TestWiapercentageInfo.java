package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestWiapercentageInfo {

	@Test
	public void test() throws ValidationException {
		WiapercentageInfo info = new WiapercentageInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Ingangsdatum niet ingevuld");
		}
		info.setStartdatum(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE,-1);
		info.setEinddatum(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Einddatum ligt voor ingangsdatum");
		}
		info.setEinddatum(null);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werknemer niet ingevuld");
		}
		info.setWerknemerId(1);
		info.validate();
	}

}
