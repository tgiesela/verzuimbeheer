package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;

public class TestActiviteitInfo {

	@Test
	public void test() throws ValidationException {
		ActiviteitInfo info = new ActiviteitInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Naam activiteit mag niet leeg zijn");
		}
		info.setNaam("Activiteit");
		info.validate();
		
		ArrayList<ActiviteitInfo> activiteiten = new ArrayList<>();
		activiteiten.add(info);
		
		ActiviteitInfo info2 = new ActiviteitInfo();
		info2.setNaam("Aactiviteit");
		
		activiteiten.add(info2);
		ActiviteitInfo.sort(activiteiten, ActiviteitInfo.__sortcol.NAAM);
		assertEquals(activiteiten.get(0).getNaam(),"Aactiviteit");
		assertEquals(activiteiten.get(1).getNaam(),"Activiteit");
	}

}
