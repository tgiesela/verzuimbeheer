package com.gieselaar.verzuimbeheer.services;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.gieselaar.verzuimbeheer.entities.Activiteit;
import com.gieselaar.verzuimbeheer.entities.Adres;
import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemer;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemerPK;
import com.gieselaar.verzuimbeheer.entities.Dienstverband;
import com.gieselaar.verzuimbeheer.entities.Holding;
import com.gieselaar.verzuimbeheer.entities.Pakket;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteit;
import com.gieselaar.verzuimbeheer.entities.PakketHasActiviteitPK;
import com.gieselaar.verzuimbeheer.entities.Settings;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakket;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakketPK;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;

public class TestUtils {
	EntityManager em;
    UserTransaction utx;
    public TestUtils(EntityManager em, UserTransaction utx){
    	this.em = em;
    	this.utx = utx;
    }
	public static Date getDate(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		return cal.getTime();
	}
	public int addWerkgever(String naam, Integer Holding, boolean factureren, Date startdatum) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Werkgever wg = new Werkgever();
		wg.setStartdatumcontract(startdatum);
		wg.setNaam(naam);
		wg.setVestigingsAdres(new Adres());
		wg.getVestigingsAdres().setStraat("Binnenweg");
		wg.setHolding_ID(Holding);
		wg.setFactureren(factureren?1:0);
		em.persist(wg);
		em.flush();
		utx.commit();
		return wg.getId();
	}
	public int addHolding(String naam, boolean factureren, Date date, __factuurtype type) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Holding h = new Holding();
		h.setStartdatumcontract(date);
		h.setNaam(naam);
		h.setVestigingsadres(new Adres());
		h.getVestigingsadres().setStraat("Binnenweg");
		h.setFactureren(factureren?1:0);
		h.setFactuurtype(type.getValue());
		em.persist(h);
		em.flush();
		utx.commit();
		return h.getId();
	}
	public int addWerknemer(String string, Integer werkgever) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Werknemer wg = new Werknemer();
		wg.setAchternaam(string);
		wg.setWerkgever_ID(werkgever);
		em.persist(wg);
		em.flush();
		utx.commit();
		return wg.getId();
	}
	public Integer addDienstverband(Integer wn, Integer wg, Date start, Date einde, Afdeling afd)throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Dienstverband dvb = new Dienstverband();
		dvb.setWerknemer_ID(wn);
		dvb.setWerkgever_ID(wg);
		dvb.setEinddatumcontract(einde);
		dvb.setStartdatumcontract(start);
		dvb.setWerkweek(new BigDecimal(30));
		em.persist(dvb);
		
		em.flush();
		utx.commit();
		connectAfdeling(wn,afd,start,einde);
		return dvb.getId();
	}
	public void connectAfdeling(Integer wn, Afdeling afd, Date start, Date einde) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		
		/* Zet oude afdelingen op afgesloten */
		Query q = em.createNativeQuery("Update AFDELING_HAS_WERKNEMER a set a.einddatum := ?1 "+ 
									    "where a.einddatum = null and a.werknemer_ID = ?2 and a.afdeling_ID = ?3");
		q.setParameter(1, start);
		q.setParameter(2, wn);
		q.setParameter(3, afd.getId());
		q.executeUpdate();
		
		AfdelingHasWerknemer ahw = new AfdelingHasWerknemer();
		AfdelingHasWerknemerPK ahwpk = new AfdelingHasWerknemerPK();
		ahwpk.setAfdeling_ID(afd.getId());
		ahwpk.setWerknemer_ID(wn);
		ahwpk.setStartdatum(start);
		ahw.setEinddatum(einde);
		ahw.setId(ahwpk);
		em.persist(ahw);
		
		em.flush();
		utx.commit();
	}
	public Afdeling addAfdeling(String naam, Integer wgr)  throws SystemException, Exception  {
		utx.begin();
		em.joinTransaction();
		Werkgever w = (Werkgever) em.createQuery("Select w from Werkgever w where w.id = " + wgr).getSingleResult();

		Afdeling afd = new Afdeling();
		afd.setNaam(naam);
		afd.setAfdelingsid(naam);
		afd.setNaam(naam);
		afd.setWerkgever(w);
		em.persist(afd);
		em.flush();
		utx.commit();
		return afd;
	}
	public void AddPakketHasActiviteit(int i, int j) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		Query qp = em.createQuery("Select p from Pakket p where p.id = " + i);
		Query qa = em.createQuery("Select a from Activiteit a where a.id = " + j);
		Pakket p = (Pakket) qp.getSingleResult();
		Activiteit a = (Activiteit) qa.getSingleResult();
		if (p.getActiviteits() == null){
			p.setActiviteits(new ArrayList<Activiteit>());
		}
		p.getActiviteits().add(a);
		
		em.persist(p);
		em.flush();
		utx.commit();
	}
	public void AddWerkgeverHasPakket(Integer wg, Integer p) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		WerkgeverHasPakket wp = new WerkgeverHasPakket();
		WerkgeverHasPakketPK wpPK = new WerkgeverHasPakketPK();
		wpPK.setPakket_ID(p);
		wpPK.setWerkgever_ID(wg);
		wp.setId(wpPK);
		em.persist(wp);
		em.flush();
		utx.commit();
	}
	public Activiteit addActiviteit
		(String naam, 
		 int periode,
		 __periodesoort psoort, 
		 __meldingsoort msoort, 
		 int waarschuwmoment,
		 boolean bijketenverzuim,
		 boolean bijnormaalverzuim,
		 boolean bijvangnet,
		 Integer planna,
		 int repeteeraantal,
		 boolean verwijdernaherstel
		 ) throws SystemException, Exception{
		utx.begin();
		em.joinTransaction();
		Activiteit g = new Activiteit();
		g.setNaam(naam);
		g.setDeadlineperiode(psoort.getValue());
		g.setDeadlineperiodesoort(psoort.getValue());
		g.setDeadlinestartpunt(msoort.getValue());
		g.setDeadlinewaarschuwmoment(waarschuwmoment);
		g.setDuur(""); /* not used */
		g.setKetenverzuim(bijketenverzuim?1:0);
		g.setNormaalverzuim(bijnormaalverzuim?1:0);
		g.setOmschrijving("Not important");
		if (planna != null)
			g.setPlannaactiviteit(planna);
		g.setRepeteeraantal(repeteeraantal);
		g.setRepeteerperiode(3);
		g.setRepeteerperiodesoort(psoort.getValue());
		g.setVangnet(bijvangnet?1:0);
		g.setVerwijdernaherstel(verwijdernaherstel?1:0);
		em.persist(g);
		em.flush();
		utx.commit();
		return g;
	}
	public Integer AddPakket(String naam) throws SystemException, Exception {
		utx.begin();
		em.joinTransaction();
		Pakket p = new Pakket();
		p.setNaam(naam);
		p.setOmschrijving(naam);
		em.persist(p);
		em.flush();
		utx.commit();
		return p.getId();
	}
	public VerzuimInfo addVerzuim(DienstverbandInfo dienstverband, WerknemerInfo wnr, Date start, Date einde, __vangnettype vangnet, __verzuimtype type){
		VerzuimInfo info = new VerzuimInfo();
		info.setCascode(1);
		info.setDienstverbandId(dienstverband.getId());
		info.setDienstverband(dienstverband);
		info.setEinddatumverzuim(einde);
		info.setGebruiker(1);
		info.setGerelateerdheid(__gerelateerdheid.NVT);
		info.setKetenverzuim(false);
		info.setMeldingsdatum(start);
		info.setMeldingswijze(__meldingswijze.TELEFOON);
		info.setStartdatumverzuim(start);
		info.setVangnettype(vangnet);
		info.setVerzuimtype(type);
		info.setWerknemer(wnr);
		return info;
	}
	public void addSettings() throws SystemException, Exception  {
		Activiteit a = addActiviteit("AFSLUITEN DVB",1,__periodesoort.DAGEN,__meldingsoort.VOLLEDIGHERSTEL,1,false,false,false,null,0,false);
		utx.begin();
		em.joinTransaction();
		Settings settings;
		settings = new Settings();
		settings.setBccemailaddress("tgiesela@hccnet.nl");
		settings.setFactuurfolder("Y:\\Facturen");
		settings.setFactuurmailtextbody("");
		settings.setSmtpmailfromaddress("tgiesela@hccnet.nl");
		settings.setSmtpmailhost("ubuntuglassfish.thuis.local");
		settings.setSmtpmailpassword("password");
		settings.setSmtpmailuser("root");
		settings.setTodoforafsluitendienstverband(a.getId());
		settings.setTodoforinformatiekaart(null);
		em.persist(settings);
		em.flush();
		utx.commit();
	}

}
