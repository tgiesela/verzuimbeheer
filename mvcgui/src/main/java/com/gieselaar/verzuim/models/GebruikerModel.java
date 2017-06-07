package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.RolInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class GebruikerModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<GebruikerInfo> gebruikers;
	private List<RolInfo> rollen;
	private List<ApplicatieFunctieInfo> applicatiefuncties;
	public GebruikerModel(LoginSessionRemote session){
		super(session);
	}

	public void selectGebruikers() throws VerzuimApplicationException {
		try {
			gebruikers = ServiceCaller.autorisatieFacade(this.getSession()).getGebruikers();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(gebruikers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectRollen() throws VerzuimApplicationException {
		try {
			rollen = ServiceCaller.autorisatieFacade(this.getSession()).getRollen();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(rollen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectApplicatiefuncties() throws VerzuimApplicationException {
		try {
			applicatiefuncties = ServiceCaller.autorisatieFacade(this.getSession()).getAppFuncties();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(applicatiefuncties);
			}
		} catch (PermissionException  | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<GebruikerInfo> getGebruikersList() {
		return gebruikers;
	}
	public List<RolInfo> getRollenList() {
		return rollen;
	}
	public List<ApplicatieFunctieInfo> getApplicatiefunctiesList() {
		return applicatiefuncties;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectGebruikers();
			selectRollen();
			selectApplicatiefuncties();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void addGebruiker(GebruikerInfo info) throws VerzuimApplicationException {
		try {
			GebruikerInfo gebruiker = ServiceCaller.autorisatieFacade(getSession()).addGebruiker(info);
			gebruikers.add(gebruiker);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(gebruiker);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void addRol(RolInfo info) throws VerzuimApplicationException {
		try {
			RolInfo rol = ServiceCaller.autorisatieFacade(getSession()).addRol(info);
			rollen.add(rol);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(rol);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void addApplicatiefunctie(ApplicatieFunctieInfo info) throws VerzuimApplicationException {
		try {
			ApplicatieFunctieInfo appfunctie = ServiceCaller.autorisatieFacade(getSession()).addAppFunctie(info);
			applicatiefuncties.add(appfunctie);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(appfunctie);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveGebruiker(GebruikerInfo info) throws VerzuimApplicationException {
		try {
			GebruikerInfo updatedinfo = ServiceCaller.autorisatieFacade(getSession()).updateGebruiker(info);
			/* Now also the list has to be updated */
			for (GebruikerInfo w: gebruikers){
				if (w.getId().equals(info.getId())){
					gebruikers.remove(w);
					break;
				}
			}
			gebruikers.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveRol(RolInfo info) throws VerzuimApplicationException {
		try {
			RolInfo updatedinfo = ServiceCaller.autorisatieFacade(getSession()).updateRol(info);
			/* Now also the list has to be updated */
			for (RolInfo w: rollen){
				if (w.getId().equals(info.getId())){
					rollen.remove(w);
					break;
				}
			}
			rollen.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveApplicatiefunctie(ApplicatieFunctieInfo info) throws VerzuimApplicationException {
		try {
			ApplicatieFunctieInfo updatedinfo = ServiceCaller.autorisatieFacade(getSession()).updateAppFunctie(info);
			/* Now also the list has to be updated */
			for (ApplicatieFunctieInfo w: applicatiefuncties){
				if (w.getId().equals(info.getId())){
					applicatiefuncties.remove(w);
					break;
				}
			}
			applicatiefuncties.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteGebruiker(GebruikerInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.autorisatieFacade(getSession()).deleteGebruiker(info);
			for (GebruikerInfo w: gebruikers){
				if (w.getId().equals(info.getId())){
					gebruikers.remove(w);
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
	public void deleteRol(RolInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.autorisatieFacade(getSession()).deleteRol(info);
			for (RolInfo w: rollen){
				if (w.getId().equals(info.getId())){
					rollen.remove(w);
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
	public void deleteApplicatiefunctie(ApplicatieFunctieInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.autorisatieFacade(getSession()).deleteAppFunctie(info);
			for (ApplicatieFunctieInfo w: applicatiefuncties){
				if (w.getId().equals(info.getId())){
					applicatiefuncties.remove(w);
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

	public GebruikerInfo getGebruiker(Integer id) throws VerzuimApplicationException {
		try {
			return ServiceCaller.autorisatieFacade(getSession()).getGebruiker(id);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
}
