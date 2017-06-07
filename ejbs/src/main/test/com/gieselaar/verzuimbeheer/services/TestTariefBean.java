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

import com.gieselaar.verzuimbeheer.entities.Adres;
import com.gieselaar.verzuimbeheer.entities.Holding;
import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctiePK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakket;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakketPK;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.SettingsConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

@RunWith(Arquillian.class)
public class TestTariefBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(TariefBean.class)
				.addClass(WerkgeverBean.class)
				.addClass(AfdelingBean.class)
				.addClass(PakketBean.class)
				.addClass(ContactpersoonBean.class)
				.addClass(FactuurBean.class)
				.addClass(InstantieBean.class)
				.addClass(WerknemerBean.class)
				.addClass(FactuurConversion.class)
				.addClass(WerkgeverConversion.class)
				.addClass(AdresConversion.class)
				.addClass(PakketConversion.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(InstantieConversion.class)
				.addClass(WerknemerConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB TariefBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Tarief").executeUpdate();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Holding").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestTarief() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		TariefInfo info = new TariefInfo();
		int holding = tu.addHolding("Holding", true, TestUtils.getDate(2001, 1, 1), __factuurtype.NVT);
		int holding2 = tu.addHolding("Holding2", true, TestUtils.getDate(2001, 1, 1), __factuurtype.NVT);
		int werkgever = tu.addWerkgever("Werkgever", holding, true, TestUtils.getDate(2010, 1, 1));
		info.setWerkgeverId(werkgever);
		info.setIngangsdatum(TestUtils.getDate(2015, 1, 1));
		info.setAansluitkostenPeriode(__tariefperiode.MAAND);
		info.setAbonnementPeriode(__tariefperiode.MAAND);
		bean.addTarief(info);
		
		/* nu een overlappend tarief, ingangsdatum voor actueel tarief */ 
		TariefInfo overlap1 = new TariefInfo();
		overlap1.setHoldingId(null);
		overlap1.setWerkgeverId(werkgever);
		overlap1.setIngangsdatum(TestUtils.getDate(2014, 1, 1));
		overlap1.setAansluitkostenPeriode(__tariefperiode.MAAND);
		overlap1.setAbonnementPeriode(__tariefperiode.MAAND);
		try{
			bean.addTarief(overlap1);
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals("Ingangsdatum tarief overlapt met reeds bestaand tarief",e.getMessage());
		}

		/* nu een overlappend tarief, ingangsdatum na actueel tarief
		 * Het oude tarief wordt automatisch afgesloten
		 *  */ 
		overlap1.setIngangsdatum(TestUtils.getDate(2015, 6, 1));
		bean.addTarief(overlap1);
		/*
		 * Nu hebben we:	2015-01-01 tot 2015-05-31
		 * 			     en 2015-06-01 tot heden
		 */
		
		/*
		 * Holding
		 */
		TariefInfo info2 = new TariefInfo();
		info2.setWerkgeverId(null);
		info2.setHoldingId(holding);
		info2.setIngangsdatum(TestUtils.getDate(2015, 1, 1));
		info2.setAansluitkostenPeriode(__tariefperiode.MAAND);
		info2.setAbonnementPeriode(__tariefperiode.MAAND);
		try{
			bean.addTarief(info2);
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals(true, e.getMessage().startsWith("Er zijn al recentere tarieven bij werkgever:"));
		}
		info2.setHoldingId(holding2);
		bean.addTarief(info2);
		
		/* nu een overlappend tarief, ingangsdatum voor actueel tarief */ 
		TariefInfo overlap2 = new TariefInfo();
		overlap2.setWerkgeverId(null);
		overlap2.setHoldingId(holding2);
		overlap2.setIngangsdatum(TestUtils.getDate(2014, 1, 1));
		overlap2.setAansluitkostenPeriode(__tariefperiode.MAAND);
		overlap2.setAbonnementPeriode(__tariefperiode.MAAND);
		try{
			bean.addTarief(overlap2);
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals("Ingangsdatum tarief overlapt met reeds bestaand tarief",e.getMessage());
		}

		/* nu een overlappend tarief, ingangsdatum na actueel tarief
		 * Het oude tarief wordt automatisch afgesloten
		 *  */ 
		overlap2.setIngangsdatum(TestUtils.getDate(2015, 6, 1));
		bean.addTarief(overlap2);
		/*
		 * Nu hebben we:	2015-01-01 tot 2015-05-31
		 * 			     en 2015-06-01 tot heden
		 */
		
		
		info.setHoldingId(holding2);
		info.setWerkgeverId(null);
		info.setEinddatum(null);
		try{
			bean.updateTarief(info); /* Tarief = 2015-1-1 tot heden */
		}catch (ValidationException e){
			assertEquals("Kan niet bestaand tarief niet wijzigen",e.getMessage());
		}
		info = bean.getActueelTariefByHolding(holding2);
		info.setIngangsdatum(TestUtils.getDate(2014, 2, 1));
		info.setEinddatum(TestUtils.getDate(2014, 2, 5));
		try{
			bean.addTarief(info);
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals("Ingangsdatum tarief overlapt met reeds bestaand tarief",e.getMessage());
		}
		
		info2.setHoldingId(null);
		info2.setWerkgeverId(werkgever);
		info2.setEinddatum(TestUtils.getDate(2014, 12, 1));
		try{
			bean.updateTarief(info2); /* Tarief = 2015-1-1 tot heden */
		}catch (ValidationException e){
			assertEquals("Kan niet bestaand tarief niet wijzigen",e.getMessage());
		}
		info2 = bean.getActueelTariefByWerkgever(werkgever);
		info2.setIngangsdatum(TestUtils.getDate(2014, 2, 1));
		info2.setEinddatum(TestUtils.getDate(2014, 2, 5));
		try{
			bean.addTarief(info2);
			throw new RuntimeException("Should not arrive here");
		}catch(ValidationException e){
			assertEquals("Ingangsdatum tarief overlapt met reeds bestaand tarief",e.getMessage());
		}
	}
}
