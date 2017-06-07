package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.AfdelingBean;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.WerkgeverBean;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerBean;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;

/**
 * Session Bean implementation class WerknemerFacade
 */
@Stateful(mappedName="java:global/verzuimbeheer/WerknemerFacade", name="WerknemerFacade")
@LocalBean
public class WerknemerFacade extends FacadeBase implements WerknemerFacadeRemote{

    /**
     * Default constructor. 
     */
	@EJB private WerknemerBean werknemerEJB;
	@EJB private WerkgeverBean werkgeverEJB;
	@EJB private AfdelingBean afdelingEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	public WerknemerFacade() {
    }

	private void setCurrentuser(){
		werkgeverEJB.setCurrentuser(this.getCurrentuser());		
		werknemerEJB.setCurrentuser(this.getCurrentuser());		
		afdelingEJB.setCurrentuser(this.getCurrentuser());		
	}
	@Override
	public void deleteWerknemer(WerknemerInfo werknemer)
			throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKNEMER);
		werknemerEJB.deleteWerknemer(werknemer);
	}
	@Override
	public void updateWerknemer(WerknemerInfo werknemer) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		werknemerEJB.updateWerknemer(werknemer);
	}
	@Override
	public List<WerknemerFastInfo> allWerknemers() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getAll();
	}
	@Override
	public List<WerknemerFastInfo> getActiveWerknemers() throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getActiveWerknemers();
	}

	@Override
	public List<WerknemerFastInfo> getActiveWerknemersByWerkgever(Integer werkgeverid)
			throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getActiveWerknemersByWerkgever(werkgeverid);
	}

	@Override
	public List<WerknemerFastInfo> getInactiveWerknemers() throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getInactiveWerknemers();
	}

	@Override
	public List<WerknemerFastInfo> getInactiveWerknemersByWerkgever(Integer werkgeverid)
			throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getInactiverWerknemersByWerkgever(werkgeverid);
	}
	@Override
	public WerkgeverInfo getWerkgever(int id) throws PermissionException, VerzuimApplicationException{
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werkgeverEJB.getById(id);
	}
	public void deleteAfdeling(AfdelingInfo afdeling)
			throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERWERKNEMER);
		afdelingEJB.deleteAfdeling(afdeling);
	}
	public void updateAfdeling(AfdelingInfo afdeling) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKNEMER);
		if (afdeling.getId() <= 0) 
			afdelingEJB.addAfdeling(afdeling);
		else
			afdelingEJB.updateAfdeling(afdeling);
	}

	@Override
	public List<WerknemerFastInfo> getWerknemersByWerkgever(int werkgeverid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getByWerkgever(werkgeverid);
	}

	@Override
	public WerknemerInfo getWerknemer(int werknemerid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getById(werknemerid);
	}

	@Override
	public WerknemerInfo addWerknemer(WerknemerInfo werknemer)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKNEMER);
		return werknemerEJB.addWerknemer(werknemer);
	}
	@Override
	public List<WerknemerInfo> getByBSN(Integer werkgever, String BSN) throws PermissionException, VerzuimApplicationException{
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKNEMER);
		return werknemerEJB.getByBSN(werkgever, BSN);
	}
	@Override
	public List<WerknemerInfo> getByBSN(String BSN) throws PermissionException, VerzuimApplicationException{
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKNEMER);
		return werknemerEJB.getByBSN(BSN);
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

	@Override
	public List<WerknemerFastInfo> getWerknemerFast(Integer werknemerId) throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKNEMER);
		return werknemerEJB.getWerknemerFast(werknemerId);
	}
}
