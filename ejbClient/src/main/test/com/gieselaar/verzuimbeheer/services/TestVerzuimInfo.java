package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;

public class TestVerzuimInfo {

	@Test
	public void test() throws ValidationException {
		VerzuimInfo info = new VerzuimInfo();
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werknemer en/of dienstverband ontbreekt");
		}
		info.setWerknemer(new WerknemerInfo());
		info.setDienstverband(new DienstverbandInfo());
		info.getWerknemer().setGeslacht(__geslacht.MAN);
		info.setVangnettype(__vangnettype.ZIEKDOORZWANGERSCHAP);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werknemer is man en vangnettype is zwangerschap");
		}
		info.setVangnettype(__vangnettype.ZWANGERSCHAP);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Werknemer is man en vangnettype is zwangerschap");
		}
		info.getWerknemer().setGeslacht(__geslacht.VROUW);
		info.setEinddatumverzuim(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 10);
		info.setStartdatumverzuim(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Startdatum verzuim ligt na einddatum verzuim");
		}
		info.setStartdatumverzuim(info.getEinddatumverzuim());
		info.setEinddatumverzuim(cal.getTime());
		info.setCascode(0);
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Reden verzuim (Cascode) niet ingevuld");
		}
		info.setCascode(1);
		cal.add(Calendar.DATE, -20);
		info.getDienstverband().setEinddatumcontract(cal.getTime());
		try {
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e) {
			assertEquals(e.getMessage(),"Startdatum verzuim ligt na einddatum dienstverband.");
		}
		info.getDienstverband().setEinddatumcontract(null);
		info.validate();
		
		VerzuimInfo info2 = new VerzuimInfo();
		info2.setStartdatumverzuim(cal.getTime());
		
		ArrayList<VerzuimInfo> verzuimen = new ArrayList<>();
		verzuimen.add(info);
		verzuimen.add(info2);
		VerzuimInfo.sort(verzuimen, VerzuimInfo.__sortcol.STARTDATUM);
		assertEquals(verzuimen.get(0),info2);
		assertEquals(verzuimen.get(1), info);
	}

}
