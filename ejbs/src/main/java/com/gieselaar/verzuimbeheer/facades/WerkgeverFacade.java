package com.gieselaar.verzuimbeheer.facades;

import java.io.Serializable;
import java.util.Date;
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
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.OeBean;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.TariefBean;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverBean;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

/**
 * Session Bean implementation class WerkgeverFacade
 */
@Stateful(mappedName="java:global/verzuimbeheer/WerkgeverFacade", name="WerkgeverFacade")
@LocalBean
public class WerkgeverFacade extends FacadeBase implements WerkgeverFacadeRemote, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB private WerkgeverBean werkgeverEJB;
	@EJB private AfdelingBean afdelingEJB;
	@EJB private GebruikerBean gebruikerEJB;
	@EJB private TariefBean tariefEJB;
	@EJB private OeBean oeEJB;
	private transient LoginSessionRemote loginsession;

	public WerkgeverFacade() {
		super();
	}

	private void setCurrentuser(){
		werkgeverEJB.setCurrentuser(this.getCurrentuser());		
		afdelingEJB.setCurrentuser(this.getCurrentuser());		
	}

	@Override
	public WerkgeverInfo addWerkgever(WerkgeverInfo werkgever)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return werkgeverEJB.addWerkgever(werkgever);
		
	}
	@Override
	public void deleteWerkgever(WerkgeverInfo werkgever) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		werkgeverEJB.deleteWerkgever(werkgever);
	}
	@Override
	public void updateWerkgever(WerkgeverInfo werkgever) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		
		werkgeverEJB.updateWerkgever(werkgever);
	}
	@Override
	public List<WerkgeverInfo> allWerkgevers() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		return werkgeverEJB.getAll();
	}
	@Override
	public WerkgeverInfo getWerkgever(int id) throws PermissionException, VerzuimApplicationException{
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		
		return werkgeverEJB.getById(id);
	}
	@Override
	public void deleteAfdeling(AfdelingInfo afdeling) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		afdelingEJB.deleteAfdeling(afdeling);
	}
	@Override
	public AfdelingInfo updateAfdeling(AfdelingInfo afdeling) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);

		if (afdeling.getId() <= 0) 
			return afdelingEJB.addAfdeling(afdeling);
		else
			return afdelingEJB.updateAfdeling(afdeling);
	}
	@Override
	public List<WerkgeverInfo> allWerkgeversList() throws PermissionException, VerzuimApplicationException{
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		
		return werkgeverEJB.getAllSimple();
	}

	@Override
	public List<AfdelingInfo> getAfdelingenWerkgever(int werkgeverId) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		
		return afdelingEJB.getByWerkgeverId(werkgeverId);
	}

	@Override
	public List<HoldingInfo> getHoldings() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		return werkgeverEJB.getHoldings();
	}
	@Override
	public void deleteHolding(HoldingInfo holding) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERWERKGEVER);
		werkgeverEJB.deleteHolding(holding);
	}
	@Override
	public void updateHolding(HoldingInfo holding) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		werkgeverEJB.updateHolding(holding);
	}
	@Override
	public HoldingInfo addHolding(HoldingInfo holding) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return werkgeverEJB.addHolding(holding);
	}
	@Override
	public AfdelingInfo addAfdeling(AfdelingInfo afdeling) throws ValidationException, PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return afdelingEJB.addAfdeling(afdeling);
	}

	@Override
	public List<OeInfo> getOes() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		return oeEJB.getOes();
	}

	@Override
	public OeInfo addOe(OeInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return oeEJB.addOe(oe);
	}

	@Override
	public void updateOe(OeInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		oeEJB.updateOe(oe);
	}

	@Override
	public void deleteOe(OeInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		oeEJB.deleteOe(oe);
	}

	@Override
	public List<OeNiveauInfo> getOeNiveaus() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		return oeEJB.getOeNiveaus();
	}

	@Override
	public OeNiveauInfo addOeniveau(OeNiveauInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return oeEJB.addOeNiveau(oe);
	}

	@Override
	public void updateOeniveau(OeNiveauInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		oeEJB.updateOeNiveau(oe);
	}

	@Override
	public void deleteOeniveau(OeNiveauInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		oeEJB.deleteOeNiveau(oe);
	}

	@Override
	public List<ImportResult> importWerknemers(int werkgever, String separator, byte[] uploadedFile, boolean fieldnamespresent) throws ValidationException, 
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return werkgeverEJB.importWerknemers(werkgever, separator, uploadedFile, fieldnamespresent);
	}
	@Override
	public List<TariefInfo> getTarieven() throws PermissionException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTARIEVEN);
		return tariefEJB.getTarieven();
	}
	@Override
	public List<TariefInfo> getTarievenByWerkgever(Integer werkgeverid)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTARIEVEN);
		return tariefEJB.getTarievenByWerkgever(werkgeverid);
	}
	@Override
	public List<TariefInfo> getTarievenByHolding(Integer holdingid)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTARIEVEN);
		return tariefEJB.getTarievenByHolding(holdingid);
	}
	
	@Override
	public TariefInfo getTariefById(Integer id)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTARIEVEN);
		return tariefEJB.getTariefById(id);
	}
	@Override
	public TariefInfo addTarief(TariefInfo tarief) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTARIEVEN);
		tariefEJB.addTarief(tarief);
		return tariefEJB.getTariefById(tarief.getId());
	}
	@Override
	public TariefInfo updateTarief(TariefInfo tarief)
			throws ValidationException, PermissionException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTARIEVEN);
		tariefEJB.updateTarief(tarief);
		return tariefEJB.getTariefById(tarief.getId());
	}
	@Override
	public void deleteTarief(TariefInfo tarief) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERTARIEVEN);
		tariefEJB.deleteTarief(tarief);
	}
	@Override
	public HoldingInfo getHolding(Integer holdingId)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		return werkgeverEJB.getHoldingById(holdingId);
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
	public List<ImportResult> importUren(String separator, byte[] uploadedFile,
			boolean fieldnamespresent) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return werkgeverEJB.importUren(separator, uploadedFile, fieldnamespresent);
	}
	@Override
	public List<ImportResult> afsluitenDienstverbanden(int holding, String separator, byte[] uploadedFile,
			boolean fieldnamespresent) throws ValidationException,
			PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKGEVER);
		return werkgeverEJB.afsluitenDienstverbanden(holding, separator, uploadedFile, fieldnamespresent);
	}
	@Override
	public List<WerkgeverInfo> getWerkgeversHolding(Integer id)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKGEVER);
		return werkgeverEJB.getWerkgeversHolding(id);
	}
	@Override
	public boolean isVasttariefHuisbezoeken(Date peildatum, Integer holdingid,
			Integer werkgeverid) throws VerzuimApplicationException, ValidationException {
		return tariefEJB.isVasttariefHuisbezoeken(peildatum, holdingid, werkgeverid);
	}
}
