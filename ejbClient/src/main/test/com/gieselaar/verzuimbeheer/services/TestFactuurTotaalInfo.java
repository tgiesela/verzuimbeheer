package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo.__sortcol;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class TestFactuurTotaalInfo {

	@Test
	public void test() throws ValidationException {
		FactuurTotaalInfo info1 = new FactuurTotaalInfo();
		info1.setHoldingid(23);
		info1.setWerkgever(new WerkgeverInfo());
		info1.getWerkgever().setNaam("AAAA");
		info1.validate();
		FactuurTotaalInfo info2 = new FactuurTotaalInfo();
		info2.setHoldingid(12);
		info2.setWerkgever(new WerkgeverInfo());
		info2.getWerkgever().setNaam("BBBB");
		
		ArrayList<FactuurTotaalInfo> totalen = new ArrayList<>(); 
		totalen.add(info1);
		totalen.add(info2);
		
		FactuurTotaalInfo.sort(totalen, __sortcol.HOLDINGID);
		assertEquals(totalen.get(0).getHoldingid().intValue(),12);
		assertEquals(totalen.get(1).getHoldingid().intValue(),23);
		FactuurTotaalInfo.sort(totalen, __sortcol.NAAM);
		assertEquals(totalen.get(0).getWerkgever().getNaam(),"AAAA");
		assertEquals(totalen.get(1).getWerkgever().getNaam(),"BBBB");
		
		
	}

}
