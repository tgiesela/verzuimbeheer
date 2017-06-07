package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctiePK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakket;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakketPK;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.SettingsConversion;

@RunWith(Arquillian.class)
public class TestSettingsBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(SettingsBean.class)
				.addClass(SettingsConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB SettingsBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Settings").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestPakket() throws SystemException, Exception{
		emptyTable();
		
		SettingsInfo info = new SettingsInfo();
		info.setBccemailaddress("a@b");
		info.setSmtpmailfromaddress("b@c");
		try{
			bean.updateSettings(info);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals(true,e.getMessage().startsWith("Ongeldig bccemailaddress"));
		}
		info.setBccemailaddress("a@b.nl");
		try{
			bean.updateSettings(info);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals(true,e.getMessage().startsWith("Ongeldig smtpmailfromaddress"));
		}
		info.setSmtpmailfromaddress("b.a@c.nl");
		bean.updateSettings(info);
		info = bean.getSettings();
		assertEquals("b.a@c.nl",info.getSmtpmailfromaddress());
		assertEquals("a@b.nl",info.getBccemailaddress());
	}
}
