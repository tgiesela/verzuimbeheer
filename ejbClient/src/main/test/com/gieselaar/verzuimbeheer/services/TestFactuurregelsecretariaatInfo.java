package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurregelsecretariaatInfo;

public class TestFactuurregelsecretariaatInfo {

	@Test
	public void test() throws ValidationException {
		FactuurregelsecretariaatInfo info = new FactuurregelsecretariaatInfo();
		info.validate();
	}

}
