package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.PakketInfo;

@Remote
public interface PakketFacadeRemote extends FacadeBaseRemote {
	/**
	* Verwijdert gegevens van een pakket
	* @author Tonny Gieselaar
	* @param pakket bevat alle informatie van een pakket. Indien ID = -1
	*        dan is de aanname nieuw dus insert.
	* @since 17-05-2012
	* @version 1.0
	* @throws ValidationException 
	 * @throws VerzuimApplicationException 
	*/
	public void deletePakket(PakketInfo pakket) throws PermissionException, ValidationException, VerzuimApplicationException;
	/**
	* Wijzigt gegevens van een pakket
	* @author Tonny Gieselaar
	* @param pakket bevat alle informatie van een pakket. Indien ID = -1
	*        dan is de aanname nieuw dus insert.
	* @since 20-05-2012
	* @version 1.0
	 * @return 
	* @throws ValidationException 
	 * @throws VerzuimApplicationException 
	*/
	public PakketInfo updatePakket(PakketInfo pakket) throws PermissionException, ValidationException, VerzuimApplicationException;
	/**
	* Geeft lijst van alle pakketten
	* @author Tonny Gieselaar
	* @since 18-05-2012
	* @version 1.0
	 * @throws VerzuimApplicationException 
	*/
	public List<ActiviteitInfo> allActivteiten() throws PermissionException, VerzuimApplicationException;
	/**
	* Geeft lijst van alle activiteiten
	* @author Tonny Gieselaar
	* @since 18-05-2012
	* @version 1.0
	 * @throws VerzuimApplicationException 
	*/
	public List<PakketInfo> allPaketten() throws PermissionException, VerzuimApplicationException;
	/**
	* Verwijdert gegevens van een activiteit
	* @author Tonny Gieselaar
	* @param activiteit bevat alle informatie van een activiteit. Indien ID = -1
	*        dan is de aanname nieuw dus insert.
	* @since 17-05-2012
	* @version 1.0
	* @throws ValidationException 
	 * @throws VerzuimApplicationException 
	*/
	public void deleteActiviteit(ActiviteitInfo activiteit) throws PermissionException, ValidationException, VerzuimApplicationException;
	/**
	* Wijzigt gegevens van een activiteit
	* @author Tonny Gieselaar
	* @param activiteit bevat alle informatie van een activiteit. 
	* @since 06-06-2012
	* @version 1.0
	 * @return 
	* @throws ValidationException 
	 * @throws VerzuimApplicationException 
	*/
	public ActiviteitInfo updateActiviteit(ActiviteitInfo activiteit) throws PermissionException, ValidationException, VerzuimApplicationException;
	/**
	* Zet de informatie over de ingelogde gebruiker
	* @author Tonny Gieselaar
	* @since 06-06-2012
	* @version 1.0
	* @param session bevat de info van de ingelogd gebruiker
	*/
	public void setLoginSession(LoginSessionRemote session) throws PermissionException;
}
