package com.gieselaar.verzuimbeheer.facades;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

@Remote
public interface WerkgeverFacadeRemote extends FacadeBaseRemote {
	public WerkgeverInfo addWerkgever(WerkgeverInfo werkgever)
			throws PermissionException, ValidationException,
			VerzuimApplicationException;

	/**
	 * Voegt gegevens van een werkgever toe
	 * 
	 * @author Tonny Gieselaar
	 * @param werkgever
	 *            alle informatie van een werkgever.
	 * @since 17-05-2012
	 * @version 1.0
	 * @throws ValidationException
	 * @throws VerzuimApplicationException 
	 */
	public void deleteWerkgever(WerkgeverInfo werkgever)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	/**
	 * Verwijdert gegevens van een werkgever
	 * 
	 * @author Tonny Gieselaar
	 * @param werkgever
	 *            alle informatie van een werkgever. Indien ID = -1 dan is de
	 *            aanname nieuw dus insert.
	 * @since 17-05-2012
	 * @version 1.0
	 * @throws ValidationException
	 * @throws VerzuimApplicationException
	 */
	public void updateWerkgever(WerkgeverInfo werkgever)
			throws PermissionException, ValidationException,
			VerzuimApplicationException;

	/**
	 * Wijzigt gegevens van een werkgever
	 * 
	 * @author Tonny Gieselaar
	 * @param werkgever
	 *            alle informatie van een werkgever. Indien ID = -1 dan is de
	 *            aanname nieuw dus insert.
	 * @since 20-05-2012
	 * @version 1.0
	 * @throws VerzuimApplicationException
	 * @throws ValidationException
	 */
	public List<WerkgeverInfo> allWerkgevers() throws PermissionException,
			VerzuimApplicationException;

	/**
	 * Geeft lijst van alle werkgevers
	 * 
	 * @author Tonny Gieselaar
	 * @since 18-05-2012
	 * @version 1.0
	 * @throws VerzuimApplicationException
	 */
	public List<AfdelingInfo> getAfdelingenWerkgever(int werkgeverId)
			throws PermissionException, VerzuimApplicationException;

	/**
	 * Geeft lijst van alle werkgevers, maar zonder details alleen naam en id
	 * tbv combobox
	 * 
	 * @author Tonny Gieselaar
	 * @since 18-05-2012
	 * @version 1.0
	 * @throws VerzuimApplicationException
	 */
	public List<WerkgeverInfo> allWerkgeversList() throws PermissionException,
			VerzuimApplicationException;

	/**
	 * Geeft lijst van alle afdelingen van een werkgever
	 * 
	 * @author Tonny Gieselaar
	 * @since 3-09-2012
	 * @version 1.0
	 * @throws VerzuimApplicationException
	 */
	public WerkgeverInfo getWerkgever(int id) throws PermissionException,
			VerzuimApplicationException;

	/**
	 * Geeft info van een specifieke werkgever
	 * 
	 * @author Tonny Gieselaar
	 * @since 18-05-2012
	 * @version 1.0
	 */
	public void setLoginSession(LoginSessionRemote session)
			throws PermissionException;

	/**
	 * Zet de informatie over de ingelogde gebruiker
	 * 
	 * @author Tonny Gieselaar
	 * @since 20-05-2012
	 * @version 1.0
	 * @param session
	 *            bevat de info van de ingelogd gebruiker
	 * @throws PermissionException
	 * @throws VerzuimApplicationException 
	 */
	public void deleteAfdeling(AfdelingInfo werkgever)
			throws ValidationException, PermissionException, VerzuimApplicationException;

	/**
	 * Verwijdert gegevens van een afdeling
	 * 
	 * @author Tonny Gieselaar
	 * @param werkgever
	 *            alle informatie van een afdeling. Indien ID = -1 dan is de
	 *            aanname nieuw dus insert.
	 * @since 17-05-2012
	 * @version 1.0
	 * @throws ValidationException
	 * @throws PermissionException
	 * @throws VerzuimApplicationException
	 */
	public AfdelingInfo updateAfdeling(AfdelingInfo werkgever)
			throws ValidationException, PermissionException,
			VerzuimApplicationException;

	/**
	 * Wijzigt gegevens van een afdeling
	 * 
	 * @author Tonny Gieselaar
	 * @param werkgever
	 *            alle informatie van een afdeling. Indien ID = -1 dan is de
	 *            aanname nieuw dus insert.
	 * @since 20-05-2012
	 * @version 1.0
	 * @throws PermissionException
	 * @throws VerzuimApplicationException
	 * @throws ValidationException
	 */
	public List<HoldingInfo> getHoldings() throws PermissionException,
			VerzuimApplicationException;

	void updateHolding(HoldingInfo holding) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	void deleteHolding(HoldingInfo holding) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public HoldingInfo addHolding(HoldingInfo holding) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public AfdelingInfo addAfdeling(AfdelingInfo afdeling) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public List<OeInfo> getOes() throws PermissionException,
			VerzuimApplicationException;

	public OeInfo addOe(OeInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public void updateOe(OeInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public void deleteOe(OeInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public List<OeNiveauInfo> getOeNiveaus() throws PermissionException,
			VerzuimApplicationException;

	public OeNiveauInfo addOeniveau(OeNiveauInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public void updateOeniveau(OeNiveauInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public void deleteOeniveau(OeNiveauInfo oe) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public List<ImportResult> importWerknemers(int werkgever, String separator,
			byte[] uploadedFile, boolean fieldnamespresent)
			throws ValidationException, PermissionException,
			VerzuimApplicationException;

	public List<ImportResult> importUren(String separator, byte[] uploadedFile,
			boolean fieldnamespresent) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public List<TariefInfo> getTarieven() throws PermissionException,
			VerzuimApplicationException;

	public List<TariefInfo> getTarievenByWerkgever(Integer werkgeverid)
			throws PermissionException, VerzuimApplicationException;

	public TariefInfo getTariefById(Integer id) throws PermissionException,
			VerzuimApplicationException;

	public TariefInfo addTarief(TariefInfo tarief) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public TariefInfo updateTarief(TariefInfo tarief)
			throws ValidationException, PermissionException,
			VerzuimApplicationException;

	public void deleteTarief(TariefInfo tarief) throws ValidationException,
			PermissionException, VerzuimApplicationException;

	public List<TariefInfo> getTarievenByHolding(Integer holdingid)
			throws PermissionException, VerzuimApplicationException;

	public HoldingInfo getHolding(Integer holdingId)
			throws PermissionException, VerzuimApplicationException;

	public List<WerkgeverInfo> getWerkgeversHolding(Integer id)
			throws PermissionException, VerzuimApplicationException;

	public boolean isVasttariefHuisbezoeken(Date peildatum, Integer holdingid,
			Integer werkgeverid) throws VerzuimApplicationException, ValidationException;

	public List<ImportResult> afsluitenDienstverbanden(int holding, String separator, byte[] uploadedFile,
			boolean fieldnamespresent) throws ValidationException, PermissionException, VerzuimApplicationException;
}
