package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
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

import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctiePK;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.CascodeConversion;

@RunWith(Arquillian.class)
public class TestCascodeBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(CascodeBean.class)
				.addClass(CascodeConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB CascodeBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Cascode").executeUpdate();
		em.createQuery("delete from Cascodegroep").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestApplicatieFuncties() throws SystemException, Exception{
		emptyTable();
		
		CascodeGroepInfo cg = new CascodeGroepInfo();
		try{
			bean.addCascodeGroep(cg);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			cg.setNaam("CG01");
			cg.setOmschrijving("Groep 001");
			cg = bean.addCascodeGroep(cg);
		}
		
		CascodeGroepInfo cg2 = new CascodeGroepInfo();
		cg2.setNaam("CG02");
		cg2.setOmschrijving("Groep 002");
		cg2 = bean.addCascodeGroep(cg2);
		CascodeInfo cc = new CascodeInfo();
		try{
			bean.addCascode(cc);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			cc.setCascode("CC001");
			cc.setOmschrijving("Cascode 001");
			cc.setCascodegroep(cg.getId());
			cc.setVangnettype(__vangnettype.NVT);
			cc = bean.addCascode(cc);
		}
		CascodeInfo cc2 = new CascodeInfo();
		cc2.setCascode("CC002");
		cc2.setOmschrijving("Cascode 002");
		cc2.setCascodegroep(cg2.getId());
		cc2.setVangnettype(__vangnettype.ZIEKDOORZWANGERSCHAP);
		cc2 = bean.addCascode(cc2);

		ArrayList<CascodeInfo> cclist = (ArrayList<CascodeInfo>) bean.allCascodes();
		assertEquals(2,cclist.size());
		
		ArrayList<CascodeGroepInfo> cglist = (ArrayList<CascodeGroepInfo>) bean.allCascodeGroepen();
		assertEquals(2,cglist.size());

		cclist = (ArrayList<CascodeInfo>) bean.allCascodesForGroep(cg.getId());
		assertEquals(1,cclist.size());
		
		cc.setOmschrijving("Cascode 1");
		bean.updateCascode(cc);

		cg.setOmschrijving("Groep 1");
		bean.updateCascodeGroep(cg);
		
		cglist = (ArrayList<CascodeGroepInfo>) bean.allCascodeGroepen();
		for (CascodeGroepInfo c:cglist){
			try{
				bean.deleteCascodeGroep(c);
				throw new RuntimeException("Should not arrive here!");
			}catch(ValidationException e){
				assertEquals("Er zijn nog cascodes gekoppeld aan deze groep",e.getMessage());
			}
		}

		cclist = (ArrayList<CascodeInfo>) bean.allCascodes();
		for (CascodeInfo c:cclist){
			bean.deleteCascode(c);
		}
		cglist = (ArrayList<CascodeGroepInfo>) bean.allCascodeGroepen();
		for (CascodeGroepInfo c:cglist){
			bean.deleteCascodeGroep(c);
		}
	}
}
