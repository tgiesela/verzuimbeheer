package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestVerzuimMedischekaartInfo {

	@Test
	public void test() throws ValidationException {
    	VerzuimMedischekaartInfo info = new VerzuimMedischekaartInfo();
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
