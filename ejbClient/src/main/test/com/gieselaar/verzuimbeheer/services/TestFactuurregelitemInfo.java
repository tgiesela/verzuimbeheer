package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurregelitemInfo;

public class TestFactuurregelitemInfo {

	@Test
	public void test() throws ValidationException {
		FactuurregelitemInfo info = new FactuurregelitemInfo();
		info.validate();
	}

}
