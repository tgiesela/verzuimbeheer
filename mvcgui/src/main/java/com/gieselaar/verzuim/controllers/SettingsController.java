package com.gieselaar.verzuim.controllers;

import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.SettingsModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.SettingsDetail;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;

public class SettingsController extends AbstractController {

	private static final long serialVersionUID = 1L;

	private SettingsModel model;
	
	private SettingsInfo settings;

	public SettingsController(LoginSessionRemote session) {
		super(new SettingsModel(session), null);
		this.model = (SettingsModel)getModel();
	}

	public void selectSettings() throws VerzuimApplicationException {
		model.selectSettings();
	}

	private SettingsInfo createNewSettings() {
		SettingsInfo info = new SettingsInfo();
		info.setId(1);
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		SettingsInfo info = (SettingsInfo)data;
		try {
			info.validate();
			model.addSettings(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan settings niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		SettingsInfo info = (SettingsInfo)data;
		try {
			info.validate();
			model.saveSettings(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan settings niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		SettingsInfo info = (SettingsInfo) data;
		AbstractDetail form = super.createDetailForm(info, SettingsDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		SettingsInfo info = createNewSettings();
		AbstractDetail form = super.createDetailForm(info, SettingsDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		SettingsInfo info = (SettingsInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteSettings(info);
	}

	public VerzuimComboBoxModel getComboModelActviteiten() {
		List<ActiviteitInfo> activiteiten = getMaincontroller().getActiviteiten();
		ActiviteitInfo.sort(activiteiten, ActiviteitInfo.__sortcol.NAAM);
		/* Just a utility function to create ComboBoxModel */
		VerzuimComboBoxModel activiteitmodel = new VerzuimComboBoxModel(getMaincontroller());
		activiteitmodel.addElement(new TypeEntry(-1, "[]"));
		for (ActiviteitInfo w: activiteiten)
		{
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			activiteitmodel.addElement(wg);
		}
		return activiteitmodel;
	}
	private boolean confirmDelete(SettingsInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u de settings wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		SettingsInfo info = (SettingsInfo)data;
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

	public SettingsInfo getSettings() {
		return settings;
	}

}
