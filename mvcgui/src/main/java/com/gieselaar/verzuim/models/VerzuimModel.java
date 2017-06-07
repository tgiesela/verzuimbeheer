package com.gieselaar.verzuim.models;

import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class VerzuimModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<VerzuimInfo> verzuimen;
	private Integer dienstverbandid = null;
	private VerzuimInfo verzuim;
	public VerzuimModel(LoginSessionRemote session){
		super(session);
	}

	public VerzuimInfo getVerzuim(int verzuimid) throws VerzuimApplicationException  {
		try {
			verzuim = ServiceCaller.verzuimFacade(this.getSession()).getVerzuimDetails(verzuimid);
			return verzuim;
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void getVerzuimen(int dienstverbandid) throws VerzuimApplicationException {
		this.dienstverbandid = dienstverbandid;
		List<VerzuimHerstelInfo> herstellen;
		try {
			verzuimen = ServiceCaller.verzuimFacade(this.getSession()).getVerzuimenWerknemer(dienstverbandid);
			herstellen = ServiceCaller.verzuimFacade(this.getSession()).getVerzuimHerstellen(dienstverbandid);
			for (VerzuimInfo v: verzuimen){
				v.setVerzuimherstellen(new ArrayList<VerzuimHerstelInfo>());
				for (VerzuimHerstelInfo h:herstellen)
				{
					List<VerzuimHerstelInfo> vzmherstellen = v.getVerzuimherstellen();
					if (v.getId().equals(h.getVerzuimId())){
						vzmherstellen.add(h);
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(verzuimen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<VerzuimInfo> getVerzuimen() {
		return verzuimen;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			if (dienstverbandid != null){
				getVerzuimen(dienstverbandid);
			}
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addVerzuim(VerzuimInfo verzuim) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).addVerzuim(verzuim);
			/* Now also the list has to be updated */
			verzuimen.add(verzuim);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(verzuim);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void saveVerzuim(VerzuimInfo verzuim) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).updateVerzuim(verzuim);
			/* Now also the list has to be updated */
			for (VerzuimInfo v:verzuimen){
				if (v.getId().equals(verzuim.getId())){
					verzuimen.remove(v);
					break;
				}
			}
			VerzuimInfo refreshedVerzuim = ServiceCaller.verzuimFacade(this.getSession()).getVerzuimDetails(verzuim.getId());
			verzuimen.add(refreshedVerzuim);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(refreshedVerzuim);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void deleteVerzuim(VerzuimInfo data) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteVerzuim(data);
			for (VerzuimInfo w: verzuimen){
				if (w.getId().equals(data.getId())){
					verzuimen.remove(w);
					break;
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(data);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public List<VerzuimHerstelInfo> getVerzuimherstellen() throws VerzuimApplicationException  {
		if (verzuim != null){
			return verzuim.getVerzuimherstellen();
		}else{
			return new ArrayList<>();
		}
	}
	public List<VerzuimDocumentInfo> getVerzuimdocumenten(VerzuimInfo verzuim) throws VerzuimApplicationException  {
		if (verzuim != null){
			return verzuim.getVerzuimdocumenten();
		}else{
			return new ArrayList<>();
		}
	}

	public void saveVerzuimherstel(VerzuimHerstelInfo info, boolean cleanupTodo) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).updateVerzuimHerstel(info, cleanupTodo);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan verzuimherstel mislukt");
		}
	}

	public void addVerzuimherstel(VerzuimHerstelInfo info, boolean cleanupTodo) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).addVerzuimHerstel(info, cleanupTodo);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(verzuimen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Toevoegen verzuimherstel mislukt");
		}
	}

	public void deleteVerzuimherstel(VerzuimHerstelInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteVerzuimHerstel(info);
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Verwijderen verzuimherstel mislukt");
		}	
	}

	public void addVerzuimdocument(VerzuimDocumentInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).addVerzuimDocument(info);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(verzuimen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan verzuimherstel mislukt");
		}
	}

	public void saveVerzuimdocument(VerzuimDocumentInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).updateVerzuimDocument(info);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan document mislukt");
		}
	}

	public void deleteVerzuimdocument(VerzuimDocumentInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteVerzuimDocument(info);
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "Verwijderen document mislukt");
		}	
	}

	public List<VerzuimActiviteitInfo> getVerzuimactiviteiten() {
		if (verzuim != null){
			return verzuim.getVerzuimactiviteiten();
		}else{
			return new ArrayList<>();
		}
	}

	public void addVerzuimactiviteit(VerzuimActiviteitInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).addVerzuimActiviteit(info);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(verzuimen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan verzuimactiviteit mislukt");
		}
	}

	public void saveVerzuimactiviteit(VerzuimActiviteitInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).updateVerzuimActiviteit(info);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan verzuimactiviteit mislukt");
		}
	}

	public void deleteVerzuimactiviteit(VerzuimActiviteitInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteVerzuimActiviteit(info);
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "Verwijderen verzuimactiviteit mislukt");
		}	
	}

	private void refreshVerzuimList(Integer verzuimid) throws PermissionException, VerzuimApplicationException, ServiceLocatorException {
		verzuim = ServiceCaller.verzuimFacade(getSession()).getVerzuimDetails(verzuimid);
		for (VerzuimInfo v:verzuimen){
			if (v.getId().equals(verzuim.getId())){
				verzuimen.remove(v);
				break;
			}
		}
		verzuimen.add(verzuim);
	}

	public List<VerzuimMedischekaartInfo> getVerzuimmedischekaarten() {
		return verzuim.getVerzuimmedischekaarten();
	}

	public void addVerzuimmedischekaart(VerzuimMedischekaartInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).addMedischekaart(info);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(verzuimen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan medische kaart mislukt");
		}
	}

	public void saveVerzuimmedischekaart(VerzuimMedischekaartInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).updateMedischekaart(info);
			/* Now also the list has to be updated */
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan medische kaart mislukt");
		}
	}

	public void deleteVerzuimmedischekaart(VerzuimMedischekaartInfo info) throws VerzuimApplicationException, ValidationException {
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteMedischekaart(info);
			refreshVerzuimList(info.getVerzuimId());
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "Verwijderen medische kaart mislukt");
		}	
	}
}
