package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.models.WerknemerModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.WiapercentageDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;

public class WiapercentageController extends AbstractController {

	/* 
	 * This controller does not have its own model. The departments
	 * are extracten from the werknemer.
	 * The interface looks like a normal controller. This allows us
	 * to change this in the future to a real model behind this controller
	 */

	
	private static final long serialVersionUID = 1L;
	
	public enum __wiapercentagefields {
		UNKNOWN(-1), PERCENTAGE(0), INGANGSDATUM(1), EINDDATUM(2);
		private int value;

		__wiapercentagefields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __wiapercentagefields parse(int type) {
			__wiapercentagefields field = UNKNOWN; // Default
			for (__wiapercentagefields item : __wiapercentagefields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkgeverModel werkgevermodel;
	private WerknemerInfo selectedWerknemer;
	private List<WiapercentageInfo> wiapercentages;

	public WiapercentageController(LoginSessionRemote session) {
		super(new WerknemerModel(session), null);
		this.werkgevermodel = new WerkgeverModel(session);
		selectedWerknemer = (WerknemerInfo)selectedrow;
	}

	public void getWiapercentages() {
		/* 
		 * Simulate access to model and send completions
		 */
		wiapercentages = selectedWerknemer.getWiaPercentages();
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		WiapercentageInfo wiapercentage = (WiapercentageInfo)data;
		try {
			wiapercentage.validate();
			for (WiapercentageInfo w: wiapercentages){
				if (w.getEinddatum() == null){
					w.setEinddatum(DateOnly.addDays(wiapercentage.getStartdatum(),-1));
				}
			}
			wiapercentages.add(wiapercentage);
			for (ControllerEventListener l:views){
				l.refreshTable();
			}
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		WiapercentageInfo wiapercentage = (WiapercentageInfo)data;
		try {
			wiapercentage.validate();
			for (ControllerEventListener l:views){
				l.refreshTable();
			}
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		WiapercentageInfo wiapercentage = (WiapercentageInfo)data;
		AbstractDetail form = super.createDetailForm(wiapercentage, WiapercentageDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		WiapercentageInfo wiapercentage = new WiapercentageInfo();
		wiapercentage.setWerknemerId(selectedWerknemer.getId());
		AbstractDetail form = super.createDetailForm(wiapercentage, WiapercentageDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		WiapercentageInfo wiapercentage = (WiapercentageInfo)data;
		wiapercentage.setAction(persistenceaction.DELETE);
		wiapercentage.setState(persistencestate.EXISTS);
		wiapercentage.setId(wiapercentage.getId());
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	public void getTableModel(List<WiapercentageInfo> wiapercentages, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (WiapercentageInfo wfi : wiapercentages) {
			List<Object> colsinmodel = new ArrayList<>();
			setColumnValues(colsinmodel, wfi, colsinview);
			if (wfi.getAction() == persistenceaction.DELETE){
			}else{
				if (wfi.getEinddatum() == null){
					tblmodel.addRow(colsinmodel,wfi);
				}else{
					tblmodel.addRow(colsinmodel, wfi, Color.GRAY);
				}
			}
		}
	}
	private void setColumnValues(List<Object> colsinmodel, WiapercentageInfo wpi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__wiapercentagefields val = __wiapercentagefields.parse(colsinview.get(i));
			switch (val) {
			case PERCENTAGE:
				colsinmodel.add(i, wpi.getCodeWiaPercentage().toString());
				break;
			case INGANGSDATUM:
				colsinmodel.add(i, wpi.getStartdatum());
				break;
			case EINDDATUM:
				colsinmodel.add(i, wpi.getEinddatum());
				break;
			default:
				break;
			}
		}
	}

	public List<WiapercentageInfo> getWiapercentagesList() {
		return selectedWerknemer.getWiaPercentages();
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
		return true;
	}

	public WerkgeverInfo getWerkgeverDetails(int werkgeverid) {
		try {
			return werkgevermodel.getWerkgeverDetails(werkgeverid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}

	public void setWerknemer(WerknemerInfo werknemer) {
		this.selectedWerknemer = werknemer;
	}
}
