package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurregelbezoekInfo;

public class TestFactuurregelbezoekInfo {

	@Test
	public void test() throws ValidationException {
		FactuurregelbezoekInfo info = new FactuurregelbezoekInfo();
		info.validate();
	}

}
