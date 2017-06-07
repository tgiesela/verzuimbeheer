package com.gieselaar.verzuim.models;

import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class WerkzaamhedenModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<WerkzaamhedenInfo> werkzaamheden;
	private Integer holdingid = null;
	private Integer werkgeverid = null;
	private Date startdate = null;
	private Date enddate = null;
	private Integer user = null;
	public WerkzaamhedenModel(LoginSessionRemote session){
		super(session);
	}

	public void selectWerkzaamheden(Integer user, Date startdate, Date enddate) throws VerzuimApplicationException, ValidationException {
		try {
			this.user = user;
			this.holdingid = null;
			this.werkgeverid = null;
			this.startdate = startdate;
			this.enddate = enddate;
			werkzaamheden = ServiceCaller.factuurFacade(this.getSession()).getWerkzaamheden(user, startdate, enddate);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(werkzaamheden);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectWerkzaamhedenHolding(Integer user, Date startdate, Date enddate, Integer holdingid) throws VerzuimApplicationException, ValidationException {
		try {
			this.user = user;
			this.holdingid = holdingid;
			this.werkgeverid = null;
			this.startdate = startdate;
			this.enddate = enddate;
			werkzaamheden = ServiceCaller.factuurFacade(this.getSession()).getWerkzaamhedenHolding(user, startdate, enddate, holdingid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(werkzaamheden);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectWerkzaamhedenWerkgever(Integer user, Date startdate, Date enddate, Integer werkgeverid) throws VerzuimApplicationException, ValidationException {
		try {
			this.user = user;
			this.holdingid = null;
			this.werkgeverid = werkgeverid;
			this.startdate = startdate;
			this.enddate = enddate;
			werkzaamheden = ServiceCaller.factuurFacade(this.getSession()).getWerkzaamhedenWerkgever(user, startdate, enddate, werkgeverid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(werkzaamheden);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<WerkzaamhedenInfo> getWerkzaamhedenList() {
		return werkzaamheden;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			if (this.holdingid != null){
				selectWerkzaamhedenHolding(this.user, this.startdate, this.enddate, this.holdingid);
			}else{
				if (this.werkgeverid != null){
					selectWerkzaamhedenHolding(this.user, this.startdate, this.enddate, this.werkgeverid);
				}else{
					selectWerkzaamheden(this.user, this.startdate, this.enddate);
				}
			}
		} catch (VerzuimApplicationException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addWerkzaamheid(WerkzaamhedenInfo info) throws VerzuimApplicationException {
		try {
			WerkzaamhedenInfo werkzaamheid = ServiceCaller.factuurFacade(getSession()).addWerkzaamheid(info);
			werkzaamheden.add(werkzaamheid);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(werkzaamheid);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveWerkzaamheid(WerkzaamhedenInfo info) throws VerzuimApplicationException {
		try {
			WerkzaamhedenInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateWerkzaamheid(info);
			/* Now also the list has to be updated */
			for (WerkzaamhedenInfo w: werkzaamheden){
				if (w.getId().equals(info.getId())){
					werkzaamheden.remove(w);
					break;
				}
			}
			werkzaamheden.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteWerkzaamheid(WerkzaamhedenInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.factuurFacade(getSession()).deleteWerkzaamheid(info);
			for (WerkzaamhedenInfo w: werkzaamheden){
				if (w.getId().equals(info.getId())){
					werkzaamheden.remove(w);
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
