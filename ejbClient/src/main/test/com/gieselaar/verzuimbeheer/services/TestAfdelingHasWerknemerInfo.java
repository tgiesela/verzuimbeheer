package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;

public class TestAfdelingHasWerknemerInfo {

	@Test
	public void test() throws ValidationException {
		AfdelingHasWerknemerInfo info = new AfdelingHasWerknemerInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Ingangsdatum afdeling niet ingevuld");
		}
		info.setStartdatum(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(info.getStartdatum());
		cal.add(Calendar.DATE, -1);
		info.setEinddatum(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Einddatum afdeling ligt voor ingangsdatum");
		}
		
		cal = Calendar.getInstance();
		cal.setTime(info.getStartdatum());
		cal.add(Calendar.DATE, +1);
		info.setEinddatum(cal.getTime());
		info.validate();
		info.setEinddatum(null);
		info.validate();
	}

}
