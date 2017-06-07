package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuurList;
import com.gieselaar.verzuim.views.FactuurcategorieDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class FactuurcategorieController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __factuurcategoriefields {
		UNKNOWN(-1), OMSCHRIJVING(0), BTWTARIEFSOORT(1), FACTUURKOP(2), OMZET(3);
		private int value;

		__factuurcategoriefields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __factuurcategoriefields parse(int type) {
			__factuurcategoriefields field = UNKNOWN; // Default
			for (__factuurcategoriefields item : __factuurcategoriefields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private FactuurModel model;
	private List<FactuurkopInfo> factuurkoppen;

	public FactuurcategorieController(LoginSessionRemote session) {
		super(new FactuurModel(session), null);
		this.model = (FactuurModel) getModel();
		try {
			model.selectFactuurkoppen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public void selectFactuurcategorien() throws VerzuimApplicationException {
		this.model.selectFactuurcategorien();
	}

	private FactuurcategorieInfo createNewFactuurkop() {
		FactuurcategorieInfo info = new FactuurcategorieInfo();
		return info;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		FactuurcategorieInfo info = (FactuurcategorieInfo) data;
		try {
			info.validate();
			model.addFactuurcategorie(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuurkop niet geslaagd.");
		}
	}

	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		FactuurcategorieInfo info = (FactuurcategorieInfo) data;
		try {
			info.validate();
			model.saveFactuurcategorie(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuurkop niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase) data))
			return;
		FactuurcategorieInfo info = (FactuurcategorieInfo) data;
		AbstractDetail form = super.createDetailForm(info, FactuurcategorieDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		FactuurcategorieInfo info = createNewFactuurkop();
		AbstractDetail form = super.createDetailForm(info, FactuurcategorieDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}

	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase) data)) {
			return;
		}
		FactuurcategorieInfo info = (FactuurcategorieInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteFactuurcategorie(info);
	}
	public VerzuimComboBoxModel getComboModelFactuurkoppen(){
		VerzuimComboBoxModel koppenmodel;
		factuurkoppen = model.getFactuurkoppenList();

		koppenmodel = new VerzuimComboBoxModel(this);
		for (FactuurkopInfo g : factuurkoppen) {
			koppenmodel.addElement(new TypeEntry(g.getId(), g.getOmschrijving()));
		}
		return koppenmodel;
	}

	public void getTableModel(List<FactuurcategorieInfo> facturen, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		factuurkoppen = model.getFactuurkoppenList();
		for (FactuurcategorieInfo info : facturen) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}
	private void setColumnValues(List<Object> colsinmodel, FactuurcategorieInfo fti, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__factuurcategoriefields val = __factuurcategoriefields.parse(colsinview.get(i));
			switch (val) {
			case OMSCHRIJVING:
				colsinmodel.add(i, fti.getOmschrijving());
				break;
			case BTWTARIEFSOORT:
				colsinmodel.add(i, fti.getBtwcategorie().toString());
				break;
			case FACTUURKOP:
				colsinmodel.add(i, getFactuurkop(fti.getFactuurkopid()));
				break;
			case OMZET:
				if (fti.isIsomzet())
					colsinmodel.add(i, "Ja");
				else
					colsinmodel.add(i, "Nee");
			default:
				break;
			}
		}
	}

	private String getFactuurkop(Integer factuurkopid) {
		for (FactuurkopInfo kop: factuurkoppen){
			if (kop.getId().equals(factuurkopid)){
				return kop.getOmschrijving();
			}
		}
		return null;
	}

	private boolean confirmDelete(FactuurcategorieInfo info) {
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
		FactuurcategorieInfo info = (FactuurcategorieInfo) data;
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

	public List<FactuurcategorieInfo> getFactuurcategorienList() {
		return model.getFactuurcategorienList();
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
