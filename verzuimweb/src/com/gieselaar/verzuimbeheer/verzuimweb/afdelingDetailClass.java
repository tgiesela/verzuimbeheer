package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacade;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;

@ManagedBean
@ViewScoped
public class afdelingDetailClass extends BackingBeanBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;

	@EJB
	WerkgeverFacade werkgeverFacade;
	private LoginSessionRemote loginsession;

	private AfdelingInfo afdeling;
	private ContactpersoonInfo contactpersoon;

	private List<SelectItem> geslachten = new ArrayList<SelectItem>();
	private Integer selectedWerkgever = null;
	private String geslachtcontactpersoon;

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
		afdeling = (AfdelingInfo) context.getExternalContext()
				.getSessionMap().get("werkgeverafdeling");
		selectedWerkgever = (Integer) context.getExternalContext()
				.getSessionMap().get("werkgeverId");
		if (afdeling == null) { /* Nieuwe afdeling */
			modeNew = true;
			afdeling = nieuweAfdeling();
			contactpersoon = new ContactpersoonInfo();
		}
		else {

			contactpersoon = afdeling.getContactpersoon();
			if (contactpersoon == null)
				contactpersoon = new ContactpersoonInfo();
		}
		for (__geslacht g : __geslacht.values()) {
			geslachten.add(new SelectItem(g.getValue(), g.toString()));
		}
	}
	@PreDestroy
	@Override
	public void predestructAction(){
		super.predestructAction();
		if (werkgeverFacade!=null){
			try{
				werkgeverFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling werkgeverFacade.remove()" + e.getMessage());
			}
			werkgeverFacade = null;
		}
	}

	public AfdelingInfo nieuweAfdeling() {
		AfdelingInfo afd = new AfdelingInfo();
		afd.setAfdelingsid("");
		afd.setWerkgeverId(selectedWerkgever);
		afd.setNaam("");
		return afd;
	}

	public String save() {
		FacesContext context;
		context = FacesContext.getCurrentInstance();
		try {

			werkgeverFacade.setLoginSession(loginsession);
			if (contactpersoon.isEmpty())
				afdeling.setContactpersoon(null);
			else
			{
				afdeling.setContactpersoon(contactpersoon);
			}
			if (modeNew){
				werkgeverFacade.addAfdeling(afdeling);
				context.addMessage(null, new FacesMessage("Afdeling toegevoegd",""));
				modeNew = false;
			}
			else{
				werkgeverFacade.updateAfdeling(afdeling);
				context.addMessage(null, new FacesMessage("Afdeling gewijzigd",""));
			}
		} catch (PermissionException | ValidationException | VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("Opslaan niet geslaagd! ",e.getMessage()));
			return "";
		}
		return "";
	}

	public AfdelingInfo getAfdeling() {
		return afdeling;
	}

	public ContactpersoonInfo getAfdelingcontactpersoon(){
		return contactpersoon;
	}
	public void setAfdeling(AfdelingInfo afdeling) {
		this.afdeling = afdeling;
	}
	public List<SelectItem> getGeslachten() {
		return geslachten;
	}

	public void setGeslachten(List<SelectItem> geslachten) {
		this.geslachten = geslachten;
	}

	public String getGeslachtcontactpersoon() {
		return geslachtcontactpersoon;
	}

	public void setGeslachtcontactpersoon(String geslachtcontactpersoon) {
		this.geslachtcontactpersoon = geslachtcontactpersoon;
	}

}
