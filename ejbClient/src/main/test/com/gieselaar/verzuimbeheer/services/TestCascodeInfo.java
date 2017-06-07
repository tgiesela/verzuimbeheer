package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;

public class TestCascodeInfo {

	@Test
	public void test() throws ValidationException {
		CascodeInfo info = new CascodeInfo();
		try{
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e){
			assertEquals("Vangnettype mag niet leeg zijn",e.getMessage());
		}
		info.setVangnettype(__vangnettype.NVT);
		try{
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e){
			assertEquals("Cascode mag niet leeg zijn",e.getMessage());
		}
		info.setCascode("");
		try{
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e){
			assertEquals("Cascode mag niet leeg zijn",e.getMessage());
		}
		info.setCascode("CC");
		try{
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e){
			assertEquals("Omschrijving mag niet leeg zijn",e.getMessage());
		}
		info.setOmschrijving("");
		try{
			info.validate();
			throw new RuntimeException("Should not arrive here");
		} catch (ValidationException e){
			assertEquals("Omschrijving mag niet leeg zijn",e.getMessage());
		}
		info.setOmschrijving("laatste");
		CascodeInfo info2 = new CascodeInfo();
		info2.setOmschrijving("eerste");
		ArrayList<CascodeInfo> cascodes = new ArrayList<>();
		cascodes.add(info);
		cascodes.add(info2);
		CascodeInfo.sort(cascodes, CascodeInfo.__sortcol.NAAM);
		assertEquals(cascodes.get(0).getOmschrijving(),"eerste");
		assertEquals(cascodes.get(1).getOmschrijving(),"laatste");
		
	}
}
