package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;

public class TestWerknemerInfo {

	public static Date getDate(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		return cal.getTime();
	}
	@Test
	public void test() throws ValidationException {
		WerknemerInfo info1 = new WerknemerInfo();
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Burgerservicenummer niet ingevuld");
		}
		info1.setBurgerservicenummer("");
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Burgerservicenummer niet ingevuld");
		}
		info1.setBurgerservicenummer("040823386");
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Naam mag niet leeg zijn");
		}
		info1.setAchternaam("");
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Naam mag niet leeg zijn");
		}
		info1.setAchternaam("achternaam");
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Geboortedatum niet ingevuld");
		}
		info1.setGeboortedatum(new Date());
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Dienstverband informatie ontbreekt");
		}
		ArrayList<DienstverbandInfo> dienstverbanden = new ArrayList<>();
		DienstverbandInfo dvb1 = new DienstverbandInfo();
		dvb1.setStartdatumcontract(new Date());
		info1.setDienstVerbanden(dienstverbanden);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Dienstverband informatie ontbreekt");
		}
		dienstverbanden.add(dvb1);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Afdeling niet ingevuld");
		}
		ArrayList<AfdelingHasWerknemerInfo> afdelingen = new ArrayList<>();
		info1.setAfdelingen(afdelingen);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Afdeling niet ingevuld");
		}
		AfdelingHasWerknemerInfo ahw1 = new AfdelingHasWerknemerInfo();
		afdelingen.add(ahw1);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"geslacht moet MAN of VROUW zijn");
		}
		info1.setGeslacht(__geslacht.MAN);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Wia informatie ontbreekt");
		}
		ArrayList<WiapercentageInfo> wiainfo = new ArrayList<WiapercentageInfo>();
		info1.setWiaPercentages(wiainfo);
		WiapercentageInfo wia1 = new WiapercentageInfo();
		wiainfo.add(wia1);
		Calendar firstDate = Calendar.getInstance();
		firstDate.set(Calendar.YEAR,1900);
		firstDate.set(Calendar.MONTH,1);
		firstDate.set(Calendar.DAY_OF_MONTH,1);
		info1.setGeboortedatum(firstDate.getTime());
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Geboortedatum ligt te ver in het verleden");
		}
		info1.setGeboortedatum(new Date());
/*
 * Dienstverbanden
 */
		
		DienstverbandInfo dvb2 = new DienstverbandInfo();
		dienstverbanden.add(dvb2);
		dvb2.setStartdatumcontract(firstDate.getTime());
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"meer dan 1 actief dienstverband");
		}
		dvb1.setEinddatumcontract(new Date());
		/*
		 * dvb1	startdatum = now()		einddatum = now()
		 * dvb2 startdatum = 1900		einddatum = null
		 */
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Oude niet afgesloten dienstverbanden gevonden");
		}
		
		firstDate.setTime(new Date());
		firstDate.add(Calendar.YEAR, 1);
		dvb2.setEinddatumcontract(new Date());
		/*
		 * dvb1	startdatum = now()		einddatum = now()
		 * dvb2 startdatum = 1900		einddatum = + 1 year
		 * 
		 * dvb1 starts and end during dvb2!
		 */
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Overlappende dienstverbanden");
		}
		dienstverbanden.remove(0);
/*
 * Afdelingen
 */
		
		AfdelingHasWerknemerInfo ahw2 = new AfdelingHasWerknemerInfo();
		afdelingen.add(ahw2);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Meer dan 1 niet afgesloten afdeling");
		}
		ahw2.setAction(persistenceaction.DELETE);
		ahw1.setAction(persistenceaction.DELETE);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Geen afdelingen gevonden bij werknemer");
		}
		ahw2.setAction(persistenceaction.INSERT);
		ahw1.setAction(persistenceaction.INSERT);
		ahw1.setStartdatum(new Date());
		ahw1.setEinddatum(new Date());
		firstDate.set(Calendar.YEAR,1900);
		firstDate.set(Calendar.MONTH,1);
		firstDate.set(Calendar.DAY_OF_MONTH,1);
		ahw2.setStartdatum(firstDate.getTime());
		ahw2.setEinddatum(null);
		/*
		 * ahw1	startdatum = now()		einddatum = now()
		 * ahw2 startdatum = 1900		einddatum = null
		 */
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Oude niet afgesloten afdeling gevonden");
		}
		firstDate.setTime(new Date());
		firstDate.add(Calendar.YEAR, 1);
		ahw2.setEinddatum(new Date());
		/*
		 * ahw1	startdatum = now()		einddatum = now()
		 * ahw2 startdatum = 1900		einddatum = + 1 year
		 * 
		 * ahw1 starts and end during dvb2!
		 */
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Overlappende afdelingen");
		}
		info1.getLaatsteDienstverband().setEinddatumcontract(null);
		ahw1.setEinddatum(new Date());

		ahw1.setStartdatum(getDate(2015,6,1));
		ahw1.setEinddatum(getDate(2016,6,1));
		ahw2.setStartdatum(getDate(2016,6,1));
		ahw2.setEinddatum(null);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Overlappende afdelingen");
		}
		ahw2.setAction(persistenceaction.DELETE);
		ahw1.setEinddatum(null);
		AfdelingHasWerknemerInfo laatste = info1.getLaatsteAfdeling();
		assertEquals(laatste.getStartdatum(), ahw1.getStartdatum());
		info1.validate();
		
		ahw1.setEinddatum(new Date());
		afdelingen.remove(1);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Geen huidige afdeling gevonden bij werknemer");
		}
		afdelingen.get(0).setEinddatum(null);
		
/*
 * Wia percentages
 */
				
		WiapercentageInfo wia2 = new WiapercentageInfo();
		wiainfo.add(wia2);
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Meer dan 1 wiaperiodes zonder einddatum");
		}
		wia2.setAction(persistenceaction.DELETE);
		wia1.setAction(persistenceaction.DELETE);
		wia1.setStartdatum(new Date());
		wia2.setStartdatum(new Date());
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Geen wiaperiodes gevonden bij werknemer");
		}
		wia2.setAction(persistenceaction.INSERT);
		wia1.setAction(persistenceaction.INSERT);
		wia1.setStartdatum(new Date());
		wia1.setEinddatum(new Date());
		firstDate.set(Calendar.YEAR,1900);
		firstDate.set(Calendar.MONTH,1);
		firstDate.set(Calendar.DAY_OF_MONTH,1);
		wia2.setStartdatum(firstDate.getTime());
		wia2.setEinddatum(null);
		/*
		 * wia1	startdatum = now()		einddatum = now()
		 * wia2 startdatum = 1900		einddatum = null
		 */
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Oudere niet afgesloten wia periode gevonden");
		}
		firstDate.setTime(new Date());
		firstDate.add(Calendar.YEAR, 1);
		wia2.setEinddatum(new Date());
		/*
		 * ahw1	startdatum = now()		einddatum = now()
		 * ahw2 startdatum = 1900		einddatum = + 1 year
		 * 
		 * ahw1 starts and end during dvb2!
		 */
		try{
			info1.validate();
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(e.getMessage(),"Overlappende wiaperiodes");
		}
		info1.getLaatsteDienstverband().setEinddatumcontract(null);
		wia1.setEinddatum(new Date());
		wiainfo.remove(1);
		info1.validate();
		
		
		WerkgeverInfo wgr1 = new WerkgeverInfo();
		wgr1.setNaam("A werkgever");
		info1.setAchternaam("B werknemer");
		info1.setBurgerservicenummer("11111111");
		info1.setWerkgever(wgr1);
		
		WerknemerInfo info2 = new WerknemerInfo();
		WerkgeverInfo wgr2 = new WerkgeverInfo();
		wgr2.setNaam("B werkgever");
		info2.setAchternaam("A werknemer");
		info2.setBurgerservicenummer("22222222");
		info2.setWerkgever(wgr2);
		
		ArrayList<WerknemerInfo> werknemers = new ArrayList<>();
		werknemers.add(info1);
		werknemers.add(info2);
		WerknemerInfo.sort(werknemers, WerknemerInfo.__sortcol.ACHTERNAAM);
		assertEquals(werknemers.get(0),info2);
		assertEquals(werknemers.get(1), info1);
		WerknemerInfo.sort(werknemers, WerknemerInfo.__sortcol.BSN);
		assertEquals(werknemers.get(0),info1);
		assertEquals(werknemers.get(1), info2);
	}

}
