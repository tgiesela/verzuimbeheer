package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.PakketModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.PakketDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.PakketInfo;

public class PakketController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __pakketfields {
		UNKNOWN(-1), PAKKETNAAM(0);
		private int value;

		__pakketfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __pakketfields parse(int type) {
			__pakketfields field = UNKNOWN; // Default
			for (__pakketfields item : __pakketfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private PakketModel model;
	
	public PakketController(LoginSessionRemote session) {
		super(new PakketModel(session), null);
		this.model = (PakketModel)getModel();
	}

	public void selectPakketten() throws VerzuimApplicationException {
		model.selectPakketten();
	}

	private PakketInfo createNewPakket() {
		PakketInfo info = new PakketInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		PakketInfo info = (PakketInfo)data;
		try {
			info.validate();
			model.addPakket(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan pakket niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		PakketInfo info = (PakketInfo)data;
		try {
			info.validate();
			model.savePakket(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan pakket niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		PakketInfo info = (PakketInfo) data;
		AbstractDetail form = super.createDetailForm(info, PakketDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		PakketInfo info = createNewPakket();
		AbstractDetail form = super.createDetailForm(info, PakketDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		PakketInfo info = (PakketInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deletePakket(info);
	}

	public void getTableModel(List<PakketInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (PakketInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, PakketInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__pakketfields val = __pakketfields.parse(colsinview.get(i));
			switch (val) {
			case PAKKETNAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(PakketInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit pakket wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		PakketInfo info = (PakketInfo)data;
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

	public List<PakketInfo> getPakketList() {
		return model.getPakkettenList();
	}
}
