package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.InstantieModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.UitkeringsinstantieDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class UitkeringsinstituutController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __uitkeringsinstituutfields {
		UNKNOWN(-1), PLAATS(1), NAAM(2);
		private int value;

		__uitkeringsinstituutfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __uitkeringsinstituutfields parse(int type) {
			__uitkeringsinstituutfields field = UNKNOWN; // Default
			for (__uitkeringsinstituutfields item : __uitkeringsinstituutfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private InstantieModel model;
	
	public UitkeringsinstituutController(LoginSessionRemote session) {
		super(new InstantieModel(session), null);
		this.model = (InstantieModel)getModel();
	}

	public void selectUitkeringsinstituten() throws VerzuimApplicationException {
		model.selectUitkeringsinstanties();
	}
	private UitvoeringsinstituutInfo createNewUitkeringsinstituut() {
		UitvoeringsinstituutInfo info = new UitvoeringsinstituutInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		UitvoeringsinstituutInfo info = (UitvoeringsinstituutInfo)data;
		try {
			info.validate();
			model.addUitkerinsinstantie(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan Uitkeringsinstituut niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		UitvoeringsinstituutInfo info = (UitvoeringsinstituutInfo)data;
		try {
			info.validate();
			model.saveUitkerinsinstantie(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan Uitkeringsinstituut niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		UitvoeringsinstituutInfo info = (UitvoeringsinstituutInfo) data;
		AbstractDetail form = super.createDetailForm(info, UitkeringsinstantieDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		UitvoeringsinstituutInfo info = createNewUitkeringsinstituut();
		AbstractDetail form = super.createDetailForm(info, UitkeringsinstantieDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		UitvoeringsinstituutInfo info = (UitvoeringsinstituutInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteUitkerinsinstantie(info);
	}

	public void getTableModel(List<UitvoeringsinstituutInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (UitvoeringsinstituutInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, UitvoeringsinstituutInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__uitkeringsinstituutfields val = __uitkeringsinstituutfields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			case PLAATS:
				if (wfi.getVestigingsadres() != null && !wfi.getVestigingsadres().isEmtpy()){
					colsinmodel.add(i, wfi.getVestigingsadres().getPlaats());
				}else{
					colsinmodel.add(i, "");
				}
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(UitvoeringsinstituutInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit Uitkeringsinstituut wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		UitvoeringsinstituutInfo info = (UitvoeringsinstituutInfo)data;
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

	public List<UitvoeringsinstituutInfo> getUitkeringsinstituutList() {
		return model.getUitkeringsinstantiesList();
	}
}
