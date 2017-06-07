package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class DocumentTemplateModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<DocumentTemplateInfo> documenttemplates;
	public DocumentTemplateModel(LoginSessionRemote session){
		super(session);
	}

	public void selectDocumentTemplates() throws VerzuimApplicationException {
		try {
			documenttemplates = ServiceCaller.verzuimFacade(this.getSession()).getDocumentTemplates();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(documenttemplates);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<DocumentTemplateInfo> getDocumenttemplatesList() {
		return documenttemplates;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectDocumentTemplates();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void addDocumenttemplate(DocumentTemplateInfo info) throws VerzuimApplicationException {
		try {
			DocumentTemplateInfo template = ServiceCaller.verzuimFacade(getSession()).addDocumentTemplate(info);
			documenttemplates.add(template);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(template);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveDocumenttemplate(DocumentTemplateInfo info) throws VerzuimApplicationException {
		try {
			DocumentTemplateInfo updatedinfo = ServiceCaller.verzuimFacade(getSession()).updateDocumentTemplate(info);
			/* Now also the list has to be updated */
			for (DocumentTemplateInfo w: documenttemplates){
				if (w.getId().equals(info.getId())){
					documenttemplates.remove(w);
					break;
				}
			}
			documenttemplates.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteDocumenttemplate(DocumentTemplateInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteDocumentTemplate(info);
			for (DocumentTemplateInfo w: documenttemplates){
				if (w.getId().equals(info.getId())){
					documenttemplates.remove(w);
					break;
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	
}
