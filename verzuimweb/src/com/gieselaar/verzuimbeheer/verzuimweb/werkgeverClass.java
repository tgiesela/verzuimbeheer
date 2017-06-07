package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import reportdatasources.VerzuimOverzichtDatasource;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacade;
import com.gieselaar.verzuimbeheer.helpers.FacesUtil;
import com.gieselaar.verzuimbeheer.helpers.WerkgeverList;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

@ManagedBean
@ViewScoped
public class werkgeverClass extends BackingBeanBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FacesContext context;

	@EJB
	WerkgeverFacade werkgeverFacade;
	private LoginSessionRemote loginsession;
	private panelMenuClass menu;
	private GebruikerInfo gebruiker;

	private List<SelectItem> werkgevers = new ArrayList<SelectItem>();
	private List<SelectItem> actieveHoldings = new ArrayList<SelectItem>();
	private List<WerkgeverInfo> allWerkgevers;
	private List<HoldingInfo> allHoldings;
	private List<SelectItem> holdings;
	private String selectedWerkgever;
	private String selectedHolding = "-1";
	private WerkgeverInfo werkgever;
	private HoldingInfo holding;
	private AdresInfo postAdres;
	private ContactpersoonInfo contactpersoon;
	private WerkgeverList wg;
	private AdresInfo vestigingsAdres;
	private List<AfdelingInfo> afdelingenlijst = null;
	
	/*
	 * Voor verzuim statistieken
	 */
	private Date startdatumperiode;
	private Date einddatumperiode;
	private boolean inclusiefZwangerschap = false;
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
			throw new ViewExpiredException("Invalid session",context.getViewRoot().getViewId());
		gebruiker = loginsession.getGebruiker();
		wg = new WerkgeverList(gebruiker, loginsession, werkgeverFacade);
//		if (gebruiker.isAlleklanten())
		/*
		 * Vul de combobox met alle klanten
		 */
//		{
			try {
				werkgeverFacade.setLoginSession(loginsession);
				werkgevers = wg.getActieveWerkgevers();
				actieveHoldings = wg.getActieveHoldings();
				String werkgeverId = wg.getSelectedWerkgever();
				if (werkgeverId == null || werkgeverId.isEmpty())
					setSelectedWerkgever(((SelectItem) werkgevers.get(0)).getValue().toString());
				else
					setSelectedWerkgever(werkgeverId);
				if (actieveHoldings.size() > 0)
					setSelectedHolding(((SelectItem) actieveHoldings.get(0))
							.getValue().toString());
			} catch (PermissionException e) {
				context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Probleem bij opvragen werkgevers! ",e.getMessage()));
//			} catch (VerzuimApplicationException e) {
//				context = FacesContext.getCurrentInstance();
//				context.addMessage(null, new FacesMessage("Probleem bij opvragen werkgevers! ",e.getMessage()));
			}
//		}  else {
			/*
			 * Vul de combobox met klanten waarvoor gebruiker geautoriseerd is
			 */
//			try {
//				werkgeverFacade.setLoginSession(loginsession);
//				allWerkgevers = werkgeverFacade.allWerkgeversList();
//				allHoldings = werkgeverFacade.getHoldings();
//				werkgevers = getActieveWerkgevers(gebruiker);
//				setSelectedWerkgever(((SelectItem) werkgevers.get(0))
//						.getValue().toString());
//				if (actieveHoldings.size() > 0)
//					setSelectedHolding(((SelectItem) actieveHoldings.get(0))
//							.getValue().toString());
//			} catch (permissionException e) {
//				context = FacesContext.getCurrentInstance();
//				context.addMessage(null, new FacesMessage("Probleem bij opvragen werkgevers! ",e.getMessage()));
//			} catch (VerzuimApplicationException e) {
//				context = FacesContext.getCurrentInstance();
//				context.addMessage(null, new FacesMessage("Probleem bij opvragen werkgevers! ",e.getMessage()));
//			}
//		}
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

	public String save() {
		FacesContext context;
		context = FacesContext.getCurrentInstance();
		try {
			werkgeverFacade.setLoginSession(loginsession);
			if (this.postAdres.isEmtpy())
				werkgever.setPostAdres(null);
			else
				werkgever.setPostAdres(postAdres);
			werkgever.setVestigingsAdres(vestigingsAdres);
			werkgever.setContactpersoon(contactpersoon);
			werkgeverFacade.updateWerkgever(werkgever);
			context.addMessage(null, new FacesMessage("Gegevens opgeslagen",""));
		} catch (PermissionException | ValidationException e) {
			context.addMessage(null, new FacesMessage("Opslaan niet geslaagd! ",e.getMessage()));
			return "";
		} catch (VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage("Opslaan niet geslaagd! ",e.getMessage()));
			return "";
		}
		return "";
	}

	public String afdelingen() {
		return menu.navigateAfdelingen();
	}
	public String verzuimstatistieken() {
		return menu.navigateVerzuimstatistiek();
	}

	public void werkgeverChanged(AjaxBehaviorEvent abe) {
	}

	public String getSelectedWerkgever() {
		return selectedWerkgever;
	}

	public void setSelectedWerkgever(String selectedWerkgever) {
		this.selectedWerkgever = selectedWerkgever;
		context = FacesContext.getCurrentInstance();
		wg.setSelectedWerkgever(selectedWerkgever);
		this.selectedWerkgever = selectedWerkgever;
		try {
			werkgeverFacade.setLoginSession(loginsession);
			werkgever = werkgeverFacade.getWerkgever(Integer
					.parseInt(this.selectedWerkgever));
			if (werkgever.getPostAdres() == null)
				this.postAdres = new AdresInfo();
			else
				this.postAdres = werkgever.getPostAdres();
			if (werkgever.getVestigingsAdres() == null)
				this.vestigingsAdres = new AdresInfo();
			else
				this.vestigingsAdres = werkgever.getVestigingsAdres();
			if (werkgever.getContactpersoon() == null)
				this.setContactpersoon(new ContactpersoonInfo());
			else
				this.setContactpersoon(werkgever.getContactpersoon());
			setAfdelingenlijst(werkgever.getAfdelings());

		} catch (NumberFormatException | PermissionException | VerzuimApplicationException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Selecteren werkgever niet geslaagd: ",e.getMessage()));
		}

	}

	public List<SelectItem> getWerkgevers() {
		return werkgevers;
	}

	public List<SelectItem> getActieveWerkgevers(GebruikerInfo gebruiker) {
/*
		List<SelectItem> actieveWerkgevers = new ArrayList<SelectItem>();
		List<HoldingInfo> holdings = new ArrayList<HoldingInfo>();
		for (WerkgeverInfo wgi:allWerkgevers){
			if (wgi.isActief()){
				if (gebruiker.isAlleklanten()){
					actieveWerkgevers.add(new SelectItem(wgi.getId(),wgi.getNaam()));
					if (wgi.getHoldingId() != null){
						for (HoldingInfo hi:allHoldings){
							if (hi.getId().equals(wgi.getHoldingId())){
								if (!holdings.contains(hi)){
									holdings.add(hi);
								}
								break;
							}
						}
					}
				} else {
					for (GebruikerWerkgeverInfo wgig: gebruiker.getWerkgevers()){
						if (wgig.getWerkgeverid() == (wgi.getId())){
							actieveWerkgevers.add(new SelectItem(wgi.getId(),wgi.getNaam()));
							if (wgi.getHoldingId() != null){
								for (HoldingInfo hi:allHoldings){
									if (hi.getId().equals(wgi.getHoldingId())){
										if (!holdings.contains(hi)){
											holdings.add(hi);
										}
										break;
									}
								}
							}
							break;
						}
					}
				}
			}
		}
		actieveHoldings.add(new SelectItem(-1,"N.v.t.")); // First empty entry
		for (HoldingInfo hi:holdings){
			actieveHoldings.add(new SelectItem(hi.getId(),hi.getNaam()));
		}
		return actieveWerkgevers;
		*/
		return wg.getActieveWerkgevers();
	}
	public List<SelectItem> getHoldings() {
		return wg.getActieveHoldings();
	}

	public HoldingInfo getHolding() {
		return holding;
	}

	public void setHolding(HoldingInfo holding) {
		this.holding = holding;
	}

	public void setWerkgevers(List<SelectItem> werkgevers) {
		this.werkgevers = werkgevers;
	}

	public WerkgeverInfo getWerkgever() {
		return werkgever;
	}

	public void setSelectWerkgever(String selectedWerkgever) {
		if (selectedWerkgever.equals(this.selectedWerkgever))
			;
		else {
			context = FacesContext.getCurrentInstance();
			FacesUtil.setSessionMapValue("werkgeverId", Integer.parseInt(selectedWerkgever));
			this.selectedWerkgever = selectedWerkgever;
		}
	}
	public String getSelectedHolding() {
		return selectedHolding;
	}

	public void setSelectedHolding(String selectedHolding) {
		if (selectedHolding.equals(this.selectedHolding))
			;
		else {
			context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap()
					.put("holdingId", Integer.parseInt(selectedHolding));
			this.selectedHolding = selectedHolding;
		}
	}

	public List<AfdelingInfo> getAfdelingenlijst() {
		return afdelingenlijst;
	}

	public void setAfdelingenlijst(List<AfdelingInfo> afdelingen) {
		this.afdelingenlijst = afdelingen;
	}

	public ContactpersoonInfo getContactpersoon() {
		return contactpersoon;
	}

	public void setContactpersoon(ContactpersoonInfo contactpersoon) {
		this.contactpersoon = contactpersoon;
	}

	public AdresInfo getPostAdres() {
		return postAdres;
	}

	public void setPostAdres(AdresInfo postAdres) {
		this.postAdres = postAdres;
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
	
	public boolean isInclusiefZwangerschap() {
		return inclusiefZwangerschap;
	}

	public void setInclusiefZwangerschap(boolean inclusiefZwangerschap) {
		this.inclusiefZwangerschap = inclusiefZwangerschap;
	}

	public void rapportVerzuimStatistiekTonen(){
		ByteArrayInputStream byteArrayInputStream;
		byte[] byteArray;
		int bytesRead;
//		ReportClientDocument repClientDoc = null;
		context = FacesContext.getCurrentInstance();

		if (startdatumperiode == null){
			context.addMessage(null, new FacesMessage("Startdatum mag niet leeg zijn",""));
			return;
		}
		if (startdatumperiode.after(einddatumperiode) || startdatumperiode.equals(einddatumperiode)){
			context.addMessage(null, new FacesMessage("Startdatum moet voor de einddatum liggen",""));
			return;
		}
		try {
			VerzuimOverzichtDatasource ds = new VerzuimOverzichtDatasource(startdatumperiode, einddatumperiode, loginsession);
			JasperPrint report;
			if (selectedHolding == null || selectedHolding == "-1"){
				report = ds.getReport(werkgever.getId(), -1, -1, "", inclusiefZwangerschap);
			}else{
				String holdingnaam = "";
				for (SelectItem item:actieveHoldings){
					if (item.getValue().equals(Integer.parseInt(selectedHolding))){
						holdingnaam = item.getLabel();
						break;
					}
				}
				report = ds.getReport(-1, Integer.parseInt(selectedHolding), -1, holdingnaam, inclusiefZwangerschap);
			}
			HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
			response.reset();
	
			JasperExportManager.exportReportToPdfStream(report, response.getOutputStream());
	
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch ( JRException | IOException e) {
			context.addMessage(null, new FacesMessage("Probleem bij aanmaken rapport",e.getMessage()));
			return;
		} catch (ValidationException e) {
			context.addMessage(null, new FacesMessage(e.getMessage(),""));
			return;
		} catch (NumberFormatException e) {
			context.addMessage(null, new FacesMessage(e.getMessage(),""));
			return;
		} catch (ServiceLocatorException e) {
			context.addMessage(null, new FacesMessage(e.getMessage(),""));
			return;
		} catch (VerzuimApplicationException e) {
			context.addMessage(null, new FacesMessage(e.getMessage(),""));
			return;
		}
			
//		ReportSourceClass rs = new ReportSourceClass();
//		try{
//			if (selectedHolding == null || selectedHolding == "-1")
//				repClientDoc = rs.getReportClientDocVerzuimOverzicht(werkgever.getId(), -1, startdatumperiode, einddatumperiode, inclusiefZwangerschap);
//			else
//				repClientDoc = rs.getReportClientDocVerzuimOverzicht(-1, Integer.parseInt(selectedHolding), startdatumperiode, einddatumperiode, inclusiefZwangerschap);
//		} catch (Exception e){
//			context.addMessage(null, new FacesMessage("Kan rapportbestand niet openen",e.getMessage()));
//			return;
//		}
//		HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
//		try {
//			byteArrayInputStream = (ByteArrayInputStream) repClientDoc
//			        .getPrintOutputController().export(ReportExportFormat.PDF);
//		} catch (ReportSDKException e) {
//			context.addMessage(null, new FacesMessage("Kan geen PrinterOutputController aanmaken",e.getMessage()));
//			return;
//		}
//		response.reset();
//		response.setHeader("Content-disposition", "inline;filename=crreport.pdf");
//		response.setContentType("application/pdf");

//		byteArray = new byte[1024];
//		try{
//		    while((bytesRead = byteArrayInputStream.read(byteArray)) != -1) {
//		    	response.getOutputStream().write(byteArray, 0, bytesRead);	
//		    }
//			response.getOutputStream().flush();
//			response.getOutputStream().close();
//			repClientDoc.close();
//		} catch (IOException e){
//			context = FacesContext.getCurrentInstance();
//			context.addMessage(null, new FacesMessage("IO-error tijdens aanmaken rapport",e.getMessage()));
//		} catch (ReportSDKException e) {
//			context = FacesContext.getCurrentInstance();
//			context.addMessage(null, new FacesMessage("Fout tijdens aanmaken rapport",e.getMessage()));
//			return; 
//		}
		FacesContext.getCurrentInstance().responseComplete();
		return;
		
	}
}
