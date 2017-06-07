package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestSettingsInfo {

	@Test
	public void test() throws ValidationException {
		SettingsInfo info1 = new SettingsInfo();
		info1.validate();
	}

}
