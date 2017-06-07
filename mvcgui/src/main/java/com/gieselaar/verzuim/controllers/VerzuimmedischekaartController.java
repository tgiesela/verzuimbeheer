package com.gieselaar.verzuim.controllers;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.AbstractModel;
import com.gieselaar.verzuim.models.VerzuimModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.VerzuimMedischeKaartDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;

public class VerzuimmedischekaartController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __verzuimmedischekaartfields {
		UNKNOWN(-1), DATUM(0), USER(1), OPENBAAR(2);
		private int value;

		__verzuimmedischekaartfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __verzuimmedischekaartfields parse(int type) {
			__verzuimmedischekaartfields field = UNKNOWN; // Default
			for (__verzuimmedischekaartfields item : __verzuimmedischekaartfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private VerzuimModel model;

	private List<VerzuimMedischekaartInfo> verzuimmedischekaarten;
	private VerzuimInfo verzuim;

	public VerzuimmedischekaartController(LoginSessionRemote session, AbstractModel abstractModel) {
		super(abstractModel, null);
		this.model = (VerzuimModel) getModel();
	}

	public void selectVerzuimmedischekaarten(VerzuimInfo verzuim) throws VerzuimApplicationException {
		verzuimmedischekaarten = model.getVerzuimmedischekaarten();
		for (VerzuimMedischekaartInfo info : verzuimmedischekaarten) {
			info.setVerzuimId(verzuim.getId());
		}
		for (ControllerEventListener l : views) {
			l.refreshTable();
		}
	}

	private VerzuimMedischekaartInfo createNewMedischekaart() {
		VerzuimMedischekaartInfo info = new VerzuimMedischekaartInfo();
		info.setVerzuimId(verzuim.getId());
		info.setUser(this.getGebruiker().getId());
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		VerzuimMedischekaartInfo info = (VerzuimMedischekaartInfo)data;
		try {
			info.validate();
			model.addVerzuimmedischekaart(info);
			VerzuimInfo vzm = model.getVerzuim(info.getVerzuimId());
			selectVerzuimmedischekaarten(vzm);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan medische kaart niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		VerzuimMedischekaartInfo info = (VerzuimMedischekaartInfo)data;
		try {
			info.validate();
			model.saveVerzuimmedischekaart(info);
			VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
			selectVerzuimmedischekaarten(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan medische kaart niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase) data))
			return;
		VerzuimMedischekaartInfo info = (VerzuimMedischekaartInfo) data;
		AbstractDetail form = super.createDetailForm(info, VerzuimMedischeKaartDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		VerzuimMedischekaartInfo info = createNewMedischekaart();
		AbstractDetail form = super.createDetailForm(info, VerzuimMedischeKaartDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}

	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase) data)) {
			return;
		}
		VerzuimMedischekaartInfo info = (VerzuimMedischekaartInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		try {
			model.deleteVerzuimmedischekaart(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Verwijderen medische kaart niet geslaagd");
		}
		VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
		selectVerzuimmedischekaarten(verzuim);
	}

	public void getTableModel(List<VerzuimMedischekaartInfo> vzmactiviteiten, ColorTableModel tblmodel,
			List<Integer> colsinview) {
		this.getMaincontroller().getGebruikers();
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (VerzuimMedischekaartInfo info : vzmactiviteiten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, VerzuimMedischekaartInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__verzuimmedischekaartfields val = __verzuimmedischekaartfields.parse(colsinview.get(i));
			switch (val) {
			case DATUM:
				colsinmodel.add(i, wfi.getWijzigingsdatum());
			case OPENBAAR:
				if (wfi.getOpenbaar()) {
					colsinmodel.add(i, "Ja");
				} else {
					colsinmodel.add(i, "Nee");
				}
				break;
			case USER:
				for (GebruikerInfo gi : getMaincontroller().getGebruikers()) {
					if (gi.getId() == wfi.getUser()) {
						colsinmodel.add(i, gi.getAchternaam());
						break;
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(VerzuimMedischekaartInfo wnr) {
		int rslt;
		rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u deze medische kaart wilt verwijderen?", "",
				JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		VerzuimMedischekaartInfo info = (VerzuimMedischekaartInfo) data;
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

	public List<VerzuimMedischekaartInfo> getVerzuimmedischekaartenList() {
		return verzuimmedischekaarten;
	}

	public void newFromClipboard() {
		VerzuimMedischekaartInfo medischekaart = createNewMedischekaart();

		try {
			medischekaart.setMedischekaart(
					Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString());
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			ExceptionLogger.ProcessException(e, null, "Cannot access clipboard");
			return;
		}

		this.showRow(null, medischekaart);

	}

}
