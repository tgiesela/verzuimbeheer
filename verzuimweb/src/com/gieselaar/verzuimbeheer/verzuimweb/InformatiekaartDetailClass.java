package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.SettingsFacade;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacade;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;

@ManagedBean
@ViewScoped
public class InformatiekaartDetailClass extends BackingBeanBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;
	@EJB
	VerzuimFacade verzuimFacade;
	@EJB
	SettingsFacade settingsFacade;
	@EJB 
	AutorisatieFacade autorisatieFacade;

	private LoginSessionRemote loginsession;
	private VerzuimInfo verzuim;
	private VerzuimMedischekaartInfo informatiekaart = null;
	private List<GebruikerInfo> gebruikers;

	private boolean modeNew = false;

	@PostConstruct
	public void init() {
		context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Invalid session");
		verzuim = (VerzuimInfo) context.getExternalContext().getSessionMap()
				.get("verzuim");
		informatiekaart = (VerzuimMedischekaartInfo) context.getExternalContext()
				.getSessionMap().get("informatiekaart");
		try {
			verzuimFacade.setLoginSession(loginsession);
			settingsFacade.setLoginSession(loginsession);
		} catch (PermissionException e) {
			context.addMessage(null, new FacesMessage("Kan niet initialiseren",e.getMessage()));
		}
		if (informatiekaart == null) { /* Nieuwe verzuim */
			modeNew = true;
			informatiekaart = nieuwinformatiekaart();
		}

	}
	@PreDestroy
	@Override
	public void predestructAction(){
		super.predestructAction();
		if (verzuimFacade!=null){
			try{
				verzuimFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling verzuimFacade.remove()" + e.getMessage());
			}
			verzuimFacade = null;
		}
		if (settingsFacade!=null){
			try{
				settingsFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling settingsFacade.remove()" + e.getMessage());
			}
			settingsFacade = null;
		}
	}

	public VerzuimMedischekaartInfo nieuwinformatiekaart() {
		context = FacesContext.getCurrentInstance();
		informatiekaart = new VerzuimMedischekaartInfo();
		informatiekaart.setOpenbaar(true); 
		informatiekaart.setUser(loginsession.getGebruiker().getId());
		informatiekaart.setVerzuimId(verzuim.getId());
		informatiekaart.setWijzigingsdatum(new Date());
		informatiekaart.setAction(persistenceaction.INSERT);
		informatiekaart.setMedischekaart("");
		modeNew = true;
		return informatiekaart;
	}

	public void saveInformatieKaart(){
		TodoInfo todo = new TodoInfo();
		context = FacesContext.getCurrentInstance();
		try {
			SettingsInfo settings = settingsFacade.getSettings();
			if (modeNew) {
				verzuimFacade.addMedischekaart(informatiekaart);
				context.addMessage(null, new FacesMessage("Informatiekaart opgeslagen",""));
				
				todo.setUser(loginsession.getGebruiker().getId());
				todo.setAchternaam(loginsession.getGebruiker().getAchternaam());
				todo.setDeadlinedatum(new Date());
				todo.setHerhalen(false);
				todo.setSoort(__soort.HANDMATIG);
				todo.setVerzuimId(verzuim.getId());
				todo.setWaarschuwingsdatum(new Date());
				todo.setAanmaakdatum(new Date());
				Integer id = settings.getTodoforinformatiekaart();
				todo.setActiviteitId(id);
				todo.setWaarschuwingsdatum(new Date());
				todo.setDeadlinedatum(new Date());
				todo.setHerhalen(false);
				todo.setOpmerking("Toevoeging aan medische kaart door Internet gebruiker");
				
				verzuimFacade.addTodo(todo);
				modeNew = false;				
			} else {
				verzuimFacade.updateMedischekaart(informatiekaart);

				todo.setUser(loginsession.getGebruiker().getId());
				todo.setAchternaam(loginsession.getGebruiker().getAchternaam());
				todo.setDeadlinedatum(new Date());
				todo.setHerhalen(false);
				todo.setSoort(__soort.HANDMATIG);
				todo.setVerzuimId(verzuim.getId());
				todo.setWaarschuwingsdatum(new Date());
				todo.setAanmaakdatum(new Date());
				Integer id = settings.getTodoforinformatiekaart();
				todo.setActiviteitId(id);
				todo.setWaarschuwingsdatum(new Date());
				todo.setDeadlinedatum(new Date());
				todo.setHerhalen(false);
				todo.setOpmerking("Wijziging in medische kaart door Internet gebruiker");
				
				verzuimFacade.addTodo(todo);
				context.addMessage(null, new FacesMessage("Informatiekaart gewijzigd",""));
			}
			verzuim = verzuimFacade.getVerzuimDetails(verzuim.getId());
		} catch (ValidationException | PermissionException | VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("kan informatiekaart niet opslaan!",e.getMessage()));
		} finally {
			RequestContext.getCurrentInstance().closeDialog("informatiekaartDialog");
		}
	}
	private String GebruikerIdToName(int id) throws PermissionException, VerzuimApplicationException{
		GebruikerInfo gebruiker = null;
		if (gebruikers == null){
			autorisatieFacade.setLoginSession(loginsession);
			gebruikers = autorisatieFacade.getGebruikers();
		}
		if (gebruikers != null){
			for (GebruikerInfo gi: gebruikers){
				if (gi.getId().equals(id)){
					gebruiker = gi;
					break;
				}
			}
		}
		
		if (gebruiker != null){
			return gebruiker.getVoornaam() + " " + gebruiker.getTussenvoegsel() + " " + gebruiker.getAchternaam();
		}else{
			return "?";
		}

	}
	public VerzuimMedischekaartInfo getInformatiekaart() {
		return informatiekaart;
	}

	public void setInformatiekaart(VerzuimMedischekaartInfo informatiekaart) {
		this.informatiekaart = informatiekaart;
	}
	public boolean isKaartEditable(){
		if (informatiekaart == null)
			return false;
		if (informatiekaart.getUser() != loginsession.getGebruiker().getId())
			return false;
		return true;
	}
	
	public String getGebruikernaam(){
		context = FacesContext.getCurrentInstance();
		try {
			return GebruikerIdToName(informatiekaart.getUser());
		} catch (PermissionException | VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("Probleem bij opvragen gebruikernaam",e.getMessage()));
			return "?";
		}
	}

}
