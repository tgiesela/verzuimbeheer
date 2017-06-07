package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.InstantieModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.BedrijfsartsDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class BedrijfsartsController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __bedrijfsartsfields {
		UNKNOWN(-1), ACHTERNAAM(1), VOORNAAM(2), TELEFOON(3), EMAIL(4);
		private int value;

		__bedrijfsartsfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __bedrijfsartsfields parse(int type) {
			__bedrijfsartsfields field = UNKNOWN; // Default
			for (__bedrijfsartsfields item : __bedrijfsartsfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private InstantieModel model;
	
	private ArbodienstInfo selectedarbodienst;

	public BedrijfsartsController(LoginSessionRemote session) {
		super(new InstantieModel(session), null);
		this.model = (InstantieModel)getModel();
	}

	public void selectBedrijfsartsen() throws VerzuimApplicationException {
		model.selectBedrijfsartsen();
	}
	public void selectBedrijfsartsen(Integer arbodienstid) throws VerzuimApplicationException {
		model.selectBedrijfsartsen(arbodienstid);
	}

	private BedrijfsartsInfo createNewBedrijfsarts() {
		BedrijfsartsInfo info = new BedrijfsartsInfo();
		info.setGeslacht(__geslacht.ONBEKEND);
		info.setArbodienstId(selectedarbodienst.getId());
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		BedrijfsartsInfo info = (BedrijfsartsInfo)data;
		try {
			info.validate();
			model.addBedrijfsarts(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan arbodienst niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		BedrijfsartsInfo info = (BedrijfsartsInfo)data;
		try {
			info.validate();
			model.saveBedrijfsarts(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan bedrijfsarts niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		BedrijfsartsInfo info = (BedrijfsartsInfo) data;
		AbstractDetail form = super.createDetailForm(info, BedrijfsartsDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		BedrijfsartsInfo info = createNewBedrijfsarts();
		AbstractDetail form = super.createDetailForm(info, BedrijfsartsDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		BedrijfsartsInfo info = (BedrijfsartsInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteBedrijfsarts(info);
	}

	public void getTableModel(List<BedrijfsartsInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (BedrijfsartsInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, BedrijfsartsInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__bedrijfsartsfields val = __bedrijfsartsfields.parse(colsinview.get(i));
			switch (val) {
			case ACHTERNAAM:
				colsinmodel.add(i, wfi.getAchternaam());
				break;
			case VOORNAAM:
				colsinmodel.add(i, wfi.getVoornaam());
				break;
			case EMAIL:
				colsinmodel.add(i, wfi.getEmail());
				break;
			case TELEFOON:
				colsinmodel.add(i, wfi.getTelefoon());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(BedrijfsartsInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze bedrijfsarts wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		BedrijfsartsInfo info = (BedrijfsartsInfo)data;
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

	public List<BedrijfsartsInfo> getBedrijfsartsenList() {
		return model.getBedrijfsartenList();
	}

	public ArbodienstInfo getSelectedarbodienst() {
		return selectedarbodienst;
	}

	public void setSelectedArboDienst(ArbodienstInfo selectedArboDienst) {
		this.selectedarbodienst = selectedArboDienst;
	}

}
