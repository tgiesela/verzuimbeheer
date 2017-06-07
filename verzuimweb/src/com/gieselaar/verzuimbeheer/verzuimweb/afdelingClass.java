package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacade;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

@ManagedBean
@ViewScoped
public class afdelingClass extends BackingBeanBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FacesContext context;
	@EJB
	WerkgeverFacade werkgeverFacade;
	private LoginSessionRemote loginsession;
	private panelMenuClass menu;
	private Integer selectedWerkgever = null;
	private WerkgeverInfo werkgever;
	private AfdelingInfo selectedAfdeling;
	private boolean rowSelected = false;
	
	@PostConstruct
	public void init() {
		FacesMessage message;
		context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		menu = (panelMenuClass) context.getApplication()
				.evaluateExpressionGet(context, "#{panelMenuClass}",
						panelMenuClass.class);
		loginsession = sessioncontext.loginSession;
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Invalid session");
		
		selectedWerkgever = (Integer) context.getExternalContext()
				.getSessionMap().get("werkgeverId");
		try {
			werkgeverFacade.setLoginSession(loginsession);
			setWerkgever(werkgeverFacade.getWerkgever(selectedWerkgever));
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			message = new FacesMessage();
			message.setDetail("Werkgever niet gevonden! " + e.getMessage());
			context.addMessage("form", message);
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			message = new FacesMessage();
			message.setDetail(e.getMessage());
			context.addMessage("form", message);
		}
	}
	@PreDestroy
	@Override
	public void predestructAction(){
		super.predestructAction();
		logger.debug(this.getClass().toString() + " destroyed");
		if (werkgeverFacade!=null){
			try{
				werkgeverFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling werkgeverFacade.remove()" + e.getMessage());
			}
			werkgeverFacade = null;
		}
	}

	public String editAfdeling() {
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.put("werkgeverafdeling", selectedAfdeling);
		return menu.navigateAfdeling();
	}

	public String nieuweAfdeling(){
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.remove("werkgeverafdeling");
		return menu.navigateAfdeling();
	}

	public WerkgeverInfo getWerkgever() {
		return werkgever;
	}

	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
	}
	public void setSelectedAfdeling(AfdelingInfo afdeling) {
		this.selectedAfdeling = afdeling;
	}
	public AfdelingInfo getSelectedAfdeling() {
		return selectedAfdeling;
	}
	public void rowSelected(SelectEvent event) {
		rowSelected = true;
	}

	public boolean isRowSelected() {
		return rowSelected;
	}

	public void setRowSelected(boolean rowSelected) {
		this.rowSelected = rowSelected;
	}
}
