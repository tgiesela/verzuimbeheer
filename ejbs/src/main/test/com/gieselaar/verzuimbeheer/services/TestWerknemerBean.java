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

import com.gieselaar.verzuimbeheer.entities.Activiteit;
import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteit;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteitPK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WerknemerBean.__inclusive;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
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
public class TestWerknemerBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;

    private int holding1;
    private int werkgever1;
    private int werkgever2;
    private Afdeling afd1;    
    private Afdeling afd2;    
    @Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(ActiviteitBean.class)
				.addClass(WerknemerBean.class)
				.addClass(AdresBean.class)
				.addClass(VerzuimBean.class)
				.addClass(WerkgeverBean.class)
				.addClass(SettingsBean.class)
				.addClass(AfdelingBean.class)
				.addClass(TariefBean.class)
				.addClass(InstantieBean.class)
				.addClass(PakketBean.class)
				.addClass(FactuurBean.class)
				.addClass(ContactpersoonBean.class)
				.addClass(VerzuimConversion.class)
				.addClass(WerkgeverConversion.class)
				.addClass(AdresConversion.class)
				.addClass(WerknemerConversion.class)
				.addClass(SettingsConversion.class)
				.addClass(AdresConversion.class)
				.addClass(FactuurConversion.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(InstantieConversion.class)
				.addClass(PakketConversion.class)
				.addClass(FactuurConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB WerknemerBean bean;
    @EJB VerzuimBean vzmbean;
    @EJB SettingsBean setbean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Werknemer").executeUpdate();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Holding").executeUpdate();
		em.createQuery("delete from Dienstverband").executeUpdate();
		em.createQuery("delete from Afdeling").executeUpdate();
		em.createQuery("delete from Wiapercentage").executeUpdate();
		em.createQuery("delete from AfdelingHasWerknemer").executeUpdate();
		em.createQuery("delete from Settings").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestWerknemer() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		vzmbean.setCurrentuser(1);
		bean.setCurrentuser(1);
		tu.addSettings();
		createstartdata(tu);
		DienstverbandInfo dvb = new DienstverbandInfo();
		dvb.setStartdatumcontract(TestUtils.getDate(2015, 1, 1));
		dvb.setWerkweek(BigDecimal.ZERO);
		WerknemerInfo wn1 = initWerknemer("Naam1", werkgever1, afd1.getId(), dvb.getStartdatumcontract(), dvb,"010429785");
		wn1 = bean.addWerknemer(wn1);
		wn1 = bean.getById(wn1.getId());
		assertNotNull(wn1);
		assertEquals(1,wn1.getAfdelingen().size());
		assertEquals(1,wn1.getDienstVerbanden().size());
		
		List<WerknemerInfo> wnrs = bean.getByBSN("010429785");
		assertNotNull(wnrs);
		assertEquals(1,wnrs.size());

		wnrs = bean.getByBSN(werkgever1, "010429785");
		assertNotNull(wnrs);
		assertEquals(1,wnrs.size());

		DienstverbandInfo dvb2 = new DienstverbandInfo();
		dvb2.setStartdatumcontract(TestUtils.getDate(2015, 1, 1));
		dvb2.setWerkweek(BigDecimal.ZERO);
		WerknemerInfo wn2 = initWerknemer("Naam2", werkgever1, afd2.getId(), dvb.getStartdatumcontract(), dvb2,"010429785");
		try {
			wn2 = bean.addWerknemer(wn2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("BSN komt al voor bij deze werkgever!", e.getMessage());
		}
		wn2 = initWerknemer("Naam2", werkgever2, afd2.getId(), dvb.getStartdatumcontract(), dvb2,"010429785");
		wn2.setBurgerservicenummer("014723244");
		wn2.getDienstVerbanden().clear();
		try {
			wn2 = bean.addWerknemer(wn2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Dienstverband informatie ontbreekt", e.getMessage());
		}
		wn2.getDienstVerbanden().add(dvb2);
		wn2 = bean.addWerknemer(wn2);
		
		wn2 = bean.getById(wn1.getId());
		assertNotNull(wn2);
		assertEquals(1,wn2.getAfdelingen().size());
		assertEquals(1,wn2.getDienstVerbanden().size());
		
		List<WerknemerFastInfo> wnrfs = bean.getAll();
		assertEquals(2,wnrfs.size());
		
		wnrfs = bean.getAllWerknemers(werkgever1,__inclusive.ACTIVEANDINACTIVE);
		assertEquals(1,wnrs.size());
		wnrfs = bean.getAllWerknemers(werkgever2,__inclusive.ACTIVEANDINACTIVE);
		assertEquals(1,wnrs.size());
		
		/* Update */
		
		wn2.getActiefDienstverband().setEinddatumcontract(TestUtils.getDate(2017, 3, 27));
		bean.updateWerknemer(wn2);
		wn2 = bean.getById(wn2.getId());
		assertEquals(false,wn2.isActief());
		
		/* Update met een open verzuim */
		VerzuimInfo vzm1 = tu.addVerzuim(wn1.getActiefDienstverband(), wn1, TestUtils.getDate(2017, 2, 20), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = vzmbean.addVerzuim(vzm1);
		wn1.getActiefDienstverband().setEinddatumcontract(TestUtils.getDate(2017, 2, 21));
		try{
			bean.updateWerknemer(wn1);
			throw new RuntimeException("Should not arrive here!");
		}catch(ValidationException e){
			assertEquals("Kan dienstverband niet afsluiten. Er is nog een open verzuim",e.getMessage());
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, +1);
		wn1.getDienstVerbanden().get(0).setEinddatumcontract(cal.getTime()); /* In de toekomst */
		cal.add(Calendar.DATE, +1);
		vzm1.setStartdatumverzuim(cal.getTime());						  /* Na einddatum dienstverband */
		vzm1.getDienstverband().setEinddatumcontract(null);
		vzmbean.updateVerzuim(vzm1);
		try{
			bean.updateWerknemer(wn1);
			throw new RuntimeException("Should not arrive here!");
		}catch(ValidationException e){
			assertEquals("Startdatum open verzuim ligt na einde dienstverband!",e.getMessage());
		}

		vzm1 = vzmbean.getVerzuimDetails(vzm1.getId());
		vzm1.setStartdatumverzuim(TestUtils.getDate(2017, 2, 21));		  /* Voor einddatum dienstverband */
		vzmbean.updateVerzuim(vzm1);
		vzm1 = vzmbean.getVerzuimDetails(vzm1.getId());
		
		VerzuimHerstelInfo herstel = new VerzuimHerstelInfo();
		herstel.setDatumHerstel(TestUtils.getDate(2017, 2, 28));
		herstel.setMeldingsdatum(herstel.getDatumHerstel());
		herstel.setPercentageHerstel(new BigDecimal(100.0));
		herstel.setPercentageHerstelAT(new BigDecimal(0.0));
		herstel.setMeldingswijze(__meldingswijze.TELEFOON);
		herstel.setVerzuim(vzm1);
		vzmbean.addVerzuimHerstel(herstel, false);
		wn1 = bean.getById(wn1.getId());
		wn1.getDienstVerbanden().get(0).setEinddatumcontract(TestUtils.getDate(2017, 2, 27)); 
		try{
			bean.updateWerknemer(wn1);
			throw new RuntimeException("Should not arrive here!");
		}catch(ValidationException e){
			assertEquals("Einddatum dienstverband ligt voor einddatum (laatste) verzuim!",e.getMessage());
		}
		SettingsInfo settings = setbean.getSettings();
		
		vzm1 = vzmbean.getVerzuimDetails(vzm1.getId());
		vzm1.getVerzuimherstellen().get(0).setPercentageHerstel(new BigDecimal(50));
		vzmbean.updateVerzuimHerstel(vzm1.getVerzuimherstellen().get(0),false);
		
		cal.add(Calendar.DATE, +1);
		wn1.getDienstVerbanden().get(0).setEinddatumcontract(cal.getTime()); 
		bean.updateWerknemer(wn1);
		vzm1 = vzmbean.getVerzuimDetails(vzm1.getId());
		boolean found = false;
		for (TodoInfo t:vzm1.getTodos()){
			if (t.getActiviteitId() == settings.getTodoforafsluitendienstverband()){
				found = true;
			}
		}
		if (!found){
			throw new RuntimeException("Todo voor afsluiten dienstverband niet gevonden");
		}
		
	}
	private WerknemerInfo initWerknemer(String naam, Integer wgrid, Integer afdid, Date startdatum, DienstverbandInfo dvb, String bsn){
		WerknemerInfo wn = new WerknemerInfo();
		AdresInfo adres = new AdresInfo();
		adres.setHuisnummer("188");
		adres.setStraat("Straat");
		adres.setPlaats("Plaats");
		adres.setPostcode("1111AA");
		
		AfdelingHasWerknemerInfo afd = new AfdelingHasWerknemerInfo();
		afd.setId(-1);
		afd.setAfdelingId(afdid);
		afd.setStartdatum(startdatum);
		afd.setWerknemerId(-1);
		afd.setWerkgeverId(wgrid);
		List<AfdelingHasWerknemerInfo> afdelingen = new ArrayList<>();
		afdelingen.add(afd);
		
		List<DienstverbandInfo> dienstVerbanden = new ArrayList<>();
		dvb.setWerkgeverId(wgrid);
		dienstVerbanden.add(dvb);
		
		List<WiapercentageInfo> percentages = new ArrayList<>();
		WiapercentageInfo wp = new WiapercentageInfo();
		wp.setCodeWiaPercentage(__wiapercentage.NVT);
		wp.setStartdatum(dvb.getStartdatumcontract());
		percentages.add(wp);
		
		wn.setAchternaam(naam);
		wn.setAdres(adres);
		wn.setAfdelingen(afdelingen);
		wn.setArbeidsgehandicapt(false);
		wn.setBurgerlijkestaat(__burgerlijkestaat.ONBEKEND);
		wn.setBurgerservicenummer(bsn);
		wn.setDienstVerbanden(dienstVerbanden);
		wn.setEmail("a@b.nl");
		wn.setGeboortedatum(TestUtils.getDate(1956, 9, 21));
		wn.setGeslacht(__geslacht.MAN);
		wn.setMobiel("");
		wn.setVoorletters("V.L.");
		wn.setVoornaam("He");
		wn.setWerkgeverid(wgrid);
		wn.setWerkweek(new BigDecimal(0));
		wn.setWiaPercentages(percentages);
		
		return wn;
	}
	private void createstartdata(TestUtils tu) throws SystemException, Exception {
		holding1 = tu.addHolding("Holding1", true, TestUtils.getDate(2001, 1, 1), __factuurtype.NVT);
		werkgever1 = tu.addWerkgever("Werkgever1", holding1, true, TestUtils.getDate(2010, 1, 1));
		werkgever2 = tu.addWerkgever("Werkgever2", holding1, true, TestUtils.getDate(2010, 1, 1));

		afd1 = tu.addAfdeling("Afdeling1", werkgever1);
		afd2 = tu.addAfdeling("Afdeling2", werkgever2);

	}
}
