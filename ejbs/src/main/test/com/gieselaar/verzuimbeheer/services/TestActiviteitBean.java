package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

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

import com.gieselaar.verzuimbeheer.entities.Pakket;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteit;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteitPK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakket;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakketPK;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;

@RunWith(Arquillian.class)
public class TestActiviteitBean {
	private TestUtils tu;
	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(ActiviteitBean.class)
				.addClass(PakketConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
	public TestActiviteitBean(){
	}
    @EJB ActiviteitBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		System.out.println("Deleting old records...");
		em.createQuery("delete from Activiteit").executeUpdate();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Pakket").executeUpdate();
		em.createQuery("delete from WerkgeverHasPakket").executeUpdate();
		utx.commit();
	}
	public void getList(int expectedcount) throws VerzuimApplicationException{
		List<ActiviteitInfo> activiteiten;
		activiteiten = bean.allActiviteiten();
		assertEquals("getList did not return zero activities",activiteiten.size(),expectedcount);
	}
	
	@Test
	public void crudTestActiviteit() throws SystemException, Exception{
		tu = new TestUtils(em, utx);
		emptyTable();
		getList(0);
		ActiviteitInfo a1 = addActiviteit("testact1");
		getList(1);
		ActiviteitInfo a2 = addActiviteit("testact2");
		getList(2);
		
		List<ActiviteitInfo> activiteiten = bean.allActiviteiten();
		for (ActiviteitInfo g:activiteiten){
			g.setDeadlineperiode(10);
			g.setDeadlineperiodesoort(__periodesoort.DAGEN);
			g.setDeadlinestartpunt(__meldingsoort.GEDEELTELIJKHERSTEL);
			g.setDeadlinewaarschuwmoment(20);
			g.setDuur("10");
			g.setKetenverzuim(true);
			g.setNormaalverzuim(false);
			g.setOmschrijving("Zomaar wat");
			g.setPlannaactiviteit(21);
			g.setRepeteeraantal(5);
			g.setRepeteerperiode(6);
			g.setRepeteerperiodesoort(__periodesoort.MAANDEN);
			g.setVangnet(true);
			g.setVerwijdernaherstel(true);
			bean.updateActiviteit(g);
		}
		
		Integer p = tu.AddPakket("STANDAARD");
		Integer wg = tu.addWerkgever("Werkgever met pakket", null, false, TestUtils.getDate(2010, 1, 1));
		tu.AddWerkgeverHasPakket(wg, p);
		tu.AddPakketHasActiviteit(p,a1.getId());
		
		activiteiten = bean.allActiviteiten(wg);
		assertEquals(1,activiteiten.size());
			
		activiteiten = bean.allActiviteiten();
		assertEquals(2,activiteiten.size());
		for (ActiviteitInfo g:activiteiten){
			bean.deleteActiviteit(g);
		}
		getList(0);
	}
	
	private ActiviteitInfo addActiviteit(String naam) throws ValidationException, VerzuimApplicationException{
		ActiviteitInfo g = new ActiviteitInfo();
		g.setNaam(naam);
		g.setDeadlineperiode(1);
		g.setDeadlineperiodesoort(__periodesoort.WEKEN);
		g.setDeadlinestartpunt(__meldingsoort.NIEUWEZIEKMELDING);
		g.setDeadlinewaarschuwmoment(10);
		g.setDuur("5");
		g.setKetenverzuim(false);
		g.setNormaalverzuim(true);
		g.setOmschrijving("Echt waar");
		g.setPlannaactiviteit(1);
		g.setRepeteeraantal(2);
		g.setRepeteerperiode(3);
		g.setRepeteerperiodesoort(__periodesoort.DAGEN);
		g.setVangnet(false);
		g.setVerwijdernaherstel(false);
		bean.addActiviteit(g);
		ActiviteitInfo a;
		a = bean.getById(g.getId());
		assertNotNull(a);
		return a;
	}
}
