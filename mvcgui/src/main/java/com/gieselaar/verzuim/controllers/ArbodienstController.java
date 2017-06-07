package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.InstantieModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.ArbodienstDetail;
import com.gieselaar.verzuim.views.BedrijfsartsenList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class ArbodienstController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __arbodienstfields {
		UNKNOWN(-1), PLAATS(1), NAAM(2);
		private int value;

		__arbodienstfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __arbodienstfields parse(int type) {
			__arbodienstfields field = UNKNOWN; // Default
			for (__arbodienstfields item : __arbodienstfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private InstantieModel model;
	
	public ArbodienstController(LoginSessionRemote session) {
		super(new InstantieModel(session), null);
		this.model = (InstantieModel)getModel();
	}

	public void selectArbodiensten() throws VerzuimApplicationException {
		model.selectArbodiensten();
	}

	public void openbedrijfsartsen() {
		BedrijfsartsController controller = new BedrijfsartsController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		ArbodienstInfo selectedarbodienst = (ArbodienstInfo)selectedrow;
		controller.setSelectedArboDienst(selectedarbodienst);
		BedrijfsartsenList frame = new BedrijfsartsenList(controller);
		try {
			controller.selectBedrijfsartsen(selectedarbodienst.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private ArbodienstInfo createNewArbodienst() {
		ArbodienstInfo info = new ArbodienstInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		ArbodienstInfo info = (ArbodienstInfo)data;
		try {
			info.validate();
			model.addArbodienst(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan arbodienst niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		ArbodienstInfo info = (ArbodienstInfo)data;
		try {
			info.validate();
			model.saveArbodienst(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan arbodienst niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		ArbodienstInfo info = (ArbodienstInfo) data;
		AbstractDetail form = super.createDetailForm(info, ArbodienstDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		ArbodienstInfo info = createNewArbodienst();
		AbstractDetail form = super.createDetailForm(info, ArbodienstDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		ArbodienstInfo info = (ArbodienstInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteArbodienst(info);
	}

	public void getTableModel(List<ArbodienstInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (ArbodienstInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, ArbodienstInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__arbodienstfields val = __arbodienstfields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			case PLAATS:
				if (wfi.getVestigingsAdres() != null && !wfi.getVestigingsAdres().isEmtpy()){
					colsinmodel.add(i, wfi.getVestigingsAdres().getPlaats());
				}else{
					colsinmodel.add(i, "");
				}
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(ArbodienstInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze arbodienst wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		ArbodienstInfo info = (ArbodienstInfo)data;
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

	public List<ArbodienstInfo> getArbodienstList() {
		return model.getArbodienstenList();
	}
}
