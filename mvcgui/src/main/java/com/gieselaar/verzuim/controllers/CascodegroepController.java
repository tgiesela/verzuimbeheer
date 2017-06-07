package com.gieselaar.verzuim.controllers;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.CascodeModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.CascodeGroepDetail;
import com.gieselaar.verzuim.views.CascodeGroepList;
import com.gieselaar.verzuim.views.CascodeList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;

public class CascodegroepController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __cascodegroepfields {
		UNKNOWN(-1), NAAM(0), OMSCHRIJVING(1);
		private int value;

		__cascodegroepfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __cascodegroepfields parse(int type) {
			__cascodegroepfields field = UNKNOWN; // Default
			for (__cascodegroepfields item : __cascodegroepfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __cascodegroepcommands {
		UNKNOWN(-1), CASCODESTONEN(2);
		private int value;

		__cascodegroepcommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __cascodegroepcommands parse(int type) {
			__cascodegroepcommands field = null; // Default
			for (__cascodegroepcommands item : __cascodegroepcommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __cascodegroepcommands parse(String type) {
			__cascodegroepcommands field = __cascodegroepcommands.UNKNOWN; // Default
			for (__cascodegroepcommands item : __cascodegroepcommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private CascodeModel model;
	private CascodeGroepInfo selectedCascodegroep;

	public CascodegroepController(LoginSessionRemote session) {
		super(new CascodeModel(session), null);
		this.model = (CascodeModel)getModel();
		selectedCascodegroep = (CascodeGroepInfo)selectedrow;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__cascodegroepcommands.parse(e.getActionCommand())) {
		case CASCODESTONEN:
			opencascodes();
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}

	public void selectCascodegroepen() throws VerzuimApplicationException {
		model.selectCascodegroepen();
	}

	public void opencascodes() {
		CascodeController controller = new CascodeController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		CascodeList frame = new CascodeList(controller);
		try {
			controller.selectCascodes(selectedCascodegroep.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	
	@Override 
	public void rowSelected(int selectedRow, Object data) {
		selectedCascodegroep = (CascodeGroepInfo)data;
		for (ControllerEventListener l : views) {
			if (l instanceof CascodeGroepList){
				((CascodeGroepList)l).setRowSelected();
			}
		}
	}
	
	private CascodeGroepInfo createNewCascodegroep() {
		CascodeGroepInfo info = new CascodeGroepInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		CascodeGroepInfo info = (CascodeGroepInfo)data;
		try {
			info.validate();
			model.addCascodegroep(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan cascodegroep niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		CascodeGroepInfo info = (CascodeGroepInfo)data;
		try {
			info.validate();
			model.saveCascodegroep(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan cascodegroep niet geslaagd.");
		}
	}
	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		CascodeGroepInfo info = (CascodeGroepInfo) data;
		AbstractDetail form = super.createDetailForm(info, CascodeGroepDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		CascodeGroepInfo info = createNewCascodegroep();
		AbstractDetail form = super.createDetailForm(info, CascodeGroepDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		CascodeGroepInfo info = (CascodeGroepInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteCascodegroep(info);
	}

	public void getTableModel(List<CascodeGroepInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (CascodeGroepInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, CascodeGroepInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__cascodegroepfields val = __cascodegroepfields.parse(colsinview.get(i));
			switch (val) {
			case OMSCHRIJVING:
				colsinmodel.add(i, wfi.getOmschrijving());
				break;
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(CascodeGroepInfo wnr) {
		int rslt = JOptionPane.showConfirmDialog(null, 
				"Weet u zeker dat u de cascodegroep wilt verwijderen? \n" +
				"Hiermee worden alle eraan gekoppelde cascodes ook verwijderd!","",JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		CascodeGroepInfo info = (CascodeGroepInfo)data;
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

	public List<CascodeGroepInfo> getCascodegroepenList() {
		return model.getCascodegroepenList();
	}

}
