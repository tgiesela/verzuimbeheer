package com.gieselaar.verzuim.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.BetalingenModel;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuurbetalingDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;

public class BetalingenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __betalingenfields {
		UNKNOWN(-1), DATUM(0), REKENINGBETALER(1), BEDRAG(2);
		private int value;

		__betalingenfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __betalingenfields parse(int type) {
			__betalingenfields field = UNKNOWN; // Default
			for (__betalingenfields item : __betalingenfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private BetalingenModel model;
	private FactuurModel factuurmodel;
	
	private FactuurTotaalInfo selectedFactuur;
	public BetalingenController(LoginSessionRemote session) {
		super(new BetalingenModel(session), null);
		this.model = (BetalingenModel) getModel();
		factuurmodel = new FactuurModel(session);
	}

	public void selectBetalingen() throws VerzuimApplicationException {
		if (selectedFactuur.isGesommeerd()){
			model.selectBetalingen(selectedFactuur.getHoldingFactuurId());
		}else{
			model.selectBetalingen(selectedFactuur.getId());
		}
	}
	private FactuurbetalingInfo createNewBetaling() {
		FactuurbetalingInfo info = new FactuurbetalingInfo();
		if (selectedFactuur.isGesommeerd()){
			info.setFactuurid(selectedFactuur.getHoldingFactuurId());
		}else{
			info.setFactuurid(selectedFactuur.getId());
		}
		
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		FactuurbetalingInfo info = (FactuurbetalingInfo)data;
		try {
			info.validate();
			model.addFactuurbetaling(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan betaling niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		FactuurbetalingInfo info = (FactuurbetalingInfo)data;
		try {
			info.validate();
			model.saveFactuurbetaling(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan betaling niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		FactuurbetalingInfo info = (FactuurbetalingInfo) data;
		AbstractDetail form = super.createDetailForm(info, FactuurbetalingDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		FactuurbetalingInfo info = createNewBetaling();
		AbstractDetail form = super.createDetailForm(info, FactuurbetalingDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		FactuurbetalingInfo info = (FactuurbetalingInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteFactuurbetaling(info);
	}

	public void getTableModel(List<FactuurbetalingInfo> betalingen, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (FactuurbetalingInfo info : betalingen) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, FactuurbetalingInfo ti, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__betalingenfields val = __betalingenfields.parse(colsinview.get(i));
			switch (val) {
			case BEDRAG:
				colsinmodel.add(i, ti.getBedrag());
				break;
			case DATUM:
				colsinmodel.add(i, ti.getDatum());
				break;
			case REKENINGBETALER:
				colsinmodel.add(i,ti.getRekeningnummerbetaler());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(FactuurbetalingInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze betaling wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		FactuurbetalingInfo info = (FactuurbetalingInfo)data;
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

	public List<FactuurbetalingInfo> getBetalingenList() {
		return model.getBetalingenList(); 
	}

	public void setFactuur(FactuurTotaalInfo selectedFactuur) {
		this.selectedFactuur = selectedFactuur;
		
	}

	public List<FactuurTotaalInfo> getFacturenInPeriode(Date firstmonth, Date lastmonth) {
		try {
			return factuurmodel.getFacturenInPeriode(firstmonth, lastmonth, false);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
			return null;
		}
		
	}

	public List<FactuurbetalingInfo> getFactuurbetalingenForFactuur(Integer factuurid) {
		try {
			return factuurmodel.getFactuurbetalingenForFactuur(factuurid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}

	public FactuurTotaalInfo findFactuurByFactuurnr(Integer factuurnummer, Integer werkgeverid, Integer holdingid,
			BigDecimal bedrag) {
		return model.findFactuurByFactuurnr(factuurnummer, werkgeverid, holdingid, bedrag);
	}
	
}
