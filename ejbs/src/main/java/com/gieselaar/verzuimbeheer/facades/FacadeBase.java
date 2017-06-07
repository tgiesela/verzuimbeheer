package com.gieselaar.verzuimbeheer.facades;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

public abstract class FacadeBase {

	protected LoginSessionRemote session = null;
	protected GebruikerBean gebruikerbean;
	protected static final Logger log = Logger.getLogger(FacadeBase.class);
	
	protected void checkPermission(__applicatiefunctie func) throws PermissionException {
		checkLogin();
		if (!isAuthorised(session.getGebruiker(), func))
			throw new PermissionException("U bent niet bevoegd om deze functie uit te voeren");
	}
	private void checkLogin() throws PermissionException{
		if (session == null)
			throw new PermissionException("Login Session not set");
		if (session.getGebruiker() == null)
			throw new PermissionException("User not logged in");
		if (session.isAuthenticated()){
			/* ok */
		}else{
			throw new PermissionException("User not authenticated");
		}
	}
	public boolean isAuthorised(GebruikerInfo user, __applicatiefunctie func) throws PermissionException {
		if (user == null)
			throw new PermissionException("isAuthorised called with User == null");
		if (gebruikerbean == null)
			throw new PermissionException("gebruikerEJB == null");
		return gebruikerbean.isAuthorised(user, func);
	}

	public Integer getCurrentuser(){
		if (this.session == null){
			log.info("LoginSession is null in getCurrentUser!!!");
			return null;
		} 
		return this.session.getGebruiker().getId();
	}
	@PrePassivate
	public void passivate(){
		log.info("FacadeBase beeing passivated: " + this.getClass().getName() + " " + this.getClass().hashCode());
	}
	@PostActivate
	public void activate(){
		initSuperclass();
		if (gebruikerbean == null){
			log.info("FacadeBase gebruikerEJB == null:" + (gebruikerbean == null));
		}
		log.info("FacadeBase beeing activated: " + this.getClass().getName() + " " + this.getClass().hashCode());
	}
	@PreDestroy
	public void destroyinfo(){
		log.info("FacadeBase beeing destroyed: " + this.getClass().getName());
	}
	@PostConstruct
	public void constructinfo(){
		initSuperclass();
		log.info("FacadeBase created: " + this.getClass().getName());
		log.info("FacadeBase gebruikerEJB == null:" + (gebruikerbean == null));
	}
	@Remove
	public void remove(){
		log.info("FacadeBase removed: " + this.getClass().getName());
	}

	public GebruikerBean getGebruikerEJB() {
		return gebruikerbean;
	}
	public void setGebruikerEJB(GebruikerBean gebruikerEJB) {
		this.gebruikerbean = gebruikerEJB;
	}
	public LoginSessionRemote getSession() {
		return session;
	}
	public void setSession(LoginSessionRemote session) {
		this.session = session;
	}
	/**
	 * Abstract method to let subclass set the session. 
	 * This is necessary because during activation of the bean
	 * the properties of the super.class are not set.
	 * 
	 * @param session
	 * @throws PermissionException
	 */
	public abstract void setLoginSession(LoginSessionRemote session) throws PermissionException ;
	/**
	 * Used to set the gebruikerBean property.
	 * The caller must call the setter setGebruikerBean(bean) 
	 */	
	public abstract void initSuperclass();

}
