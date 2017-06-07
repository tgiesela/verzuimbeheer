package com.gieselaar.verzuim.controllers;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.InstantieModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.BedrijfsgegevensDetail;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;

public class BedrijfsgegevensController extends AbstractController {

	private static final long serialVersionUID = 1L;

	private InstantieModel model;
	
	public BedrijfsgegevensController(LoginSessionRemote session) {
		super(new InstantieModel(session), null);
		this.model = (InstantieModel)getModel();
	}

	public void selectBedrijfsgegevens() throws VerzuimApplicationException {
		model.selectBedrijfsgegevens();
	}

	private BedrijfsgegevensInfo createNewBedrijfsgegevens() {
		BedrijfsgegevensInfo info = new BedrijfsgegevensInfo();
		info.setId(1);
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		BedrijfsgegevensInfo info = (BedrijfsgegevensInfo)data;
		try {
			info.validate();
			model.addBedrijfsgegevens(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan bedrijfsgegevens niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		BedrijfsgegevensInfo info = (BedrijfsgegevensInfo)data;
		try {
			info.validate();
			model.saveBedrijfsgegevens(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan bedrijfsgegevens niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		BedrijfsgegevensInfo info = (BedrijfsgegevensInfo) data;
		AbstractDetail form = super.createDetailForm(info, BedrijfsgegevensDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		BedrijfsgegevensInfo info = createNewBedrijfsgegevens();
		AbstractDetail form = super.createDetailForm(info, BedrijfsgegevensDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		BedrijfsgegevensInfo info = (BedrijfsgegevensInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteBedrijfsgegevens(info);
	}

	private boolean confirmDelete(BedrijfsgegevensInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u de bedrijfsgegevens wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		BedrijfsgegevensInfo info = (BedrijfsgegevensInfo)data;
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

	public BedrijfsgegevensInfo getBedrijfsgegevens() {
		return model.getBedrijfsgegevens();
	}

}
