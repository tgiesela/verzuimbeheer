package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.VerzuimModel;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.models.WerknemerModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.DienstverbandDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

public class DienstverbandController extends AbstractController {

	/* 
	 * This controller does not have its own model. The departments
	 * are extracten from the werknemer.
	 * The interface looks like a normal controller. This allows us
	 * to change this in the future to a real model behind this controller
	 */

	
	private static final long serialVersionUID = 1L;
	
	public enum __dienstverbandfields {
		UNKNOWN(-1), INGANGSDATUM(1), EINDDATUM(2), PERSONEELSNUMMER(3), WERKWEEK(4), FUNCTIE(5);
		private int value;

		__dienstverbandfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __dienstverbandfields parse(int type) {
			__dienstverbandfields field = UNKNOWN; // Default
			for (__dienstverbandfields item : __dienstverbandfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerknemerModel model;
	private WerkgeverModel werkgevermodel;
	private VerzuimModel verzuimmodel;
	private WerknemerInfo selectedWerknemer;
	private List<DienstverbandInfo> dienstverbanden;

	public DienstverbandController(LoginSessionRemote session) {
		super(new WerknemerModel(session), null);
		this.model = (WerknemerModel) getModel();
		this.werkgevermodel = new WerkgeverModel(session);
		this.setVerzuimmodel(new VerzuimModel(session));
		selectedWerknemer = (WerknemerInfo)selectedrow;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		DienstverbandInfo dienstverband = (DienstverbandInfo)data;
		try {
			dienstverband.validate();
			dienstverbanden.add(dienstverband);
			for (ControllerEventListener l:views){
				l.refreshTable();
			}
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan dienstverband");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		DienstverbandInfo dienstverband = (DienstverbandInfo)data;
		try {
			dienstverband.validate();
			for (ControllerEventListener l:views){
				l.refreshTable();
			}
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Wijzigen dienstverband");
		}
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		DienstverbandInfo dienstverband = (DienstverbandInfo)data;
		AbstractDetail form = super.createDetailForm(dienstverband, DienstverbandDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		DienstverbandInfo dienstverband = new DienstverbandInfo();
		dienstverband.setWerkgeverId(selectedWerknemer.getWerkgeverid());
		dienstverband.setWerknemerId(selectedWerknemer.getId());
		AbstractDetail form = super.createDetailForm(dienstverband, DienstverbandDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		DienstverbandInfo dienstverband = (DienstverbandInfo)data;
		dienstverband.setAction(persistenceaction.DELETE);
		dienstverband.setState(persistencestate.EXISTS);
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	public void getTableModel(List<DienstverbandInfo> dienstverbanden, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (DienstverbandInfo wfi : dienstverbanden) {
			List<Object> colsinmodel = new ArrayList<>();
			setColumnValues(colsinmodel, wfi, colsinview);
			if (wfi.getAction() == persistenceaction.DELETE){
			}else{
				if (wfi.getEinddatumcontract() == null){
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
		DefaultComboBoxModel<TypeEntry> afdelingModel = new DefaultComboBoxModel<>();
		for (AfdelingInfo w: werkgever.getAfdelings())
		{
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			afdelingModel.addElement(wg);
		}
		return afdelingModel;
	}

	private void setColumnValues(List<Object> colsinmodel, DienstverbandInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__dienstverbandfields val = __dienstverbandfields.parse(colsinview.get(i));
			switch (val) {
			case PERSONEELSNUMMER:
				colsinmodel.add(i, wfi.getPersoneelsnummer());
				break;
			case FUNCTIE:
				colsinmodel.add(i, wfi.getFunctie());
				break;
			case WERKWEEK:
				colsinmodel.add(i, wfi.getWerkweek());
				break;
			case INGANGSDATUM:
				colsinmodel.add(i, wfi.getStartdatumcontract());
				break;
			case EINDDATUM:
				colsinmodel.add(i, wfi.getEinddatumcontract());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(DienstverbandInfo info) {
		DienstverbandInfo dvb = null;

		for (DienstverbandInfo d: selectedWerknemer.getDienstVerbanden()){
			if (d.getId().equals(((DienstverbandInfo)info).getId())){
				dvb = d;
			}
		}
		if (dvb == null){
			return false;
		}
		
		// Add logic to relocate verzuimen 
		int result = JOptionPane.showConfirmDialog(null, "Dienstverband wordt verwijderd. \nWeet u het zeker?", "Dienstverband verwijderen", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.NO_OPTION){
			return false;
		}
		if (selectedWerknemer.getDienstVerbanden().size() == 1){
			JOptionPane.showMessageDialog(null, "Dit is het enige dienstverband. Het kan niet worden verwijderd. ", "Dienstverband verwijderen", JOptionPane.OK_OPTION);
			return false;
		}
		
		
		DienstverbandInfo vorigdvb = selectedWerknemer.getDienstVerbanden().get(selectedWerknemer.getDienstVerbanden().size()-2);
		if (vorigdvb == null){
			JOptionPane.showMessageDialog(null, "Vorig dienstverband niet gevonden. Het kan niet worden verwijderd. ", "Dienstverband verwijderen", JOptionPane.OK_OPTION);
			return false;
		}
		if (!vorigdvb.getWerknemerId().equals(dvb.getWerknemerId())){
			JOptionPane.showMessageDialog(null, "Vorig dienstverband niet gevonden. Werknemers ongelijk. ", "Dienstverband verwijderen", JOptionPane.OK_OPTION);
			return false;
		}
		vorigdvb.setEinddatumcontract(null);
		if (dvb.getVerzuimen() != null && !dvb.getVerzuimen().isEmpty()){
			// Er zijn verzuimen aan het dienstverband gekoppeld 
			result = JOptionPane.showConfirmDialog(null, "Moeten de verzuimen aan het vorige dienstverband worden gekoppeld?", "Dienstverband verwijderen", JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.CANCEL_OPTION){
				return false;
			}
			if (result == JOptionPane.NO_OPTION){
				result = JOptionPane.showConfirmDialog(null, "De verzuimen gekoppeld aan dit dienstverand worden verwijderd.\n Weet u het zeker?", "Verzuimen verwijderen", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION){
					return false;
				}
			}else{
				// De verzuimen en de afdelingen worden omgehangen 
				try {
					for (VerzuimInfo vzm: dvb.getVerzuimen()){
						vzm.setDienstverbandId(vorigdvb.getId());
						vzm.setWerknemer(selectedWerknemer);
						vzm.setDienstverband(vorigdvb);
						verzuimmodel.saveVerzuim(vzm);
					}
					dvb.setAction(persistenceaction.DELETE);
					// Afdelingen met ingangsdatum gelijk aan of na startdatum dvb worden verwijderd 
					AfdelingHasWerknemerInfo prevafd = null;
					for (AfdelingHasWerknemerInfo afhwi:selectedWerknemer.getAfdelingen()){
						if (DateOnly.before(afhwi.getStartdatum(),dvb.getStartdatumcontract())){
							;
						}else{
							afhwi.setAction(persistenceaction.DELETE);
							if (prevafd != null){
								prevafd.setEinddatum(null);
							}
						}
						prevafd = afhwi;
					}
					 model.saveWerknemer(selectedWerknemer);
					 model.getWerknemerDetails(selectedWerknemer.getId());
				} catch (VerzuimApplicationException | ValidationException e2) {
		        	ExceptionLogger.ProcessException(e2,null);
				}
				return true;
			}
		}
		return true;
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		DienstverbandInfo wnrf;
		wnrf = (DienstverbandInfo) data;
		try {
			model.getWerknemerDetails(wnrf.getId());
						
			return confirmDelete((DienstverbandInfo)data);
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return false;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		if (selectedWerknemer.getDienstVerbanden() != null && selectedWerknemer.getActiefDienstverband() != null){
			JOptionPane.showMessageDialog(null, "Er is nog een actief dienstverband. Sluit dit af voordat een nieuwe wordt aangemaakt");
			return false;
		}else{
			if (selectedWerknemer.getWerkgeverid() != null && selectedWerknemer.getWerkgeverid() > 0){
				return true;
			}else{
				JOptionPane.showMessageDialog(null, "Selecteer eerst de werkgever");
				return false;
			}
		}
	}
	public boolean isFirstRow(){
		return (selectedrowindex <= 0);
	}
	public boolean isLastRow(){
		return (selectedrowindex == (rowcount - 1));
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

	public List<DienstverbandInfo> getDienstverbandenList() {
		return selectedWerknemer.getDienstVerbanden();
	}

	public void selectDienstverbanden() {
		/* 
		 * Simulate access to model and send completions
		 */
		dienstverbanden = selectedWerknemer.getDienstVerbanden();
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	public VerzuimModel getVerzuimmodel() {
		return verzuimmodel;
	}

	public void setVerzuimmodel(VerzuimModel verzuimmodel) {
		this.verzuimmodel = verzuimmodel;
	}

}
