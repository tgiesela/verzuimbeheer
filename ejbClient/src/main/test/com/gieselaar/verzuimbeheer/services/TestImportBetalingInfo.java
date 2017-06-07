package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestImportBetalingInfo {

	@Test
	public void test() throws ValidationException {
		ImportBetalingInfo info1 = new ImportBetalingInfo();
		info1.validate();
	}

}
