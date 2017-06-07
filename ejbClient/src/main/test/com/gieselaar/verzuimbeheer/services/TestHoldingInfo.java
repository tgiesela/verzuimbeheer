package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;

public class TestHoldingInfo {

	@Test
	public void test() throws ValidationException {
    	HoldingInfo info = new HoldingInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam holding mag niet leeg zijn");
		}
		info.setNaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam holding mag niet leeg zijn");
		}
		info.setNaam("holdingnaam");
		info.setFactureren(true);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Factuurtype is verplicht als er gefactuurd kan worden");
		}
		info.setFactuurtype(__factuurtype.NVT);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Factuurtype is verplicht als er gefactuurd kan worden");
		}
		info.setFactuurtype(__factuurtype.GESPECIFICEERD);
		AdresInfo vestigingsadres = new AdresInfo();
		info.setVestigingsAdres(vestigingsadres);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Vestigingsadres is verplicht");
		}
		vestigingsadres.setStraat("Binnenweg");
		info.validate();
	}

}
