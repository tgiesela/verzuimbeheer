package com.gieselaar.verzuim.controllers;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.GebruikerModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.GebruikerDetail;
import com.gieselaar.verzuim.views.GebruikerRollen;
import com.gieselaar.verzuim.views.GebruikerWerkgevers;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class GebruikerController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __gebruikerfields {
		UNKNOWN(-1), ID(0), ACHTERNAAM(1);
		private int value;

		__gebruikerfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __gebruikerfields parse(int type) {
			__gebruikerfields field = UNKNOWN; // Default
			for (__gebruikerfields item : __gebruikerfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __gebruikercommands {
		UNKNOWN(-1), WERKGEVERGEBRUIKERSTONEN(1), ROLLENTONEN(2);
		private int value;

		__gebruikercommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __gebruikercommands parse(int type) {
			__gebruikercommands field = null; // Default
			for (__gebruikercommands item : __gebruikercommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __gebruikercommands parse(String type) {
			__gebruikercommands field = __gebruikercommands.UNKNOWN; // Default
			for (__gebruikercommands item : __gebruikercommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private GebruikerModel model;
	private GebruikerInfo gebruiker;
	public GebruikerController(LoginSessionRemote session) {
		super(new GebruikerModel(session), null);
		this.model = (GebruikerModel)getModel();
	}

	public void selectGebruikers() throws VerzuimApplicationException {
		model.selectGebruikers();
	}

	private GebruikerInfo createNewGebruiker() {
		GebruikerInfo info = new GebruikerInfo();
		return info;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__gebruikercommands.parse(e.getActionCommand())) {
		case WERKGEVERGEBRUIKERSTONEN:
			openGebruikerWerkgevers();
			break;
		case ROLLENTONEN:
			openGebruikerRollen();
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}
	
	private void openGebruikerRollen() {
		GebruikerRollenController rollencontroller = new GebruikerRollenController(this.getModel().getSession());
		rollencontroller.setDesktoppane(getDesktoppane());
		rollencontroller.setMaincontroller(this.getMaincontroller());
		GebruikerRollen rollenframe = new GebruikerRollen(rollencontroller);
		try {
			rollencontroller.selectRollen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		rollenframe.setData(gebruiker);
		rollenframe.setVisible(true);
		this.getDesktoppane().add(rollenframe);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(rollenframe);
	}

	private void openGebruikerWerkgevers() {
		GebruikerWerkgeverController controller = new GebruikerWerkgeverController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		GebruikerWerkgevers frame = new GebruikerWerkgevers(controller);
		controller.setGebruiker(gebruiker);
		frame.setData(gebruiker);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		GebruikerInfo info = (GebruikerInfo)data;
		try {
			info.validate();
			model.addGebruiker(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan gebruiker niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		GebruikerInfo info = (GebruikerInfo)data;
		try {
			info.validate();
			model.saveGebruiker(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan gebruiker niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		GebruikerInfo info = (GebruikerInfo) data;
		try {
			gebruiker = model.getGebruiker(info.getId());
			AbstractDetail form = super.createDetailForm(gebruiker, GebruikerDetail.class, __formmode.UPDATE);
			super.showRow(ves, form, gebruiker);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		gebruiker = createNewGebruiker();
		AbstractDetail form = super.createDetailForm(gebruiker, GebruikerDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		GebruikerInfo info = (GebruikerInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteGebruiker(info);
	}

	public void getTableModel(List<GebruikerInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (GebruikerInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, GebruikerInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__gebruikerfields val = __gebruikerfields.parse(colsinview.get(i));
			switch (val) {
			case ID:
				colsinmodel.add(i, wfi.getName());
				break;
			case ACHTERNAAM:
				colsinmodel.add(i, wfi.getAchternaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(GebruikerInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit document wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		GebruikerInfo info = (GebruikerInfo)data;
		return confirmDelete(info);
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public List<GebruikerInfo> getGebruikerList() {
		return model.getGebruikersList();
	}
}
