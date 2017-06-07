package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

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

import com.gieselaar.verzuimbeheer.entities.Adres;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemer;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemerPK;
import com.gieselaar.verzuimbeheer.entities.Applicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctiePK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.AutorisatieConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

@RunWith(Arquillian.class)
public class TestApplicatieFunctieBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(ApplicatieFunctieBean.class)
				.addClass(AutorisatieConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB ApplicatieFunctieBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		System.out.println("Deleting old records...");
		em.createQuery("delete from Applicatiefunctie").executeUpdate();
		em.createQuery("delete from Rol").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestApplicatieFuncties() throws SystemException, Exception{
		emptyTable();
		
		ApplicatieFunctieInfo af = new ApplicatieFunctieInfo();
		try{
			bean.addApplicatiefunctie(af);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			af.setFunctieId("BWG");
			af.setFunctieomschrijving("Beheer werkgever");
			af = bean.addApplicatiefunctie(af);
		}

		ApplicatieFunctieInfo af2 = new ApplicatieFunctieInfo();
		af2.setFunctieId("RWG");
		af2.setFunctieomschrijving("Raadplegen werkgever");
		af2 = bean.addApplicatiefunctie(af2);
		af2 = bean.getById(af2.getId());

		af.setFunctieomschrijving("Gewijzigd");
		bean.updateApplicatiefunctie(af);
		af = bean.getById(af.getId());
		assertEquals("Gewijzigd", af.getFunctieomschrijving());
		
		ArrayList<ApplicatieFunctieInfo> list = (ArrayList<ApplicatieFunctieInfo>) bean.allApplicatiefuncties();
		assertEquals(2,list.size());
		
		bean.deleteApplicatiefunctie(af);
		bean.deleteApplicatiefunctie(af2);
		list = (ArrayList<ApplicatieFunctieInfo>) bean.allApplicatiefuncties();
		assertEquals(0,list.size());
	}
	private void addFunctieToRol(ApplicatieFunctieInfo af, int rol) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		RolApplicatiefunctie raf = new RolApplicatiefunctie();
		RolApplicatiefunctiePK rafpk = new RolApplicatiefunctiePK();
		rafpk.setRolId(rol);
		rafpk.setApplicatiefunctieId(af.getId());
		raf.setId(rafpk);
		em.persist(rol);
		em.flush();
		utx.commit();
	}
	private int addRol(String naam) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Rol rol = new Rol();
		rol.setOmschrijving(naam);
		em.persist(rol);
		em.flush();
		utx.commit();
		return rol.getId();
	}
}
