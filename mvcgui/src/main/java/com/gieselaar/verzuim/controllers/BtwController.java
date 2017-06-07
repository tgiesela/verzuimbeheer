package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuurList;
import com.gieselaar.verzuim.views.BtwDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class BtwController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __btwfields {
		UNKNOWN(-1), INGANGSDATUM(0), EINDDATUM(1), PERCENTAGE(2), SOORT(3);
		private int value;

		__btwfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __btwfields parse(int type) {
			__btwfields field = UNKNOWN; // Default
			for (__btwfields item : __btwfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __btwcommands {
		UNKNOWN(-1), BTWAFGESLOTENTONEN(2);
		private int value;

		__btwcommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __btwcommands parse(int type) {
			__btwcommands field = null; // Default
			for (__btwcommands item : __btwcommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __btwcommands parse(String type) {
			__btwcommands field = __btwcommands.UNKNOWN; // Default
			for (__btwcommands item : __btwcommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private FactuurModel model;
	private boolean afgeslotenTonen;

	public BtwController(LoginSessionRemote session) {
		super(new FactuurModel(session), null);
		this.model = (FactuurModel) getModel();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__btwcommands.parse(e.getActionCommand())) {
		case BTWAFGESLOTENTONEN:
			afgeslotenTonen = !afgeslotenTonen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}

	public void selectBtw() throws VerzuimApplicationException {
		this.model.selectBtws();
	}

	private BtwInfo createNewBtw() {
		BtwInfo info = new BtwInfo();
		info.setId(-1);
		info.setIngangsdatum(new Date());
		info.setEinddatum(null);
		info.setBtwtariefsoort(__btwtariefsoort.HOOG);
		info.setPercentage(new BigDecimal(0));
		return info;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		BtwInfo info = (BtwInfo) data;
		try {
			info.validate();
			model.addBtw(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan Btw niet geslaagd.");
		}
	}

	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		BtwInfo info = (BtwInfo) data;
		try {
			info.validate();
			model.saveBtw(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan Btw niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase) data))
			return;
		BtwInfo info = (BtwInfo) data;
		AbstractDetail form = super.createDetailForm(info, BtwDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		BtwInfo info = createNewBtw();
		AbstractDetail form = super.createDetailForm(info, BtwDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}

	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase) data)) {
			return;
		}
		BtwInfo info = (BtwInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteBtw(info);
	}

	public void getTableModel(List<BtwInfo> facturen, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		
		for (BtwInfo info : facturen) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			if (info.getEinddatum() != null){
				if (afgeslotenTonen){
					tblmodel.addRow(colsinmodel, info, Color.GRAY);
				}
			}else{
				tblmodel.addRow(colsinmodel, info);
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, BtwInfo fti, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__btwfields val = __btwfields.parse(colsinview.get(i));
			switch (val) {
			case INGANGSDATUM:
				colsinmodel.add(i, fti.getIngangsdatum());
				break;
			case EINDDATUM:
				colsinmodel.add(i,fti.getEinddatum());
				break;
			case PERCENTAGE:
				colsinmodel.add(i,fti.getPercentage());
				break;
			case SOORT:
				colsinmodel.add(i, fti.getBtwtariefsoort().toString());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(BtwInfo info) {
		int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u de regel wilt verwijderen?", "",
				JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		BtwInfo info = (BtwInfo) data;
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

	public List<BtwInfo> getBtwList() {
		return model.getBtwList();
	}
	@Override 
	public void rowSelected(int selectedRow, Object data) {
		for (ControllerEventListener l : views) {
			if (l instanceof FactuurList){
				((FactuurList)l).setRowSelected();
			}
		}
	}
}
