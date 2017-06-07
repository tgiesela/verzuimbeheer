package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuurList;
import com.gieselaar.verzuim.views.FactuurkopDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class FactuurkopController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __factuurkopfields {
		UNKNOWN(-1), OMSCHRIJVING(0), PRIORITEIT(1);
		private int value;

		__factuurkopfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __factuurkopfields parse(int type) {
			__factuurkopfields field = UNKNOWN; // Default
			for (__factuurkopfields item : __factuurkopfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private FactuurModel model;

	public FactuurkopController(LoginSessionRemote session) {
		super(new FactuurModel(session), null);
		this.model = (FactuurModel) getModel();
	}

	public void selectFactuurkoppen() throws VerzuimApplicationException {
		this.model.selectFactuurkoppen();
	}

	private FactuurkopInfo createNewFactuurkop() {
		FactuurkopInfo info = new FactuurkopInfo();
		return info;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		FactuurkopInfo info = (FactuurkopInfo) data;
		try {
			info.validate();
			model.addFactuurkop(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuurkop niet geslaagd.");
		}
	}

	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		FactuurkopInfo info = (FactuurkopInfo) data;
		try {
			info.validate();
			model.saveFactuurkop(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuurkop niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase) data))
			return;
		FactuurkopInfo info = (FactuurkopInfo) data;
		AbstractDetail form = super.createDetailForm(info, FactuurkopDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		FactuurkopInfo info = createNewFactuurkop();
		AbstractDetail form = super.createDetailForm(info, FactuurkopDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}

	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase) data)) {
			return;
		}
		FactuurkopInfo info = (FactuurkopInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteFactuurkop(info);
	}

	public void getTableModel(List<FactuurkopInfo> facturen, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		
		for (FactuurkopInfo info : facturen) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, FactuurkopInfo fti, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__factuurkopfields val = __factuurkopfields.parse(colsinview.get(i));
			switch (val) {
			case OMSCHRIJVING:
				colsinmodel.add(i, fti.getOmschrijving());
				break;
			case PRIORITEIT:
				Integer prio = fti.getPrioriteit();
				colsinmodel.add(i,prio.toString());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(FactuurkopInfo info) {
		int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u de regel wilt verwijderen?", "",
				JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		FactuurkopInfo info = (FactuurkopInfo) data;
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

	public void setFactuurModel(FactuurModel FactuurModel) {
		this.model = FactuurModel;
	}

	public List<FactuurkopInfo> getFactuurkoppenList() {
		return model.getFactuurkoppenList();
	}
	@Override 
	public void rowSelected(int selectedRow, Object data) {
		for (ControllerEventListener l : views) {
			if (l instanceof FactuurList){
				((FactuurList)l).setRowSelected();
			}
		}
	}
}
