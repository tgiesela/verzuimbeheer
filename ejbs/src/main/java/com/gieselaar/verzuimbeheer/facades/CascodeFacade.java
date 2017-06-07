package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.CascodeBean;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;

@Stateful(mappedName="java:global/verzuimbeheer/CascodeFacade", name="CascodeFacade")
@LocalBean
public class CascodeFacade extends FacadeBase implements CascodeFacadeRemote {

	@EJB CascodeBean cascodeEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	private void setCurrentuser(){
		cascodeEJB.setCurrentuser(this.getCurrentuser());	
	}
	@Override
	public List<CascodeGroepInfo> allCascodeGroepen()
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTABELLEN);
		return cascodeEJB.allCascodeGroepen();
	}

	@Override
	public List<CascodeInfo> allCascodes() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTABELLEN);
		return cascodeEJB.allCascodes();
	}

	@Override
	public List<CascodeInfo> getCascodesForGroep(int cascodegroepid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTABELLEN);
		return cascodeEJB.allCascodesForGroep(cascodegroepid);
	}
	@Override
	public void deleteCascodeGroep(CascodeGroepInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERTABELLEN);
		cascodeEJB.deleteCascodeGroep(info);
	}

	@Override
	public void deleteCascode(CascodeInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERTABELLEN);
		cascodeEJB.deleteCascode(info);
	}

	@Override
	public CascodeGroepInfo updateCascodeGroep(CascodeGroepInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTABELLEN);
		return cascodeEJB.updateCascodeGroep(info);
	}

	@Override
	public CascodeInfo updateCascode(CascodeInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTABELLEN);
		return cascodeEJB.updateCascode(info);
		
	}

	@Override
	public void addCascodeGroep(CascodeGroepInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTABELLEN);
		cascodeEJB.addCascodeGroep(info);
	}

	@Override
	public void addCascode(CascodeInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTABELLEN);
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
