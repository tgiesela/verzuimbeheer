package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.AbstractModel;
import com.gieselaar.verzuim.models.VerzuimModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.VerzuimActiviteitDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;

public class VerzuimactiviteitenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __verzuimactiviteitfields {
		UNKNOWN(-1), DATUM(0), ACTIVITEIT(1), DEADLINE(2), USER(3);
		private int value;

		__verzuimactiviteitfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __verzuimactiviteitfields parse(int type) {
			__verzuimactiviteitfields field = UNKNOWN; // Default
			for (__verzuimactiviteitfields item : __verzuimactiviteitfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private VerzuimModel model;
	
	private List<VerzuimActiviteitInfo> verzuimactiviteiten;
	private List<ActiviteitInfo> activiteiten;
	private VerzuimInfo verzuim;
	
	public VerzuimactiviteitenController(LoginSessionRemote session, AbstractModel abstractModel) {
		super(abstractModel, null);
		this.model = (VerzuimModel) getModel();
	}

	public void selectVerzuimactiviteiten(VerzuimInfo verzuim) throws VerzuimApplicationException {
		verzuimactiviteiten = model.getVerzuimactiviteiten();
		for (VerzuimActiviteitInfo info: verzuimactiviteiten){
			info.setVerzuimId(verzuim.getId());
		}
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	private VerzuimActiviteitInfo createNewVerzuimactiviteit() {
		VerzuimActiviteitInfo info = new VerzuimActiviteitInfo();
		info.setVerzuimId(verzuim.getId());
		info.setUser(this.getMaincontroller().getGebruiker().getId());
		return info;
	}

	public void addData(InfoBase data) throws VerzuimApplicationException {
		VerzuimActiviteitInfo info = (VerzuimActiviteitInfo)data;
		try {
			info.validate();
			model.addVerzuimactiviteit(info);
			VerzuimInfo vzm = model.getVerzuim(info.getVerzuimId());
			selectVerzuimactiviteiten(vzm);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan activiteit niet geslaagd.");
		}
	}

	public void saveData(InfoBase data) throws VerzuimApplicationException {
		VerzuimActiviteitInfo info = (VerzuimActiviteitInfo)data;
		try {
			info.validate();
			model.saveVerzuimactiviteit(info);
			VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
			selectVerzuimactiviteiten(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan activiteit niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		VerzuimActiviteitInfo info = (VerzuimActiviteitInfo) data;
		AbstractDetail form = super.createDetailForm(info, VerzuimActiviteitDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		VerzuimActiviteitInfo info = createNewVerzuimactiviteit();
		AbstractDetail form = super.createDetailForm(info, VerzuimActiviteitDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		VerzuimActiviteitInfo info = (VerzuimActiviteitInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteVerzuimactiviteit(info);
		VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
		selectVerzuimactiviteiten(verzuim);
	}

	public void getTableModel(List<VerzuimActiviteitInfo> vzmactiviteiten, ColorTableModel tblmodel, List<Integer> colsinview) {
		activiteiten = this.getMaincontroller().getActiviteiten();
		this.getMaincontroller().getGebruikers();
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (VerzuimActiviteitInfo info : vzmactiviteiten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, VerzuimActiviteitInfo wfi, List<Integer> colsinview) {
		ActiviteitInfo act;
		for (int i = 0; i < colsinview.size(); i++) {
			__verzuimactiviteitfields val = __verzuimactiviteitfields.parse(colsinview.get(i));
			switch (val) {
			case ACTIVITEIT:
				act = lookupActiviteit(wfi.getActiviteitId());
				if (act != null){
					colsinmodel.add(i, act.getNaam());
				}else{
					colsinmodel.add(i, "<Onbekend>");
				}
				break;
			case DATUM:
				colsinmodel.add(i, wfi.getDatumactiviteit());
				break;
			case DEADLINE:
				if (wfi.getDatumdeadline() == null){
					colsinmodel.add(i,"");
				}else{
					colsinmodel.add(i,wfi.getDatumdeadline());
				}
				break;
			case USER:
				for (GebruikerInfo gi: getMaincontroller().getGebruikers()){
					if (gi.getId() == wfi.getUser()){
						colsinmodel.add(i,gi.getAchternaam());
						break;
					}
				}
				break;
			default:
				break;
			}
		}
	}
	private ActiviteitInfo lookupActiviteit(int activiteitId) {
		for (ActiviteitInfo ai : activiteiten){
			if (ai.getId().equals(activiteitId)) {
				return ai;
			}
		}
		return null;
	}

	private boolean confirmDelete(VerzuimActiviteitInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze activiteit wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		VerzuimActiviteitInfo info = (VerzuimActiviteitInfo)data;
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

	public List<VerzuimActiviteitInfo> getVerzuimactiviteitenList() {
		return verzuimactiviteiten;
	}
	
}
