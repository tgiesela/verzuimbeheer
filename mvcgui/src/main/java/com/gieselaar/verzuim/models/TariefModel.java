package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class TariefModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<TariefInfo> tarieven;
	public TariefModel(LoginSessionRemote session){
		super(session);
	}

	public void selectTarieven() throws VerzuimApplicationException {
		try {
			tarieven = ServiceCaller.werkgeverFacade(this.getSession()).getTarieven();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(tarieven);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectTarievenHolding(Integer holdingid) throws VerzuimApplicationException {
		try {
			tarieven = ServiceCaller.werkgeverFacade(this.getSession()).getTarievenByHolding(holdingid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(tarieven);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectTarievenWerkgever(Integer werkgeverid) throws VerzuimApplicationException {
		try {
			tarieven = ServiceCaller.werkgeverFacade(this.getSession()).getTarievenByWerkgever(werkgeverid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(tarieven);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<TariefInfo> getTarievenList() {
		return tarieven;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectTarieven();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addTarief(TariefInfo info) throws VerzuimApplicationException {
		try {
			TariefInfo todo = ServiceCaller.werkgeverFacade(getSession()).addTarief(info);
			tarieven.add(todo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(todo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveTarief(TariefInfo info) throws VerzuimApplicationException {
		try {
			TariefInfo updatedinfo = ServiceCaller.werkgeverFacade(getSession()).updateTarief(info);
			/* Now also the list has to be updated */
			for (TariefInfo w: tarieven){
				if (w.getId().equals(info.getId())){
					tarieven.remove(w);
					break;
				}
			}
			tarieven.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteTarief(TariefInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).deleteTarief(info);
			for (TariefInfo w: tarieven){
				if (w.getId().equals(info.getId())){
					tarieven.remove(w);
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
