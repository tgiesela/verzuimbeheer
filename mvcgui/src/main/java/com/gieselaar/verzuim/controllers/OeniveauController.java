package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.OeNiveauDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;

public class OeniveauController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __oeniveaufields {
		UNKNOWN(-1), PAKKETNAAM(0);
		private int value;

		__oeniveaufields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __oeniveaufields parse(int type) {
			__oeniveaufields field = UNKNOWN; // Default
			for (__oeniveaufields item : __oeniveaufields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkgeverModel model;
	
	public OeniveauController(LoginSessionRemote session) {
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel)getModel();
	}

	public void selectOeNiveaus() throws VerzuimApplicationException {
		model.selectOeNiveaus();
	}

	private OeNiveauInfo createNewOeniveau() {
		OeNiveauInfo info = new OeNiveauInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		OeNiveauInfo info = (OeNiveauInfo)data;
		try {
			info.validate();
			model.addOeNiveau(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan niveau niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		OeNiveauInfo info = (OeNiveauInfo)data;
		try {
			info.validate();
			model.saveOeniveau(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan niveau niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		OeNiveauInfo info = (OeNiveauInfo) data;
		AbstractDetail form = super.createDetailForm(info, OeNiveauDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		OeNiveauInfo info = createNewOeniveau();
		AbstractDetail form = super.createDetailForm(info, OeNiveauDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		OeNiveauInfo info = (OeNiveauInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteOeNiveau(info);
	}

	public void getTableModel(List<OeNiveauInfo> oeniveaus, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (OeNiveauInfo info : oeniveaus) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, OeNiveauInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__oeniveaufields val = __oeniveaufields.parse(colsinview.get(i));
			switch (val) {
			case PAKKETNAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(OeNiveauInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit oeniveau wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		OeNiveauInfo info = (OeNiveauInfo)data;
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

	public List<OeNiveauInfo> getOeniveauList() {
		return model.getOeNiveauList();
	}
}
