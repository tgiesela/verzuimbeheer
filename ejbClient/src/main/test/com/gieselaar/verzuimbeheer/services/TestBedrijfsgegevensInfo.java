package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;

public class TestBedrijfsgegevensInfo {

	@Test
	public void test() throws ValidationException {
		BedrijfsgegevensInfo info = new BedrijfsgegevensInfo();
		info.validate();
	}
}
