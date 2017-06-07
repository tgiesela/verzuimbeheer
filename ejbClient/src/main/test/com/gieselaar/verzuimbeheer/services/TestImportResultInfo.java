package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestImportResultInfo {

	@Test
	public void test() throws ValidationException {
		ImportResult info1 = new ImportResult();
		info1.validate();
	}

}
