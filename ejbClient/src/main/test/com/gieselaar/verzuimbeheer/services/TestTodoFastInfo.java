package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestTodoFastInfo {

	@Test
	public void test() throws ValidationException {
    	TodoFastInfo info = new TodoFastInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Deadline niet ingevuld");
		}
		info.setDeadlinedatum(new Date());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Verzuim niet ingevuld");
		}
		info.setVerzuimId(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Activiteit niet ingevuld");
		}
		info.setActiviteitId(1);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Waarschuwingsdatum niet ingevuld");
		}
		info.setWaarschuwingsdatum(new Date());
		info.validate();
	}

}
