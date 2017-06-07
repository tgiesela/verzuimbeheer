package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.faces.model.SelectItem;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacade;
import com.gieselaar.verzuimbeheer.facades.WerknemerFacade;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;

@ManagedBean
@ViewScoped
public class werknemerDetailClass extends BackingBeanBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;

	@EJB
	WerknemerFacade werknemerFacade;
	@EJB
	WerkgeverFacade werkgeverFacade;
	private LoginSessionRemote loginsession;
	private panelMenuClass menu;

	private WerknemerInfo werknemer;

	private List<AfdelingInfo> afdelingenWerkgever;
	private List<SelectItem> afdelingen = new ArrayList<SelectItem>();

	private String selectedAfdeling;
	private Integer selectedWerkgever = null;

	private DienstverbandInfo dienstverband;
	private DienstverbandInfo laatsteDvb;
	private boolean modeNew = false;
	private __wiapercentage selectedWiapercentage;
	@PostConstruct
	public void init() {
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
		werknemer = (WerknemerInfo) context.getExternalContext()
				.getSessionMap().get("werknemer");
		selectedWerkgever = (Integer) context.getExternalContext()
				.getSessionMap().get("werkgeverId");
		try {
			werknemerFacade.setLoginSession(loginsession);
			if (werknemer != null){
				modeNew = false;
				werknemer = werknemerFacade.getWerknemer(werknemer.getId());
			}
			else{
				modeNew = true;
				werknemer = nieuweWerknemer();
			}
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan details van werknemer niet opvragen!",e.getMessage()));
			return;
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan details van werknemer niet opvragen!",e.getMessage()));
			return;
		}

		this.dienstverband = new DienstverbandInfo();
		copyDienstverband(this.dienstverband,
				werknemer.getLaatsteDienstverband());
		
		try {
			werkgeverFacade.setLoginSession(loginsession);
			afdelingenWerkgever = werkgeverFacade.getAfdelingenWerkgever(selectedWerkgever);
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan afdelingen van werkgever niet opvragen! ",e.getMessage()));
			return;
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan afdelingen van werkgever niet opvragen! ",e.getMessage()));
			return;
		}

		populateCombos();
		if (modeNew){
			selectedAfdeling = ((SelectItem) afdelingen.get(0)).getValue().toString();
		}
		else{
			if (werknemer.getLaatsteAfdeling() != null)
				selectedAfdeling = Integer.toString(werknemer.getLaatsteAfdeling()
						.getAfdelingId());
			else
				selectedAfdeling = ((SelectItem) afdelingen.get(0)).getValue()
						.toString();
		}
	}
	@PreDestroy
	@Override
	public void predestructAction(){
		super.predestructAction();
		if (werknemerFacade!=null){
			try{
				werknemerFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling werknemerFacade.remove()" + e.getMessage());
			}
			werknemerFacade = null;
		}
		if (werkgeverFacade!=null){
			try{
				werkgeverFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling werkgeverFacade.remove()" + e.getMessage());
			}
			werkgeverFacade = null;
		}
	}

	private void populateCombos() {
		for (AfdelingInfo a: afdelingenWerkgever)
        {
        	afdelingen.add(new SelectItem(a.getId(), a.getNaam()));
        }
		
	}

	public WerknemerInfo nieuweWerknemer() {
		WerknemerInfo wnr = new WerknemerInfo();
		wnr = new WerknemerInfo();
		wnr.setAdres(new AdresInfo());
		wnr.setGeslacht(__geslacht.MAN);
		wnr.setBurgerlijkestaat(__burgerlijkestaat.ONBEKEND);
		wnr.setAchternaam("");
		wnr.setVoorletters("");
		wnr.setVoornaam("");
		wnr.setBurgerservicenummer("");
		wnr.setEmail("");
		wnr.setMobiel("");
		DienstverbandInfo dvb = new DienstverbandInfo();
		dvb.setEinddatumcontract(null);
		dvb.setStartdatumcontract(new Date());
		dvb.setWerkgeverId(selectedWerkgever);
		dvb.setWerknemer(wnr);
		dvb.setWerknemerId(wnr.getId());
		dvb.setWerkweek(new BigDecimal(0));
		laatsteDvb = new DienstverbandInfo();
		laatsteDvb.setStartdatumcontract(dvb.getStartdatumcontract());
		laatsteDvb.setEinddatumcontract(dvb.getEinddatumcontract());

		wnr.setDienstVerbanden(new ArrayList<DienstverbandInfo>());
		wnr.getDienstVerbanden().add(dvb);
		this.dienstverband = dvb;

		WiapercentageInfo wp = new WiapercentageInfo();
		wp.setCodeWiaPercentage(__wiapercentage.NVT);
		wp.setStartdatum(new Date());
		wp.setWerknemer(wnr);
		wp.setWerknemerId(wnr.getId());
		wnr.setWiaPercentages(new ArrayList<WiapercentageInfo>());
		wnr.getWiaPercentages().add(wp);

		wnr.setAfdelingen(new ArrayList<AfdelingHasWerknemerInfo>());

		wnr.setWerkgeverid(selectedWerkgever);

		return wnr;
	}

	private void copyDienstverband(DienstverbandInfo target,
			DienstverbandInfo source) {
		target.setId(source.getId());
		target.setStartdatumcontract(source.getStartdatumcontract());
		target.setEinddatumcontract(source.getEinddatumcontract());
		target.setFunctie(source.getFunctie());
		target.setPersoneelsnummer(source.getPersoneelsnummer());
		target.setWerkweek(source.getWerkweek());
		if (source.getWerkgever() != null) {
			target.setWerkgever(source.getWerkgever());
			target.setWerkgeverId(source.getWerkgeverId());
		}
		if (source.getWerknemer() != null) {
			target.setWerknemer(source.getWerknemer());
			target.setWerknemerId(source.getWerknemerId());
		}
	}

	public void save() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (werknemer.getLaatsteWiapercentage().getCodeWiaPercentage()
					!= selectedWiapercentage) {
				/*
				 * wia percentage is gewijzigd. Nieuwe toevoegen aan de tabel en
				 * laatste afsluiten.
				 */

				WiapercentageInfo wp = new WiapercentageInfo();
				wp.setCodeWiaPercentage(selectedWiapercentage);
				wp.setStartdatum(new Date());
				wp.setEinddatum(null);
				werknemer.getLaatsteWiapercentage().setEinddatum(wp.getStartdatum());
				werknemer.getWiaPercentages().add(wp);
			}
			/* 
			 * this.dienstverband bevat de informatie die op het
			 * het scherm wordt getoond. 
			 * laatsteDvb wijst naar het laatste dienstverband in
			 * WerknemerInfo. Die moet worden overschreven, behalve
			 * als het laatste dienstverband afgesloten is. 
			 */
			laatsteDvb = werknemer.getLaatsteDienstverband();
			if (this.dienstverband.getId() == laatsteDvb.getId()) {
				if (laatsteDvb.getEinddatumcontract() != null) {
					// Laatste dienstverband is afgesloten
					if (this.dienstverband.getEinddatumcontract() == null) {
						if (laatsteDvb.getStartdatumcontract().equals(this.dienstverband.getStartdatumcontract())){
							/*
							 * Waarschijnlijk per ongeluk uit dienst gemeld, dan activeren we het
							 * dvb weer.
							 */
							copyDienstverband(laatsteDvb, this.dienstverband);
						} else {
							// Nieuw dienstverband maken
							DienstverbandInfo nieuwDvb = new DienstverbandInfo();
							copyDienstverband(nieuwDvb, this.dienstverband);
							werknemer.getDienstVerbanden().add(nieuwDvb);
						}
					} else {
						// gegevens overnemen
						copyDienstverband(laatsteDvb, this.dienstverband);
					}
				}
				else
					copyDienstverband(laatsteDvb, this.dienstverband);
			}
			if ((this.getAfdelingen().size() > 0)
					&& (this.werknemer.getLaatsteAfdeling() != null)
					&& (Integer.parseInt(this.selectedAfdeling) == this.werknemer
							.getLaatsteAfdeling().getAfdelingId()))
				; // geen wijzigingen
			else {
				AfdelingHasWerknemerInfo newafd = new AfdelingHasWerknemerInfo();
				newafd.setAfdelingId(Integer.parseInt(this.selectedAfdeling));
				newafd.setWerknemerId(this.werknemer.getId());
				newafd.setStartdatum(new Date());
				newafd.setEinddatum(null);
				if (werknemer.getLaatsteAfdeling() != null)
					werknemer.getLaatsteAfdeling().setEinddatum(new Date());
				werknemer.getAfdelingen().add(newafd);
			}

			werknemerFacade.setLoginSession(loginsession);
			if (werknemer.getId() <= 0){
				werknemerFacade.addWerknemer(werknemer);
				context.addMessage(null, new FacesMessage("Gegevens toegevoegd! ",""));
			}
			else{
				werknemerFacade.updateWerknemer(werknemer);
				context.addMessage(null, new FacesMessage("Gegevens gewijzigd! ",""));
			}
			werknemer = werknemerFacade.getWerknemer(werknemer.getId());
			this.modeNew = false;
		} catch (PermissionException | ValidationException e) {
			context.addMessage(null, new FacesMessage("Opslaan niet geslaagd.",e.getMessage()));
			//clearComponent(FacesContext.getCurrentInstance().getViewRoot());
		} catch (VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("Opslaan niet geslaagd.",e.getMessage()));
		}
	}

	public String verzuimen(){
		menu.navigateVerzuimen();
		if (this.modeNew){
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Let op!","Werknemer eerst opslaan."));
			return "";
		}
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.put("werknemer", werknemer);
		return "werknemerverzuimen";
	}
	
	public String verzuimhistorie(){
		menu.navigateVerzuimhistorie();
		if (this.modeNew){
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Let op!","Werknemer eerst opslaan."));
			return "";
		}
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.put("werknemer", werknemer);
		return "verzuimhistorie.xhtml?faces-redirect=true";
	}

	public String getAfdeling() {
		return selectedAfdeling;
	}

	public void setAfdeling(String selectedAfdeling) {
		this.selectedAfdeling = selectedAfdeling;
	}

	public List<SelectItem> getAfdelingen() {
		return afdelingen;
	}

	public void setAfdelingen(List<SelectItem> afdelingen) {
		this.afdelingen = afdelingen;
	}

	public WerknemerInfo getWerknemer() {
		return werknemer;
	}

	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}

	public DienstverbandInfo getDienstverband() {
		return dienstverband;
	}
	public void setDienstverband(DienstverbandInfo dienstverband) {
		this.dienstverband = dienstverband;
	}

	public __wiapercentage getSelectedWiapercentage() {
		return selectedWiapercentage;
	}

	public void setSelectedWiapercentage(__wiapercentage selectedWiapercentage) {
		this.selectedWiapercentage = selectedWiapercentage;
	}

}
