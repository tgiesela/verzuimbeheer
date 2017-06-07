package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

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

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;

@RunWith(Arquillian.class)
public class TestInstantieBean {
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear =
				ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(InstantieBean.class)
				.addClass(AdresBean.class)
				.addClass(InstantieConversion.class)
				.addClass(AdresConversion.class)
				.addClass(ContactpersoonConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB InstantieBean bean;
    @EJB AdresBean beanAdres;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		System.out.println("Deleting old records...");
		em.createQuery("delete from Bedrijfsarts").executeUpdate();
		em.createQuery("delete from Arbodienst").executeUpdate();
		utx.commit();
	}
	public void getListArbo(int expectedcount) throws VerzuimApplicationException{
		List<ArbodienstInfo> arbodiensten;
		arbodiensten = bean.getArbodiensten();
		assertEquals("getList did not return zero arbodiensten",arbodiensten.size(),expectedcount);
	}

	public void getListBedrijfsarts(int expectedcount) throws VerzuimApplicationException{
		List<BedrijfsartsInfo> list;
		list = bean.getBedrijfsartsen();
		assertEquals("getList did not return zero bedrijfsartesen",list.size(),expectedcount);
	}
	
	@Test
	public void crudTestArbodienst() throws SystemException, Exception{
		emptyTable();
		getListArbo(0);
		ArbodienstInfo a1 = addArbodienst("Arbodienst1");
		getListArbo(1);
		ArbodienstInfo a2 = addArbodienst("Arbodienst2");
		getListArbo(2);
		
		List<ArbodienstInfo> list = bean.getArbodiensten();
		for (ArbodienstInfo el:list){
			el.setTelefoonnummer("");
			bean.updateArbodienst(el);
		}

		list = bean.getArbodiensten();
		for (ArbodienstInfo el:list){
			bean.deleteArbodienst(el);
			AdresInfo a = beanAdres.getById(el.getPostAdres().getId());
			assertNull(a);
			AdresInfo b = beanAdres.getById(el.getVestigingsAdres().getId());
			assertNull(b);
		}
	}
	
	@Test
	public void crudTestBedrijfsarts() throws SystemException, Exception{
		emptyTable();
		getListBedrijfsarts(0);
		ArbodienstInfo arbodienst = addArbodienst("Arbodienst2");
		BedrijfsartsInfo a1 = addBedrijfarts("Arbodienst1", arbodienst.getId());
		getListBedrijfsarts(1);
		
		List<BedrijfsartsInfo> list = bean.getBedrijfsartsen();
		for (BedrijfsartsInfo el:list){
			el.setArbodienstId(1);
			bean.updateBedrijfsarts(el);
		}
		list = bean.getBedrijfsartsen();
		for (BedrijfsartsInfo el:list){
			bean.deleteBedrijfsarts(el);
		}
	}
	private BedrijfsartsInfo addBedrijfarts(String naam, Integer ba) throws ValidationException, VerzuimApplicationException {
		BedrijfsartsInfo g = new BedrijfsartsInfo();
		g.setAchternaam(naam);
		g.setArbodienstId(null);
		g.setEmail(naam + "@bedrijfsts.nl");
		g.setGeslacht(__geslacht.VROUW);
		g.setTelefoon("02011111111");
		g.setVoorletters("B.A.");
		g.setVoornaam("Test");
		g.setArbodienstId(ba);
		bean.addBedrijfsarts(g);
		BedrijfsartsInfo a;
		a = bean.getBedrijfsarts(g.getId());
		assertNotNull(a);
		return a;
	}
	private ArbodienstInfo addArbodienst(String naam) throws ValidationException, VerzuimApplicationException{
		ArbodienstInfo g = new ArbodienstInfo();
		AdresInfo postAdres = new AdresInfo();
		AdresInfo vestigingsAdres = new AdresInfo();
		ContactpersoonInfo contactpersoon = new ContactpersoonInfo();
		g.setNaam(naam);
		contactpersoon.setAchternaam("Test");
		contactpersoon.setEmailadres(naam + "@test.nl");
		contactpersoon.setGeslacht(__geslacht.MAN);
		contactpersoon.setMobiel("0611111111");
		contactpersoon.setTelefoon(null);
		contactpersoon.setVoorletters("T.T");
		contactpersoon.setVoornaam(null);
		contactpersoon.setVoorvoegsel("ter");
		g.setContactpersoon(contactpersoon);
		postAdres.setHuisnummer("188");
		postAdres.setHuisnummertoevoeging("bg");
		postAdres.setLand("NL");
		postAdres.setPlaats("Amsterdam");
		postAdres.setPostcode("1000AA");
		postAdres.setStraat("Postbus");
		g.setPostAdres(postAdres);
		g.setTelefoonnummer("0201111111");
		vestigingsAdres.setHuisnummer("48");
		vestigingsAdres.setHuisnummertoevoeging("bg");
		vestigingsAdres.setLand("NL");
		vestigingsAdres.setPlaats("Haarlem");
		vestigingsAdres.setPostcode("2000BB");
		vestigingsAdres.setStraat("Dreef");
		g.setVestigingsAdres(vestigingsAdres);
		g = bean.addArbodienst(g);
		ArbodienstInfo a;
		a = bean.getArbodienst(g.getId());
		assertNotNull(a);
		return a;
	}
}
