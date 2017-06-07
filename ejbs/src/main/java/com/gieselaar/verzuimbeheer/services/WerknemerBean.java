package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemer;
import com.gieselaar.verzuimbeheer.entities.Dienstverband;
import com.gieselaar.verzuimbeheer.entities.Verzuim;
import com.gieselaar.verzuimbeheer.entities.WerknemerFast;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.entities.Wiapercentage;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.VerzuimConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

/**
 * Session Bean implementation class WerknemerBean
 */
@Stateless
@LocalBean
// @WebService
public class WerknemerBean extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	AdresBean adres;
	@EJB
	WerknemerConversion converter;
	@EJB
	WerkgeverConversion werkgeverconverter;
	@EJB
	AdresConversion adresconverter;
	@EJB
	VerzuimConversion verzuimconverter;
	@EJB
	VerzuimBean verzuim;
	@EJB
	WerkgeverBean werkgever;
	@EJB
	SettingsBean settings;
	/**
	 * Default constructor.
	 */
	private Werknemer werknemer = null;
	private HashMap<Integer, WerkgeverInfo> hashwerkgevers = new HashMap<>();
	private static String werknemerQuery = 
			  "select w.id, d.id, afd.id, w.Achternaam,count(v.id) as vzmcnt,sum(if(v.einddatumverzuim is null,1,0)) as openvzm "
			+ ", w.burgerlijkestaat, w.burgerservicenummer, w.email, w.geboortedatum, w.geslacht, w.mobiel "
			+ ", w.opmerkingen, w.telefoon, w.telefoonprive, w.voorletters, w.voornaam, w.voorvoegsel, w.wijzigingsdatum "
			+ ", d.einddatumcontract, d.startdatumcontract, d.werkweek, d.personeelsnummer, wg.naam as werkgevernaam "
			+ ", afd.naam as afdelingnaam "
			+ "from      WERKNEMER w " 
			+ "join      DIENSTVERBAND d on d.werknemer_ID = w.id "
			+ "join      WERKGEVER wg on wg.id = d.werkgever_ID "
			+ "left join VERZUIM v on d.id = v.dienstverband_ID " 
			+ "left join AFDELING_HAS_WERKNEMER ahwn on ahwn.werknemer_ID = w.id "
			+ "and ((ahwn.einddatum is null and d.einddatumcontract is null)" 
			+ "or ((ahwn.einddatum = d.einddatumcontract or ahwn.einddatum is null) " 
			+ "and d.einddatumcontract is not null)) "
			+ "left join AFDELING afd on ahwn.afdeling_id = afd.id "
			+ "where d.startdatumcontract = "
			+ "(select max(d.startdatumcontract) from DIENSTVERBAND d where d.werknemer_ID = w.id) ";
	private static String groupAndOrderBy = " group by w.id, d.id, afd.id order by d.werkgever_ID, w.id";

	public enum __inclusive{
		ACTIVEANDINACTIVE,
		ACTIVEONLY,
		INACTIVEONLY;
	}
	public List<WerknemerFastInfo> getByWerkgever(Integer werkgeverid) throws VerzuimApplicationException {
		return getAllWerknemers(werkgeverid,__inclusive.ACTIVEANDINACTIVE);
	}

	public List<WerknemerFastInfo> getAll() throws VerzuimApplicationException {
		return getAllWerknemers(null, __inclusive.ACTIVEANDINACTIVE);
	}
	public List<WerknemerFastInfo> getInactiverWerknemersByWerkgever(Integer werkgeverid) throws VerzuimApplicationException {
		return getAllWerknemers(werkgeverid,__inclusive.INACTIVEONLY);
	}

	public List<WerknemerFastInfo> getInactiveWerknemers() throws VerzuimApplicationException {
		return getAllWerknemers(null, __inclusive.INACTIVEONLY);
	}

	public List<WerknemerFastInfo> getActiveWerknemersByWerkgever(Integer werkgeverid) throws VerzuimApplicationException {
		return getAllWerknemers(werkgeverid,__inclusive.ACTIVEONLY);
	}

	public List<WerknemerFastInfo> getActiveWerknemers() throws VerzuimApplicationException {
		return getAllWerknemers(null, __inclusive.ACTIVEONLY);
	}

	public List<WerknemerFastInfo> getWerknemerFast(Integer werknemerId) throws VerzuimApplicationException {
		werkgever.setCurrentuser(getCurrentuser());
		String queryString;
		Query qw;
		if (werknemerId == null) {
			queryString = werknemerQuery + "and (wg.einddatumcontract is null or wg.einddatumcontract > ?1)"
					+ groupAndOrderBy;
			qw = em.createNativeQuery(queryString, WerknemerFast.class);
			qw.setParameter(1, new Date());
		} else {
			queryString = werknemerQuery + "and w.id = ?1" + groupAndOrderBy;
			qw = em.createNativeQuery(queryString, WerknemerFast.class);
			qw.setParameter(1, werknemerId);
		}

		@SuppressWarnings("unchecked")
		List<WerknemerFast> resultWnr = (List<WerknemerFast>) getResultList(qw);

		List<WerknemerFastInfo> wnrl = new ArrayList<>();
		((ArrayList<WerknemerFastInfo>) wnrl).ensureCapacity(resultWnr.size());
		for (WerknemerFast wf : resultWnr) {
			wnrl.add(converter.fromEntity(wf));
		}
		return wnrl;
	}
	public List<WerknemerFastInfo> getAllWerknemers(Integer werkgeverId, __inclusive inclusive) throws VerzuimApplicationException {
		werkgever.setCurrentuser(getCurrentuser());

		String queryString = "";
		Query qw;
		if (werkgeverId == null) {
			switch (inclusive){
			case ACTIVEANDINACTIVE:
				queryString = werknemerQuery + "and (wg.einddatumcontract is null or wg.einddatumcontract > ?1)"
						+ groupAndOrderBy;
				break;
			case INACTIVEONLY:
				queryString = werknemerQuery + "and (wg.einddatumcontract is null or wg.einddatumcontract > ?1)"
						+ " and not d.einddatumcontract is null "
						+ groupAndOrderBy;
				break;
			case ACTIVEONLY:
				queryString = werknemerQuery + "and (wg.einddatumcontract is null or wg.einddatumcontract > ?1)"
						+ " and d.einddatumcontract is null "
						+ groupAndOrderBy;
				break;
			}
			qw = em.createNativeQuery(queryString, WerknemerFast.class);
			qw.setParameter(1, new Date());
		} else {
			switch (inclusive){
			case ACTIVEANDINACTIVE:
				queryString = werknemerQuery + "and wg.id = ?1"  
						+ groupAndOrderBy;
				break;
			case INACTIVEONLY:
				queryString = werknemerQuery + "and wg.id = ?1"  
						+ " and not d.einddatumcontract is null "
						+ groupAndOrderBy;
				break;
			case ACTIVEONLY:
				queryString = werknemerQuery + "and wg.id = ?1"  
						+ " and d.einddatumcontract is null "
						+ groupAndOrderBy;
				break;
			}
			qw = em.createNativeQuery(queryString, WerknemerFast.class);
			qw.setParameter(1, werkgeverId);
		}
		@SuppressWarnings("unchecked")
		List<WerknemerFast> resultWnr = (List<WerknemerFast>) getResultList(qw);

		List<WerknemerFastInfo> wnrl = new ArrayList<>();
		((ArrayList<WerknemerFastInfo>) wnrl).ensureCapacity(resultWnr.size());
		for (WerknemerFast wf : resultWnr) {
			wnrl.add(converter.fromEntity(wf));
		}
		return wnrl;

	}

	private WerknemerInfo completeWerknemerInfo(Werknemer w, List<Dienstverband> dvl, List<Verzuim> vzml,
			boolean detail) throws VerzuimApplicationException {
		WerknemerInfo wi = converter.fromEntity(w);
		wi.setWerkgever(hashwerkgevers.get(w.getWerkgever_ID()));
		List<DienstverbandInfo> dvil = new ArrayList<>();
		List<VerzuimInfo> vzil;
		Verzuim v;
		for (Dienstverband d : dvl) {
			DienstverbandInfo di = converter.fromEntity(d);
			int sizevzml = vzml.size();
			int inxvzm = 0;
			vzil = new ArrayList<>();
			if (!vzml.isEmpty()) {
				do {
					v = vzml.get(inxvzm);
					if (v.getDienstverband_ID() == d.getId())
						vzil.add(verzuimconverter.fromEntity(v));
					inxvzm++;
				} while (inxvzm < sizevzml);
			}

			di.setVerzuimen(vzil);
			dvil.add(di);
		}
		wi.setDienstVerbanden(dvil);
		if (detail) {
			wi.setDetailsPresent(true);

			Query qwia = createQuery(
					"select wia from Wiapercentage wia where wia.werknemer_ID = :wnr_id order by wia.werknemer_ID");
			qwia.setParameter("wnr_id", w.getId());
			@SuppressWarnings("unchecked")
			List<Wiapercentage> resultwia = (List<Wiapercentage>) getResultList(qwia);
			List<WiapercentageInfo> wial = new ArrayList<>();
			for (Wiapercentage wia : resultwia) {
				wial.add(converter.fromEntity(wia));
			}
			wi.setWiaPercentages(wial);
			Query qafd = createQuery("select a, aw from Afdeling a, AfdelingHasWerknemer aw "
					+ "where aw.id.afdeling_ID = a.id and aw.id.werknemer_ID = :wnr_id order by aw.id.werknemer_ID");
			qafd.setParameter("wnr_id", w.getId());
			@SuppressWarnings("unchecked")
			List<Object[]> resultafd = (List<Object[]>) getResultList(qafd);
			List<AfdelingHasWerknemerInfo> afdl = new ArrayList<>();
			for (Object[] afd : resultafd) {
				AfdelingHasWerknemerInfo afdwn = new AfdelingHasWerknemerInfo();
				afdwn.setAction(persistenceaction.UPDATE);
				afdwn.setState(persistencestate.EXISTS);
				afdwn = converter.fromEntity((AfdelingHasWerknemer) afd[1]);
				afdwn.setAfdeling(werkgeverconverter.fromEntity((Afdeling) afd[0]));
				afdl.add(afdwn);
			}
			wi.setAfdelingen(afdl);
		}
		return wi;
	}

	private List<WerknemerInfo> mergeDienstverbanden(List<Werknemer> resultw, List<Dienstverband> resultd,
			List<Verzuim> resultv, boolean detail) {

		List<WerknemerInfo> inforesult = new ArrayList<>();

		((ArrayList<WerknemerInfo>) inforesult).ensureCapacity(resultw.size());

		Dienstverband d = null;
		Werknemer w = null;
		Verzuim v = null;
		int inxdvb = 0;
		int inxvzm = 0;
		List<Dienstverband> dvl = new ArrayList<>();
		List<Verzuim> vzml = new ArrayList<>();

		int sizedvb = resultd.size();
		int sizevzm = resultv.size();
		try {
			for (Werknemer res : resultw) {
				w = res;
				dvl.clear();
				vzml.clear();

				/*
				 * Eerst de dienstverbanden bij de werknemer zoeken
				 */
				boolean eod = false;
				do {
					d = resultd.get(inxdvb);
					if (d.getWerknemer_ID() == w.getId()) {
						dvl.add(d);
						inxdvb++;
						/*
						 * Nu nog de verzuimen bij het dienstverband zoeken
						 */
						boolean eov = false;
						while ((inxvzm < sizevzm) && (!eov)) {
							v = resultv.get(inxvzm);
							if (v.getDienstverband_ID() == d.getId()) {
								vzml.add(v);
								inxvzm++;
							} else
								eov = true;
						}
					} else
						eod = true;
				} while ((inxdvb < sizedvb) && (!eod));

				inforesult.add(completeWerknemerInfo(w, dvl, vzml, detail));
			}
		} catch (Exception e) {
			log.info("Unexpted error during merge",e);
			applicationException(new ValidationException("Unexpected error during merge"));
		}
		System.currentTimeMillis();
		return inforesult;
	}

	private void createHashtabWerkgever(List<WerkgeverInfo> werkgevers) {
		for (WerkgeverInfo wg : werkgevers)
			hashwerkgevers.put(wg.getId(), wg);
	}

	@SuppressWarnings("unchecked")
	public WerknemerInfo getById(int werknemerid) throws VerzuimApplicationException {
		Query qw = createQuery("select w from Werknemer w left join fetch w.adres where w.id = :wnr_id");
		Query qd = createQuery(
				"select d from Dienstverband d where d.werknemer_ID = :wnr_id order by d.werknemer_ID, d.id");
		Query qv = createQuery("select v from Verzuim v, Dienstverband d, Werknemer w, Werkgever wg "
				+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and wg.id = w.werkgever_ID and d.werknemer_ID = :wnr_id "
				+ "order by d.werknemer_ID, v.dienstverband_ID");
		qw.setParameter("wnr_id", werknemerid);
		qd.setParameter("wnr_id", werknemerid);
		qv.setParameter("wnr_id", werknemerid);
		List<Werknemer> resultw = (List<Werknemer>) getResultList(qw);
		List<Dienstverband> resultd = (List<Dienstverband>) getResultList(qd);
		List<Verzuim> resultv = (List<Verzuim>) getResultList(qv);

		if (resultw.size() != 1)
			return null;
		else {
			return mergeDienstverbanden(resultw, resultd, resultv, true).get(0);
		}
	}

	public List<WerknemerInfo> getByBSN(Integer werkgeverId, String bsn) throws VerzuimApplicationException {
		/*
		 * Zoeken naar een BSN bij een bepaalde werkgever
		 */
		werkgever.setCurrentuser(getCurrentuser());
		Query qw = createQuery(
				"select w from Werknemer w left join fetch w.adres where w.werkgever_ID = :wg_id and w.burgerservicenummer = :bsn order by w.id");
		Query qd = createQuery("select d from Dienstverband d,Werknemer w "
				+ "where d.werkgever_ID = :wg_id and w.burgerservicenummer = :bsn and w.id = d.werknemer_ID order by d.werknemer_ID, d.id");
		Query qv = createQuery("select v from Verzuim v, Dienstverband d, Werknemer w, Werkgever wg "
				+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and wg.id = w.werkgever_ID and wg.id = :wg_id and w.burgerservicenummer = :bsn "
				+ "order by d.werknemer_ID, v.dienstverband_ID");
		if (hashwerkgevers.isEmpty()) {
			List<WerkgeverInfo> werkgevers = werkgever.getAll();
			createHashtabWerkgever(werkgevers);
		}

		qw.setParameter("wg_id", werkgeverId);
		qd.setParameter("wg_id", werkgeverId);
		qv.setParameter("wg_id", werkgeverId);
		qw.setParameter("bsn", bsn);
		qd.setParameter("bsn", bsn);
		qv.setParameter("bsn", bsn);
		@SuppressWarnings("unchecked")
		List<Werknemer> resultw = (List<Werknemer>) getResultList(qw);
		@SuppressWarnings("unchecked")
		List<Dienstverband> resultd = (List<Dienstverband>) getResultList(qd);
		@SuppressWarnings("unchecked")
		List<Verzuim> resultv = (List<Verzuim>) getResultList(qv);
		return mergeDienstverbanden(resultw, resultd, resultv, true);
	}

	public List<WerknemerInfo> getByBSN(String bsn) throws VerzuimApplicationException {
		/*
		 * Zoeken naar een BSN bij een alle werkgevers
		 */
		werkgever.setCurrentuser(getCurrentuser());
		Query qw = createQuery(
				"select w from Werknemer w left join fetch w.adres where w.burgerservicenummer = :bsn order by w.id");
		Query qd = createQuery("select d from Dienstverband d,Werknemer w "
				+ "where w.burgerservicenummer = :bsn and w.id = d.werknemer_ID order by d.werknemer_ID, d.id");
		Query qv = createQuery("select v from Verzuim v, Dienstverband d, Werknemer w, Werkgever wg "
				+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and wg.id = w.werkgever_ID and w.burgerservicenummer = :bsn "
				+ "order by d.werknemer_ID, v.dienstverband_ID");
		if (hashwerkgevers.isEmpty()) {
			List<WerkgeverInfo> werkgevers = werkgever.getAll();
			createHashtabWerkgever(werkgevers);
		}

		qw.setParameter("bsn", bsn);
		qd.setParameter("bsn", bsn);
		qv.setParameter("bsn", bsn);
		@SuppressWarnings("unchecked")
		List<Werknemer> resultw = (List<Werknemer>) getResultList(qw);
		@SuppressWarnings("unchecked")
		List<Dienstverband> resultd = (List<Dienstverband>) getResultList(qd);
		@SuppressWarnings("unchecked")
		List<Verzuim> resultv = (List<Verzuim>) getResultList(qv);
		return mergeDienstverbanden(resultw, resultd, resultv, true);
	}

	public WerknemerInfo addWerknemer(WerknemerInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		if (!getByBSN(info.getWerkgeverid(), info.getBurgerservicenummer()).isEmpty())
			throw new ValidationException("BSN komt al voor bij deze werkgever!");
		if (info.getDienstVerbanden() == null || info.getDienstVerbanden().isEmpty())
			applicationException(new ValidationException("Dienstverband ontbreekt"));

		if (info.getWiaPercentages() == null || info.getWiaPercentages().isEmpty())
			applicationException(new ValidationException("Wiapercentage ontbreekt"));

		adres.setCurrentuser(getCurrentuser());
		info.setAdres(adres.update(info.getAdres()));
		this.werknemer = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.werknemer);

		for (DienstverbandInfo d: info.getDienstVerbanden()){
			d.setWerknemerId(this.werknemer.getId());
		}
		updateDienstverbanden(info.getDienstVerbanden());
		updateAfdelingen(info.getAfdelingen(),null);
		updateWiaPercentages(info.getWiaPercentages());

		return this.getById(this.werknemer.getId());
	}

	private void updateWiaPercentages(List<WiapercentageInfo> wiaPercentages) throws VerzuimApplicationException {
		if (wiaPercentages != null) {
			for (WiapercentageInfo wpi : wiaPercentages) {
				Wiapercentage wp = converter.toEntity(wpi, this.getCurrentuser());
				wp.setWerknemer_ID(this.werknemer.getId());
				switch (wpi.getAction()) {
				case DELETE:
					this.deleteEntity(wp);
					break;
				case INSERT:
					this.insertEntity(wp);
					break;
				case UPDATE:
					this.updateEntity(wp);
					break;
				}
			}
		}
	}

	private void updateDienstverbanden(List<DienstverbandInfo> dienstVerbanden) throws VerzuimApplicationException, ValidationException {
		if (dienstVerbanden != null) {
			for (DienstverbandInfo dv : dienstVerbanden) {
				Dienstverband d = converter.toEntity(dv, this.getCurrentuser());
				d.setWerknemer_ID(this.werknemer.getId());
				switch (dv.getAction()) {
				case DELETE:
					this.deleteEntity(d);
					break;
				case INSERT:
					dv.validate();
					this.insertEntity(d);
					break;
				case UPDATE:
					dv.validate();
					this.updateEntity(d);
					break;
				}
			}
		}
	}

	private void updateAfdelingen(List<AfdelingHasWerknemerInfo> afdelingen, List<AfdelingHasWerknemerInfo> oorspronkelijkeafdelingen) throws VerzuimApplicationException {
		/*
		 * First find out which old departments are no longer used, and delete them
		 */
		boolean found;
		if (oorspronkelijkeafdelingen != null){
			for (AfdelingHasWerknemerInfo oorspronkelijkeawi:oorspronkelijkeafdelingen){
				found = false;
				for (AfdelingHasWerknemerInfo awi: afdelingen){
					if (awi.getAfdelingId() == oorspronkelijkeawi.getAfdelingId()){
						found = true;
						break;
					}
				}
				if (!found){
					AfdelingHasWerknemer aw = converter.toEntity(oorspronkelijkeawi);
					this.deleteEntity(aw);
				}
			}
		}
		if (afdelingen != null) {
			for (AfdelingHasWerknemerInfo awi : afdelingen) {
				awi.setWerknemerId(this.werknemer.getId());
				AfdelingHasWerknemer aw = converter.toEntity(awi);
				switch (awi.getAction()) {
				case DELETE:
					this.deleteEntity(aw);
					break;
				case INSERT:
					this.insertEntity(aw);
					break;
				case UPDATE:
					this.updateEntity(aw);
					break;
				}
			}
		}
	}

	public boolean deleteWerknemer(WerknemerInfo wnrinfo) throws VerzuimApplicationException {
		Query qwi = createQuery("Delete from Wiapercentage wia where wia.werknemer_ID = :wnr_id");
		Query wa = createQuery("Delete from AfdelingHasWerknemer wa where wa.id.werknemer_ID = :wnr_id");
		Query dv = createQuery("Delete from Dienstverband d where d.werknemer_ID = :wnr_id");
		Query qw = createQuery("Delete from Werknemer w where w.id = :wnr_id");

		WerknemerInfo info = this.getById(wnrinfo.getId());
		for (DienstverbandInfo dvb : info.getDienstVerbanden()) {
			verzuim.deleteVerzuimenDienstverband(dvb.getId());
		}

		wa.setParameter("wnr_id", info.getId());
		dv.setParameter("wnr_id", info.getId());
		qw.setParameter("wnr_id", info.getId());
		qwi.setParameter("wnr_id", info.getId());
		executeUpdate(wa);
		executeUpdate(dv);
		executeUpdate(qw);
		executeUpdate(qwi);

		return true;
	}

	public boolean updateWerknemer(WerknemerInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		if (info.getLaatsteDienstverband().getEinddatumcontract() != null) {
			for (AfdelingHasWerknemerInfo afd : info.getAfdelingen()) {
				/*
				 * Als dienstverband wordt afgesloten, dan moet ook de nog
				 * actieve afdeling worden afgesloten
				 */
				if (afd.getEinddatum() == null) {
					afd.setEinddatum(info.getLaatsteDienstverband().getEinddatumcontract());
				}
			}
			List<VerzuimInfo> verzuimen = verzuim.getVerzuimenDienstverband(info.getLaatsteDienstverband().getId());
			if (verzuimen != null) {
				for (VerzuimInfo vzmi : verzuimen) {
					if (vzmi.getEinddatumverzuim() == null) {
						/*
						 * Als Einddatum contract in de toekomst ligt: Einddatum
						 * contract moet na startdatum verzuim liggen
						 * 
						 */
						checkZiekUitDient(vzmi, info.getLaatsteDienstverband().getEinddatumcontract());
						if (info.getLaatsteDienstverband().getEinddatumcontract().after(new Date())) {
							if (DateOnly.after(info.getLaatsteDienstverband().getEinddatumcontract(),
									vzmi.getStartdatumverzuim())) {
								/* Niets te doen */
							} else {
								throw new ValidationException("Startdatum open verzuim ligt na einde dienstverband!");
							}
						} else {
							throw new ValidationException(
									"Kan dienstverband niet afsluiten. Er is nog een open verzuim");
						}
					} else {
						if (DateOnly.before(info.getLaatsteDienstverband().getEinddatumcontract(),
								vzmi.getEinddatumverzuim())) {
							throw new ValidationException(
									"Einddatum dienstverband ligt voor einddatum (laatste) verzuim!");
						}
					}
				}
			}
		}

		this.werknemer = converter.toEntity(info, this.getCurrentuser());
		updateDienstverbanden(info.getDienstVerbanden());
		this.updateEntity(this.werknemer);
		WerknemerInfo updatedWnr = this.getById(this.werknemer.getId());
		updateAfdelingen(info.getAfdelingen(), updatedWnr.getAfdelingen());
		updateWiaPercentages(info.getWiaPercentages());

		return true;
	}

	public void checkZiekUitDient(VerzuimInfo verzuiminfo, Date einddatumcontract) throws VerzuimApplicationException, ValidationException {
		boolean todofound;
		Date einddatumtodo;
		Date einddatumwarn;
		Calendar cal = Calendar.getInstance();
		cal.setTime(einddatumcontract);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		einddatumtodo = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		einddatumwarn = cal.getTime();
		SettingsInfo ss = settings.getSettings();
		Integer todoId = ss.getTodoforafsluitendienstverband();
		if (todoId == null || todoId.equals(-1)){
			/* Skip, not used */
		}else{
			VerzuimInfo vzmi = verzuim.getVerzuimDetails(verzuiminfo.getId());
			todofound = false;
			for (TodoInfo ti: vzmi.getTodos()){
				if (ti.getActiviteitId() == todoId){
					todofound = true;
					ti.setWaarschuwingsdatum(einddatumwarn);
					ti.setDeadlinedatum(einddatumtodo);
					verzuim.updateEntity(verzuimconverter.toEntity(ti, this.getCurrentuser()));
				}
			}
			if (!todofound){
				TodoInfo todo = new TodoInfo();
				todo.setUser(this.getCurrentuser());
				todo.setAchternaam("SYSTEM");
				todo.setHerhalen(false);
				todo.setSoort(__soort.AUTOMATISCH); // Hierdoor wordt de Verzuimtodo automatisch verwijderd
				todo.setVerzuimId(vzmi.getId());
				todo.setWaarschuwingsdatum(einddatumwarn);
				todo.setAanmaakdatum(new Date());
				todo.setActiviteitId(todoId);
				todo.setDeadlinedatum(einddatumtodo);
				todo.setHerhalen(false);
				todo.setOpmerking("Controleer ziek uit dienst");
					
				verzuim.addTodo(todo);
				
			}
		}
	}

	public void deleteWerknemers(int werkgeverid) throws VerzuimApplicationException {

		List<WerknemerFastInfo> wnrs = getByWerkgever(werkgeverid);
		for (WerknemerFastInfo wnr : wnrs) {
			WerknemerInfo wi = new WerknemerInfo();
			wi.setId(wnr.getId());
			this.deleteWerknemer(wi);
		}
	}
}
