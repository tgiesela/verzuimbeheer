package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.VerzuimModel;
import com.gieselaar.verzuim.models.WerknemerModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.VerzuimDetail;
import com.gieselaar.verzuim.views.VerzuimMedischeKaartList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;

public class VerzuimController extends AbstractController {

	/*
	 * Deze controller werkt voor de verzuimen van 1 werknemer. Daarom moet de
	 * werknemerid worden gezet na creatie van de controller.
	 */
	private static final long serialVersionUID = 1L;
	private List<CascodeInfo> cascodes;

	public enum __verzuimfields {
		UNKNOWN(-1), TYPE(0), AANVANG(1), VANGNET(2), HERSTEL(3), HERSTELPERCENTAGE(4), KETENVZM(5), CASCODE(6);
		private int value;

		__verzuimfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __verzuimfields parse(int type) {
			__verzuimfields field = UNKNOWN; // Default
			for (__verzuimfields item : __verzuimfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private VerzuimModel model;
	private WerknemerModel werknemermodel;
	private WerknemerInfo werknemer;
	private VerzuimInfo selectedVerzuim;
	private DienstverbandInfo dienstverband;
	/*
	 * Other controllers
	 */

	public VerzuimController(LoginSessionRemote session) {
		super(new VerzuimModel(session), null);
		this.model = (VerzuimModel) getModel();
		this.werknemermodel = new WerknemerModel(this.model.getSession());
		selectedVerzuim = (VerzuimInfo)selectedrow;
	}

	public VerzuimInfo getVerzuim(int verzuimid) {
		try {
			return model.getVerzuim(verzuimid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
			return null;
		}
	}

	public void selectVerzuimen(Integer dienstverbandid) {
		try {
			model.getVerzuimen(dienstverbandid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public List<VerzuimInfo> getVerzuimen() {
		return model.getVerzuimen();
	}

	public void addData(InfoBase data) throws VerzuimApplicationException {
		VerzuimInfo verzuim = (VerzuimInfo)data;
		try {
			verzuim.validate();
			additionalValidations(verzuim, true);
			model.addVerzuim(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan verzuim niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		VerzuimInfo verzuim = (VerzuimInfo)data;
		verzuim.setVersion(selectedVerzuim.getVersion());
		verzuim.setEinddatumverzuim(selectedVerzuim.getEinddatumverzuim());
		try {
			verzuim.validate();
			additionalValidations(verzuim, false);
			model.saveVerzuim(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan verzuim niet geslaagd.");
		}
	}

	private void additionalValidations(VerzuimInfo verzuim, boolean isnew) throws VerzuimApplicationException {
		List<VerzuimInfo> verzuimen = model.getVerzuimen();
		try {
			if (!verzuim.getKetenverzuim() && (verzuim.checkIsKetenverzuim(verzuim, verzuimen))) {
				JOptionPane.showMessageDialog(null, "Let op: dit is een ketenverzuim. De Todo's worden berekend\r\n"
						+ "vanaf de verzuimdatum minus het aantal ketenverzuimdagen.");
			}
			if (isnew && verzuim.isFrequentverzuim(verzuim, verzuimen)) {
				JOptionPane.showMessageDialog(null, "Let op: Frequent verzuim.");
			}
			verzuim.checkOverlap(verzuim, verzuimen);

		} catch (ValidationException e2) {
			throw new VerzuimApplicationException(e2, e2.getMessage());
		}
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException{
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		model.deleteVerzuim((VerzuimInfo)data);
	}

	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed()){
			return;
		}
		VerzuimInfo verzuim = new VerzuimInfo();
		initializeVerzuim(verzuim);
		selectedrow = verzuim;
		selectedVerzuim = verzuim;
		this.getVerzuim(-1);
		AbstractDetail form = super.createDetailForm(verzuim, VerzuimDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data)){
			return;
		}
		VerzuimInfo vi = (VerzuimInfo) data;
		VerzuimInfo videtails = this.getVerzuim(vi.getId());
		AbstractDetail form = super.createDetailForm(videtails, VerzuimDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void getTableModel(List<VerzuimInfo> verzuimen, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		cascodes = this.getMaincontroller().getCascodes();
		tblmodel.getDataVector().removeAllElements();
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (VerzuimInfo vi : verzuimen) {
			List<Object> colsinmodel = new ArrayList<>();
			setColumnValues(colsinmodel, vi, colsinview);
			if (vi.getEinddatumverzuim() == null)
				if (vi.getVerzuimtype() == __verzuimtype.VERZUIM)
					tblmodel.addRow(colsinmodel, vi, Color.ORANGE);
				else
					tblmodel.addRow(colsinmodel, vi, Color.GREEN);
			else if (vi.getVerzuimtype() == __verzuimtype.PREVENTIEF)
				tblmodel.addRow(colsinmodel, vi, Color.PINK);
			else
				tblmodel.addRow(colsinmodel, vi);
		}
	}

	private CascodeInfo lookupCascode(Integer id) {
		for (CascodeInfo c : cascodes) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}

	private void setColumnValues(List<Object> colsinmodel, VerzuimInfo vzi, List<Integer> colsinview) {
		CascodeInfo cascode;
		for (int i = 0; i < colsinview.size(); i++) {
			__verzuimfields val = __verzuimfields.parse(colsinview.get(i));
			if (val != null) {
				switch (val) {
				case AANVANG:
					colsinmodel.add(i, vzi.getStartdatumverzuim());
					break;
				case CASCODE:
					cascode = (CascodeInfo) lookupCascode(vzi.getCascode());
					if (cascode != null) {
						colsinmodel.add(i, cascode.getCascode() + "," + cascode.getOmschrijving());
					} else {
						colsinmodel.add(i, "");
					}
					break;
				case HERSTEL:
					if (vzi.getEinddatumverzuim() == null) {
						colsinmodel.add(i, null);
					} else {
						colsinmodel.add(i, vzi.getEinddatumverzuim());
					}
					break;
				case HERSTELPERCENTAGE:
					colsinmodel.add(i, getHerstelPercentage(vzi));
					break;
				case KETENVZM:
					if (vzi.getKetenverzuim())
						colsinmodel.add(i, "Ja");
					else
						colsinmodel.add(i, "N.v.t.");
					break;
				case TYPE:
					colsinmodel.add(i, vzi.getVerzuimtype().toString());
					break;
				case VANGNET:
					colsinmodel.add(i, vzi.getVangnettype().toString());
					break;
				default:
					break;
				}
			}
		}
	}

	private Object getHerstelPercentage(VerzuimInfo wfi) {
		BigDecimal percentage = BigDecimal.ZERO;
		Date datumHerstel;
		List<VerzuimHerstelInfo> herstellen = wfi.getVerzuimherstellen();
		if (herstellen != null) {
			datumHerstel = wfi.getStartdatumverzuim();
			for (VerzuimHerstelInfo h : herstellen) {
				if (h.getDatumHerstel().after(datumHerstel) || h.getDatumHerstel().equals(datumHerstel)) {
					datumHerstel = h.getDatumHerstel();
					percentage = h.getPercentageHerstel();
				}
			}
		}
		return percentage;
	}

	public void openMedischekaart(ControllerEventListener ves, __formmode mode) {

		VerzuimmedischekaartController verzuimmedischekaartcontroller = new VerzuimmedischekaartController(model.getSession(), this.model);
		
		VerzuimInfo videtails = this.getVerzuim(selectedVerzuim.getId());
		
		verzuimmedischekaartcontroller.setVerzuim(videtails);
		verzuimmedischekaartcontroller.setDesktoppane(getDesktoppane());
		verzuimmedischekaartcontroller.setMaincontroller(this.getMaincontroller());
		VerzuimMedischeKaartList form = new VerzuimMedischeKaartList(verzuimmedischekaartcontroller );
		ControllerEventListener listener = form;
		listener.setDetailFormmode(mode);
		this.getDesktoppane().add(form);
		this.getDesktoppane().moveToFront(form);
		form.setVisible(true);
		if (videtails.getVerzuimmedischekaarten() == null){
			videtails.setVerzuimmedischekaarten(new ArrayList<VerzuimMedischekaartInfo>());
		}
		listener.setData((InfoBase)videtails);
	}

	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}

	public void initializeVerzuim(VerzuimInfo verzuim) {
		verzuim.setStartdatumverzuim(new Date());
		verzuim.setEinddatumverzuim(null);
		verzuim.setMeldingsdatum(new Date());
		verzuim.setKetenverzuim(false);
		verzuim.setGerelateerdheid(__gerelateerdheid.NVT);
		verzuim.setVangnettype(__vangnettype.NVT);
		verzuim.setVerzuimtype(__verzuimtype.VERZUIM);
		verzuim.setGebruiker(this.getGebruiker().getId());
		verzuim.setDienstverbandId(dienstverband.getId());
		verzuim.setDienstverband(dienstverband);
		verzuim.setWerknemer(werknemer);
		List<WiapercentageInfo> wiapercentages = werknemer.getWiaPercentages();
		if (wiapercentages != null) {
			for (WiapercentageInfo wp : wiapercentages) {
				if (wp.getEinddatum() == null && wp.getCodeWiaPercentage() != __wiapercentage.NVT) {
					verzuim.setVangnettype(__vangnettype.WIA);
				}
			}
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		try {
			int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u het verzuim wilt verwijderen?",
					"", JOptionPane.YES_NO_OPTION);
			if (rslt == JOptionPane.YES_OPTION) {
				VerzuimInfo vzm = model.getVerzuim(((VerzuimInfo) data).getId());
				if (vzm.getVerzuimmedischekaarten() != null && !vzm.getVerzuimmedischekaarten().isEmpty()) {
					rslt = JOptionPane.showConfirmDialog(null,
							"De medische kaart is niet leeg.\n Weet u zeker dat u het verzuim wilt verwijderen?",
							"", JOptionPane.YES_NO_OPTION);
					if (rslt != JOptionPane.YES_OPTION) {
						return false;
					}
				}
			} else
				return false;
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return true;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		/*
		 * Dienstverband may have been refreshed (verzuimen) due
		 * to changes on the HerstelForm.
		 */
		for (DienstverbandInfo dvb: werknemer.getDienstVerbanden()){
			if (dvb.getId().equals(dienstverband.getId())){
				dienstverband = dvb;
				break;
			}
		}
		
		
		if (werknemer.hasOpenVerzuim(dienstverband)) {
			JOptionPane.showMessageDialog(null,
					"Nieuw verzuim aanmaken niet toegestaan.\r\nEr is nog een open verzuim");
			return false;
		}
		if (werknemer.getId() == null || werknemer.getId() <= 0){
			JOptionPane.showMessageDialog(null,
					"Nieuw verzuim aanmaken niet toegestaan.\r\nSla eerst de gegevens op");
		}
		return true;
	}

	public VerzuimModel getVerzuimmodel() {
		return this.model;
	}

	public TodoController getTodoController()throws VerzuimApplicationException{
		TodoController todocontroller;
		todocontroller = new TodoController(this.model.getSession()){
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		todocontroller.setDesktoppane(getDesktoppane());
		todocontroller.setMaincontroller(this.getMaincontroller());
		todocontroller.setVerzuim(null);
		return todocontroller;
	}

	public VerzuimherstellenController getVerzuimherstellenController() throws VerzuimApplicationException {
		VerzuimherstellenController verzuimherstelcontroller;
		verzuimherstelcontroller = new VerzuimherstellenController(this.model.getSession(),this.model){
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
			@Override
			public void closeView(ControllerEventListener listener) {
				handleHerstelformClosed();
				
			}
		};
		verzuimherstelcontroller.setDesktoppane(getDesktoppane());
		verzuimherstelcontroller.setMaincontroller(this.getMaincontroller());
		verzuimherstelcontroller.setVerzuimModel(this.model);
		selectedVerzuim = (VerzuimInfo)selectedrow;
		verzuimherstelcontroller.setVerzuim(selectedVerzuim);
		return verzuimherstelcontroller;
	}

	protected void handleHerstelformClosed() {
		try {
			werknemer = werknemermodel.getWerknemerDetails(werknemer.getId());
			selectedVerzuim = model.getVerzuim(selectedVerzuim.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		for (ControllerEventListener l: views){
			if (l instanceof VerzuimDetail){
				((VerzuimDetail)l).setData(selectedVerzuim);
			}
		}
	}

	public VerzuimdocumentenController getVerzuimdocumentenController() throws VerzuimApplicationException {
		VerzuimdocumentenController verzuimdocumentcontroller;
		verzuimdocumentcontroller = new VerzuimdocumentenController(this.model.getSession(),this.model){
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		verzuimdocumentcontroller.setDesktoppane(getDesktoppane());
		verzuimdocumentcontroller.setMaincontroller(this.getMaincontroller());
		verzuimdocumentcontroller.setVerzuimModel(this.model);
		selectedVerzuim = (VerzuimInfo)selectedrow;
		verzuimdocumentcontroller.setVerzuim(selectedVerzuim);
		return verzuimdocumentcontroller;
	}

	public VerzuimactiviteitenController getVerzuimactiviteitenController() throws VerzuimApplicationException {
		VerzuimactiviteitenController verzuimactiviteitcontroller;
		verzuimactiviteitcontroller = new VerzuimactiviteitenController(this.model.getSession(),this.model){
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		verzuimactiviteitcontroller.setDesktoppane(getDesktoppane());
		verzuimactiviteitcontroller.setMaincontroller(this.getMaincontroller());
		verzuimactiviteitcontroller.setVerzuimModel(this.model);
		selectedVerzuim = (VerzuimInfo)selectedrow;
		verzuimactiviteitcontroller.setVerzuim(selectedVerzuim);
		return verzuimactiviteitcontroller;
	}
	public DienstverbandInfo getDienstverband() {
		return dienstverband;
	}
	public void setDienstverband(DienstverbandInfo dienstverband) {
		this.dienstverband = dienstverband;
	}

	public void fileDropped(File droppedfile) {
		VerzuimDocumentInfo newDoc = new VerzuimDocumentInfo();
		VerzuimdocumentenController controller = new VerzuimdocumentenController(
				model.getSession(), model);
		controller.setDesktoppane(getDesktoppane());
		newDoc.setAanmaakdatum(new Date());
		newDoc.setAanmaakuser(getMaincontroller().getGebruiker().getId());
		newDoc.setDocumentnaam(droppedfile.getName());
		newDoc.setPadnaam(droppedfile.getAbsolutePath());
		newDoc.setVerzuimId(selectedVerzuim.getId());

		controller.showRow(null, newDoc);
		
	}

	public void setSelectedverzuim(VerzuimInfo selectedVerzuim) {
		this.selectedVerzuim = selectedVerzuim;
	}
	@Override
	public void rowAdded(Object data) {
		super.rowAdded(data);
		/*
		 * data contains the newly added verzuim, so now we 
		 * know the verzuimid.
		 */
		try {
			VerzuimInfo verzuim = model.getVerzuim(((VerzuimInfo)data).getId());
			this.getDetailform().setData(verzuim);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, null);
		}
	}

}
