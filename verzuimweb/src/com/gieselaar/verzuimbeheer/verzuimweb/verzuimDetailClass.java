package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.CascodeFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacade;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;

@ManagedBean
@ViewScoped
public class verzuimDetailClass extends BackingBeanBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;
	@EJB
	VerzuimFacade verzuimFacade;
	@EJB
	CascodeFacade cascodeFacade;

	private LoginSessionRemote loginsession;
	private panelMenuClass menu;

	private WerknemerInfo werknemer;
	private DienstverbandInfo dienstverband;
	private VerzuimInfo verzuim;
	private VerzuimHerstelInfo selectedHerstel;
	private List<VerzuimHerstelInfo> verzuimherstellen = new ArrayList<>();
	private List<VerzuimInfo> verzuimen = new ArrayList<>();

	private boolean modeNew = false;
	private boolean frequentverzuim = false;
	
	private List<CascodeInfo> cascodes;
	private List<SelectItem> cascodeslist = new ArrayList<SelectItem>();

	private String selectedGerelateerdheid;
	private String selectedVerzuimtype;
	private String selectedCascode;
	private __vangnettype selectedVangnet;
	private String meldingswijze;
	private boolean rowSelected = false;

	@PostConstruct
	public void init() {
		context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;
		if (loginsession.getGebruiker() == null)
			return;

		menu = (panelMenuClass) context.getApplication()
				.evaluateExpressionGet(context, "#{panelMenuClass}",
						panelMenuClass.class);

		verzuim = (VerzuimInfo) context.getExternalContext().getSessionMap()
				.get("verzuim");
		werknemer = (WerknemerInfo) context.getExternalContext()
				.getSessionMap().get("werknemer");
		dienstverband = (DienstverbandInfo) context.getExternalContext()
				.getSessionMap().get("dienstverband");
		try {
			cascodeFacade.setLoginSession(loginsession);
			verzuimFacade.setLoginSession(loginsession);
			if (verzuim == null) { /* Nieuwe verzuim */
				modeNew = true;
				verzuim = nieuwverzuim();
				verzuimherstellen = verzuim.getVerzuimherstellen();
			} else {
				verzuim = verzuimFacade.getVerzuimDetails(verzuim.getId());
				verzuimherstellen = verzuim.getVerzuimherstellen();
			}
			verzuimen = verzuimFacade.getVerzuimenWerknemer(verzuim
					.getDienstverbandId());
			
			cascodes = verzuimFacade.getCascodes();
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan details van verzuim niet opvragen! ",e.getMessage()));
			return;
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan details van verzuim niet opvragen! ",e.getMessage()));
			return;
		}

		populateCombos();
		this.setGerelateerdheid(Integer.toString(verzuim.getGerelateerdheid()
				.getValue()));
		this.setVerzuimtype(Integer.toString(verzuim.getVerzuimtype()
				.getValue()));
		this.setVangnet(verzuim.getVangnettype());
		this.setCascode(Integer.toString(verzuim.getCascode()));
		meldingswijze = verzuim.getMeldingswijze().toString();
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
		if (cascodeFacade!=null){
			try{
				cascodeFacade.remove();
			} catch (Exception e) {
				logger.info("Non-fatal exception while calling cascodeFacade.remove()" + e.getMessage());
			}
			cascodeFacade = null;
		}
	}
	
	private VerzuimInfo nieuwverzuim() {
		VerzuimInfo vzm = new VerzuimInfo();
		vzm = new VerzuimInfo();
		vzm.setDienstverbandId(dienstverband.getId());
		vzm.setGerelateerdheid(__gerelateerdheid.NVT);
		vzm.setGebruiker(loginsession.getGebruiker().getId());
		vzm.setKetenverzuim(false);
		vzm.setMeldingsdatum(new Date());
		vzm.setWerknemer(werknemer);
		vzm.setDienstverband(dienstverband);
		vzm.setDienstverbandId(dienstverband.getId());
		vzm.setMeldingswijze(__meldingswijze.INTERNET);
		vzm.setVangnettype(__vangnettype.NVT);
		vzm.setVerzuimtype(__verzuimtype.VERZUIM);
		vzm.setStartdatumverzuim(new Date());
		vzm.setMeldingsdatum(new Date());
		
		List<WiapercentageInfo> wiapercentages = werknemer.getWiaPercentages();
		if (wiapercentages != null) {
			for (WiapercentageInfo wp : wiapercentages) {
				if (wp.getEinddatum() == null
						&& wp.getCodeWiaPercentage() != __wiapercentage.NVT) {
					vzm.setVangnettype(__vangnettype.WIA);
				}
			}
		}

		VerzuimHerstelInfo vzmh = new VerzuimHerstelInfo();
		vzmh.setDatumHerstel(new Date());
		vzmh.setMeldingswijze(__meldingswijze.INTERNET);
		vzmh.setMeldingsdatum(new Date());
		vzmh.setPercentageHerstel(new BigDecimal(0));
		vzmh.setPercentageHerstelAT(new BigDecimal(0));
		vzmh.setUser(loginsession.getGebruiker().getId());

		vzm.setVerzuimherstellen(new ArrayList<VerzuimHerstelInfo>());

		return vzm;
	}

	private void populateCombos() {
		for (CascodeInfo a : cascodes) {
			if (a.isActief()) {
				cascodeslist
						.add(new SelectItem(a.getId(), a.getOmschrijving()));
			} else {
				if (a.getId() == verzuim.getCascode()) {
					cascodeslist.add(new SelectItem(a.getId(), a
							.getOmschrijving()));
				}
			}
		}
	}

	public void editHerstel() {
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Session invalid",context.getViewRoot().getViewId());

		Map<String,Object> options = new HashMap<String, Object>();

		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("verzuim", verzuim);
		context.getExternalContext().getSessionMap()
				.put("verzuimherstel", selectedHerstel);
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        RequestContext.getCurrentInstance().openDialog("herstelDialog", options, null);		
	}

	public void nieuwHerstel() {
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Session invalid",context.getViewRoot().getViewId());

		Map<String,Object> options = new HashMap<String, Object>();

		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("verzuim", verzuim);
		context.getExternalContext().getSessionMap()
				.put("verzuimherstel", null);
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        RequestContext.getCurrentInstance().openDialog("herstelDialog", options, null);		
	}

	public String saveVerzuim() {
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Session invalid",context.getViewRoot().getViewId());
		try {
			context = FacesContext.getCurrentInstance();
			verzuim.setVangnettype(this.selectedVangnet);
			verzuim.setCascode(Integer.parseInt(this.selectedCascode));
			verzuim.validate();
			if (modeNew) {
				frequentverzuim = verzuim.isFrequentverzuim(verzuim, verzuimen);
				verzuim = verzuimFacade.addVerzuim(verzuim);
				context.addMessage(null, new FacesMessage("Gegevens opgeslagen! ",""));
				context.addMessage(null, new FacesMessage("U kunt nu opmerkingen invoeren via Informatiekaart.",""));
				context.getExternalContext().getSessionMap()
						.put("verzuim", verzuim);
				modeNew = false;
			} else{
				frequentverzuim = false;
				verzuimFacade.updateVerzuim(verzuim);
				verzuim = verzuimFacade.getVerzuimDetails(verzuim.getId());
				context.addMessage(null, new FacesMessage("Gegevens gewijzigd! ",""));
			}
		} catch (ValidationException | PermissionException | VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("kan verzuim niet opslaan! ",e.getMessage()));
		}
		return "";
	}
	public String showInformatiekaart(){
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Session invalid",context.getViewRoot().getViewId());
		return menu.navigateVerzuimInformatieKaart();
	}
	public String showDocumenten(){
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Session invalid",context.getViewRoot().getViewId());
		return menu.navigateVerzuimDocumenten();
	}
	public void cascodeChanged(ValueChangeEvent event){
		int cascode = Integer.parseInt((String) event.getNewValue());
		__vangnettype vangnet = this.selectedVangnet;
		for (CascodeInfo cci : cascodes) {
			if (cci.getId() == cascode) {
				/* Geselecteerde cascode gevonden */
				if (cci.getVangnettype().getValue() == vangnet.getValue())
					/* Vangnettype staat al goed */;
				else {
					if (vangnet.getValue() == __vangnettype.WIA.getValue()){
						; // Vangnettype laten staan
					} else {

						if (vangnet.getValue() != cci.getVangnettype()
									.getValue()) {
							context = FacesContext.getCurrentInstance();
							context.addMessage(null, new FacesMessage("Vangnettype gewijzigd! ",""));
							this.setVangnet(cci.getVangnettype());
						}
					}
				}
				break;
			}
		}
	}
	

	public VerzuimInfo getVerzuim() {
		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Session invalid",context.getViewRoot().getViewId());
		return verzuim;
	}
	public void refreshVerzuim(){
		logger.debug("refreshVerzuim");
		context = FacesContext.getCurrentInstance();
		try {
			verzuimFacade.setLoginSession(loginsession);
			verzuim = verzuimFacade.getVerzuimDetails(verzuim.getId());
			verzuimherstellen = verzuim.getVerzuimherstellen();
			context.getExternalContext().getSessionMap().remove("verzuim");
			context.getExternalContext().getSessionMap().put("verzuim",verzuim);
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan details van verzuim niet opvragen! ",e.getMessage()));
			return;
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan details van verzuim niet opvragen! ",e.getMessage()));
			return;
		}
	}

	public boolean getFrequentverzuim(){
		return this.frequentverzuim;
	}
	public void setVerzuim(VerzuimInfo verzuim) {
		this.verzuim = verzuim;
	}

	public List<VerzuimHerstelInfo> getVerzuimherstellen() {
		return verzuimherstellen;
	}

	public void setVerzuimherstellen(List<VerzuimHerstelInfo> verzuimherstellen) {
		this.verzuimherstellen = verzuimherstellen;
	}

	public String getGerelateerdheid() {
		return selectedGerelateerdheid;
	}

	public void setGerelateerdheid(String selectedGerelateerdheid) {
		this.selectedGerelateerdheid = selectedGerelateerdheid;
	}

	public String getVerzuimtype() {
		return selectedVerzuimtype;
	}

	public void setVerzuimtype(String selectedVerzuimtype) {
		this.selectedVerzuimtype = selectedVerzuimtype;
	}

	public String getCascode() {
		return selectedCascode;
	}

	public void setCascode(String selectedCascode) {
		this.selectedCascode = selectedCascode;
	}

	public __vangnettype getVangnet() {
		return selectedVangnet;
	}

	public void setVangnet(__vangnettype selectedVangnet) {
		this.selectedVangnet = selectedVangnet;
	}
	public void clearVangnettype(){
		//this.selectedVangnet = null;
	}
	public List<SelectItem> getCascodeslist() {
		return cascodeslist;
	}

	public void setCascodeslist(List<SelectItem> cascodeslist) {
		this.cascodeslist = cascodeslist;
	}

	public String getMeldingswijze() {
		return meldingswijze;
	}

	public void setMeldingswijze(String meldingswijze) {
		this.meldingswijze = meldingswijze;
	}

	public VerzuimHerstelInfo getSelectedHerstel() {
		return selectedHerstel;
	}

	public void setSelectedHerstel(VerzuimHerstelInfo selectedHerstel) {
		this.selectedHerstel = selectedHerstel;
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
	public boolean isNewVerzuim(){
		return modeNew;
	}
}
