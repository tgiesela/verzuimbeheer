package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieBean;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.RolBean;
import com.gieselaar.verzuimbeheer.services.RolInfo;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

/**
 * Session Bean implementation class AutorisatieFacade
 */
@Stateful(mappedName="java:global/verzuimbeheer/AutorisatieFacade", name="AutorisatieFacade")
@LocalBean

public class AutorisatieFacade extends FacadeBase implements AutorisatieFacadeRemote {

    /**
     * Default constructor. 
     */
	@EJB private GebruikerBean gebruikerEJB;
	@EJB private ApplicatieFunctieBean applfuncEJB;
	@EJB private RolBean rolEJB;
	private LoginSessionRemote loginsession;

	private void setCurrentuser(){
		rolEJB.setCurrentuser(this.getCurrentuser());
		gebruikerEJB.setCurrentuser(this.getCurrentuser());
		applfuncEJB.setCurrentuser(this.getCurrentuser());
	}
	@Override
	public List<RolInfo> getRollen() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENAUTORISATIES);
		return rolEJB.getRollen();
	}

	@Override
	public List<RolInfo> getRollenForUser(int user) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENAUTORISATIES);
		return rolEJB.getByUser(user);
	}

	@Override
	public List<ApplicatieFunctieInfo> getAppFuncties() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENAUTORISATIES);
		return applfuncEJB.allApplicatiefuncties();
	}

	@Override
	public List<GebruikerInfo> getGebruikers() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENAUTORISATIES);
		return gebruikerEJB.allGebruikers();
	}

	@Override
	public GebruikerInfo getGebruiker(int id) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENAUTORISATIES);
		return gebruikerEJB.getGebruikerbyId(id);
	}
	@Override
	public RolInfo addRol(RolInfo rol) throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		return rolEJB.addRol(rol);
	}

	@Override
	public void deleteRol(RolInfo rol) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		rolEJB.deleterol(rol);
	}

	@Override
	public RolInfo updateRol(RolInfo rol) throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		return rolEJB.updateRol(rol);
	}

	@Override
	public ApplicatieFunctieInfo addAppFunctie(ApplicatieFunctieInfo func) throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		return applfuncEJB.addApplicatiefunctie(func);
	}

	@Override
	public void deleteAppFunctie(ApplicatieFunctieInfo func) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		applfuncEJB.deleteApplicatiefunctie(func);
	}

	@Override
	public ApplicatieFunctieInfo updateAppFunctie(ApplicatieFunctieInfo func) throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		return applfuncEJB.updateApplicatiefunctie(func);
	}

	@Override
	public GebruikerInfo addGebruiker(GebruikerInfo gebruiker) throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		return gebruikerEJB.addGebruiker(gebruiker);
	}

	@Override
	public void deleteGebruiker(GebruikerInfo gebruiker) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		gebruikerEJB.deleteGebruiker(gebruiker);
	}

	@Override
	public GebruikerInfo updateGebruiker(GebruikerInfo gebruiker) throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERAUTORISATIES);
		return gebruikerEJB.updateGebruiker(gebruiker);
	}

	@Override
	public boolean isAuthorised(GebruikerInfo user, __applicatiefunctie func) {
		return gebruikerEJB.isAuthorised(user, func);
	}

	@Override
	public void sendNewPassword(String username, String emailaddress) throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		List<GebruikerInfo> gebruikers = gebruikerEJB.allGebruikers();
		GebruikerInfo gebruiker;
		for (GebruikerInfo g: gebruikers){
			if (g.getEmailadres().equalsIgnoreCase(emailaddress.toLowerCase()) &&
					g.getName().equalsIgnoreCase(username)){
				gebruiker = g;
				gebruikerEJB.sendNewpassword(gebruiker);
				return;
			}
		}
		throw new ValidationException("Combinatie Emailadres/gebruiker onbekend");
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
