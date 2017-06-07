package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Btw;
import com.gieselaar.verzuimbeheer.entities.Factuur;
import com.gieselaar.verzuimbeheer.entities.FactuurTotaal;
import com.gieselaar.verzuimbeheer.entities.Factuurbetaling;
import com.gieselaar.verzuimbeheer.entities.Factuurcategorie;
import com.gieselaar.verzuimbeheer.entities.Factuuritem;
import com.gieselaar.verzuimbeheer.entities.Factuurkop;
import com.gieselaar.verzuimbeheer.entities.Factuurregelbezoek;
import com.gieselaar.verzuimbeheer.entities.Factuurregelitem;
import com.gieselaar.verzuimbeheer.entities.Factuurregelsecretariaat;
import com.gieselaar.verzuimbeheer.entities.Oe;
import com.gieselaar.verzuimbeheer.entities.Werkzaamheden;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.FactuurInfo.__factuurstatus;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

/**
 * Session Bean implementation class InstantieBean
 */
@Stateless
@LocalBean
public class FactuurBean extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FactuurConversion converter;
	private WerkgeverConversion werkgeverconverter;
	@EJB
	WerkgeverBean werkgeverEJB;
	@EJB
	TariefBean tariefEJB;

	private String selectFactuur = "select f.*, t.vasttariefhuisbezoeken as vasttariefhuisbezoeken" + " from FACTUUR f "
			+ " join TARIEF t on f.tariefid = t.id ";

	private String selectFactuurBezoeken = "select  coalesce(sum(fb.uurkosten),0), coalesce(sum(fb.kilometerkosten),0),"
			+ " coalesce(sum(fb.vastekosten),0), "
			+ " coalesce(sum(fb.overigekosten),0), coalesce(sum(fb.casemanagementkosten),0) "
			+ " from Factuurregelbezoek fb where fb.factuurid = :id";
	private String selectFactuurSecretariaat = "select coalesce(sum(fs.overigekosten),0), "
			+ " coalesce(sum(fs.secretariaatskosten),0)" + " from Factuurregelsecretariaat fs where fs.factuurid = :id";
	private String selectFactuurItems = "select coalesce(sum(fi.bedrag),0) "
			+ ",coalesce(sum(case when (fi.btwcategorie = 1) then fi.btwbedrag else 0 end),0) "
			+ ",coalesce(sum(case when (fi.btwcategorie = 2) then fi.btwbedrag else 0 end),0)  "
			+ " from Factuurregelitem fi where fi.factuurid = :id";
	private String selectFactuurBetalingen = "select coalesce(sum(fb.bedrag),0) "
			+ " from Factuurbetaling fb where fb.factuurid = :id";

	/**
	 * Default constructor.
	 */
	public FactuurBean() {
		converter = new FactuurConversion();
		werkgeverconverter = new WerkgeverConversion();
	}

	private BtwInfo completeBtw(Btw b) {
		return converter.fromEntity(b);
	}

	public BtwInfo getBtwById(Integer id) throws VerzuimApplicationException, ValidationException {
		Query q = createQuery("select b from Btw b where b.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Btw> result = (List<Btw>) getResultList(q);
		if (result.isEmpty()) {
			return null;
		}
		if (result.size() > 1) {
			throw applicationException(new ValidationException("Meerdere rijen met zelfde id."));
		}
		return completeBtw(result.get(0));

	}

	public BtwInfo getActueelBtwPercentage(__btwtariefsoort soort)
			throws VerzuimApplicationException, ValidationException {
		Query q = createQuery("select b from Btw b where b.btwtariefsoort = :soort and b.einddatum is null");
		q.setParameter("soort", soort.getValue());
		@SuppressWarnings("unchecked")
		List<Btw> result = (List<Btw>) getResultList(q);
		if (result.isEmpty()) {
			return null;
		}
		if (result.size() > 1) {
			throw applicationException(
					new ValidationException("Meerdere actuele BTW-percentages met zelfde soort en einddatum leeg."));
		}
		return completeBtw(result.get(0));
	}

	public BtwInfo addBtw(BtwInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		/*
		 * Check overlapping dates
		 */
		Query q = createQuery("Select count(b.id) from Btw b where (:start >= b.ingangsdatum AND "
				+ "((b.einddatum is null) OR (:start <= b.einddatum))) and b.btwtariefsoort = :soort");
		q.setParameter("start", info.getIngangsdatum());
		q.setParameter("soort", info.getBtwtariefsoort().getValue());
		long count = (long) q.getSingleResult();
		if (count > 0)
			throw applicationException(
					new ValidationException("Ingangsdatum btwpercentage overlapt met reeds bestaand btwpercentage"));

		BtwInfo current = getActueelBtwPercentage(info.getBtwtariefsoort());
		if (current != null) {
			if (info.getIngangsdatum().before(current.getIngangsdatum())) {
				throw applicationException(new ValidationException("Er zijn al recentere btwprecentages."));
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(info.getIngangsdatum());
			cal.add(Calendar.DATE, -1);
			current.setEinddatum(cal.getTime());
			this.updateBtw(current);
		}

		Btw percentage = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(percentage);
		return converter.fromEntity(percentage);
	}

	public void deleteBtw(BtwInfo info) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public BtwInfo updateBtw(BtwInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Btw entity = converter.toEntity(info, this.getCurrentuser());
		this.updateEntity(entity);
		return converter.fromEntity(entity);
	}

	public List<BtwInfo> getBtws() throws VerzuimApplicationException {
		Query q = createQuery("select b from Btw b");
		@SuppressWarnings("unchecked")
		List<Btw> result = (List<Btw>) getResultList(q);
		List<BtwInfo> inforesult = new ArrayList<>();
		for (Btw b : result)
			inforesult.add(completeBtw(b));

		return inforesult;
	}

	private WerkzaamhedenInfo completeWerkzaamheden(Werkzaamheden w) {
		return converter.fromEntity(w);
	}

	public void deleteWerkzaamheid(WerkzaamhedenInfo werk) throws ValidationException, VerzuimApplicationException {
		if (isGefactureerd(werk)) {
			throw applicationException(
					new ValidationException("Verwijderen niet toegestaan. Dit is reeds gefactureerd!"));
		}
		this.deleteEntity(converter.toEntity(werk, this.getCurrentuser()));
	}

	public List<WerkzaamhedenInfo> getWerkzaamheden(Integer user, Date startperiode, Date eindperiode)
			throws VerzuimApplicationException {
		StringBuilder bldr = new StringBuilder();
		bldr.append(" where w.datum >= :startperiode and w.datum <= :eindperiode");
		if (user != null) {
			bldr.append(" and w.userid = :id ");
		}
		Query q = createQuery("select w from Werkzaamheden w " + bldr.toString());
		q.setParameter("startperiode", startperiode);
		q.setParameter("eindperiode", eindperiode);
		if (user != null) {
			q.setParameter("id", user);
		}

		@SuppressWarnings("unchecked")
		List<Werkzaamheden> result = (List<Werkzaamheden>) getResultList(q);
		List<WerkzaamhedenInfo> inforesult = new ArrayList<>();
		for (Werkzaamheden b : result)
			inforesult.add(completeWerkzaamheden(b));

		return inforesult;
	}

	public List<WerkzaamhedenInfo> getWerkzaamhedenWerkgever(Integer gebruiker, Date startperiode, Date eindperiode,
			Integer werkgever) throws VerzuimApplicationException {
		StringBuilder bldr = new StringBuilder();
		bldr.append(" where w.datum >= :startperiode and w.datum <= :eindperiode");
		if (gebruiker != null) {
			bldr.append(" and w.userid = :id ");
		}
		bldr.append(" and w.werkgeverid = :werkgever");
		Query q = createQuery("select w from Werkzaamheden w " + bldr.toString());
		q.setParameter("startperiode", startperiode);
		q.setParameter("eindperiode", eindperiode);
		q.setParameter("werkgever", werkgever);
		if (gebruiker != null) {
			q.setParameter("id", gebruiker);
		}

		@SuppressWarnings("unchecked")
		List<Werkzaamheden> result = (List<Werkzaamheden>) getResultList(q);
		List<WerkzaamhedenInfo> inforesult = new ArrayList<>();
		for (Werkzaamheden b : result)
			inforesult.add(completeWerkzaamheden(b));

		return inforesult;
	}

	public List<WerkzaamhedenInfo> getWerkzaamhedenHolding(Integer gebruiker, Date startperiode, Date eindperiode,
			Integer holding) throws VerzuimApplicationException {
		StringBuilder bldr = new StringBuilder();
		bldr.append(" where w.datum >= :startperiode and w.datum <= :eindperiode");
		if (gebruiker != null) {
			bldr.append(" and w.userid = :id ");
		}
		bldr.append(" and w.werkgeverid = wg.id and wg.holding_ID = :holding");
		Query q = createQuery("select w from Werkzaamheden w, Werkgever wg " + bldr.toString());
		q.setParameter("startperiode", startperiode);
		q.setParameter("eindperiode", eindperiode);
		q.setParameter("holding", holding);
		if (gebruiker != null) {
			q.setParameter("id", gebruiker);
		}

		@SuppressWarnings("unchecked")
		List<Werkzaamheden> result = (List<Werkzaamheden>) getResultList(q);
		List<WerkzaamhedenInfo> inforesult = new ArrayList<>();
		for (Werkzaamheden b : result)
			inforesult.add(completeWerkzaamheden(b));

		return inforesult;
	}

	public WerkzaamhedenInfo addWerkzaamheid(WerkzaamhedenInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Werkzaamheden wz = converter.toEntity(info, this.getCurrentuser()); 
		this.insertEntity(wz);
		return converter.fromEntity(wz);
	}

	public WerkzaamhedenInfo updateWerkzaamheid(WerkzaamhedenInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		if (isGefactureerd(info)) {
			throw new ValidationException("Wijzigen niet toegestaan. Dit is reeds gefactureerd!");
		}
		Werkzaamheden wz = converter.toEntity(info, this.getCurrentuser()); 
		this.updateEntity(wz);
		return converter.fromEntity(wz);
	}

	private FactuurcategorieInfo completeFactuurcategorie(Factuurcategorie b) {
		return converter.fromEntity(b);
	}

	public List<FactuurcategorieInfo> getFactuurcategorien() throws VerzuimApplicationException {
		Query q = createQuery("select fc from Factuurcategorie fc");
		@SuppressWarnings("unchecked")
		List<Factuurcategorie> result = (List<Factuurcategorie>) getResultList(q);
		List<FactuurcategorieInfo> inforesult = new ArrayList<>();
		for (Factuurcategorie b : result)
			inforesult.add(completeFactuurcategorie(b));

		return inforesult;
	}

	public FactuurcategorieInfo addFactuurcategorie(FactuurcategorieInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuurcategorie fc = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(fc);
		return converter.fromEntity(fc);
	}

	public FactuurcategorieInfo updateFactuurcategorie(FactuurcategorieInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuurcategorie entity = converter.toEntity(info, this.getCurrentuser());
		this.updateEntity(entity);
		return converter.fromEntity(entity);
	}

	public void deleteFactuurcategorie(FactuurcategorieInfo werk)
			throws ValidationException, VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(werk, this.getCurrentuser()));
	}

	private FactuurkopInfo completeFactuurkop(Factuurkop b) {
		return converter.fromEntity(b);
	}

	public List<FactuurkopInfo> getFactuurkoppen() throws VerzuimApplicationException {
		Query q = createQuery("select fc from Factuurkop fc");
		@SuppressWarnings("unchecked")
		List<Factuurkop> result = (List<Factuurkop>) getResultList(q);
		List<FactuurkopInfo> inforesult = new ArrayList<>();
		for (Factuurkop b : result)
			inforesult.add(completeFactuurkop(b));

		return inforesult;
	}

	public FactuurkopInfo addFactuurkop(FactuurkopInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuurkop fk = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(fk);
		return converter.fromEntity(fk);
	}

	public FactuurkopInfo updateFactuurkop(FactuurkopInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuurkop entity = converter.toEntity(info, this.getCurrentuser());
		this.updateEntity(entity);
		return converter.fromEntity(entity);
	}

	public void deleteFactuurkop(FactuurkopInfo werk) throws ValidationException, VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(werk, this.getCurrentuser()));
	}

	private FactuuritemInfo completeFactuuritem(Factuuritem b) {
		return converter.fromEntity(b);
	}

	private FactuurbetalingInfo completeFactuurbetaling(Factuurbetaling b) {
		return converter.fromEntity(b);
	}

	public List<FactuuritemInfo> getFactuuritems(Date startperiode, Date eindperiode)
			throws VerzuimApplicationException {
		StringBuilder bldr = new StringBuilder();
		bldr.append(" where fi.datum >= :startperiode and fi.datum <= :eindperiode");
		Query q = createQuery("select fi from Factuuritem fi " + bldr.toString());
		q.setParameter("startperiode", startperiode);
		q.setParameter("eindperiode", eindperiode);

		@SuppressWarnings("unchecked")
		List<Factuuritem> result = (List<Factuuritem>) getResultList(q);
		List<FactuuritemInfo> inforesult = new ArrayList<>();
		for (Factuuritem b : result)
			inforesult.add(completeFactuuritem(b));

		return inforesult;

	}

	public FactuuritemInfo addFactuuritem(FactuuritemInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuuritem fi = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(fi);
		return converter.fromEntity(fi);
	}

	public FactuuritemInfo updateFactuuritem(FactuuritemInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		if (isGefactureerd(info)) {
			throw new ValidationException("Wijzigen niet toegestaan. Dit is reeds gefactureerd!");
		}
		Factuuritem entity = converter.toEntity(info, this.getCurrentuser()); 
		this.updateEntity(entity);
		return converter.fromEntity(entity);
	}

	public void deleteFactuuritem(FactuuritemInfo werk) throws ValidationException, VerzuimApplicationException {

		if (isGefactureerd(werk)) {
			throw new ValidationException("Verwijderen niet toegestaan. Dit is reeds gefactureerd!");
		}
		this.deleteEntity(converter.toEntity(werk, this.getCurrentuser()));
	}

	private boolean isGefactureerd(FactuuritemInfo werk) throws VerzuimApplicationException {
		Query q = createQuery("select count(fi) from Factuurregelitem fi, Factuur f "
				+ "where f.id = fi.factuurid and fi.factuuritemid = :id and f.factuurstatus <> :status");
		q.setParameter("id", werk.getId());
		q.setParameter("status", __factuurstatus.VERWIJDERD.getValue());
		Long result = (Long) q.getSingleResult();
		return !(result == null || result == 0);
	}

	private boolean isGefactureerd(WerkzaamhedenInfo werk) throws VerzuimApplicationException {
		Query q;
		if (werk.getSoortwerkzaamheden() == __werkzaamhedensoort.SECRETARIAAT) {
			q = createQuery("select count(fs) from Factuurregelsecretariaat fs, Factuur f "
					+ "where f.id = fs.factuurid and fs.werkzaamhedenid = :id and f.factuurstatus <> :status");
		} else {
			q = createQuery("select count(fb) from Factuurregelbezoek fb , Factuur f "
					+ "where f.id = fb.factuurid and fb.werkzaamhedenid = :id and f.factuurstatus <> :status");
		}
		q.setParameter("id", werk.getId());
		q.setParameter("status", __factuurstatus.VERWIJDERD.getValue());
		Long result = (Long) q.getSingleResult();
		return !(result == null || result == 0);
	}

	private Integer getNewFactuurnr(int jaar, int maand, Integer holdingid) throws VerzuimApplicationException, ValidationException {
		Query q;
		if (maand < 1 || maand > 12){
			throw new ValidationException("Parameter maand bevat ongeldige waarde: " + maand);
		}
		
		if (holdingid != null) {
			String qstr = "Select max(f.factuurnr) from FACTUUR f "
					+ "JOIN WERKGEVER w on w.id = f.werkgeverid "
					+ "JOIN HOLDING   h on h.id = w.holding_id AND " + "h.Factuurtype in ("
					+ __factuurtype.GEAGGREGEERD.getValue() + "," + __factuurtype.GESPECIFICEERD.getValue() + ")"
					+ " WHERE h.id = ?1 AND jaar = ?2 AND maand = ?3";
			q = em.createNativeQuery(qstr);
			q.setParameter(1, holdingid);
			q.setParameter(2, jaar);
			q.setParameter(3, maand);
			Integer result = (Integer) q.getSingleResult();
			if (result != null) {
				return result;
			}
		}
		q = createQuery("Select max(f.factuurnr) from Factuur f");
		Integer result = (Integer) q.getSingleResult();
		if (result == null) {
			/*
			 * Geen enkele factuur in database
			 */
			return (jaar * 10000) + 1;
		} else {
			if ((result / 10000) < jaar) {
				/*
				 * Nog geen facturen in dit jaar
				 */
				return (jaar * 10000) + 1;
			} else {
				/*
				 * tel er 1 bij op
				 */
				return result + 1;
			}
		}
	}

	private BtwInfo lookupBtw(Date date, __btwtariefsoort soort, List<BtwInfo> btws) throws ValidationException {
		for (BtwInfo btw : btws) {
			if (btw.getBtwtariefsoort() == soort && isActiveBtw(btw, date)){
				return btw;
			}
		}
		throw applicationException(new ValidationException("Geen BTW tarief gevonden voor datum: " + date.toString()));
	}

	private boolean isActiveBtw(BtwInfo btw, Date date) {
		if (btw.getEinddatum() == null) {
			if (btw.getIngangsdatum().before(date)) {
				return true;
			}
		} else {
			if (date.before(btw.getIngangsdatum())) {
				/* skip */
			} else {
				if (date.after(btw.getEinddatum())) {
					/* skip */
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private FactuurcategorieInfo lookupFactuurcategorie(Integer categorie, List<FactuurcategorieInfo> categorien) {
		for (FactuurcategorieInfo fci : categorien) {
			if (fci.getId().equals(categorie))
				return fci;
		}
		return null;
	}
	private FactuurregelbezoekInfo processHuisbezoek(FactuurInfo factuur, TariefInfo tarief, WerkzaamhedenInfo wzi, WerkgeverInfo wgr) throws ValidationException{
		FactuurregelbezoekInfo factuurbezoek = new FactuurregelbezoekInfo();
		Calendar cal = Calendar.getInstance();
		factuurbezoek.setFactuurid(factuur.getId());
		factuurbezoek.setCasemanagementkosten(BigDecimal.ZERO);
		factuurbezoek.setKilometerkosten(BigDecimal.ZERO);
		factuurbezoek.setKilometertarief(BigDecimal.ZERO);
		factuurbezoek.setOverigekosten(BigDecimal.ZERO);
		factuurbezoek.setUurkosten(BigDecimal.ZERO);
		factuurbezoek.setUurtarief(BigDecimal.ZERO);
		factuurbezoek.setVastekosten(BigDecimal.ZERO);
		factuurbezoek.setWerkzaamhedenid(wzi.getId());
		if (tarief.getVasttariefhuisbezoeken()) {
			switch (wzi.getUrgentie()) {
			case HUISBEZOEKZATERDAG:
				factuurbezoek.setVastekosten(tarief.getHuisbezoekZaterdagTarief());
				break;
			case ZELFDEDAG:
				factuurbezoek.setVastekosten(tarief.getHuisbezoekTarief());
				break;
			case SPOEDBEZOEK:
				factuurbezoek.setVastekosten(tarief.getSpoedbezoekTarief());
				break;
			case STANDAARD:
				factuurbezoek.setVastekosten(tarief.getStandaardHuisbezoekTarief());
				break;
			case SPOEDBEZOEKZELFDEDAG:
				factuurbezoek.setVastekosten(tarief.getSpoedbezoekZelfdedagTarief());
				break;
			case TELEFONISCHECONTROLE:
				factuurbezoek.setVastekosten(tarief.getTelefonischeControleTarief());
				break;
			default:
				throw applicationException(new ValidationException("Onbekend soort huisbezoek bij: "
						+ wgr.getNaam() + "(" + wzi.getWerkgeverid() + ") op " + wzi.getDatum().toString()));
			}
		} else {
			cal.setTime(wzi.getDatum());
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				factuurbezoek.setUurtarief(tarief.getUurtariefWeekend());
			} else {
				factuurbezoek.setUurtarief(tarief.getUurtariefNormaal());
			}
			factuurbezoek.setUurkosten(factuurbezoek.getUurtarief().multiply(wzi.getUren()));
			factuurbezoek.setOverigekosten(wzi.getOverigekosten());
			factuurbezoek.setKilometertarief(tarief.getKmTarief());
			factuurbezoek.setKilometerkosten(factuurbezoek.getKilometertarief().multiply(wzi.getAantalkm()));
		}
		return factuurbezoek;
	}
	private FactuurregelsecretariaatInfo processSecretariaat(FactuurInfo factuur, TariefInfo tarief, WerkzaamhedenInfo wzi){
		FactuurregelsecretariaatInfo factuursecretariaat = new FactuurregelsecretariaatInfo();
		Calendar cal = Calendar.getInstance();
		factuursecretariaat.setFactuurid(factuur.getId());
		factuursecretariaat.setOverigekosten(BigDecimal.ZERO);
		factuursecretariaat.setUurtarief(tarief.getSecretariaatskosten());
		factuursecretariaat.setSecretariaatskosten(factuursecretariaat.getUurtarief().multiply(wzi.getUren()));
		cal.setTime(wzi.getDatum());
		factuursecretariaat.setWeeknummer(cal.get(Calendar.WEEK_OF_YEAR));
		factuursecretariaat.setWerkzaamhedenid(wzi.getId());
		return factuursecretariaat;
	}
	private FactuurregelbezoekInfo processCasemanagement(FactuurInfo factuur, TariefInfo tarief, WerkzaamhedenInfo wzi){
		FactuurregelbezoekInfo factuurcasemanagement = new FactuurregelbezoekInfo();
		factuurcasemanagement.setFactuurid(factuur.getId());
		factuurcasemanagement.setCasemanagementkosten(BigDecimal.ZERO);
		factuurcasemanagement.setKilometerkosten(BigDecimal.ZERO);
		factuurcasemanagement.setOverigekosten(BigDecimal.ZERO);
		factuurcasemanagement.setUurkosten(BigDecimal.ZERO);
		factuurcasemanagement.setVastekosten(BigDecimal.ZERO);
		factuurcasemanagement.setWerkzaamhedenid(wzi.getId());

		factuurcasemanagement.setUurtarief(tarief.getSociaalbezoekTarief());
		factuurcasemanagement
				.setCasemanagementkosten(factuurcasemanagement.getUurtarief().multiply(wzi.getUren()));
		factuurcasemanagement.setOverigekosten(wzi.getOverigekosten());
		factuurcasemanagement.setKilometertarief(tarief.getKmTarief());
		factuurcasemanagement
				.setKilometerkosten(factuurcasemanagement.getKilometertarief().multiply(wzi.getAantalkm()));
		return factuurcasemanagement;

	}
	private void insertFactuur(FactuurInfo factuur) throws VerzuimApplicationException, ValidationException {
		WerkgeverInfo wgr;
		List<BtwInfo> btws = getBtws();
		List<FactuurcategorieInfo> categorien = getFactuurcategorien();
		/*
		 * We gebruiken het BTW-tarief dat op de laatste dag van de maand nog
		 * geldig is. Daarbij nemen we aan dat BTW tarief wijzigingen nooit
		 * midden in een maand vallen.
		 */
		Date laatstedagmaand;
		Date eerstedagmaand;
		Calendar cal = Calendar.getInstance();
		cal.set(factuur.getJaar(), factuur.getMaand() - 1, 1);
		eerstedagmaand = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		laatstedagmaand = cal.getTime();
		BtwInfo btwhoog = lookupBtw(laatstedagmaand, __btwtariefsoort.HOOG, btws);
		BtwInfo btwlaag = lookupBtw(laatstedagmaand, __btwtariefsoort.LAAG, btws);
		/*
		 * Voor de tarieven gaan we uit van de eerste dag van de maand
		 */

		wgr = werkgeverEJB.getById(factuur.getWerkgeverid());
		cal.setTime(eerstedagmaand);
		TariefInfo tarief = tariefEJB.getTariefByWerkgever(factuur.getWerkgeverid(), cal.getTime());
		if (tarief == null) {
			tarief = tariefEJB.getTariefByHolding(factuur.getHoldingid(), cal.getTime());
			if (tarief == null) {
				throw applicationException(new ValidationException(
						"Geen tarief gevonden voor werkgever/holding: " + wgr.getNaam() + "(" + factuur.getWerkgeverid()
								+ "), datum: " + factuur.getJaar() + "-" + factuur.getMaand()));
			}
		}

		BigDecimal abonnementskosten = BigDecimal.ZERO;
		BigDecimal aansluitkosten = BigDecimal.ZERO;
		Date peildatumaansluitkosten = new Date();
		int aantalwerknemers = 0;

		List<WerkzaamhedenInfo> werkzaamheden = getWerkzaamhedenForFactuur(factuur.getWerkgeverid(), laatstedagmaand);
		List<FactuuritemInfo> items = getFactuuritemsForFacturen(factuur.getWerkgeverid(), laatstedagmaand);

		if (tarief.getAansluitkosten().compareTo(java.math.BigDecimal.ZERO) != 0) {
			if (tarief.getAansluitkostenPeriode() == __tariefperiode.JAAR) {
				cal.setTime(tarief.getDatumAansluitkosten());
				if ((cal.get(Calendar.MONTH) + 1) == factuur.getMaand()) {
					cal.set(Calendar.YEAR, factuur.getJaar()); /* Zet peildatum op dit jaar */
					peildatumaansluitkosten = cal.getTime();
					aantalwerknemers = werkgeverEJB.getAantalWerknemers(factuur.getWerkgeverid(), null,
							peildatumaansluitkosten);
					aansluitkosten = tarief.getAansluitkosten().multiply(new BigDecimal(aantalwerknemers));
				}
			} else {
				if (tarief.getAansluitkostenPeriode() == __tariefperiode.MAAND) {
					if (tarief.getDatumAansluitkosten() == null) {
						peildatumaansluitkosten = eerstedagmaand;
					} else {
						cal.setTime(tarief.getDatumAansluitkosten());
						int monthstoadd = factuur.getMaand() - (cal.get(Calendar.MONTH) + 1);
						int yearstoadd = factuur.getJaar() - cal.get(Calendar.YEAR);
						if (monthstoadd < 0) {
							yearstoadd--;
							monthstoadd = 12 + monthstoadd;
						}
						cal.add(Calendar.YEAR, yearstoadd);
						cal.add(Calendar.MONTH, monthstoadd);
						if (DateOnly.before(peildatumaansluitkosten, wgr.getStartdatumcontract())) {
							peildatumaansluitkosten = wgr.getStartdatumcontract();
						} else {
							peildatumaansluitkosten = cal.getTime();
						}
					}
					aantalwerknemers = werkgeverEJB.getAantalWerknemers(factuur.getWerkgeverid(), null,
							peildatumaansluitkosten);
					aansluitkosten = tarief.getAansluitkosten().multiply(new BigDecimal(aantalwerknemers));
				}
			}
		}
		if (wgr.getFactureren() && (tarief.getAbonnement().compareTo(BigDecimal.ZERO) != 0)) {
			/*
			 * Voor een factureren op werkgever niveau kan altijd het abonnement
			 * in rekening worden gebracht. Voor factureren op holdingniveau,
			 * wordt een aparte factuur voor de holding toegevoegd met alleen
			 * het abonnementstarief.
			 */
			if (tarief.getAbonnementPeriode() == __tariefperiode.JAAR) {
				cal.setTime(tarief.getDatumAansluitkosten());
				if ((cal.get(Calendar.MONTH) + 1) == factuur.getMaand()) {
					abonnementskosten = tarief.getAbonnement();
				}
			} else {
				if (tarief.getAbonnementPeriode() == __tariefperiode.MAAND) {
					abonnementskosten = tarief.getAbonnement();
				}
			}
		}
		factuur.setMaandbedragsecretariaat(tarief.getMaandbedragSecretariaat());
		factuur.setAbonnementskostenperiode(tarief.getAbonnementPeriode());
		factuur.setAbonnementskosten(abonnementskosten);
		factuur.setAansluitkostenperiode(tarief.getAansluitkostenPeriode());
		factuur.setAansluitkosten(aansluitkosten);
		factuur.setAantalmedewerkers(aantalwerknemers);
		factuur.setBtwpercentagehoog(btwhoog.getPercentage());
		factuur.setBtwpercentagelaag(btwlaag.getPercentage());
		factuur.setFactuurstatus(__factuurstatus.AANGEMAAKT);
		factuur.setAanmaakdatum(new Date());
		factuur.setOmschrijvingfactuur("");
		factuur.setPeildatumaansluitkosten(peildatumaansluitkosten);
		if (tarief.getOmschrijvingFactuur() != null) {
			factuur.setOmschrijvingfactuur(tarief.getOmschrijvingFactuur());
		}
		factuur.setTariefid(tarief.getId());
		factuur = addFactuur(factuur);
		for (WerkzaamhedenInfo wzi : werkzaamheden) {
			switch (wzi.getSoortwerkzaamheden()) {
			case HUISBEZOEK:
				addFactuurregelBezoek(processHuisbezoek(factuur, tarief, wzi, wgr));
				break;
			case SECRETARIAAT:
				addFactuuregelSecretariaat(processSecretariaat(factuur, tarief, wzi));
				break;
			case CASEMANAGEMENT:
				addFactuurregelBezoek(processCasemanagement(factuur, tarief, wzi));
				break;
			case DOKTERBEZOEK:
			case OVERWERK:
			case VERLOF:
			case ZIEK:
				break;
			default:
				throw applicationException(new ValidationException("Onbekend soort werkzaamheden bij: " + wgr.getNaam()
						+ "(" + wzi.getWerkgeverid() + ") op " + wzi.getDatum().toString()));
			}
		}
		BigDecimal btwbedrag;
		BigDecimal btwpercentage = new BigDecimal(0);
		for (FactuuritemInfo fii : items) {
			btwbedrag = BigDecimal.ZERO;
			btwbedrag = btwbedrag.setScale(2, RoundingMode.HALF_UP);
			FactuurregelitemInfo fri = new FactuurregelitemInfo();
			FactuurcategorieInfo fci = lookupFactuurcategorie(fii.getFactuurcategorieid(), categorien);
			if (fci == null){
				throw new VerzuimRuntimeException("Kan factuurcategorie niet vinden: " + fii.getFactuurcategorieid());
			}
			switch (fci.getBtwcategorie()) {
			case HOOG:
				btwpercentage = factuur.getBtwpercentagehoog();
				btwbedrag = btwpercentage.multiply(fii.getBedrag());
				break;
			case LAAG:
				btwpercentage = factuur.getBtwpercentagelaag();
				btwbedrag = btwpercentage.multiply(fii.getBedrag());
				break;
			default:
				break;
			}
			btwbedrag = btwbedrag.divide(new BigDecimal(100));
			fri.setBedrag(fii.getBedrag());
			fri.setBtwbedrag(btwbedrag);
			fri.setBtwcategorie(fci.getBtwcategorie());
			fri.setFactuurid(factuur.getId());
			fri.setFactuuritemid(fii.getId());
			fri.setBtwpercentage(btwpercentage);
			addFactuurregelItem(fri);
		}
	}

	private void insertFactuurHolding(FactuurInfo factuur, HoldingInfo hi)
			throws VerzuimApplicationException, ValidationException {
		List<BtwInfo> btws = getBtws();
		/*
		 * We gebruiken het BTW-tarief dat op de laatste dag van de maand nog
		 * geldig is. Daarbij nemen we aan dat BTW tarief wijzigingen nooit
		 * midden in een maand vallen.
		 */
		Date laatstedagmaand;
		Date eerstedagmaand;
		Calendar cal = Calendar.getInstance();
		cal.set(factuur.getJaar(), factuur.getMaand() - 1, 1);
		eerstedagmaand = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		laatstedagmaand = cal.getTime();
		BtwInfo btwhoog = lookupBtw(laatstedagmaand, __btwtariefsoort.HOOG, btws);
		BtwInfo btwlaag = lookupBtw(laatstedagmaand, __btwtariefsoort.LAAG, btws);
		/*
		 * Voor de tarieven gaan we uit van de eerste dag van de maand
		 */

		cal.setTime(eerstedagmaand);
		TariefInfo tarief = tariefEJB.getTariefByHolding(factuur.getHoldingid(), cal.getTime());
		if (tarief == null) {
			throw applicationException(new ValidationException("Geen tarief gevonden voor holding: " + hi.getNaam()
					+ "(" + factuur.getHoldingid() + "), datum: " + factuur.getJaar() + "-" + factuur.getMaand()));
		}

		BigDecimal abonnementskosten = BigDecimal.ZERO;
		Date peildatumaansluitkosten = new Date();

		/*
		 * Voor factureren op holdingniveau, wordt een aparte factuur voor de
		 * holding toegevoegd met alleen het abonnementstarief.
		 */
		if (tarief.getAbonnement().compareTo(BigDecimal.ZERO) != 0) {
			if (tarief.getAbonnementPeriode() == __tariefperiode.JAAR) {
				cal.setTime(tarief.getDatumAansluitkosten());
				if ((cal.get(Calendar.MONTH) + 1) == factuur.getMaand()) {
					abonnementskosten = tarief.getAbonnement();
				}
			} else {
				if (tarief.getAbonnementPeriode() == __tariefperiode.MAAND) {
					abonnementskosten = tarief.getAbonnement();
				}
			}
		}
		factuur.setMaandbedragsecretariaat(BigDecimal.ZERO);
		factuur.setAbonnementskostenperiode(tarief.getAbonnementPeriode());
		factuur.setAbonnementskosten(abonnementskosten);
		factuur.setAansluitkostenperiode(tarief.getAansluitkostenPeriode());
		factuur.setAansluitkosten(BigDecimal.ZERO);
		factuur.setAantalmedewerkers(0);
		factuur.setBtwpercentagehoog(btwhoog.getPercentage());
		factuur.setBtwpercentagelaag(btwlaag.getPercentage());
		factuur.setFactuurstatus(__factuurstatus.AANGEMAAKT);
		factuur.setAanmaakdatum(new Date());
		factuur.setOmschrijvingfactuur("");
		factuur.setPeildatumaansluitkosten(peildatumaansluitkosten);
		if (tarief.getOmschrijvingFactuur() != null) {
			factuur.setOmschrijvingfactuur(tarief.getOmschrijvingFactuur());
		}
		factuur.setTariefid(tarief.getId());
		addFactuur(factuur);
	}

	private List<FactuuritemInfo> getFactuuritemsForFacturen(Integer werkgeverid, Date laatstedagmaand)
			throws VerzuimApplicationException {
		Query qw = createQuery("select fi from Factuuritem fi "
				+ "where fi.werkgeverid = :wgid and fi.datum <= :datum and "
				+ "not exists (select fri.factuuritemid from Factuurregelitem fri where fi.id = fri.factuuritemid)");
		qw.setParameter("wgid", werkgeverid);
		qw.setParameter("datum", laatstedagmaand);

		@SuppressWarnings("unchecked")
		List<Factuuritem> result = (List<Factuuritem>) getResultList(qw);
		List<FactuuritemInfo> inforesult = new ArrayList<>();
		for (Factuuritem b : result)
			inforesult.add(completeFactuuritem(b));

		return inforesult;
	}

	private List<WerkzaamhedenInfo> getWerkzaamhedenForFactuur(Integer werkgeverid, Date laatstedagmaand)
			throws VerzuimApplicationException {
		Query qw = createQuery("select wz from Werkzaamheden wz "
				+ "where wz.werkgeverid = :wgid and wz.datum <= :datum and "
				+ "not exists (select fb.werkzaamhedenid from Factuurregelbezoek fb where wz.id = fb.werkzaamhedenid) and "
				+ "not exists (select fs.werkzaamhedenid from Factuurregelsecretariaat fs where wz.id = fs.werkzaamhedenid)");
		qw.setParameter("wgid", werkgeverid);
		qw.setParameter("datum", laatstedagmaand);

		@SuppressWarnings("unchecked")
		List<Werkzaamheden> result = (List<Werkzaamheden>) getResultList(qw);
		List<WerkzaamhedenInfo> inforesult = new ArrayList<>();
		for (Werkzaamheden b : result)
			inforesult.add(completeWerkzaamheden(b));

		return inforesult;
	}

	private void addFactuuregelSecretariaat(FactuurregelsecretariaatInfo factuursecretariaat)
			throws VerzuimApplicationException {
		this.insertEntity(converter.toEntity(factuursecretariaat, getCurrentuser()));
	}

	private void addFactuurregelBezoek(FactuurregelbezoekInfo factuurbezoek) throws VerzuimApplicationException {
		this.insertEntity(converter.toEntity(factuurbezoek, getCurrentuser()));
	}

	private void addFactuurregelItem(FactuurregelitemInfo factuuritem) throws VerzuimApplicationException {
		this.insertEntity(converter.toEntity(factuuritem, getCurrentuser()));
	}

	private List<FactuurregelitemInfo> getFactuurregelsitem(Integer id) throws VerzuimApplicationException {
		Query q = createQuery(
				"select fi, i, c, k from Factuurregelitem fi, Factuuritem i, Factuurcategorie c, Factuurkop k "
						+ "where fi.factuurid = :id "
						+ " and i.id = fi.factuuritemid and c.id = i.factuurcategorieid and c.factuurkopid = k.id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();
		List<FactuurregelitemInfo> inforesult = new ArrayList<>();
		for (Object[] oa : result) {
			Factuurregelitem fi = (Factuurregelitem) oa[0];
			FactuurregelitemInfo fbi = converter.fromEntity(fi);
			Factuuritem i = (Factuuritem) oa[1];
			Factuurcategorie c = (Factuurcategorie) oa[2];
			Factuurkop k = (Factuurkop) oa[3];
			fbi.setFactuuritem(converter.fromEntity(i));
			fbi.setFactuurcategorie(converter.fromEntity(c));
			fbi.setFactuurkop(converter.fromEntity(k));
			inforesult.add(fbi);
		}
		return inforesult;

	}

	@SuppressWarnings("unchecked")
	private List<FactuurregelbezoekInfo> getFactuurregelsbezoek(Integer id)
			throws VerzuimApplicationException {
		Query q = createQuery(
				"select fb, w, oe from Factuurregelbezoek fb, Werkzaamheden w, Oe oe where fb.factuurid = :id and w.id = fb.werkzaamhedenid and (w.filiaalid = oe.id)");
		q.setParameter("id", id);
		List<Object[]> result = q.getResultList();
		List<FactuurregelbezoekInfo> inforesult = new ArrayList<>();
		for (Object[] oa : result) {
			Factuurregelbezoek fb = (Factuurregelbezoek) oa[0];
			FactuurregelbezoekInfo fbi = converter.fromEntity(fb);
			Werkzaamheden w = (Werkzaamheden) oa[1];
			fbi.setWerkzaamheden(converter.fromEntity(w));
			if (w.getFiliaalid() != null) {
				Oe oe = (Oe) oa[2];
				OeInfo oei = werkgeverconverter.fromEntity(oe);
				fbi.setOe(oei);
			}
			inforesult.add(fbi);
		}

		q = createQuery(
				"select fb, w from Factuurregelbezoek fb, Werkzaamheden w where fb.factuurid = :id and w.id = fb.werkzaamhedenid and (w.filiaalid is null)");
		q.setParameter("id", id);
		result = q.getResultList();
		for (Object[] oa : result) {
			Factuurregelbezoek fb = (Factuurregelbezoek) oa[0];
			FactuurregelbezoekInfo fbi = converter.fromEntity(fb);
			Werkzaamheden w = (Werkzaamheden) oa[1];
			fbi.setWerkzaamheden(converter.fromEntity(w));
			inforesult.add(fbi);
		}
		return inforesult;
	}

	private List<FactuurregelsecretariaatInfo> getFactuurregelssecretariaat(Integer id)
			throws VerzuimApplicationException {
		Query q = createQuery(
				"select fs, w from Factuurregelsecretariaat fs, Werkzaamheden w where fs.factuurid = :id and w.id = fs.werkzaamhedenid");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();
		List<FactuurregelsecretariaatInfo> inforesult = new ArrayList<>();
		for (Object[] oa : result) {
			Factuurregelsecretariaat fs = (Factuurregelsecretariaat) oa[0];
			FactuurregelsecretariaatInfo fsi = converter.fromEntity(fs);
			Werkzaamheden w = (Werkzaamheden) oa[1];
			fsi.setWerkzaamheden(converter.fromEntity(w));
			inforesult.add(fsi);
		}
		return inforesult;
	}

	private List<FactuurbetalingInfo> getFactuurbetalingen(Integer id) throws VerzuimApplicationException {
		Query q = createQuery("select fb from Factuurbetaling fb where fb.factuurid = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Factuurbetaling> result = q.getResultList();
		List<FactuurbetalingInfo> inforesult = new ArrayList<>();
		for (Factuurbetaling fs : result) {
			FactuurbetalingInfo fsi = converter.fromEntity(fs);
			inforesult.add(fsi);
		}
		return inforesult;
	}

	public List<FactuurbetalingInfo> getFactuurbetalingen() throws VerzuimApplicationException {
		Query q = createQuery("select fb from Factuurbetaling fb");
		@SuppressWarnings("unchecked")
		List<Factuurbetaling> result = q.getResultList();
		List<FactuurbetalingInfo> inforesult = new ArrayList<>();
		for (Factuurbetaling fs : result) {
			FactuurbetalingInfo fsi = converter.fromEntity(fs);
			inforesult.add(fsi);
		}
		return inforesult;
	}

	private FactuurInfo completeFactuur(Factuur b, boolean details) throws VerzuimApplicationException {
		FactuurInfo bi = converter.fromEntity(b);
		if (details) {
			bi.setFactuurregelitems(getFactuurregelsitem(b.getId()));
			bi.setFactuurregelbezoeken(getFactuurregelsbezoek(b.getId()));
			bi.setFactuurregelsecretariaat(getFactuurregelssecretariaat(b.getId()));
			bi.setFactuurbetalingen(getFactuurbetalingen(b.getId()));
		}
		return bi;
	}

	private FactuurTotaalInfo completeFactuurTotaal(FactuurTotaal b, boolean details)
			throws VerzuimApplicationException {
		FactuurTotaalInfo bi = converter.fromEntity(b);

		Query qb = em.createQuery(selectFactuurBezoeken);
		qb.setParameter("id", b.getId());
		Object[] result2 = (Object[]) getSingleResult(qb);
		bi.setSomuurkosten((BigDecimal) result2[0]);
		bi.setSomkilometerkosten((BigDecimal) result2[1]);
		bi.setSomvastekosten((BigDecimal) result2[2]);
		bi.setSomoverigekostenbezoek((BigDecimal) result2[3]);
		bi.setSomcasemanagementkosten((BigDecimal) result2[4]);

		Query qs = em.createQuery(selectFactuurSecretariaat);
		qs.setParameter("id", b.getId());
		Object[] result3 = (Object[]) getSingleResult(qs);
		bi.setSomoverigekostensecretariaat((BigDecimal) result3[0]);
		bi.setSomsecretariaatskosten((BigDecimal) result3[1]);

		Query qi = em.createQuery(selectFactuurItems);
		qi.setParameter("id", b.getId());
		Object[] result4 = (Object[]) getSingleResult(qi);
		bi.setSomitembedrag((BigDecimal) result4[0]);
		bi.setSomitembtwbedraglaag((BigDecimal) result4[1]);
		bi.setSomitembtwbedraghoog((BigDecimal) result4[2]);

		Query qp = em.createQuery(selectFactuurBetalingen);
		qp.setParameter("id", b.getId());
		bi.setSombetalingen((BigDecimal) getSingleResult(qp));

		bi.setWerkgever(werkgeverEJB.getById(b.getWerkgeverid()));
		if (details) {
			bi.setFactuurregelitems(getFactuurregelsitem(b.getId()));
			bi.setFactuurregelbezoeken(getFactuurregelsbezoek(b.getId()));
			bi.setFactuurregelsecretariaat(getFactuurregelssecretariaat(b.getId()));
			bi.setFactuurbetalingen(getFactuurbetalingen(b.getId()));
		}
		return bi;
	}

	public FactuurTotaalInfo getFactuurDetails(Integer id) throws VerzuimApplicationException {
		if (__btwtariefsoort.LAAG.getValue() != 1)
			throw new VerzuimRuntimeException("Enumeration of __btwtariefsoort.LAAG does not match values in DB");
		if (__btwtariefsoort.HOOG.getValue() != 2)
			throw new VerzuimRuntimeException("Enumeration of __btwtariefsoort.HOOG does not match values in DB");

		Query q = em.createNativeQuery(selectFactuur + "where (f.id = ?1) and not f.id is null group by f.id",
				FactuurTotaal.class);
		q.setParameter(1, id);
		FactuurTotaal f = (FactuurTotaal) getSingleResult(q);

		FactuurTotaalInfo fti;
		fti = completeFactuurTotaal(f, true);

		return fti;

	}

	private FactuurInfo getFactuur(Integer jaar, Integer maand, Integer werkgeverid)
			throws VerzuimApplicationException, ValidationException {
		Query q = createQuery(
				"select f from Factuur f where f.werkgeverid = :werkgever and f.maand = :maand and f.jaar = :jaar");
		q.setParameter("werkgever", werkgeverid);
		q.setParameter("jaar", jaar);
		q.setParameter("maand", maand);

		@SuppressWarnings("unchecked")
		List<Factuur> result = (List<Factuur>) getResultList(q);
		if (result.size() > 1) {
			throw applicationException(
					new ValidationException("Expected 0 or 1 rows, but received: " + result.size() + " rows"));
		}
		if (result.size() == 1)
			return completeFactuur((Factuur) result.get(0), false);
		else
			return null;

	}

	private FactuurInfo getFactuurById(Integer factuurid)
			throws VerzuimApplicationException, ValidationException {
		Query q = createQuery(
				"select f from Factuur f where f.id = :factuurid");
		q.setParameter("factuurid", factuurid);

		@SuppressWarnings("unchecked")
		List<Factuur> result = (List<Factuur>) getResultList(q);
		if (result.size() > 1) {
			throw applicationException(
					new ValidationException("Expected 0 or 1 rows, but received: " + result.size() + " rows"));
		}
		if (result.size() == 1)
			return completeFactuur((Factuur) result.get(0), false);
		else
			return null;

	}
	private FactuurInfo getFactuurHolding(Integer jaar, Integer maand, Integer holdingid)
			throws VerzuimApplicationException, ValidationException {
		Query q = createQuery(
				"select f from Factuur f where f.holdingid = :holding and f.maand = :maand and f.jaar = :jaar");
		q.setParameter("holding", holdingid);
		q.setParameter("jaar", jaar);
		q.setParameter("maand", maand);

		@SuppressWarnings("unchecked")
		List<Factuur> result = (List<Factuur>) getResultList(q);

		if (result.isEmpty())
			return null;
		else
			return completeFactuur((Factuur) result.get(0), false);

	}

	public Integer getAantalontbrekendeFacturen(Integer jaar, Integer maand)
			throws ValidationException, VerzuimApplicationException {
		if (jaar == null || maand == null) {
			throw applicationException(new ValidationException("Maand en jaar moeten beide ingevuld zijn."));
		}
		Date eerstedagmaand;
		Date laatstedagmaand;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, jaar);
		cal.set(Calendar.MONTH, maand - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		eerstedagmaand = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		laatstedagmaand = cal.getTime();

		Query qw = createQuery("select count(wg.id) from Werkgever wg "
				+ "where wg.startdatumcontract <= :laatstedagmaand and (wg.einddatumcontract is null or wg.einddatumcontract >= :eerstedagmaand) "
				+ "and wg.factureren = 1");

		qw.setParameter("eerstedagmaand", eerstedagmaand);
		qw.setParameter("laatstedagmaand", laatstedagmaand);

		Long aantwerkgevers = (Long) qw.getSingleResult();

		Query qh = createQuery("select count(wg.id) from Werkgever wg, Holding h "
				+ "where h.id = wg.holding_ID and h.factureren = 1 and "
				+ "wg.startdatumcontract <= :laatstedagmaand and (wg.einddatumcontract is null or wg.einddatumcontract >= :eerstedagmaand) "
				+ "and wg.factureren = 0");

		qh.setParameter("eerstedagmaand", eerstedagmaand);
		qh.setParameter("laatstedagmaand", laatstedagmaand);

		Long aantwerkgeversonderholding = (Long) qh.getSingleResult();

		Query qf = createQuery("select count(f.id) from Factuur f, Werkgever wg "
				+ "where f.jaar = :jaar and f.maand = :maand and wg.id = f.werkgeverid "
				+ " and wg.startdatumcontract <= :laatstedagmaand and (wg.einddatumcontract is null or wg.einddatumcontract >= :eerstedagmaand) "
				+ " and f.factuurstatus <> " + __factuurstatus.VERWIJDERD.getValue());

		qf.setParameter("jaar", jaar);
		qf.setParameter("maand", maand);
		qf.setParameter("eerstedagmaand", eerstedagmaand);
		qf.setParameter("laatstedagmaand", laatstedagmaand);

		Long aantfacturen = (Long) qf.getSingleResult();
		return aantwerkgeversonderholding.intValue() + aantwerkgevers.intValue() - aantfacturen.intValue();
	}

	private boolean moetFactureren(int jaar, int maand, Date startdatum, Date einddatum) {

		Calendar cal = Calendar.getInstance();
		Date laatsteDagVanMaand;
		Date eersteDagVanMaand;
		cal.set(jaar, maand - 1, 1);
		eersteDagVanMaand = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		laatsteDagVanMaand = cal.getTime();

		if (!DateOnly.after(startdatum, laatsteDagVanMaand)) {
			if (einddatum == null) {
				return true;
			} else {
				return !DateOnly.before(einddatum, eersteDagVanMaand);
			}
		} else {
			return false;
		}
	}

	public void afsluitenMaand(Integer jaar, Integer maand) throws VerzuimApplicationException, ValidationException {

		if (jaar == null || maand == null) {
			throw applicationException(new ValidationException("Maand en jaar moeten beide ingevuld zijn."));
		}

		List<WerkgeverInfo> werkgevers = werkgeverEJB.getAll();
		List<HoldingInfo> holdings = werkgeverEJB.getHoldings();

		for (HoldingInfo hi : holdings) {
			processHolding(hi, jaar, maand);
		}

		for (WerkgeverInfo wgi : werkgevers) {
			processWerkgever(wgi, jaar, maand);
		}

	}

	private void processWerkgever(WerkgeverInfo wgi, Integer jaar, Integer maand) throws VerzuimApplicationException, ValidationException {
		Integer factuurnr;
		boolean factureren;

		factuurnr = null;
		if (wgi.getHoldingId() != null)
			wgi.setHolding(werkgeverEJB.getHoldingById(wgi.getHoldingId()));

		factureren = moetFactureren(jaar, maand, wgi.getStartdatumcontract(), wgi.getEinddatumcontract());
		if (factureren && (wgi.getFactureren() || (wgi.getHolding() != null && wgi.getHolding().isFactureren()))) {
			if (wgi.getHolding() != null && 
				wgi.getHolding().isFactureren() && 
				wgi.getHolding().getFactuurtype().equals(__factuurtype.NVT)) {
					throw new ValidationException(
							"Factuurtype niet gekozen bij Holding: " + wgi.getHolding().getNaam());
			}
			if (wgi.getFactureren() && (wgi.getHolding() != null && wgi.getHolding().isFactureren())){
				throw new ValidationException("Factureren op werkgeverniveau en holdingniveau niet toegestaan ("
						+ wgi.getNaam() + ")");
			}

			FactuurInfo factuur = getFactuur(jaar, maand, wgi.getId());
			if (factuur != null) { // Factuur is al eerder aangemaakt
				if (factuur.getFactuurstatus() == __factuurstatus.VERWIJDERD) {
					factuurnr = factuur.getFactuurnr();
					deleteFactuur(factuur);
					factuur = null;
				} else {
					log.info("Factuur bestaat al: werkgever = " + wgi.getNaam() + " voor periode: " + jaar + "-"
							+ maand);
					return;
				}
			}
			if (factuur == null) {
				factuur = new FactuurInfo();
				if (factuurnr == null) {
					factuurnr = getNewFactuurnr(jaar, maand, wgi.getHoldingId());
				}
				factuur.setFactuurnr(factuurnr);
				factuur.setJaar(jaar);
				factuur.setMaand(maand);
				factuur.setWerkgeverid(wgi.getId());
				factuur.setHoldingid(wgi.getHoldingId());
				insertFactuur(factuur);
			}
		}
	}

	private void processHolding(HoldingInfo hi, Integer jaar, Integer maand) throws ValidationException, VerzuimApplicationException {
		Integer factuurnr;
		boolean factureren;
		factuurnr = null;
		if (hi.isFactureren() && (hi.getFactuurtype() == __factuurtype.GESPECIFICEERD
				|| hi.getFactuurtype() == __factuurtype.GEAGGREGEERD)) {
			factureren = moetFactureren(jaar, maand, hi.getStartdatumcontract(), hi.getEinddatumcontract());
			FactuurInfo factuur = getFactuurHolding(jaar, maand, hi.getId());
			if (factuur != null) { // Factuur is al eerder aangemaakt
				if (factuur.getFactuurstatus() == __factuurstatus.VERWIJDERD) {
					factuurnr = factuur.getFactuurnr();
					deleteFactuur(factuur);
					factuur = null;
				} else {
					log.info("Factuur bestaat al: holding = " + hi.getNaam() + " voor periode: " + jaar + "-"
							+ maand);
				}
			}
			if (factuur == null && factureren) {
				factuur = new FactuurInfo();
				if (factuurnr == null) {
					factuurnr = getNewFactuurnr(jaar, maand, hi.getId());
				}
				factuur.setFactuurnr(factuurnr);
				factuur.setJaar(jaar);
				factuur.setMaand(maand);
				factuur.setWerkgeverid(-1);
				factuur.setHoldingid(hi.getId());
				insertFactuurHolding(factuur, hi);
			}
		}
	}

	public void terugdraaienMaand(Integer jaar, Integer maand) throws VerzuimApplicationException, ValidationException {
		Calendar cal = Calendar.getInstance();
		Date laatsteDagVanMaand;
		Date eersteDagVanMaand;

		cal.set(jaar, maand - 1, 1);
		eersteDagVanMaand = cal.getTime();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		laatsteDagVanMaand = cal.getTime();

		List<FactuurTotaalInfo> facturen = this.getFacturenInPeriode(eersteDagVanMaand, laatsteDagVanMaand, false);
		
		for (FactuurTotaalInfo fti : facturen) {
			FactuurInfo factuur;
			if (fti.getId().equals(-1)){
				List<FactuurTotaalInfo> holdingfacturen = getFacturenInPeriodeByHolding(cal.getTime(), cal.getTime(),
						fti.getHoldingid(), false);
				for (FactuurTotaalInfo hfti : holdingfacturen) {
					this.deleteFactuur(hfti);
				}
			}else{
				factuur = getFactuurById(fti.getId());
				this.deleteFactuur(factuur);
			}
		}
	}

	public FactuurTotaalInfo updateFactuur(FactuurInfo info) throws VerzuimApplicationException, ValidationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		if (info.getFactuurstatus().equals(__factuurstatus.VERWIJDERD) && info.getId().equals(-1)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, info.getJaar());
			cal.set(Calendar.MONTH, info.getMaand() - 1);
			List<FactuurTotaalInfo> facturen = getFacturenInPeriodeByHolding(cal.getTime(), cal.getTime(),
					info.getHoldingid(), false);
			for (FactuurTotaalInfo fti : facturen) {
				fti.setFactuurstatus(info.getFactuurstatus());
				this.updateEntity(converter.toEntity(fti, this.getCurrentuser()));
			}
		} else {
			
			this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
		}
		return this.getFactuurDetails(info.getId());
	}

	private FactuurInfo addFactuur(FactuurInfo info) throws VerzuimApplicationException, ValidationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuur factuur = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(factuur);
		return converter.fromEntity(factuur);
	}

	public void deleteFactuur(FactuurInfo info) throws ValidationException, VerzuimApplicationException {

		Query qfrb = createQuery("Delete from Factuurregelbezoek fb where fb.factuurid = :factuurnr");
		Query qfrs = createQuery("Delete from Factuurregelsecretariaat fs where fs.factuurid = :factuurnr");
		Query qfri = createQuery("Delete from Factuurregelitem fi where fi.factuurid = :factuurnr");
		Query qffb = createQuery("Delete from Factuurbetaling fb where fb.factuurid = :factuurnr");

		qfrb.setParameter("factuurnr", info.getId());
		qfrs.setParameter("factuurnr", info.getId());
		qfri.setParameter("factuurnr", info.getId());
		qffb.setParameter("factuurnr", info.getId());

		executeUpdate(qfrb);
		executeUpdate(qfrs);
		executeUpdate(qfri);
		executeUpdate(qffb);

		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	private int getNumberofMonths(Date start, Date end) {
		Calendar calstart = Calendar.getInstance();
		Calendar calend = Calendar.getInstance();
		int diffyears;
		int diffmonths;
		calstart.setTime(start);
		calend.setTime(end);

		diffyears = calend.get(Calendar.YEAR) - calstart.get(Calendar.YEAR);
		diffmonths = diffyears * 12 + calend.get(Calendar.MONTH) - calstart.get(Calendar.MONTH);
		return diffmonths;
	}

	public List<FactuurTotaalInfo> getFacturenHoldingInPeriode(int jaar, int maand) throws VerzuimApplicationException {
		/**
		 * Parameters in query:
		 * 
		 * ?1 = firstyear ?2 = firstmonth ?3 = lastyear ?4 = lastmonth
		 */

		Query q = em.createNativeQuery(selectFactuur + "where f.maand = ?2 and f.jaar = ?1 and f.werkgeverid = -1 "
				+ " and not f.id is null group by f.id", FactuurTotaal.class);
		q.setParameter(1, jaar);
		q.setParameter(2, maand);

		@SuppressWarnings("unchecked")
		List<FactuurTotaal> result = (List<FactuurTotaal>) getResultList(q);
		List<FactuurTotaalInfo> inforesult = new ArrayList<>();
		for (FactuurTotaal f : result) {
			FactuurTotaalInfo fti;
			fti = completeFactuurTotaal(f, false);

			inforesult.add(fti);
		}
		return inforesult;
	}

	public List<FactuurTotaalInfo> getFacturenWerkgeverInPeriode(int jaar, int maand, boolean details)
			throws VerzuimApplicationException {
		/**
		 * Parameters in query:
		 * 
		 * ?1 = firstyear ?2 = firstmonth ?3 = lastyear ?4 = lastmonth
		 */

		Query q = em.createNativeQuery(selectFactuur + "where f.maand = ?2 and f.jaar = ?1 and not exists "
				+ "(select fi.id from FACTUUR fi where f.id = fi.id and fi.maand = ?2 and fi.jaar = ?1"
				+ "    and fi.holdingid in (SELECT distinct holdingid FROM FACTUUR fi2 where fi2.werkgeverid = -1 and jaar = ?1 and maand = ?2))"
				+ " and not f.id is null group by f.id", FactuurTotaal.class);
		q.setParameter(1, jaar);
		q.setParameter(2, maand);

		@SuppressWarnings("unchecked")
		List<FactuurTotaal> result = (List<FactuurTotaal>) getResultList(q);
		List<FactuurTotaalInfo> inforesult = new ArrayList<>();
		for (FactuurTotaal f : result) {
			FactuurTotaalInfo fti;
			fti = completeFactuurTotaal(f, details);

			inforesult.add(fti);
		}
		return inforesult;
	}

	public List<FactuurTotaalInfo> getFacturenInPeriode(Date firstmonth, Date lastmonth, boolean details)
			throws VerzuimApplicationException {
		List<FactuurTotaalInfo> somfacturen = new ArrayList<>();
		List<FactuurTotaalInfo> holdingfacturen;
		FactuurTotaalInfo somfactuur;

		List<HoldingInfo> holdings = werkgeverEJB.getHoldings();

		int nrofmonths = getNumberofMonths(firstmonth, lastmonth);
		Calendar factuurdate = Calendar.getInstance();
		factuurdate.setTime(firstmonth);
		for (int i = 0; i <= nrofmonths; i++) {
			List<FactuurTotaalInfo> facturen = getFacturenHoldingInPeriode(factuurdate.get(Calendar.YEAR),
					factuurdate.get(Calendar.MONTH) + 1);
			for (FactuurTotaalInfo fti : facturen) {
				holdingfacturen = getFacturenInPeriodeByHolding(factuurdate.getTime(), factuurdate.getTime(),
						fti.getHoldingid(), details);
				somfactuur = FactuurTotaalInfo.sommeer(holdingfacturen);
				setWerkgevernaamToHoldingNaam(holdings, somfactuur, fti);
				somfacturen.add(somfactuur);
			}
			facturen = getFacturenWerkgeverInPeriode(factuurdate.get(Calendar.YEAR), factuurdate.get(Calendar.MONTH)+1,
					details);
			for (FactuurTotaalInfo fti : facturen) {
				somfacturen.add(fti);
			}
			factuurdate.add(Calendar.MONTH, 1);
		}
		return somfacturen;
	}

	private void setWerkgevernaamToHoldingNaam(List<HoldingInfo> holdings, FactuurTotaalInfo somfactuur,
			FactuurTotaalInfo fti) {
		for (HoldingInfo hi : holdings) {
			if (hi.getId().equals(fti.getHoldingid())) {
				somfactuur.getWerkgever().setNaam(hi.getNaam());
				break;
			}
		}
	}

	public List<FactuurTotaalInfo> getFacturenInPeriodeByHolding(Date firstmonth, Date lastmonth, Integer holdingid,
			boolean details) throws VerzuimApplicationException {
		/**
		 * Parameters in query:
		 * 
		 * ?1 = firstyear ?2 = firstmonth ?3 = lastyear ?4 = lastmonth
		 */

		Query q = em.createNativeQuery(
				selectFactuur + "where (f.maand >= ?2 and f.jaar = ?1 or f.jaar > ?1) "
						+ "and (f.maand <= ?4  and f.jaar = ?3  or f.jaar < ?3) and f.holdingid = ?5 and not f.id is null group by f.id",
				FactuurTotaal.class);
		Calendar first = Calendar.getInstance();
		first.setTime(firstmonth);
		Calendar last = Calendar.getInstance();
		last.setTime(lastmonth);
		q.setParameter(1, first.get(Calendar.YEAR));
		q.setParameter(2, first.get(Calendar.MONTH) + 1);
		q.setParameter(3, last.get(Calendar.YEAR));
		q.setParameter(4, last.get(Calendar.MONTH) + 1);
		q.setParameter(5, holdingid);

		@SuppressWarnings("unchecked")
		List<FactuurTotaal> result = (List<FactuurTotaal>) getResultList(q);
		List<FactuurTotaalInfo> inforesult = new ArrayList<>();
		for (FactuurTotaal f : result) {
			FactuurTotaalInfo fti;
			fti = completeFactuurTotaal(f, details);

			inforesult.add(fti);
		}
		return inforesult;
	}

	public List<FactuurTotaalInfo> getFacturenHoldingByFactuurnr(Integer factuurnr) throws VerzuimApplicationException {
		Query q = em.createNativeQuery(selectFactuur + "where (f.factuurnr = ?1) and not f.id is null group by f.id",
				FactuurTotaal.class);
		q.setParameter(1, factuurnr);

		@SuppressWarnings("unchecked")
		List<FactuurTotaal> result = (List<FactuurTotaal>) getResultList(q);
		List<FactuurTotaalInfo> inforesult = new ArrayList<>();
		for (FactuurTotaal f : result) {
			FactuurTotaalInfo fti;
			fti = completeFactuurTotaal(f, true);

			inforesult.add(fti);
		}
		return inforesult;
	}

	public List<FactuurTotaalInfo> getFacturenInPeriodeByWerkgever(Date firstmonth, Date lastmonth, Integer werkgeverid,
			boolean details) throws VerzuimApplicationException {
		/**
		 * Parameters in query:
		 * 
		 * ?1 = firstyear ?2 = firstmonth ?3 = lastyear ?4 = lastmonth
		 */

		Query q = em.createNativeQuery(
				selectFactuur + "where (f.maand >= ?2 and f.jaar = ?1 or f.jaar > ?1) "
						+ "and (f.maand <= ?4  and f.jaar = ?3  or f.jaar < ?3) and f.werkgeverid = ?5 and not f.id is null group by f.id",
				FactuurTotaal.class);
		Calendar first = Calendar.getInstance();
		first.setTime(firstmonth);
		Calendar last = Calendar.getInstance();
		last.setTime(lastmonth);
		q.setParameter(1, first.get(Calendar.YEAR));
		q.setParameter(2, first.get(Calendar.MONTH) + 1);
		q.setParameter(3, last.get(Calendar.YEAR));
		q.setParameter(4, last.get(Calendar.MONTH) + 1);
		q.setParameter(5, werkgeverid);

		@SuppressWarnings("unchecked")
		List<FactuurTotaal> result = (List<FactuurTotaal>) getResultList(q);
		List<FactuurTotaalInfo> inforesult = new ArrayList<>();
		for (FactuurTotaal f : result) {
			FactuurTotaalInfo fti;
			fti = completeFactuurTotaal(f, details);

			inforesult.add(fti);
		}
		return inforesult;
	}

	public List<FactuurbetalingInfo> getFactuurbetalingenForFactuur(Integer factuurid)
			throws VerzuimApplicationException {
		Query qw = createQuery("select fb from Factuurbetaling fb " + "where fb.factuurid = :factuurid");
		qw.setParameter("factuurid", factuurid);

		@SuppressWarnings("unchecked")
		List<Factuurbetaling> result = (List<Factuurbetaling>) getResultList(qw);
		List<FactuurbetalingInfo> inforesult = new ArrayList<>();
		for (Factuurbetaling b : result)
			inforesult.add(completeFactuurbetaling(b));

		return inforesult;
	}

	public void deleteFactuurbetaling(FactuurbetalingInfo factuurbetaling) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(factuurbetaling, this.getCurrentuser()));
	}

	public FactuurbetalingInfo updateFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws VerzuimApplicationException, ValidationException {
		try {
			factuurbetaling.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuurbetaling entity = converter.toEntity(factuurbetaling, this.getCurrentuser()); 
		this.updateEntity(entity);
		return converter.fromEntity(entity);
	}

	public FactuurbetalingInfo addFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws VerzuimApplicationException, ValidationException {
		try {
			factuurbetaling.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Factuurbetaling fbi = converter.toEntity(factuurbetaling, this.getCurrentuser());
		this.insertEntity(fbi);
		return converter.fromEntity(fbi);
	}

	public void deleteFacturenWerkgever(Integer id) throws ValidationException, VerzuimApplicationException {
		Query qw = createQuery("select f from Factuur f " + "where f.werkgeverid = :werkgeverid");
		qw.setParameter("werkgeverid", id);

		@SuppressWarnings("unchecked")
		List<Factuur> result = (List<Factuur>) getResultList(qw);
		for (Factuur b : result){
			deleteFactuur(this.converter.fromEntity(b));
		}
	}

}
