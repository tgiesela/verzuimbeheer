package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.OeDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;

public class OeController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __oefields {
		UNKNOWN(-1), PAKKETNAAM(0);
		private int value;

		__oefields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __oefields parse(int type) {
			__oefields field = UNKNOWN; // Default
			for (__oefields item : __oefields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkgeverModel model;
	
	public OeController(LoginSessionRemote session) {
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel)getModel();
	}

	public void selectOeNiveaus() throws VerzuimApplicationException {
		model.selectOeNiveaus();
	}

	public void selectOes() throws VerzuimApplicationException {
		model.selectOes();
	}
	private OeInfo createNewOe() {
		OeInfo info = new OeInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		OeInfo info = (OeInfo)data;
		try {
			info.validate();
			model.addOe(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan oe niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		OeInfo info = (OeInfo)data;
		try {
			info.validate();
			model.saveOe(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan oe niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		OeInfo info = (OeInfo) data;
		AbstractDetail form = super.createDetailForm(info, OeDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		OeInfo info = createNewOe();
		AbstractDetail form = super.createDetailForm(info, OeDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		OeInfo info = (OeInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteOe(info);
	}

	public void getTableModel(List<OeInfo> oes, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (OeInfo info : oes) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, OeInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__oefields val = __oefields.parse(colsinview.get(i));
			switch (val) {
			case PAKKETNAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(OeInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze oe wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		OeInfo info = (OeInfo)data;
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

	public List<OeInfo> getOeList() {
		return model.getOeList();
	}

	public List<OeNiveauInfo> getOeNiveauList() {
		return model.getOeNiveauList();
	}
	public VerzuimComboBoxModel getComboModelOeniveaus() {
		List<OeNiveauInfo> niveaus = getOeNiveauList();
		/* Just a utility function to create ComboBoxModel */
		VerzuimComboBoxModel niveaumodel = new VerzuimComboBoxModel(getMaincontroller());
		for (OeNiveauInfo w: niveaus)
		{
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			niveaumodel.addElement(wg);
		}
		return niveaumodel;
	}
}
