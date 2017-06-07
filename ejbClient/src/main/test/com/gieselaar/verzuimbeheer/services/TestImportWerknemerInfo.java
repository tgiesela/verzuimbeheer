package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ImportWerknemerInfo.__sortcol;

public class TestImportWerknemerInfo {

	@Test
	public void test() throws ValidationException {
    	ImportResult rslt1 = new ImportResult();
    	rslt1.setResult(10);
    	ImportResult rslt2 = new ImportResult();
    	rslt2.setResult(9);
    	rslt1.setImportok(false);
    	rslt2.setImportok(false);
		
		
		ImportWerknemerInfo info1 = new ImportWerknemerInfo();
		info1.validate();
		info1.setBurgerservicenummer("033333330");
    	ImportWerknemerInfo info2 = new ImportWerknemerInfo();
		info2.validate();
		info2.setBurgerservicenummer("022222220");
		ArrayList<ImportWerknemerInfo> werknemers = new ArrayList<>();
		werknemers.add(info1);
		werknemers.add(info2);
		ImportWerknemerInfo.sort(werknemers, __sortcol.BSN);
		assertEquals(werknemers.get(0),info2);
		assertEquals(werknemers.get(1),info1);
		
		info1.setImportresult(rslt2);
		info2.setImportresult(rslt1);
		
		
		ImportWerknemerInfo.sort(werknemers, __sortcol.DATUM);
		
		/*
		 * ImpRslt	ImpOk 	BSN		Datum	Seqnr	ResultingOrder
		 * 10		T	  	3333	Now()	2		3
		 * 10		T	  	3333	Now()	1		2
		 * 10		T		2222    Now()-1	3		1
		 * 
		 */
		ImportWerknemerInfo info3 = new ImportWerknemerInfo();
		info1.setBurgerservicenummer("033333330");
		info1.setDatumindienst(new Date());
		info1.setImportresult(rslt1);
		info1.setSequencenr(2);
		info2.setBurgerservicenummer("033333330");
		info2.setDatumindienst(new Date());
		info2.setSequencenr(1);
		info3.setBurgerservicenummer("022222220");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		info3.setDatumindienst(cal.getTime());
		info3.setSequencenr(3);
    	rslt1.setImportok(true);
		info2.setImportresult(rslt1);
		info3.setImportresult(rslt1);
		werknemers.clear();
		werknemers.add(info1);
		werknemers.add(info2);
		werknemers.add(info3);
		ImportWerknemerInfo.sort(werknemers, __sortcol.DATUM);
		
		assertEquals(werknemers.get(0),info3);
		assertEquals(werknemers.get(1),info2);
		assertEquals(werknemers.get(2),info1);
	
	}

}
