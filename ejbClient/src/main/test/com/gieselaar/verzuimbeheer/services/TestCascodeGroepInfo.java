package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;

public class TestCascodeGroepInfo {

	@Test
	public void test() throws ValidationException {
		CascodeGroepInfo info = new CascodeGroepInfo();
		try{
			info.validate();
		} catch (ValidationException e){
			assertEquals("Naam mag niet leeg zijn", e.getMessage());
		}
		info.setNaam("Naam");
	}
}
