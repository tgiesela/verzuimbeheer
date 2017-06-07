package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.CascodeModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.CascodeDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;

public class CascodeController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __cascodefields {
		UNKNOWN(-1), NAAM(0), OMSCHRIJVING(1);
		private int value;

		__cascodefields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __cascodefields parse(int type) {
			__cascodefields field = UNKNOWN; // Default
			for (__cascodefields item : __cascodefields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private CascodeModel model;
	public CascodeController(LoginSessionRemote session) {
		super(new CascodeModel(session), null);
		this.model = (CascodeModel)getModel();
	}

	public void selectCascodes(Integer selectedgroep) throws VerzuimApplicationException {
		if (selectedgroep == null || selectedgroep == -1){
			model.selectCascodes();
		}else{
			model.selectCascodes(selectedgroep);
		}
	}
	public void selectCascodegroepen() throws VerzuimApplicationException {
		model.selectCascodegroepen();
	}

	private CascodeInfo createNewCascode() {
		CascodeInfo info = new CascodeInfo();
		info.setActief(true);
		info.setCascode("");
		info.setVangnettype(__vangnettype.NVT);
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		CascodeInfo info = (CascodeInfo)data;
		try {
			info.validate();
			model.addCascode(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan Cascode niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		CascodeInfo info = (CascodeInfo)data;
		try {
			info.validate();
			model.saveCascode(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan Cascode niet geslaagd.");
		}
	}
	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		CascodeInfo info = (CascodeInfo) data;
		AbstractDetail form = super.createDetailForm(info, CascodeDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		CascodeInfo info = createNewCascode();
		AbstractDetail form = super.createDetailForm(info, CascodeDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		CascodeInfo info = (CascodeInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteCascode(info);
	}

	public void getTableModel(List<CascodeInfo> cascodes, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		if (cascodes != null){
			for (CascodeInfo info : cascodes) {
				List<Object> colsinmodel = new ArrayList<>();
	
				setColumnValues(colsinmodel, info, colsinview);
				tblmodel.addRow(colsinmodel, info);
			}
		}
	}
	public void updateComboModelCascodegroepen(VerzuimComboBoxModel cascodemodel) {
		/* Just a utility function to update ComboBoxModel */
		cascodemodel.removeAllElements();
		List<CascodeGroepInfo> groepen = model.getCascodegroepenList();
		for (CascodeGroepInfo cascode : groepen) {
			TypeEntry te = new TypeEntry(cascode.getId(), cascode.getNaam());
			cascodemodel.addElement(te);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, CascodeInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__cascodefields val = __cascodefields.parse(colsinview.get(i));
			switch (val) {
			case OMSCHRIJVING:
				colsinmodel.add(i, wfi.getOmschrijving());
				break;
			case NAAM:
				colsinmodel.add(i, wfi.getCascode());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(CascodeInfo wnr) {
		int rslt = JOptionPane.showConfirmDialog(null, 
				"Weet u zeker dat u de Cascode wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		CascodeInfo info = (CascodeInfo)data;
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

	public List<CascodeInfo> getCascodesList() {
		return model.getCascodesList();
	}
	public List<CascodeGroepInfo> getCascodegroepenList() {
		return model.getCascodegroepenList();
	}
}
