package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteit;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteitPK;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;
import com.gieselaar.verzuimbeheer.utils.SettingsConversion;
import com.gieselaar.verzuimbeheer.utils.VerzuimConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

@RunWith(Arquillian.class)
public class TestVerzuimBean {

	@PersistenceContext EntityManager em;
    @Resource UserTransaction utx;

    private int holding1;
    private int werkgever1;
    private int werkgever2;

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

	@Deployment 
	public static Archive<?> createDeployment() {
		JavaArchive ear = 
			ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(VerzuimBean.class)
				.addClass(WerkgeverBean.class)
				.addClass(AfdelingBean.class)
				.addClass(PakketBean.class)
				.addClass(ContactpersoonBean.class)
				.addClass(FactuurBean.class)
				.addClass(InstantieBean.class)
				.addClass(WerknemerBean.class)
				.addClass(ActiviteitBean.class)
				.addClass(SettingsBean.class)
				.addClass(AdresBean.class)
				.addClass(TariefBean.class)
				.addClass(VerzuimConversion.class)
				.addClass(FactuurConversion.class)
				.addClass(WerkgeverConversion.class)
				.addClass(AdresConversion.class)
				.addClass(PakketConversion.class)
				.addClass(ContactpersoonConversion.class)
				.addClass(InstantieConversion.class)
				.addClass(WerknemerConversion.class)
				.addClass(SettingsConversion.class)
				.addClass(AdresConversion.class)
				.addAsResource("resourcesglassfishembedded/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resourcesglassfishembedded/glassfish-resources.xml","META-INF/glassfish-resources.xml");
		return ear;

	}
    @EJB VerzuimBean bean;
    @EJB WerknemerBean wnr;
	private void emptyTable() throws Exception, SystemException{
		utx.begin();
		em.joinTransaction();
		em.createQuery("delete from Verzuim").executeUpdate();
		em.createQuery("delete from Verzuimherstel").executeUpdate();
		em.createQuery("delete from Verzuimdocument").executeUpdate();
		em.createQuery("delete from Verzuimactiviteit").executeUpdate();
		em.createQuery("delete from Verzuimmedischekaart").executeUpdate();
		em.createQuery("delete from Todo").executeUpdate();
		em.createQuery("delete from Werkgever").executeUpdate();
		em.createQuery("delete from Werknemer").executeUpdate();
		em.createQuery("delete from Dienstverband").executeUpdate();
		em.createQuery("delete from Afdeling").executeUpdate();
		em.createQuery("delete from Holding").executeUpdate();
		em.createQuery("delete from Documenttemplate").executeUpdate();
		em.createQuery("delete from Pakket").executeUpdate();
		em.createQuery("delete from WerkgeverHasPakket").executeUpdate();
		em.createQuery("delete from PakketHasActiviteit").executeUpdate();
		em.createQuery("delete from Activiteit").executeUpdate();
		utx.commit();
	}
	@Test
	public void crudTestVerzuimen() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);
		WerknemerInfo wni2 = wnr.getById(wn2);
		
		/* 
		 * Tests op overlappende verzuimen 
		 * ===============================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		VerzuimInfo vzm2 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Er is nog een open verzuim",e.getMessage());
		}
		/* Start voor start open verzuim, einde na start open verzuim */ 
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017,3, 22));
		vzm2.setEinddatumverzuim(TestUtils.getDate(2017,3,24));
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Einddatum overlapt met open verzuim",e.getMessage());
		}
		/* Start na start open verzuim, einde na start open verzuim */ 
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017,3, 24));
		vzm2.setEinddatumverzuim(TestUtils.getDate(2017,3,25));
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Startdatum overlapt met open verzuim",e.getMessage());
		}
		/* Update einddatum vzm1 (2017-03-23 t/m 2017-03-25 */
		vzm1.setEinddatumverzuim(TestUtils.getDate(2017, 3, 25));
		bean.updateVerzuim(vzm1);
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017, 3, 22));
		vzm2.setEinddatumverzuim(null);
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Startdatum overlapt met afgesloten verzuim",e.getMessage());
		}
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017, 3, 24));
		vzm2.setEinddatumverzuim(null);
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Startdatum overlapt met afgesloten verzuim",e.getMessage());
		}
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017, 3, 21));
		vzm2.setEinddatumverzuim(TestUtils.getDate(2017, 3, 25));
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Einddatum overlapt met afgesloten verzuim",e.getMessage());
		}
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017, 3, 21));
		vzm2.setEinddatumverzuim(TestUtils.getDate(2017, 3, 27));
		try{
			bean.addVerzuim(vzm2);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Einddatum overlapt met afgesloten verzuim",e.getMessage());
		}
		vzm2.setStartdatumverzuim(TestUtils.getDate(2017, 3, 26));
		vzm2.setEinddatumverzuim(null);
		vzm2 = bean.addVerzuim(vzm2);
		assertEquals(true, vzm2.getKetenverzuim());
		
		/* wnr4/dvb4 is een afgesloten dienstverband (2016,1,1)/(2016,12,31*/
		WerknemerInfo wni4 = wnr.getById(wn4);
		VerzuimInfo vzm3 = tu.addVerzuim(wni4.getDienstVerbanden().get(0), wni4, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		try{
			vzm3 = bean.addVerzuim(vzm3);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Startdatum verzuim ligt na einddatum dienstverband.",e.getMessage());			
		}
		
		List<VerzuimInfo> vzmlist = bean.getVerzuimenDienstverband(wni1.getActiefDienstverband().getId());
		assertEquals(2,vzmlist.size());
		
		bean.deleteVerzuim(vzmlist.get(0));

		vzmlist = bean.getVerzuimenDienstverband(wni1.getActiefDienstverband().getId());
		assertEquals(1,vzmlist.size());
	}
	@Test
	public void crudTestHerstellen() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);
		
		/* 
		 * Tests op verzuimherstellen 
		 * ===============================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		
		VerzuimHerstelInfo vh1 = new VerzuimHerstelInfo();
		vh1.setDatumHerstel(TestUtils.getDate(2017, 3, 20));
		vh1.setMeldingsdatum(vh1.getDatumHerstel());
		vh1.setPercentageHerstel(new BigDecimal(50.0));
		vh1.setPercentageHerstelAT(new BigDecimal(0.0));
		vh1.setMeldingswijze(__meldingswijze.TELEFOON);
		try{
			bean.addVerzuimHerstel(vh1, false);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Verzuim niet ingevuld.",e.getMessage());
		}
		vh1.setVerzuimId(vzm1.getId());
		try{
			bean.addVerzuimHerstel(vh1, false);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Hersteldatum ligt voor startdatum verzuim!",e.getMessage());
		}
		vh1.setDatumHerstel(TestUtils.getDate(2017, 3, 28));
		vh1.setMeldingsdatum(vh1.getDatumHerstel());
		vh1.setMeldingswijze(__meldingswijze.TELEFOON);
		bean.addVerzuimHerstel(vh1, false);
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(1,vzm1.getVerzuimherstellen().size());
		assertEquals(null, vzm1.getEinddatumverzuim());
		/* 50% herstel */
		
		VerzuimHerstelInfo vh2 = new VerzuimHerstelInfo();
		vh2.setDatumHerstel(TestUtils.getDate(2017, 3, 30));
		vh2.setMeldingsdatum(vh2.getDatumHerstel());
		vh2.setPercentageHerstel(new BigDecimal(100.0));
		vh2.setPercentageHerstelAT(new BigDecimal(0.0));
		vh2.setVerzuimId(vzm1.getId());
		vh2.setMeldingswijze(__meldingswijze.TELEFOON);
		bean.addVerzuimHerstel(vh2, true);
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		/* 100% herstel */

		VerzuimHerstelInfo vh3 = new VerzuimHerstelInfo();
		vh3.setDatumHerstel(TestUtils.getDate(2017, 3, 31));
		vh3.setMeldingsdatum(vh3.getDatumHerstel());
		vh3.setPercentageHerstel(new BigDecimal(100.0));
		vh3.setPercentageHerstelAT(new BigDecimal(0.0));
		vh3.setVerzuimId(vzm1.getId());
		vh3.setMeldingswijze(__meldingswijze.TELEFOON);
		try{
			bean.addVerzuimHerstel(vh3, true);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Verzuim is al afgesloten. U kunt geen nieuw herstel toevoegen!",e.getMessage());
		}
		
		/* wnr4/dvb4 is een afgesloten dienstverband (2016,1,1)/(2016,12,31*/
		WerknemerInfo wni4 = wnr.getById(wn4);
		VerzuimInfo vzm2 = tu.addVerzuim(wni4.getDienstVerbanden().get(0), wni4, TestUtils.getDate(2016, 3, 1), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		
		VerzuimHerstelInfo vh4 = new VerzuimHerstelInfo();
		vh4.setDatumHerstel(TestUtils.getDate(2017, 3, 30));
		vh4.setMeldingsdatum(vh4.getDatumHerstel());
		vh4.setPercentageHerstel(new BigDecimal(100.0));
		vh4.setPercentageHerstelAT(new BigDecimal(0.0));
		vh4.setVerzuim(vzm2);
		vh4.setMeldingswijze(__meldingswijze.TELEFOON);
		try{
			bean.addVerzuimHerstel(vh4,false);
			throw new RuntimeException("Should not arrive here!");
		}catch (ValidationException e){
			assertEquals("Datum verzuim overlapt met einddatum dienstverband!",e.getMessage());			
		}
		List<VerzuimHerstelInfo> vzmlist = bean.getVerzuimHerstellen(wni1.getActiefDienstverband().getId());
		assertEquals(2,vzmlist.size());
		
		vh4 = vzmlist.get(0);
		vh4.setVerzuim(vzm2);
		vh4.setMeldingswijze(__meldingswijze.BRIEF);
		bean.updateVerzuimHerstel(vh4, false);
		
		vzmlist = bean.getVerzuimHerstellen(wni1.getActiefDienstverband().getId());
		assertEquals(2,vzmlist.size());
		
		vh4 = vzmlist.get(0);
		bean.deleteVerzuimHerstel(vh4);
		vzmlist = bean.getVerzuimHerstellen(wni1.getActiefDienstverband().getId());
		assertEquals(1,vzmlist.size());
	}
	@Test
	public void crudTestDocumenten() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);

		/* 
		 * Tests op verzuimdocumenten 
		 * ===============================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		
		VerzuimDocumentInfo vzmdoc = new VerzuimDocumentInfo();
		vzmdoc.setAanmaakdatum(TestUtils.getDate(2017, 1, 1));
		vzmdoc.setAanmaakuser(1);
		vzmdoc.setDocumentnaam("Documentnaam");
		vzmdoc.setOmschrijving("Een document");
		vzmdoc.setPadnaam("\\server\\pad\\documentnaam.docx");
		vzmdoc.setVerzuimId(vzm1.getId());
		vzmdoc = bean.addVerzuimDocument(vzmdoc);
		
		vzmdoc.setDocumentnaam("Nieuw documentnaam");
		bean.updateVerzuimDocument(vzmdoc);
		
		List<VerzuimDocumentInfo> docs = bean.getVerzuimDocumenten(vzm1.getDienstverbandId());
		assertEquals(1,docs.size());
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(1,vzm1.getVerzuimdocumenten().size());
		
		bean.deleteVerzuimDocument(vzm1.getVerzuimdocumenten().get(0));
		
		docs = bean.getVerzuimDocumenten(vzm1.getId());
		assertEquals(0,docs.size());
	}
	@Test
	public void crudTestActiviteit() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);

		/* 
		 * Tests op verzuimactiviteiten 
		 * ===============================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		
		VerzuimActiviteitInfo vzmdoc = new VerzuimActiviteitInfo();
		vzmdoc.setActiviteitId(1);
		vzmdoc.setDatumactiviteit(TestUtils.getDate(2016, 1, 1));
		vzmdoc.setDatumdeadline(TestUtils.getDate(2016, 1, 2));
		vzmdoc.setUser(1);
		vzmdoc.setVerzuimId(vzm1.getId());
		vzmdoc = bean.addVerzuimActiviteit(vzmdoc);
		
		vzmdoc.setTijdbesteed(10);
		bean.updateVerzuimActiviteit(vzmdoc);
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(1,vzm1.getVerzuimactiviteiten().size());
		
		bean.deleteVerzuimActiviteit(vzm1.getVerzuimactiviteiten().get(0));
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(0,vzm1.getVerzuimactiviteiten().size());
		
	}
	@Test
	public void crudTestMedischekaart() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);

		/* 
		 * Tests op Medischekaarten 
		 * ========================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		
		VerzuimMedischekaartInfo vzmdoc = new VerzuimMedischekaartInfo();
		vzmdoc.setMedischekaart("Een beetje tekst");
		vzmdoc.setOpenbaar(false);
		vzmdoc.setUser(1);
		vzmdoc.setVerzuimId(vzm1.getId());
		vzmdoc.setWijzigingsdatum(TestUtils.getDate(2015, 12, 11));
		vzmdoc = bean.addMedischekaart(vzmdoc);
		
		vzmdoc.setMedischekaart("Aangepast");
		bean.updateMedischekaart(vzmdoc);
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(1,vzm1.getVerzuimmedischekaarten().size());
		
		bean.deleteMedischekaart(vzm1.getVerzuimmedischekaarten().get(0));
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(0,vzm1.getVerzuimmedischekaarten().size());
	}

	@Test
	public void crudTestTodo() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);

		/* 
		 * Tests op Todos 
		 * ========================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		
		TodoInfo todo = new TodoInfo();
		todo.setAanmaakdatum(TestUtils.getDate(2015, 3, 25));
		todo.setAchternaam("Achternaam");
		todo.setBurgerservicenummer("0120123010");
		todo.setDeadlinedatum(TestUtils.getDate(2017, 3, 25));
		todo.setHerhalen(true);
		todo.setOpmerking("");
		todo.setSoort(__soort.HANDMATIG);
		todo.setUser(1);
		todo.setVerzuim(vzm1);
		todo.setVerzuimId(vzm1.getId());
		todo.setWaarschuwingsdatum(TestUtils.getDate(2017, 1, 1));
		todo.setWerkgever(null);
		todo.setWerkgevernaam("Werkgevernaam");
		todo.setWerknemer(wni1);
		todo.setActiviteitId(1);
		todo = bean.addTodo(todo);
		todo = bean.getTodo(todo.getId());
		
		todo.setWerkgevernaam("Aangepast");
		bean.updateTodo(todo);
		
		TodoFastInfo todofast = bean.getTodoFast(todo.getId());
		assertEquals(todo.getId(), todofast.getId());
		
		List<TodoInfo> todos = bean.getTodos();
		assertEquals(1,todos.size());

		List<TodoFastInfo> todosfast = bean.getTodosFast();
		assertEquals(1,todosfast.size());
		
		todosfast = bean.getOpenTodosFast();
		assertEquals(1,todosfast.size());
		
		todosfast = bean.getClosedTodosFast();
		assertEquals(0,todosfast.size());

		todosfast = bean.getTodosVerzuim(vzm1.getId());
		assertEquals(1,todosfast.size());

		todosfast = bean.getTodosVerzuim(-1);
		assertEquals(0,todosfast.size());
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(1,vzm1.getTodos().size());
		
		bean.deleteTodo(vzm1.getTodos().get(0));
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(0,vzm1.getTodos().size());
	}
	@Test
	public void crudTestDocumenttemplates() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		DocumentTemplateInfo vzmdoc = new DocumentTemplateInfo();
		vzmdoc.setNaam("templatenaam");
		vzmdoc.setOmschrijving("omschrijving van template");
		vzmdoc.setPadnaam("\\server\\pad\\document");
		vzmdoc = bean.addDocumenttemplate(vzmdoc);
		
		vzmdoc.setOmschrijving("Nieuw omschrijving");
		bean.updateDocumenttemplate(vzmdoc);
		
		List<DocumentTemplateInfo> docs = bean.getDocumentTemplates();
		assertEquals(1,docs.size());
		
		bean.deleteDocumenttemplate(docs.get(0));
		
		docs = bean.getDocumentTemplates();
		assertEquals(0,docs.size());
	}
	@EJB PakketBean pkbean;
	@Test
	public void crudTestVerzuimTodos() throws SystemException, Exception{
		TestUtils tu = new TestUtils(em, utx);
		emptyTable();
		
		createstartdata(tu);
		WerknemerInfo wni1 = wnr.getById(wn1);

		createTodoConfig(tu);
		List<PakketInfo> pkgs = pkbean.getByWerkgever(werkgever1);

		pkgs = pkbean.allPakketen();
		assertEquals(1,pkgs.size());
		assertEquals(5,pkgs.get(0).getAktiviteiten().size());
		
		/* 
		 * Tests op genereren todo's 
		 * ===============================
		 */
		
		VerzuimInfo vzm1 = tu.addVerzuim(wni1.getActiefDienstverband(), wni1, TestUtils.getDate(2017, 3, 23), null,__vangnettype.NVT,__verzuimtype.VERZUIM);
		vzm1 = bean.addVerzuim(vzm1);
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		
		/* Er zijn twee Todos die worden gemaakt bij een nieuwe ziekmelding en aanvang verzuim, waarvan 1 herhaald wordt */
		assertEquals(3,vzm1.getTodos().size());

		VerzuimHerstelInfo vh1 = new VerzuimHerstelInfo();
		vh1.setDatumHerstel(TestUtils.getDate(2017, 3, 20));
		vh1.setMeldingsdatum(vh1.getDatumHerstel());
		vh1.setPercentageHerstel(new BigDecimal(50.0));
		vh1.setPercentageHerstelAT(new BigDecimal(0.0));
		vh1.setMeldingswijze(__meldingswijze.TELEFOON);
		vh1.setVerzuimId(vzm1.getId());
		vh1.setDatumHerstel(TestUtils.getDate(2017, 3, 28));
		vh1.setMeldingsdatum(vh1.getDatumHerstel());
		vh1.setMeldingswijze(__meldingswijze.TELEFOON);
		bean.addVerzuimHerstel(vh1, false);

		/* Er is slechts 1 Todo die wordt gemaakt bij een deelherstel */
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(3+1,vzm1.getTodos().size());
		for (TodoInfo todo:vzm1.getTodos()){
			VerzuimActiviteitInfo vzmact = new VerzuimActiviteitInfo();
			vzmact.setActiviteitId(todo.getActiviteitId());
			vzmact.setDatumactiviteit(TestUtils.getDate(2017, 1, 1));
			vzmact.setDatumdeadline(todo.getDeadlinedatum());
			vzmact.setVerzuimId(vzm1.getId());
			todo.setVerzuimactiviteitId(vzmact.getId());
			todo.setVerzuimActiviteit(vzmact);
			bean.updateTodo(todo);
		}
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(4, vzm1.getVerzuimactiviteiten().size());
		
		/* Nu moet door het afronden van de verzuimTODO van het deelherstel
		 * een nieuw deelherstel aangemaakt zijn
		 */
		assertEquals(3+1+1,vzm1.getTodos().size());
		
		VerzuimHerstelInfo vh2 = new VerzuimHerstelInfo();
		vh2.setDatumHerstel(TestUtils.getDate(2017, 3, 29));
		vh2.setMeldingsdatum(vh2.getDatumHerstel());
		vh2.setPercentageHerstel(new BigDecimal(100.0));
		vh2.setPercentageHerstelAT(new BigDecimal(0.0));
		vh2.setMeldingswijze(__meldingswijze.TELEFOON);
		vh2.setVerzuim(vzm1);
		bean.addVerzuimHerstel(vh2, false);
		
		vzm1 = bean.getVerzuimDetails(vzm1.getId());
		assertEquals(6, vzm1.getTodos().size());
	}
	private void createTodoConfig(TestUtils tu) throws SystemException, Exception {

		Integer p = tu.AddPakket("STANDAARD");
		tu.AddWerkgeverHasPakket(werkgever1, p);
		
		Activiteit a1 = tu.addActiviteit("A1", 1, __periodesoort.DAGEN, __meldingsoort.NIEUWEZIEKMELDING, 1, false, true, false, null, 1, false);
		tu.AddPakketHasActiviteit(p,a1.getId());
		Activiteit a11 = tu.addActiviteit("A11", 1, __periodesoort.DAGEN, __meldingsoort.ZIEKMELDING, 1, false, true, false, null, 0, false);
		tu.AddPakketHasActiviteit(p,a11.getId());
		Activiteit a2 = tu.addActiviteit("A2", 1, __periodesoort.DAGEN, __meldingsoort.GEDEELTELIJKHERSTEL, 1, false, true, false, null, 0, false);
		tu.AddPakketHasActiviteit(p,a2.getId());
		Activiteit a3 = tu.addActiviteit("A3", 1, __periodesoort.DAGEN, __meldingsoort.GEDEELTELIJKHERSTEL, 1, false, true, false, a2.getId(), 0, false);
		tu.AddPakketHasActiviteit(p,a3.getId());
		Activiteit a4 = tu.addActiviteit("A4", 1, __periodesoort.DAGEN, __meldingsoort.VOLLEDIGHERSTEL, 1, false, true, false, null, 0, false);
		tu.AddPakketHasActiviteit(p,a4.getId());
		
	}
	private void createstartdata(TestUtils tu) throws SystemException, Exception {
		holding1 = tu.addHolding("Holding1", true, TestUtils.getDate(2001, 1, 1), __factuurtype.NVT);
		werkgever1 = tu.addWerkgever("Werkgever1", holding1, true, TestUtils.getDate(2010, 1, 1));
		werkgever2 = tu.addWerkgever("Werkgever2", holding1, true, TestUtils.getDate(2010, 1, 1));

		Afdeling afd1 = tu.addAfdeling("Afdeling1", werkgever1);
		Afdeling afd2 = tu.addAfdeling("Afdeling2", werkgever2);

		wn1 = tu.addWerknemer("Werknemer1",werkgever1);
		wn2 = tu.addWerknemer("Werknemer2",werkgever1);
		wn3 = tu.addWerknemer("Werknemer3",werkgever1);
		wn4 = tu.addWerknemer("Werknemer4",werkgever1);
		wn5 = tu.addWerknemer("Werknemer5",werkgever2);
		wn6 = tu.addWerknemer("Werknemer6",werkgever2);
		wn7 = tu.addWerknemer("Werknemer7",werkgever2);
		wn8 = tu.addWerknemer("Werknemer8",werkgever2);
		
		dvb1 = tu.addDienstverband(wn1,werkgever1, TestUtils.getDate(2016,1,1),null, afd1);
		dvb2 = tu.addDienstverband(wn2,werkgever1, TestUtils.getDate(2016,1,1),null, afd1);
		dvb3 = tu.addDienstverband(wn3,werkgever1, TestUtils.getDate(2016,1,1),null, afd2);
		dvb4 = tu.addDienstverband(wn4,werkgever1, TestUtils.getDate(2016,1,1),TestUtils.getDate(2016,12,31), afd2);
		tu.connectAfdeling(wn4, afd1, TestUtils.getDate(2017,1,1), null); /* wn4 heeft twee afdelingen gehad */

	}
}
