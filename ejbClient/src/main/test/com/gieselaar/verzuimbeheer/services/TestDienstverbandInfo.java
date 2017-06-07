package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;

public class TestDienstverbandInfo {

	@Test
	public void test() throws ValidationException  {
		DienstverbandInfo info = new DienstverbandInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Ingangsdatum niet ingevuld");
		}
		info.setStartdatumcontract(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(info.getStartdatumcontract());
		cal.add(Calendar.DATE, -1);
		info.setEinddatumcontract(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Einddatum ligt voor ingangsdatum");
		}
		cal.set(Calendar.YEAR, 1949);
		info.setStartdatumcontract(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Ingangsdatum dienstverband ligt voor 1950!");
		}
		info.setStartdatumcontract(new Date());
		info.setEinddatumcontract(null);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werkgever niet ingevuld");
		}
		info.setWerkgeverId(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werkweek niet ingevuld");
		}
		info.setWerkweek(new BigDecimal(0));
		info.validate();
	}
}
