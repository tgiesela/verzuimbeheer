package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ActiviteitBean;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.CascodeBean;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimBean;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;

/**
 * Session Bean implementation class VerzuimFacade
 */
@Stateful(mappedName="java:global/verzuimbeheer/VerzuimFacade", name="VerzuimFacade")
@LocalBean
public class VerzuimFacade extends FacadeBase implements VerzuimFacadeRemote{

    /**
     * Default constructor. 
     */
	@EJB private VerzuimBean verzuimEJB;
	@EJB private CascodeBean cascodeEJB; 
	@EJB private ActiviteitBean activiteitEJB; 
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	public VerzuimFacade() {
    }
	private void setCurrentuser(){
		verzuimEJB.setCurrentuser(this.getCurrentuser());		
		cascodeEJB.setCurrentuser(this.getCurrentuser());		
		activiteitEJB.setCurrentuser(this.getCurrentuser());		
		gebruikerEJB.setCurrentuser(this.getCurrentuser());		
	}

	@Override
	public List<VerzuimInfo> getVerzuimenWerknemer(int dienstverbandid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getVerzuimenDienstverband(dienstverbandid);
	}
	@Override
	public List<VerzuimHerstelInfo> getVerzuimHerstellen(int dienstverbandid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getVerzuimHerstellen(dienstverbandid);
	}
	@Override
	public List<VerzuimDocumentInfo> getVerzuimDocumenten(int dienstverbandid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getVerzuimDocumenten(dienstverbandid);
	}
	@Override
	public List<DocumentTemplateInfo> getDocumentTemplates() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENTEMPLATES);
		return verzuimEJB.getDocumentTemplates();
	}
	@Override
	public List<VerzuimInfo> getVerzuimenWerkgever(int werkgeverid, boolean uitDienst) throws PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return null;
	}

	@Override
	public VerzuimInfo addVerzuim(VerzuimInfo verzuim) throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		return verzuimEJB.addVerzuim(verzuim);
	}

	@Override
	public void addVerzuimHerstel(VerzuimHerstelInfo herstel, boolean cleanupTodo)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.addVerzuimHerstel(herstel, cleanupTodo);
	}

	@Override
	public void addVerzuimDocument(VerzuimDocumentInfo document)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.addVerzuimDocument(document);
	}

	@Override
	public void addVerzuimActiviteit(VerzuimActiviteitInfo activiteit)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.addVerzuimActiviteit(activiteit);
	}

	@Override
	public DocumentTemplateInfo addDocumentTemplate(DocumentTemplateInfo template)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		return verzuimEJB.addDocumenttemplate(template);
	}

	@Override
	public void addMedischekaart(VerzuimMedischekaartInfo kaart)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.addMedischekaart(kaart);
	}

	@Override
	public TodoInfo addTodo(TodoInfo todo)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		return verzuimEJB.addTodo(todo);
	}

	@Override
	public void updateVerzuim(VerzuimInfo verzuim) throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.updateVerzuim(verzuim);
	}

	@Override
	public void updateVerzuimHerstel(VerzuimHerstelInfo herstel, boolean cleanupTodo)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.updateVerzuimHerstel(herstel, cleanupTodo);
	}

	@Override
	public void updateVerzuimDocument(VerzuimDocumentInfo document)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.updateVerzuimDocument(document);
	}

	@Override
	public void updateVerzuimActiviteit(VerzuimActiviteitInfo activiteit)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.updateVerzuimActiviteit(activiteit);
	}

	@Override
	public void updateMedischekaart(VerzuimMedischekaartInfo kaart)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.updateMedischekaart(kaart);
	}

	@Override
	public void updateTodo(TodoInfo todo)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERVERZUIM);
		verzuimEJB.updateTodo(todo);
	}

	@Override
	public DocumentTemplateInfo updateDocumentTemplate(DocumentTemplateInfo template)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERTEMPLATES);
		return verzuimEJB.updateDocumenttemplate(template);
	}

	@Override
	public void deleteVerzuim(VerzuimInfo verzuim) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERVERZUIM);
		verzuimEJB.deleteVerzuim(verzuim);
	}

	@Override
	public void deleteVerzuimHerstel(VerzuimHerstelInfo herstel)
			throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERVERZUIM);
		verzuimEJB.deleteVerzuimHerstel(herstel);
	}

	@Override
	public void deleteVerzuimDocument(VerzuimDocumentInfo herstel)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERVERZUIM);
		verzuimEJB.deleteVerzuimDocument(herstel);
	}

	@Override
	public void deleteVerzuimActiviteit(VerzuimActiviteitInfo activiteit)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERVERZUIM);
		verzuimEJB.deleteVerzuimActiviteit(activiteit);
	}

	@Override
	public void deleteMedischekaart(VerzuimMedischekaartInfo kaart)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERVERZUIM);
		verzuimEJB.deleteMedischekaart(kaart);
	}

	@Override
	public void deleteTodo(TodoInfo todo)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERVERZUIM);
		verzuimEJB.deleteTodo(todo);
	}
	@Override
	public void deleteDocumentTemplate(DocumentTemplateInfo document)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERTEMPLATES);
		verzuimEJB.deleteDocumenttemplate(document);
	}

	@Override
	public VerzuimInfo getVerzuimDetails(int verzuimid)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getVerzuimDetails(verzuimid);
	}

	@Override
	public List<CascodeInfo> getCascodes() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return cascodeEJB.allCascodes();
	}
	public List<ActiviteitInfo> getActiviteiten() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return activiteitEJB.allActiviteiten();
	}

	@Override
	public List<GebruikerInfo> getGebruikers() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return gebruikerEJB.allGebruikers();
	}

	@Override
	public List<TodoFastInfo> getTodosVerzuim(Integer verzuimid) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getTodosVerzuim(verzuimid);
	}

	@Override
	public TodoInfo getTodo(int todo) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getTodo(todo);
	}
	@Override
	public TodoFastInfo getTodoFast(Integer todo) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getTodoFast(todo);
	}
	
	@Override
	public List<TodoFastInfo> getTodosFast() throws PermissionException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getTodosFast();
	}
	@Override
	public List<TodoFastInfo> getOpenTodosFast() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getOpenTodosFast();
	}
	@Override
	public List<TodoFastInfo> getClosedTodosFast() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENVERZUIM);
		return verzuimEJB.getClosedTodosFast();
	}
	/**
	 * Implementation abstract functions from FacadeBase
	 */
	@Override
	public void setLoginSession(LoginSessionRemote session) throws PermissionException {
		loginsession = session;
		super.setSession(loginsession);
	}

	@Override
	public void initSuperclass() {
		super.setGebruikerEJB(gebruikerEJB);
		super.setSession(loginsession);
	}
}
