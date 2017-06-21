package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.AfdelingDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class AfdelingController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __afdelingfields {
		UNKNOWN(-1), NAAM(0), CONTACTPERSOON(1);
		private int value;

		__afdelingfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __afdelingfields parse(int type) {
			__afdelingfields field = UNKNOWN; // Default
			for (__afdelingfields item : __afdelingfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private WerkgeverModel model;
	private WerkgeverInfo selectedWerkgever;
	
	public AfdelingController(LoginSessionRemote session) {
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel) getModel();
	}

	public void selectAfdelingen() {
		try {
			model.selectAfdelingen(this.selectedWerkgever);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	private AfdelingInfo createNewAfdeling() {
		AfdelingInfo afdeling = new AfdelingInfo();
		afdeling.setWerkgeverId(this.selectedWerkgever.getId());
		return afdeling;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		AfdelingInfo afdeling = (AfdelingInfo)data;
		try {
			afdeling.validate();
			model.addAfdeling(afdeling);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan afdeling niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		AfdelingInfo afdeling = (AfdelingInfo)data;
		try {
			afdeling.validate();
			model.saveAfdeling(afdeling);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan afdeling niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		AbstractDetail form = super.createDetailForm(data, AfdelingDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		AfdelingInfo todo = createNewAfdeling();
		AbstractDetail form = super.createDetailForm(todo, AfdelingDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		AfdelingInfo afdeling = (AfdelingInfo) data;
		afdeling.setAction(persistenceaction.DELETE);
		afdeling.setState(persistencestate.EXISTS);
		afdeling.setId(afdeling.getId());
		model.deleteAfdeling(afdeling);

	}

	public void getTableModel(List<AfdelingInfo> afdelingen, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in todos is already filtered
		 */

		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (AfdelingInfo afdeling : afdelingen) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, afdeling, colsinview);
			tblmodel.addRow(colsinmodel, afdeling);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, AfdelingInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__afdelingfields val = __afdelingfields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			case CONTACTPERSOON:
				if (wfi.getContactpersoon() != null){
					colsinmodel.add(i, wfi.getContactpersoon().getAchternaam());
				}else{
					colsinmodel.add(i, "");
				}
				break;
			default:
				break;
			}
		}
	}

	public List<AfdelingInfo> getAfdelingList() {
		return model.getAfdelingen();
	}

	private boolean confirmDelete(AfdelingInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u de Afdeling wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		AfdelingInfo afdeling;
		afdeling = (AfdelingInfo) data;
		return confirmDelete(afdeling);
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public void setWerkgever(WerkgeverInfo werkgever) {
		selectedWerkgever = werkgever;
		
	}

	public void setAfdelingen(List<AfdelingInfo> afdelingen) {
		// Trick to bypass query in model when werkgever has not been created yet
		model.setAfdelingen(afdelingen);

	}
}
