package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;

public class TestFactuurbetalingInfo {

	@Test
	public void test() throws ValidationException {
		FactuurbetalingInfo info = new FactuurbetalingInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Datum niet ingevuld");
		}
		info.setDatum(new Date());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"FactuurId niet ingevuld");
		}
		info.setFactuurid(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Bedrag niet ingevuld");
		}
		info.setBedrag(new BigDecimal(0));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Bedrag niet ingevuld(=0)");
		}
		info.setBedrag(new BigDecimal(1));
		info.validate();
	}

}
