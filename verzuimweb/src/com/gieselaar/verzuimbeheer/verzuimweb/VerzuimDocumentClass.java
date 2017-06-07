package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacade;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.SettingsFacade;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacade;
import com.gieselaar.verzuimbeheer.services.SettingsBean;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;

@ManagedBean
@ViewScoped
public class VerzuimDocumentClass extends BackingBeanBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FacesContext context;
	@EJB VerzuimFacade verzuimFacade;
	@EJB AutorisatieFacade autorisatieFacade;
	@EJB SettingsFacade settings;

	private LoginSessionRemote loginsession;

	private WerknemerInfo werknemer;
	private VerzuimInfo verzuim;

	private boolean rowSelected = false;
	private VerzuimDocumentInfo selectedDocument;

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
    public VerzuimDocumentInfo getSelectedDocument() {
    	if (selectedDocument == null)
    		return null;
    	else{
    		return selectedDocument;
    	}
    }
 
    public void setSelectedDocument(VerzuimDocumentInfo selectedDocument) {
        this.selectedDocument = selectedDocument;
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
    
	public List<VerzuimDocumentInfo> getDocumenten() {
		return verzuim.getVerzuimdocumenten();
	}

	public StreamedContent getDocument(){
    	StreamedContent file;
    	try {
			String filename = selectedDocument.getPadnaam();
    		if (filename.startsWith("Y:\\")){
    			filename = filename.replace("Y:\\", "");
    		}
    		if (File.separatorChar == '\\'){
    			if (filename.contains("//")){
    				filename = filename.replace("/", "\\");
    			}
    		}else{
    			if (filename.contains("\\")){
    				filename = filename.replace("\\", "/");
    			}
    		}
			settings.setLoginSession(loginsession);
    		SettingsInfo si = settings.getSettings();
    		filename = si.getServershare() + File.separatorChar + filename;
        	InputStream stream = new FileInputStream(filename);
        	Path path = FileSystems.getDefault().getPath("", filename);
        	String mimetype = Files.probeContentType(path);
			file = new DefaultStreamedContent(stream, mimetype, filename);
		} catch (IOException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan document niet openen! ",e.getMessage()));
			return null;
		} catch (VerzuimApplicationException|PermissionException e) {
			context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kan instellingen niet opvragen! ",e.getMessage()));
			return null;
		}
        return file;
    }
}
