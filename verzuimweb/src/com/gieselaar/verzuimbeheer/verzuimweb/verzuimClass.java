package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import reportdatasources.VerzuimHistorieDatasource;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.CascodeFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacade;
import com.gieselaar.verzuimbeheer.facades.WerknemerFacade;
import com.gieselaar.verzuimbeheer.helpers.VerzuimInfoWeb;
//import com.gieselaar.verzuimbeheer.reporthelpers.ReportSourceClass;
//import com.gieselaar.verzuimbeheer.reporthelpers.ReportTestClass;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;

@ManagedBean
@ViewScoped
public class verzuimClass extends BackingBeanBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;

	@EJB
	WerknemerFacade werknemerFacade;
	@EJB
	VerzuimFacade verzuimFacade;
	@EJB
	CascodeFacade cascodeFacade;
	private LoginSessionRemote loginsession;
	private panelMenuClass menu;

	private WerknemerInfo werknemer;
	private List<VerzuimInfo> verzuimen = new ArrayList<VerzuimInfo>();
	private List<VerzuimInfoWeb> verzuimenWeb = new ArrayList<VerzuimInfoWeb>();
	private List<VerzuimHerstelInfo> herstellen = new ArrayList<VerzuimHerstelInfo>();
	private List<CascodeInfo> cascodes = new ArrayList<CascodeInfo>();
	private VerzuimInfo verzuim = new VerzuimInfo();
	private HashMap<Integer, String> cascodeHT;
	private Integer currentVerzuimIndex;
	private boolean rowSelected = false;
	private String selectedGeslacht;
	private String selectedBurgerlijkestaat;
	private Date startdatumperiode;
	private Date einddatumperiode;
	
	private VerzuimInfoWeb selectedVerzuim;
	
	private static Logger log = Logger.getLogger(verzuimClass.class);

	@PostConstruct
	public void init() {
		context = FacesContext.getCurrentInstance();
		
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;

		if (loginsession.getGebruiker() == null)
			throw new ViewExpiredException("Invalid session",context.getViewRoot().getViewId());

		menu = (panelMenuClass) context.getApplication()
				.evaluateExpressionGet(context, "#{panelMenuClass}",
						panelMenuClass.class);
		
		
		werknemer = (WerknemerInfo) context.getExternalContext()
				.getSessionMap().get("werknemer");
		if (werknemer == null){
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Werknemer niet gevonden!!! ", ""));
			return;
		}
		try {
			verzuimFacade.setLoginSession(loginsession);
			cascodeFacade.setLoginSession(loginsession);
			werknemerFacade.setLoginSession(loginsession);
			cascodes = cascodeFacade.allCascodes();
			verzuimen = verzuimFacade.getVerzuimenWerknemer(werknemer.getLaatsteDienstverband().getId());
			if (verzuimen != null){
				VerzuimInfo.sortreverse(verzuimen, VerzuimInfo.__sortcol.STARTDATUM);
				herstellen = verzuimFacade.getVerzuimHerstellen(werknemer.getLaatsteDienstverband().getId());
				for (VerzuimInfo v: verzuimen)
				{
					List<VerzuimHerstelInfo> hl = new ArrayList<VerzuimHerstelInfo>();
					for (VerzuimHerstelInfo h: herstellen){
						if (v.getId() == h.getVerzuimId())
							hl.add(h);
					}
					v.setVerzuimherstellen(hl);
				}
				cascodeHT = new HashMap<Integer,String>();
				for (CascodeInfo cc: cascodes)
					cascodeHT.put(cc.getId(), cc.getOmschrijving());

				verzuimenWeb = new ArrayList<VerzuimInfoWeb>();
				for (VerzuimInfo vi: verzuimen){
					VerzuimInfoWeb viw = new VerzuimInfoWeb(vi);
					viw.setCascodeOmschrijving(cascodeHT.get(vi.getCascode()));
					verzuimenWeb.add(viw);
				}
			} else {
				herstellen = null;
			}
			
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Verzuimen niet gevonden!!! ",e.getMessage()));
			return;
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Verzuimen niet gevonden!!! ",e.getMessage()));
			return;
		}
		einddatumperiode = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(einddatumperiode);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH,1);
		startdatumperiode = null;
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
	
	public String edit() {
		verzuim = selectedVerzuim.getVerzuim();
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.put("verzuim", verzuim);
		context.getExternalContext().getSessionMap()
				.put("dienstverband", werknemer.getLaatsteDienstverband());
		return menu.navigateVerzuimDetail();
	}
	public String rapportTonen(){

		context = FacesContext.getCurrentInstance();
		if (startdatumperiode == null){
			context.addMessage(null, new FacesMessage("Startdatum mag niet leeg zijn",""));
			return "";
		}
		if (startdatumperiode.after(einddatumperiode) || startdatumperiode.equals(einddatumperiode)){
			context.addMessage(null, new FacesMessage("Startdatum moet voor de einddatum liggen",""));
			return "";
		}
			
		
		HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
		try {
			VerzuimHistorieDatasource ds = new VerzuimHistorieDatasource(startdatumperiode, einddatumperiode, loginsession);
			JasperPrint report = ds.getReport(werknemer.getId());
			if (report == null){
				context.addMessage(null, new FacesMessage("Probleem bij aanmaken rapport","getReport() is leeg"));
				return "";
			}
			
			response.reset();

			JasperExportManager.exportReportToPdfStream(report, response.getOutputStream());

			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch ( JRException | IOException e) {
			context.addMessage(null, new FacesMessage("Probleem bij aanmaken rapport",e.getMessage()));
			return "";
		} catch (ValidationException e) {
			context.addMessage(null, new FacesMessage(e.getMessage(),""));
			return "";
		}

		FacesContext.getCurrentInstance().responseComplete();
		return "";
	}

	public String nieuwVerzuim() {
		if (werknemer.hasOpenVerzuim(werknemer.getLaatsteDienstverband()))
		{
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("U kunt geen nieuw verzuim aanmaken. Er is nog een open verzuim.",""));
			return "";
		}
		else
		{
		context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("verzuim");
			context.getExternalContext().getSessionMap()
					.put("dienstverband", werknemer.getLaatsteDienstverband());
			return menu.navigateVerzuimDetail();
		}
	}
	public void herstelInvoeren(){
		
	}
	public void delete() {
		verzuim = verzuimen.get(currentVerzuimIndex);
		try {
			verzuimFacade.deleteVerzuim(verzuim);
			verzuimen.remove(verzuim);
			// werknemer.getVerzuimen().remove(verzuim);
		} catch (PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Verzuim kan niet worden verwijderd!!! ", e.getMessage()));
			return;
		} catch (VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Verzuim kan niet worden verwijderd!!! ", e.getMessage()));
			return;
		}
	}

	public String getBurgerlijkestaat() {
		return selectedBurgerlijkestaat;
	}
	public void setBurgerlijkestaat(String selectedBurgerlijkestaat) {
		if (selectedBurgerlijkestaat.equals(this.selectedBurgerlijkestaat))
			;
		else {
			this.selectedBurgerlijkestaat = selectedBurgerlijkestaat;
		}
	}

	public String getGeslacht() {
		return selectedGeslacht;
	}
	public void setGeslacht(String selectedgeslacht) {
		if (selectedgeslacht.equals(this.selectedGeslacht))
			;
		else {
			this.selectedGeslacht = selectedgeslacht;
		}
	}

	public WerknemerInfo getWerknemer() {
		if (werknemer == null)
			logger.debug("WERKNEMER IS NULL!!");
		return werknemer;
	}
	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}
	public boolean hasOpenVerzuim(){
		return werknemer.hasOpenVerzuim(werknemer.getLaatsteDienstverband());
	}
	public List<VerzuimInfoWeb> getVerzuimen() {
		return verzuimenWeb;
	}
	public void setVerzuimen(List<VerzuimInfoWeb> verzuimen) {
		this.verzuimenWeb = verzuimen;
	}

	public Integer getCurrentVerzuimIndex() {
		return currentVerzuimIndex;
	}
	public void setCurrentVerzuimIndex(Integer currentVerzuimIndex) {
		this.currentVerzuimIndex = currentVerzuimIndex;
	}

	public void setSelectedVerzuim(VerzuimInfoWeb verzuim) {
		this.selectedVerzuim = verzuim;
	}
	public VerzuimInfoWeb getSelectedVerzuim(){
		return selectedVerzuim;
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

	public Date getStartdatumperiode() {
		return startdatumperiode;
	}

	public void setStartdatumperiode(Date startdatumperiode) {
		this.startdatumperiode = startdatumperiode;
	}

	public Date getEinddatumperiode() {
		return einddatumperiode;
	}

	public void setEinddatumperiode(Date einddatumperiode) {
		this.einddatumperiode = einddatumperiode;
	}

/*
 * Alternatief voor CrystalReports viewer:
 * 
 * CrystalReportViewer viewer = new CrystalReportViewer();
 * 
 * viewer.setReportSource(reportClientDocument.getReportSource());
 * // tell the viewer to display the report
 * viewer.processHttpRequest(request,
 *                           response,
 *                           getServletConfig().getServletContext(),
 *                           null);
 */
	
}
