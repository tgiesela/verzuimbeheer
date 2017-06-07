package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;

@Remote
public interface VerzuimFacadeRemote extends FacadeBaseRemote {
	List<VerzuimInfo> getVerzuimenWerknemer(int dienstverbandid) throws PermissionException, VerzuimApplicationException;
	List<VerzuimInfo> getVerzuimenWerkgever(int werkgeverid, boolean uitDienst) throws PermissionException;
	VerzuimInfo getVerzuimDetails(int verzuimid) throws PermissionException, VerzuimApplicationException;
	List<CascodeInfo> getCascodes() throws PermissionException, VerzuimApplicationException;
	List<VerzuimHerstelInfo> getVerzuimHerstellen(int dienstverbandid) throws PermissionException, VerzuimApplicationException;
	List<VerzuimDocumentInfo> getVerzuimDocumenten(int dienstverbandid)	throws PermissionException, VerzuimApplicationException;
	List<ActiviteitInfo> getActiviteiten() throws PermissionException, VerzuimApplicationException;
	List<GebruikerInfo> getGebruikers() throws PermissionException, VerzuimApplicationException;
	List<TodoFastInfo> getTodosFast() throws PermissionException, VerzuimApplicationException;
	List<TodoFastInfo> getOpenTodosFast() throws PermissionException, VerzuimApplicationException;
	List<TodoFastInfo> getClosedTodosFast() throws PermissionException, VerzuimApplicationException;
	List<DocumentTemplateInfo> getDocumentTemplates() throws PermissionException, VerzuimApplicationException;
	TodoInfo getTodo(int todo) throws PermissionException, VerzuimApplicationException;
	
	VerzuimInfo addVerzuim(VerzuimInfo verzuim) throws PermissionException, ValidationException, VerzuimApplicationException;
	void addVerzuimHerstel(VerzuimHerstelInfo herstel, boolean cleanupTodo) throws PermissionException, ValidationException, VerzuimApplicationException;
	void addVerzuimDocument(VerzuimDocumentInfo document) throws PermissionException, ValidationException, VerzuimApplicationException;
	void addVerzuimActiviteit(VerzuimActiviteitInfo activiteit) throws PermissionException, ValidationException, VerzuimApplicationException;
	void addMedischekaart(VerzuimMedischekaartInfo kaart) throws PermissionException, ValidationException, VerzuimApplicationException;
	TodoInfo addTodo(TodoInfo todo) throws PermissionException, ValidationException, VerzuimApplicationException;
	DocumentTemplateInfo addDocumentTemplate(DocumentTemplateInfo template)	throws PermissionException, ValidationException, VerzuimApplicationException;
	
	void updateVerzuim(VerzuimInfo verzuim) throws PermissionException, ValidationException, VerzuimApplicationException;
	void updateVerzuimHerstel(VerzuimHerstelInfo herstel, boolean cleanupTodo) throws PermissionException, ValidationException, VerzuimApplicationException;
	void updateVerzuimDocument(VerzuimDocumentInfo document) throws PermissionException, ValidationException, VerzuimApplicationException;
	void updateVerzuimActiviteit(VerzuimActiviteitInfo activiteit) throws PermissionException, ValidationException, VerzuimApplicationException;
	void updateMedischekaart(VerzuimMedischekaartInfo kaart) throws PermissionException, ValidationException, VerzuimApplicationException;
	void updateTodo(TodoInfo todo) throws PermissionException, ValidationException, VerzuimApplicationException;
	DocumentTemplateInfo updateDocumentTemplate(DocumentTemplateInfo template) throws PermissionException, ValidationException, VerzuimApplicationException;
	
	void deleteVerzuim(VerzuimInfo verzuim) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteVerzuimHerstel(VerzuimHerstelInfo herstel) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteVerzuimDocument(VerzuimDocumentInfo herstel) throws PermissionException, VerzuimApplicationException;
	void deleteVerzuimActiviteit(VerzuimActiviteitInfo activiteit) throws PermissionException, VerzuimApplicationException;
	void deleteMedischekaart(VerzuimMedischekaartInfo kaart) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteTodo(TodoInfo todo) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteDocumentTemplate(DocumentTemplateInfo document) throws PermissionException, ValidationException, VerzuimApplicationException;
	void setLoginSession(LoginSessionRemote loginSession) throws PermissionException;
	List<TodoFastInfo> getTodosVerzuim(Integer verzuimid) throws PermissionException, VerzuimApplicationException;
	TodoFastInfo getTodoFast(Integer id) throws PermissionException, VerzuimApplicationException;
}
