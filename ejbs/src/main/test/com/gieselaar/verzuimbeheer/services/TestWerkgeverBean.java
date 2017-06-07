package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.WerknemerBean.__inclusive;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.SettingsConversion;
import com.gieselaar.verzuimbeheer.utils.VerzuimConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

@RunWith(Arquillian.class)
public class TestWerkgeverBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;

    UitvoeringsinstituutInfo ui;
    ArbodienstInfo ad;
    BedrijfsartsInfo ba;
    
    @Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(VerzuimBean.class)
				.addClass(WerkgeverBean.class)
				.addClass(AfdelingBean.class)
				.addClass(PakketBean.class)
				.addClass(ContactpersoonBean.class)
				.addClass(FactuurBean.class)
				.addClass(InstantieBean.class)
				.addClass(WerknemerBean.class)
				.addClass(ActiviteitBean.class)
				.addClass(SettingsBean.class)
				.addClass(AdresBean.class)
				.addClass(TariefBean.class)
				.addClass(VerzuimConversion.class)
				.addClass(FactuurConversion.class)
				.addClass(WerkgeverConversion.class)
				.addClass(AdresConversion.class)
				.addClass(PakketConversion.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(InstantieConversion.class)
				.addClass(WerknemerConversion.class)
				.addClass(SettingsConversion.class)
				.addClass(AdresConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB WerkgeverBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Holding").executeUpdate();
		em.createQuery("delete from Afdeling").executeUpdate();
		em.createQuery("delete from Dienstverband").executeUpdate();
		em.createQuery("delete from Werknemer").executeUpdate();
		em.createQuery("delete from AfdelingHasWerknemer").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestWerkgever() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerkgeverInfo wg1 = initWerkgever(ad, ba, null, "Werkgever1", null, ui);
		wg1 = bean.addWerkgever(wg1);
		wg1 = bean.getById(wg1.getId());
		assertEquals(1,wg1.getAfdelings().size());
		
		List<PakketInfo> pakketten = new ArrayList<>();
		Integer pakketid = tu.AddPakket("PAKKET01");
		PakketInfo pakket = new PakketInfo();
		pakket.setId(pakketid);
		pakketten.add(pakket);
		
		int holding = tu.addHolding("Holding", false, TestUtils.getDate(2010,1, 1),__factuurtype.NVT );
		WerkgeverInfo wg2 = initWerkgever(ad, ba, holding, "Werkgever1", pakketten, ui);
		wg2 = bean.addWerkgever(wg2);
		wg2 = bean.getById(wg2.getId());
		assertEquals(1,wg2.getAfdelings().size());
		assertEquals(1,wg2.getPakketten().size());
		
		List<WerkgeverInfo> werkgevers = bean.getWerkgeversHolding(holding);
		assertEquals(1,werkgevers.size());
		
		wg1.setEmailadresfactuur("nieuwadres@host.com");
		wg1.setPakketten(pakketten);
		bean.updateWerkgever(wg1);
		wg1 = bean.getById(wg1.getId());
		assertEquals(1,wg1.getAfdelings().size());
		assertEquals(1,wg1.getPakketten().size());
		
		int wn1 = tu.addWerknemer("Werknemer1", wg1.getId());
		Afdeling afd = getAfdeling(wg1.getAfdelings().get(0).getId());
		int dvb1 = tu.addDienstverband(wn1, wg1.getId(), TestUtils.getDate(2010, 1, 1), TestUtils.getDate(2017, 12, 31), afd);
		int aantal;
		aantal = bean.getAantalWerknemers(wg1.getId(), null, TestUtils.getDate(2011,1,1));
		assertEquals(1,aantal);
		aantal = bean.getAantalWerknemers(wg1.getId(), null, TestUtils.getDate(2009,12,31));
		assertEquals(0,aantal);
		aantal = bean.getAantalWerknemers(wg1.getId(), null, TestUtils.getDate(2018,1,1));
		assertEquals(0,aantal);

		bean.deleteWerkgever(wg2);
		werkgevers = bean.getAll();
		assertEquals(1,werkgevers.size());
		
		werkgevers = bean.getAllSimple();
		assertEquals(1,werkgevers.size());
		
	}
	private Afdeling getAfdeling(Integer id) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Query q = em.createQuery("Select a from Afdeling a where a.id = " + id);
		Afdeling afd = (Afdeling) q.getSingleResult();
		utx.commit();
		return afd;
	}
	@Test
	public void crudTestHolding() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		HoldingInfo h1 = initHolding("Holding1");
		h1 = bean.addHolding(h1);
		List<HoldingInfo> holdings;
		holdings = bean.getHoldings();
		assertEquals(1,holdings.size());
		h1 = bean.getHoldingById(h1.getId());
		assertNotNull(h1);

		h1.setEinddatumcontract(TestUtils.getDate(2016,12,31));
		bean.updateHolding(h1);
		h1 = bean.getHoldingById(h1.getId());
		assertNotNull(h1.getEinddatumcontract());
		
		/* Add werkgever to holding, holding cannot be deleted */
		WerkgeverInfo wg2 = initWerkgever(ad, ba, h1.getId(), "Werkgever1", null, ui);
		wg2 = bean.addWerkgever(wg2);
		wg2 = bean.getById(wg2.getId());
		try{
			bean.deleteHolding(h1);
			throw new RuntimeException("Should not arrive here!");
		} catch (ValidationException e){
			assertEquals("Er bestaan nog gekoppelde werkgevers",e.getMessage());
		}
		wg2.setHoldingId(null);
		bean.updateWerkgever(wg2);
		bean.deleteHolding(h1);
		
		holdings = bean.getHoldings();
		assertEquals(0,holdings.size());

	}
    @EJB WerknemerBean wnbean;
	@Test
	public void testImportWerknemers() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em,utx);
		emptyTable();
		byte[] uploadedFile;
		
		String[] lines = new String[10];
		lines[0]= "Algemeen;Algemeen;01-10-2016;236945890;Ouardi;el;A.;Abdellah;16-03-2000;M;OG;01-10-2016;;656786;0,00000;Israelslaan 35;1816 VK;Alkmaar;;;0653614587;;;0";
		lines[1]= "Algemeen;Algemeen;01-10-2016;218999719;Czeyki;;M. M.;Marisa Michelina;12-03-1994;V;OG;01-10-2016;;656788;0,00000;Gedempte Baansloot 28;1811 DJ;Alkmaar;0725333862;;0624458470;;;0";
		lines[2]= "Algemeen;Algemeen;01-10-2016;212042683;Jansen;;J. J. R.;Jesse James Roelof;24-03-1993;M;OG;01-10-2016;;656789;0,00000;Fredrik Hendriklaan 137;1814 NB;Alkmaar;;;0619204330;charlotte@debewindvoerder.nl;;0";
		lines[3]= "Algemeen;Algemeen;15-09-2016;576948640;Zulfiqar;;S.;Shafqat;01-02-1987;M;GH;15-09-2016;;656752;0,00000;Velduil 22;1704 XC;Heerhugowaard;;;0684631987;;;0";
		lines[4]= "Algemeen;Algemeen;01-09-2016;634135417;Yussuf;;A. M.;Abdulkadir Mohamed;27-09-1981;M;GH;01-09-2016;;656733;0,00000;Jan de Heemstraat 7;1816 KB;Alkmaar;;;0685527765;;;0";
		lines[5]= "Algemeen;Algemeen;01-09-2016;236540142;Limon;;J. B.;Joël Benjamin;04-02-2000;M;OG;01-09-2016;;656732;0,00000;Spilstraat 112;1825 JJ;Alkmaar;0728446056;;0640592277;;;0";
		lines[6]= "Algemeen;Algemeen;01-09-2016;520432472;Ouedraogo;;A. L. J.;Assèta L J;06-11-2000;V;OG;01-09-2016;;656731;0,00000;Vincent van Goghlaan 30;1816 VP;Alkmaar;;;0684180166;;;0";
		lines[7]= "Algemeen;Algemeen;15-10-2016;225400698;Sondagh;;D.;Dennis;22-11-1996;M;OG;15-10-2016;;656798;0,00000;Van Wassenaarstraat 15;1701 EC;Heerhugowaard;0727431094;;0624380275;;;0";
		lines[8]= "Algemeen;Algemeen;15-10-2016;228660257;Dahshan;El;A.;Ayia;23-11-1997;V;OG;15-10-2016;;656860;0,00000;Bregwaard 33;1824 EH;Alkmaar;;;0640075423;;;0";
		lines[9]= "Algemeen;Algemeen;21-10-2016;242932587;Aboubacar;;I.;Idrissou;01-07-1980;M;OG;21-10-2016;;656903;0,00000;Bart van Leckstraat 86;1816 XS;Alkmaar;;;06-19449709;;;0";
		
		StringBuilder str = new StringBuilder();
		for (String s:lines){
			str.append(s + "\n");
		}
		uploadedFile = str.toString().getBytes();
		
		int wg1 = tu.addWerkgever("Werkgever1", null, false, TestUtils.getDate(2010, 1, 1));
		List<ImportResult> results = bean.importWerknemers(wg1, ";", uploadedFile, false);
		assertEquals(10,results.size());
		List<WerknemerFastInfo> werknemers = wnbean.getAllWerknemers(wg1,__inclusive.ACTIVEANDINACTIVE);
		assertEquals(10,werknemers.size());
	}
	@Test
	public void testImportUren() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em,utx);
		emptyTable();
		byte[] uploadedFile;
		
		String[] lines = new String[10];
		lines[0]= "Algemeen;Algemeen;01-10-2016;236945890;Ouardi;el;A.;Abdellah;16-03-2000;M;OG;01-10-2016;;656786;0,00000;Israelslaan 35;1816 VK;Alkmaar;;;0653614587;;;0";
		lines[1]= "Algemeen;Algemeen;01-10-2016;218999719;Czeyki;;M. M.;Marisa Michelina;12-03-1994;V;OG;01-10-2016;;656788;0,00000;Gedempte Baansloot 28;1811 DJ;Alkmaar;0725333862;;0624458470;;;0";
		lines[2]= "Algemeen;Algemeen;01-10-2016;212042683;Jansen;;J. J. R.;Jesse James Roelof;24-03-1993;M;OG;01-10-2016;;656789;0,00000;Fredrik Hendriklaan 137;1814 NB;Alkmaar;;;0619204330;charlotte@debewindvoerder.nl;;0";
		lines[3]= "Algemeen;Algemeen;15-09-2016;576948640;Zulfiqar;;S.;Shafqat;01-02-1987;M;GH;15-09-2016;;656752;0,00000;Velduil 22;1704 XC;Heerhugowaard;;;0684631987;;;0";
		lines[4]= "Algemeen;Algemeen;01-09-2016;634135417;Yussuf;;A. M.;Abdulkadir Mohamed;27-09-1981;M;GH;01-09-2016;;656733;0,00000;Jan de Heemstraat 7;1816 KB;Alkmaar;;;0685527765;;;0";
		lines[5]= "Algemeen;Algemeen;01-09-2016;236540142;Limon;;J. B.;Joël Benjamin;04-02-2000;M;OG;01-09-2016;;656732;0,00000;Spilstraat 112;1825 JJ;Alkmaar;0728446056;;0640592277;;;0";
		lines[6]= "Algemeen;Algemeen;01-09-2016;520432472;Ouedraogo;;A. L. J.;Assèta L J;06-11-2000;V;OG;01-09-2016;;656731;0,00000;Vincent van Goghlaan 30;1816 VP;Alkmaar;;;0684180166;;;0";
		lines[7]= "Algemeen;Algemeen;15-10-2016;225400698;Sondagh;;D.;Dennis;22-11-1996;M;OG;15-10-2016;;656798;0,00000;Van Wassenaarstraat 15;1701 EC;Heerhugowaard;0727431094;;0624380275;;;0";
		lines[8]= "Algemeen;Algemeen;15-10-2016;228660257;Dahshan;El;A.;Ayia;23-11-1997;V;OG;15-10-2016;;656860;0,00000;Bregwaard 33;1824 EH;Alkmaar;;;0640075423;;;0";
		lines[9]= "Algemeen;Algemeen;21-10-2016;242932587;Aboubacar;;I.;Idrissou;01-07-1980;M;OG;21-10-2016;;656903;0,00000;Bart van Leckstraat 86;1816 XS;Alkmaar;;;06-19449709;;;0";
		
		StringBuilder str = new StringBuilder();
		for (String s:lines){
			str.append(s + "\n");
		}
		uploadedFile = str.toString().getBytes();

		int h1 = tu.addHolding("Holding1", false, TestUtils.getDate(2010, 1, 1), __factuurtype.NVT);
		int wg1 = tu.addWerkgever("Werkgever1", h1, false, TestUtils.getDate(2010, 1, 1));
		List<ImportResult> results = bean.importWerknemers(wg1, ";", uploadedFile, false);
		assertEquals(10,results.size());
		for (ImportResult r:results){
			assertEquals(true,r.isImportok());
		}
		List<WerknemerFastInfo> werknemers = wnbean.getAllWerknemers(wg1,__inclusive.ACTIVEANDINACTIVE);
		assertEquals(10,werknemers.size());
		
		String[] uren = new String[3];
		uren[0] = "Identificatienr;Uren laatste maand;Werkuren laatste maand;Totaal uren laatste 3 mnd;Gemiddelde uren laatste 3 mnd;Gemiddelde weekuren laatste 3 mnd";
		uren[1] = "236945890;50,28;11,60;314,42;104,81;24,19";
		uren[2] = "218999719;111,80;25,80;392,01;130,67;30,15";

		str = new StringBuilder();
		for (String s:uren){
			str.append(s + "\n");
		}
		uploadedFile = str.toString().getBytes();
		results = bean.importUren(";", uploadedFile, true);
		assertEquals(2,results.size());
		for (ImportResult r:results){
			assertEquals(true,r.isImportok());
		}
		werknemers = wnbean.getAllWerknemers(wg1,__inclusive.ACTIVEANDINACTIVE);
		for (WerknemerFastInfo wn: werknemers){
			if (wn.getBurgerservicenummer().equals("236945890")){
				assertEquals(24.19, wn.getWerkweek().doubleValue(),0);
			}
			if (wn.getBurgerservicenummer().equals("218999719")){
				assertEquals(30.15, wn.getWerkweek().doubleValue(),0);
			}
		}
	}
	@Test
	public void testAfsluitenDienstverbanden() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em,utx);
		emptyTable();
		byte[] uploadedFile;
		
		String[] lines = new String[10];
		lines[0]= "Algemeen;Algemeen;01-10-2016;236945890;Ouardi;el;A.;Abdellah;16-03-2000;M;OG;01-10-2016;;656786;0,00000;Israelslaan 35;1816 VK;Alkmaar;;;0653614587;;;0";
		lines[1]= "Algemeen;Algemeen;01-10-2016;218999719;Czeyki;;M. M.;Marisa Michelina;12-03-1994;V;OG;01-10-2016;;656788;0,00000;Gedempte Baansloot 28;1811 DJ;Alkmaar;0725333862;;0624458470;;;0";
		lines[2]= "Algemeen;Algemeen;01-10-2016;212042683;Jansen;;J. J. R.;Jesse James Roelof;24-03-1993;M;OG;01-10-2016;;656789;0,00000;Fredrik Hendriklaan 137;1814 NB;Alkmaar;;;0619204330;charlotte@debewindvoerder.nl;;0";
		lines[3]= "Algemeen;Algemeen;15-09-2016;576948640;Zulfiqar;;S.;Shafqat;01-02-1987;M;GH;15-09-2016;;656752;0,00000;Velduil 22;1704 XC;Heerhugowaard;;;0684631987;;;0";
		lines[4]= "Algemeen;Algemeen;01-09-2016;634135417;Yussuf;;A. M.;Abdulkadir Mohamed;27-09-1981;M;GH;01-09-2016;;656733;0,00000;Jan de Heemstraat 7;1816 KB;Alkmaar;;;0685527765;;;0";
		lines[5]= "Algemeen;Algemeen;01-09-2016;236540142;Limon;;J. B.;Joël Benjamin;04-02-2000;M;OG;01-09-2016;;656732;0,00000;Spilstraat 112;1825 JJ;Alkmaar;0728446056;;0640592277;;;0";
		lines[6]= "Algemeen;Algemeen;01-09-2016;520432472;Ouedraogo;;A. L. J.;Assèta L J;06-11-2000;V;OG;01-09-2016;;656731;0,00000;Vincent van Goghlaan 30;1816 VP;Alkmaar;;;0684180166;;;0";
		lines[7]= "Algemeen;Algemeen;15-10-2016;225400698;Sondagh;;D.;Dennis;22-11-1996;M;OG;15-10-2016;;656798;0,00000;Van Wassenaarstraat 15;1701 EC;Heerhugowaard;0727431094;;0624380275;;;0";
		lines[8]= "Algemeen;Algemeen;15-10-2016;228660257;Dahshan;El;A.;Ayia;23-11-1997;V;OG;15-10-2016;;656860;0,00000;Bregwaard 33;1824 EH;Alkmaar;;;0640075423;;;0";
		lines[9]= "Algemeen;Algemeen;21-10-2016;242932587;Aboubacar;;I.;Idrissou;01-07-1980;M;OG;21-10-2016;;656903;0,00000;Bart van Leckstraat 86;1816 XS;Alkmaar;;;06-19449709;;;0";
		
		StringBuilder str = new StringBuilder();
		for (String s:lines){
			str.append(s + "\n");
		}
		uploadedFile = str.toString().getBytes();

		int h1 = tu.addHolding("Holding1", false, TestUtils.getDate(2010, 1, 1), __factuurtype.NVT);
		int wg1 = tu.addWerkgever("Werkgever1", h1, false, TestUtils.getDate(2010, 1, 1));
		List<ImportResult> results = bean.importWerknemers(wg1, ";", uploadedFile, false);
		assertEquals(10,results.size());
		for (ImportResult r:results){
			assertEquals(true,r.isImportok());
		}
		List<WerknemerFastInfo> werknemers = wnbean.getAllWerknemers(wg1,__inclusive.ACTIVEANDINACTIVE);
		assertEquals(10,werknemers.size());
		
		String[] uren = new String[4];
		uren[0] = "BSN;Naam;In dienst;Uit dienst;Medewerker;Reden einde dienstverband code;Reden einde dienstverband";
		uren[1] = "236945890;Abdellah Ouardi;01-10-2016;12-12-2016;Personeelslid;12;Naar Franchise";
		uren[2] = "218999719;Marisa Michelina Czeyki;01-10-2016;01-12-2016;Personeelslid;11;Einde rechtswege";
		uren[3] = "212042683;Jesse James Roelof Jansen;01-10-2016;31-12-2016;Personeelslid;08;Non-Performance";
		str = new StringBuilder();
		for (String s:uren){
			str.append(s + "\n");
		}
		uploadedFile = str.toString().getBytes();
		results = bean.afsluitenDienstverbanden(h1, ";", uploadedFile, true);
		assertEquals(3,results.size());
		for (ImportResult r:results){
			assertEquals(true,r.isImportok());
		}
		werknemers = wnbean.getAllWerknemers(wg1,__inclusive.ACTIVEANDINACTIVE);
		assertEquals(10,werknemers.size());
		werknemers = wnbean.getAllWerknemers(wg1,__inclusive.ACTIVEONLY);
		assertEquals(7,werknemers.size());
		werknemers = wnbean.getAllWerknemers(wg1,__inclusive.INACTIVEONLY);
		assertEquals(3,werknemers.size());
	}
	private HoldingInfo initHolding(String naam){
		HoldingInfo h = new HoldingInfo();
		h.setBtwnummer("BTW-NR");
		ContactpersoonInfo contactpersoon = new ContactpersoonInfo();
		contactpersoon.setAchternaam("Contactpersoon");
		contactpersoon.setGeslacht(__geslacht.MAN);
		h.setContactpersoon(contactpersoon);
		h.setContactpersoonfactuur(null);
		h.setDetailsecretariaat(false);
		h.setEinddatumcontract(null);
		h.setEmailadresfactuur("email@host.nl");
		h.setFactureren(false);
		h.setNaam(naam);
		h.setPostAdres(null);
		h.setStartdatumcontract(TestUtils.getDate(2010, 1, 1));
		
		AdresInfo vestigingAdres = new AdresInfo();
		vestigingAdres.setHuisnummer("188");
		vestigingAdres.setStraat("Straat");
		h.setVestigingsAdres(vestigingAdres);
		return h;
	}
	private WerkgeverInfo initWerkgever(ArbodienstInfo ad, BedrijfsartsInfo ba, Integer holding, String naam, List<PakketInfo> pakketten, UitvoeringsinstituutInfo ui){
		WerkgeverInfo wg = new WerkgeverInfo();
		List<AfdelingInfo> afdelingen = new ArrayList<>();
		
		AfdelingInfo afd = new AfdelingInfo();
		afd.setAfdelingsid("AFD1");
		afd.setNaam("Afdeling 1");
		afdelingen.add(afd);
		
		wg.setAfdelings(afdelingen);
		wg.setArbodienst(ad);
		if (ba == null){
			wg.setBedrijfsartsid(null);
		}else{
			wg.setBedrijfsartsid(ba.getId());
		}
		wg.setBtwnummer("BTW-NR");
		wg.setContactpersoon(null);
		wg.setContactpersoonfactuur(null);
		wg.setDetailsecretariaat(false);
		wg.setEinddatumcontract(null);
		wg.setEmailadresfactuur("email@host.nl");
		wg.setExterncontractnummer("ExtNr");
		wg.setFactureren(true);
		wg.setHoldingId(holding);
		wg.setNaam(naam);
		wg.setPakketten(pakketten);
		wg.setPostAdres(null);
		wg.setStartdatumcontract(TestUtils.getDate(2010, 1, 1));
		wg.setTelefoon("020-1234567");
		wg.setUwv(ui);
		if (ui != null)
			wg.setUwvId(ui.getId());
		
		AdresInfo vestigingAdres = new AdresInfo();
		vestigingAdres.setHuisnummer("188");
		vestigingAdres.setStraat("Straat");
		wg.setVestigingsAdres(vestigingAdres);
		wg.setWerkweek(new BigDecimal(40));
		return wg;
	}
	private void createstartdata(TestUtils tu) throws SystemException, Exception {
	}
}
