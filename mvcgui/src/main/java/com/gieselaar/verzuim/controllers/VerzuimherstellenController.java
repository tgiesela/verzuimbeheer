package com.gieselaar.verzuim.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.VerzuimModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.VerzuimHerstelDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class VerzuimherstellenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __verzuimherstelfields {
		UNKNOWN(-1), DATUMHERSTEL(0), PERCENTAGEHERSTEL(1), PERCENTAGEHERSTELAT(2), OPMERKINGEN(3);
		private int value;

		__verzuimherstelfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __verzuimherstelfields parse(int type) {
			__verzuimherstelfields field = UNKNOWN; // Default
			for (__verzuimherstelfields item : __verzuimherstelfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private VerzuimModel model;
	
	private List<VerzuimHerstelInfo> verzuimherstellen;

	private VerzuimInfo verzuim;

	public VerzuimherstellenController(LoginSessionRemote session, VerzuimModel model) {
		super(model, null);
		this.model = (VerzuimModel) getModel();
	}

	public void selectVerzuimherstellen(VerzuimInfo verzuim) throws VerzuimApplicationException {
		verzuimherstellen = model.getVerzuimherstellen();
		for (VerzuimHerstelInfo info: verzuimherstellen){
			info.setVerzuim(verzuim);
		}
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	private VerzuimHerstelInfo createNewVerzuimherstel() {
		VerzuimHerstelInfo herstel = new VerzuimHerstelInfo();
		herstel.setDatumHerstel(new Date());
		herstel.setMeldingsdatum(new Date());
		herstel.setOpmerkingen("");
		herstel.setPercentageHerstel(new BigDecimal(100));
		herstel.setPercentageHerstelAT(new BigDecimal(0));
		herstel.setUser(model.getSession().getGebruiker().getId());
		herstel.setVerzuimId(verzuim.getId());
		return herstel;
	}
	private boolean confirmCleanupTodo(VerzuimHerstelInfo verzuimherstel){
		if (verzuimherstel.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
			/*
			 * Vraag of de to do's opgeschoond moeten worden.
			 */
			if (JOptionPane.showConfirmDialog(null, "Wilt U de Todo's voor dit verzuim opschonen?",
					"Opslaan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		VerzuimHerstelInfo verzuimherstel = (VerzuimHerstelInfo)data;
		try {
			verzuimherstel.validate();
			boolean cleanupTodo = confirmCleanupTodo(verzuimherstel);
			model.addVerzuimherstel(verzuimherstel,cleanupTodo);
			VerzuimInfo updatedverzuim = model.getVerzuim(verzuimherstel.getVerzuimId());
			selectVerzuimherstellen(updatedverzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Toevoegen herstel niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		VerzuimHerstelInfo verzuimherstel = (VerzuimHerstelInfo)data;
		try {
			verzuimherstel.validate();
			boolean cleanupTodo = confirmCleanupTodo(verzuimherstel);
			model.saveVerzuimherstel(verzuimherstel,cleanupTodo);
			/* Verzuim needs to be refreshed */
			VerzuimInfo verzuim = model.getVerzuim(verzuimherstel.getVerzuimId());
			selectVerzuimherstellen(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan herstel niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		VerzuimHerstelInfo info = (VerzuimHerstelInfo) data;
		AbstractDetail form = super.createDetailForm(info, VerzuimHerstelDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		VerzuimHerstelInfo info = createNewVerzuimherstel();
		AbstractDetail form = super.createDetailForm(info, VerzuimHerstelDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		VerzuimHerstelInfo info = (VerzuimHerstelInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteVerzuimherstel(info);
		VerzuimInfo verzuimupdated = model.getVerzuim(info.getVerzuimId());
		selectVerzuimherstellen(verzuimupdated);
	}

	public void getTableModel(List<VerzuimHerstelInfo> herstellen, ColorTableModel tblmodel, List<Integer> colsinview) {

		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (VerzuimHerstelInfo info : herstellen) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, VerzuimHerstelInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__verzuimherstelfields val = __verzuimherstelfields.parse(colsinview.get(i));
			switch (val) {
			case DATUMHERSTEL:
				colsinmodel.add(i, wfi.getDatumHerstel());
				break;
			case OPMERKINGEN:
				colsinmodel.add(i, wfi.getOpmerkingen());
				break;
			case PERCENTAGEHERSTEL:
				colsinmodel.add(i,wfi.getPercentageHerstel());
				break;
			case PERCENTAGEHERSTELAT:
				colsinmodel.add(i, wfi.getPercentageHerstelAT());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(VerzuimHerstelInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit herstel wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		VerzuimHerstelInfo info = (VerzuimHerstelInfo)data;
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

	public void setVerzuim(VerzuimInfo verzuim) {
		this.verzuim = verzuim;
	}

	public void setVerzuimModel(VerzuimModel verzuimmodel) {
		this.model = verzuimmodel;
	}

	public List<VerzuimHerstelInfo> getVerzuimherstelList() {
		return verzuimherstellen;
	}

}
