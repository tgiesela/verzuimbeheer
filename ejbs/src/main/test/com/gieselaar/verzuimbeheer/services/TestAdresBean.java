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
import com.gieselaar.verzuimbeheer.utils.AdresConversion;

@RunWith(Arquillian.class)
public class TestAdresBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(AdresBean.class)
				.addClass(AdresConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB AdresBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		System.out.println("Deleting old records...");
		em.createQuery("delete from Adres").executeUpdate();
		utx.commit();
	}
	private Integer adresCount() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		System.out.println("Deleting old records...");
		Long count = (Long) em.createQuery("Select count(a) from Adres a").getSingleResult();
		utx.commit();
		return count.intValue();
	}
	@Test
	public void crudTestAdres() throws SystemException, Exception{
		emptyTable();
		AdresInfo a1 = addAdres();
		AdresInfo a2 = bean.getById(a1.getId());
		assertEquals(a1.getId(),a2.getId());
		assertEquals("AMSTERDAM",a2.getPlaats());
		a2.setPlaats("ROTTERDAM");
		a2 = bean.update(a2);
		a2 = bean.getById(a2.getId());
		assertEquals("ROTTERDAM",a2.getPlaats());
		bean.delete(a2);
		a1 = bean.getById(a1.getId());
		bean.delete(a1);
		assertEquals((Integer)0,adresCount());
	}
	private AdresInfo addAdres() throws ValidationException, VerzuimApplicationException {
		AdresInfo adres = new AdresInfo();
		adres.setHuisnummer("10");
		adres.setHuisnummertoevoeging("I");
		adres.setLand("NL");
		adres.setPlaats("AMSTERDAM");
		adres.setPostcode("1000AA");
		adres.setStraat("DAM");
		adres = bean.update(adres);
		return adres;
	}
	
}
