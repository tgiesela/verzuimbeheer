package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.services.AdresInfo;

public class TestAdresInfo {

	@Test
	public void testExtractHuisnummerfromStraat() {
		AdresInfo adres = new AdresInfo();
		AdresInfo.extractHuisnummerfromStraat("Binnenweg188a", adres);
		assertEquals(adres.getStraat(),"Binnenweg188a");
		assertEquals(adres.getHuisnummer(),"");
		assertEquals(adres.getHuisnummertoevoeging(),"");
		AdresInfo.extractHuisnummerfromStraat("Binnenweg 188a", adres);
		assertEquals(adres.getStraat(),"Binnenweg");
		assertEquals(adres.getHuisnummer(),"188");
		assertEquals(adres.getHuisnummertoevoeging(),"a");
		AdresInfo.extractHuisnummerfromStraat("Binnenweg 188 a", adres);
		assertEquals(adres.getStraat(),"Binnenweg");
		assertEquals(adres.getHuisnummer(),"188");
		assertEquals(adres.getHuisnummertoevoeging(),"a");
		AdresInfo.extractHuisnummerfromStraat("Binnenweg 188", adres);
		assertEquals(adres.getStraat(),"Binnenweg");
		assertEquals(adres.getHuisnummer(),"188");
		assertEquals(adres.getHuisnummertoevoeging(),"");
	}

}
