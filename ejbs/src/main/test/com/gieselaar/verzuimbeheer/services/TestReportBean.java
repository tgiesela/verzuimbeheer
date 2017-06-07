package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemer;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemerPK;
import com.gieselaar.verzuimbeheer.entities.Dienstverband;
import com.gieselaar.verzuimbeheer.entities.Holding;
import com.gieselaar.verzuimbeheer.entities.Oe;
import com.gieselaar.verzuimbeheer.entities.Oeniveau;
import com.gieselaar.verzuimbeheer.entities.Verzuim;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.VerzuimAantalInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerAantallenInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.ReportConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

@RunWith(Arquillian.class)
public class TestReportBean {

	private Integer wgzonderhold;
	private Integer hold;
	private Integer wgmethold;

	private Integer wn1 ;
	private Integer wn2 ;
	private Integer wn3 ;
	private Integer wn4 ;
	private Integer wn5 ;
	private Integer wn6 ;
	private Integer wn7 ;
	private Integer wn8 ;
	private Integer dvb1;
	private Integer dvb2;
	private Integer dvb3;
	private Integer dvb4;
	private Integer dvb5;
	private Integer dvb6;
	private Integer dvb7;
	private Integer dvb8;
	private Oeniveau oen1;
	private Oeniveau oen2;
	private Oeniveau oen3;
	private Oe oe1;
	private Oe oe2;
	private Oe oe3;

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;
	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(ReportBean.class)
				.addClass(ReportConversion.class)
				.addClass(WerkgeverBean.class)
				.addClass(WerkgeverConversion.class)
				.addClass(OeBean.class)
				.addClass(AfdelingBean.class)
				.addClass(AdresConversion.class)
				.addClass(TariefBean.class)
				.addClass(PakketBean.class)
				.addClass(PakketConversion.class)
				.addClass(ContactpersoonBean.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(InstantieBean.class)
				.addClass(InstantieConversion.class)
				.addClass(FactuurBean.class)
				.addClass(FactuurConversion.class)
				.addClass(WerknemerBean.class)
				.addClass(WerknemerConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB ReportBean bean;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Holding").executeUpdate();
		em.createQuery("delete from Oe").executeUpdate();
		em.createQuery("delete from Oeniveau").executeUpdate();
		em.createQuery("delete from Verzuim").executeUpdate();
		em.createQuery("delete from Werknemer").executeUpdate();
		em.createQuery("delete from Dienstverband").executeUpdate();
		em.createQuery("delete from Afdeling").executeUpdate();
		em.createQuery("delete from AfdelingHasWerknemer").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestAantalVerzuimen() throws SystemException, Exception{
		emptyTable();
		createWerkgeverAndWerknemers();
		List<VerzuimAantalInfo> list;
		try{
			list = bean.getAantalverzuimenInPeriode(null, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
			throw new RuntimeException("Should not arrive here!");
		} catch (ValidationException e){
			assertEquals("Minimaal een van holdingid, werkgeverid of oeid moet ingevuld zijn", e.getMessage());
		}
		list = bean.getAantalverzuimenInPeriode(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
		assertEquals(0, list.size());
		
		addVerzuim(dvb1,TestUtils.getDate(2016,2,1),TestUtils.getDate(2016,2,10),__vangnettype.NVT);
		list = bean.getAantalverzuimenInPeriode(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2016,1,31), true);
		assertEquals(0, list.size());

		list = bean.getAantalverzuimenInPeriode(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2016,2,1), true);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getAantalverzuimen().intValue());
		assertEquals(wn1,list.get(0).getWerknemerid());

		list = bean.getAantalverzuimenInPeriode(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
		assertEquals(1, list.size());
		
		addVerzuim(dvb4,TestUtils.getDate(2016,2,1),TestUtils.getDate(2016,2,10),__vangnettype.NVT);
		list = bean.getAantalverzuimenInPeriode(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
		assertEquals(2, list.size());
		assertEquals(1, list.get(0).getAantalverzuimen().intValue());
		assertEquals(1, list.get(1).getAantalverzuimen().intValue());

		addVerzuim(dvb6,TestUtils.getDate(2016,2,1),TestUtils.getDate(2016,2,10),__vangnettype.NVT);
		addVerzuim(dvb5,TestUtils.getDate(2016,10,10),null,__vangnettype.ZWANGERSCHAP);
		list = bean.getAantalverzuimenInPeriode(wgmethold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getAantalverzuimen().intValue());
		/* Zelfde query, maar nu inclusief zwangerschap */
		list = bean.getAantalverzuimenInPeriode(wgmethold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
		assertEquals(2, list.size());

		list = bean.getAantalverzuimenInPeriode(null, hold, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getAantalverzuimen().intValue());

		list = bean.getAantalverzuimenInPeriode(null, null, oe1.getId(), TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getAantalverzuimen().intValue());
		
		ArrayList<WerknemerAantallenInfo> wnrcnt;
		wnrcnt = (ArrayList<WerknemerAantallenInfo>) bean.getAantalWerknemersAfdeling(wgmethold, TestUtils.getDate(2017,1,1));
		assertEquals(2,wnrcnt.size());
		assertEquals(2,wnrcnt.get(0).getAantalwerknemers().intValue());
		assertEquals(1,wnrcnt.get(1).getAantalwerknemers().intValue());
		wnrcnt = (ArrayList<WerknemerAantallenInfo>) bean.getAantalWerknemersAfdeling(wgmethold, TestUtils.getDate(2017,1,1),TestUtils.getDate(2017,1,1));
		assertEquals(2,wnrcnt.size());
		assertEquals(2,wnrcnt.get(0).getAantalwerknemers().intValue());
		assertEquals(1,wnrcnt.get(1).getAantalwerknemers().intValue());
		
		ArrayList<ActueelVerzuimInfo> actvzm;
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(wgmethold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(1,actvzm.size());
		/* Zelfde query maar nu inclusief zwangerschap */
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(wgmethold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
		assertEquals(2,actvzm.size());

		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(2,actvzm.size());
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(null, hold, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(1,actvzm.size());
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(null, null, oe1.getId(), TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(1,actvzm.size());
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), false);
		assertEquals(2,actvzm.size());
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getActueelVerzuim(wgzonderhold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), true);
		assertEquals(2,actvzm.size());
		
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getVerzuimPerMaand(wgmethold, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2016,12,31), false);
		assertEquals(25,actvzm.size()); /* 24 maanden: 1 line for each month + 1 verzuim = 25 */
		ArrayList<WerkgeverInfo> wgrs;
		wgrs = (ArrayList<WerkgeverInfo>) bean.getWerkgevers(null, hold);
		assertEquals(1, wgrs.size());
		wgrs = (ArrayList<WerkgeverInfo>) bean.getWerkgevers(wgmethold,null);
		assertEquals(1, wgrs.size());
		
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getWerknemerVerzuimen(wn1, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31));
		assertEquals(1,actvzm.size());
		
		actvzm = (ArrayList<ActueelVerzuimInfo>) bean.getWerknemerVerzuimen(wn5, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31));
		assertEquals(1,actvzm.size());
		
		ArrayList<WerknemerVerzuimInfo> wnrvzm;
		wnrvzm = (ArrayList<WerknemerVerzuimInfo>) bean.getWerknemerVerzuimen(wgmethold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), 1);
		assertEquals(2,wnrvzm.size());
		wnrvzm = (ArrayList<WerknemerVerzuimInfo>) bean.getWerknemerVerzuimen(null, hold, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), 1);
		assertEquals(2,wnrvzm.size());
		wnrvzm = (ArrayList<WerknemerVerzuimInfo>) bean.getWerknemerVerzuimen(null, null, oe1.getId(), TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), 1);
		assertEquals(2,wnrvzm.size());
		wnrvzm = (ArrayList<WerknemerVerzuimInfo>) bean.getWerknemerVerzuimen(wgmethold, null, null, TestUtils.getDate(2016,1,1), TestUtils.getDate(2017,1,31), 2);
		assertEquals(0,wnrvzm.size());
	}
	private void addVerzuim(Integer dvb, Date start, Date einde, __vangnettype type) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Verzuim wg = new Verzuim();
		wg.setDienstverband_ID(dvb);
		wg.setEinddatumverzuim(einde);
		wg.setGerelateerdheid(__gerelateerdheid.NVT.getValue());
		wg.setKetenverzuim(0);
		wg.setMeldingsdatum(new Date());
		wg.setMeldingswijze(__meldingswijze.TELEFOON.getValue());
		wg.setOpmerkingen("");
		wg.setStartdatumverzuim(start);
		wg.setVangnettype(type.getValue());
		wg.setUitkeringnaarwerknemer(0);
		wg.setVerzuimtype(__verzuimtype.VERZUIM.getValue());
		em.persist(wg);
		em.flush();
		utx.commit();
	}
	private void createWerkgeverAndWerknemers() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		wgzonderhold = addWerkgever("Werkgever zonder Holding", null);
		hold = addHolding("Holding");
		wgmethold = addWerkgever("Werkgever met Holding", hold);
		
		oen1 = addOeniveau("HOLDING", 1, null);
		oen2 = addOeniveau("REGIO",2, oen1);
		oen3 = addOeniveau("KANTOOR",3,oen2);
		oe1 = addOe("KFC",oen1,null,null);
		oe2 = addOe("WEST",oen2,oe1.getId(),null);
		oe3 = addOe("Een kantoor", oen3, oe2.getId(), wgmethold);
		
		Afdeling afd1 = tu.addAfdeling("Afdeling1", wgzonderhold);
		Afdeling afd2 = tu.addAfdeling("Afdeling2", wgzonderhold);

		Afdeling afd3 = tu.addAfdeling("Afdeling3", wgmethold);
		Afdeling afd4 = tu.addAfdeling("Afdeling4", wgmethold);
		
		wn1 = tu.addWerknemer("Werknemer1",wgzonderhold);
		wn2 = tu.addWerknemer("Werknemer2",wgzonderhold);
		wn3 = tu.addWerknemer("Werknemer3",wgzonderhold);
		wn4 = tu.addWerknemer("Werknemer4",wgzonderhold);
		wn5 = tu.addWerknemer("Werknemer5",wgmethold);
		wn6 = tu.addWerknemer("Werknemer6",wgmethold);
		wn7 = tu.addWerknemer("Werknemer7",wgmethold);
		wn8 = tu.addWerknemer("Werknemer8",wgmethold);
		
		dvb1 = tu.addDienstverband(wn1,wgzonderhold, TestUtils.getDate(2016,1,1),null, afd1);
		dvb2 = tu.addDienstverband(wn2,wgzonderhold, TestUtils.getDate(2016,1,1),null, afd1);
		dvb3 = tu.addDienstverband(wn3,wgzonderhold, TestUtils.getDate(2016,1,1),null, afd2);
		dvb4 = tu.addDienstverband(wn4,wgzonderhold, TestUtils.getDate(2016,1,1),TestUtils.getDate(2016,12,31), afd2);
		tu.connectAfdeling(wn4, afd1, TestUtils.getDate(2017,1,1), null); /* wn4 heeft twee afdelingen gehad */

		dvb6 = tu.addDienstverband(wn6,wgmethold, TestUtils.getDate(2016,1,1),null,afd3);
		dvb7 = tu.addDienstverband(wn7,wgmethold, TestUtils.getDate(2016,1,1),null,afd4);
		dvb8 = tu.addDienstverband(wn8,wgmethold, TestUtils.getDate(2016,1,1),TestUtils.getDate(2016,12,31),afd4);
		dvb5 = tu.addDienstverband(wn5,wgmethold, TestUtils.getDate(2016,1,1),null,afd3);
	}
	private Integer addWerkgever(String naam, Integer holding) throws ValidationException, VerzuimApplicationException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		em.joinTransaction();
		Werkgever wg = new Werkgever();
		wg.setNaam(naam);
		wg.setStartdatumcontract(new Date());
		wg.setHolding_ID(holding);
		wg.setWerkweek(new BigDecimal(40));
		em.persist(wg);
		utx.commit();
		return wg.getId();
	}
	private Integer addHolding(String naam) throws ValidationException, VerzuimApplicationException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		utx.begin();
		em.joinTransaction();
		Holding wg = new Holding();
		wg.setNaam(naam);
		wg.setStartdatumcontract(new Date());
		wg.setVestigingsadres(new Adres());
		wg.getVestigingsadres().setStraat("Binnenweg");
		wg.setFactureren(0);
		em.persist(wg);
		utx.commit();
		return wg.getId();
	}
	private Oeniveau addOeniveau(String naam, Integer niveau, Oeniveau parentoeniveau) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Oeniveau g = new Oeniveau();
		g.setNaam(naam);
		g.setOeniveau(niveau);
		if (parentoeniveau == null){
			g.setParentoeniveauId(null);
		}else{
			g.setParentoeniveauId(parentoeniveau.getId());
		}
		em.persist(g);
		utx.commit();
		return g;
	}
	private Oe addOe(String naam, Oeniveau oeniveau, Integer parentoeid, Integer werkgeverId) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		Oe g = new Oe();
		g.setNaam(naam);
		g.setOeniveau(oeniveau);
		g.setParentoeId(parentoeid);
		g.setWerkgeverId(werkgeverId);
		em.persist(g);
		utx.commit();
		return g;
	}
}
