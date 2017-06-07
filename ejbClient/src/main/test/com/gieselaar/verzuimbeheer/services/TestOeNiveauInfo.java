package com.gieselaar.verzuimbeheer.services;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestOeNiveauInfo {

	@Test
	public void test() throws ValidationException {
		OeNiveauInfo info = new OeNiveauInfo();
		info.validate();
	}

}
