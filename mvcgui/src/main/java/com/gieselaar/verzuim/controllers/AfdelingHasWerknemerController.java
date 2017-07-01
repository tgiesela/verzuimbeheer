package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.AbstractModel;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.AfdelingHasWerknemerDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

public class AfdelingHasWerknemerController extends AbstractController {

	/* 
	 * This controller does not have its own model. The departments
	 * are extracten from the werknemer.
	 * The interface looks like a normal controller. This allows us
	 * to change this in the future to a real model behind this controller
	 */

	
	private static final long serialVersionUID = 1L;
	
	public enum __werknemerafdelingfields {
		UNKNOWN(-1), NAAM(0), INGANGSDATUM(1), EINDDATUM(2);
		private int value;

		__werknemerafdelingfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __werknemerafdelingfields parse(int type) {
			__werknemerafdelingfields field = UNKNOWN; // Default
			for (__werknemerafdelingfields item : __werknemerafdelingfields.values()) {
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
	private List<AfdelingHasWerknemerInfo> afdelingen;
	private List<AfdelingInfo> werkgeverafdelingen;

	public AfdelingHasWerknemerController(LoginSessionRemote session, AbstractModel abstractmodel) {
		super(abstractmodel, null);
		this.werkgevermodel = new WerkgeverModel(session);
		selectedWerknemer = (WerknemerInfo)selectedrow;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		AfdelingHasWerknemerInfo afdeling = (AfdelingHasWerknemerInfo)data;
		try {
			for (AfdelingInfo afd: werkgeverafdelingen){
				if (afd.getId().equals(afdeling.getAfdelingId())){
					afdeling.setAfdeling(afd);
					break;
				}
			}
			afdeling.validate();
			for (AfdelingHasWerknemerInfo w: afdelingen){
				if (w.getEinddatum() == null){
					w.setEinddatum(DateOnly.addDays(afdeling.getStartdatum(),-1));
				}
			}
			
			afdelingen.add(afdeling);
			for (ControllerEventListener l:views){
				l.refreshTable();
			}
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan afdelingen");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		AfdelingHasWerknemerInfo afdeling = (AfdelingHasWerknemerInfo)data;
		try {
			for (AfdelingInfo afd: werkgeverafdelingen){
				if (afd.getId().equals(afdeling.getAfdelingId())){
					afdeling.setAfdeling(afd);
					break;
				}
			}
			afdeling.validate();
			for (ControllerEventListener l:views){
				l.refreshTable();
			}
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan afdeling");
		}
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		AfdelingHasWerknemerInfo afdeling = (AfdelingHasWerknemerInfo)data;
		AbstractDetail form = super.createDetailForm(afdeling, AfdelingHasWerknemerDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		AfdelingHasWerknemerInfo afdeling = new AfdelingHasWerknemerInfo();
		afdeling.setWerknemerId(selectedWerknemer.getId());
		afdeling.setWerkgeverId(selectedWerknemer.getWerkgeverid());
		AbstractDetail form = super.createDetailForm(afdeling, AfdelingHasWerknemerDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		AfdelingHasWerknemerInfo afdeling =  (AfdelingHasWerknemerInfo)data;
		afdeling.setAction(persistenceaction.DELETE);
		afdeling.setState(persistencestate.EXISTS);
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	public void getTableModel(List<AfdelingHasWerknemerInfo> afdelingen, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (AfdelingHasWerknemerInfo wfi : afdelingen) {
			List<Object> colsinmodel = new ArrayList<>();
			setColumnValues(colsinmodel, wfi, colsinview);
			if (wfi.getAction() == persistenceaction.DELETE){
				/* skip deleted rows */
			}else{
				if (wfi.getEinddatum() == null){
					tblmodel.addRow(colsinmodel,wfi);
				}else{
					tblmodel.addRow(colsinmodel, wfi, Color.GRAY);
				}
			}
		}
	}
	public DefaultComboBoxModel<TypeEntry> getComboModelWerkgeverAfdelingen() {
		/* Just a utility function to create ComboBoxModel */
		Integer werkgeverid = selectedWerknemer.getWerkgeverid();
		WerkgeverInfo werkgever = getWerkgeverDetails(werkgeverid);
		VerzuimComboBoxModel afdelingModel = new VerzuimComboBoxModel(this);
		werkgeverafdelingen = werkgever.getAfdelings();
		for (AfdelingInfo w: werkgever.getAfdelings())
		{
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			afdelingModel.addElement(wg);
		}
		return afdelingModel;
	}

	private void setColumnValues(List<Object> colsinmodel, AfdelingHasWerknemerInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__werknemerafdelingfields val = __werknemerafdelingfields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getAfdeling().getNaam());
				break;
			case INGANGSDATUM:
				colsinmodel.add(i, wfi.getStartdatum());
				break;
			case EINDDATUM:
				colsinmodel.add(i, wfi.getEinddatum());
				break;
			default:
				break;
			}
		}
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
		WerkgeverInfo werkgever;
		try {
			werkgever = werkgevermodel.getWerkgeverDetails(selectedWerknemer.getWerkgeverid());
			if (werkgever.getAfdelings() != null && !werkgever.getAfdelings().isEmpty()){
				return true;
			}else{
				JOptionPane.showMessageDialog(null, "Er zijn (nog) geen afdelingen bij de werkgever");
				return false;
			}
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return false;
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
	
	public List<AfdelingHasWerknemerInfo> getAfdelingenList() {
		return selectedWerknemer.getAfdelingen();
	}

	public void selectAfdelingen() {
		/* 
		 * Simulate access to model and send completions
		 */
		afdelingen = selectedWerknemer.getAfdelingen();
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}
}
