package com.gieselaar.verzuimbeheer.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import com.gieselaar.verzuimbeheer.entities.Holding;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakket;
import com.gieselaar.verzuimbeheer.entities.WerkgeverHasPakketPK;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationExceptionNoabort;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo.Afdelingcompare;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo.Dienstverbandcompare;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

/**
 * Session Bean implementation class WerkgeverBean
 */
@Stateless
@LocalBean
//@WebService
public class WerkgeverBean extends BeanBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource transient EJBContext context;
	@EJB WerkgeverConversion converter;
	@EJB AdresConversion adresconverter;
	@EJB ContactpersoonConversion contactpersoonconverter;
	@EJB AfdelingBean afdeling;
	@EJB WerknemerBean werknemer;
	@EJB PakketBean pakket;
	@EJB InstantieBean instantie;
	@EJB TariefBean tarief;
	@EJB FactuurBean factuur;
    /**
     * Default constructor. 
     */
	private Werkgever werkgever = null;
	private Holding holding = null;
	private String querydefault = "select w from Werkgever w left join fetch w.vestigingsAdres " + 
															"left join fetch w.postAdres " + 
															"left join fetch w.factuurAdres " + 
															"left join fetch w.contactPersoon ";
	private String queryholding = "select h from Holding h left join fetch h.vestigingsadres left join fetch h.postadres left join fetch h.contactpersoon ";

	private enum importresult{
		WERKGEVERNOTFOUND,
		FIELDSMISSING,
		REQUIREDFIELDMISSING,
		INVALIDLENGTH,
		FORMATERROR,
		INVALIDTYPE,
		INVALIDDATE,
		UPDATERROR,
		EMPTYROW,
		OK,
		WARNING,
		VALIDATIONERROR
	}
	
	private interface Kolom extends Serializable{
		public int getValue();
		@Override
		public String toString();
	}
	private enum importkolommen implements Kolom{
		EXTERNAFDELINGSID(1) 	{@Override public String toString(){return "externAfdelingsId";}},				
		AFDELINGSNAAM(2) 		{@Override public String toString(){return "afdelingsNaam";}},                  
		DATUMBEGINAFDELING(3)	{@Override public String toString(){return "datumBeginAfdeling";}},             
		BSN(4)                  {@Override public String toString(){return "bsn";}},           
		ACHTERNAAM(5)           {@Override public String toString(){return "achternaam";}},           
		VOORVOEGSEL(6)          {@Override public String toString(){return "voorvoegsel";}},           
		VOORLETTERS(7)          {@Override public String toString(){return "voorletters";}},           
		VOORNAAM(8)             {@Override public String toString(){return "voornaam";}},           
		GEBOORTEDATUM(9)        {@Override public String toString(){return "geboorteDatum";}},           
		GESLACHT(10)            {@Override public String toString(){return "geslacht";}},            
		BURGERLIJKESTAAT(11) 	{@Override public String toString(){return "burgerlijkeStaat";}},            
		DATUMINDIENST(12)       {@Override public String toString(){return "datumInDienst";}},            
		DATUMUITDIENST(13)      {@Override public String toString(){return "datumUitDienst";}},            
		PERSONEELSNR(14)        {@Override public String toString(){return "personeelsnr";}},            
		WERKWEEK(15)            {@Override public String toString(){return "werkweek";}},            
		ADRES(16)               {@Override public String toString(){return "adres";}},            
		POSTCODE(17)            {@Override public String toString(){return "postcode";}},            
		WOONPLAATS(18)          {@Override public String toString(){return "woonplaats";}},            
		TELPRIVE(19)            {@Override public String toString(){return "telPrivé";}},            
		TELWERK(20)             {@Override public String toString(){return "telWerk";}},            
		TELMOBIEL(21)           {@Override public String toString(){return "telMobiel";}},            
		EMAIL(22)               {@Override public String toString(){return "email";}},            
		FUNCTIE(23)             {@Override public String toString(){return "functie";}},            
		WAOPERCENTAGE(24)       {@Override public String toString(){return "waoPercentage";}};

		private int value;
		importkolommen(int value){
			this.value = value;
		}
		@Override
		public int getValue() { return value; }

        @SuppressWarnings("unused")
		public static importkolommen parse(Integer id) {
        	importkolommen soort = null; // Default
            for (importkolommen item : importkolommen.values()) {
                if (item.getValue()==id) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
	private enum importkolommenKFCuren implements Kolom{
		BSN(1)							{@Override public String toString(){return "bsn";}},
		URENLAATSTEMAAND(2)				{@Override public String toString(){return "UrenLaatsteMaand";}},				
		GEMIDDELDPERWEEKLAATSTEMND(3)	{@Override public String toString(){return "GemiddeldPerWeekLaatsteMnd";}},		
		URENLAATSTE3MAANDEN(4)			{@Override public String toString(){return "UrenLaatste3Maanden";}},			
		GEMIDDELDPERMAAND(5)			{@Override public String toString(){return "GemiddeldPerMaand";}},	            
		GEMIDDELDPERWEEKLAATSTE3MND(6)	{@Override public String toString(){return "GemiddeldPerWeekLaatste3mnd";}},	
		GEMIDDELDPERMAANDLAATSTEJAAR(7)	{@Override public String toString(){return "GemiddeldPerMaandLaatsteJaar";}},   
		AANTALDIENSTVERBANDEN(8)		{@Override public String toString(){return "Aantaldienstverbanden";}};
		
		private int value;
		importkolommenKFCuren(int value){
			this.value = value;
		}
		@Override
		public int getValue() { return value; }

        @SuppressWarnings("unused")
		public static importkolommenKFCuren parse(Integer id) {
        	importkolommenKFCuren soort = null; // Default
            for (importkolommenKFCuren item : importkolommenKFCuren.values()) {
                if (item.getValue()==id) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
	private enum importkolommenKFCuitdienst implements Kolom{
		bsn(1)							{@Override public String toString(){return "bsn";}},
		Naam(2)							{@Override public String toString(){return "Naam";}},			
		datumInDienst(3)	       		{@Override public String toString(){return "datumInDienst";}},            
		datumUitDienst(4)	      		{@Override public String toString(){return "datumUitDienst";}},            
		Medewerker(5)					{@Override public String toString(){return "Medewerker";}},	            
		Uitdienstreden(6)				{@Override public String toString(){return "Redenuitdienst";}},	
		Uitdienstredenomschrijving(7)	{@Override public String toString(){return "Uitdienstredenomschrijving";}};
		
		private int value;
		importkolommenKFCuitdienst(int value){
			this.value = value;
		}
		@Override
		public int getValue() { return value; }

        @SuppressWarnings("unused")
		public static importkolommenKFCuren parse(Integer id) {
        	importkolommenKFCuren soort = null; // Default
            for (importkolommenKFCuren item : importkolommenKFCuren.values()) {
                if (item.getValue()==id) {
                    soort = item;
                    break;
                }
            }
            return soort;
        }
	}
	private class ParseTable implements Serializable{
		Kolom kolom;
		String formaat;
		int minlen;
		int maxlen;
		boolean verplicht;
		String type;
		@SuppressWarnings("unused")
		String allowedvalues;
		ParseTable(Kolom kolom, String type,int minlen,int maxlen,boolean verplicht){
			storevalues(kolom,type,minlen,maxlen,verplicht);
			this.formaat = null;
		}
		ParseTable(Kolom kolom, String type,int minlen,int maxlen,boolean verplicht,String formaat){
			storevalues(kolom,type,minlen,maxlen,verplicht);
			this.formaat = formaat; 
		}
		ParseTable(Kolom kolom, String type,int minlen,int maxlen,boolean verplicht,String formaat,String allowedvalues){
			storevalues(kolom,type,minlen,maxlen,verplicht);
			this.formaat = formaat; 
			this.allowedvalues = allowedvalues;
		}
		private void storevalues(Kolom kolom, String type,int minlen,int maxlen,boolean verplicht){
			this.kolom = kolom;
			this.type = type;
			this.minlen = minlen;
			this.maxlen = maxlen;
			this.verplicht = verplicht;
			this.formaat = null;
			this.allowedvalues = null;
		}

	}
	private ParseTable[] importexcel = 
	   {new ParseTable(importkolommen.EXTERNAFDELINGSID,              "A",0,45,true),
		new ParseTable(importkolommen.AFDELINGSNAAM,                  "A",0,45,true),
		new ParseTable(importkolommen.DATUMBEGINAFDELING,             "D",8,10,false,	"d-MM-yyyy"),
		new ParseTable(importkolommen.BSN,                            "N",1,11,true),
		new ParseTable(importkolommen.ACHTERNAAM,                     "A",0,50,true),
		new ParseTable(importkolommen.VOORVOEGSEL,                    "A",0,10,false),
		new ParseTable(importkolommen.VOORLETTERS,                    "A",0,20,true),
		new ParseTable(importkolommen.VOORNAAM,                       "A",0,20,false),
		new ParseTable(importkolommen.GEBOORTEDATUM,                  "D",8,10,true,	"d-MM-yyyy"),
		new ParseTable(importkolommen.GESLACHT,                       "A",1,5,true,		null,			"M,V,Man,Vrouw"),
		new ParseTable(importkolommen.BURGERLIJKESTAAT,	              "A",0,50,true,	null,			"O,G,D,S,W,OG,GH"),
		new ParseTable(importkolommen.DATUMINDIENST,                  "D",8,10,true,	"d-MM-yyyy"),
		new ParseTable(importkolommen.DATUMUITDIENST,                 "D",8,10,false,	"d-MM-yyyy"),
		new ParseTable(importkolommen.PERSONEELSNR,                   "A",0,45,false),
		new ParseTable(importkolommen.WERKWEEK,                       "ND",0,8,true), // number with decimal positions
		new ParseTable(importkolommen.ADRES,                          "A",0,50,true),
		new ParseTable(importkolommen.POSTCODE,                       "A",0,12,true),
		new ParseTable(importkolommen.WOONPLAATS,                     "A",1,50,true),
		new ParseTable(importkolommen.TELPRIVE,                       "A",0,20,false),
		new ParseTable(importkolommen.TELWERK,                        "A",0,20,false),
		new ParseTable(importkolommen.TELMOBIEL,                      "A",0,20,false),
		new ParseTable(importkolommen.EMAIL,                          "A",0,50,false),
		new ParseTable(importkolommen.FUNCTIE,                        "A",0,50,false),
		new ParseTable(importkolommen.WAOPERCENTAGE,                  "A",0,50,false,	null,			"0,1,2,3,4,5,6,7,8")};

	private ParseTable[] importexcelUren = 
	   {new ParseTable(importkolommenKFCuren.BSN,              			  "N",0,11,true),
		new ParseTable(importkolommenKFCuren.URENLAATSTEMAAND,            "ND",0,8,false),
		new ParseTable(importkolommenKFCuren.GEMIDDELDPERWEEKLAATSTEMND,  "ND",0,8,false),
		new ParseTable(importkolommenKFCuren.URENLAATSTE3MAANDEN,		  "ND",0,8,false),
		new ParseTable(importkolommenKFCuren.GEMIDDELDPERMAAND,	          "ND",0,8,false),
		new ParseTable(importkolommenKFCuren.GEMIDDELDPERWEEKLAATSTE3MND, "ND",0,8,true)};

	private ParseTable[] importexcelUitdienst = 
		   {new ParseTable(importkolommenKFCuitdienst.bsn,                          "N",0,11,true),
			new ParseTable(importkolommenKFCuitdienst.Naam,         	            "A",0,50,false),
			new ParseTable(importkolommenKFCuitdienst.datumInDienst,                "ND",8,10,true),
			new ParseTable(importkolommenKFCuitdienst.datumUitDienst,  	            "ND",8,10,true),
			new ParseTable(importkolommenKFCuitdienst.Medewerker,		  			"A",0,50,false),
			new ParseTable(importkolommenKFCuitdienst.Uitdienstreden,	          	"ND",0,2,false),
			new ParseTable(importkolommenKFCuitdienst.Uitdienstredenomschrijving, 	"A",0,50,false)};

	public WerkgeverBean() {
    }
    private WerkgeverInfo completeWerkgever(Werkgever w, boolean details) throws VerzuimApplicationException{
		pakket.setCurrentuser(getCurrentuser());
		instantie.setCurrentuser(getCurrentuser());
    	WerkgeverInfo wi = converter.fromEntity(w);
		afdeling.setCurrentuser(getCurrentuser());
		if (details)
		{
			List<AfdelingInfo> afdelingen;
			afdelingen = afdeling.getByWerkgeverId(wi.getId());
			wi.setAfdelings(afdelingen);
			List<PakketInfo> pakketten;
			pakketten = pakket.getByWerkgever(wi.getId());
			wi.setPakketten(pakketten);
			wi.setArbodienst(instantie.getArbodienst(wi.getArbodienstId()));
			wi.setHolding(getHoldingById(wi.getHoldingId()));
			wi.setUwv(instantie.getUitvoeringsinstituut(wi.getUwvId()));
		}
		return wi;
    }
    @SuppressWarnings("unchecked")
	public List<WerkgeverInfo> getAll() throws VerzuimApplicationException {
		Query q = createQuery(querydefault);
		List<Werkgever> result = (List<Werkgever>)getResultList(q);
		List<WerkgeverInfo> inforesult = new ArrayList<>();
		for (Werkgever w: result)
			inforesult.add(completeWerkgever(w, false));
	
		return inforesult;
	}
    public List<WerkgeverInfo> getAllSimple() throws VerzuimApplicationException {
		Query q = createQuery("select w.naam, w.id, w.startdatumcontract, w.einddatumcontract, w.factureren, w.holding_ID from Werkgever w");
		@SuppressWarnings("unchecked")
		List<Object[]> result = (List<Object[]>)getResultList(q);
		List<WerkgeverInfo> inforesult = new ArrayList<>();
		for (Object[] w: result)
		{
			WerkgeverInfo wi = new WerkgeverInfo();
			wi.setNaam((String)w[0]);
			wi.setId((int)w[1]);
			wi.setStartdatumcontract((Date)w[2]);
			wi.setEinddatumcontract((Date)w[3]);
			if (((Integer)w[4]).equals(0))
				wi.setFactureren(false);
			else
				wi.setFactureren(true);
			wi.setHoldingId((Integer)w[5]);
			inforesult.add(wi);
		}
		
		return inforesult;
	}

	public WerkgeverInfo getById(long id) throws VerzuimApplicationException {
		/*
		 * Als de details van de werkgever worden opgevraagd, dan worden
		 * ook de afdelingen en pakketten toegevoegd.
		 */
		Query q = createQuery(querydefault + " where w.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Werkgever> result = (List<Werkgever>)getResultList(q);
		if (result.size() != 1)
			return null;
		Werkgever w = result.get(0);
		
		return completeWerkgever(w, true);
	}

	public int getAantalWerknemers(Integer werkgeverid, Integer holdingid, Date peildatum){
		Query q = em.createNativeQuery("SELECT count(*) from WERKNEMER wn " + 
					  				   " join 		WERKGEVER wg on wn.werkgever_id = wg.id " + 
									   " left join HOLDING hd   on wg.holding_id = hd.id " + 
									   " join 	    DIENSTVERBAND dvb on dvb.werknemer_id = wn.id" +
									   " WHERE (dvb.einddatumcontract is null or dvb.einddatumcontract >= ?3) AND " +
									   "        dvb.startdatumcontract  <= ?3 AND " + 
									   "       (wn.werkgever_id = ?1 Or (?1 is null AND hd.id = ?2))");
		q.setParameter(1, werkgeverid);
		q.setParameter(2, holdingid);
		q.setParameter(3, peildatum);

		long result = (long) q.getSingleResult();
		return (int)result;
	}
	public WerkgeverInfo addWerkgever(WerkgeverInfo info) throws ValidationException, VerzuimApplicationException {
		HoldingInfo hld = null;
		
		afdeling.setCurrentuser(getCurrentuser());
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		if (info.getHoldingId() != null){
			hld = getHoldingById(info.getHoldingId());
		}
		if (hld != null){
			if (hld.isFactureren() && info.getFactureren()){
				throw new ValidationException("Factureren op holding niveau staat al aan");
			}
		}
		if (info.getFactureren()){
			info.setDebiteurnummer(getMaxDebiteurNr() + 1);
		}
		
		this.werkgever = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.werkgever);
		if (info.getAfdelings() != null)
		{
			for (AfdelingInfo a:info.getAfdelings())
	    	{
				a.setWerkgeverId((Integer)werkgever.getId());
	    		afdeling.updateAfdeling(a);
	    	}
		}
		updatePakketten(info.getPakketten(),this.werkgever.getId());
		return converter.fromEntity(werkgever);
	}

	private int getMaxDebiteurNr() {
		Query q = em.createQuery("select max(wg.debiteurnummer) from Werkgever wg");
		Object nrwg = q.getSingleResult();
		if (nrwg == null){
			nrwg = (Integer)9000;
		}
		q = em.createQuery("select max(h.debiteurnummer) from Holding h");
		Object nrh = q.getSingleResult();
		if (nrh == null){
			nrh = (Integer)9000;
		}
		if ((int)nrh > (int)nrwg){
			return (int)nrh;
		}else{
			return (int)nrwg;
		}
	}
	private void updatePakketten(List<PakketInfo> pakketten, Integer werkgeverid) throws VerzuimApplicationException {
		WerkgeverHasPakket wp;
		WerkgeverHasPakketPK wppk;
		if (pakketten == null)
			return;
		for (PakketInfo a:pakketten)
    	{
			switch (a.getAction()) {
				case DELETE:
					Query qd	= createQuery("Delete from WerkgeverHasPakket wp where wp.id.werkgever_ID = :werkgeverid and wp.id.pakket_ID = :pakketid");
					qd.setParameter	("werkgeverid", werkgeverid);
					qd.setParameter	("pakketid", a.getId());
			    	executeUpdate(qd);
					break;
				case INSERT:
					wp = new WerkgeverHasPakket();
					wppk = new WerkgeverHasPakketPK();
					wppk.setPakket_ID(a.getId());
					wppk.setWerkgever_ID(werkgeverid);
					wp.setId(wppk);
					em.persist(wp);
					break;
				case UPDATE:
					break;
			}
    	}
		
	}
	public boolean deleteWerkgever(WerkgeverInfo info) throws VerzuimApplicationException, ValidationException  {
		this.werknemer.deleteWerknemers(info.getId());
		this.afdeling.deleteAfdelingen(info.getId());
		this.pakket.deletePakketten(info.getId());
		this.tarief.deleteTarievenWerkgever(info.getId());
		this.werkgever = converter.toEntity(info, this.getCurrentuser());
		this.factuur.deleteFacturenWerkgever(info.getId());
		
		/*
		 * Verwijder de koppeling tussen gebruiker en werkgever
		 */
		Query qw	= createQuery("Delete from GebruikerWerkgever gw where gw.id.werkgeverid = :id");
		qw.setParameter	("id", info.getId());
    	executeUpdate(qw);
		
		
		try{
			this.deleteEntity(this.werkgever);
		}
		catch (Exception e){
			dbException(e, "deleteWerkgever");
		}
		
		return true;
	}
	public boolean updateWerkgever(WerkgeverInfo info) throws ValidationException, VerzuimApplicationException {
		HoldingInfo hld = null;
		
		afdeling.setCurrentuser(getCurrentuser());
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		
		if (info.getHoldingId() != null){
			hld = getHoldingById(info.getHoldingId());
		}
		if (hld != null){
			if (hld.isFactureren() && info.getFactureren()){
				throw new ValidationException("Factureren op holding niveau staat al aan");
			}
		}
		if (info.getFactureren() && info.getDebiteurnummer() == null){
			info.setDebiteurnummer(getMaxDebiteurNr() + 1);
		}
		
    	if (info.getAfdelings() != null){
			for (AfdelingInfo a:info.getAfdelings())
	    	{
	    		afdeling.updateAfdeling(a);
	    	}
    	}
    	updatePakketten(info.getPakketten(), info.getId());
    	this.werkgever = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.werkgever);
    	return true;
	}
    private HoldingInfo completeHolding(Holding w){
		return converter.fromEntity(w);
    }
    public List<HoldingInfo> getHoldings() throws VerzuimApplicationException {
		Query q = createQuery(queryholding);
		@SuppressWarnings("unchecked")
		List<Holding> result = (List<Holding>)getResultList(q);
		List<HoldingInfo> inforesult = new ArrayList<>();
		for (Holding w: result)
			inforesult.add(completeHolding(w));
		
		return inforesult;
	}
	public HoldingInfo getHoldingById(Integer id) throws VerzuimApplicationException {
		/*
		 * Als de detailts van de werkgever worden opgevraagd, dan worden
		 * ook de afdelingen en pakketten toegevoegd.
		 */	
		if (id == null)
			return null;
		Query q = createQuery(queryholding + " where h.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Holding> result = (List<Holding>)getResultList(q);
		if (result.size() != 1)
			return null;
		Holding w = result.get(0);
		
		return completeHolding(w);
	}
	public HoldingInfo addHolding(HoldingInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

		this.holding = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.holding);
		return converter.fromEntity(holding);
	}
	public boolean deleteHolding(HoldingInfo info) throws ValidationException, VerzuimApplicationException  {
		List<WerkgeverInfo> werkgevers = this.getAll();
		for (WerkgeverInfo wi:werkgevers){
			if (wi.getHoldingId() != null)
				if (wi.getHoldingId().equals(info.getId()))
					throw new ValidationException("Er bestaan nog gekoppelde werkgevers");
		}
		this.holding = converter.toEntity(info, this.getCurrentuser());
		tarief.deleteTarievenHolding(this.holding.getId());
		this.deleteEntity(this.holding);
		
		return true;
	}
	public boolean updateHolding(HoldingInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		
		if (info.isFactureren()){
			/*
			 * Als factureren wordt aangezet, moet het bij de werkgevers worden uitgezet
			 */
			List<WerkgeverInfo> werkgevers = getWerkgeversHolding(info.getId());
			for (WerkgeverInfo wg:werkgevers){
				if (wg.getFactureren()){
					wg.setFactureren(false);
					updateWerkgever(wg);
				}
			}
			if (info.isFactureren() && info.getDebiteurnummer() == null){
				info.setDebiteurnummer(getMaxDebiteurNr() + 1);
			}
		}
		
    	this.holding = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.holding);
    	return true;
	}

	/**
	 * functions for import from excel
	 * @param val
	 * @return
	 */
	private __geslacht setGeslacht(String val){
		 switch(val){
		 	case "M":		
		 	case "Man": 	return __geslacht.MAN;
		 	case "V":		
		 	case "Vrouw": 	return __geslacht.VROUW;
		 	default:		return __geslacht.ONBEKEND;
		 }
	}
	private __wiapercentage setWiapercentage(String val){
		if (val.isEmpty())
			val = "0";
		switch (val){
			case "1":	return __wiapercentage.WIA_0_15;
			case "2":	return __wiapercentage.WIA_15_25;
			case "3":	return __wiapercentage.WIA_25_35;
			case "4":	return __wiapercentage.WIA_35_45;
			case "5":	return __wiapercentage.WIA_45_55;
			case "6":	return __wiapercentage.WIA_55_65;
			case "7":	return __wiapercentage.WIA_65_80;
			case "8":	return __wiapercentage.WIA_80_100;
			default: return __wiapercentage.NVT;
		}
	}
	private __burgerlijkestaat setBurgerlijkeStaat(String val){
		switch (val) {
			case "O": 	
			case "OG": 	return __burgerlijkestaat.ONGEHUWD;
			case "G":	
			case "GH": 	return __burgerlijkestaat.GEHUWD;
			case "D": 	return __burgerlijkestaat.GESCHEIDEN;
			case "S": 	return __burgerlijkestaat.SAMENWONEND;
			case "W": 	return __burgerlijkestaat.WEDUWE;
			default: 	return __burgerlijkestaat.ONBEKEND;
		}
	}
	private boolean isEmtpyRow(String[] tokens){
		for (int i = 0;i<tokens.length;i++){
			if (!tokens[i].isEmpty())
				return false;
		}
		return true;
	}
	private AfdelingInfo addAfdeling(int werkgeverid, String extAfdId, String afdNaam) throws VerzuimApplicationException
	{
		 AfdelingInfo afd = new AfdelingInfo();
		 afd.setAfdelingsid(extAfdId);
		 afd.setNaam(afdNaam);
		 afd.setWerkgeverId(werkgeverid);
		 afd.setContactpersoon(null);
		 try {
			 afd = this.afdeling.addAfdeling(afd);
		 } catch (ValidationException e) {
			 log.error("Unable to insert afdeling",e);
			 afd = null;
		 }
		 
		 return afd;
	}

	private AdresInfo setAdres(AdresInfo adres, ImportWerknemerInfo line){
		if (adres == null)
			adres = new AdresInfo();
		adres.setLand("Nederland");
		adres.setPlaats(line.getWoonplaats());
		adres.setPostcode(line.getPostcode());
		adres.setStraat(line.getAdres());
		AdresInfo.extractHuisnummerfromStraat(adres.getStraat(), adres);
		return adres;
	}
	private String formatBSN(String bsn){
		String formattedbsn;
		if (bsn.isEmpty())
			return bsn;
		else{
			formattedbsn = bsn.trim();
			while (formattedbsn.length()<9){
				formattedbsn = "0" + formattedbsn; 
			}
			return formattedbsn;
		}
	}
	private WerknemerInfo setWerknemer(WerknemerInfo werknemer, ImportWerknemerInfo line){
		String achternaam = line.getAchternaam();
		String []parts = achternaam.split(",");
		if (parts.length > 1)
			werknemer.setAchternaam(parts[0]);
		else
			werknemer.setAchternaam(line.getAchternaam());
		werknemer.setBurgerservicenummer(line.getBurgerservicenummer());
		werknemer.setArbeidsgehandicapt(false);
		werknemer.setBurgerlijkestaat(line.getBurgerlijkestaat());
		werknemer.setEmail(line.getEmail());
		werknemer.setGeboortedatum(line.getGeboortedatum());
		werknemer.setGeslacht(line.getGeslacht());
		werknemer.setMobiel(line.getTelefoonmobiel());
		werknemer.setTelefoonPrive(line.getTelefoonprive());
		werknemer.setTelefoon(line.getTelefoonwerk());
		werknemer.setVoorletters(line.getVoorletters());
		werknemer.setVoornaam(line.getVoornaam());
		werknemer.setVoorvoegsel(line.getVoorvoegsel());
		if (werknemer.getWiaPercentages() == null || werknemer.getWiaPercentages().isEmpty()){
			List<WiapercentageInfo> wiapercentages; 
			if (werknemer.getWiaPercentages() == null)
				wiapercentages = new ArrayList<>();
			else
				wiapercentages = werknemer.getWiaPercentages();
			WiapercentageInfo wiapercentage = new WiapercentageInfo();
			wiapercentage.setCodeWiaPercentage(line.getWiapercentage());
			wiapercentage.setStartdatum(line.getDatumindienst());
			wiapercentage.setEinddatum(null);
			wiapercentage.setWerknemer(werknemer);
			wiapercentage.setWerknemerId(werknemer.getId());
			wiapercentages.add(wiapercentage);
			werknemer.setWiaPercentages(wiapercentages);
		}else{
			if (werknemer.getLaatsteWiapercentage().getCodeWiaPercentage().equals(line.getWiapercentage())){
				/* wiapercentage niet gewijzigd */
			}else{
				WiapercentageInfo wiapercentage = new WiapercentageInfo();
				wiapercentage.setCodeWiaPercentage(line.getWiapercentage());
				wiapercentage.setStartdatum(new Date());
				wiapercentage.setEinddatum(null);
				wiapercentage.setWerknemer(werknemer);
				wiapercentage.setWerknemerId(werknemer.getId());
				werknemer.getWiaPercentages().add(wiapercentage);
			}
		}
		return werknemer;
	}
	private void updateAfdelingen(WerknemerInfo werknemer,AfdelingInfo wnrAfdeling, ImportWerknemerInfo line){
		boolean found = false;
		Date startdatum;
		Date einddatum;
		AfdelingHasWerknemerInfo afdhaswnr = null;
		AfdelingHasWerknemerInfo afdhaswnrActive = null;

		startdatum = line.getDatumbeginafdeling();
		einddatum = line.getDatumuitdienst();
		
		if (werknemer.getAfdelingen() != null){
			for (AfdelingHasWerknemerInfo afdhw: werknemer.getAfdelingen()){
				if (afdhw.getAfdelingId() == wnrAfdeling.getId() &&
					afdhw.getStartdatum().equals(startdatum)){
					found = true;
					afdhaswnr = afdhw;
				}
				if (afdhw.getEinddatum() == null){
					afdhaswnrActive = afdhw;
				}
			}
		}
		if (found){
			if (afdhaswnr.getEinddatum() != null){
				if (einddatum != null){
					if (DateOnly.after(afdhaswnr.getStartdatum(), einddatum)){
						/* het gaat om een oudere mutatie...*/
					}else{
						afdhaswnr.setEinddatum(einddatum);
					}
				}
			}
			return; /* We zijn hiermee klaar */
		}

		/* Het is nog geen bestaande afdeling voor de werknemer */
		if (afdhaswnrActive != null){
			/* Actieve afdeling gevonden, die moeten we dan afsluiten als het om een actief dienstverband gaat */
			if (einddatum == null){
				afdhaswnrActive.setEinddatum(DateOnly.addDays(startdatum, -1));
			}else{
				if (DateOnly.after(afdhaswnrActive.getStartdatum(), einddatum)){
					/* het gaat om een oudere mutatie...*/
				}else{
					afdhaswnrActive.setEinddatum(einddatum);
				}
				/*
				 * Het dienstverband is beeindigd, dan maken we geen nieuwe koppeling
				 * meer met een andere afdeling 
				 */
				return;
			}
		}
		afdhaswnr = new AfdelingHasWerknemerInfo();
		afdhaswnr.setAfdeling(wnrAfdeling);
		afdhaswnr.setStartdatum(startdatum);
		afdhaswnr.setEinddatum(null);
		afdhaswnr.setWerkgeverId(werknemer.getWerkgeverid());
		afdhaswnr.setAfdelingId(wnrAfdeling.getId());
		
		List <AfdelingHasWerknemerInfo> afdelingenwnr;
		afdelingenwnr = werknemer.getAfdelingen();
		if (afdelingenwnr == null){
			afdelingenwnr =  new ArrayList<>();
		}
			
		afdelingenwnr.add(afdhaswnr);
		werknemer.setAfdelingen(afdelingenwnr);
		
	}
	private void updateDienstverband(WerknemerInfo wnr, ImportWerknemerInfo line){
		Date startdatum;
		Date einddatum ;
		DienstverbandInfo dvb = null;
		List<DienstverbandInfo> dienstverbanden;

		einddatum = line.getDatumuitdienst();
		startdatum = line.getDatumindienst();
		
		if (wnr.getDienstVerbanden() == null){
			dienstverbanden = new ArrayList<>();
			dvb = new DienstverbandInfo();
			dvb.setEinddatumcontract(einddatum);
			dvb.setStartdatumcontract(startdatum);
			dvb.setWerkweek(BigDecimal.ZERO);
			dienstverbanden.add(dvb);
			wnr.setDienstVerbanden(dienstverbanden);
		}else{
			if (wnr.getDienstVerbanden().isEmpty()){
				dvb = new DienstverbandInfo();
				dvb.setEinddatumcontract(einddatum);
				dvb.setStartdatumcontract(startdatum);
				dvb.setWerkweek(BigDecimal.ZERO);
				wnr.getDienstVerbanden().add(dvb);
			}else{
				for (DienstverbandInfo wnrdvb:wnr.getDienstVerbanden()){
					if (wnrdvb.getStartdatumcontract().equals(startdatum)){
						dvb = wnrdvb;
						dvb.setEinddatumcontract(einddatum);
					}else{
						/*
						 * Het is niet hetzelfde dienstverband, maar wel actief.
						 * Dat moet dan afgesloten worden, met einddatum = startdatum - 1 dag
						 */
						/* Ondanks dit, doen we gewoon alsof het hetzelfde dienstverband is.
						 * Dit doen we omdat KFC voor elke nieuw contract een wijziging instuurt
						 * met een nieuwe datum dienstverband. Dit vervuilt het systeem en bovendien
						 * zijn dan alle verzuimen niet meer zichtbaar.
						 */
						if (wnrdvb.getEinddatumcontract() == null && einddatum != null){
							dvb = wnrdvb;
							if (DateOnly.after(dvb.getStartdatumcontract(), einddatum)){
								/* het gaat om een oudere mutatie...*/
							}else{
								dvb.setEinddatumcontract(einddatum);
							}
						}
					}
				}
				if (dvb == null){
					/*
					 * Dienstverband niet gevonden, dan moet er
					 * een nieuwe komen.
					 */
					dvb = new DienstverbandInfo();
					dvb.setEinddatumcontract(einddatum);
					dvb.setStartdatumcontract(startdatum);
					dvb.setWerkweek(BigDecimal.ZERO);
					wnr.getDienstVerbanden().add(dvb);
				}
			}
		}
		
		BigDecimal urenNieuw = line.getWerkweek();
		if (urenNieuw.compareTo(BigDecimal.ZERO) == 0){
			/*
			 * Werkweek uren niet opgegeven. Als het Dienstverband al een werkweek
			 * bevat, dan laten we die ongewijzigd.
			 */
			if (dvb.getWerkweek().compareTo(BigDecimal.ZERO)!= 0){
				urenNieuw = dvb.getWerkweek();
			}else{
				urenNieuw = urenNieuw.add(new BigDecimal(10));
			}
		}
		dvb.setWerkweek(urenNieuw);
		dvb.setFunctie(line.getFunctie());
		dvb.setPersoneelsnummer(line.getPersoneelsnummer());
		dvb.setWerkgever(wnr.getWerkgever());
		dvb.setWerkgeverId(wnr.getWerkgever().getId());
		dvb.setWerknemer(wnr);
		dvb.setWerknemerId(wnr.getId());

	}
	private importresult parseLine(String[] tokens, StringBuilder errormessage,ParseTable[] importexcel){
		
		for (int i=0; i<tokens.length;i++){

			if (importexcel[i].verplicht)
				if (tokens[i].isEmpty()){
					errormessage.append("Veld " + i + "("+ importexcel[i].kolom.toString() + ") is niet ingevuld");
					return importresult.VALIDATIONERROR;
				}
			if ((tokens[i].length() > importexcel[i].maxlen) ||
				(tokens[i].length() < importexcel[i].minlen)){
				if (!importexcel[i].verplicht && tokens[i].length() == 0){
					/* skip empty field */
				}else{
					errormessage.append("Veld " + i + "("+ importexcel[i].kolom.toString() + ") heeft ongeldige lengte");
					return importresult.VALIDATIONERROR;
				}
			}
			if (tokens[i].length() > 0){
				if (importexcel[i].type == "N"){
					try{
						Integer.parseInt(tokens[i]);
					}
					catch (NumberFormatException e){
						errormessage.append("Veld " + i + "("+ importexcel[i].kolom.toString() + ") is geen geldig getal");
						return importresult.VALIDATIONERROR;
					}
				}
				else
				if (importexcel[i].type == "ND"){
					try{
						parseDecimal(tokens[i]);
					} catch (ValidationException e) {
						errormessage.append("Veld " + i + "("+ importexcel[i].kolom.toString() + ") is geen geldig decimaal getal(nn.n)");
						return importresult.VALIDATIONERROR;
					}			
				}
				else
				if (importexcel[i].type == "D"){
					 try {
						parseDate(tokens[i],importexcel[i].formaat);
					} catch (ValidationException e) {
						errormessage.append("Veld " + i + "("+ importexcel[i].kolom.toString() + ") is geen geldige datum (" + importexcel[i].formaat +")");
						return importresult.VALIDATIONERROR;
					}  
				}
				else
				if (importexcel[i].type == "A"){
					// do nothing
				}
				else{
					errormessage.append("Veld " + i + "("+ importexcel[i].kolom.toString() + ") heeft onbekend datatype. Neem contact op met T. Gieselaar");
					return importresult.VALIDATIONERROR; 
				}
			}
		}
		
		return importresult.OK;
	}
	private Date parseDate(String strdate, String format) throws ValidationException{
		DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
		Date result ;
		try {
			if (strdate.isEmpty())
				result = null;
			else
				result =  df.parse(strdate);
		} catch (ParseException e) {
			throw new ValidationException("Unexpected date parsing error");
		}  
		return result;
	}
	private BigDecimal parseDecimal(String strdecimal) throws ValidationException{
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator('.');
		symbols.setDecimalSeparator(',');
		String pattern = "##0,00";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		BigDecimal bigDecimal;
		decimalFormat.setParseBigDecimal(true);

		// parse the string
		try {
			bigDecimal = (BigDecimal) decimalFormat.parse(strdecimal);
		} catch (ParseException e) {
			throw new ValidationException("Unexpected decimal parsing error");
		}			
		return bigDecimal;
	}
	private importresult setResult(importresult result,ImportResult impresult){
		impresult.setResult(result.ordinal());
		impresult.setImportok(false);
		if (result == importresult.OK)
			impresult.setImportok(true);
		else{
			if (result == importresult.WARNING || result == importresult.VALIDATIONERROR){
				impresult.setWarning(true);
			}else{
				context.setRollbackOnly();
			}
		}
		return result;
	}
	public List<ImportResult> importWerknemers(int werkgever, String separator, byte[] uploadedFile, boolean fieldnamespresent) throws ValidationException, VerzuimApplicationException{
		/* uploadedFile is a csv file */
		/* Based on ARCO Enterprise 5.5
		Kol	Naam Veld						Verplicht	Betekenis							Formaat		Toegestane Waarden	Voorbeeld
		nr									(J/N)				
		1	Extern Afdeling's Id				J		Het nummer waarmee de afdeling in 
														het externe systeem uniek wordt 
														geïdentificeerd						A-50		12345
		2	Afdeling's Naam						J		De naam van de afdeling			    A-50		Werkplaats
		3	Datum Begin Afdeling				J	    De datum waarop de werknemer voor 
														de afdeling is gaan werken			D			1-1-1990
		4	Sofi Nummer							J	    Sofinummer	                        N-9 		(geen punten!)		123456789
		5	Achternaam							J	    Achternaam	                        A-50		Laan
		6	Voorvoegsel							N	    Voorvoegsel	                        A-10		van der
		7	Voorletters							J	    Voorletters	                        A-10		P.J.M.
		8	Voornaam							N	    Voornaam	                        A-20		Piet
		9	Geboortedatum						J	    Geboortedatum	                    D			27-3-1967
		10	Geslacht							J	    Geslacht	                        A-1	        M = Man				M
													    			                        	        V = Vrouw	
		11	Burgerlijke Staat					J	    Burgerlijke Staat	                A-1	        O = Ongehuwd		G
													    					                	        G = Gehuwd	
													    					                	        D = Gescheiden	
													    					                	        S = Samenwonend	
													    					                	        W = Weduwe (Weduwnaar)	
		12	Datum In Dienst						J	    Datum In Dienst		                D		    1-1-1990
		13	Datum Uit Dienst					N	    Datum Uit Dienst	                D		    1-1-2003
		14	Personeelsnr						N	    Personeelsnr		                A-50		9,87654E+11
		15	Werkweek							J	    Het aantal contractuele aantal uren 
														per week							N-2			40
		16	Adres								J	    Adres	                            A-50		Straatweg 
		16-1	Huisnummer						J	    Huisnummer                          N-10		12
		16-2	Huisnummer toevoeging			N	    Huisnummer toevoeging               A-50		
		17	Postcode							J	    Postcode	                        A-7 (met spatie!)		1234 BB
		18	Woonplaats							J	    Woonplaats	                        A-50		Nijmegen
		19	Tel Privé							N	    Privé telefoonnummer	            A-20		015-1234567
		20	Tel Werk							N	    Telefoonnummer waarop de werknemer 
														op het werk bereikbaar is			A-20		015-7654321
		21	Tel Mobiel							N	    Mobiele nummer van de werknemer		A-20		06-12345678
		22	Email								N	    Email								A-50		piet.vander.laan@compubase.nl
		23	Functie								N	    De naam van de functie				A-50		Hoofd magazijn
		24	WAO Percentage						J	    De eventuele WAO percentage 
														categorie							N-1			0 =  Niet van toepassing	2
													    											    1 =  0 - 15%	
													    											    2 = 15 - 25%	
													    											    3 = 25 - 35%	
													    											    4 = 35 - 45%	
													    											    5 = 45 - 55%	
													    											    6 = 55 - 65%	
													    											    7 = 65 - 80%	
													    											    8 = 80 - 100%	
*/		
		
/*
 * 		XML Document met resultaten:
 * 
 * 		<importresults>
 * 			<werknemer result="">
 * 				<sourceline>
 * 				</sourceline>
 * 				<errormessage>
 * 				</errormessage>
 * 			</werknemer>
 * 		</importresults>		
 */
		werknemer.setCurrentuser(getCurrentuser());
		List<ImportResult> importresults = new ArrayList<>();
		List<ImportWerknemerInfo> lines = new ArrayList<>();
	
		WerkgeverInfo wgr = this.getById(werkgever);
		if (wgr == null){
			context.setRollbackOnly();
			throw new ValidationException("Werkgever niet gevonden");
		}
		String strLine = "";
		try {
			// Load the byte array to an input stream for reading.
			ByteArrayInputStream bis = new ByteArrayInputStream(uploadedFile);

			// Load the input stream to a buffered reader to read the data.
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));

			if (fieldnamespresent)
				// skip the first line
				br.readLine();
			// Read comma separated file line by line
			while ((strLine = br.readLine()) != null) {
				convertLine(strLine, lines, separator);
			}
			// Sorteer de records op BSN en DatumInDienst
			ImportWerknemerInfo.sort(lines,ImportWerknemerInfo.__sortcol.DATUM);
			
			for (int i=0;i<lines.size();i++){
				ImportWerknemerInfo line = lines.get(i);
				if (!line.getImportresult().importok){
					importresults.add(line.getImportresult());
					continue;
				}
				if (line.getDatumuitdienst() != null){
					/*
					 * Uitdienstmelding.
					 * Kijk of de volgende rij van hetzelfde BSN is
					 * en of de datum in dienst gelijk is of 1 dag verschilt.
					 * In dat geval negeren we de uitdienst melding.
					 */
					if ((i+1) < lines.size()){
						ImportWerknemerInfo nextline = lines.get(i+1);
						if (!nextline.getImportresult().importok){
							/*
							 * We kunnen niet vergelijken met een eventueel volgende 
							 * regel, want die is niet in orde. We nemen dan de gok.
							 */
							processLineWerknemer(line, wgr);
						}else{
							if (nextline.getBurgerservicenummer().equals(line.getBurgerservicenummer())){
								if (DateOnly.diffDays(nextline.getDatumindienst(),line.getDatumuitdienst()) < 2){
									processLineWerknemer(nextline, wgr);
									log.debug("Skipping uitdienstmelding: " + line.getAchternaam());
									i=i+1;
								}
							}else{
								processLineWerknemer(line, wgr);
							}
						}
					}else{
						/*
						 * Nu wordt de uitdienstmelding wel verwerkt. Ik neem daarbij aan
						 * dat in geval van een contractverlenging, de uitdienstmelding en
						 * indienstmelding op dezelfde dag worden ingevoerd. In theorie kan
						 * dit nog altijd resulteren in een onterecht uitdienstmelding als dit
						 * toevallig niet gebeurt en ook de datum waarop het importbestand wordt
						 * aangemaakt er precies tussen invalt.
						 */
						processLineWerknemer(line, wgr);
					}
				}else{
					processLineWerknemer(line, wgr);
				}
				importresults.add(line.getImportresult());
				
			}
		} catch (Exception e) {
			log.info(e.getMessage(),e);
			return null;
		}
		return importresults;
	}
	private void convertLine(String strLine, List<ImportWerknemerInfo> lines, String separator) throws VerzuimApplicationException {
		/**
		 * Routine om een comma-separated line te splitsen en op te slaan in
		 * een ImportWerknemerInfo object
		 */
		
		ImportWerknemerInfo iwi = new ImportWerknemerInfo();
		// Break comma separated line using the separator in the calling parameters
		importresult result; // Local variable used to indicate error or warning
		
		iwi.getImportresult().setImportok(true);
		iwi.getImportresult().setResult(importresult.OK.ordinal());
		iwi.getImportresult().setSourceLine(strLine);

		String[] tokens = strLine.split(separator);
		
		if (importexcel.length != tokens.length){
			setResult(importresult.VALIDATIONERROR , iwi.getImportresult());
			iwi.getImportresult().setErrorMessage("Aantal kolommen(" + tokens.length + ") niet zoals verwacht("+ importexcel.length +")");
		}

		if (iwi.getImportresult().isImportok()){
		// check syntax
			StringBuilder errormessage = new StringBuilder();
			if (isEmtpyRow(tokens)){
				result = setResult(importresult.VALIDATIONERROR, iwi.getImportresult());
				iwi.getImportresult().setErrorMessage("Lege regel");
			}else{
				result = parseLine(tokens, errormessage, importexcel);
			}
			if (result != importresult.OK){
				setResult(result,iwi.getImportresult());
				iwi.getImportresult().setErrorMessage(errormessage.toString());
			}
		}
		if (iwi.getImportresult().isImportok()){
			try {
				setImportWerknemerInfo(tokens, iwi);
			} catch (ValidationException | ParseException e) {
				throw new VerzuimApplicationException(e, "Unexpected error during parse", null);
			}
		}
		lines.add(iwi);
		iwi.setSequencenr(lines.size());
	}
	private void processLineWerknemer(ImportWerknemerInfo line, WerkgeverInfo wgr) throws VerzuimApplicationException {
		boolean afdelingfound;
		WerknemerInfo wnr;
		// Break comma separated line using the separator in the calling parameters
		ImportResult impresult = line.getImportresult();
		if (!impresult.importok)
			return;
		
		impresult.setImportok(true);
		impresult.setResult(importresult.OK.ordinal());

		wnr = new WerknemerInfo();

		// Kijk of de afdeling al bestaat
		afdelingfound = false;
		AfdelingInfo wnrAfdeling = null;
		for (AfdelingInfo afd:wgr.getAfdelings())
		{
			if (afd.getNaam().equalsIgnoreCase(line.getAfdelingsnaam())){
				afdelingfound = true;
				wnrAfdeling = afd;
				break;
			}else{
				if (afd.getAfdelingsid() != null){
					if (afd.getAfdelingsid().equalsIgnoreCase(line.getExternafdelingsid())){
						afdelingfound = true;
						wnrAfdeling = afd;
						break;
					}
				}
			}
		}
		if (!context.getRollbackOnly()){
			if (!afdelingfound){
				wnrAfdeling = addAfdeling(wgr.getId()
									,line.getExternafdelingsid()
									,line.getAfdelingsnaam());
				wgr.getAfdelings().add(wnrAfdeling);
			}
			
			List<WerknemerInfo> werknemers = this.werknemer.getByBSN(wgr.getId(), line.getBurgerservicenummer());
			if (werknemers == null || werknemers.isEmpty()){
				AdresInfo adr = new AdresInfo();
				adr = setAdres(adr, line);
				wnr.setAdres(adr);
				wnr = setWerknemer(wnr, line);
				wnr.setWerkgever(wgr);
				wnr.setWerkgeverid(wgr.getId());
				
				updateAfdelingen(wnr, wnrAfdeling, line);
				updateDienstverband(wnr, line);
				// Werknemer bestaat nog niet, dan opvoeren
				try {
					validateWerknemerSimple(wnr, impresult);
					this.werknemer.addWerknemer(wnr);
				} catch (Exception e){
					setResult(importresult.WARNING,impresult);
					impresult.setErrorMessage(e.getMessage());
				}
			}
			else{
				if (werknemers.size() == 1){
					// Werknemer bestaat al, dan aanpassen
					wnr = werknemers.get(0);
					wnr.setAdres(setAdres(wnr.getAdres(), line));
					wnr = setWerknemer(wnr, line);
					updateAfdelingen(wnr, wnrAfdeling, line);
					updateDienstverband(wnr, line);
					// Werknemer bestaat al, dan wijzigen
					try {
						validateWerknemerSimple(wnr, impresult);
						if (!context.getRollbackOnly() && impresult.importok){
							this.werknemer.updateWerknemer(wnr);
						}
					} catch (Exception e){
						log.info(e.getMessage(),e);
						setResult(importresult.UPDATERROR,impresult);
						impresult.setErrorMessage(e.getMessage());
					}
				}
				else{
					// Meerdere werknemers met dit BSN, dan is het waarschijnlijk een dummy-BSN en dan voeren we
					// de werknemer gewoon op
					AdresInfo adr = new AdresInfo();
					adr = setAdres(adr, line);
					wnr.setAdres(adr);
					wnr = setWerknemer(wnr, line);
					wnr.setWerkgever(wgr);
					wnr.setWerkgeverid(wgr.getId());
					
					updateAfdelingen(wnr, wnrAfdeling, line);
					updateDienstverband(wnr, line);
					// Werknemer bestaat nog niet, dan opvoeren
					try {
						validateWerknemerSimple(wnr, impresult);
						if (!context.getRollbackOnly() && impresult.importok){
							this.werknemer.addWerknemer(wnr);
						}
					} catch (Exception e){
						log.info(e.getMessage(),e);
						setResult(importresult.UPDATERROR,impresult);
						impresult.setErrorMessage(e.getMessage());
					}
				}
			}
		}
	}
	private void setImportWerknemerInfo(String[] tokens, ImportWerknemerInfo iwi) throws ValidationException, ParseException {
		iwi.setAchternaam(tokens[importkolommen.ACHTERNAAM.ordinal()]);
		iwi.setAdres(tokens[importkolommen.ADRES.ordinal()]);
		iwi.setAfdelingsnaam(tokens[importkolommen.AFDELINGSNAAM.ordinal()]);
		iwi.setBurgerlijkestaat(setBurgerlijkeStaat(tokens[importkolommen.BURGERLIJKESTAAT.ordinal()]));
		iwi.setBurgerservicenummer(formatBSN(tokens[importkolommen.BSN.ordinal()]));
		if (tokens[importkolommen.DATUMBEGINAFDELING.ordinal()].isEmpty())
			tokens[importkolommen.DATUMBEGINAFDELING.ordinal()] = tokens[importkolommen.DATUMINDIENST.ordinal()]; 
		iwi.setDatumbeginafdeling(parseDate(tokens[importkolommen.DATUMBEGINAFDELING.ordinal()],"d-MM-yyyy"));
		iwi.setDatumindienst(parseDate(tokens[importkolommen.DATUMINDIENST.ordinal()],"d-MM-yyyy"));
		iwi.setDatumuitdienst(parseDate(tokens[importkolommen.DATUMUITDIENST.ordinal()],"d-MM-yyyy"));
		iwi.setGeboortedatum(parseDate(tokens[importkolommen.GEBOORTEDATUM.ordinal()],"d-MM-yyyy"));
		iwi.setEmail(tokens[importkolommen.EMAIL.ordinal()]);
		iwi.setExternafdelingsid(tokens[importkolommen.EXTERNAFDELINGSID.ordinal()]);
		iwi.setFunctie(tokens[importkolommen.FUNCTIE.ordinal()]);
		iwi.setGeslacht(setGeslacht(tokens[importkolommen.GESLACHT.ordinal()]));
		iwi.setPersoneelsnummer(tokens[importkolommen.PERSONEELSNR.ordinal()]);
		iwi.setPostcode(tokens[importkolommen.POSTCODE.ordinal()]);
		iwi.setTelefoonmobiel(tokens[importkolommen.TELMOBIEL.ordinal()]);
		iwi.setTelefoonprive(tokens[importkolommen.TELPRIVE.ordinal()]);
		iwi.setTelefoonwerk(tokens[importkolommen.TELWERK.ordinal()]);
		iwi.setVoorletters(tokens[importkolommen.VOORLETTERS.ordinal()]);
		iwi.setVoornaam(tokens[importkolommen.VOORNAAM.ordinal()]);
		iwi.setVoorvoegsel(tokens[importkolommen.VOORVOEGSEL.ordinal()]);
		iwi.setWerkweek(parseDecimal(tokens[importkolommen.WERKWEEK.ordinal()]));
		iwi.setWiapercentage(setWiapercentage(tokens[importkolommen.WAOPERCENTAGE.ordinal()]));
		iwi.setWoonplaats(tokens[importkolommen.WOONPLAATS.ordinal()]);
	}
	private void validateWerknemerSimple(WerknemerInfo wnr, ImportResult result) throws ValidationExceptionNoabort {
	List<DienstverbandInfo> dienstVerbanden = wnr.getDienstVerbanden();
		Collections.sort(dienstVerbanden, new Dienstverbandcompare());
		if (!dienstVerbanden.isEmpty()){
			DienstverbandInfo vorigdienstverband = dienstVerbanden.get(0);
			DienstverbandInfo volgenddienstverband;
			int i = 1;
			while (i < dienstVerbanden.size()){
				volgenddienstverband = dienstVerbanden.get(i);
				if (volgenddienstverband.getAction() != persistenceaction.DELETE && vorigdienstverband.getAction() != persistenceaction.DELETE){
					if (vorigdienstverband.getEinddatumcontract() == null){
						setResult(importresult.VALIDATIONERROR,result);
						result.setErrorMessage("Oude niet afgesloten dienstverbanden gevonden");
						return;
					}
					if (DateOnly.after(volgenddienstverband.getStartdatumcontract(), vorigdienstverband.getEinddatumcontract())){
						/*skip*/
					}else{
						setResult(importresult.VALIDATIONERROR,result);
						result.setErrorMessage("Overlappende dienstverbanden");
						return;
					}
				}
				i++;
				vorigdienstverband = volgenddienstverband;
			}
		}
		else{
			setResult(importresult.VALIDATIONERROR,result);
			result.setErrorMessage("Dienstverband informatie ontbreekt");
			return;
		}
		if (wnr.getAfdelingen() == null){
			setResult(importresult.VALIDATIONERROR,result);
			result.setErrorMessage("Afdeling niet ingevuld");
			return;
		}
		if (wnr.getAfdelingen().isEmpty()){
			setResult(importresult.VALIDATIONERROR,result);
			result.setErrorMessage("Afdeling niet ingevuld");
			return;
		}
		Collections.sort(wnr.getAfdelingen(), new Afdelingcompare());
		if (!wnr.getAfdelingen().isEmpty()){
			AfdelingHasWerknemerInfo vorigeafdeling = wnr.getAfdelingen().get(0);
			AfdelingHasWerknemerInfo volgendeafdeling;
			int i = 1;
			while (i < wnr.getAfdelingen().size()){
				volgendeafdeling = wnr.getAfdelingen().get(i);
				if (volgendeafdeling.getAction() != persistenceaction.DELETE && vorigeafdeling.getAction() != persistenceaction.DELETE){
					if (vorigeafdeling.getEinddatum() == null){
						setResult(importresult.VALIDATIONERROR,result);
						result.setErrorMessage("Oude niet afgesloten afdeling gevonden");
						return;
					}
					if (DateOnly.after(volgendeafdeling.getStartdatum(), vorigeafdeling.getEinddatum())){
						/*skip*/
					}else{
						setResult(importresult.VALIDATIONERROR,result);
						result.setErrorMessage("Overlappende afdelingen");
						return;
					}
				}
				i++;
				vorigeafdeling = volgendeafdeling;
			}
		}
	}
	public List<ImportResult> importUren(String separator, byte[] uploadedFile, boolean fieldnamespresent) throws ValidationException{
		/* uploadedFile is a csv file */
		/* 
		Kol	Naam Veld						Verplicht	Betekenis							Formaat		Toegestane Waarden	Voorbeeld
		nr									(J/N)				
		1	BSN									J	    Burgerservicenummer	                N-9 		(geen punten!)		123456789
		2	Uren laatste maand					J	    Totaal aantal uren laatste mnd      N-9			142,15
		3	Gemiddeld per week laatste mnd		J	    									N-9	
		4	Uren laatste 3 maanden				J	    									N-9	
		5	Gemiddeld per maand					J	    									N-9	
		6	Gemiddeld per week laatste 3 mnd	J	    									N-9	
*/		
		
/*
 * 		XML Document met resultaten:
 * 
 * 		<importresults>
 * 			<werknemer result="">
 * 				<sourceline>
 * 				</sourceline>
 * 				<errormessage>
 * 				</errormessage>
 * 			</werknemer>
 * 		</importresults>		
 */
		boolean trxaborted = false;
		werknemer.setCurrentuser(getCurrentuser());
		List<ImportResult> importresults = new ArrayList<>();
	
		WerknemerInfo wnr;
		String strLine = "";
		try {
			// Load the byte array to an input stream for reading.
			ByteArrayInputStream bis = new ByteArrayInputStream(uploadedFile);

			// Load the input stream to a buffered reader to read the data.
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));

			if (fieldnamespresent)
				// skip the first line
				br.readLine();
			// Read comma separated file line by line
			while ((strLine = br.readLine()) != null) {
				// Break comma separated line using the separator in the calling parameters
				importresult result = importresult.OK;
				ImportResult impresult = new ImportResult();
				
				impresult.setImportok(true);
				impresult.setResult(importresult.OK.ordinal());
				impresult.setSourceLine(strLine);

				String[] tokens = strLine.split(separator);
				
				if (importexcelUren.length != tokens.length){
					setResult(importresult.FIELDSMISSING, impresult);
					impresult.setErrorMessage("Aantal kolommen(" + tokens.length + ") niet zoals verwacht("+ importexcel.length +")");
				}

				if (impresult.isImportok()){
				// check syntax
					StringBuilder errormessage = new StringBuilder();
					if (isEmtpyRow(tokens))
						result = setResult(importresult.EMPTYROW, impresult);
					else
						result = parseLine(tokens, errormessage, importexcelUren);
					if (result != importresult.OK){
						setResult(result,impresult);
						impresult.setErrorMessage(errormessage.toString());
					}
				}
				
				if (impresult.isImportok() && !trxaborted){
    				List<WerknemerInfo> werknemers = this.werknemer.getByBSN(formatBSN(tokens[importkolommenKFCuren.BSN.ordinal()]));
    				if (werknemers == null || werknemers.isEmpty()){
   						setResult(importresult.WARNING,impresult);
   						impresult.setErrorMessage("Werknemer niet gevonden");
    				}
    				else{
    					wnr = null;
    					if (werknemers.size() > 1){
    						for (WerknemerInfo wi: werknemers){
    							if (wi.isActief() && wi.getWerkgever().isActief()){
	    							if (wnr != null){
	    		   						setResult(importresult.WARNING,impresult);
	    		      						impresult.setErrorMessage("BSN niet uniek, meerdere actieve werknemers met zelfde BSN gevonden");
	    							}
	    							wnr = wi;
    							}
    						}
    					}
    					if (werknemers.size() == 1){
    						// Werknemer bestaat, dan aanpassen
    						wnr = werknemers.get(0);
    					}
    					if (wnr != null){
	    					DienstverbandInfo dvbi = wnr.getActiefDienstverband();
	    					if (dvbi != null){
		    					if (wnr.hasOpenVerzuim(dvbi)){
		    						// We passen de uren niet aan, want die zijn waarschijnlijk nul
		    					}else{
		    						Date lastverzuim = null;
		    						for (VerzuimInfo vzi:dvbi.getVerzuimen()){
		    							if (lastverzuim == null || vzi.getEinddatumverzuim().after(lastverzuim)){
		    								lastverzuim = vzi.getEinddatumverzuim();
		    							}
		    						}
		    						try{
			    						if (lastverzuim == null){
											dvbi.setWerkweek(parseDecimal(tokens[importkolommenKFCuren.GEMIDDELDPERWEEKLAATSTE3MND.ordinal()]));
				       						werknemer.updateWerknemer(wnr);
			    						}else{
			    							if (lastverzuim != null && (Math.abs(DateOnly.diffDays(lastverzuim, new Date()))) > 30){
			    								dvbi.setWerkweek(parseDecimal(tokens[importkolommenKFCuren.GEMIDDELDPERWEEKLAATSTE3MND.ordinal()]));
			    	       						werknemer.updateWerknemer(wnr);
			    							}
			    						}
		    						} catch (ValidationException e){
		    							log.info(e.getMessage(),e);
		    							throw new ValidationException("BSN: " + wnr.getBurgerservicenummer()+ ": " + e.getMessage());
		    						}
		    					}
	    					}
	    				}
    				}
				}
				importresults.add(impresult);
				if (result == importresult.EMPTYROW){
					/* voor lege rijen maken we een uitzondering */
				}else{
					if (!impresult.isImportok())
						trxaborted = true;
				}
			}
		} catch (ValidationException e) {
			throw e;
		} catch (Exception e) {
			log.info("Error in File Processing", e);
			throw new ValidationException(e.getMessage());
		}
		return importresults;
	}
	public List<ImportResult> afsluitenDienstverbanden(int holding, String separator, byte[] uploadedFile, boolean fieldnamespresent) throws ValidationException, VerzuimApplicationException{
		/* uploadedFile is a csv file */
		/* 
		Kol	Naam Veld						Verplicht	Betekenis							Formaat		Toegestane Waarden	Voorbeeld
		nr									(J/N)				
		1	BSN									J	    Burgerservicenummer	                N-9 		(geen punten!)		123456789
		2	Naam								N	    								    A-50
		3	Datum in dienst						J	    									D	
		4	Datum uit dienst					J	    									D	
		5	Medewerker							N	    									A-50	
		6	Reden eind dienstverband			N	    									N-2
		7	Omschrijving reden					N											A-50
*/		
		
/*
 * 		XML Document met resultaten:
 * 
 * 		<importresults>
 * 			<werknemer result="">
 * 				<sourceline>
 * 				</sourceline>
 * 				<errormessage>
 * 				</errormessage>
 * 			</werknemer>
 * 		</importresults>		
 */
		werknemer.setCurrentuser(getCurrentuser());
		List<ImportResult> importresults = new ArrayList<>();
	
		WerknemerInfo wnr;
		String strLine = "";
		try {
			// Load the byte array to an input stream for reading.
			ByteArrayInputStream bis = new ByteArrayInputStream(uploadedFile);

			// Load the input stream to a buffered reader to read the data.
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));

			if (fieldnamespresent)
				// skip the first line
				br.readLine();
			// Read comma separated file line by line
			while ((strLine = br.readLine()) != null) {
				// Break comma separated line using the separator in the calling parameters
				importresult result = importresult.OK;
				ImportResult impresult = new ImportResult();
				
				impresult.setImportok(true);
				impresult.setResult(importresult.OK.ordinal());
				impresult.setSourceLine(strLine);

				String[] tokens = strLine.split(separator);
				
				if (importexcelUitdienst.length != tokens.length){
					setResult(importresult.FIELDSMISSING, impresult);
					impresult.setErrorMessage("Aantal kolommen(" + tokens.length + ") niet zoals verwacht("+ importexcel.length +")");
				}

				if (impresult.isImportok()){
				// check syntax
					StringBuilder errormessage = new StringBuilder();
					if (isEmtpyRow(tokens))
						result = setResult(importresult.EMPTYROW, impresult);
					else
						result = parseLine(tokens, errormessage, importexcelUitdienst);
					if (result != importresult.OK){
						setResult(result,impresult);
						impresult.setErrorMessage(errormessage.toString());
					}
				}
				
				if (impresult.isImportok() && !context.getRollbackOnly()){
    				List<WerknemerInfo> werknemers = this.werknemer.getByBSN(formatBSN(tokens[importkolommenKFCuitdienst.bsn.ordinal()]));
    				if (werknemers == null || werknemers.isEmpty()){
   						setResult(importresult.WARNING,impresult);
   						impresult.setErrorMessage("Werknemer niet gevonden");
    				}
    				else{
    					wnr = null;
    					if (werknemers.size() > 1){
    						for (WerknemerInfo wi: werknemers){
    							if (wi.getWerkgever().getHoldingId() != null && 
    									wi.getWerkgever().getHoldingId() == holding && wi.isActief()){
    								/* werknemer met bsn bij deze werkgever onder de opgegeven holding (KFC) */
    								/* met een actief dienstverband */
   									wnr = wi;
    							}
    						}
							if (wnr == null){
	   							setResult(importresult.WARNING,impresult);
	       						impresult.setErrorMessage("BSN komt vaker voor, maar geen actief dienstverband onder deze holding");
							}
    					}
    					else{
        					if (werknemers.size() == 1){
        						/* Werknemer bestaat, dan deze kijken of dit bij de holding hoort */
								wnr = werknemers.get(0);
    							if (wnr.getWerkgever().getHoldingId() == null || wnr.getWerkgever().getHoldingId() != holding){
    	   							setResult(importresult.WARNING,impresult);
    	       						impresult.setErrorMessage("BSN gevonden, maar geen actief dienstverband onder deze holding");
    							}
        					}
    					}
    					if (wnr != null && impresult.importok){
	    					DienstverbandInfo dvbi = wnr.getActiefDienstverband();
	    					if (dvbi != null){
		    					ImportWerknemerInfo line = new ImportWerknemerInfo();
		    					line.setBurgerservicenummer(formatBSN(tokens[importkolommenKFCuitdienst.bsn.ordinal()]));
		    					line.setDatumindienst(tokens[importkolommenKFCuitdienst.datumInDienst.ordinal()]);
		    					line.setDatumuitdienst(tokens[importkolommenKFCuitdienst.datumUitDienst.ordinal()]);
		    					if (dvbi.getFunctie() != null)
		    						line.setFunctie(dvbi.getFunctie());
		    					else
		    						line.setFunctie("");
		    					line.setPersoneelsnummer(dvbi.getPersoneelsnummer());
		    					line.setWerkweek(dvbi.getWerkweek());
	
		    					updateDienstverband(wnr,line);
		    					try{
			    					this.werknemer.updateWerknemer(wnr);
		    					} catch (ValidationException e){
		    						log.info(e.getMessage(),e);
		    						setResult(importresult.WARNING,impresult);
		    						impresult.setErrorMessage(e.getMessage());
		    					}
	    					}else{
	   							setResult(importresult.WARNING,impresult);
	       						impresult.setErrorMessage("Geen actief dienstverband gevonden");
	    					}
    					}
    				}
				}
				importresults.add(impresult);
				if (result == importresult.EMPTYROW){
					/* voor lege rijen maken we een uitzondering */
				}
			}
		} catch (Exception e) {
			log.info("Error in File Processing :",e);
			throw new ValidationException(e.getMessage());
		}
		return importresults;
	}
	public List<WerkgeverInfo> getWerkgeversHolding(Integer id) throws VerzuimApplicationException  {
		Query q = createQuery(querydefault + " where w.holding_ID = " + id.toString());
		@SuppressWarnings("unchecked")
		List<Werkgever> result = (List<Werkgever>)getResultList(q);
		List<WerkgeverInfo> inforesult = new ArrayList<>();
		for (Werkgever w: result)
			inforesult.add(completeWerkgever(w, true));
	
		return inforesult;
	}

}
