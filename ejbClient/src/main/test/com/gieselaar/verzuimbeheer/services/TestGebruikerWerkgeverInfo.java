package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestGebruikerWerkgeverInfo {

	@Test
	public void test() throws ValidationException {
		GebruikerWerkgeverInfo info = new GebruikerWerkgeverInfo();
		info.validate();
	}

}
