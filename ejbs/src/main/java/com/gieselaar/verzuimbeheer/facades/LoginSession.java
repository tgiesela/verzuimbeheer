package com.gieselaar.verzuimbeheer.facades;


import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimAuthenticationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

/**
 * Session Bean implementation class LoginSession
 */
@LocalBean
@Stateful(mappedName="java:global/verzuimbeheer/LoginSession", name="LoginSession")
//@StatefulTimeout(unit=TimeUnit.HOURS, value = 8)
//@EJB(name="LoginSession", beanInterface=LoginSessionRemote.class, beanName="LoginSession", mappedName="java:global/verzuimbeheer/LoginSession",lookup="java:global/verzuimbeheer/LoginSession")
public class LoginSession 
	extends FacadeBase 
	implements LoginSessionRemote, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB private GebruikerBean gebruikerEJB;
	private GebruikerInfo gebruiker = null;
	private transient LoginSessionRemote loginsessionremote;
	private boolean authenticated;

	private static final String LOGINSESSIONNAME = "LoginSession: ";
	public LoginSession() {
		log.info("LoginSession constructed: " + this.hashCode());
		authenticated = false;
	}
	public void setGebruiker(GebruikerInfo gebruiker) {
		log.info(LOGINSESSIONNAME + this.hashCode() + " setGebruiker(" + gebruiker.getName() + ")");
		this.gebruiker = gebruiker;
	}
	@Override
	public GebruikerInfo getGebruiker() {
		return gebruiker;
	}
	@Override
	public boolean isAuthenticated(){
		return authenticated;
	}

	@Override
	public boolean authenticateGebruiker(String name, String password) throws VerzuimAuthenticationException, VerzuimApplicationException{
		authenticated = gebruikerEJB.authenticateGebruiker(name, password);
		if (isAuthenticated()){
			gebruiker = gebruikerEJB.getGebruikerbyName(name);
			log.info(LOGINSESSIONNAME + this.hashCode() + " authenticateGebruiker OK: for " + gebruiker.getName());
		}else{
			gebruiker = null;
			log.info(LOGINSESSIONNAME + this.hashCode() + " authenticateGebruiker FAILED: for " + name);
		}
		return isAuthenticated();
	}
	@Override
	public boolean changePassword(String newusername, String newPassword) throws VerzuimAuthenticationException, VerzuimApplicationException {
		if (isAuthenticated())
			return gebruikerEJB.changePassword(gebruiker.getName(), newusername, newPassword);
		else
			throw new VerzuimAuthenticationException("User not authenticated, cannot change password"); 
	}
	@Override
	@Remove
	public void logoffSession(){
		log.info(LOGINSESSIONNAME + this.hashCode() + " destroyed");
		authenticated = false;
	}
	/**
	 * Implementation abstract functions from FacadeBase
	 */
	@Override
	public void setLoginSession(LoginSessionRemote session) throws PermissionException {
		loginsessionremote = session;
		super.setSession(loginsessionremote);
	}

	@Override
	public void initSuperclass() {
		super.setGebruikerEJB(gebruikerEJB);
		super.setSession(loginsessionremote);
	}
}
