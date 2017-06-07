package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

@RunWith(Arquillian.class)
public class TestOeBean {
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear =
				ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(OeBean.class)
				.addClass(WerkgeverConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB OeBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Oe").executeUpdate();
		em.createQuery("delete from Oeniveau").executeUpdate();
		utx.commit();
	}
	public void getListOe(int expectedcount) throws VerzuimApplicationException{
		List<OeInfo> arbodiensten;
		arbodiensten = bean.getOes();
		assertEquals("getList did not return expected count oes",arbodiensten.size(),expectedcount);
	}

	public void getListOenivueaus(int expectedcount) throws VerzuimApplicationException{
		List<OeNiveauInfo> list;
		list = bean.getOeNiveaus();
		assertEquals("getList did not return expected count oeniveaus",list.size(),expectedcount);
	}
	
	@Test
	public void crudTestOe() throws SystemException, Exception{
		emptyTable();
		getListOe(0);
		getListOenivueaus(0);
		OeNiveauInfo n1 = addOeniveau("NIVEAU1",1, null);
		getListOenivueaus(1);
		OeNiveauInfo n2 = addOeniveau("NIVEAU2",2, n1);
		getListOenivueaus(2);
		OeInfo a1 = addOe("OE1",n1,null,1);
		getListOe(1);
		OeInfo a2 = addOe("OE2",n2,a1.getId(),1);
		getListOe(2);
		
		ArrayList<OeInfo> oes;
		oes = (ArrayList<OeInfo>) bean.getOesByNiveauId(n1.getId());
		assertEquals(1,oes.size());
		oes = (ArrayList<OeInfo>) bean.getOesByNiveauId(n2.getId());
		assertEquals(1,oes.size());
		oes = (ArrayList<OeInfo>) bean.getOesUnderId(a1.getId());
		assertEquals(1,oes.size());

		List<OeInfo> list = bean.getOes();
		for (OeInfo el:list){
			el.setNaam(el.getNaam()+" Updated");
			bean.updateOe(el);
		}

		List<OeNiveauInfo> list2 = bean.getOeNiveaus();
		for (OeNiveauInfo el:list2){
			el.setNaam(el.getNaam()+" Updated");
			bean.updateOeNiveau(el);
		}

		try{
			bean.deleteOe(a1);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Er bestaan nog onderliggende bedrijfseenheden. Verwijder deze eerst.",e.getMessage());
		}
		a2 = bean.getOeById(a2.getId());
		bean.deleteOe(a2);
		a1 = bean.getOeById(a1.getId());
		bean.deleteOe(a1);
		
		try{
			bean.deleteOeNiveau(n1);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Er bestaan nog onderliggende niveau's. Verwijder deze eerst.",e.getMessage());
		}
		n2 = bean.getOeNiveauById(n2.getId());
		bean.deleteOeNiveau(n2);
		n1 = bean.getOeNiveauById(n1.getId());
		bean.deleteOeNiveau(n1);
	}
	
	private OeNiveauInfo addOeniveau(String naam, Integer niveau, OeNiveauInfo parentoeniveau) throws ValidationException, VerzuimApplicationException {
		OeNiveauInfo g = new OeNiveauInfo();
		g.setNaam(naam);
		g.setOeniveau(niveau);
		if (parentoeniveau != null)
			g.setParentoeniveauId(parentoeniveau.getId());
		g =bean.addOeNiveau(g);
		OeNiveauInfo a;
		a = bean.getOeNiveauById(g.getId());
		assertNotNull(a);
		return a;
	}
	private OeInfo addOe(String naam, OeNiveauInfo oeniveau, Integer parentoeid, Integer werkgeverId) throws ValidationException, VerzuimApplicationException{
		OeInfo g = new OeInfo();
		g.setNaam(naam);
		g.setOeniveau(oeniveau);
		g.setParentoeId(parentoeid);
		g.setWerkgeverId(werkgeverId);
		g = bean.addOe(g);
		OeInfo a;
		a = bean.getOeById(g.getId());
		assertNotNull(a);
		return a;
	}
}
