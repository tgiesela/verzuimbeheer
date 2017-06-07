package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;

public class TestTariefInfo {

	@Test
	public void test() throws ValidationException {
    	TariefInfo info = new TariefInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals("Zowel werkgever als holding zijn niet ingevuld!",e.getMessage());
		}
		info.setHoldingId(-1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals("Holding niet ingevuld!",e.getMessage());
		}
		info.setHoldingId(null);
		info.setWerkgeverId(-1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals("Werkgever niet ingevuld!",e.getMessage());
		}
		info.setHoldingId(1);
		info.setWerkgeverId(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Zowel werkgever als holding zijn ingevuld!");
		}
		info.setHoldingId(null);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals("Aansluitkostenperiode afwezig", e.getMessage());
		}
		info.setAansluitkostenPeriode(__tariefperiode.MAAND);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals("Abonnementperiode afwezig", e.getMessage());
		}
		info.setAbonnementPeriode(__tariefperiode.MAAND);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals("Ingangsdatum niet ingevuld", e.getMessage());
		}
		info.setIngangsdatum(new Date());
		info.validate();
	}

}
