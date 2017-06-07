package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo;

public class TestBtwInfo {

	@Test
	public void test() throws ValidationException {
		BtwInfo info = new BtwInfo();
		info.validate();
	}
}
