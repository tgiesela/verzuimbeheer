package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.PakketModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.ActiviteitDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;

public class ActiviteitController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __activiteitfields {
		UNKNOWN(-1), CODE(0), NAAM(1);
		private int value;

		__activiteitfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __activiteitfields parse(int type) {
			__activiteitfields field = UNKNOWN; // Default
			for (__activiteitfields item : __activiteitfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private PakketModel model;
	
	public ActiviteitController(LoginSessionRemote session) {
		super(new PakketModel(session), null);
		this.model = (PakketModel)getModel();
	}

	public void selectActiviteiten() throws VerzuimApplicationException {
		model.selectActiviteiten();
	}

	private ActiviteitInfo createNewActiviteit() {
		ActiviteitInfo info = new ActiviteitInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		ActiviteitInfo info = (ActiviteitInfo)data;
		try {
			info.validate();
			model.addActiviteit(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan activiteit niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		ActiviteitInfo info = (ActiviteitInfo)data;
		try {
			info.validate();
			model.saveActiviteit(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan activiteit niet geslaagd.");
		}
	}
	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		ActiviteitInfo info = (ActiviteitInfo) data;
		AbstractDetail form = super.createDetailForm(info, ActiviteitDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		ActiviteitInfo info = createNewActiviteit();
		AbstractDetail form = super.createDetailForm(info, ActiviteitDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		ActiviteitInfo info = (ActiviteitInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteActiviteit(info);
	}

	public void getTableModel(List<ActiviteitInfo> activiteiten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (ActiviteitInfo info : activiteiten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, ActiviteitInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__activiteitfields val = __activiteitfields.parse(colsinview.get(i));
			switch (val) {
			case CODE:
				colsinmodel.add(i, wfi.getOmschrijving());
				break;
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(ActiviteitInfo wnr) {
		int rslt = JOptionPane.showConfirmDialog(null, 
				"Weet u zeker dat u de activiteit wilt verwijderen? \n" +
				"Hiermee worden alle eraan gekoppelde TODO's en verrichtingen ook verwijderd!","",JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		ActiviteitInfo info = (ActiviteitInfo)data;
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

	public List<ActiviteitInfo> getActiviteitList() {
		return model.getActiviteitenList();
	}

	public void updateComboModelActiviteiten(VerzuimComboBoxModel activiteitenmodel) {
		activiteitenmodel.removeAllElements();
		List<ActiviteitInfo> activiteiten = model.getActiviteitenList();
		activiteitenmodel.addElement(new TypeEntry(-1, "[]"));
		ActiviteitInfo.sort(activiteiten, ActiviteitInfo.__sortcol.NAAM);
		for (ActiviteitInfo w : activiteiten) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			activiteitenmodel.addElement(wg);
		}
	}
}
