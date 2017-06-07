package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.PakketInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class PakketModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<PakketInfo> pakketten;
	private List<ActiviteitInfo> activiteiten;
	public PakketModel(LoginSessionRemote session){
		super(session);
	}

	public void selectPakketten() throws VerzuimApplicationException {
		try {
			pakketten = ServiceCaller.pakketFacade(this.getSession()).allPaketten();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(pakketten);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectActiviteiten() throws VerzuimApplicationException {
		try {
			activiteiten = ServiceCaller.pakketFacade(this.getSession()).allActivteiten();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(activiteiten);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<PakketInfo> getPakkettenList() {
		return pakketten;
	}

	public List<ActiviteitInfo> getActiviteitenList() {
		return activiteiten;
	}
	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectPakketten();
			selectActiviteiten();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addPakket(PakketInfo info) throws VerzuimApplicationException {
		try {
			PakketInfo pakket = ServiceCaller.pakketFacade(getSession()).updatePakket(info);
			pakketten.add(pakket);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(pakket);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void savePakket(PakketInfo info) throws VerzuimApplicationException {
		try {
			PakketInfo updatedinfo = ServiceCaller.pakketFacade(getSession()).updatePakket(info);
			/* Now also the list has to be updated */
			for (PakketInfo w: pakketten){
				if (w.getId().equals(info.getId())){
					pakketten.remove(w);
					break;
				}
			}
			pakketten.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deletePakket(PakketInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.pakketFacade(getSession()).deletePakket(info);
			for (PakketInfo w: pakketten){
				if (w.getId().equals(info.getId())){
					pakketten.remove(w);
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
	public void addActiviteit(ActiviteitInfo info) throws VerzuimApplicationException {
		try {
			ActiviteitInfo pakket = ServiceCaller.pakketFacade(getSession()).updateActiviteit(info);
			activiteiten.add(pakket);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(pakket);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveActiviteit(ActiviteitInfo info) throws VerzuimApplicationException {
		try {
			ActiviteitInfo updatedinfo = ServiceCaller.pakketFacade(getSession()).updateActiviteit(info);
			/* Now also the list has to be updated */
			for (ActiviteitInfo w: activiteiten){
				if (w.getId().equals(info.getId())){
					activiteiten.remove(w);
					break;
				}
			}
			activiteiten.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteActiviteit(ActiviteitInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.pakketFacade(getSession()).deleteActiviteit(info);
			for (ActiviteitInfo w: activiteiten){
				if (w.getId().equals(info.getId())){
					activiteiten.remove(w);
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
