package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.gieselaar.verzuimbeheer.entities.Adres;
import com.gieselaar.verzuimbeheer.entities.Dienstverband;
import com.gieselaar.verzuimbeheer.entities.Factuurcategorie;
import com.gieselaar.verzuimbeheer.entities.Factuuritem;
import com.gieselaar.verzuimbeheer.entities.Factuurkop;
import com.gieselaar.verzuimbeheer.entities.Holding;
import com.gieselaar.verzuimbeheer.entities.Tarief;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.entities.Werkzaamheden;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__huisbezoekurgentie;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__verzuimsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

@RunWith(Arquillian.class)
public class TestFactuurBean{

	private int wgrzonderholding;
	private int holdingzonderfactureren;
	private int wgrmetholding;
	private int holdinggeaggregeerd;
	private int wgrmetholdingfactureren;
	private int holdingseparaat;
	private TestUtils tu;
	
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(FactuurBean.class)
				.addClass(WerkgeverBean.class)
				.addClass(TariefBean.class)
				.addClass(FactuurConversion.class)
				.addClass(WerkgeverConversion.class)
				.addClass(AfdelingBean.class)
				.addClass(AdresConversion.class)
				.addClass(PakketBean.class)
				.addClass(PakketConversion.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(WerknemerBean.class)
				.addClass(WerknemerConversion.class)
				.addClass(InstantieBean.class)
				.addClass(InstantieConversion.class)
				.addClass(ContactpersoonBean.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
	public TestFactuurBean(){
	}
    @EJB FactuurBean bean;
    @EJB WerkgeverBean wgbean;
	private void emptyTableBtw() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Btw").executeUpdate();
		utx.commit();
	}
	private void emptyTableFactuurbetaling() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Factuurbetaling").executeUpdate();
		utx.commit();
	}
	private void emptyTableFactuurcategorie() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Factuurcategorie").executeUpdate();
		utx.commit();
	}
	private void emptyTableFactuurkop() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Factuurkop").executeUpdate();
		utx.commit();
	}
	private void emptyTableFactuuritem() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Factuuritem").executeUpdate();
		utx.commit();
	}
	private void emptyTableWerkzaamheden() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Werkzaamheden").executeUpdate();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Holding").executeUpdate();
		em.createQuery("delete from Tarief").executeUpdate();
		utx.commit();
	}
	private void emptyTableFacturen() throws Exception, SystemException{
		this.
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Factuur").executeUpdate();
		em.createQuery("delete from Factuurregelbezoek").executeUpdate();
		em.createQuery("delete from Factuurregelitem").executeUpdate();
		em.createQuery("delete from Factuurregelsecretariaat").executeUpdate();
		utx.commit();
	}
	private Integer btwCount() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		Long count = (Long) em.createQuery("Select count(a) from Btw a").getSingleResult();
		utx.commit();
		return count.intValue();
	}
	@Test
	public void crudTestBtw() throws SystemException, Exception{
		emptyTableBtw();
		BtwInfo a1 = addBtw(__btwtariefsoort.LAAG, TestUtils.getDate(2015,1,1), null, 6);
		BtwInfo a2 = bean.getBtwById(a1.getId());
		assertEquals(a1.getId(),a2.getId());

		BtwInfo a4 = addBtw(__btwtariefsoort.HOOG, TestUtils.getDate(2010,01,01),TestUtils.getDate(2014,12,31),19.0);
		
		a2 = addBtw(__btwtariefsoort.HOOG, TestUtils.getDate(2015,1,1), null,20.0);
		a2.setPercentage(new BigDecimal(21));
		bean.updateBtw(a2);
		a2 = bean.getBtwById(a2.getId());

		BtwInfo a3 = addBtw(__btwtariefsoort.NVT, TestUtils.getDate(2015,1,1), null,0.0);
		a3 = bean.getBtwById(a3.getId());
		assertEquals(0.0,a3.getPercentage().doubleValue(),0);
		
		BtwInfo actueel = bean.getActueelBtwPercentage(__btwtariefsoort.HOOG);
		assertEquals(actueel.getId(),a2.getId());
		assertEquals(21.0, actueel.getPercentage().doubleValue(),0);
		
		bean.deleteBtw(a4);
		assertEquals((Integer)3, btwCount());
		/*
		 * Nu zitten alleen de actuele percentages er nog in
		 */
	}
	@Test
	public void crudTestFactuurbetaling() throws SystemException, Exception{
		emptyTableFactuurbetaling();
		FactuurbetalingInfo fb1 = addFactuurbetaling(12.34, 1);
		fb1.setRekeningnummerbetaler("0222222220");
		bean.updateFactuurbetaling(fb1);
		ArrayList<FactuurbetalingInfo> list = (ArrayList<FactuurbetalingInfo>) bean.getFactuurbetalingen();
		assertEquals(1,list.size());
		assertEquals("0222222220", list.get(0).getRekeningnummerbetaler());
		list = (ArrayList<FactuurbetalingInfo>) bean.getFactuurbetalingenForFactuur(1);
		assertEquals(1,list.size());
		for (FactuurbetalingInfo fbi: list){
			bean.deleteFactuurbetaling(fbi);
		}
	}
	@Test
	public void crudTestFactuurcategorie() throws SystemException, Exception{
		emptyTableFactuurcategorie();
		FactuurcategorieInfo fb1 = addFactuurcategorie(1, true, "Bedrijfsarts");
		fb1.setOmschrijving("Bedrijfsartsen");
		bean.updateFactuurcategorie(fb1);
		ArrayList<FactuurcategorieInfo> list = (ArrayList<FactuurcategorieInfo>) bean.getFactuurcategorien();
		assertEquals(1,list.size());
		assertEquals("Bedrijfsartsen", list.get(0).getOmschrijving());
		for (FactuurcategorieInfo fbi: list){
			bean.deleteFactuurcategorie(fbi);
		}
		list = (ArrayList<FactuurcategorieInfo>) bean.getFactuurcategorien();
		assertEquals(0,list.size());
	}
	@Test
	public void crudTestFactuurkop() throws SystemException, Exception{
		emptyTableFactuurkop();
		FactuurkopInfo fb1 = addFactuurkop("KOP", 1);
		fb1.setOmschrijving("Bedrijfsartsen");
		bean.updateFactuurkop(fb1);
		ArrayList<FactuurkopInfo> list = (ArrayList<FactuurkopInfo>) bean.getFactuurkoppen();
		assertEquals(1,list.size());
		assertEquals("Bedrijfsartsen", list.get(0).getOmschrijving());
		for (FactuurkopInfo fbi: list){
			bean.deleteFactuurkop(fbi);
		}
		list = (ArrayList<FactuurkopInfo>) bean.getFactuurkoppen();
		assertEquals(0,list.size());
	}
	@Test
	public void crudTestFactuuritem() throws SystemException, Exception{
		emptyTableFactuuritem();
		FactuuritemInfo fb1 = addFactuuritem("item", 12.34, new Date(),1,null, 1);
		fb1.setOmschrijving("Bedrijfsartsen");
		bean.updateFactuuritem(fb1);
		ArrayList<FactuuritemInfo> list = (ArrayList<FactuuritemInfo>) bean.getFactuuritems(TestUtils.getDate(2016,1,1), new Date());
		assertEquals(1,list.size());
		assertEquals("Bedrijfsartsen", list.get(0).getOmschrijving());
		for (FactuuritemInfo fbi: list){
			bean.deleteFactuuritem(fbi);
		}
		list = (ArrayList<FactuuritemInfo>) bean.getFactuuritems(TestUtils.getDate(2016,1,1), new Date());
		assertEquals(0,list.size());
	}
	@Test
	public void crudTestWerkzaamheid() throws SystemException, Exception{
		tu = new TestUtils(em,utx);
		emptyTableWerkzaamheden();
		int wgr = tu.addWerkgever("TEST",1,false, new Date());
		WerkzaamhedenInfo fb1 = addHuisbezoek(20,new Date(),null, __geslacht.MAN,
				wgr, null, "Huisbezoek", 1.50, "7098", "Gieselaar", __verzuimsoort.MEDISCH,
				1.50, __huisbezoekurgentie.NVT, 1);

		WerkzaamhedenInfo fb2 = addHuisbezoek(20,new Date(),null, __geslacht.VROUW,
				wgr, null, "Huisbezoek", 1.00, null, "De Vos", __verzuimsoort.MEDISCH,
				2.50, __huisbezoekurgentie.SPOEDBEZOEK, 2);

		fb2.setFiliaalid(1);
		bean.updateWerkzaamheid(fb2);
		
		ArrayList<WerkzaamhedenInfo> list;
		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamheden(1, TestUtils.getDate(2016,1,1), new Date());
		assertEquals(1,list.size());

		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamheden(null, TestUtils.getDate(2016,1,1), new Date());
		assertEquals(2,list.size());
		
		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamhedenWerkgever(1, TestUtils.getDate(2016,1,1), new Date(), wgr);
		assertEquals(1,list.size());
		
		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamhedenWerkgever(null, TestUtils.getDate(2016,1,1), new Date(), wgr);
		assertEquals(2,list.size());

		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamhedenWerkgever(null, TestUtils.getDate(2016,1,1), new Date(), 2);
		assertEquals(0,list.size());
		
		fb1.setHoldingid(1);
		bean.updateWerkzaamheid(fb1);
		
		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamhedenHolding(null, TestUtils.getDate(2016,0,1), new Date(), 1);
		assertEquals(2,list.size());

		list = (ArrayList<WerkzaamhedenInfo>) bean.getWerkzaamheden(null, TestUtils.getDate(2016,0,1), new Date());
		for (WerkzaamhedenInfo wzi:list){
			bean.deleteWerkzaamheid(wzi);
		}
		emptyTableWerkzaamheden();
	}
	@Test 
	public void afsluitenMaand() throws SystemException, Exception{
		emptyTableFacturen();
		try{
			bean.afsluitenMaand(null, null);
			throw new RuntimeException("Should not arrive here!");
		}catch(ValidationException e){
			assertEquals("Maand en jaar moeten beide ingevuld zijn.",e.getMessage());
		}
		bean.afsluitenMaand(2017, 01);
		assertEquals(0,bean.getFacturenInPeriode(TestUtils.getDate(2017,1,1), TestUtils.getDate(2017,1,1), false).size());
		tu = new TestUtils(em,utx);
		addWerkgevers();
		try{
			bean.afsluitenMaand(2017, 01);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals(true,e.getMessage().startsWith("Geen tarief gevonden voor holding"));
		}
		addTarief(100, __tariefperiode.MAAND, 0, __tariefperiode.JAAR, 
						   10, 20, TestUtils.getDate(2016,1,1), 
						   holdinggeaggregeerd, null, TestUtils.getDate(2016,1,1),
						   0.50, 25, 30, 35, 
						   40, 45, 15, 5, 50, 55, false);
		
		addTarief(0, __tariefperiode.JAAR, 101, __tariefperiode.MAAND, 
				   11, 21, TestUtils.getDate(2016,1,1), 
				   holdingseparaat, null, TestUtils.getDate(2016,1,1),
				   0.51, 26, 31, 36, 
				   41, 46, 16, 6, 51, 56, true);
		try{
			bean.afsluitenMaand(2017, 01);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals(true,e.getMessage().startsWith("Geen tarief gevonden voor werkgever/holding"));
		}
		addTarief(0, __tariefperiode.JAAR, 102, __tariefperiode.MAAND, 
				   12, 22, TestUtils.getDate(2016,1,1), 
				   null, wgrzonderholding, TestUtils.getDate(2016,1,1),
				   0.52, 27, 32, 37, 
				   42, 47, 17, 7, 52, 57, false);
		try{
			bean.afsluitenMaand(2017, 01);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals(true,e.getMessage().startsWith("Geen tarief gevonden voor werkgever/holding: Werkgever met holding"));
		}
		addTarief(0, __tariefperiode.JAAR, 101, __tariefperiode.MAAND, 
				   13, 23, TestUtils.getDate(2016,1,1), 
				   null, wgrmetholding, TestUtils.getDate(2016,1,1),
				   0.53, 28, 33, 38, 
				   43, 48, 18, 8, 53, 58, true);
		try{
			bean.afsluitenMaand(2017, 01);
			throw new RuntimeException("Should not arrive here!");
		} catch (ValidationException e){
			assertEquals(true,e.getMessage().startsWith("Factureren op werkgeverniveau en holdingniveau niet toegestaan"));
		}
		WerkgeverInfo wi = wgbean.getById(wgrmetholdingfactureren);
		wi.setFactureren(false);
		wi.setWerkweek(new BigDecimal(40));
		wgbean.updateWerkgever(wi);
		bean.afsluitenMaand(2017, 01);
		
		/*
		 * Verwacht:
		 * 		1 Werkgever zonder holding
		 * 		2 Werkgever met holding, maar factuur op werkgever niveau
		 * 		3 Holding geaggregeerd (werkgeverid = -1)
		 */
		List<FactuurTotaalInfo> facturen;
		facturen = bean.getFacturenInPeriode(TestUtils.getDate(2017,1,1), TestUtils.getDate(2017,1,1), true); 
		assertEquals(3,facturen.size());
		for (FactuurTotaalInfo f: facturen){
			System.out.println("Factuurnr: " + f.getFactuurnr()+ " voor wg: " + f.getWerkgeverid() + " holding " + f.getHoldingid());
			if (f.getWerkgeverid().equals(wgrzonderholding)){
				assertEquals(102.00, f.getAbonnementskosten().doubleValue(),0);
				assertEquals(0, f.getFactuurregelbezoeken().size());
				assertEquals(0, f.getFactuurregelitems().size());
				assertEquals(0, f.getFactuurregelsecretariaat().size());
			}
			if (f.getWerkgeverid().equals(wgrmetholding)){
				assertEquals(101.00, f.getAbonnementskosten().doubleValue(),0);
				assertEquals(0, f.getFactuurregelbezoeken().size());
				assertEquals(0, f.getFactuurregelitems().size());
				assertEquals(0, f.getFactuurregelsecretariaat().size());
			}
			if (f.getWerkgeverid().equals(wgrmetholdingfactureren)){
				assertEquals(0, f.getAansluitkosten().doubleValue(),0);
				assertEquals(0, f.getFactuurregelbezoeken().size());
				assertEquals(0, f.getFactuurregelitems().size());
				assertEquals(0, f.getFactuurregelsecretariaat().size());
			}
		}
		bean.terugdraaienMaand(2017, 01);
		facturen = bean.getFacturenInPeriode(TestUtils.getDate(2017,1,1), TestUtils.getDate(2017,1,1), true); 
		assertEquals(0,facturen.size());
		addWerkzaamheden();
		addWerknemer(wgrmetholdingfactureren);
		bean.afsluitenMaand(2017, 01);
		facturen = bean.getFacturenInPeriode(TestUtils.getDate(2017,1,1), TestUtils.getDate(2017,1,1), true); 
		assertEquals(3,facturen.size());
		for (FactuurTotaalInfo f: facturen){
			if (f.getWerkgeverid().equals(wgrzonderholding)){
				assertEquals(102, f.getAbonnementskosten().doubleValue(),0);
				assertEquals(2, f.getFactuurregelbezoeken().size());
				assertEquals(2, f.getFactuurregelitems().size());
				assertEquals(0, f.getFactuurregelsecretariaat().size());
				assertEquals(181.60, f.getSomkilometerkosten().doubleValue() + f.getSomuurkosten().doubleValue(),0);
				assertEquals(200.00, f.getSomitembedrag().doubleValue(),0);
				assertEquals(0, f.getSomvastekosten().doubleValue(),0);
			}
			if (f.getWerkgeverid().equals(wgrmetholding)){
				assertEquals(101.00, f.getAbonnementskosten().doubleValue(),0);
				assertEquals(7, f.getFactuurregelbezoeken().size());
				assertEquals(0, f.getFactuurregelitems().size());
				assertEquals(1, f.getFactuurregelsecretariaat().size());
				assertEquals(7.95, f.getSomkilometerkosten().doubleValue() + f.getSomuurkosten().doubleValue(),0);
				assertEquals(153.00, f.getSomvastekosten().doubleValue(),0);
			}
			if (f.getWerkgeverid().equals(wgrmetholdingfactureren)){
				assertEquals(100.00, f.getAansluitkosten().doubleValue(),0);
				assertEquals(1, f.getFactuurregelbezoeken().size());
				assertEquals(0, f.getFactuurregelitems().size());
				assertEquals(0, f.getFactuurregelsecretariaat().size());
				assertEquals(165.00, f.getSomkilometerkosten().doubleValue() + f.getSomuurkosten().doubleValue(),0);
				assertEquals(0, f.getSomvastekosten().doubleValue(),0);
			}
		}
		int aantal;
		aantal = bean.getAantalontbrekendeFacturen(2017, 1);
		assertEquals(0, aantal);
		aantal = bean.getAantalontbrekendeFacturen(2017, 2);
		assertEquals(3, aantal);
	}
	private void addWerknemer(int wgr) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Werknemer h = new Werknemer();
		h.setAchternaam("Achternaam");
		h.setAdres(new Adres());
		h.setArbeidsgehandicapt(0);
		h.setBurgerlijkestaat(__burgerlijkestaat.GEHUWD.getValue());
		h.setGeboortedatum(TestUtils.getDate(1956,9,21));
		h.setGeslacht(__geslacht.MAN.getValue());
		h.setMobiel("06");
		h.setOpmerkingen(null);
		h.setTelefoon("023");
		h.setVoorletters("T");
		h.setVoornaam("Voornaam");
		h.setWerkgever_ID(wgr);
		h.setWijzigingsdatum(null);
		em.persist(h);
		em.flush();
		
		Dienstverband d = new Dienstverband();
		d.setEinddatumcontract(null);
		d.setFunctie(null);
		d.setPersoneelsnummer("7098");
		d.setStartdatumcontract(TestUtils.getDate(1982,2,1));
		d.setWerkgever_ID(wgr);
		d.setWerknemer_ID(h.getId());
		d.setWerkweek(new BigDecimal(36));
		em.persist(d);
		em.flush();
		utx.commit();
	}	
	private void addTarief
		(double aansluitkosten, __tariefperiode aansluitkostenPeriode, double abonnement, __tariefperiode abonnementPeriode, 
		 double huisbezoekTarief, double huisbezoekZaterdagTarief, Date datumAansluitkosten, 
		 Integer holdingId,Integer werkgeverId, Date ingangsdatum,
		 double kmTarief, double maandbedragSecretariaat, double secretariaatskosten, double sociaalbezoekTarief,
		 double spoedbezoekTarief, double spoedbezoekZelfdedagTarief, double standaardHuisbezoekTarief,
		 double telefonischeControleTarief, double uurtariefNormaal, double uurtariefWeekend, boolean vasttariefhuisbezoeken
		) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Tarief h = new Tarief();
		h.setAansluitkosten(new BigDecimal(aansluitkosten));
		h.setAansluitkostenPeriode(aansluitkostenPeriode.getValue());
		h.setAbonnement(new BigDecimal(abonnement));
		h.setAbonnementPeriode(abonnementPeriode.getValue());
		h.setDatumAansluitkosten(datumAansluitkosten);
		h.setEinddatum(null);
		h.setHoldingId(holdingId);
		h.setHuisbezoekTarief(new BigDecimal(huisbezoekTarief));
		h.setHuisbezoekZaterdagTarief(new BigDecimal(huisbezoekZaterdagTarief));
		h.setIngangsdatum(ingangsdatum);
		h.setKmTarief(new BigDecimal(kmTarief));
		h.setMaandbedragSecretariaat(new BigDecimal(maandbedragSecretariaat));
		h.setOmschrijvingFactuur("Omschrijving factuur");
		h.setSecretariaatskosten(new BigDecimal(secretariaatskosten));
		h.setSociaalbezoekTarief(new BigDecimal(sociaalbezoekTarief));
		h.setSpoedbezoekTarief(new BigDecimal(spoedbezoekTarief));
		h.setSpoedbezoekZelfdedagTarief(new BigDecimal(spoedbezoekZelfdedagTarief));
		h.setStandaardHuisbezoekTarief(new BigDecimal(standaardHuisbezoekTarief));
		h.setTelefonischeControleTarief(new BigDecimal(telefonischeControleTarief));
		h.setUurtariefNormaal(new BigDecimal(uurtariefNormaal));
		h.setUurtariefWeekend(new BigDecimal(uurtariefWeekend));
		h.setVasttariefhuisbezoeken(vasttariefhuisbezoeken?1:0);
		h.setWerkgeverId(werkgeverId);
		em.persist(h);
		em.flush();
		utx.commit();
	}
	private void addWerkzaamheden() throws SystemException, Exception{
		/* Vast tarief huisbezoeken voor werkgever met holding */
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 0, __huisbezoekurgentie.HUISBEZOEKZATERDAG );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 0, __huisbezoekurgentie.SPOEDBEZOEK );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 0, __huisbezoekurgentie.SPOEDBEZOEKZELFDEDAG );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 0, __huisbezoekurgentie.STANDAARD );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 0, __huisbezoekurgentie.TELEFONISCHECONTROLE );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 0, __huisbezoekurgentie.ZELFDEDAG );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 15, TestUtils.getDate(2017,1,29), 1, 
				__werkzaamhedensoort.CASEMANAGEMENT, 1.5, __huisbezoekurgentie.NVT);
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 15, TestUtils.getDate(2017,1,28), 1, 
				__werkzaamhedensoort.DOKTERBEZOEK, 1.5, __huisbezoekurgentie.NVT );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 15, TestUtils.getDate(2017,1,28), 1, 
				__werkzaamhedensoort.OVERWERK, 1.5, __huisbezoekurgentie.NVT );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 15, TestUtils.getDate(2017,1,28), 1, 
				__werkzaamhedensoort.VERLOF, 1.5, __huisbezoekurgentie.NVT );
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 15, TestUtils.getDate(2017,1,28), 1, 
				__werkzaamhedensoort.ZIEK, 1.5, __huisbezoekurgentie.NVT );
		/*
		 * Bedrag moet zijn:
		 * 		23 + 43 + 48 + 18 + 8 + 13 = 153	
		 */
		
		
	
		/* Variabel tarief huisbezoeken voor werkgever zonder holding */
		addWerkzaamheid(null, wgrzonderholding, 10, TestUtils.getDate(2017,1,26), 1, 
				__werkzaamhedensoort.HUISBEZOEK, 1.00, __huisbezoekurgentie.NVT);
		addWerkzaamheid(null, wgrzonderholding, 20, TestUtils.getDate(2017,1,7), 1, /* Zaterdag */ 
				__werkzaamhedensoort.HUISBEZOEK, 2.00, __huisbezoekurgentie.NVT);
		/* Bedrag moet zijn:	10 * 0.52 + 1.00 * 52 = 57.20
		 * 					 	20 * 0.52 + 2.00 * 57 =124.40
		 * 												-----
		 * 											   181.60
		 */
		addWerkzaamheid(holdingzonderfactureren, wgrmetholding, 0, TestUtils.getDate(2017,1,31), 1, 
				__werkzaamhedensoort.SECRETARIAAT, 1.5, __huisbezoekurgentie.NVT);
	
		int kop = addFactuurkop("Factuurkop");
		int categorie = addFactuurcategorie(kop);
		addItem(null, wgrzonderholding, TestUtils.getDate(2017,1,10), 50, categorie);
		addItem(null, wgrzonderholding, TestUtils.getDate(2017,1,6), 150, categorie);
		
		/* Variabel tarief huisbezoeken voor werkgever met holding en factureren */
		addWerkzaamheid(null, wgrmetholdingfactureren, 30, TestUtils.getDate(2017,1,6), 1,  
				__werkzaamhedensoort.HUISBEZOEK, 3.00, __huisbezoekurgentie.NVT);
		/* Bedrag moet zijn:	30 * 0.50 + 3.00 * 50 = 165.00
		 */
		
	}
	private int addFactuurkop(String kop)throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		Factuurkop i = new Factuurkop();
		i.setOmschrijving(kop);
		i.setPrioriteit(1);
		em.persist(i);
		em.flush();
		utx.commit();
		return i.getId();
	}
	private int addFactuurcategorie(int id)throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		Factuurcategorie i = new Factuurcategorie();
		i.setFactuurkopid(id);
		i.setBtwcategorie(__btwtariefsoort.LAAG.getValue());
		i.setIsomzet(1);
		i.setOmschrijving("categorie");
		em.persist(i);
		em.flush();
		utx.commit();
		return i.getId();
	}
	private void addItem
		(Integer holdingid, Integer werkgeverid, Date datum, double bedrag, int categorie 
		) throws SystemException, Exception{ 
		utx.begin();
		em.joinTransaction();
		Factuuritem i = new Factuuritem();
		i.setBedrag(new BigDecimal(bedrag));
		i.setDatum(datum);
		i.setFactuurcategorieid(categorie);
		i.setHoldingid(holdingid);
		i.setOmschrijving("Item");
		i.setUserid(1);
		i.setWerkgeverid(werkgeverid);
		em.persist(i);
		em.flush();
		utx.commit();
	}
	private void addWerkzaamheid
	(Integer holdingid, Integer werkgeverid, double aantalkm, Date datum, double overigekosten, 
	 __werkzaamhedensoort werkzaamhedensoort, double uren, __huisbezoekurgentie urgentie	 
	) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		Werkzaamheden h = new Werkzaamheden();
		h.setAantalkm(new BigDecimal(aantalkm));
		h.setDatum(datum);
		h.setFiliaalid(null);
		h.setGeslacht(__geslacht.MAN.getValue());
		h.setHoldingid(holdingid);
		h.setOmschrijving("");
		h.setOverigekosten(new BigDecimal(overigekosten));
		h.setPersoneelsnummer("Nr");
		h.setPersoon("Persoon");
		h.setSoortverzuim(__verzuimsoort.MEDISCH.getValue());
		h.setSoortwerkzaamheden(werkzaamhedensoort.getValue());
		h.setUren(new BigDecimal(uren));
		h.setUrgentie(urgentie.getValue());
		h.setUserid(1);
		h.setWerkgeverid(werkgeverid);
		h.setWoonplaats("Woonplaats");
		em.persist(h);
		em.flush();
		utx.commit();
	}
	private int addHolding(String naam, boolean factureren, Date date, __factuurtype type) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Holding h = new Holding();
		h.setStartdatumcontract(date);
		h.setNaam(naam);
		h.setVestigingsadres(new Adres());
		h.getVestigingsadres().setStraat("Binnenweg");
		h.setFactureren(factureren?1:0);
		h.setFactuurtype(type.getValue());
		em.persist(h);
		em.flush();
		utx.commit();
		return h.getId();
	}
	
	public void addWerkgevers() throws SystemException, Exception{
		wgrzonderholding = tu.addWerkgever("Werkgever zonder holding",null,true, TestUtils.getDate(2010,1,1));
		holdingzonderfactureren = addHolding("Holding nvt", false, TestUtils.getDate(2010,1,1),__factuurtype.NVT);
		wgrmetholding = tu.addWerkgever("Werkgever met holding",holdingzonderfactureren,true, TestUtils.getDate(2010,1,1));
		holdinggeaggregeerd = addHolding("Holding geaggregeerd", true, TestUtils.getDate(2010,1,1),__factuurtype.GEAGGREGEERD);
		wgrmetholdingfactureren = tu.addWerkgever("Werkgever met holdingfactuur",holdinggeaggregeerd,true, TestUtils.getDate(2010,1,1));
		holdingseparaat = addHolding("Holding separaat", true, TestUtils.getDate(2010,1,1),__factuurtype.SEPARAAT);
	}
	private WerkzaamhedenInfo addHuisbezoek
		(double aantalkm, Date datum, Integer filiaalid,
		 __geslacht geslacht, Integer werkgeverid, Integer holdingid, String omschrijving, double kosten,
		 String personeelsnummer, String persoon, __verzuimsoort soortvzm, double uren,
		 __huisbezoekurgentie urgentie, int userid) 
				 throws ValidationException, VerzuimApplicationException {
		WerkzaamhedenInfo fk = new WerkzaamhedenInfo();
		fk.setSoortwerkzaamheden(__werkzaamhedensoort.HUISBEZOEK);
		fk.setAantalkm(new BigDecimal(aantalkm));
		fk.setDatum(datum);
		fk.setFiliaalid(filiaalid);
		fk.setGeslacht(geslacht);
		fk.setHoldingid(holdingid);
		fk.setOmschrijving(omschrijving);
		fk.setOverigekosten(new BigDecimal(kosten));
		fk.setPersoneelsnummer(personeelsnummer);
		fk.setPersoon(persoon);
		fk.setSoortverzuim(soortvzm);
		fk.setUren(new BigDecimal(uren));
		fk.setUrgentie(urgentie);
		fk.setUserid(userid);
		fk.setWerkgeverid(werkgeverid);
		fk.setWoonplaats("AMSTERDAM");
		fk = bean.addWerkzaamheid(fk);
		return fk;
	}
	private FactuuritemInfo addFactuuritem
		(String item, double bedrag, Date datum, int factuurcategorie
				, Integer holdingid, Integer werkgeverid) throws ValidationException, VerzuimApplicationException {
		FactuuritemInfo fk = new FactuuritemInfo();
		fk.setOmschrijving(item);
		fk.setBedrag(new BigDecimal(bedrag));
		fk.setDatum(datum);
		fk.setFactuurcategorieid(factuurcategorie);
		fk.setHoldingid(holdingid);
		fk.setWerkgeverid(werkgeverid);
		fk = bean.addFactuuritem(fk);
		return fk;
	}
	private FactuurkopInfo addFactuurkop(String kop, int prioriteit) throws ValidationException, VerzuimApplicationException {
		FactuurkopInfo fk = new FactuurkopInfo();
		fk.setOmschrijving(kop);
		fk.setPrioriteit(prioriteit);
		fk = bean.addFactuurkop(fk);
		return fk;
	}
	private FactuurcategorieInfo addFactuurcategorie(int kop, boolean isomzet, String omschrijving) throws ValidationException, VerzuimApplicationException {
		FactuurcategorieInfo fc = new FactuurcategorieInfo();
		fc.setBtwcategorie(__btwtariefsoort.HOOG);
		fc.setFactuurkopid(kop);
		fc.setIsomzet(isomzet);
		fc.setOmschrijving(omschrijving);
		fc = bean.addFactuurcategorie(fc);
		return fc;
	}
	private BtwInfo addBtw(__btwtariefsoort soort, Date ingangsdatum, Date einddatum, double percentage) throws ValidationException, VerzuimApplicationException {
		BtwInfo btw = new BtwInfo();
		btw.setBtwtariefsoort(soort);
		btw.setIngangsdatum(ingangsdatum);
		btw.setEinddatum(einddatum);
		btw.setPercentage(new BigDecimal(percentage));
		btw = bean.addBtw(btw);
		return btw;
	}
	private FactuurbetalingInfo addFactuurbetaling(double bedrag, int factuurid) throws ValidationException, VerzuimApplicationException {
		FactuurbetalingInfo bet = new FactuurbetalingInfo();
		bet.setBedrag(new BigDecimal(bedrag));
		bet.setDatum(new Date());
		bet.setFactuurid(factuurid);
		bet.setRekeningnummerbetaler("0111111110");
		bet = bean.addFactuurbetaling(bet);
		return bet;
	}
}
