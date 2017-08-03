package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacade;
import com.gieselaar.verzuimbeheer.facades.WerknemerFacade;
import com.gieselaar.verzuimbeheer.helpers.FacesUtil;
import com.gieselaar.verzuimbeheer.helpers.WerkgeverList;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.utils.ListUtils;
import com.gieselaar.verzuimbeheer.utils.Predicate;
import com.gieselaar.verzuimbeheer.utils.Predicate.__filterType;

@ManagedBean
@ViewScoped
public class werknemerClass extends BackingBeanBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	WerknemerFacade werknemerFacade;
	@EJB
	WerkgeverFacade werkgeverFacade;
	private LoginSessionRemote loginsession;
	private panelMenuClass menu;
	private FacesContext context;
	private GebruikerInfo gebruiker;
	private List<SelectItem> werkgevers = new ArrayList<SelectItem>();
	private String selectedWerkgever;
	private List<WerknemerFastInfo> werknemers = new ArrayList<WerknemerFastInfo>();
	private WerknemerFastInfo werknemer = new WerknemerFastInfo();
	private boolean uitDienstTonen = false;
	private boolean rowSelected = false;
	private int currentWerknemerIndex;
	private WerknemerFastInfo selectedWerknemer;

	private WerkgeverList wg = null;
	private String zoekString = "";
	private String naamFilter;

	private Collection<Object> selection;

	public werknemerClass() {
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
	@PostConstruct
	public void init() {
		Integer werkgeverId;
		/*
		 * We moeten kijken of er al een werkgever geselecteerd was in deze
		 * sessie. Als dat zo is, wordt die weer geselecteerd Bij verlaten van
		 * het scherm, moet de werkgever weer gewist worden, om misverstanden te
		 * voorkomen. De geselecteerde werkgever moet in de lijst van toegestane
		 * werkgever voorkomen. Vraag is of onderstaande werkt.
		 */

		context = FacesContext.getCurrentInstance();

		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		menu = (panelMenuClass) context.getApplication()
				.evaluateExpressionGet(context, "#{panelMenuClass}",
						panelMenuClass.class);

		werkgeverId = (Integer) FacesUtil.getSessionMapValue("werkgeverId");
		loginsession = sessioncontext.loginSession;
		gebruiker = loginsession.getGebruiker();
		if (gebruiker == null) /*
								 * Dit gebeurt bij het verlaten van het scherm
								 * en terugkeer naar main, geen idee waarom
								 */
			return;
//		try {
			wg = new WerkgeverList(gebruiker,loginsession, werkgeverFacade);
			werkgevers = wg.getActieveWerkgevers();

//			if (gebruiker.isAlleklanten()) {
				/*
				 * Vul de combobox met alle klanten
				 */
//				werkgeverFacade.setLoginSession(loginsession);
//				for (WerkgeverInfo wg : werkgeverFacade.allWerkgeversList()) {
//					werkgevers.add(new SelectItem(wg.getId(), wg.getNaam()));
//				}
//			} else {
				/*
				 * Vul de combobox met klanten waarvoor gebruiker geautoriseerd
				 * is
				 */
//				werkgeverFacade.setLoginSession(loginsession);
//				for (WerkgeverInfo wg : werkgeverFacade.allWerkgeversList()) {
//					for (GebruikerWerkgeverInfo gwi : gebruiker.getWerkgevers())
//						if (gwi.getWerkgeverid() == wg.getId())
//							werkgevers.add(new SelectItem(wg.getId(), wg
//									.getNaam()));
//				}
//			}
//		} catch (permissionException e) {
//			context = FacesContext.getCurrentInstance();
//			context.addMessage(null, new FacesMessage(
//					"Probleem bij opvragen werkgevers! ", e.getMessage()));
//			return;
//		} catch (VerzuimApplicationException e) {
//			context = FacesContext.getCurrentInstance();
//			context.addMessage(null, new FacesMessage(
//					"Probleem bij opvragen werkgevers! ", e.getMessage()));
//			return;
//		}
		if (werkgeverId == null)
			setWerkgever(((SelectItem) werkgevers.get(0)).getValue().toString());
		else
			setWerkgever(werkgeverId.toString());

	}

	public String editWerknemer() {
		WerknemerInfo wi = new WerknemerInfo();
		try {
			if (selectedWerknemer != null)
				wi = werknemerFacade.getWerknemer(selectedWerknemer.getId());
			else
				return "";
		} catch (PermissionException | VerzuimApplicationException | ValidationException e) {
		}
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("werknemer", wi);
		return menu.navigateWerknemerDetail();
	}

	public String verzuimenWerknemer() {
		WerknemerInfo wi = new WerknemerInfo();
		try {
			if (selectedWerknemer != null)
				wi = werknemerFacade.getWerknemer(selectedWerknemer.getId());
			else
				return "";
		} catch (PermissionException | VerzuimApplicationException | ValidationException e) {
		}
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("werknemer", wi);
		return menu.navigateVerzuimen();
	}

	public String nieuweWerknemer() {
		
		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("werknemer");
		return menu.navigateWerknemerDetail();
	}

	public String getWerkgever() {
		return wg.getSelectedWerkgever();
	}

	public void setWerkgever(String selectedWerkgever) {
		if (selectedWerkgever.equals(this.selectedWerkgever))
			;
		else {
			wg.setSelectedWerkgever(selectedWerkgever);
			context = FacesContext.getCurrentInstance();
			this.selectedWerkgever = selectedWerkgever;
			try {
				werknemerFacade.setLoginSession(loginsession);
				werknemers = werknemerFacade.getWerknemersByWerkgever(Integer
						.parseInt(this.selectedWerkgever));
				if (werknemers != null)
					WerknemerFastInfo.sort(werknemers, WerknemerFastInfo.__sortcol.ACHTERNAAM);
				this.setUitDienstTonen(false);

			} catch (NumberFormatException | PermissionException
					| VerzuimApplicationException e) {
				context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(
						"Selecteren werkgever niet geslaagd: ", e.getMessage()));
			}
		}
	}

	public List<WerknemerFastInfo> getWerknemers() {
		if (zoekString.isEmpty()){
			if (uitDienstTonen == true)
				return werknemers;
			else {
				List<WerknemerFastInfo> werknemersFiltered = new ArrayList<WerknemerFastInfo>();
				for (WerknemerFastInfo wf : werknemers) {
					if (wf.isActief())
						werknemersFiltered.add(wf);
				}
				return werknemersFiltered;
			}
		} else {
			List<WerknemerFastInfo> werknemersSearched = 
					ListUtils.applyFilter(werknemers, this.searchAll,
						zoekString, __filterType.wildcard);
			if (uitDienstTonen == true){
				List<WerknemerFastInfo> werknemersFiltered = new ArrayList<WerknemerFastInfo>();
				for (WerknemerFastInfo wf : werknemers) {
					if (wf.isActief())
						werknemersFiltered.add(wf);
				}
				return werknemersFiltered;
			} else {
				return werknemersSearched;
			}
			
		}
	}

	public List<SelectItem> getWerkgevers() {
		return werkgevers;
	}

	public void setWerkgevers(List<SelectItem> werkgevers) {
		this.werkgevers = werkgevers;
	}

	public WerknemerFastInfo getWerknemer() {
		return werknemer;
	}

	public void setWerknemer(WerknemerFastInfo werknemer) {
		this.werknemer = werknemer;
	}

	public boolean isUitDienstTonen() {
		return uitDienstTonen;
	}

	public void setUitDienstTonen(boolean uitDienstTonen) {
		this.uitDienstTonen = uitDienstTonen;
	}

	public String zoekWerknemers() {
		return null;
	}

	public Predicate<WerknemerFastInfo> searchAll = new Predicate<WerknemerFastInfo>() {
		/*
		 * This function searches some properties
		 */
		@Override
		public boolean search(WerknemerFastInfo type, String regex) {

			if (type.getAchternaam().matches(regex))
				return true;
			if (type.getVoornaam() != null)
				if (type.getVoornaam().matches(regex))
					return true;
			if (type.getEmail() != null)
				if (type.getEmail().matches(regex))
					return true;
			if (type.getBurgerservicenummer().matches(regex))
				return true;
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String datestring;
			datestring = formatter.format(type.getGeboortedatum());
			if (datestring.matches(regex))
				return true;
			else
				return false;
		}
	};

	public String getNaamFilter() {
		return naamFilter;
	}

	public void setNaamFilter(String naamFilter) {
		this.naamFilter = naamFilter;
	}

	/*
	 * public void selectionListener(AjaxBehaviorEvent event) {
	 * 
	 * UIDataTable dataTable = (UIDataTable) event.getComponent(); Object
	 * originalKey = dataTable.getRowKey();
	 * System.out.println("SelectionListener:" + selection.size());
	 * 
	 * werknemers.clear(); for (Object selectionKey : selection) {
	 * System.out.println("SelectionKey = " + selectionKey.toString());
	 * dataTable.setRowKey(selectionKey); if (dataTable.isRowAvailable()) {
	 * werknemers.add((WerknemerFastInfo) dataTable.getRowData());
	 * System.out.println("SelectionListener werknemer:" + ((WerknemerFastInfo)
	 * dataTable.getRowData()) .getAchternaam()); } }
	 * dataTable.setRowKey(originalKey); }
	 */

	/*
	 * public void selectionListener(AjaxBehaviorEvent event) {
	 * UIExtendedDataTable dataTable = (UIExtendedDataTable)
	 * event.getComponent(); Object originalKey = dataTable.getRowKey(); for
	 * (Object selectionKey : selection) { dataTable.setRowKey(selectionKey); if
	 * (dataTable.isRowAvailable()) { // selectionItems.add((InventoryItem)
	 * dataTable.getRowData()); } }
	 * 
	 * dataTable.setRowKey(originalKey); }
	 */
	public Collection<Object> getSelection() {
		return selection;
	}

	public void setSelection(Collection<Object> selection) {
		this.selection = selection;
	}

	public int getCurrentWerknemerIndex() {
		return currentWerknemerIndex;
	}

	public void setCurrentWerknemerIndex(int currentWerknemerIndex) {
		this.currentWerknemerIndex = currentWerknemerIndex;
	}

	public void setSelectedWerknemer(WerknemerFastInfo werknemer) {
		this.selectedWerknemer = werknemer;
	}

	public WerknemerFastInfo getSelectedWerknemer() {
		return selectedWerknemer;
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
	
	public String getZoekString() {
		return zoekString;
	}

	public void setZoekString(String zoekString) {
		this.zoekString = zoekString;
	}

}
