package com.gieselaar.verzuim.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuuritemDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;

public class FactuuritemController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __factuuritemfields {
		UNKNOWN(-1), WERKGEVER(1), DATUM(2), CATEGORIE(3), BEDRAG(4), 
		GEFACTUREERD(5);
		private int value;

		__factuuritemfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __factuuritemfields parse(int type) {
			__factuuritemfields field = UNKNOWN; // Default
			for (__factuuritemfields item : __factuuritemfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private FactuurModel model;
	private WerkgeverModel werkgevermodel;
	
	private List<WerkgeverInfo> werkgevers;
	private List<FactuurcategorieInfo> categorien;
	private ReportController reportcontroller;
	private Integer selectedWerkgeverid = -1;

	public FactuuritemController(LoginSessionRemote session) {
		super(new FactuurModel(session), null);
		this.model = (FactuurModel)getModel();
		werkgevermodel = new WerkgeverModel(session);
		try {
			model.selectFactuurcategorien();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		categorien = model.getFactuurcategorienList();
	}

	public void selectFactuuritems(Date startdate, Date enddate) throws VerzuimApplicationException, ValidationException {
		model.selectFactuuritems(startdate, enddate);
	}

	private FactuuritemInfo createNewFactuuritem() {
		FactuuritemInfo info = new FactuuritemInfo();
		info.setWerkgeverid(-1);
		info.setDatum(new Date());
		info.setFactuurcategorieid(-1);
		info.setBedrag(BigDecimal.ZERO);
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		FactuuritemInfo info = (FactuuritemInfo)data;
		try {
			info.validate();
			model.addFactuuritem(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuuritem niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		FactuuritemInfo info = (FactuuritemInfo)data;
		try {
			info.validate();
			model.saveFactuuritem(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuuritem niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		FactuuritemInfo info = (FactuuritemInfo) data;
		AbstractDetail form = super.createDetailForm(info, FactuuritemDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		FactuuritemInfo info = createNewFactuuritem();
		AbstractDetail form = super.createDetailForm(info, FactuuritemDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		FactuuritemInfo info = (FactuuritemInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteFactuuritem(info);
	}

	public void getTableModel(List<FactuuritemInfo> factuuritems, ColorTableModel tblmodel, List<Integer> colsinview) {
		boolean showrow;
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		werkgevers = this.getMaincontroller().getWerkgevers();
		for (FactuuritemInfo info : factuuritems) {
			List<Object> colsinmodel = new ArrayList<>();

			showrow = true;
			if (selectedWerkgeverid != -1 && !info.getWerkgeverid().equals(selectedWerkgeverid)){
				showrow = false;
			}
			if (showrow){
				setColumnValues(colsinmodel, info, colsinview);
				tblmodel.addRow(colsinmodel, info);
			}
		}
	}
	private String getWerkgeverNaam(Integer werkgeverid){
		for (WerkgeverInfo wgr:werkgevers){
			if (wgr.getId().equals(werkgeverid))
				return wgr.getNaam();
		}
		return "?";
	}
	private String getCategorie(int categorieid) {
		for (FactuurcategorieInfo categorie: categorien){
			if (categorie.getId().equals(categorieid))
				return categorie.getOmschrijving();
		}
		return "?";
	}


	private void setColumnValues(List<Object> colsinmodel, FactuuritemInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__factuuritemfields val = __factuuritemfields.parse(colsinview.get(i));
			switch (val) {
			case BEDRAG:
				colsinmodel.add(i, wfi.getBedrag());
				break;
			case DATUM:
				colsinmodel.add(i, wfi.getDatum());
				break;
			case CATEGORIE:
				colsinmodel.add(i, getCategorie(wfi.getFactuurcategorieid()));
				break;
			case GEFACTUREERD:
				break;
			case WERKGEVER:
				colsinmodel.add(i, getWerkgeverNaam(wfi.getWerkgeverid()));
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(FactuuritemInfo wnr) {
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
		FactuuritemInfo info = (FactuuritemInfo)data;
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

	public List<FactuuritemInfo> getFactuuritemList() {
		return model.getFactuuritemList();
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
		FactuuritemInfo factuuritem = (FactuuritemInfo) view.collectData();
		FactuuritemInfo newfii = new FactuuritemInfo();
		newfii.setWerkgeverid(factuuritem.getWerkgeverid());
		newfii.setDatum(factuuritem.getDatum());
		newfii.setHoldingid(factuuritem.getHoldingid());
		newfii.setFactuurcategorieid(factuuritem.getFactuurcategorieid());
		newfii.setBedrag(BigDecimal.ZERO);
		view.setData(newfii);
	}
	public VerzuimComboBoxModel getComboModelKoppen() {
		try {
			model.selectFactuurkoppen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		List<FactuurkopInfo> koppen = model.getFactuurkoppenList();

		VerzuimComboBoxModel kopmodel = new VerzuimComboBoxModel(this);
		kopmodel.addElement(new TypeEntry(-1,"[]"));
		for (FactuurkopInfo kop:koppen) {
			kopmodel.addElement(new TypeEntry(kop.getId(), kop.getOmschrijving()));
		}
		return kopmodel;
	}
	public VerzuimComboBoxModel getComboModelCategorien(Integer kopid) {
		try {
			model.selectFactuurcategorien();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		List<FactuurcategorieInfo> categorien = model.getFactuurcategorienList();

		VerzuimComboBoxModel categoriemodel = new VerzuimComboBoxModel(this);
		categoriemodel.addElement(new TypeEntry(-1,"[]"));
		for (FactuurcategorieInfo kop:categorien) {
			if (kop.getFactuurkopid().equals(kopid)){
				categoriemodel.addElement(new TypeEntry(kop.getId(), kop.getOmschrijving()));
			}
		}
		return categoriemodel;
	}
	public void setSelectedWerkgever(Integer id) {
		selectedWerkgeverid = id;
		for (ControllerEventListener l : views) {
			l.refreshTable();
		}
	}

	public Integer getFactuurkopforCategorie(int factuurcategorieid) {
		for (FactuurcategorieInfo cat: categorien){
			if (cat.getId().equals(factuurcategorieid)){
				return cat.getFactuurkopid();
			}
		}
		return -1;
	}
}
