package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;

public class TestVerzuimHerstelInfo {

	@Test
	public void test() throws ValidationException {
		VerzuimHerstelInfo info = new VerzuimHerstelInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Herstelpercentage LW en Herstelpecentage AT kunnen niet beide leeg zijn");
		}
		info.setPercentageHerstelAT(new BigDecimal(0));
		info.setPercentageHerstel(new BigDecimal(101));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Herstelpercentage LW mag maximaal 100% zijn");
		}
		info.setPercentageHerstel(new BigDecimal(-1));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Herstelpercentage LW moet groter of gelijk aan 0% zijn");
		}
		info.setPercentageHerstel(new BigDecimal(50));
		info.setPercentageHerstelAT(new BigDecimal(101));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Herstelpercentage AT mag maximaal 100% zijn");
		}
		info.setPercentageHerstelAT(new BigDecimal(-1));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Herstelpercentage AT moet groter of gelijk aan 0% zijn");
		}
		info.setPercentageHerstelAT(new BigDecimal(50));
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Meldingswijze moet ingevuld zijn");
		}
		info.setMeldingswijze(__meldingswijze.TELEFOON);
		info.validate();
		
		info.setDatumHerstel(new Date());
		VerzuimHerstelInfo info2 = new VerzuimHerstelInfo();
		info2.setPercentageHerstelAT(new BigDecimal(0));
		info2.setPercentageHerstel(new BigDecimal(100));
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -10);
		info2.setDatumHerstel(cal.getTime());
		
		ArrayList<VerzuimHerstelInfo> herstellen = new ArrayList<>();
		herstellen.add(info);
		herstellen.add(info2);
		VerzuimHerstelInfo.sort(herstellen, VerzuimHerstelInfo.__sortcol.HERSTELDATUM);
		assertEquals(herstellen.get(0),info2);
		assertEquals(herstellen.get(1), info);
	}

}
