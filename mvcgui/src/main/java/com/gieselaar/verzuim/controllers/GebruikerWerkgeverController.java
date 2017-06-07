package com.gieselaar.verzuim.controllers;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.GebruikerModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerWerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class GebruikerWerkgeverController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __gebruikerwerkgeverfields {
		UNKNOWN(-1), ID(0), ACHTERNAAM(1);
		private int value;

		__gebruikerwerkgeverfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __gebruikerwerkgeverfields parse(int type) {
			__gebruikerwerkgeverfields field = UNKNOWN; // Default
			for (__gebruikerwerkgeverfields item : __gebruikerwerkgeverfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private GebruikerInfo gebruiker;

	public GebruikerWerkgeverController(LoginSessionRemote session) {
		super(new GebruikerModel(session), null);
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		GebruikerWerkgeverInfo info = (GebruikerWerkgeverInfo)data;
		try {
			info.validate();
			gebruiker.getWerkgevers().add(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan werkgever bij gebruiker niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		GebruikerInfo info = (GebruikerInfo)data;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan werkgever bij gebruiker niet geslaagd.");
		}
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
}
