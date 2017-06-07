package com.gieselaar.verzuim.models;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class SettingsModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private SettingsInfo settings;
	public SettingsModel(LoginSessionRemote session){
		super(session);
	}

	public void selectSettings() throws VerzuimApplicationException {
		try {
			settings = ServiceCaller.settingsFacade(this.getSession()).getSettings();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(settings);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public SettingsInfo getSettings() {
		return settings;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectSettings();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addSettings(SettingsInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.settingsFacade(getSession()).updateSettings(info);
			settings = ServiceCaller.settingsFacade(getSession()).getSettings();
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(settings);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveSettings(SettingsInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.settingsFacade(getSession()).updateSettings(info);
			settings = ServiceCaller.settingsFacade(getSession()).getSettings();
			/* Now also the list has to be updated */
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(settings);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteSettings(SettingsInfo info) throws VerzuimApplicationException {
		/* no delete */
	}
}
