package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimAuthenticationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

@ManagedBean
@SessionScoped
public class loginClass extends BackingBeanBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB GebruikerBean gebruikerBean;
	@EJB AutorisatieFacade autorisatieFacade;
	@EJB LoginSessionRemote loginSession;
	
	private String username;
	private String password;
	private String emailadres;
	private GebruikerInfo gebruiker;
	private FacesContext context;
	private FacesMessage message;
	private panelMenuClass menu;
	private long lastmillis;
	private int sessionTimeout;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailadres() {
		return emailadres;
	}
	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}
	@PostConstruct
	public void init(){
		
	}
	public String authenticate(){
		context = FacesContext.getCurrentInstance();
		menu = (panelMenuClass) context.getApplication()
				.evaluateExpressionGet(context, "#{panelMenuClass}",
						panelMenuClass.class);
		try {
			if (loginSession.authenticateGebruiker(this.getUsername(), this.getPassword()))
			{
				//gebruiker = gebruikerBean.getGebruikerbyName(username);
				//loginSession.setGebruiker(gebruiker);
				//loginSession.
				//return "/main.xhtml?faces-redirect=true";
				return menu.navigateHome();
			}
			else
			{
				message = new FacesMessage();
				gebruiker = loginSession.getGebruiker();    
				if (gebruiker == null)
					message.setDetail("Mislukt!" + " onbekend");
				else
					message.setDetail("Mislukt!" + gebruiker.getName());
				
			    context.addMessage(null, new FacesMessage("Ongeldige gebruikersnaam/wachtwoord",""));
				return "";
			}
		} catch (VerzuimAuthenticationException e) {
		    context.addMessage(null, new FacesMessage("Mislukt!",e.getMessage()));
			return "";
		} catch (VerzuimApplicationException e) {
		    context.addMessage(null, new FacesMessage("Mislukt!",e.getMessage()));
			return "";
		}
	}
	public String requestNewPassword(){
		return "wachtwoordvergeten.xhtml?faces-redirect=true";
	}
	public String newPassword(){
		context = FacesContext.getCurrentInstance();
		menu = (panelMenuClass) context.getApplication()
				.evaluateExpressionGet(context, "#{panelMenuClass}",
						panelMenuClass.class);
		try {
			autorisatieFacade.sendNewPassword(this.getUsername(),this.getEmailadres());
		    context.addMessage(null, new FacesMessage("Er is een nieuw wachtwoord naar uw emailadres verzonden.",""));
			return menu.navigateIndex();
		} catch (PermissionException e) {
		    context.addMessage(null, new FacesMessage("Verzenden nieuw wachtwoord is mislukt!",e.getMessage()));
			return "";
		} catch (ValidationException e) {
		    context.addMessage(null, new FacesMessage("Verzenden nieuw wachtwoord is mislukt!",e.getMessage()));
			return "";
		} catch (VerzuimApplicationException e) {
		    context.addMessage(null, new FacesMessage("Verzenden nieuw wachtwoord is mislukt!",e.getMessage()));
			return "";
		}
	}
	public int getSessionTimeout(){
		context = FacesContext.getCurrentInstance();
		ExternalContext extContext = context.getExternalContext();
		HttpSession session = (HttpSession) extContext.getSession(false);
		if (session == null)
			return 0;
		else{
			sessionTimeout = session.getMaxInactiveInterval();
			logger.info("Session Timeout = " + sessionTimeout);
			lastmillis = System.currentTimeMillis();
			return sessionTimeout * 1000;
		}
	}
	public String onIdle(){
		context = FacesContext.getCurrentInstance();
		/**
		 * Inactivity timer expired. Check if we are still within the session
		 * timeout
		 */
		long currentmillis = System.currentTimeMillis();
		if ((currentmillis - lastmillis)/1000 < (sessionTimeout - 60)){
			;/* There is still more than a minute left */
		}else{
			if ((currentmillis - lastmillis)/1000 > (sessionTimeout)){
				logger.info("Forced logoff");
				return menu.navigateIndex();
			}else{
				logger.info("Inactivity warning");
				if (context != null){
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
							"Geen activiteit.", "U wordt over een minuut afgemeld"));
				}
			}
		}
		return "";
	}
	public void onActive(){
		context = FacesContext.getCurrentInstance();
		if (context != null){
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Welkom terug.", ""));
		}
		return;
	}
	public String forcelogoff(){
		context = FacesContext.getCurrentInstance();
		logger.info("Forced logoff");
		return "/loggedoff.xhtml?faces-redirect=true";
	}
	public void TerminateSession(){
		cleanup();
	}
	public LoginSessionRemote getLoginSession() {
		return loginSession;
	}
	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public String logoffReason(){
		context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
			"Sessie limiet overschreden. U bent automatisch afgemeld.", ""));
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		String view = (String) requestMap.get("currentViewId");
		if (view == null || view.isEmpty()){
			return "";
		} else {
			return "Sessie limiet overschreden. U bent automatisch afgemeld.";
		}
		
	}
	@PreDestroy
	@Override
	public void predestructAction(){
		super.predestructAction();
		logger.debug(this.getClass().toString() + " destructed");
		cleanup();
	}
	private void cleanup(){
		if (autorisatieFacade != null){
			try{
				autorisatieFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling autorisatieFacade.remove()" + e.getMessage());
			}
			autorisatieFacade = null;
		}
		setUsername("");
		setPassword("");
		if (loginSession != null){
			try{
				loginSession.logoffSession();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling loginSession.logoff()" + e.getMessage());
			}
			loginSession = null;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null){
			ExternalContext ctx = context.getExternalContext();
			if (ctx != null){
				ctx.invalidateSession();
			}
		}
	}
	
}
