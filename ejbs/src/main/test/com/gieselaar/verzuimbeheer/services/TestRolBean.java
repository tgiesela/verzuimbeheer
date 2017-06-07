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

import com.gieselaar.verzuimbeheer.entities.Gebruiker;
import com.gieselaar.verzuimbeheer.entities.GebruikerRol;
import com.gieselaar.verzuimbeheer.entities.GebruikerRolPK;
import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctiePK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.AutorisatieConversion;

@RunWith(Arquillian.class)
public class TestRolBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(RolBean.class)
				.addClass(AutorisatieConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB RolBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Rol").executeUpdate();
		em.createQuery("delete from Gebruiker").executeUpdate();
		em.createQuery("delete from RolApplicatiefunctie").executeUpdate();
		em.createQuery("delete from GebruikerRol").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestRol() throws SystemException, Exception{
		emptyTable();
		
		RolInfo cg = new RolInfo();
		try{
			bean.addRol(cg);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			cg.setRolid("Rol01");
			cg.setOmschrijving("Rol 01");
			cg = bean.addRol(cg);
		}
		
		RolInfo cg2 = new RolInfo();
		cg2.setRolid("Rol02");
		cg2.setOmschrijving("Rol 02");
		cg2 = bean.addRol(cg2);
		
		cg2 = bean.getById(cg2.getId());
		assertNotEquals(null, cg2);

		ArrayList<RolInfo> cclist = (ArrayList<RolInfo>) bean.getRollen();
		assertEquals(2,cclist.size());
		
		/* Opvragen alle rollen */
		cclist = (ArrayList<RolInfo>) bean.getRollen();
		assertEquals(2,cclist.size());
		
		for (RolInfo r:cclist){
			r.setOmschrijving(r.getOmschrijving()+ "aangepast");
			bean.updateRol(r);
		}
		cclist = (ArrayList<RolInfo>) bean.getRollen();
		assertEquals(2,cclist.size());
		for (RolInfo r:cclist){
			assertEquals(true, r.getOmschrijving().contains("aangepast"));
		}
		
		/* Opvragen alle rollen van een gebruiker */
		Integer g = addGebruiker("tonny");
		addGebruikerHasRol(g, cg2.getId());
		cclist = (ArrayList<RolInfo>) bean.getByUser(g);
		assertEquals(1,cclist.size());
		assertEquals(cg2.getId(),cclist.get(0).getId());
		
		cclist = (ArrayList<RolInfo>) bean.getRollen();
		for (RolInfo c:cclist){
			bean.deleterol(c);
		}
	}
	private void addGebruikerHasRol(Integer g, Integer r) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		GebruikerRol gr = new GebruikerRol();
		GebruikerRolPK grpk = new GebruikerRolPK();
		grpk.setGebruikerid(g);
		grpk.setRolid(r);
		gr.setId(grpk);
		em.persist(gr);
		utx.commit();
	}
	private Integer addGebruiker(String naam) throws ValidationException, VerzuimApplicationException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		em.joinTransaction();
		Gebruiker wg = new Gebruiker();
		wg.setAchternaam("Gebruiker");
		wg.setAduser(0);
		wg.setEmailadres("a@b.nl");
		wg.setGebruikersnaam(naam);
		wg.setInlogfouten(0);
		wg.setVoornaam("voornaam");
		em.persist(wg);
		utx.commit();
		return wg.getId();
	}
}
