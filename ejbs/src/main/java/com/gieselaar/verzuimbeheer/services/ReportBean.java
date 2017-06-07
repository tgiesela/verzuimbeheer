package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Oereport;
import com.gieselaar.verzuimbeheer.entities.VActueelverzuim;
import com.gieselaar.verzuimbeheer.entities.WerknemerAantallen;
import com.gieselaar.verzuimbeheer.entities.WerknemerVerzuim;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.VerzuimAantalInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerAantallenInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.ReportConversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Session Bean implementation class ReportBean
 */
@Stateless(mappedName = "java:global/verzuimbeheer/ReportBean", name = "ReportBean")
@LocalBean
public class ReportBean extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	ReportConversion converter;
	@EJB
	WerkgeverBean werkgever;
	@EJB
	OeBean oe;
	/**
	 * Default constructor.
	 */
	/* Onderstaande qeury vervangt de oorspronkelijke VIEW V_ACTUEELVERZUIM */
	private String actueelverzuimQuery = "select dienstverband.einddatumcontract AS einddatumcontract"
			+ "    ,dienstverband.startdatumcontract AS startdatumcontract  "
			+ "    ,dienstverband.id AS dienstverbandid "
			+ "    ,dienstverband.werkweek AS werkweek "
			+ "    ,dienstverband.personeelsnummer AS personeelsnummer "
			+ "    ,werknemer.voorletters AS voorletters "
			+ "    ,werknemer.voorvoegsel AS voorvoegsel "
			+ "    ,werknemer.achternaam AS achternaam "
			+ "    ,werknemer.voornaam AS voornaam "
			+ "    ,werknemer.id AS werknemerid "
			+ "    ,werknemer.geslacht AS geslacht "
			+ "    ,werknemer.burgerservicenummer AS burgerservicenummer "
			+ "    ,werknemer.geboortedatum AS geboortedatum "
			+ "    ,verzuim.meldingsdatum AS verzuimmeldingsdatum "
			+ "    ,verzuim.einddatumverzuim AS einddatumverzuim "
			+ "    ,verzuim.startdatumverzuim AS startdatumverzuim "
			+ "    ,verzuim.gerelateerdheid AS gerelateerdheid "
			+ "    ,verzuim.cascode AS cascode "
			+ "    ,verzuim.uitkeringnaarwerknemer AS uitkeringnaarwerknemer "
			+ "    ,cascode.omschrijving AS cascodeomschrijving "
			+ "    ,verzuim.opmerkingen AS opmerkingen "
			+ "    ,coalesce(verzuim.id,0) AS verzuimid "
			+ "    ,coalesce(verzuim.vangnettype,2) AS vangnettype "
			+ "    ,werkgever.naam AS werkgevernaam "
			+ "    ,werkgever.id AS werkgeverid "
			+ "    ,coalesce(werkgever.Holding_id,0) AS holdingid "
			+ "    ,werkgever.werkweek AS werkgeverwerkweek "
			+ "    ,holding.naam AS holdingnaam "
			+ "    ,afdeling.naam AS Afdelingnaam "
			+ "    ,afdeling.id AS afdelingid "
			+ "    ,herstel.datum_herstel AS datum_herstel "
			+ "    ,herstel.percentage_herstel AS percentage_herstel "
			+ "    ,herstel.percentage_herstel_at AS percentage_herstel_at "
			+ "    ,herstel.meldingsdatum AS herstelmeldingsdatum "
			+ "    ,herstel.opmerkingen AS herstelopmerkingen "
			+ "    ,coalesce(herstel.id,0) AS herstelid "
			+ " from   WERKGEVER werkgever "
			+ " left join HOLDING holding "
			+ "    on werkgever.Holding_id = holding.id "
			+ "   join AFDELING afdeling "
			+ "    on werkgever.id = afdeling.werkgever_id "
			+ "   join AFDELING_HAS_WERKNEMER afdwnr "
			+ "    on afdeling.id = afdwnr.afdeling_ID "
			+ "   join WERKNEMER werknemer "
			+ "    on werknemer.id = afdwnr.werknemer_ID "
			+ "   join DIENSTVERBAND dienstverband "
			+ "    on dienstverband.werknemer_ID = werknemer.id "
			+ "   join VERZUIM verzuim "
			+ "    on verzuim.dienstverband_ID = dienstverband.id "
			+ " and verzuimtype = "
			+ __verzuimtype.VERZUIM.getValue()
			+ " 	left join CASCODE cascode "
			+ "    on verzuim.cascode = cascode.id "
			+ " 	left join VERZUIMHERSTEL herstel "
			+ "    on herstel.verzuim_id = verzuim.id";

	private String aantalverzuimenQuery = "select werknemer.id AS werknemerid "
			+ "    ,count(*) AS aantal "
			+ " from   WERKGEVER werkgever "
			+ " left join HOLDING holding "
			+ "    on werkgever.Holding_id = holding.id "
			+ "   join AFDELING afdeling "
			+ "    on werkgever.id = afdeling.werkgever_id "
			+ "   join AFDELING_HAS_WERKNEMER afdwnr "
			+ "    on afdeling.id = afdwnr.afdeling_ID "
			+ "   join WERKNEMER werknemer "
			+ "    on werknemer.id = afdwnr.werknemer_ID "
			+ "   join DIENSTVERBAND dienstverband "
			+ "    on dienstverband.werknemer_ID = werknemer.id "
			+ "   join VERZUIM verzuim "
			+ "    on verzuim.dienstverband_ID = dienstverband.id "
			+ " and verzuimtype = " + __verzuimtype.VERZUIM.getValue();

	private String sqlDate(Date date) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

	public int zzgetDateDiff(Date date1, Date date2) {
		long dif = date2.getTime() - date1.getTime();
		return (int) Math.round(dif / (1000.0 * 60 * 60 * 24));
	}

	private Date addDays(Date date, int nrofdays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, nrofdays);
		return cal.getTime();
	}

	private boolean verzuiminperiode(ActueelVerzuimInfo ovzmi, Date start,
			Date einde) {
		if (ovzmi.getStartdatumverzuim() == null)
			return false;
		else if (ovzmi.getEinddatumverzuim() == null)
			return DateOnly.after(ovzmi.getStartdatumverzuim(), einde)?false:true;
		else {
		// Einddatum is ingevuld
			if (DateOnly.before(ovzmi.getEinddatumverzuim(), start)){
				return false;
			}else{ 
				return DateOnly.after(ovzmi.getStartdatumverzuim(), einde)?false:true;
			}
		}
	}


	private ActueelVerzuimInfo copyactueelverzuim(ActueelVerzuimInfo ovzmi) {
		ActueelVerzuimInfo dummyherstel = new ActueelVerzuimInfo();
		dummyherstel.setAchternaam(ovzmi.getAchternaam());
		dummyherstel.setAfdelingid(ovzmi.getAfdelingid());
		dummyherstel.setAfdelingid(ovzmi.getAfdelingid());
		dummyherstel.setAfdelingnaam(ovzmi.getAfdelingnaam());
		dummyherstel.setBurgerservicenummer(ovzmi.getBurgerservicenummer());
		dummyherstel.setCascode(ovzmi.getCascode());
		dummyherstel.setCascodeomschrijving(ovzmi.getCascodeomschrijving());
		dummyherstel.setDatumHerstel(ovzmi.getDatumHerstel());
		dummyherstel.setDienstverbandid(ovzmi.getDienstverbandid());
		dummyherstel.setEinddatumcontract(ovzmi.getEinddatumcontract());
		dummyherstel.setEinddatumverzuim(ovzmi.getEinddatumverzuim());
		dummyherstel.setEindperiode(ovzmi.getEindperiode());
		dummyherstel.setGeboortedatum(ovzmi.getGeboortedatum());
		dummyherstel.setGerelateerdheid(ovzmi.getGerelateerdheid());
		dummyherstel.setGeslacht(ovzmi.getGeslacht());
		dummyherstel.setHerstelid(ovzmi.getHerstelid());
		dummyherstel.setHerstelmeldingsdatum(ovzmi.getHerstelmeldingsdatum());
		dummyherstel.setHerstelopmerkingen(ovzmi.getHerstelopmerkingen());
		dummyherstel.setHoldingid(ovzmi.getHoldingid());
		dummyherstel.setHoldingnaam(ovzmi.getHoldingnaam());
		dummyherstel.setId(ovzmi.getId());
		dummyherstel.setOpmerkingen(ovzmi.getOpmerkingen());
		dummyherstel.setPercentageHerstel(ovzmi.getPercentageHerstel());
		dummyherstel.setPercentageHerstelAt(ovzmi.getPercentageHerstelAt());
		dummyherstel.setPersoneelsnummer(ovzmi.getPersoneelsnummer());
		dummyherstel.setStartdatumcontract(ovzmi.getStartdatumcontract());
		dummyherstel.setStartdatumverzuim(ovzmi.getStartdatumverzuim());
		dummyherstel.setStartperiode(ovzmi.getStartperiode());
		dummyherstel.setVangnettype(ovzmi.getVangnettype());
		dummyherstel.setVerzuimduurinperiode(ovzmi.getVerzuimduurinperiode());
		dummyherstel.setVerzuimherstelduurinperiode(ovzmi
				.getVerzuimherstelduurinperiode());
		dummyherstel.setVerzuimherstelduurnettoinperiode(ovzmi
				.getVerzuimherstelduurnettoinperiode());
		dummyherstel.setVerzuimid(ovzmi.getVerzuimid());
		dummyherstel.setVerzuimmeldingsdatum(ovzmi.getVerzuimmeldingsdatum());
		dummyherstel.setVerzuimpercentage(ovzmi.getVerzuimpercentage());
		dummyherstel.setVoorletters(ovzmi.getVoorletters());
		dummyherstel.setVoornaam(ovzmi.getVoornaam());
		dummyherstel.setVoorvoegsel(ovzmi.getVoorvoegsel());
		dummyherstel.setWerkgeverid(ovzmi.getWerkgeverid());
		dummyherstel.setWerkgevernaam(ovzmi.getWerkgevernaam());
		dummyherstel.setWerkgeverwerkweek(ovzmi.getWerkgeverwerkweek());
		dummyherstel.setWerknemerid(ovzmi.getWerknemerid());
		dummyherstel.setWerkweek(ovzmi.getWerkweek());
		dummyherstel.setUitkeringnaarwerknemer(ovzmi.isUitkeringnaarwerknemer());
		return dummyherstel;
	}

	private List<ActueelVerzuimInfo> postProcessVerzuimen(List<ActueelVerzuimInfo> inforesult,
			int nrrowsbefore, Date start, Date einde) {
		// post processing
		BigDecimal percentage;
		BigDecimal hundred = new BigDecimal(100);

		List<ActueelVerzuimInfo> newlist = new ArrayList<>();

		int i = nrrowsbefore;
		while (i < inforesult.size()) {
			ActueelVerzuimInfo vzm = inforesult.get(i);
			ActueelVerzuimInfo next;
			/*
			 * Als er een herstel bij dit verzuim zit, moet een extra herstel
			 * worden toegevoegd voor de periode tussen het melden van het
			 * verzuim en de startdatumherstel. Als die periode nul is hoeft dit
			 * niet te gebeuren
			 */
			if ((i + 1) < inforesult.size()) {
				next = inforesult.get(i + 1);
				if (!next.getVerzuimid().equals(vzm.getVerzuimid()))
					next = null;
			}else{
				next = null;
			}
			if (vzm.getHerstelid() != 0) {
				if (DateOnly.equals(vzm.getDatumHerstel(),
						vzm.getStartdatumverzuim())) {
					/* We hoeven geen extra regel in te voegen */
				} else {
					/*
					 * Voor de periode tussen de startdatumverzuim en eerste
					 * herstel datum moet een extra herstel worden tussengevoegd
					 */
					ActueelVerzuimInfo extrvzm = copyactueelverzuim(vzm);
					percentage = BigDecimal.ZERO;
					extrvzm.setVerzuimpercentage(hundred.subtract(percentage));
					extrvzm.setPercentageHerstel(percentage);
					extrvzm.setPercentageHerstelAt(percentage);
					extrvzm.setDatumHerstel(extrvzm.getStartdatumverzuim());
					doCalculations(extrvzm, start, einde, vzm);
					newlist.add(extrvzm);
				}
			} else {
				vzm.setDatumHerstel(vzm.getStartdatumverzuim());
				percentage = BigDecimal.ZERO;
				vzm.setVerzuimpercentage(hundred.subtract(percentage));
				vzm.setPercentageHerstel(percentage);
				vzm.setPercentageHerstelAt(percentage);
			}
			Integer verzuimId = vzm.getVerzuimid();
			while (i < inforesult.size() && vzm.getVerzuimid().equals(verzuimId)) {
				if (!verzuiminperiode(vzm, start, einde)) {
					vzm.setVerzuimduurinperiode(BigDecimal.ZERO);
					vzm.setVerzuimherstelduurinperiode(BigDecimal.ZERO);
					vzm.setVerzuimherstelduurnettoinperiode(BigDecimal.ZERO);
					percentage = BigDecimal.ZERO;
					vzm.setVerzuimpercentage(hundred.subtract(percentage));
				} else {
					// het verzuim valt in de opgegeven periode
					doCalculations(vzm, start, einde, next);
					newlist.add(vzm);
				}
				i++;
				if (i < inforesult.size()){
					vzm = inforesult.get(i);
					if (vzm.getVerzuimid().equals(verzuimId)){
						next = null;
						if ((i + 1) < inforesult.size()) {
							next = inforesult.get(i + 1);
							if (!next.getVerzuimid().equals(vzm.getVerzuimid()))
								next = null;
						}
					}else{
						next = null;
					}
				}
			}
		}
		return newlist;
	}

	private void doCalculations(ActueelVerzuimInfo vzm, Date start, Date einde,
			ActueelVerzuimInfo next) {
		Date startdate;
		Date enddate;
		Date startherstel;
		Date eindherstel;
		BigDecimal percentage;
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal vzmpercentage;
		BigDecimal vzmduurinperiode;
		BigDecimal vzmduurnettoinperiode;

		vzm.setStartperiode(start);
		vzm.setEindperiode(einde);

		if (vzm.getEinddatumverzuim() == null)
			enddate = addDays(einde,1); // Bij een openverzuim telt de rapportagedag nog mee, want dan is men nog ziek
		else if (vzm.getEinddatumverzuim().after(einde))
			enddate = einde;
		else if (vzm.getEinddatumverzuim().before(start))
			enddate = start;
		else
			enddate = vzm.getEinddatumverzuim();
		startdate = vzm.getStartdatumverzuim();
		vzm.setVerzuimduurinperiode(new BigDecimal(DateOnly.diffDays(enddate, startdate)));
		if (vzm.getHerstelid() == 0) { /*
										 * geen herstel en dus ook geen
										 * vervolgherstellen
										 */
			percentage = BigDecimal.ZERO;
			startherstel = vzm.getStartdatumverzuim();
			eindherstel = addDays(einde, 1);
		} else { // Dit is een vervolgherstel
			startherstel = vzm.getDatumHerstel();
			percentage = vzm.getPercentageHerstel();
			if (next != null)
				eindherstel = next.getDatumHerstel();
			else
				if (vzm.getEinddatumverzuim() == null)
					eindherstel = addDays(einde, 1); // Bij een openverzuim telt de rapportagedag nog mee...
				else{
					if (DateOnly.before(vzm.getEinddatumverzuim(), einde))
						eindherstel = vzm.getEinddatumverzuim();
					else
						eindherstel = einde;
				}
		}
		if (startherstel.before(start))
			startherstel = start;
		if (eindherstel.after(einde))
			eindherstel = einde;
		else 
			if (eindherstel.before(start))
				eindherstel = start;
		if (startherstel.after(einde))
			startherstel = eindherstel;
		vzmduurinperiode = new BigDecimal(
				DateOnly.diffDays(eindherstel, startherstel));
		vzmpercentage = hundred.subtract(percentage);
		vzmduurnettoinperiode = vzmduurinperiode.multiply(vzmpercentage
				.multiply(
						vzm.getWerkweek().divide(vzm.getWerkgeverwerkweek(), 2,
								RoundingMode.HALF_UP)).divide(hundred, 2,
						RoundingMode.HALF_UP));

		vzm.setVerzuimherstelduurnettoinperiode(vzmduurnettoinperiode);
		vzm.setVerzuimpercentage(vzmpercentage);
		vzm.setVerzuimherstelduurinperiode(vzmduurinperiode);
	}


	private List<ActueelVerzuimInfo> doGetVerzuimenWerknemer(
			Integer werknemerId, Date start, Date einde,
			List<ActueelVerzuimInfo> inforesult)
			throws VerzuimApplicationException {
		int nrrowsbefore = inforesult.size();

		Query q;
		q = em.createNativeQuery(
				actueelverzuimQuery
						+ " where werknemer.id = ?3 "
						+ " AND (dienstverband.startdatumcontract <= ?1 AND (dienstverband.einddatumcontract is null OR dienstverband.einddatumcontract >= ?2)) "
					    + " AND (afdwnr.startdatum <= verzuim.startdatumverzuim AND (afdwnr.einddatum is null OR afdwnr.einddatum >= verzuim.startdatumverzuim)) "
						+ " AND (startdatumverzuim <= ?1 AND (einddatumverzuim is null OR einddatumverzuim >= ?2))"
						+ " order by werkgevernaam, afdelingid, achternaam, werknemerid, startdatumverzuim, datum_herstel",
				VActueelverzuim.class);

		q.setParameter(1, einde);
		q.setParameter(2, start);
		q.setParameter(3, werknemerId);

		@SuppressWarnings("unchecked")
		List<VActueelverzuim> result = (List<VActueelverzuim>) getResultList(q);
		for (VActueelverzuim ovzm : result) {
			ActueelVerzuimInfo ovzmi;
			ovzmi = converter.fromEntity(ovzm);

			inforesult.add(ovzmi);
		}
		return postProcessVerzuimen(inforesult, nrrowsbefore, start, einde);

	}

	public List<VerzuimAantalInfo> getAantalverzuimenInPeriode(
			Integer werkgeverid, Integer holdingid, Integer oeid, Date start,
			Date einde, boolean inclusiefzwangerschap)
			throws VerzuimApplicationException, ValidationException{
		Query q;

		if (holdingid == null && werkgeverid == null && oeid == null)
			throw new ValidationException("Minimaal een van holdingid, werkgeverid of oeid moet ingevuld zijn");
		String wherepart = " AND (dienstverband.startdatumcontract <= ?1 AND (dienstverband.einddatumcontract is null OR dienstverband.einddatumcontract >= ?2)) "
				+ " AND (afdwnr.startdatum <= verzuim.startdatumverzuim AND (afdwnr.einddatum is null OR afdwnr.einddatum >= verzuim.startdatumverzuim)) "
				+ " AND (startdatumverzuim <= ?1 AND (einddatumverzuim is null OR einddatumverzuim >= ?2))";
		if (inclusiefzwangerschap){
			/* Nothing to exclude */
		}else{
			wherepart = wherepart + " AND (verzuim.vangnettype <> "
					+ __vangnettype.ZWANGERSCHAP.getValue().toString()
					+ " AND verzuim.vangnettype <> "
					+ __vangnettype.ZIEKDOORZWANGERSCHAP.getValue().toString()
					+ " ) ";
		}	
		String orderby = " group by werknemer.id order by werknemer.id";
		if (werkgeverid != null && werkgeverid != -1) {
			StringBuilder sb = new StringBuilder(aantalverzuimenQuery);
			sb.append(" where werkgever.id = ?3 ");
			sb.append(wherepart);
			sb.append(orderby);
			q = em.createNativeQuery(sb.toString());
			q.setParameter(3, werkgeverid);
		} else if (holdingid != null && holdingid != -1) {
			StringBuilder sb = new StringBuilder(aantalverzuimenQuery);
			sb.append(" where werkgever.Holding_id = ?3 ");
			sb.append(wherepart);
			sb.append(orderby);
			q = em.createNativeQuery(sb.toString());
			q.setParameter(3, holdingid);
		} else if (oeid != null && oeid != -1) {
			populateReportOE(oeid);
			StringBuilder sb = new StringBuilder(aantalverzuimenQuery);
			sb.append(" where exists (select r.id from OEREPORT r where werkgever.id = r.werkgever_id)");
			sb.append(wherepart);
			sb.append(orderby);
			q = em.createNativeQuery(sb.toString());
		} else{
			throw new ValidationException("Minimaal een van holdingid, werkgeverid of oeid moet ingevuld zijn");
		}

		q.setParameter(1, einde);
		q.setParameter(2, start);

		List<VerzuimAantalInfo> aantallen = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();
		for (Object[] o : results) {
			VerzuimAantalInfo aantal = new VerzuimAantalInfo();
			aantal.setWerknemerid((Integer) o[0]);
			aantal.setAantalverzuimen(((Long) o[1]).intValue());
			aantallen.add(aantal);
		}
		return aantallen;
	}

	public List<ActueelVerzuimInfo> getWerknemerVerzuimen(Integer werknemerId,
			Date start, Date einde) throws VerzuimApplicationException {
		List<ActueelVerzuimInfo> inforesult = new ArrayList<>();
		return doGetVerzuimenWerknemer(werknemerId, start, einde, inforesult);
	}

	public List<ActueelVerzuimInfo> getVerzuimPerMaand(Integer werkgeverId,
			Integer holdingId, Date start, Date einde,
			boolean inclusiefzwangerschap) throws VerzuimApplicationException, ValidationException {
		List<ActueelVerzuimInfo> ovzml = new ArrayList<>();
		List<ActueelVerzuimInfo> result;
		List<WerkgeverInfo> werkgevers;
		if (werkgeverId.equals(-1))
			werkgevers = werkgever.getWerkgeversHolding(holdingId);
		else {
			werkgevers = new ArrayList<>();
			werkgevers.add(werkgever.getById(werkgeverId));
		}

		Calendar periodstartcal = Calendar.getInstance();
		Calendar periodendcal = Calendar.getInstance();
		periodstartcal.setTime(start);
		while (periodstartcal.getTime().before(einde)) {
			periodendcal.setTime(periodstartcal.getTime());
			if (DateOnly.diffDays(einde, start) >= 90) {
				periodendcal.add(Calendar.MONTH, 1);
				periodendcal.add(Calendar.DATE, -1);
			} else {
				periodendcal.add(Calendar.DAY_OF_MONTH, 7);
			}
			if (DateOnly.after(periodendcal.getTime(), einde)) {
				periodendcal.setTime(einde);
			}

			/*
			 * Voeg voor elke afdeling een dummy record toe voor de periode Dit
			 * is nodig voor de grafiek op het verzuimoverzicht
			 */
			List<ActueelVerzuimInfo> resultleeg = new ArrayList<>();
			for (WerkgeverInfo wgr : werkgevers) {
				if (wgr.getAfdelings() != null) {
					for (AfdelingInfo afd : wgr.getAfdelings()) {
						ActueelVerzuimInfo avi = new ActueelVerzuimInfo();
						avi.setWerkgeverid(wgr.getId());
						avi.setWerkgevernaam(wgr.getNaam());
						if (wgr.getHoldingId() == null)
							avi.setHoldingid(0);
						else
							avi.setHoldingid(wgr.getHoldingId());
						avi.setAfdelingid(afd.getId());
						avi.setAfdelingnaam(afd.getNaam());
						avi.setWerknemerid(-1);
						avi.setVerzuimduurinperiode(BigDecimal.ZERO);
						avi.setVerzuimherstelduurinperiode(BigDecimal.ZERO);
						avi.setVerzuimherstelduurnettoinperiode(BigDecimal.ZERO);
						avi.setVerzuimpercentage(BigDecimal.ZERO);
						avi.setStartperiode(periodstartcal.getTime());
						avi.setEindperiode(periodendcal.getTime());
						resultleeg.add(avi);
					}
				}
			}

			result = getActueelVerzuim(werkgeverId, holdingId, -1,
					periodstartcal.getTime(), periodendcal.getTime(),
					inclusiefzwangerschap);
			ovzml.addAll(result);
			ovzml.addAll(resultleeg); // Nodig om de grafiek met alle maanden te
										// kunnen vullen
			if (DateOnly.diffDays(einde, start) >= 90) {
				periodstartcal.add(Calendar.MONTH, 1);
			} else {
				periodstartcal.add(Calendar.DAY_OF_MONTH, 7);
			}
		}
		return ovzml;
	}

	@SuppressWarnings("unchecked")
	public List<WerknemerVerzuimInfo> getWerknemerVerzuimen(
			Integer werkgeverId, Integer holdingId, Integer oeId, Date start,
			Date einde, int aantalVerzuimen) throws VerzuimApplicationException, ValidationException {
		List<WerknemerVerzuimInfo> objectlist = new ArrayList<>();

		Query q;
		String query;
		query = 
		"SELECT werkgever.Holding_id AS holdingid"
		+ ",werkgever.id AS werkgeverid"
		+ ",afdeling.id AS afdelingid"
		+ ",werknemer.ID AS werknemerid"
		+ ",dienstverband.id AS dienstverbandid"
		+ ",werknemer.voorletters AS voorletters"
		+ ",werknemer.achternaam AS achternaam"
		+ ",werknemer.voornaam AS voornaam"
		+ ",werknemer.voorvoegsel AS voorvoegsel"
		+ ",werknemer.geslacht AS geslacht"
		+ ",werknemer.burgerservicenummer AS burgerservicenummer"
		+ ",werknemer.geboortedatum AS geboortedatum"
		+ ",werkgever.naam AS werkgevernaam"
		+ ",holding.naam as holdingnaam"
		+ ",afdeling.naam AS Afdelingnaam"
		+ ",count(*) AS aantalverzuimen "
		+ "from   WERKGEVER werkgever "
		+ "left join HOLDING holding ON werkgever.holding_id = holding.id"
		+ "     join AFDELING afdeling ON werkgever.id = afdeling.werkgever_id"
		+ "     join AFDELING_HAS_WERKNEMER afdwnr ON afdeling.id = afdwnr.afdeling_id"
		+ "     join WERKNEMER werknemer ON werknemer.id = afdwnr.werknemer_id"
		+ "     join DIENSTVERBAND dienstverband ON dienstverband.werknemer_id = werknemer.id"
		+ "     join VERZUIM verzuim ON verzuim.dienstverband_id = dienstverband.id"
		+ " WHERE dienstverband.startdatumcontract <= \""
		+ sqlDate(einde)
		+ "\" AND (dienstverband.einddatumcontract is null "
		+ "    OR dienstverband.einddatumcontract >= \""
		+ sqlDate(start)
		+ "\") "
		+ " AND verzuim.startdatumverzuim <= \""
		+ sqlDate(einde)
		+ "\" AND (verzuim.einddatumverzuim is null OR verzuim.einddatumverzuim >= \""
		+ sqlDate(start) + "\")"
		+ " AND (afdwnr.startdatum <= verzuim.startdatumverzuim"
		+ " AND (afdwnr.einddatum is null OR afdwnr.einddatum >= verzuim.startdatumverzuim))";
		if (holdingId != null && holdingId != -1){
			query = query + " AND werkgever.holding_id = "
					+ holdingId.toString();
		}else if (werkgeverId != null && werkgeverId != -1){
			query = query + " AND werkgever.id = " + werkgeverId.toString();
		}else if (oeId != null && oeId != -1){ 
			populateReportOE(oeId);
			query = query + " AND exists (select r.id from OEREPORT r where werkgever.id = r.werkgever_id)";
		} else {
			throw new ValidationException("Een van werkgever, holding of oeid moet ingevuld zijn");
		}

		query = query
				+ " group by werkgever.holding_id, werkgever.id, afdeling.id, werknemer.id, dienstverband.id having count(*) >= ?3 "
				+ " order by werkgever.naam, afdeling.id, werknemer.achternaam, werknemer.id";
		q = em.createNativeQuery(query,
						WerknemerVerzuim.class);

		q.setParameter(1, einde);
		q.setParameter(2, start);
		q.setParameter(3, aantalVerzuimen);

		List<WerknemerVerzuim> result;
		result = (List<WerknemerVerzuim>) getResultList(q);
		for (WerknemerVerzuim ovzm : result) {
			WerknemerVerzuimInfo ovzmi;
			ovzmi = converter.fromEntity(ovzm);

			objectlist.add(ovzmi);
		}
		
		return objectlist;
	}

	public List<WerkgeverInfo> getWerkgevers(Integer werkgeverid,
			Integer holdingid) throws VerzuimApplicationException, ValidationException {
		/*
		 * Returns one of: a list with one Werkgever (if werkgeverid is supplied 
		 * 				   a list of all Werkgever under a Holding
		 */
		if (werkgeverid != null && werkgeverid != -1) {
			WerkgeverInfo wg = werkgever.getById(werkgeverid);
			List<WerkgeverInfo> wgl = new ArrayList<>();
			wgl.add(wg);
			return wgl;
		} else if (holdingid != null && holdingid != -1){
			return werkgever.getWerkgeversHolding(holdingid);
		} else {
			throw new ValidationException("Een van werkgever of holding moet ingevuld zijn");
		}

	}

	public List<ActueelVerzuimInfo> getActueelVerzuim(Integer werkgeverid,
			Integer holdingid, Integer oeid, Date start, Date einde,
			boolean inclusiefzwangerschap) throws VerzuimApplicationException, ValidationException {

		List<ActueelVerzuimInfo> inforesult = new ArrayList<>();
		int nrrowsbefore = inforesult.size();
		Query q;

		if (holdingid == null && werkgeverid == null && oeid == null)
			throw new ValidationException("Minimaal een van holdingid, werkgeverid of oeid moet ingevuld zijn");

		String wherepart = 
				  " AND (dienstverband.startdatumcontract <= ?1 AND (dienstverband.einddatumcontract is null OR dienstverband.einddatumcontract >= ?2)) "
			    + " AND (afdwnr.startdatum <= verzuim.startdatumverzuim AND (afdwnr.einddatum is null OR afdwnr.einddatum >= verzuim.startdatumverzuim)) "
				+ " AND (startdatumverzuim <= ?1 AND (einddatumverzuim is null OR einddatumverzuim >= ?2))";
		if (inclusiefzwangerschap){
			/* nothing to exclude */
		}else{
			wherepart = wherepart + " AND (verzuim.vangnettype <> "
					+ __vangnettype.ZWANGERSCHAP.getValue().toString()
					+ " AND verzuim.vangnettype <> "
					+ __vangnettype.ZIEKDOORZWANGERSCHAP.getValue().toString()
					+ " ) ";
		}
		String orderby = " order by werkgevernaam, afdelingid, achternaam, werknemerid,startdatumverzuim,datum_herstel";
		if (werkgeverid != null && werkgeverid != -1) {
			StringBuilder sb = new StringBuilder(actueelverzuimQuery);
			sb.append(" where werkgever.id = ?3 ");
			sb.append(wherepart);
			sb.append(orderby);
			q = em.createNativeQuery(sb.toString(), VActueelverzuim.class);
			q.setParameter(3, werkgeverid);
		} else if (holdingid != null && holdingid != -1) {
			StringBuilder sb = new StringBuilder(actueelverzuimQuery);
			sb.append(" where werkgever.Holding_id = ?3 ");
			sb.append(wherepart);
			sb.append(orderby);
			q = em.createNativeQuery(sb.toString(), VActueelverzuim.class);
			q.setParameter(3, holdingid);
		} else if (oeid != null && oeid != -1){
			populateReportOE(oeid);
			StringBuilder sb = new StringBuilder(actueelverzuimQuery);
			sb.append(" where exists (select r.id from OEREPORT r where werkgever.id = r.werkgever_id)");
			sb.append(wherepart);
			sb.append(orderby);
			q = em.createNativeQuery(sb.toString(), VActueelverzuim.class);
		} else {
			throw new ValidationException("Minimaal een van holdingid, werkgeverid of oeid moet ingevuld zijn");
		}

		q.setParameter(1, einde);
		q.setParameter(2, start);

		@SuppressWarnings("unchecked")
		List<VActueelverzuim> result = (List<VActueelverzuim>) getResultList(q);
		for (VActueelverzuim ovzm : result) {
			ActueelVerzuimInfo ovzmi;
			ovzmi = converter.fromEntity(ovzm);

			inforesult.add(ovzmi);
		}
		return postProcessVerzuimen(inforesult, nrrowsbefore, start, einde);
	}

	@SuppressWarnings("unchecked")
	public List<WerknemerAantallenInfo> getAantalWerknemersAfdeling(
			Integer werkgeverid, Date peildatum)
			throws VerzuimApplicationException {
		List<WerknemerAantallenInfo> inforesult = new ArrayList<>();
		Query q;
		q = em.createNativeQuery(
				"SELECT dvb.werkgever_id AS werkgeverid, ahw.afdeling_id AS afdelingid"
						+ ", geslacht, count(*) AS aantalwerknemers, sum(dvb.werkweek) AS totaalurenwerknemers "
						+ ", ?1 AS startdatum, ?1 AS einddatum " 
						+ "FROM WERKNEMER wn "
						+ "JOIN DIENSTVERBAND dvb on wn.id = dvb.werknemer_id "
						+ "JOIN AFDELING_HAS_WERKNEMER ahw on ahw.werknemer_id = wn.id AND "
						+ "     (ahw.startdatum <= ?1 and "
						+ "     (ahw.einddatum is null or ahw.einddatum >= ?1)) "
						+ "JOIN AFDELING afd on afd.id = ahw.afdeling_ID "
						+ "where wn.werkgever_id = "
						+ werkgeverid.toString()
						+ "  and dvb.startdatumcontract <= ?1 and "
						+ "     (dvb.einddatumcontract is null or dvb.einddatumcontract >= ?1)"
						+ " group by 1,2,3;", WerknemerAantallen.class);

		q.setParameter(1, peildatum);

		List<WerknemerAantallen> result;
		result = (List<WerknemerAantallen>) getResultList(q);
		for (WerknemerAantallen wna : result) {
			WerknemerAantallenInfo wnai;
			wnai = converter.fromEntity(wna);
			wnai.setPeildatum(peildatum);
			inforesult.add(wnai);
		}
		return inforesult;
	}

	@SuppressWarnings("unchecked")
	public List<WerknemerAantallenInfo> getAantalWerknemersAfdeling(
			Integer werkgeverid, Date startdatum, Date einddatum)
			throws VerzuimApplicationException {
		List<WerknemerAantallenInfo> inforesult = new ArrayList<>();
		Query q;
		/*
		 * ?1 = start
		 * ?2 = eind
		 */
		
		q = em.createNativeQuery(
				"SELECT dvb.werkgever_id AS werkgeverid"
					+ ", ahw.afdeling_id AS afdelingid"
					+ ", geslacht"
					+ ", count(*) AS aantalwerknemers"
					+ ", sum(datediff(if(ahw.einddatum is null,?2,ahw.einddatum),"
					+ "               if(ahw.startdatum >= ?1,ahw.startdatum,?1))*dvb.werkweek)/"
					+ "      datediff(?2,?1) as totaalurenwerknemers "
					+ ", ?1 AS startdatum, ?1 AS einddatum " 
					+ "FROM WERKNEMER wn "
					+ "JOIN DIENSTVERBAND dvb on wn.id = dvb.werknemer_id and wn.werkgever_id = dvb.werkgever_id "
					+ "JOIN AFDELING_HAS_WERKNEMER ahw on ahw.werknemer_id = wn.id "
					+ " and ((ahw.einddatum is null and dvb.einddatumcontract is null) " 
					+ " or ((ahw.einddatum <= ?2 or ahw.einddatum is null)))"
					+ "JOIN AFDELING afd on afd.id = ahw.afdeling_ID "
					+ "where wn.werkgever_id = "
					+ werkgeverid.toString()
					+ "  and dvb.startdatumcontract <= ?2 and "
					+ "     (dvb.einddatumcontract is null or dvb.einddatumcontract >= ?1) "
					+ "  and ahw.startdatum <= ?2 and " 
					+ "     (ahw.einddatum is null or ahw.einddatum >= ?1) " 
					+ " group by 1,2,3;", WerknemerAantallen.class);

		q.setParameter(1, startdatum);
		q.setParameter(2, einddatum);

		List<WerknemerAantallen> result;
		result = (List<WerknemerAantallen>) getResultList(q);
		for (WerknemerAantallen wna : result) {
			WerknemerAantallenInfo wnai;
			wnai = converter.fromEntity(wna);
			wnai.setPeildatum(startdatum);
			inforesult.add(wnai);
		}
		return inforesult;
	}
	private void populateReportOE(int i) throws VerzuimApplicationException {
		// Empty OEREPORT table
		Query emptyTable = createQuery("Delete from Oereport r");
		executeUpdate(emptyTable);

		// Copy first OE to OEREPORT table
		oe.setCurrentuser(getCurrentuser());
		OeInfo w = oe.getOeById(i);

		Oereport r = new Oereport();
		r.setId(w.getId());
		r.setNaam(w.getNaam());
		r.setOeniveauId(w.getOeniveau().getId());
		r.setParentoeId(w.getParentoeId());
		r.setWerkgeverId(w.getWerkgeverId());
		this.insertEntity(r);

		// Process all subniveaus and insert OE rows into OEREPORT
		List<OeNiveauInfo> niveaus = oe.getOeNiveaus();
		boolean found;
		int currentniveauid = w.getOeniveau().getId();
		do {
			found = false;
			for (OeNiveauInfo niveau : niveaus) {
				if ((niveau.getParentoeniveauId() != null) && (niveau.getParentoeniveauId() == currentniveauid)) {
					Query q = em
							.createNativeQuery("insert into OEREPORT select o.* from OE o JOIN OENIVEAU n on n.id = o.oeniveau_id "
									+ "where o.parentoe_id in (select id from OEREPORT) and n.oeniveau = "
									+ niveau.getOeniveau());
					executeUpdate(q);
					found = true;
					currentniveauid = niveau.getId();
				}
			}
		} while (found);
	}
}
