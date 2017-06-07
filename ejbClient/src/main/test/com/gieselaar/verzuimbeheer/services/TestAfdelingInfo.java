package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;

public class TestAfdelingInfo {

	@Test
	public void test() throws ValidationException {
		AfdelingInfo info = new AfdelingInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Afdelingnaam niet ingevuld");
		}
		info.setNaam("Afdeling");
		info.validate();
	}

}
