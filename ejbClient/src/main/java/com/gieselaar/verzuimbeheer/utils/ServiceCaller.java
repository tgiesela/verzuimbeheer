package com.gieselaar.verzuimbeheer.utils;

import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.CascodeFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.FactuurFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.InstantieFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.PakketFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.ReportFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.SettingsFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.WerknemerFacadeRemote;

public class ServiceCaller {
	private ServiceCaller(){
		
	}
	public static AutorisatieFacadeRemote autorisatieFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		AutorisatieFacadeRemote facade = (AutorisatieFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.AutorisatieFacade.getValue(),AutorisatieFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static PakketFacadeRemote pakketFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		PakketFacadeRemote facade = (PakketFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.PakketFacade.getValue(),PakketFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static SettingsFacadeRemote settingsFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		SettingsFacadeRemote facade = (SettingsFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.SettingsFacade.getValue(),SettingsFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static WerkgeverFacadeRemote werkgeverFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		WerkgeverFacadeRemote facade = (WerkgeverFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.WerkgeverFacade.getValue(),WerkgeverFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static WerknemerFacadeRemote werknemerFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		WerknemerFacadeRemote facade = (WerknemerFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.WerknemerFacade.getValue(),WerknemerFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static InstantieFacadeRemote instantieFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		InstantieFacadeRemote facade = (InstantieFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.InstantieFacade.getValue(),InstantieFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static FactuurFacadeRemote factuurFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		FactuurFacadeRemote facade = (FactuurFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.FactuurFacade.getValue(),FactuurFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static VerzuimFacadeRemote verzuimFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		VerzuimFacadeRemote facade = (VerzuimFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.VerzuimFacade.getValue(),VerzuimFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static CascodeFacadeRemote cascodeFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		CascodeFacadeRemote facade = (CascodeFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.CascodeFacade.getValue(),CascodeFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static ReportFacadeRemote reportFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		ReportFacadeRemote facade = (ReportFacadeRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.ReportFacade.getValue(),ReportFacadeRemote.class);
		facade.setLoginSession(loginsession);
		return facade;
	}
	public static LoginSessionRemote loginSessionFacade(LoginSessionRemote loginsession) throws ServiceLocatorException, PermissionException{
		return (LoginSessionRemote) ServiceLocator.getInstance().getRemoteHome (
				RemoteInterfaces.LoginSession.getValue(),LoginSessionRemote.class);
	}
}
