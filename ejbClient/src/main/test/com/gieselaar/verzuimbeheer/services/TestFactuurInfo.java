package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurInfo;

public class TestFactuurInfo {

	@Test
	public void test() throws ValidationException {
		FactuurInfo info = new FactuurInfo();
		info.validate();
	}

}
