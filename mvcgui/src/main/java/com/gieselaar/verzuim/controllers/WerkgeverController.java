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
import com.gieselaar.verzuim.views.AfdelingList;
import com.gieselaar.verzuim.views.TariefList;
import com.gieselaar.verzuim.views.WerkgeverDetail;
import com.gieselaar.verzuim.views.WerkgeverList;
import com.gieselaar.verzuim.views.WerkgeverPakketten;
import com.gieselaar.verzuim.views.WerknemerList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class WerkgeverController extends AbstractController {
	
	private static final long serialVersionUID = 1L;
	public enum __werkgeverfields {
		UNKNOWN(-1), NAAM(0), HOLDINGNAAM(1), POSTCODE(2), PLAATS(3), INGANGSDATUM(4), EINDDATUM(5);
		private int value;

		__werkgeverfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __werkgeverfields parse(int type) {
			__werkgeverfields field = UNKNOWN; // Default
			for (__werkgeverfields item : __werkgeverfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __werkgevercommands {
		UNKNOWN(-1), WERKNEMERAFGESLOTENTONEN(1), WERKNEMERTONEN(2);
		private int value;

		__werkgevercommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __werkgevercommands parse(int type) {
			__werkgevercommands field = null; // Default
			for (__werkgevercommands item : __werkgevercommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __werkgevercommands parse(String type) {
			__werkgevercommands field = __werkgevercommands.UNKNOWN; // Default
			for (__werkgevercommands item : __werkgevercommands.values()) {
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
	private WerkgeverInfo selectedWerkgever;
	private WerknemerController werknemercontroller;
	private AfdelingController afdelingcontroller;
	public WerkgeverController(LoginSessionRemote session){
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel) getModel();
		selectedWerkgever = (WerkgeverInfo)selectedrow;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__werkgevercommands.parse(e.getActionCommand())) {
		case WERKNEMERAFGESLOTENTONEN:
			afgeslotentonen = !afgeslotentonen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		case WERKNEMERTONEN:
			WerknemerController controller = new WerknemerController(this.getModel().getSession());
			controller.setDesktoppane(getDesktoppane());
			controller.setMaincontroller(this.getMaincontroller());
			WerknemerList frame = new WerknemerList(controller);
			controller.selectWerknemers(selectedWerkgever.getId());
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
		selectedWerkgever = (WerkgeverInfo)data;
		for (ControllerEventListener l : views) {
			if (l instanceof WerkgeverList){
				((WerkgeverList)l).setRowSelected();
			}
		}
	}
	public void getWerkgevers(){
		try {
			model.selectWerkgevers();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public void getWerkgevers(Integer holdingid){
		try {
			model.selectWerkgevers(holdingid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public WerkgeverInfo getWerkgeverDetails(int werkgeverid){
		try {
			return model.getWerkgeverDetails(werkgeverid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}
	public void setFilterObsolete(boolean filterobsolete) {
		model.setFilterObsolete(filterobsolete);
	}
	public List<WerkgeverInfo> getWerkgeverList() {
		return model.getWerkgeverList();
	}
	public WerknemerController getWerknemerController() throws VerzuimApplicationException {
		if (werknemercontroller == null) {
			werknemercontroller = new WerknemerController(this.model.getSession()){
				private static final long serialVersionUID = 1L;

				@Override
				public void rowSelected(int selectedRow, Object data) {
					for (ControllerEventListener l: views){
						l.rowSelected(selectedRow, data);
					}
				}
			};
			werknemercontroller.setDesktoppane(getDesktoppane());
			werknemercontroller.setMaincontroller(this.getMaincontroller());
			selectedWerkgever = (WerkgeverInfo)selectedrow;
		}
		return werknemercontroller;
	}
	public AfdelingController getAfdelingController() throws VerzuimApplicationException {
		if (afdelingcontroller == null) {
			afdelingcontroller = new AfdelingController(this.model.getSession()){
				private static final long serialVersionUID = 1L;

				@Override
				public void rowSelected(int selectedRow, Object data) {
					for (ControllerEventListener l: views){
						l.rowSelected(selectedRow, data);
					}
				}
			};
			afdelingcontroller.setDesktoppane(getDesktoppane());
			afdelingcontroller.setMaincontroller(this.getMaincontroller());
			afdelingcontroller.setWerkgever(selectedWerkgever.getId());
		}
		return afdelingcontroller;
	}
	
	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,
						"Weet u zeker dat u de werkgever wilt verwijderen?\n"
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
		WerkgeverInfo wfi = (WerkgeverInfo) data;
		try {
			selectedWerkgever = model.getWerkgeverDetails(wfi.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		AbstractDetail form = super.createDetailForm(selectedWerkgever, WerkgeverDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		WerkgeverInfo werkgever = createNewWerkgever();
		AbstractDetail form = super.createDetailForm(werkgever, WerkgeverDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		WerkgeverInfo info = (WerkgeverInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteWerkgever(info);
	}
	
	private WerkgeverInfo createNewWerkgever() {
		WerkgeverInfo werkgever = new WerkgeverInfo();
		werkgever.setStartdatumcontract(new Date());
		return null;
	}
	public void getTableModel(List<WerkgeverInfo> werkgevers, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		List<HoldingInfo> holdings = this.getMaincontroller().getHoldings();
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (WerkgeverInfo wfi : werkgevers) {
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
	private void setColumnValues(List<Object> colsinmodel, WerkgeverInfo wfi, List<Integer> colsinview, List<HoldingInfo> holdings) {
		for (int i = 0; i < colsinview.size(); i++) {
			__werkgeverfields val = __werkgeverfields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			case HOLDINGNAAM:
				colsinmodel.add(i,getHoldingname(wfi.getHoldingId(), holdings));
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
	private Object getHoldingname(Integer holdingId, List<HoldingInfo> holdings) {
		if (holdingId == null){
			return "";
		}else{
			for (HoldingInfo h: holdings){
				if (holdingId.equals(h.getId())){
					return h.getNaam();
				}
			}
			return "";
		}
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		WerkgeverInfo werkgever = (WerkgeverInfo)data;
		model.addWerkgever(werkgever);
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		WerkgeverInfo werkgever = (WerkgeverInfo)data;
		model.saveWerkgever(werkgever);
	}
	public void opentarieven() {
		TariefController controller = new TariefController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		TariefList frame = new TariefList(controller);
		try {
			controller.setWerkgever(selectedWerkgever);
			controller.setHolding(null);
			controller.selectTarievenWerkgever(selectedWerkgever.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	public void openwerknemers() {
		WerknemerController controller = new WerknemerController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		WerknemerList frame = new WerknemerList(controller);
		controller.selectWerknemers(selectedWerkgever.getId());
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	public void openafdelingen() {
		AfdelingController controller = new AfdelingController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		AfdelingList frame = new AfdelingList(controller);
		controller.setWerkgever(selectedWerkgever.getId());
		controller.selectAfdelingen();
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	public void openpakketten() {
		WerkgeverPakkettenController controller = new WerkgeverPakkettenController(this.getModel().getSession(),model);
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		controller.setSelectedWerkgever(selectedWerkgever);
		WerkgeverPakketten frame = new WerkgeverPakketten(controller);
		frame.setVisible(true);
		frame.setData(selectedWerkgever);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	public void selectWerkgevers(Integer id) throws VerzuimApplicationException {
		model.selectWerkgevers(id);
	}
}
