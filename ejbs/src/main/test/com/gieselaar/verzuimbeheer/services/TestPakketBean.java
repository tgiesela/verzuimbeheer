package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
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

import com.gieselaar.verzuimbeheer.entities.Activiteit;
import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.RolApplicatiefunctiePK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakket;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakketPK;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;

@RunWith(Arquillian.class)
public class TestPakketBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(PakketBean.class)
				.addClass(PakketConversion.class)
				.addClass(ActiviteitBean.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB PakketBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Pakket").executeUpdate();
		em.createQuery("delete from WerkgeverHasPakket").executeUpdate();
		em.createQuery("delete from Activiteit").executeUpdate();
		em.createQuery("delete from PakketHasActiviteit").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestPakket() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em,utx);
		emptyTable();
		
		PakketInfo pakket1 = new PakketInfo();
		try{
			bean.addPakket(pakket1);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			pakket1.setNaam("PAKKET01");
			pakket1.setOmschrijving("Pakket 01");
			pakket1 = bean.addPakket(pakket1);
		}
		List<PakketInfo> pakketten = bean.allPakketen();
		assertEquals(1,pakketten.size());
		
		/* Voeg een paar activiteiten aan het pakket toe */
		Activiteit a1 = tu.addActiviteit("A1", 1, __periodesoort.DAGEN, __meldingsoort.NIEUWEZIEKMELDING, 2, false, true, false, null, 1, false);
		tu.AddPakketHasActiviteit(pakket1.getId(),a1.getId());
		Activiteit a2 = tu.addActiviteit("A2", 1, __periodesoort.DAGEN, __meldingsoort.GEDEELTELIJKHERSTEL, 2, false, true, false, null, 1, false);
		tu.AddPakketHasActiviteit(pakket1.getId(),a2.getId());
		pakketten = bean.allPakketen();
		assertEquals(1,pakketten.size());
		assertEquals(2,pakketten.get(0).getAktiviteiten().size());
		
		pakket1 = bean.getById(pakket1.getId());
		assertNotNull(pakket1);
		assertEquals(2,pakket1.getAktiviteiten().size());
		
		PakketInfo pakket2 = new PakketInfo();
		pakket2.setNaam("PAKKET02");
		pakket2.setOmschrijving("Pakket 02");
		pakket2 = bean.addPakket(pakket2);
		
		pakket2 = bean.getById(pakket2.getId());
		assertNotEquals(null, pakket2);

		ArrayList<PakketInfo> cclist = (ArrayList<PakketInfo>) bean.allPakketen();
		assertEquals(2,cclist.size());
		
		Integer wg = AddWerkgever("Werkgever met pakket");
		AddWerkgeverHasPakket(wg, pakket1.getId());

		cclist = (ArrayList<PakketInfo>) bean.getByWerkgever(wg);
		
		pakket1.setOmschrijving("Pakket 01 updated");
		bean.updatePakket(pakket1);
		
		pakket1 = bean.getById(pakket1.getId());
		
		cclist = (ArrayList<PakketInfo>) bean.allPakketen();
		for (PakketInfo c:cclist){
			bean.deletepakket(c);
		}
	}
	private void AddWerkgeverHasPakket(Integer wg, Integer p) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		WerkgeverHasPakket wp = new WerkgeverHasPakket();
		WerkgeverHasPakketPK wpPK = new WerkgeverHasPakketPK();
		wpPK.setPakket_ID(p);
		wpPK.setWerkgever_ID(wg);
		wp.setId(wpPK);
		em.persist(wp);
		utx.commit();
	}
	private Integer AddWerkgever(String naam) throws ValidationException, VerzuimApplicationException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		em.joinTransaction();
		Werkgever wg = new Werkgever();
		wg.setNaam(naam);
		wg.setStartdatumcontract(new Date());
		em.persist(wg);
		utx.commit();
		return wg.getId();
	}
}
