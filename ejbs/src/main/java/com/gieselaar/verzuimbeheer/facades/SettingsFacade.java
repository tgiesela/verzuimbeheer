package com.gieselaar.verzuimbeheer.facades;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.SettingsBean;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;

@Stateful(mappedName="java:global/verzuimbeheer/SettingsFacade", name="SettingsFacade")
@LocalBean
public class SettingsFacade extends FacadeBase implements SettingsFacadeRemote {

	@EJB SettingsBean settingsEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	private void setCurrentuser(){
		settingsEJB.setCurrentuser(this.getCurrentuser());	
	}
	@Override
	public SettingsInfo getSettings()
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTABELLEN);
		return settingsEJB.getSettings();
	}

	@Override
	public void updateSettings(SettingsInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTABELLEN);
		settingsEJB.updateSettings(info);
		
	}
	/**
	 * Implementation abstract functions from FacadeBase
	 */
	@Override
	public void setLoginSession(LoginSessionRemote session) throws PermissionException {
		loginsession = session;
		super.setSession(loginsession);
	}

	@Override
	public void initSuperclass() {
		super.setGebruikerEJB(gebruikerEJB);
		super.setSession(loginsession);
	}
}
