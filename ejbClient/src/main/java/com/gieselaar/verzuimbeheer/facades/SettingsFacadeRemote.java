package com.gieselaar.verzuimbeheer.facades;

import javax.ejb.Remote;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
@Remote
public interface SettingsFacadeRemote extends FacadeBaseRemote {
	SettingsInfo getSettings() throws PermissionException, VerzuimApplicationException;
	void updateSettings(SettingsInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;

	public void setLoginSession(LoginSessionRemote session) throws PermissionException;
}
