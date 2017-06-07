package com.gieselaar.verzuim.controllers;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.GebruikerModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerWerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.RolInfo;

public class GebruikerRollenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __gebruikerrollenfields {
		UNKNOWN(-1), ID(0), ACHTERNAAM(1);
		private int value;

		__gebruikerrollenfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __gebruikerrollenfields parse(int type) {
			__gebruikerrollenfields field = UNKNOWN; // Default
			for (__gebruikerrollenfields item : __gebruikerrollenfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private GebruikerModel model;
	private GebruikerInfo gebruiker;

	public GebruikerRollenController(LoginSessionRemote session) {
		super(new GebruikerModel(session), null);
		this.model = (GebruikerModel)getModel();
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		/* nothing to do */
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		/* nothing to do */
	}

	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		GebruikerWerkgeverInfo info = (GebruikerWerkgeverInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
	}

	public void setGebruiker(GebruikerInfo gebruiker) {
		this.gebruiker = gebruiker;
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public List<GebruikerWerkgeverInfo> getGebruikerWerkgeversList() {
		return gebruiker.getWerkgevers();
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		/* noop */
	}

	@Override
	public void addRow(ControllerEventListener ves) {
		/* noop */
	}

	public List<RolInfo> getRollen() {
		return model.getRollenList();
	}

	public void selectRollen() throws VerzuimApplicationException {
		model.selectRollen();
	}
}
