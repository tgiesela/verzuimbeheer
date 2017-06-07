package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

@ManagedBean
@ViewScoped
public class gebruikerClass extends BackingBeanBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB AutorisatieFacade autorisatieFacade;

	private FacesContext context;
	private LoginSessionRemote loginsession;
	private GebruikerInfo gebruiker;
	private String achternaam;
	private String voornaam;
	private String gebruikersnaam;
	private String tussenvoegsel;
	private String emailadres;
	private String password;
	private String oldpassword;
	private String newpassword;
	private String newpasswordRepeated;
	private boolean isADuser;
	
	public gebruikerClass(){
	}
	@PostConstruct
	public void init(){
		context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Invalid session");
		if (loginsession != null)
		{
			try {
				gebruiker = loginsession.getGebruiker();
				autorisatieFacade.setLoginSession(loginsession);
				gebruiker = autorisatieFacade.getGebruiker(gebruiker.getId());
				this.setAchternaam(gebruiker.getAchternaam());
				this.setVoornaam(gebruiker.getVoornaam());
				this.setGebruikersnaam(gebruiker.getName());
				this.setEmailadres(gebruiker.getEmailadres());
				this.setTussenvoegsel(gebruiker.getTussenvoegsel());
				this.setADUser(gebruiker.isAduser());
			} catch (PermissionException | VerzuimApplicationException e) {
				context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(
						"Probleem bij opvragen gebruikergegevens ", e.getMessage()));
				return;
			}
		}
	}
	@Override
	public void predestructAction(){
		super.predestructAction();
		if (autorisatieFacade!=null){
			try{
				autorisatieFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling autorisatieFacade.remove()" + e.getMessage());
			}
			autorisatieFacade  = null;
		}
	}

	/*
	 *  UI Components
	 */
	
	/*
	 *  Functions called from buttons etc.
	 */

	public String opslaan(){
		context = FacesContext.getCurrentInstance();
		try {
			gebruiker.setAchternaam(this.getAchternaam());
			gebruiker.setEmailadres(this.getEmailadres());
			gebruiker.setName(this.getGebruikersnaam());
			gebruiker.setVoornaam(this.getVoornaam());
			gebruiker.setNewPassword(this.getPassword());
			autorisatieFacade.setLoginSession(loginsession);
			autorisatieFacade.updateGebruiker(gebruiker);
			gebruiker = autorisatieFacade.getGebruiker(gebruiker.getId());
			//loginsession.setGebruiker(gebruiker);
			context.addMessage(null, new FacesMessage("Gegevens opgeslagen",""));
		} catch (PermissionException | ValidationException | VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Opslaan niet geslaagd!",e.getMessage()));
			return "";
		}
		return "";
	}
	public String changePassword(){
		context = FacesContext.getCurrentInstance();
		try{
			if (oldpassword.isEmpty() || newpassword.isEmpty() || newpasswordRepeated.isEmpty()){
				context.addMessage(null, new FacesMessage("Alle wachtwoorden moet ingevuld worden!",""));
				return "";
			}
				
			if (newpassword.equals(newpasswordRepeated)){
				gebruiker.setNewPassword(newpassword);
				autorisatieFacade.setLoginSession(loginsession);
				autorisatieFacade.updateGebruiker(gebruiker);
				gebruiker = autorisatieFacade.getGebruiker(gebruiker.getId());
				//loginsession.setGebruiker(gebruiker);
				context.addMessage(null, new FacesMessage("Wachtwoord gewijzigd",""));
			} else {
				context.addMessage(null, new FacesMessage("Nieuw wachtwoorden verschillen",""));
			}
		} catch (PermissionException | ValidationException | VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("Wachtwoord niet gewijzigd",e.getMessage()));
			return "";
		}
		return "";
	}

	/*
	 * Properties for fields on screen
	 */
	
	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getTussenvoegsel() {
		return tussenvoegsel;
	}

	public void setTussenvoegsel(String tussenvoegsel) {
		this.tussenvoegsel = tussenvoegsel;
	}

	public String getGebruikersnaam() {
		return gebruikersnaam;
	}

	public void setGebruikersnaam(String gebruikersnaam) {
		this.gebruikersnaam = gebruikersnaam;
	}

	public String getEmailadres() {
		return emailadres;
	}

	public void setEmailadres(String emailadres) {
		this.emailadres = emailadres;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public void setADUser(boolean aduser){
		this.isADuser = aduser;
	}
	public boolean getADUser(){
		return this.isADuser;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getNewpasswordRepeated() {
		return newpasswordRepeated;
	}

	public void setNewpasswordRepeated(String newpasswordRepeated) {
		this.newpasswordRepeated = newpasswordRepeated;
	}
}
