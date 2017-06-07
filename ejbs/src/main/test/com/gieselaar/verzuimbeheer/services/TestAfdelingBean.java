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
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

@RunWith(Arquillian.class)
public class TestAfdelingBean {

	private TestUtils tu;
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(AfdelingBean.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(ContactpersoonBean.class)
				.addClass(WerkgeverConversion.class)
				.addClass(AdresConversion.class)
				.addClass(WerkgeverBean.class)
				.addClass(TariefBean.class)
				.addClass(PakketBean.class)
				.addClass(PakketConversion.class)
				.addClass(FactuurBean.class)
				.addClass(InstantieBean.class)
				.addClass(InstantieConversion.class)
				.addClass(WerknemerBean.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
	public TestAfdelingBean(){
	}
    @EJB AfdelingBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Afdeling").executeUpdate();
		em.createQuery("delete from AfdelingHasWerknemer").executeUpdate();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Werknemer").executeUpdate();
		utx.commit();
	}
	private Integer afdelingCount() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		Long count = (Long) em.createQuery("Select count(a) from Afdeling a").getSingleResult();
		utx.commit();
		return count.intValue();
	}
	@Test
	public void crudTestAfdeling() throws SystemException, Exception{
		emptyTable();

		tu = new TestUtils(em, utx);
		int wgid1 = tu.addWerkgever("werkgever1", null, false, TestUtils.getDate(2010, 1, 1));
		int wgid2 = tu.addWerkgever("werkgever2", null, false, TestUtils.getDate(2010, 1, 1));
		int wnr = addWerknemer("Gieselaar");
		
		AfdelingInfo a1 = addAfdeling("AFDID", "Afdelingnaam",wgid1);
		AfdelingHasWerknemer ahw = addWerknemerToAfdeling(wnr,a1);
		a1 = bean.getById(a1.getId());
		assertEquals("AFDID",a1.getAfdelingsid());
		assertEquals("Afdelingnaam",a1.getNaam());

		AfdelingInfo a2 = addAfdeling("DEPID", "Department",wgid2);
		a2 = bean.updateAfdeling(a2);
		a2 = bean.getById(a2.getId());
		assertEquals("Department",a2.getNaam());
		
		ArrayList<AfdelingInfo> afdelingen = (ArrayList<AfdelingInfo>) bean.allAfdelingen();
		assertEquals(afdelingen.size(),2);
		assertEquals("AFDID", afdelingen.get(0).getAfdelingsid());
		assertEquals("DEPID", afdelingen.get(1).getAfdelingsid());
		
		a1.setNaam("Gewijzigd");
		a1 = bean.updateAfdeling(a1);
		assertEquals("Gewijzigd",a1.getNaam());
		
		afdelingen = (ArrayList<AfdelingInfo>) bean.getByWerkgeverId(wgid1);		
		assertEquals(afdelingen.size(),1);
		
		try{
			bean.deleteAfdelingen(a1.getWerkgeverId());
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Afdeling heeft nog werknemers",e.getMessage());
			deleteWerknemerfromAfdeling(ahw);
			bean.deleteAfdelingen(a1.getWerkgeverId());
		}
		bean.deleteAfdeling(a2);
		assertEquals((Integer)0,afdelingCount());
	}
	private void deleteWerknemerfromAfdeling(AfdelingHasWerknemer ahw) throws SystemException, Exception  {
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from AfdelingHasWerknemer where id.afdeling_ID = " + ahw.getId().getAfdeling_ID() +
					   " and id.werknemer_ID = " + ahw.getId().getWerknemer_ID()).executeUpdate();
		utx.commit();
		
	}
	private AfdelingHasWerknemer addWerknemerToAfdeling(int wnr, AfdelingInfo a1) throws SystemException, Exception  {
		utx.begin();
		em.joinTransaction();
		AfdelingHasWerknemer ahw = new AfdelingHasWerknemer();
		AfdelingHasWerknemerPK ahwpk = new AfdelingHasWerknemerPK();
		ahwpk.setAfdeling_ID(a1.getId());
		ahwpk.setWerknemer_ID(wnr);
		ahwpk.setStartdatum(new Date());
		ahw.setId(ahwpk);

		em.persist(ahw);
		em.flush();
		utx.commit();
		return ahw;
	}
	private int addWerknemer(String string) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Werknemer wg = new Werknemer();
		wg.setAchternaam(string);
		em.persist(wg);
		em.flush();
		utx.commit();
		return wg.getId();
	}
	private AfdelingInfo addAfdeling(String afdid, String afdnaam, int wgr) throws ValidationException, VerzuimApplicationException {
		AfdelingInfo afdeling = new AfdelingInfo();
		afdeling.setAfdelingsid(afdid);
		afdeling.setContactpersoon(new ContactpersoonInfo());
		afdeling.setNaam(afdnaam);
		afdeling.setWerkgeverId(wgr);
		afdeling = bean.addAfdeling(afdeling);
		return afdeling;
	}
	
}
