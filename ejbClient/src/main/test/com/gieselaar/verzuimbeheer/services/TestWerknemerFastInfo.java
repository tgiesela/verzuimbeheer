package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class TestWerknemerFastInfo {

	@Test
	public void test() throws ValidationException {
		WerknemerFastInfo info1 = new WerknemerFastInfo();
		WerkgeverInfo wgr1 = new WerkgeverInfo();
		wgr1.setNaam("A werkgever");
		info1.setAchternaam("B werknemer");
		info1.setBurgerservicenummer("11111111");
		info1.setWerkgever(wgr1);
		info1.setWerkgevernaam(wgr1.getNaam());
		info1.validate();
		
		WerknemerFastInfo info2 = new WerknemerFastInfo();
		WerkgeverInfo wgr2 = new WerkgeverInfo();
		wgr2.setNaam("B werkgever");
		info2.setAchternaam("A werknemer");
		info2.setBurgerservicenummer("22222222");
		info2.setWerkgever(wgr2);
		info2.setWerkgevernaam(wgr2.getNaam());
		
		ArrayList<WerknemerFastInfo> werknemers = new ArrayList<>();
		werknemers.add(info1);
		werknemers.add(info2);
		WerknemerFastInfo.sort(werknemers, WerknemerFastInfo.__sortcol.ACHTERNAAM);
		assertEquals(werknemers.get(0),info2);
		assertEquals(werknemers.get(1), info1);
		WerknemerFastInfo.sort(werknemers, WerknemerFastInfo.__sortcol.BSN);
		assertEquals(werknemers.get(0),info1);
		assertEquals(werknemers.get(1), info2);
		WerknemerFastInfo.sort(werknemers, WerknemerFastInfo.__sortcol.WERKGEVERNAAM);
		assertEquals(werknemers.get(0),info1);
		assertEquals(werknemers.get(1), info2);
	}

}
