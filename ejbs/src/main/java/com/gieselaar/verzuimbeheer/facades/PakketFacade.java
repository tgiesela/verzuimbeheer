package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ActiviteitBean;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.PakketBean;
import com.gieselaar.verzuimbeheer.services.PakketInfo;

/**
 * Session Bean implementation class PakketFacade
 */
@Stateful(mappedName="java:global/verzuimbeheer/PakketFacade", name="PakketFacade")
@LocalBean
public class PakketFacade extends FacadeBase implements PakketFacadeRemote {

    /**
     * Default constructor. 
     */
	@EJB private PakketBean pakketEJB;
	@EJB private ActiviteitBean activiteitEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	public PakketFacade() {
    }
	private void setCurrentuser(){
		pakketEJB.setCurrentuser(this.getCurrentuser());		
		activiteitEJB.setCurrentuser(this.getCurrentuser());
	}

	@Override
	public void deletePakket(PakketInfo pakket) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERPAKKET);
		pakketEJB.deletepakket(pakket);
	}

	@Override
	public PakketInfo updatePakket(PakketInfo pakket) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERPAKKET);
		if (pakket.getId() <= 0) 
			return pakketEJB.addPakket(pakket);
		else{
			pakketEJB.updatePakket(pakket);
			return pakket;
		}
	}

	@Override
	public List<ActiviteitInfo> allActivteiten() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENPAKKET);
		return activiteitEJB.allActiviteiten();
	}

	@Override
	public List<PakketInfo> allPaketten() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENPAKKET);
		return pakketEJB.allPakketen();
	}

	@Override
	public void deleteActiviteit(ActiviteitInfo activiteit)
			throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERPAKKET);
		activiteitEJB.deleteActiviteit(activiteit);
	}

	@Override
	public ActiviteitInfo updateActiviteit(ActiviteitInfo activiteit)
			throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERPAKKET);
		if (activiteit.getId() <= 0) 
			return activiteitEJB.addActiviteit(activiteit);
		else{
			activiteitEJB.updateActiviteit(activiteit);
			return activiteit;
		}
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
