package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestVerzuimDocumentInfo {

	@Test
	public void test() throws ValidationException {
		VerzuimDocumentInfo info = new VerzuimDocumentInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Documentnaam niet ingevuld");
		}
		info.setDocumentnaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Documentnaam niet ingevuld");
		}
		info.setDocumentnaam("documentnaam");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Document niet ingevuld");
		}
		info.setPadnaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Document niet ingevuld");
		}
		info.setPadnaam("document");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Documentomschrijving niet ingevuld");
		}
		info.setOmschrijving("");
		info.validate();
	}

}
