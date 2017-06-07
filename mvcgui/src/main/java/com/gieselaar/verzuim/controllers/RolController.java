package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.GebruikerModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.RolDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.RolInfo;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class RolController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __rolfields {
		UNKNOWN(-1), ROL(0), OMSCHRIJVING(1);
		private int value;

		__rolfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __rolfields parse(int type) {
			__rolfields field = UNKNOWN; // Default
			for (__rolfields item : __rolfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private GebruikerModel model;
	public RolController(LoginSessionRemote session) {
		super(new GebruikerModel(session), null);
		this.model = (GebruikerModel)getModel();
	}

	public void selectRollen() throws VerzuimApplicationException {
		model.selectRollen();
		model.selectApplicatiefuncties();
	}
	private RolInfo createNewRol() {
		RolInfo info = new RolInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		RolInfo info = (RolInfo)data;
		try {
			info.validate();
			model.addRol(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan rol niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		RolInfo info = (RolInfo)data;
		try {
			info.validate();
			model.saveRol(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan rol niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		RolInfo info = (RolInfo) data;
		AbstractDetail form = super.createDetailForm(info, RolDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		RolInfo info = createNewRol();
		AbstractDetail form = super.createDetailForm(info, RolDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		RolInfo info = (RolInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteRol(info);
	}

	public void getTableModel(List<RolInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (RolInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, RolInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__rolfields val = __rolfields.parse(colsinview.get(i));
			switch (val) {
			case ROL:
				colsinmodel.add(i, wfi.getRolid());
				break;
			case OMSCHRIJVING:
				colsinmodel.add(i, wfi.getOmschrijving());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(RolInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze rol wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		RolInfo info = (RolInfo)data;
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

	public List<RolInfo> getRollenList() {
		return model.getRollenList();
	}

	public List<ApplicatieFunctieInfo> getApplicatieFuncties() {
		return model.getApplicatiefunctiesList();
	}
}
