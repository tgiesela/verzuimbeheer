package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;

public class TestFactuurcategorieInfo {

	@Test
	public void test() throws ValidationException {
		FactuurcategorieInfo info = new FactuurcategorieInfo();
		info.validate();
	}

}
