package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class CascodeModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<CascodeInfo> cascodes;
	private List<CascodeGroepInfo> cascodegroepen;
	public CascodeModel(LoginSessionRemote session){
		super(session);
	}

	public void selectCascodes() throws VerzuimApplicationException {
		try {
			cascodes = ServiceCaller.cascodeFacade(this.getSession()).allCascodes();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(cascodes);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectCascodes(Integer selectedgroep) throws VerzuimApplicationException {
		try {
			cascodes = ServiceCaller.cascodeFacade(this.getSession()).getCascodesForGroep(selectedgroep);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(cascodes);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectCascodegroepen() throws VerzuimApplicationException {
		try {
			cascodegroepen = ServiceCaller.cascodeFacade(this.getSession()).allCascodeGroepen();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(cascodegroepen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<CascodeInfo> getCascodesList() {
		return cascodes;
	}

	public List<CascodeGroepInfo> getCascodegroepenList() {
		return cascodegroepen;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectCascodes();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}


	public void deleteCascodegroep(CascodeGroepInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.cascodeFacade(getSession()).deleteCascodeGroep(info);
			for (CascodeGroepInfo w: cascodegroepen){
				if (w.getId().equals(info.getId())){
					cascodegroepen.remove(w);
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

	public void saveCascodegroep(CascodeGroepInfo info) throws VerzuimApplicationException {
		try {
			CascodeGroepInfo updatedinfo = ServiceCaller.cascodeFacade(getSession()).updateCascodeGroep(info);
			/* Now also the list has to be updated */
			for (CascodeGroepInfo w: cascodegroepen){
				if (w.getId().equals(info.getId())){
					cascodegroepen.remove(w);
					break;
				}
			}
			cascodegroepen.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void addCascodegroep(CascodeGroepInfo info) throws VerzuimApplicationException {
		try {
			CascodeGroepInfo cascode = ServiceCaller.cascodeFacade(getSession()).updateCascodeGroep(info);
			cascodegroepen.add(cascode);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(cascode);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void addCascode(CascodeInfo info) throws VerzuimApplicationException {
		try {
			CascodeInfo cascode = ServiceCaller.cascodeFacade(getSession()).updateCascode(info);
			cascodes.add(cascode);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(cascode);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveCascode(CascodeInfo info) throws VerzuimApplicationException {
		try {
			CascodeInfo updatedinfo = ServiceCaller.cascodeFacade(getSession()).updateCascode(info);
			/* Now also the list has to be updated */
			for (CascodeInfo w: cascodes){
				if (w.getId().equals(info.getId())){
					cascodes.remove(w);
					break;
				}
			}
			cascodes.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteCascode(CascodeInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.cascodeFacade(getSession()).deleteCascode(info);
			for (CascodeInfo w: cascodes){
				if (w.getId().equals(info.getId())){
					cascodes.remove(w);
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
