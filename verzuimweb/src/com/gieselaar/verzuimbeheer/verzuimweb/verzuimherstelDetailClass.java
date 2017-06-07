package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacade;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;

@ManagedBean
@ViewScoped
public class verzuimherstelDetailClass extends BackingBeanBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;
	@EJB
	VerzuimFacade verzuimFacade;

	private LoginSessionRemote loginsession;
	private VerzuimInfo verzuim;
	private VerzuimHerstelInfo verzuimherstel = null;

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
		verzuimherstel = (VerzuimHerstelInfo) context.getExternalContext()
				.getSessionMap().get("verzuimherstel");
		try {
			verzuimFacade.setLoginSession(loginsession);
		} catch (PermissionException e) {
			context.addMessage(null, new FacesMessage("Kan niet initialiseren",e.getMessage()));
		}
		if (verzuimherstel == null) { /* Nieuwe verzuim */
			modeNew = true;
			verzuimherstel = nieuwherstel();
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
	}

	public VerzuimHerstelInfo nieuwherstel() {
		verzuimherstel = new VerzuimHerstelInfo();
		verzuimherstel.setMeldingsdatum(new Date());
		verzuimherstel.setMeldingswijze(__meldingswijze.INTERNET);
		verzuimherstel.setDatumHerstel(new Date());
		verzuimherstel.setPercentageHerstel(new BigDecimal(100));
		verzuimherstel.setPercentageHerstelAT(new BigDecimal(0));
		verzuimherstel.setUser(loginsession.getGebruiker().getId());
		verzuimherstel.setVerzuim(verzuim);
		verzuimherstel.setVerzuimId(verzuim.getId());
		return verzuimherstel;
	}

	public String saveHerstel() {
		boolean cleanupTodo = false;
		context = FacesContext.getCurrentInstance();
		try {
			verzuimherstel.setVerzuim(verzuim);
			verzuimherstel.validate();
			if (verzuimherstel.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
				/*
				 * Vraag of de todo's opgeschoond moeten worden.
				 */
				cleanupTodo = true;
			}
			if (modeNew) {
				verzuimFacade.addVerzuimHerstel(verzuimherstel, cleanupTodo);
				context.addMessage(null, new FacesMessage("Herstel opgeslagen",""));
				modeNew = false;
			} else {
				verzuimFacade.updateVerzuimHerstel(verzuimherstel, cleanupTodo);
				context.addMessage(null, new FacesMessage("Herstel gewijzigd",""));
			}
		} catch (ValidationException | PermissionException | VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("kan herstel niet opslaan!",e.getMessage()));
		} finally {
			RequestContext.getCurrentInstance().closeDialog("herstelDialog");
		}
		return "";
	}

	public VerzuimHerstelInfo getVerzuimherstel() {
		return verzuimherstel;
	}

	public void setVerzuimherstel(VerzuimHerstelInfo verzuimherstel) {
		this.verzuimherstel = verzuimherstel;
	}

}
