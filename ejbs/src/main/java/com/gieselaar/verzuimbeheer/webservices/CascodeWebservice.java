package com.gieselaar.verzuimbeheer.webservices;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.FacadeBase;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.CascodeBean;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;

@WebService
public class CascodeWebservice extends FacadeBase  {

	@EJB CascodeBean cascodeEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	public List<CascodeGroepInfo> allCascodeGroepen()
			throws PermissionException, VerzuimApplicationException {
		return cascodeEJB.allCascodeGroepen();
	}

	public List<CascodeInfo> allCascodes() throws PermissionException, VerzuimApplicationException {
		return cascodeEJB.allCascodes();
	}

	public List<CascodeInfo> getCascodesForGroep(int cascodegroepid) throws PermissionException, VerzuimApplicationException {
		return cascodeEJB.allCascodesForGroep(cascodegroepid);
	}
	public void deleteCascodeGroep(CascodeGroepInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		cascodeEJB.deleteCascodeGroep(info);
	}

	public void deleteCascode(CascodeInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		cascodeEJB.deleteCascode(info);
	}

	public void updateCascodeGroep(CascodeGroepInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		cascodeEJB.updateCascodeGroep(info);
	}

	public void updateCascode(CascodeInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		cascodeEJB.updateCascode(info);
	}

	public void addCascodeGroep(CascodeGroepInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		cascodeEJB.addCascodeGroep(info);
	}

	public void addCascode(CascodeInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		cascodeEJB.addCascode(info);
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
