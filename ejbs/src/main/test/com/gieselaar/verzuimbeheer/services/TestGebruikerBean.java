package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimAuthenticationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.utils.AutorisatieConversion;

@RunWith(Arquillian.class)
public class TestGebruikerBean {
	private TestUtils tu;
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;

	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(GebruikerBean.class)
				.addClass(AutorisatieConversion.class)
				.addClass(SettingsBean.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}

	@Before
	public void testinit(){
		/*
		 * Initialization which applies to all tests in this class.
		 * Currently none
		 */
	}

    @EJB GebruikerBean gebruiker;
	@EJB SettingsBean settingsBean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Gebruiker").executeUpdate();
		em.createQuery("delete from Settings").executeUpdate();
		utx.commit();
	}
	
	public void getList(int expectedcount) throws VerzuimApplicationException{
		List<GebruikerInfo> gebruikers;
		gebruikers = gebruiker.allGebruikers();
		assertEquals("getList did not return zero users",gebruikers.size(),expectedcount);
	}
	@Test
	public void crudTestGebruiker() throws SystemException, Exception{
		emptyTable();
		getList(0);
		addGebruiker("testuser1");
		getList(1);
		addGebruiker("testuser2");
		getList(2);
		
		List<GebruikerInfo> gebruikers = gebruiker.allGebruikers();
		for (GebruikerInfo g:gebruikers){
			g.setStatus(__status.ACTIVE);
			UpdateGebruiker(g);
			assertEquals(gebruiker.getGebruikerbyId(g.getId()).getStatus(),__status.ACTIVE);
		}

		gebruikers = gebruiker.allGebruikers();
		for (GebruikerInfo g:gebruikers){
			g.setStatus(__status.ACTIVE);
			gebruiker.deleteGebruiker(g);
		}
		getList(0);
	}
	@Test
	public void newPasswordTest() throws SystemException, Exception{
		insertSettings();
		addGebruiker("testuser5");
		GebruikerInfo g = gebruiker.getGebruikerbyName("testuser5");
		g.setEmailadres("tgiesela@hccnet.nl");
		g = UpdateGebruiker(g);
		gebruiker.sendNewpassword(g);
	}
	@Test
	public void addWerkgeversToGebruiker() throws SystemException, Exception{
		tu = new TestUtils(em,utx);
		Integer wg1 = tu.addWerkgever("Werkgever1",null,false,TestUtils.getDate(2010, 1, 1));
		Integer wg2 = tu.addWerkgever("Werkgever2",null,false,TestUtils.getDate(2010, 1, 1));
		addGebruiker("testuser4");
		GebruikerInfo g = gebruiker.getGebruikerbyName("testuser4");
		GebruikerWerkgeverInfo gw1 = new GebruikerWerkgeverInfo();
		GebruikerWerkgeverInfo gw2 = new GebruikerWerkgeverInfo();
		gw1.setWerkgeverid(wg1);
		gw2.setWerkgeverid(wg2);
		g.getWerkgevers().add(gw1);
		g.getWerkgevers().add(gw2);
		g = UpdateGebruiker(g);
		assertNotNull(g);
		assertEquals(g.getWerkgevers().size(),2);
		deleteWerkgever(wg1);
		deleteWerkgever(wg2);
	}
	private void deleteWerkgever(Integer wg2) throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Werkgever where id = " + wg2).executeUpdate();
		utx.commit();
	}

	private void insertSettings() throws VerzuimApplicationException, ValidationException {
		SettingsInfo settings;
		settings = settingsBean.getSettings();
		if (settings == null){
			settings = new SettingsInfo();
			settings.setBccemailaddress("tgiesela@hccnet.nl");
			settings.setFactuurfolder("Y:\\Facturen");
			settings.setFactuurmailtextbody("");
			settings.setSmtpmailfromaddress("tgiesela@hccnet.nl");
			settings.setSmtpmailhost("ubuntuglassfish.thuis.local");
			settings.setSmtpmailpassword("password");
			settings.setSmtpmailuser("root");
			settings.setTodoforafsluitendienstverband(null);
			settings.setTodoforinformatiekaart(null);
			settingsBean.updateSettings(settings);
		}
	}

	@Test
	public void authenticationTest() throws SystemException, Exception{
		
		emptyTable();
		addGebruiker("testuser3");
		GebruikerInfo g = gebruiker.getGebruikerbyName("testuser3");
		
		/* Authenticate while user is not active */
		try{
			boolean result = gebruiker.authenticateGebruiker(g.getName(),"password");
			assertEquals(result,false);
		} catch (VerzuimAuthenticationException e){
		} catch (VerzuimApplicationException e){
			fail("Authentication failed, with unexpected exception:" + e.toString());
		}
		
		/* Authenticate while user is active with bad password*/
		g.setStatus(__status.ACTIVE);
		UpdateGebruiker(g);
		try{
			boolean result = gebruiker.authenticateGebruiker(g.getName(),"badpassword");
			assertEquals(result,false);
		} catch (VerzuimAuthenticationException e){
		} catch (VerzuimApplicationException e) {
			fail("Authentication failed, with unexpected exception:" + e.toString());
		}

		/* Authenticate unknown user */
		try{
			boolean result = gebruiker.authenticateGebruiker("xxxxxxxx","badpassword");
			assertEquals(result,false);
		} catch (VerzuimAuthenticationException e){
		} catch (VerzuimApplicationException e) {
			fail("Authentication failed, with unexpected exception:" + e.toString());
		}

		/* Authenticate known user and correct password */
		try{
			boolean result = gebruiker.authenticateGebruiker("testuser3","password");
			assertEquals(result,true);
		} catch (VerzuimAuthenticationException e){
			fail("Authentication failed, while it should have been successful");
		} catch (VerzuimApplicationException e) {
			fail("Authentication failed, with unexpected exception:" + e.toString());
		}

		/* Authenticate known user and correct password against AD */
		/* user must exist in AD */
		addGebruiker("testuser1");
		g = gebruiker.getGebruikerbyName("testuser1");
		g.setStatus(__status.ACTIVE);
		g.setAduser(true);
		g.setDomainname("thuis.local");
		gebruiker.updateGebruiker(g);
		/* With correct username and password */
		gebruiker.changePassword("testuser1", "", "Test_01");
		g = gebruiker.getGebruikerbyName("testuser1");
		try{
			boolean result = gebruiker.authenticateGebruiker(g.getName(),"Test_01");
			assertEquals(result,true);
		} catch (VerzuimAuthenticationException e){
			fail("Authentication failed, while it should have been successful");
		} catch (VerzuimApplicationException e) {
			fail("Authentication failed, with unexpected exception:" + e.toString());
		}

		/* With correct username and incorrect password */
		gebruiker.changePassword("testuser1", "", "Test_01");
		g = gebruiker.getGebruikerbyName("testuser1");
		try{
			boolean result = gebruiker.authenticateGebruiker(g.getName(),"Test_02");
			assertEquals(result,false);
		} catch (VerzuimAuthenticationException e){
			fail("Authentication failed, while it should have been successful");
		} catch (VerzuimApplicationException e) {
			fail("Authentication failed, with unexpected exception:" + e.toString());
		}
	}
	public void addGebruiker(String username) throws SystemException, Exception {
		GebruikerInfo g = new GebruikerInfo();
		g.setAduser(false);
		g.setAlleklanten(false);
		g.setEmailadres(username + "@b.nl");
		g.setName(username);
		g.setStatus(__status.INACTIVE);
		g.setTussenvoegsel("de");
		g.setAchternaam(username + "Achternaam");
		g.setVoornaam(username + "Voornaam");
		g.setNewPassword("password");
		try {
			gebruiker.addGebruiker(g);
			g = gebruiker.getGebruikerbyName(username);
			assertNotNull(g);
		} catch (ValidationException e) {
			fail(e.getMessage());
		} catch (VerzuimApplicationException e) {
			fail(e.getMessage());
		}
	}

	public GebruikerInfo UpdateGebruiker(GebruikerInfo user) throws ValidationException, VerzuimApplicationException {
		gebruiker.updateGebruiker(user);
		return gebruiker.getGebruikerbyId(user.getId());
	}
}
