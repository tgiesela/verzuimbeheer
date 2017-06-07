package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;

public class TestFactuurkopInfo {

	@Test
	public void test() throws ValidationException {
		FactuurkopInfo info = new FactuurkopInfo();
		info.validate();
	}

}
