package com.gieselaar.verzuimbeheer.facades;

import javax.ejb.Remote;
import javax.ejb.Remove;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimAuthenticationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

@Remote
public interface LoginSessionRemote {
	/**
	* Autoriseert gebruiker met opgegeven naam en wachtwoord
	* @author Tonny Gieselaar
	* @param username naam van de gebruiker
	* @param password wachtwoord van de gebruiker
	* @return boolean true indien succesvol
	* @since 12-05-2012
	* @version 1.0
	* @exception VerzuimAuthenticationException Als er een fout optreed
	 * @throws VerzuimApplicationException 
	*/
	public boolean authenticateGebruiker(String username, String password) throws VerzuimAuthenticationException, VerzuimApplicationException;
	/**
	* Wijzigt wachtwoord van reeds geautoriseerde gebruiker
	* @author Tonny Gieselaar
	* @param newUsername naam van de gebruiker als de ze gewijizgd moet worden
	* @param newPassword nieuw wachtwoord van de gebruiker
	* @return boolean true indien succesvol
	* @since 12-05-2012
	* @version 1.0
	* @exception VerzuimAuthenticationException Als er een fout optreed
	 * @throws VerzuimApplicationException 
	*/
	public boolean changePassword(String newUsername, String newPassword) throws VerzuimAuthenticationException, VerzuimApplicationException;
	/**
	* vraagt of de gebruiker geauthenticeerd is
	* @author Tonny Gieselaar
	* @return Object
	* @since 22-09-2012
	* @version 1.0
	* @exception VerzuimAuthenticationException Als er een fout optreed
	 */
	boolean isAuthenticated();
	/**
	 * vraagt de gegevens van de ingelogd gebruiker op 
	 */
	public GebruikerInfo getGebruiker();

	@Remove
	void logoffSession();
}
