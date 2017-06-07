package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacade;
import com.gieselaar.verzuimbeheer.helpers.InformatieKaartInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;

@ManagedBean
@ViewScoped
public class InformatiekaartClass extends BackingBeanBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;
	@EJB VerzuimFacade verzuimFacade;
	@EJB AutorisatieFacade autorisatieFacade;

	private LoginSessionRemote loginsession;

	private WerknemerInfo werknemer;
	private VerzuimInfo verzuim;

	private boolean modeNew = false;
	private TreeNode root;
	private InformatieKaartInfo selectedKaart;
	private TreeNode selectedNode;

	private List<GebruikerInfo> gebruikers;
	@PostConstruct
	public void init() {
		context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;
		if (loginsession.getGebruiker() == null){
			return;
		}
		verzuim = (VerzuimInfo) context.getExternalContext().getSessionMap()
				.get("verzuim");
		werknemer = (WerknemerInfo) context.getExternalContext()
				.getSessionMap().get("werknemer");
		try {
			verzuimFacade.setLoginSession(loginsession);
			verzuim = verzuimFacade.getVerzuimDetails(verzuim.getId());
			root = createKaarten();
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
	private String getGebruikerNaam(int id) throws PermissionException, VerzuimApplicationException{
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
	public TreeNode createKaarten() {
		context = FacesContext.getCurrentInstance();
        TreeNode root = new DefaultTreeNode(null, null);
        Date currentDate = null;
        Integer currentGebruiker = null;
        TreeNode currentdateleaf = null;
        TreeNode currentuserleaf = null;
        for (VerzuimMedischekaartInfo ki:verzuim.getVerzuimmedischekaarten()){
        	if (ki.getOpenbaar()){
	        	if((currentDate == null) || !DateOnly.equals(currentDate, ki.getWijzigingsdatum())){
	        		currentdateleaf = new DefaultTreeNode(new InformatieKaartInfo(ki.getWijzigingsdatum(),"-","",ki,false),root);
	            	currentDate = ki.getWijzigingsdatum();
	            	currentGebruiker = null;
	        	}
	        	if ((currentGebruiker == null) || currentGebruiker != ki.getCreatedby()){
	        		try {
		        		currentuserleaf = new DefaultTreeNode(new InformatieKaartInfo(getGebruikerNaam(ki.getCreatedby()),"","",ki,false), currentdateleaf);
		        		currentGebruiker = ki.getCreatedby();
					} catch (PermissionException | VerzuimApplicationException e) {
						context.addMessage(null, new FacesMessage("Informatiekaart opvragen",e.getMessage()));
					}
	        	}
				@SuppressWarnings("unused")
				TreeNode kaart = new DefaultTreeNode("Inhoud", new InformatieKaartInfo(ki.getId(),"",ki.getMedischekaart(),ki,ki.getCreatedby()==loginsession.getGebruiker().getId()), currentuserleaf);
        	}
        }
         
        return root;
    }
	
	public void newInformatiekaart() {
		Map<String,Object> options = new HashMap<String, Object>();

		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.put("informatiekaart", null);
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 350);
        options.put("contentWidth", 800);
        options.put("height", 400);
        options.put("width", 850);
        
        RequestContext.getCurrentInstance().openDialog("informatiekaartDialog", options, null);		
		
	}
	public void editInformatiekaart(){
		Map<String,Object> options = new HashMap<String, Object>();

		context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.put("informatiekaart", selectedKaart.getKaart());
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 350);
        options.put("contentWidth", 800);
        options.put("height", 400);
        options.put("width", 850);
        RequestContext.getCurrentInstance().openDialog("informatiekaartDialog", options, null);		

	}
	
	public void refreshkaarten(){
		context = FacesContext.getCurrentInstance();
		try {
			verzuimFacade.setLoginSession(loginsession);
			verzuim = verzuimFacade.getVerzuimDetails(verzuim.getId());
			root = createKaarten();
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
	public TreeNode getRoot() {
        return root;
    }
 
    public InformatieKaartInfo getSelectedKaart() {
    	if (selectedKaart == null)
    		return null;
    	if (selectedKaart.getKaart() == null)
    		return null;
    	else{
    		/**
    		 * Om een onduidelijke reden wordt de text van de selectedKaart
    		 * nog aangepast met de text van de vorige selectie. Daarom
    		 * zetten we hier de waarden nog eens goed.
    		 */
    		/*
    		VerzuimMedischekaartInfo kaart = selectedKaart.getKaart();
    		selectedKaart.setDate(kaart.getWijzigingsdatum());
    		try {
				selectedKaart.setUser(getGebruikerNaam(kaart.getUser()));
			} catch (permissionException | VerzuimApplicationException e) {
				e.printStackTrace();
			}
    		selectedKaart.setText(kaart.getMedischekaart());
    		*/
    		return selectedKaart;
    	}
    }
 
    public void setSelectedKaart(InformatieKaartInfo selectedKaart) {
        this.selectedKaart = selectedKaart;
    }
	public TreeNode getSelectedNode() {
		return selectedNode;
	}
	public void setSelectedNode(TreeNode selectedNode) {
		if (selectedNode != null){
			if (modeNew){
				/*
				 * Wijzig selectedKaart niet. Het staat al goed en mag juist
				 * niet worden overschreven. 
				 */
			}else{
				selectedKaart = (InformatieKaartInfo)selectedNode.getData();
			}
		}else{
			selectedKaart = null;
		}
		this.selectedNode = selectedNode;
	}
}
