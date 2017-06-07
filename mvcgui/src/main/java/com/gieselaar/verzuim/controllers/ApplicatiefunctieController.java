package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.GebruikerModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.ApplicatieFunctieDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class ApplicatiefunctieController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __applicatiefunctiefields {
		UNKNOWN(-1), FUNCTIEID(0), OMSCHRIJVING(1);
		private int value;

		__applicatiefunctiefields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __applicatiefunctiefields parse(int type) {
			__applicatiefunctiefields field = UNKNOWN; // Default
			for (__applicatiefunctiefields item : __applicatiefunctiefields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private GebruikerModel model;
	
	public ApplicatiefunctieController(LoginSessionRemote session) {
		super(new GebruikerModel(session), null);
		this.model = (GebruikerModel)getModel();
	}

	public void selectApplicatiefuncties() throws VerzuimApplicationException {
		model.selectApplicatiefuncties();
	}

	private ApplicatieFunctieInfo createNewApplicatiefunctie() {
		ApplicatieFunctieInfo info = new ApplicatieFunctieInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		ApplicatieFunctieInfo info = (ApplicatieFunctieInfo)data;
		try {
			info.validate();
			model.addApplicatiefunctie(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan applicatiefunctie niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		ApplicatieFunctieInfo info = (ApplicatieFunctieInfo)data;
		try {
			info.validate();
			model.saveApplicatiefunctie(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan applicatiefunctie niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		ApplicatieFunctieInfo info = (ApplicatieFunctieInfo) data;
		AbstractDetail form = super.createDetailForm(info, ApplicatieFunctieDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		ApplicatieFunctieInfo info = createNewApplicatiefunctie();
		AbstractDetail form = super.createDetailForm(info, ApplicatieFunctieDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		ApplicatieFunctieInfo info = (ApplicatieFunctieInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteApplicatiefunctie(info);
	}

	public void getTableModel(List<ApplicatieFunctieInfo> functies, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (ApplicatieFunctieInfo info : functies) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, ApplicatieFunctieInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__applicatiefunctiefields val = __applicatiefunctiefields.parse(colsinview.get(i));
			switch (val) {
			case FUNCTIEID:
				colsinmodel.add(i, wfi.getFunctieId());
				break;
			case OMSCHRIJVING:
				colsinmodel.add(i, wfi.getFunctieomschrijving());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(ApplicatieFunctieInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze applicatiefunctie wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		ApplicatieFunctieInfo info = (ApplicatieFunctieInfo)data;
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

	public List<ApplicatieFunctieInfo> getApplicatiefunctiesList() {
		return model.getApplicatiefunctiesList();
	}
}
