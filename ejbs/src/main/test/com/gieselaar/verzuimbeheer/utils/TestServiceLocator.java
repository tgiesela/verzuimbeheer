package com.gieselaar.verzuimbeheer.utils;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gieselaar.verzuimbeheer.entities.Gebruiker;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacade;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.FacadeBase;
import com.gieselaar.verzuimbeheer.facades.LoginSession;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieBean;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.RolBean;
import com.gieselaar.verzuimbeheer.services.SettingsBean;

@RunWith(Arquillian.class)
public class TestServiceLocator {
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;

	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ejbjar = 
				ShrinkWrap.create(JavaArchive.class, "ejbs.jar")
					.addClass(LoginSessionRemote.class)
					.addClass(LoginSession.class)
					.addClass(AutorisatieFacadeRemote.class)
					.addClass(AutorisatieFacade.class)
					.addClass(GebruikerBean.class)
					.addClass(SettingsBean.class)
					.addClass(RolBean.class)
					.addClass(ApplicatieFunctieBean.class)
					.addClass(AutorisatieConversion.class)
					.addClass(ServiceLocatorException.class)
					.addClass(PermissionException.class)
					.addClass(FacadeBase.class)
					.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
					.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");

		return ejbjar;

	}
    @EJB GebruikerBean gebruiker;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		System.out.println("Deleting old records...");
		em.createQuery("delete from Gebruiker").executeUpdate();
		utx.commit();
	}
	private int insertGebruiker(String user, String password) throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		Gebruiker g = new Gebruiker();
		g.setAchternaam(user);
		g.setAduser(0);
		g.setAlleklanten(0);
		g.setDomainname(null);
		g.setEmailadres(user+"@x.nl");
		g.setGebruikersnaam(user);
		g.setInlogfouten(0);
		g.setPasswordhash(password);
		g.setStatus(__status.ACTIVE.getValue());
		g.setTussenvoegsel("");
		em.persist(g);
		em.flush();
		utx.commit();
		return g.getId();
	}
	@Test
	@InSequence(1)
	public void test() throws SystemException, Exception {
		emptyTable();
		int gebruikerid = insertGebruiker("tonny","4e5a562fa6fb9be5910e7c52ac802b191c6d46ec5e42cba0a970c537a0a62e09");
		LoginSessionRemote session;
		session   = (LoginSessionRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.LoginSession.getValue(),LoginSessionRemote.class);
		try{
			session.authenticateGebruiker("tonny", "password");
		} catch (Exception e){
			throw e;
		}
		if (!session.isAuthenticated())
			throw new RuntimeException("Not authenticated!!!");
		/*
		 * Unfortunately we cannot use the Facades here. If we access the bean, then it's ok, but if
		 * we go via the Facade, an strange error is thrown:
		 * 		java.lang.RuntimeException: WARNING: OBJCOPY00401: 
		 * 			Exception in readResolve() for com.gieselaar.verzuimbeheer.facades._LoginSessionRemote_Wrapper@5b85e367
		 * This error does not occur when we use the @RunAsClient annotation, but this throws a huge marshalling error:(
		 */
	}
/*
	@Test
	@RunAsClient
	@InSequence(2)
	public void testRemoteInterfaces() throws SystemException, Exception{
		LoginSessionRemote session;
		session   = (LoginSessionRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.LoginSession.getValue(),LoginSessionRemote.class);
		try{
			session.authenticateGebruiker("tonny", "password");
		} catch (Exception e){
			throw e;
		}
		if (!session.isAuthenticated())
			throw new RuntimeException("Not authenticated!!!");
		ArrayList<GebruikerInfo> gebruikers = (ArrayList<GebruikerInfo>) ServiceCaller.autorisatieFacade(session).getGebruikers();
	}
*/
}
