package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

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

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;

@RunWith(Arquillian.class)
public class TestContactpersoonBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(ContactpersoonBean.class)
				.addClass(ContactpersoonConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB ContactpersoonBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Contactpersoon").executeUpdate();
		utx.commit();
	}
	private Integer contactpersoonCount() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		Long count = (Long) em.createQuery("Select count(a) from Contactpersoon a").getSingleResult();
		utx.commit();
		return count.intValue();
	}
	@Test
	public void crudTestAdres() throws SystemException, Exception{
		emptyTable();
		ContactpersoonInfo a1 = addContactpersoon();
		ContactpersoonInfo a2 = bean.getById(a1.getId());
		assertEquals(a1.getId(),a2.getId());
		a2 = bean.update(a2);
		a2 = bean.getById(a2.getId());
		bean.delete(a2);
		assertEquals((Integer)0,contactpersoonCount());
	}
	private ContactpersoonInfo addContactpersoon() throws ValidationException, VerzuimApplicationException {
		ContactpersoonInfo cp = new ContactpersoonInfo();
		cp.setAchternaam("Contactpersoon");
		cp.setEmailadres("cp@a.nl");
		cp.setGeslacht(__geslacht.MAN);
		cp.setVoorletters("I.K.");
		cp.setVoornaam("Ik");
		cp.setVoorvoegsel("de");
		cp = bean.update(cp);
		return cp;
	}
	
}
