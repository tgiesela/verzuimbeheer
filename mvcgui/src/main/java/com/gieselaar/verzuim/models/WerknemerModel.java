package com.gieselaar.verzuim.models;

import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class WerknemerModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<WerknemerFastInfo> activewerknemers;
	private List<WerknemerFastInfo> inactivewerknemers;
	private Integer werkgeverid = null; 
	private boolean inclusiefafgesloten = false;
	public WerknemerModel(LoginSessionRemote session){
		super(session);
	}

	public void getWerknemers() throws VerzuimApplicationException {
		try {
			this.werkgeverid = null;
			activewerknemers = ServiceCaller.werknemerFacade(this.getSession()).getActiveWerknemers();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(activewerknemers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void getWerknemers(Integer werkgeverid) throws VerzuimApplicationException {
		try {
			this.werkgeverid = werkgeverid;
			activewerknemers = ServiceCaller.werknemerFacade(this.getSession()).getActiveWerknemersByWerkgever(werkgeverid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(activewerknemers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void getWerknemersuitdienst() throws VerzuimApplicationException {
		try {
			inclusiefafgesloten = true;
			this.werkgeverid = null;
			inactivewerknemers = ServiceCaller.werknemerFacade(this.getSession()).getInactiveWerknemers();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(inactivewerknemers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void getWerknemersuitdienst(Integer werkgeverid) throws VerzuimApplicationException {
		try {
			inclusiefafgesloten = true;
			this.werkgeverid = werkgeverid;
			inactivewerknemers = ServiceCaller.werknemerFacade(this.getSession()).getInactiveWerknemersByWerkgever(werkgeverid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(activewerknemers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	
	public WerknemerInfo getWerknemerDetails(int werknemerid) throws VerzuimApplicationException  {
		try {
			return ServiceCaller.werknemerFacade(getSession()).getWerknemer(werknemerid);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public List<WerknemerFastInfo> getWerknemerFast(Integer werknemerid) throws VerzuimApplicationException {
		try {
			return ServiceCaller.werknemerFacade(getSession()).getWerknemerFast(werknemerid);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void saveWerknemer(WerknemerInfo werknemer) throws VerzuimApplicationException {
		boolean found = false;
		try {
			ServiceCaller.werknemerFacade(getSession()).updateWerknemer(werknemer);
			/* Now also the list has to be updated */
			if (activewerknemers != null){
				for (WerknemerFastInfo w: activewerknemers){
					if (w.getId().equals(werknemer.getId())){
						found = true;
						activewerknemers.remove(w);
						break;
					}
				}
			}
			if (!found && inactivewerknemers != null){
				for (WerknemerFastInfo w: inactivewerknemers){
					if (w.getId().equals(werknemer.getId())){
						inactivewerknemers.remove(w);
						break;
					}
				}
			}
			List<WerknemerFastInfo> werknemersNew = getWerknemerFast(werknemer.getId());
			if (werknemer.isActief()){
				if (activewerknemers != null){
					activewerknemers.addAll(werknemersNew);
				}
			}else{
				if (inactivewerknemers != null){
					inactivewerknemers.addAll(werknemersNew);
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(werknemer);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void addWerknemer(WerknemerInfo werknemer) throws VerzuimApplicationException {
		try {
			WerknemerInfo werknemerNew = ServiceCaller.werknemerFacade(getSession()).addWerknemer(werknemer);
			List<WerknemerFastInfo> werknemersNew = getWerknemerFast(werknemerNew.getId());
			if (werknemer.isActief()){
				if (activewerknemers != null){
					activewerknemers.addAll(werknemersNew);
				}
			}else{
				if (inactivewerknemers != null){
					inactivewerknemers.addAll(werknemersNew);
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(werknemer);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public List<WerknemerFastInfo> getActiveWerknemersFast() {
		return activewerknemers;
	}
	public List<WerknemerFastInfo> getAllWerknemersFast() {
		List<WerknemerFastInfo> allwerknemers = new ArrayList<>();
		allwerknemers.addAll(activewerknemers);
		if (inactivewerknemers != null){
			allwerknemers.addAll(inactivewerknemers);
		}
		return allwerknemers;
	}
	public void deleteWerknemer(WerknemerInfo werknemer) throws VerzuimApplicationException {
		boolean found = true;
		try {
			ServiceCaller.werknemerFacade(getSession()).deleteWerknemer(werknemer);
			if (activewerknemers != null){
				for (WerknemerFastInfo w: activewerknemers){
					if (w.getId().equals(werknemer.getId())){
						activewerknemers.remove(w);
						found = true;
						break;
					}
				}
			}
			if (!found && inactivewerknemers != null){
				for (WerknemerFastInfo w: inactivewerknemers){
					if (w.getId().equals(werknemer.getId())){
						inactivewerknemers.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(werknemer);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		if (werkgeverid != null){
			this.getWerknemers(werkgeverid);
			if (inclusiefafgesloten){
				this.getWerknemersuitdienst(werkgeverid);
			}
		}else{
			this.getWerknemers();
			if (inclusiefafgesloten){
				this.getWerknemersuitdienst();
			}
		}
	}

	public List<WerknemerInfo> getByBSN(Integer werkgeverid, String bsn) throws VerzuimApplicationException {
		try {
			return ServiceCaller.werknemerFacade(getSession()).getByBSN(werkgeverid, bsn);
		} catch (PermissionException  | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<WerknemerInfo> getByBSN(String bsn) throws VerzuimApplicationException {
		try {
			return ServiceCaller.werknemerFacade(getSession()).getByBSN(bsn);
		} catch (PermissionException  | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
}
