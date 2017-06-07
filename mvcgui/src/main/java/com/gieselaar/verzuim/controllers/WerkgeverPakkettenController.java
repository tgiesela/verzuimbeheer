package com.gieselaar.verzuim.controllers;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.AbstractModel;
import com.gieselaar.verzuim.models.PakketModel;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.AfdelingHasWerknemerDetail;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.PakketInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class WerkgeverPakkettenController extends AbstractController {

	/* 
	 * This controller does not have its own model. The departments
	 * are extracten from the werknemer.
	 * The interface looks like a normal controller. This allows us
	 * to change this in the future to a real model behind this controller
	 */

	
	private static final long serialVersionUID = 1L;
	
	private WerkgeverModel model;
	private PakketModel pakketmodel;
	private WerkgeverInfo selectedWerkgever;

	public WerkgeverPakkettenController(LoginSessionRemote session, AbstractModel abstractmodel) {
		super(abstractmodel, null);
		this.model = (WerkgeverModel) getModel();
		pakketmodel = new PakketModel(model.getSession());
		selectedWerkgever = (WerkgeverInfo)selectedrow;
	}

	public void getPakketten() throws VerzuimApplicationException {
		/* 
		 * Simulate access to model and send completions
		 */
		pakketmodel.selectPakketten();
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		PakketInfo afdeling = (PakketInfo)data;
		AbstractDetail form = super.createDetailForm(afdeling, AfdelingHasWerknemerDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		PakketInfo pakket = new PakketInfo();
		AbstractDetail form = super.createDetailForm(pakket, AfdelingHasWerknemerDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		PakketInfo afdeling =  (PakketInfo)data;
		afdeling.setAction(persistenceaction.DELETE);
		afdeling.setState(persistencestate.EXISTS);
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	public List<PakketInfo> getPakkettenWerkgeverList() {
		return selectedWerkgever.getPakketten();
	}
	public List<PakketInfo> getPakkettenList() {
		return pakketmodel.getPakkettenList();
	}

	public WerkgeverInfo getSelectedWerkgever() {
		return selectedWerkgever;
	}

	public void setSelectedWerkgever(WerkgeverInfo selectedWerkgever) {
		this.selectedWerkgever = selectedWerkgever;
	}

	private boolean confirmDelete() {
		return true;
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		return confirmDelete();
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return false;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {/* noop */}
}
