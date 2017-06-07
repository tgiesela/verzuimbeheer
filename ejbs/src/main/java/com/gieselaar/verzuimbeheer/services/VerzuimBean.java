package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.gieselaar.verzuimbeheer.entities.Dienstverband;
import com.gieselaar.verzuimbeheer.entities.Documenttemplate;
import com.gieselaar.verzuimbeheer.entities.Todo;
import com.gieselaar.verzuimbeheer.entities.TodoFast;
import com.gieselaar.verzuimbeheer.entities.Verzuim;
import com.gieselaar.verzuimbeheer.entities.Verzuimactiviteit;
import com.gieselaar.verzuimbeheer.entities.Verzuimdocument;
import com.gieselaar.verzuimbeheer.entities.Verzuimherstel;
import com.gieselaar.verzuimbeheer.entities.Verzuimmedischekaart;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.VerzuimConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;
import com.gieselaar.verzuimbeheer.utils.WerknemerConversion;

/**
 * Session Bean implementation class VerzuimBean
 */
@SuppressWarnings("serial")
@Stateless
@LocalBean
// @TransactionManagement(TransactionManagementType.BEAN)
public class VerzuimBean extends BeanBase {

	@EJB
	VerzuimConversion converter;
	@EJB
	WerknemerConversion werknemerconverter;
	@EJB
	WerkgeverConversion werkgeverconverter;
	@EJB
	WerkgeverBean werkgever;
	@EJB
	ActiviteitBean activiteitEJB;
	@EJB
	WerknemerBean werknemer;

	private static String todofastquery = "select t.*, v.startdatumverzuim, w.id as werknemerid, w.achternaam, w.burgerservicenummer, wg.naam as werkgevernaam"
			+ " from TODO t, VERZUIM v, DIENSTVERBAND d, WERKNEMER w, WERKGEVER wg "
			+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and v.id = t.verzuim_ID and w.werkgever_ID = wg.id ";
	private static String todofastorderby = " order by t.waarschuwingsdatum";

	private enum __verzuimofherstel {
		VERZUIM, HERSTEL;
	}

	@SuppressWarnings("unchecked")
	public List<VerzuimInfo> getVerzuimenDienstverband(int dienstverbandid) throws VerzuimApplicationException {
		Query qv = em.createQuery(
				"select v from Verzuim v, Dienstverband d where v.dienstverband_ID = d.id and d.id = :dvb_id");
		qv.setParameter("dvb_id", dienstverbandid);
		List<Verzuim> resultvzm = (List<Verzuim>) getResultList(qv);

		List<VerzuimInfo> lvzm = new ArrayList<>();
		if (resultvzm.isEmpty())
			return lvzm;
		else {
			for (Verzuim vzm : resultvzm) {
				VerzuimInfo vzmi;
				vzmi = converter.fromEntity(vzm);
				lvzm.add(vzmi);
			}
			return lvzm;
		}
	}

	public List<TodoFastInfo> getTodosFast() throws VerzuimApplicationException {
		Query qt = em.createNativeQuery(todofastquery + todofastorderby,
				TodoFast.class);
		@SuppressWarnings("unchecked")
		List<TodoFast> resulttodos = (List<TodoFast>) getResultList(qt);
		List<TodoFastInfo> tdl = new ArrayList<>();

		((ArrayList<TodoFastInfo>) tdl).ensureCapacity(resulttodos.size());

		if (resulttodos.isEmpty())
			return tdl;
		else {
			for (TodoFast tf : resulttodos) {
				tdl.add(converter.fromEntity(tf));
			}
			return tdl;
		}
	}

	public List<TodoFastInfo> getOpenTodosFast() throws VerzuimApplicationException {
		Query qt = em.createNativeQuery(todofastquery  
									  + "  and t.verzuimactiviteit_id is null"
									  + todofastorderby,
				TodoFast.class);
		@SuppressWarnings("unchecked")
		List<TodoFast> resulttodos = (List<TodoFast>) getResultList(qt);
		List<TodoFastInfo> tdl = new ArrayList<>();

		((ArrayList<TodoFastInfo>) tdl).ensureCapacity(resulttodos.size());

		if (resulttodos.isEmpty())
			return tdl;
		else {
			for (TodoFast tf : resulttodos) {
				tdl.add(converter.fromEntity(tf));
			}
			return tdl;
		}
	}
	public List<TodoFastInfo> getClosedTodosFast() throws VerzuimApplicationException {
		Query qt = em.createNativeQuery(todofastquery
									  + "  and not t.verzuimactiviteit_id is null"
									  + todofastorderby,
				TodoFast.class);
		@SuppressWarnings("unchecked")
		List<TodoFast> resulttodos = (List<TodoFast>) getResultList(qt);
		List<TodoFastInfo> tdl = new ArrayList<>();

		((ArrayList<TodoFastInfo>) tdl).ensureCapacity(resulttodos.size());

		if (resulttodos.isEmpty())
			return tdl;
		else {
			for (TodoFast tf : resulttodos) {
				tdl.add(converter.fromEntity(tf));
			}
			return tdl;
		}
	}

	@SuppressWarnings("unchecked")
	public List<TodoInfo> getTodos() throws VerzuimApplicationException {
		Query qt = em.createQuery(
				"select t, v, w.achternaam, w.burgerservicenummer, wg.naam, w from Todo t, Verzuim v, Dienstverband d, Werknemer w, Werkgever wg "
						+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and v.id = t.verzuim_ID and w.werkgever_ID = wg.id "
						+ "order by t.waarschuwingsdatum");
		List<Object[]> resulttodos = (List<Object[]>) getResultList(qt);
		List<TodoInfo> tdl = new ArrayList<>();

		((ArrayList<TodoInfo>) tdl).ensureCapacity(resulttodos.size());

		if (resulttodos.isEmpty())
			return tdl;
		else {
			for (Object[] o : resulttodos) {
				TodoInfo ti;
				ti = converter.fromEntity((Todo) o[0]);
				ti.setVerzuim(converter.fromEntity((Verzuim) o[1]));
				ti.setAchternaam((String) o[2]);
				ti.setBurgerservicenummer((String) o[3]);
				ti.setWerkgevernaam((String) o[4]);
				ti.setWerknemer(werknemerconverter.fromEntity((Werknemer) o[5]));
				tdl.add(ti);
			}
			return tdl;
		}
	}
	public List<TodoFastInfo> getTodosVerzuim(Integer verzuimid) throws VerzuimApplicationException {
		Query qt = em.createNativeQuery(
				"select t.*, v.startdatumverzuim, w.id as werknemerid, w.achternaam, w.burgerservicenummer, wg.naam as werkgevernaam"
						+ " from TODO t, VERZUIM v, DIENSTVERBAND d, WERKNEMER w, WERKGEVER wg "
						+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and v.id = t.verzuim_ID and w.werkgever_ID = wg.id "
						+ " and v.id = ?1 "
						+ "order by t.waarschuwingsdatum",
				TodoFast.class);

		qt.setParameter(1, verzuimid);
		@SuppressWarnings("unchecked")
		List<TodoFast> resulttodos = (List<TodoFast>) getResultList(qt);
		List<TodoFastInfo> tdl = new ArrayList<>();

		((ArrayList<TodoFastInfo>) tdl).ensureCapacity(resulttodos.size());

		if (resulttodos.isEmpty())
			return tdl;
		else {
			for (TodoFast tf : resulttodos) {
				tdl.add(converter.fromEntity(tf));
			}
			return tdl;
		}
	}

	@SuppressWarnings("unchecked")
	public TodoInfo getTodo(int todoid) throws VerzuimApplicationException {
		Query qt = em
				.createQuery("select t, v, w, wg from Todo t, Verzuim v, Dienstverband d, Werknemer w, Werkgever wg "
						+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and v.id = t.verzuim_ID and "
						+ " w.werkgever_ID = wg.id and t.id = :todo_id");
		Query qva = em.createQuery("select va from Verzuimactiviteit va where va.id = :va_id");
		qt.setParameter("todo_id", todoid);
		List<Object[]> resulttodos = (List<Object[]>) getResultList(qt);

		if (resulttodos.size() != 1)
			return null;
		else {
			Object[] o = resulttodos.get(0);
			TodoInfo ti;
			ti = converter.fromEntity((Todo) o[0]);
			ti.setVerzuim(converter.fromEntity((Verzuim) o[1]));
			ti.setWerknemer(werknemerconverter.fromEntity((Werknemer) o[2]));
			ti.setWerkgever(werkgeverconverter.fromEntity((Werkgever) o[3]));
			if (ti.getVerzuimactiviteitId() != null) {
				qva.setParameter("va_id", ti.getVerzuimactiviteitId());
				List<Verzuimactiviteit> resultva = (List<Verzuimactiviteit>) getResultList(qva);
				ti.setVerzuimActiviteit(converter.fromEntity(resultva.get(0)));
			} else
				ti.setVerzuimActiviteit(null);

			ti.setAchternaam(ti.getWerknemer().getAchternaam());
			ti.setBurgerservicenummer(ti.getWerknemer().getBurgerservicenummer());
			ti.setWerkgevernaam(ti.getWerkgever().getNaam());
			return ti;
		}
	}
	public TodoFastInfo getTodoFast(Integer todo) throws VerzuimApplicationException {
		Query qt = em.createNativeQuery(
				"select t.*, v.startdatumverzuim, w.id as werknemerid, w.achternaam, w.burgerservicenummer, wg.naam as werkgevernaam"
						+ " from TODO t, VERZUIM v, DIENSTVERBAND d, WERKNEMER w, WERKGEVER wg "
						+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and v.id = t.verzuim_ID and w.werkgever_ID = wg.id "
						+ " and t.id = ?1 "
						+ "order by t.waarschuwingsdatum",
				TodoFast.class);

		qt.setParameter(1, todo);
		TodoFast resulttodo = (TodoFast) getSingleResult(qt);
		return converter.fromEntity(resulttodo);
	}

	@SuppressWarnings("unchecked")
	public VerzuimInfo getVerzuimDetails(int verzuimid) throws VerzuimApplicationException {
		Query qv;
		Query qh;
		Query qm;
		Query qa;
		Query qd;
		Query qt;
		List<Object[]> resultv;
		List<Verzuimherstel> resulth;
		List<Verzuimmedischekaart> resultm;
		List<Verzuimdocument> resultd;
		List<Verzuimactiviteit> resulta;
		List<Todo> resultt;
		List<VerzuimHerstelInfo> verzuimherstellen;
		List<VerzuimMedischekaartInfo> verzuimmedischekaarten;
		List<VerzuimDocumentInfo> verzuimdocumenten;
		List<VerzuimActiviteitInfo> verzuimactiviteiten;
		List<TodoInfo> todos;
		VerzuimInfo v;

		qv = createQuery("select v,d,w from Verzuim v, Dienstverband d, Werknemer w, Werkgever wg "
				+ "where v.dienstverband_ID = d.id and d.werknemer_ID = w.id and w.werkgever_ID = wg.id "
				+ " and v.id = :vzm_id");
		qh = createQuery("select h from Verzuimherstel h where h.verzuimId = :vzm_id");
		qm = createQuery("select m from Verzuimmedischekaart m where m.verzuim_ID = :vzm_id");
		qd = createQuery("select d from Verzuimdocument d where d.verzuimId = :vzm_id");
		qa = createQuery("select a from Verzuimactiviteit a where a.verzuim_ID = :vzm_id");
		qt = createQuery("select t from Todo t where t.verzuim_ID = :vzm_id order by t.deadlinedatum");
		qv.setParameter("vzm_id", verzuimid);
		qh.setParameter("vzm_id", verzuimid);
		qm.setParameter("vzm_id", verzuimid);
		qd.setParameter("vzm_id", verzuimid);
		qa.setParameter("vzm_id", verzuimid);
		qt.setParameter("vzm_id", verzuimid);

		resultv = (List<Object[]>) getResultList(qv);
		resulth = (List<Verzuimherstel>) getResultList(qh);
		resultm = (List<Verzuimmedischekaart>) getResultList(qm);
		resultd = (List<Verzuimdocument>) getResultList(qd);
		resulta = (List<Verzuimactiviteit>) getResultList(qa);
		resultt = (List<Todo>) getResultList(qt);
		v = new VerzuimInfo();
		if (resultv.size() != 1)
			return null;
		else {
			for (Object[] res : resultv) {
				v = converter.fromEntity((Verzuim) res[0]);
				v.setDienstverband(werknemerconverter.fromEntity((Dienstverband) res[1]));
				v.setWerknemer(werknemerconverter.fromEntity((Werknemer) res[2]));

				verzuimherstellen = new ArrayList<>();
				for (Verzuimherstel vh : resulth)
					verzuimherstellen.add(converter.fromEntity(vh));
				v.setVerzuimherstellen(verzuimherstellen);

				verzuimmedischekaarten = new ArrayList<>();
				for (Verzuimmedischekaart vm : resultm)
					verzuimmedischekaarten.add(converter.fromEntity(vm));
				v.setVerzuimmedischekaarten(verzuimmedischekaarten);

				verzuimdocumenten = new ArrayList<>();
				for (Verzuimdocument vd : resultd)
					verzuimdocumenten.add(converter.fromEntity(vd));
				v.setVerzuimdocumenten(verzuimdocumenten);

				verzuimactiviteiten = new ArrayList<>();
				for (Verzuimactiviteit va : resulta)
					verzuimactiviteiten.add(converter.fromEntity(va));
				v.setVerzuimactiviteiten(verzuimactiviteiten);

				todos = new ArrayList<>();
				for (Todo t : resultt)
					todos.add(converter.fromEntity(t));
				v.setTodos(todos);
			}
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	public List<VerzuimHerstelInfo> getVerzuimHerstellen(int dienstverbandid) throws VerzuimApplicationException {
		Query qh;
		List<Verzuimherstel> resulth;
		List<VerzuimHerstelInfo> verzuimherstellen;
		qh = createQuery("select h from Verzuim v, Verzuimherstel h, Dienstverband d "
				+ "where v.dienstverband_ID = d.id and h.verzuimId = v.id and d.id = :dvb_id order by h.verzuimId");
		qh.setParameter("dvb_id", dienstverbandid);

		resulth = (List<Verzuimherstel>) getResultList(qh);
		verzuimherstellen = new ArrayList<>();
		for (Verzuimherstel h : resulth) {
			verzuimherstellen.add(converter.fromEntity(h));
		}
		return verzuimherstellen;
	}

	@SuppressWarnings("unchecked")
	private List<VerzuimHerstelInfo> getVerzuimHerstellenVerzuim(int verzuimid) throws VerzuimApplicationException {
		Query qh;
		List<Verzuimherstel> resulth;
		List<VerzuimHerstelInfo> verzuimherstellen;
		qh = createQuery("select h from Verzuim v, Verzuimherstel h "
				+ "where h.verzuimId = v.id and v.id = :vzm_id order by h.verzuimId");
		qh.setParameter("vzm_id", verzuimid);

		resulth = (List<Verzuimherstel>) getResultList(qh);
		verzuimherstellen = new ArrayList<>();
		for (Verzuimherstel h : resulth) {
			verzuimherstellen.add(converter.fromEntity(h));
		}
		return verzuimherstellen;
	}

	@SuppressWarnings("unchecked")
	public List<VerzuimDocumentInfo> getVerzuimDocumenten(int dienstverbandid) throws VerzuimApplicationException {
		Query qd;
		List<Verzuimdocument> resultd;
		List<VerzuimDocumentInfo> verzuimdocumenten;
		qd = createQuery("select vd from Verzuim v, Verzuimdocument vd, Dienstverband d "
				+ "where v.dienstverband_ID = d.id and vd.verzuimId = v.id and d.id = :dvb_id order by vd.verzuimId");
		qd.setParameter("dvb_id", dienstverbandid);

		resultd = (List<Verzuimdocument>) getResultList(qd);
		verzuimdocumenten = new ArrayList<>();
		for (Verzuimdocument d : resultd) {
			verzuimdocumenten.add(converter.fromEntity(d));
		}
		return verzuimdocumenten;
	}

	public List<DocumentTemplateInfo> getDocumentTemplates() throws VerzuimApplicationException {
		Query qd;
		qd = createQuery("select dt from Documenttemplate dt");

		@SuppressWarnings("unchecked")
		List<Documenttemplate> resultdt = (List<Documenttemplate>) getResultList(qd);
		List<DocumentTemplateInfo> dtl = new ArrayList<>();
		for (Documenttemplate dt : resultdt)
			dtl.add(converter.fromEntity(dt));
		return dtl;
	}

	public boolean deleteVerzuim(VerzuimInfo info) throws VerzuimApplicationException {
		Query vm = em.createQuery("Delete from Verzuimmedischekaart vm where vm.verzuim_ID = :vzm_id");
		Query vd = em.createQuery("Delete from Verzuimdocument vd where vd.verzuimId = :vzm_id");
		Query vh = em.createQuery("Delete from Verzuimherstel vh where vh.verzuimId = :vzm_id");
		Query va = em.createQuery("Delete from Verzuimactiviteit va where va.verzuim_ID = :vzm_id");
		Query vz = em.createQuery("Delete from Verzuim vz where vz.id = :vzm_id");

		vm.setParameter("vzm_id", info.getId());
		vd.setParameter("vzm_id", info.getId());
		vh.setParameter("vzm_id", info.getId());
		va.setParameter("vzm_id", info.getId());
		vz.setParameter("vzm_id", info.getId());
		executeUpdate(vm);
		executeUpdate(vd);
		executeUpdate(vh);
		executeUpdate(va);
		executeUpdate(vz);

		return true;
	}

	public boolean deleteVerzuimenDienstverband(int dienstverbandid) throws VerzuimApplicationException {
		List<VerzuimInfo> vzmn = getVerzuimenDienstverband(dienstverbandid);
		if (vzmn != null)
			for (VerzuimInfo vzm : vzmn) {
				deleteVerzuim(vzm);
			}
		return true;
	}

	private Date calcDate(Date from, __periodesoort soort, int lengte) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(from);

		switch (soort) {
		case DAGEN:
			cal.add(Calendar.DATE, lengte);
			break;
		case WEKEN:
			cal.add(Calendar.DATE, lengte * 7);
			break;
		case MAANDEN:
			cal.add(Calendar.MONTH, lengte);
			break;
		}
		return cal.getTime();
	}

	private DienstverbandInfo getDienstverband(Integer dvb) throws ValidationException {
		Query q = em.createQuery("Select d from Dienstverband d where id = :dvb");
		q.setParameter("dvb", dvb);

		Dienstverband d = (Dienstverband) q.getSingleResult();
		if (d != null) {
			return werknemerconverter.fromEntity(d);
		} else {
			throw new ValidationException("Dienstverband niet gevonden: " + dvb.toString());
		}
	}

	public VerzuimInfo addVerzuim(VerzuimInfo info) throws ValidationException, VerzuimApplicationException {
		int ketenverzuimduur;
		Verzuim verzuim;
		List<VerzuimInfo> alleVerzuimen;
		int cntOpen;

		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		if (info.getDienstverband() == null) {
			info.setDienstverband(getDienstverband(info.getDienstverbandId()));
		}
		if ((info.getDienstverband().getEinddatumcontract() != null)
				&& (DateOnly.after(info.getStartdatumverzuim(), info.getDienstverband().getEinddatumcontract()))) {
			throw applicationException(new ValidationException("Datum verzuim overlapt met einddatum dienstverband!"));
		}

		alleVerzuimen = getVerzuimenDienstverband(info.getDienstverbandId());
		try {
			cntOpen = countOpenVerzuim(alleVerzuimen, null);
			if ((cntOpen > 0) && (info.getEinddatumverzuim() == null)) {
				throw applicationException(new ValidationException("Er is nog een open verzuim"));
			}
			checkOverlapVerzuimen(info, alleVerzuimen);

			ketenverzuimduur = checkKetenverzuim(info, alleVerzuimen);
			verzuim = converter.toEntity(info, this.getCurrentuser());
			this.insertEntity(verzuim);
			info = this.getVerzuimDetails(verzuim.getId());
		} catch (ValidationException ve) {
			throw applicationException(ve);
		}

		if (info.getVerzuimtype() == __verzuimtype.VERZUIM) {
			generateTodos(info, info.getStartdatumverzuim(), false, ketenverzuimduur);
			if ((info.getDienstverband() != null) && (info.getDienstverband().getEinddatumcontract() != null)) {
				werknemer.checkZiekUitDient(info, info.getDienstverband().getEinddatumcontract());
			}
		}
		return info;
	}

	private boolean activiteitApplicable(ActiviteitInfo ai, VerzuimInfo vzm) {
		boolean applicable = false;
		if (ai.isNormaalverzuim() && (vzm.getVangnettype() == __vangnettype.NVT)) {
			applicable = true;
		}
		if (ai.isVangnet()) {
			if (ai.getVangnettype() != null) { /*
												 * alleen voor opgegeven
												 * vangnettype
												 */
				if (vzm.getVangnettype() == ai.getVangnettype())
					applicable = true;
			} else { /* voor alle vangnettypene */
				if (vzm.getVangnettype() != __vangnettype.NVT)
					applicable = true;
			}
		}
		if ((ai.isKetenverzuim()) && 
			(vzm.getKetenverzuim())){
				applicable = true;
		}
		return applicable;
	}

	private void generateTodosHerstel(VerzuimHerstelInfo info, VerzuimInfo vzm) throws VerzuimApplicationException {
		HashMap<Integer, ActiviteitInfo> planfirst = getListOfActiviteiten(vzm, __verzuimofherstel.HERSTEL);
		Iterator<?> it = planfirst.entrySet().iterator();
		while (it.hasNext()) {
			Entry<?, ?> pair = (Entry<?, ?>) it.next();
			ActiviteitInfo ai = (ActiviteitInfo) pair.getValue();
			if (info.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
				if (ai.getDeadlinestartpunt() == __meldingsoort.VOLLEDIGHERSTEL && activiteitApplicable(ai, vzm)) {
					createTodo(ai, info.getDatumHerstel(), info.getVerzuim(), 0);
				}
			} else if (ai.getDeadlinestartpunt() == __meldingsoort.GEDEELTELIJKHERSTEL && activiteitApplicable(ai, vzm))
				createTodo(ai, info.getDatumHerstel(), info.getVerzuim(), 0);
		}
	}

	private HashMap<Integer, ActiviteitInfo> getListOfActiviteiten(VerzuimInfo info, __verzuimofherstel type)
			throws VerzuimApplicationException {
		WerkgeverInfo wgi;
		List<PakketInfo> pakketten;
		HashMap<Integer, ActiviteitInfo> planfirst;
		planfirst = new HashMap<>();

		werkgever.setCurrentuser(getCurrentuser());
		wgi = werkgever.getById(info.getDienstverband().getWerkgeverId());
		pakketten = wgi.getPakketten();

		for (PakketInfo pi : pakketten) {
			// Pakketten kunnen dezelfde activiteiten bevatten
			// we bouwen hier de lijst van unieke activiteiten op
			// die van toepassing zijn na aanvang van het verzuim
			processActiviteiten(info, planfirst, pi.getAktiviteiten(), type);
		}
		return planfirst;
	}

	private void processActiviteiten(VerzuimInfo info, HashMap<Integer, ActiviteitInfo> planfirst,
			List<ActiviteitInfo> aktiviteiten, __verzuimofherstel type) {
		for (ActiviteitInfo ai : aktiviteiten) {
			boolean applicable = false;
			if (planfirst.containsKey(ai.getId()) || (!ai.isNormaalverzuim() && !ai.isVangnet())) {
				/*
				 * staat er al een keer in, of is niet actief (zowel vangnet als
				 * normaalverzuim staan uit
				 */
			} else {
				if (type == __verzuimofherstel.VERZUIM && (ai.getDeadlinestartpunt() == __meldingsoort.ZIEKMELDING
						|| ai.getDeadlinestartpunt() == __meldingsoort.NIEUWEZIEKMELDING)) {
					applicable = activiteitApplicable(ai, info);
				}
				if (type == __verzuimofherstel.HERSTEL
						&& (ai.getDeadlinestartpunt() == __meldingsoort.GEDEELTELIJKHERSTEL
								|| ai.getDeadlinestartpunt() == __meldingsoort.VOLLEDIGHERSTEL)) {
					applicable = activiteitApplicable(ai, info);
				}
				if (applicable) {
					if (ai.getPlannaactiviteit() != null) {
						/*
						 * Deze doet nu niet mee, pas bij afsluiten van een
						 * andere TodoInfo
						 */
					} else {
						planfirst.put(ai.getId(), ai);
					}
				}
			}
		}
	}

	private void generateTodos(VerzuimInfo info, Date startdatumTodo, boolean regenerate, int ketenverzuimduur)
			throws VerzuimApplicationException {
		HashMap<Integer, ActiviteitInfo> planfirst = getListOfActiviteiten(info, __verzuimofherstel.VERZUIM);
		Iterator<?> it = planfirst.entrySet().iterator();
		while (it.hasNext()) {
			Entry<?, ?> pair = (Entry<?, ?>) it.next();
			ActiviteitInfo ai = (ActiviteitInfo) pair.getValue();
			if (regenerate && (!ai.isVerwijdernaherstel())) {
				/*
				 * Om te voorkomen dat er TODOs dubbel worden aangemaakt, wordt
				 * gekeken of het TODOs zijn die niet verwijderd mogen worden na
				 * een herstel, die blijven dan namelijk bestaan en zijn bij
				 * heraanmaak van de TODOs al aanwezig
				 */
			} else {
				createTodo(ai, startdatumTodo, info, ketenverzuimduur);
			}
		}
	}

	private void regenerateTodos(VerzuimInfo info, Date startdatumTodo)
			throws VerzuimApplicationException, ValidationException {
		int ketenverzuimduur = 0;
		Date startdatum = startdatumTodo;
		if (startdatum == null) {
			/*
			 * we moeten kijken of het een ketenverzuim betreft en dan de
			 * startdatum bepalen
			 */
			List<VerzuimInfo> alleVerzuimen = getVerzuimenDienstverband(info.getDienstverbandId());
			ketenverzuimduur = checkKetenverzuim(info, alleVerzuimen);
		}

		generateTodos(info, info.getStartdatumverzuim(), true, ketenverzuimduur);
		List<VerzuimActiviteitInfo> verrichtingen = info.getVerzuimactiviteiten();

		VerzuimInfo verzuim = getVerzuimDetails(info.getId());
		List<TodoInfo> todos = verzuim.getTodos();
		for (TodoInfo todo : todos) {
			VerzuimActiviteitInfo objecttoremove = null;
			for (VerzuimActiviteitInfo vai : verrichtingen) {
				if (todo.getActiviteitId() == vai.getActiviteitId()) {
					objecttoremove = vai;
					break;
				}
			}
			if (objecttoremove != null) {
				todo.setUser(this.getCurrentuser());
				todo.setVerzuimActiviteit(objecttoremove);
				todo.setVerzuimactiviteitId(objecttoremove.getId());
				updateTodo(todo); /* komt al voor in verrichtingen */
				verrichtingen.remove(objecttoremove);
			}
		}
	}

	private int countOpenVerzuim(List<VerzuimInfo> alleVerzuimen, VerzuimInfo verzuim) {
		/*
		 * Het aantal open verzuimen met uitzondering van het meegegeven verzuim
		 * wordt geteld.
		 */
		int cnt = 0;
		for (VerzuimInfo oudverzuim : alleVerzuimen) {
			if (oudverzuim.getEinddatumverzuim() == null) {
				if (verzuim != null && oudverzuim.getId().equals(verzuim.getId())) {
					/* Skip */
				} else {
					cnt++;
				}
			}
		}
		return cnt;
	}

	private int checkKetenverzuim(VerzuimInfo info, List<VerzuimInfo> alleVerzuimen) {
		/*
		 * Hier wordt bepaald of een verzuim een ketenverzuim is. De flag voor
		 * ketenverzuim wordt hier aangezet (indien van toepassing) en de
		 * startdatum voor de todos wordt gezet.
		 */
		/*
		 * LET OP!! Soortgelijke controle zit ook in VerzuimInfo om de gebruiker
		 * vooraf te kunnen informeren
		 */
		int ketenverzuimduur = 0;
		if (info.getVerzuimtype() == __verzuimtype.PREVENTIEF) {
			return ketenverzuimduur;
		}
		info.setKetenverzuim(false);
		Calendar datumKetenverzuim = Calendar.getInstance();
		datumKetenverzuim.setTime(info.getStartdatumverzuim());
		datumKetenverzuim.add(Calendar.DATE, -28);
		for (VerzuimInfo oudverzuim : alleVerzuimen) {
			if (!info.getId().equals(oudverzuim.getId())) {
				if (oudverzuim.getStartdatumverzuim().after(info.getStartdatumverzuim())
						|| oudverzuim.getVerzuimtype() == __verzuimtype.PREVENTIEF) {
					/* die vergeten we dan maar */
				} else {
					if (oudverzuim.getEinddatumverzuim().after(datumKetenverzuim.getTime())) {
						info.setKetenverzuim(true);
						/*
						 * De hersteldatum van dit verzuim ligt binnen 4 weken
						 * van het onderhavige verzuim
						 */
						ketenverzuimduur = ketenverzuimduur + DateOnly.diffDays(oudverzuim.getEinddatumverzuim(),
								oudverzuim.getStartdatumverzuim());

					}
				}
			}
		}

		return ketenverzuimduur;
	}

	private void checkOverlapVerzuimen(VerzuimInfo info, List<VerzuimInfo> alleVerzuimen) throws ValidationException {
		/*
		 * LET OP!! Soortgelijke controle zit ook in VerzuimInfo om de gebruiker
		 * vooraf te kunnen informeren
		 */
		for (VerzuimInfo oudverzuim : alleVerzuimen) {
			if (info.getId().equals(oudverzuim.getId())) {
				/* Skip deze, het is hetzelfde verzuim */
			} else {
				/*
				 * info.Startdatum mag niet tussen startdatum en einddatum ander
				 * verzuim liggen
				 * 
				 * info.Einddatum mag niet tussen startdatum en einddatum ander
				 * verzuim liggen
				 * 
				 * als ander verzuim nog open is, mag info.startdatum EN
				 * info.einddatum niet na de startdatum liggen
				 * 
				 * info.Startdatum mag niet voor de startdatum liggen als de
				 * info.Einddatum na de startdatum ligt
				 */

				if (oudverzuim.getEinddatumverzuim() == null) {
					/* Er is nog een open verzuim */
					checkOverlapOpenverzuim(oudverzuim, info);
				} else {
					/* dit is een afgesloten verzuim */
					checkOverlapAfgeslotenverzuim(oudverzuim, info);
				}
			}
		}
	}

	private void checkOverlapAfgeslotenverzuim(VerzuimInfo oudverzuim, VerzuimInfo info) throws ValidationException {
		if (info.getStartdatumverzuim().after(oudverzuim.getStartdatumverzuim())
				&& info.getStartdatumverzuim().before(oudverzuim.getEinddatumverzuim())) {
			throw applicationException(
					new ValidationException("Startdatum overlapt met afgesloten verzuim"));
		} else {
			if (info.getEinddatumverzuim() != null) {
				if (info.getEinddatumverzuim().after(oudverzuim.getStartdatumverzuim())
						&& info.getEinddatumverzuim().before(oudverzuim.getEinddatumverzuim()))
					throw applicationException(
							new ValidationException("Einddatum overlapt met afgesloten verzuim"));
				if (info.getStartdatumverzuim().before(oudverzuim.getStartdatumverzuim())
						&& info.getEinddatumverzuim().after(oudverzuim.getStartdatumverzuim())) {
					throw applicationException(
							new ValidationException("Einddatum overlapt met afgesloten verzuim"));
				}
			} else {
				/* Startdatum mag niet voor einddatum liggen */
				if (!info.getStartdatumverzuim().before(oudverzuim.getEinddatumverzuim())) {
					/* Dit mag info.start >= vorig.eind*/
				} else {
					throw applicationException(
							new ValidationException("Startdatum overlapt met afgesloten verzuim"));
				}
			}
		}
	}

	private void checkOverlapOpenverzuim(VerzuimInfo oudverzuim, VerzuimInfo info) throws ValidationException {
		if (info.getStartdatumverzuim().after(oudverzuim.getStartdatumverzuim()))
			throw applicationException(new ValidationException("Startdatum overlapt met open verzuim"));
		else if (info.getEinddatumverzuim().after(oudverzuim.getStartdatumverzuim()))
			throw applicationException(new ValidationException("Einddatum overlapt met open verzuim"));
	}

	private void createTodo(ActiviteitInfo ai, Date datum, VerzuimInfo info, int ketenverzuimduur)
			throws VerzuimApplicationException {
		TodoInfo todo = new TodoInfo();
		Date startdatum = datum;
		if (ai.getDeadlinestartpunt() == __meldingsoort.ZIEKMELDING) {
			/*
			 * hier moet de lengte van het ketenverzuim op in mindering worden
			 * gebracht
			 */
			Calendar startcal = Calendar.getInstance();
			startcal.setTime(startdatum);
			startcal.add(Calendar.DAY_OF_MONTH, -ketenverzuimduur);
			startdatum = startcal.getTime();
		}
		todo.setActiviteitId(ai.getId());

		todo.setDeadlinedatum(calcDate(startdatum, ai.getDeadlineperiodesoort(), ai.getDeadlineperiode()));
		todo.setWaarschuwingsdatum(calcDate(todo.getDeadlinedatum(), ai.getDeadlinewaarschuwmomentsoort(),
				-ai.getDeadlinewaarschuwmoment()));
		if (ai.getRepeteeraantal() > 0)
			todo.setHerhalen(true);
		else
			todo.setHerhalen(false);
		todo.setSoort(__soort.AUTOMATISCH);
		todo.setVerzuimId(info.getId());
		todo.setVerzuim(info);
		todo.setVerzuimactiviteitId(null);
		todo.setUser(info.getGebruiker());
		todo.setAanmaakdatum(datum);

		if (ai.getRepeteeraantal() > 0) {
			for (int i = 0; i <= ai.getRepeteeraantal(); i++) {
				this.insertEntity(converter.toEntity(todo, this.getCurrentuser()));
				todo.setDeadlinedatum(
						calcDate(todo.getDeadlinedatum(), ai.getRepeteerperiodesoort(), ai.getRepeteerperiode()));
				todo.setWaarschuwingsdatum(calcDate(todo.getDeadlinedatum(), ai.getDeadlinewaarschuwmomentsoort(),
						-ai.getDeadlinewaarschuwmoment()));
			}
		} else
			this.insertEntity(converter.toEntity(todo, this.getCurrentuser()));

	}

	public void addVerzuimHerstel(VerzuimHerstelInfo info, boolean cleanupTodo)
			throws ValidationException, VerzuimApplicationException {
		VerzuimInfo verzuim;
		List<VerzuimHerstelInfo> herstellen;
		VerzuimHerstelInfo laatsteHerstel;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}

		verzuim = info.getVerzuim();
		if (verzuim == null) {
			verzuim = this.getVerzuimDetails(info.getVerzuimId());
			if (verzuim == null){
				throw new ValidationException("Verzuim niet ingevuld.");
			}
			info.setVerzuim(verzuim);
		}

		if ((verzuim.getDienstverband().getEinddatumcontract() != null) &&
			(DateOnly.after(info.getDatumHerstel(), verzuim.getDienstverband().getEinddatumcontract()))) {
			throw applicationException(
					new ValidationException("Datum verzuim overlapt met einddatum dienstverband!"));
		}

		if (verzuim.getHerstelpercentage().compareTo(new BigDecimal(100)) == 0) {
			throw applicationException(
					new ValidationException("Verzuim is al afgesloten. U kunt geen nieuw herstel toevoegen!"));
		}

		if (DateOnly.before(info.getDatumHerstel(), verzuim.getStartdatumverzuim())) {
			throw applicationException(new ValidationException("Hersteldatum ligt voor startdatum verzuim!"));
		}

		herstellen = this.getVerzuimHerstellenVerzuim(verzuim.getId());
		VerzuimHerstelInfo.sort(herstellen, VerzuimHerstelInfo.__sortcol.HERSTELDATUM);
		if (!herstellen.isEmpty()) {
			laatsteHerstel = herstellen.get(herstellen.size() - 1);
			if (DateOnly.after(laatsteHerstel.getDatumHerstel(), info.getDatumHerstel())
					|| DateOnly.equals(laatsteHerstel.getDatumHerstel(), info.getDatumHerstel())) {
				throw applicationException(
						new ValidationException("Datum herstel overlapt met laatste verzuimherstel"));
			}
		}

		info.setVerzuimId(verzuim.getId());
		this.insertEntity(converter.toEntity(info, this.getCurrentuser()));
		if (info.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
			verzuim.setEinddatumverzuim(info.getDatumHerstel());
			this.updateVerzuim(verzuim);
			if (cleanupTodo) {
				cleanupTodos(verzuim.getId(), verzuim.getEinddatumverzuim(), null, false);
			}
		}

		if (info.getState() == persistencestate.ABSENT) {
			generateTodosHerstel(info, verzuim);
		}
	}

	public VerzuimDocumentInfo addVerzuimDocument(VerzuimDocumentInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Verzuimdocument entity = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(entity);
		return converter.fromEntity(entity);
	}

	public VerzuimActiviteitInfo addVerzuimActiviteit(VerzuimActiviteitInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Verzuimactiviteit entity = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(entity);
		return converter.fromEntity(entity);
	}

	public DocumentTemplateInfo addDocumenttemplate(DocumentTemplateInfo info)
			throws ValidationException, VerzuimApplicationException {
		Documenttemplate entity;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		entity = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(entity);
		return converter.fromEntity(entity);
	}

	public VerzuimMedischekaartInfo addMedischekaart(VerzuimMedischekaartInfo info)
			throws ValidationException, VerzuimApplicationException {
		Verzuimmedischekaart entity;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		entity = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(entity);
		return converter.fromEntity(entity);
	}

	public TodoInfo addTodo(TodoInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		Todo entity = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(entity);
		return converter.fromEntity(entity);
	}

	public void updateVerzuim(VerzuimInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}

		List<VerzuimInfo> alleVerzuimen;
		int cntOpenverzuimen;
		int ketenverzuimduur;

		if (info.getDienstverband() == null) {
			info.setDienstverband(getDienstverband(info.getDienstverbandId()));
		}
		alleVerzuimen = getVerzuimenDienstverband(info.getDienstverbandId());

		try {
			cntOpenverzuimen = countOpenVerzuim(alleVerzuimen, info);
			if ((cntOpenverzuimen > 0) && (info.getEinddatumverzuim() == null))
				throw new ValidationException("Er is nog een open verzuim");

			checkOverlapVerzuimen(info, alleVerzuimen);
			ketenverzuimduur = checkKetenverzuim(info, alleVerzuimen);
		} catch (ValidationException ve) {
			throw applicationException(ve);
		}
		if ((info.getEinddatumverzuim() == null) &&
			(startdatumChanged(info, alleVerzuimen) || vangnettypeChanged(info, alleVerzuimen))) {
			deleteOpenTodos(info.getId());
			generateTodos(info, info.getStartdatumverzuim(), false, ketenverzuimduur);
		}
		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	private void deleteOpenTodos(int id) throws VerzuimApplicationException {
		Query td = em.createQuery(
				"Delete from Todo td where td.verzuim_ID = :vzm_id and td.soort = :soort and td.verzuimactiviteit_ID is null");

		td.setParameter("vzm_id", id);
		td.setParameter("soort", __soort.AUTOMATISCH.getValue());
		executeUpdate(td);
	}

	private boolean startdatumChanged(VerzuimInfo info, List<VerzuimInfo> alleVerzuimen) {
		for (VerzuimInfo oudverzuim : alleVerzuimen) {
			if (oudverzuim.getId().equals(info.getId())
					&& (!oudverzuim.getStartdatumverzuim().equals(info.getStartdatumverzuim()))) {
				return true;
			}
		}
		return false;
	}

	private boolean vangnettypeChanged(VerzuimInfo info, List<VerzuimInfo> alleVerzuimen) {
		for (VerzuimInfo oudverzuim : alleVerzuimen) {
			if (oudverzuim.getId().equals(info.getId())
					&& (!oudverzuim.getVangnettype().equals(info.getVangnettype()))) {
				return true;
			}
		}
		return false;
	}

	private void cleanupTodos(int verzuimId, Date einddatumverzuim, __meldingsoort deadlinestartpunt,
			boolean forcedelete) throws VerzuimApplicationException {
		/*
		 * Opschonen van lijst. Als een verzuim wordt afgesloten worden alle
		 * todos verwijderd die automatisch aangemaakt zijn en die de eigenschap
		 * 'verwijder na herstel' hebben.
		 * 
		 * Als een herstel wordt verwijderd, dan moeten de todos die voor dat
		 * herstel zijn aangemaakt worden verwijderd.
		 */
		Query qwi;
		if (deadlinestartpunt != null) {
			if (forcedelete) {
				qwi = createQuery("Delete from Todo t where t.id in " + "(Select t2.id from Todo t2, Activiteit a "
						+ "where t2.verzuim_ID = :id " + "And a.id = t2.activiteit_ID " + "And t2.soort = :soort "
						+ "And a.deadlinestartpunt = :deadlinestartpunt)" + "And t.aanmaakdatum = :datum");
			} else {
				qwi = createQuery("Delete from Todo t where t.id in " + "(Select t2.id from Todo t2, Activiteit a "
						+ "where t2.verzuim_ID = :id " + "And a.id = t2.activiteit_ID "
						+ "And a.verwijdernaherstel = 1 " + "And t2.soort = :soort "
						+ "And a.deadlinestartpunt = :deadlinestartpunt)" + "And t.aanmaakdatum = :datum");
			}
			qwi.setParameter("deadlinestartpunt", deadlinestartpunt.getValue());
			qwi.setParameter("datum", einddatumverzuim, TemporalType.DATE);
		} else {
			qwi = createQuery("Delete from Todo t where t.id in " + "(Select t2.id from Todo t2, Activiteit a "
					+ "where t2.verzuim_ID = :id " + "And a.id = t2.activiteit_ID " + "And a.verwijdernaherstel = 1 "
					+ "And t2.soort = :soort)");
		}
		qwi.setParameter("soort", __soort.AUTOMATISCH.getValue());
		qwi.setParameter("id", verzuimId);
		executeUpdate(qwi);
	}

	public void updateVerzuimHerstel(VerzuimHerstelInfo info, boolean cleanupTodo)
			throws ValidationException, VerzuimApplicationException {
		VerzuimInfo verzuim;
		VerzuimHerstelInfo originalHerstel = null;
		VerzuimHerstelInfo laatsteHerstel;
		List<VerzuimHerstelInfo> herstellen;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		verzuim = this.getVerzuimDetails(info.getVerzuimId());

		if ((verzuim.getDienstverband().getEinddatumcontract() != null)
				&& DateOnly.after(info.getDatumHerstel(), verzuim.getDienstverband().getEinddatumcontract())) {
			throw applicationException(new ValidationException("Datum verzuim overlapt met einddatum dienstverband!"));
		}

		herstellen = this.getVerzuimHerstellenVerzuim(verzuim.getId());
		VerzuimHerstelInfo.sort(herstellen, VerzuimHerstelInfo.__sortcol.HERSTELDATUM);
		laatsteHerstel = herstellen.get(herstellen.size() - 1);
		if (laatsteHerstel.getDatumHerstel().before(info.getDatumHerstel())) {
			/*
			 * Het gewijzigde herstel heeft een recentere datum dan het laatste
			 * herstel. Als het om hetzelfde herstel gaat is dat ok.
			 */
			if (laatsteHerstel.getId().equals(info.getId())) {
				/* Skip deze, dit is hetzelfde herstel */
			} else {
				/*
				 * Door de datum te wijzigen wordt de volgorde van de herstellen
				 * gewijzigd. Dat mag niet.
				 */
				throw applicationException(
						new ValidationException("Datum verzuim overlapt met laatste verzuimherstel"));
			}
		}

		if (laatsteHerstel.getId().equals(info.getId())) {
			/* het laatste herstel mag altijd gewijzigd worden */
		} else {
			if (info.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0)
				throw applicationException(new ValidationException(
						"U kunt een tussenliggend herstel niet wijzigen in een volledig herstel."));
		}
		Date lastHerstelDate = null;
		for (VerzuimHerstelInfo h : herstellen) {
			if (lastHerstelDate == null) {
				lastHerstelDate = h.getDatumHerstel();
			} else {
				if (h.getId().equals(info.getId())) {
					if (DateOnly.after(info.getDatumHerstel(), lastHerstelDate)) {
						lastHerstelDate = info.getDatumHerstel();
					} else {
						throw applicationException(
								new ValidationException("U kunt niet de volgorde van herstellen wijzigen!"));
					}
				} else {
					if (DateOnly.after(h.getDatumHerstel(), lastHerstelDate)) {
						lastHerstelDate = h.getDatumHerstel();
					} else {
						throw applicationException(
								new ValidationException("U kunt niet de volgorde van herstellen wijzigen!"));
					}
				}
			}
			if (h.getId().equals(info.getId())) {
				originalHerstel = h;
			}
		}
		if (originalHerstel == null)
			throw applicationException(new ValidationException("Wijziging van niet bestaand herstel."));
		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));

		if (info.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
			verzuim.setEinddatumverzuim(info.getDatumHerstel());
			this.updateVerzuim(verzuim);

			/*
			 * de todos van het voormalige deelherstel mogen worden verwijderd
			 */
			cleanupTodos(verzuim.getId(), originalHerstel.getDatumHerstel(), __meldingsoort.GEDEELTELIJKHERSTEL, false);
			if (cleanupTodo) {
				cleanupTodos(verzuim.getId(), verzuim.getEinddatumverzuim(), null, false);
			}
			generateTodosHerstel(info, verzuim);

		} else {
			if (verzuim.getEinddatumverzuim() != null) {
				Date originalEinddatumverzuim = verzuim.getEinddatumverzuim();

				List<VerzuimInfo> alleVerzuimen = getVerzuimenDienstverband(verzuim.getDienstverbandId());
				if (countOpenVerzuim(alleVerzuimen, verzuim) > 0)
					throw applicationException(new ValidationException("Er is al een open verzuim"));

				verzuim.setEinddatumverzuim(null);
				this.updateVerzuim(verzuim);
				if (originalHerstel.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
					cleanupTodos(verzuim.getId(), originalEinddatumverzuim, __meldingsoort.VOLLEDIGHERSTEL, false);
					if (verzuim.getKetenverzuim())
						regenerateTodos(verzuim, null);
					else
						regenerateTodos(verzuim, verzuim.getStartdatumverzuim());
				} else
					cleanupTodos(verzuim.getId(), originalHerstel.getDatumHerstel(), __meldingsoort.GEDEELTELIJKHERSTEL,
							false);
				generateTodosHerstel(info, verzuim);
			}
		}
	}

	public void updateVerzuimDocument(VerzuimDocumentInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public void updateVerzuimActiviteit(VerzuimActiviteitInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public DocumentTemplateInfo updateDocumenttemplate(DocumentTemplateInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
		return getDocumentTemplate(info.getId());
	}

	private DocumentTemplateInfo getDocumentTemplate(Integer id) throws VerzuimApplicationException {
		Query qd;
		qd = createQuery("select dt from Documenttemplate dt where dt.id = :id");
		qd.setParameter("id", id);
		Documenttemplate resultdt = (Documenttemplate) getSingleResult(qd);
		return converter.fromEntity(resultdt);
	}

	public void updateMedischekaart(VerzuimMedischekaartInfo info)
			throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public void updateTodo(TodoInfo info) throws ValidationException, VerzuimApplicationException {
		Verzuimactiviteit verzuimactiviteit;
		VerzuimActiviteitInfo vai;
		VerzuimInfo verzuim;
		Integer werkgeverid;
		activiteitEJB.setCurrentuser(getCurrentuser());
		try {
			info.validate();
		} catch (ValidationException e) {
			throw applicationException(e);
		}
		verzuim = this.getVerzuimDetails(info.getVerzuimId());
		werkgeverid = verzuim.getDienstverband().getWerkgeverId();
		vai = info.getVerzuimActiviteit();
		if (vai != null) {
			if (vai.getState() == persistencestate.ABSENT) {
				verzuimactiviteit = converter.toEntity(vai, this.getCurrentuser());
				this.insertEntity(verzuimactiviteit);
				info.setVerzuimactiviteitId(verzuimactiviteit.getId());
				List<ActiviteitInfo> lai = activiteitEJB.allActiviteiten(werkgeverid);
				checkPlanAfter(lai, verzuim, vai);
			}
		} else
			info.setVerzuimactiviteitId(null);

		this.updateEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	private void checkPlanAfter(List<ActiviteitInfo> lai, VerzuimInfo verzuim, VerzuimActiviteitInfo vai)
			throws VerzuimApplicationException {
		for (ActiviteitInfo ai : lai) {
			if ((ai.getPlannaactiviteit() != null) &&
				(ai.getPlannaactiviteit() == vai.getActiviteitId())){
				createTodo(ai, vai.getDatumactiviteit(), verzuim, 0);
			}
		}
	}

	public void deleteVerzuimHerstel(VerzuimHerstelInfo info) throws VerzuimApplicationException, ValidationException {
		VerzuimInfo verzuim;
		List<VerzuimInfo> alleVerzuimen;
		BigDecimal herstelpercentage;
		BigDecimal oorspronkelijkpercentage;

		oorspronkelijkpercentage = info.getPercentageHerstel();

		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
		verzuim = this.getVerzuimDetails(info.getVerzuimId());
		/*
		 * Wat is het nieuwe verzuimpercentage? Het nieuwe percentage = 100% =>
		 * Dan is er een tussenliggend herstel verwijderd. Voor de zekerheid
		 * wordt de einddatum verzuim gezet als deze nog leeg is.
		 * 
		 * Het nieuwe percentage < 100%. Dan kan het nog steeds een
		 * tussenliggend herstel zijn of een volledig herstel.
		 */
		herstelpercentage = verzuim.getHerstelpercentage();
		/*
		 * De TODOs gekoppeld aan het (deel)herstel worden verwijderd
		 */
		if (oorspronkelijkpercentage.compareTo(new BigDecimal(100)) == 0) {
			/*
			 * Het verwijderde herstel was een volledig herstel
			 */
			alleVerzuimen = getVerzuimenDienstverband(verzuim.getDienstverbandId());
			if (countOpenVerzuim(alleVerzuimen, verzuim) > 0)
				throw applicationException(new ValidationException("Er is al een open verzuim"));

			cleanupTodos(verzuim.getId(), verzuim.getEinddatumverzuim(), __meldingsoort.VOLLEDIGHERSTEL, true);

		} else {
			/*
			 * Het verwijderde herstel was een deelherstel
			 */
			cleanupTodos(verzuim.getId(), info.getDatumHerstel(), __meldingsoort.GEDEELTELIJKHERSTEL, false);
		}

		if (herstelpercentage.compareTo(new BigDecimal(100)) == 0) {
			/*
			 * Dit kan eigenlijk niet voorkomen. Het verwijderen van een herstel
			 * kan niet resulteren in een afgesloten verzuim. Maar toch...
			 */
			if (verzuim.getEinddatumverzuim() == null) {
				verzuim.setEinddatumverzuim(info.getDatumHerstel());
				this.updateEntity(converter.toEntity(verzuim, this.getCurrentuser()));
			}
		} else {
			if (verzuim.getEinddatumverzuim() != null) {
				verzuim.setEinddatumverzuim(null);
				this.updateEntity(converter.toEntity(verzuim, this.getCurrentuser()));
			}
		}

		if (info.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
			/*
			 * het verwijderde herstel is een 100% herstel, het verzuim moet dan
			 * weer herleven. TODOs die aangemaakt zijn agv afsluiten verzuim,
			 * moeten worden verwijderd.
			 */
			cleanupTodos(verzuim.getId(), info.getDatumHerstel(), __meldingsoort.VOLLEDIGHERSTEL, true);
			if (verzuim.getKetenverzuim())
				regenerateTodos(verzuim, null);
			else
				regenerateTodos(verzuim, verzuim.getStartdatumverzuim());
		}
	}

	public void deleteVerzuimActiviteit(VerzuimActiviteitInfo info) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public void deleteVerzuimDocument(VerzuimDocumentInfo info) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public void deleteDocumenttemplate(DocumentTemplateInfo info) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public void deleteMedischekaart(VerzuimMedischekaartInfo info) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

	public void deleteTodo(TodoInfo info) throws VerzuimApplicationException {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
	}

}
