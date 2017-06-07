package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

public class TestGebruikerInfo {

	@Test
	public void test() throws ValidationException {
		GebruikerInfo info = new GebruikerInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Achternaam is niet ingevuld");
		}
		info.setAchternaam("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Achternaam is niet ingevuld");
		}
		info.setAchternaam("gieselaar");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Emailadres is niet ingevuld");
		}
		info.setEmailadres("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Emailadres is niet ingevuld");
		}
		info.setEmailadres("a@b.nl");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Gebruikersnaam is niet ingevuld");
		}
		info.setName("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Gebruikersnaam is niet ingevuld");
		}
		info.setName("gebruiker");
		info.setAduser(true);
		info.setDomainname("");
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Domainname niet ingevuld voor AD user");
		}
		info.setDomainname("domain.local");
		info.validate();
	}

}
