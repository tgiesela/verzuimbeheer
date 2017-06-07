package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.HoldingDetail;
import com.gieselaar.verzuim.views.TariefList;
import com.gieselaar.verzuim.views.WerkgeverList;
import com.gieselaar.verzuim.views.HoldingList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class HoldingController extends AbstractController {
	
	private static final long serialVersionUID = 1L;
	public enum __holdingfields {
		UNKNOWN(-1), NAAM(0), HOLDINGNAAM(1), POSTCODE(2), PLAATS(3), INGANGSDATUM(4), EINDDATUM(5);
		private int value;

		__holdingfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __holdingfields parse(int type) {
			__holdingfields field = UNKNOWN; // Default
			for (__holdingfields item : __holdingfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __holdingcommands {
		UNKNOWN(-1), HOLDINGAFGESLOTENTONEN(1), WERKGEVERSTONEN(2);
		private int value;

		__holdingcommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __holdingcommands parse(int type) {
			__holdingcommands field = null; // Default
			for (__holdingcommands item : __holdingcommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __holdingcommands parse(String type) {
			__holdingcommands field = __holdingcommands.UNKNOWN; // Default
			for (__holdingcommands item : __holdingcommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkgeverModel model;
	private boolean afgeslotentonen = false;
	private HoldingInfo selectedHolding;
	public HoldingController(LoginSessionRemote session){
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel) getModel();
		selectedHolding = (HoldingInfo)selectedrow;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__holdingcommands.parse(e.getActionCommand())) {
		case HOLDINGAFGESLOTENTONEN:
			afgeslotentonen = !afgeslotentonen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		case WERKGEVERSTONEN:
			WerkgeverController controller = new WerkgeverController(this.getModel().getSession());
			controller.setDesktoppane(getDesktoppane());
			controller.setMaincontroller(this.getMaincontroller());
			WerkgeverList frame = new WerkgeverList(controller);
			try {
				controller.selectWerkgevers(selectedHolding.getId());
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, null);
			}
			frame.setVisible(true);
			this.getDesktoppane().add(frame);
			this.getDesktoppane().setOpaque(true);
			this.getDesktoppane().moveToFront(frame);
			
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}
	@Override 
	public void rowSelected(int selectedRow, Object data) {
		selectedHolding = (HoldingInfo)data;
		for (ControllerEventListener l : views) {
			if (l instanceof HoldingList){
				((HoldingList)l).setRowSelected();
			}
		}
	}
	public void selectHoldings(){
		try {
			model.selectHoldings();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public HoldingInfo getHoldingDetails(int holdingid){
		try {
			return model.getHoldingDetails(holdingid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}
	public void setFilterObsolete(boolean filterobsolete) {
		model.setFilterObsolete(filterobsolete);
	}
	public List<HoldingInfo> getHoldingList() {
		return model.getHoldingsList();
	}
	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,
						"Weet u zeker dat u de holding wilt verwijderen?\n"
								+ "Hiermee wordt alle informatie definitief verwijderd.",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}
	@Override
	protected boolean isNewAllowed() {
		return true;
	}
	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		HoldingInfo wfi = (HoldingInfo) data;
		try {
			selectedHolding = model.getHoldingDetails(wfi.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		AbstractDetail form = super.createDetailForm(selectedHolding, HoldingDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		HoldingInfo werkgever = createNewHolding();
		AbstractDetail form = super.createDetailForm(werkgever, HoldingDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		HoldingInfo info = (HoldingInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteHolding(info);
	}
	
	private HoldingInfo createNewHolding() {
		HoldingInfo holding = new HoldingInfo();
		holding.setStartdatumcontract(new Date());
		holding.setPostAdres(new AdresInfo());
		holding.setVestigingsAdres(new AdresInfo());
		holding.setContactpersoon(new ContactpersoonInfo());
		return null;
	}
	public void getTableModel(List<HoldingInfo> holdings, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (HoldingInfo wfi : holdings) {
			List<Object> colsinmodel = new ArrayList<>();
			setColumnValues(colsinmodel, wfi, colsinview, holdings);
			if (!wfi.isActief()) {
				if (afgeslotentonen)
					tblmodel.addRow(colsinmodel, (Object) wfi, Color.GRAY);
			} else { 
				tblmodel.addRow(colsinmodel, wfi);
			}
		}
	}
	private void setColumnValues(List<Object> colsinmodel, HoldingInfo wfi, List<Integer> colsinview, List<HoldingInfo> holdings) {
		for (int i = 0; i < colsinview.size(); i++) {
			__holdingfields val = __holdingfields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			case EINDDATUM:
				colsinmodel.add(i, wfi.getEinddatumcontract());
				break;
			case INGANGSDATUM:
				colsinmodel.add(i, wfi.getStartdatumcontract());
				break;
			case PLAATS:
				colsinmodel.add(i, wfi.getVestigingsAdres().getPlaats());
				break;
			case POSTCODE:
				colsinmodel.add(i, wfi.getVestigingsAdres().getPostcode());
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		HoldingInfo holding = (HoldingInfo)data;
		model.addHolding(holding);
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		HoldingInfo holding = (HoldingInfo)data;
		model.saveHolding(holding);
	}
	public void opentarieven() {
		TariefController controller = new TariefController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		TariefList frame = new TariefList(controller);
		try {
			controller.setHolding(selectedHolding);
			controller.setWerkgever(null);
			controller.selectTarievenHolding(selectedHolding.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	public void openwerkgevers() {
		WerkgeverController controller = new WerkgeverController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		WerkgeverList frame = new WerkgeverList(controller);
		try {
			controller.selectWerkgevers(selectedHolding.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
}
