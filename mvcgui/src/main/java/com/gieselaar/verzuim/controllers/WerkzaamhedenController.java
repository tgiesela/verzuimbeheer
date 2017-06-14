package com.gieselaar.verzuim.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkzaamhedenModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.WerkzaamhedenDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__huisbezoekurgentie;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__verzuimsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

public class WerkzaamhedenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __werkzaamhedenfields {
		UNKNOWN(-1), WERKGEVER(1), DATUM(2), NAAM(3), PLAATS(4), 
		DOOR(5), ACTIVITEIT(6), UREN(7), KM(8), KOSTEN(9);
		private int value;

		__werkzaamhedenfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __werkzaamhedenfields parse(int type) {
			__werkzaamhedenfields field = UNKNOWN; // Default
			for (__werkzaamhedenfields item : __werkzaamhedenfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkzaamhedenModel model;
	private WerkgeverModel werkgevermodel;
	
	private List<WerkgeverInfo> werkgevers;
	private List<GebruikerInfo> gebruikers;
	private ReportController reportcontroller;

	public WerkzaamhedenController(LoginSessionRemote session) {
		super(new WerkzaamhedenModel(session), null);
		this.model = (WerkzaamhedenModel)getModel();
		werkgevermodel = new WerkgeverModel(session);
	}

	public void selectWerkzaamheden(Integer user, Date startdate, Date enddate) throws VerzuimApplicationException, ValidationException {
		model.selectWerkzaamheden(user, startdate, enddate);
	}
	public void selectWerkzaamhedenHolding(Integer user, Date startdate, Date enddate, Integer holdingid) throws VerzuimApplicationException, ValidationException {
		model.selectWerkzaamhedenHolding(user, startdate, enddate, holdingid);
	}
	public void selectWerkzaamhedenWerkgever(Integer user, Date startdate, Date enddate, Integer werkgeverid) throws VerzuimApplicationException, ValidationException {
		model.selectWerkzaamhedenWerkgever(user, startdate, enddate, werkgeverid);
	}

	private WerkzaamhedenInfo createNewWerkzaamheid() {
		WerkzaamhedenInfo info = new WerkzaamhedenInfo();
		info.setUserid(getMaincontroller().getGebruiker().getId());
		info.setUren(BigDecimal.ZERO);
		info.setAantalkm(BigDecimal.ZERO);
		info.setOverigekosten(BigDecimal.ZERO);
		info.setDatum(new Date());
		info.setSoortwerkzaamheden(__werkzaamhedensoort.SECRETARIAAT);
		info.setGeslacht(__geslacht.ONBEKEND);
		info.setSoortverzuim(__verzuimsoort.ONBEKEND);
		info.setUrgentie(__huisbezoekurgentie.NVT);
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		WerkzaamhedenInfo info = (WerkzaamhedenInfo)data;
		try {
			info.validate();
			model.addWerkzaamheid(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan werkzaamheden niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		WerkzaamhedenInfo info = (WerkzaamhedenInfo)data;
		try {
			info.validate();
			model.saveWerkzaamheid(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan werkzaamheden niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		WerkzaamhedenInfo info = (WerkzaamhedenInfo) data;
		AbstractDetail form = super.createDetailForm(info, WerkzaamhedenDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		WerkzaamhedenInfo info = createNewWerkzaamheid();
		AbstractDetail form = super.createDetailForm(info, WerkzaamhedenDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		WerkzaamhedenInfo info = (WerkzaamhedenInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteWerkzaamheid(info);
	}

	public void getTableModel(List<WerkzaamhedenInfo> werkzaamheden, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		werkgevers = this.getMaincontroller().getWerkgevers();
		gebruikers = this.getMaincontroller().getGebruikers();
		for (WerkzaamhedenInfo info : werkzaamheden) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}
	private String getWerkgeverNaam(Integer werkgeverid){
		for (WerkgeverInfo wgr:werkgevers){
			if (wgr.getId().equals(werkgeverid))
				return wgr.getNaam();
		}
		return "?";
	}
	private String getGebruikerNaam(int userid) {
		for (GebruikerInfo user:gebruikers){
			if (user.getId().equals(userid))
				return user.getAchternaam();
		}
		return "?";
	}


	private void setColumnValues(List<Object> colsinmodel, WerkzaamhedenInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__werkzaamhedenfields val = __werkzaamhedenfields.parse(colsinview.get(i));
			switch (val) {
			case ACTIVITEIT:
				colsinmodel.add(i, wfi.getSoortwerkzaamheden().toString());
				break;
			case DATUM:
				colsinmodel.add(i, wfi.getDatum());
				break;
			case DOOR:
				colsinmodel.add(i, getGebruikerNaam(wfi.getUserid()));
				break;
			case KM:
				colsinmodel.add(i, wfi.getAantalkm());
				break;
			case KOSTEN:
				colsinmodel.add(i, wfi.getOverigekosten());
				break;
			case NAAM:
				colsinmodel.add(i, wfi.getPersoon());
				break;
			case PLAATS:
				colsinmodel.add(i, wfi.getWoonplaats());
				break;
			case UREN:
				colsinmodel.add(i, wfi.getUren());
				break;
			case WERKGEVER:
				colsinmodel.add(i, getWerkgeverNaam(wfi.getWerkgeverid()));
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(WerkzaamhedenInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u de regel wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		WerkzaamhedenInfo info = (WerkzaamhedenInfo)data;
		return confirmDelete(info);
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public List<WerkzaamhedenInfo> getWerkzaamhedenList() {
		return model.getWerkzaamhedenList();
	}

	public ReportController getReportController() {
		if (reportcontroller == null) {
			reportcontroller = new ReportController(this.model.getSession()){
				private static final long serialVersionUID = 1L;

				@Override
				public void rowSelected(int selectedRow, Object data) {
					for (ControllerEventListener l: views){
						l.rowSelected(selectedRow, data);
					}
				}
			};
			reportcontroller.setDesktoppane(getDesktoppane());
			reportcontroller.setMaincontroller(this.getMaincontroller());
		}
		return reportcontroller;
	}

	public boolean isVasttariefHuisbezoeken(Date peildatum, Integer holdingid, Integer werkgeverid) throws VerzuimApplicationException {
		boolean vasttariefhuisbezoeken;
		vasttariefhuisbezoeken = werkgevermodel.isVasttariefHuisbezoeken(peildatum, holdingid, werkgeverid);
		return vasttariefhuisbezoeken;
	}

	/** 
	 * 
	 * Function called when item is saved and form is not closed.
	 * This can be used to show a new item.
	 * 
	 */
	@Override 
	public void dataSaved(AbstractDetail view){
		WerkzaamhedenInfo werkzaamheid = (WerkzaamhedenInfo) view.collectData();
		WerkzaamhedenInfo newwzi = new WerkzaamhedenInfo();
		newwzi.setWerkgeverid(werkzaamheid.getWerkgeverid());
		newwzi.setDatum(werkzaamheid.getDatum());
		newwzi.setHoldingid(werkzaamheid.getHoldingid());
		newwzi.setSoortwerkzaamheden(werkzaamheid
				.getSoortwerkzaamheden());
		newwzi.setUserid(werkzaamheid.getUserid());
		newwzi.setOverigekosten(BigDecimal.ZERO);
		newwzi.setAantalkm(BigDecimal.ZERO);
		newwzi.setUren(BigDecimal.ZERO);
		newwzi.setGeslacht(__geslacht.ONBEKEND);
		newwzi.setSoortverzuim(__verzuimsoort.ONBEKEND);
		newwzi.setUrgentie(__huisbezoekurgentie.NVT);
		view.setData(newwzi);
	}
	public VerzuimComboBoxModel getComboModelFilialen(Integer werkgeverid) {
		List<OeInfo> oes;
		try {
			werkgevermodel.selectOes();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		oes = werkgevermodel.getOeList();
		OeInfo.sort(oes, OeInfo.__sortcol.NAAM);

		VerzuimComboBoxModel oemodel = new VerzuimComboBoxModel(this);
		oemodel.addElement(new TypeEntry(-1,"[]"));
		if (werkgeverid == null){
			/* Geen werkgever geselecteerd */
			return oemodel;
		}
		if (oes != null && !oes.isEmpty() && werkgeverid > 0) {
			if (werkgeverid != null)
				oemodel.setId(-1);
			for (OeInfo oeniveau:oes) {
				if (oeniveau.getWerkgeverId() != null &&
				   (oeniveau.getOeniveau().getOeniveau().equals(1) && 
					oeniveau.getWerkgeverId().equals(werkgeverid))) {
					/*
					 * Alle OE's onder dit niveau worden als filiaal
					 * van de opgegeven werkgever beschouwd.
					 */
					for (OeInfo filiaal : oes) {
						if (filiaal.getParentoeId() != null) {
							if (filiaal.getParentoeId().equals(oeniveau.getId())) {
								oemodel.addElement(new TypeEntry(filiaal.getId(), filiaal.getNaam()));
							}
						}
					}
				}
			}
		}
		return oemodel;
	}
	public VerzuimComboBoxModel getComboModelGebruiker(){
		VerzuimComboBoxModel gebruikermodel;
		Integer currentgebruikerid = getMaincontroller().getGebruiker().getId();
		gebruikers = getMaincontroller().getGebruikers();
		boolean allUsers = getMaincontroller().isAuthorised(__applicatiefunctie.WERKZAAMHEDENALLUSERS);

		gebruikermodel = new VerzuimComboBoxModel(this);
		if (allUsers){
			gebruikermodel.addElement(new TypeEntry(-1, "[Alle]"));
		}
		for (GebruikerInfo g : gebruikers) {
			String naam = g.getVoornaam() + " " + g.getTussenvoegsel() + " "
					+ g.getAchternaam();
			if (allUsers){
				gebruikermodel.addElement(new TypeEntry(g.getId(), naam));
			}else{
				if (g.getId().equals(currentgebruikerid)){
					gebruikermodel.addElement(new TypeEntry(g.getId(), naam));
				}
			}
		}
		return gebruikermodel;
	}
}
