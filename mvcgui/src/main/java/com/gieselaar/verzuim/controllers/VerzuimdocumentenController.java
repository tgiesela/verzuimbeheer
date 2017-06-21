package com.gieselaar.verzuim.controllers;

import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.AbstractModel;
import com.gieselaar.verzuim.models.VerzuimModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.VerzuimDocumentDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;

public class VerzuimdocumentenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __verzuimdocumentfields {
		UNKNOWN(-1), DOCUMENT(0), LOCATIE(1), DATUM(2);
		private int value;

		__verzuimdocumentfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __verzuimdocumentfields parse(int type) {
			__verzuimdocumentfields field = UNKNOWN; // Default
			for (__verzuimdocumentfields item : __verzuimdocumentfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private VerzuimModel model;
	
	private List<VerzuimDocumentInfo> verzuimdocumenten;

	private VerzuimInfo verzuim;
	
	public VerzuimdocumentenController(LoginSessionRemote session, AbstractModel abstractModel) {
		super(abstractModel, null);
		this.model = (VerzuimModel) getModel();
	}

	public void selectVerzuimdocumenten(VerzuimInfo verzuim) throws VerzuimApplicationException {
		verzuimdocumenten = model.getVerzuimdocumenten(verzuim);
		if (verzuimdocumenten != null){
			for (VerzuimDocumentInfo info: verzuimdocumenten){
				info.setVerzuimId(verzuim.getId());
			}
		}
		for (ControllerEventListener l:views){
			l.refreshTable();
		}
	}

	private VerzuimDocumentInfo createNewVerzuimdocument() {
		VerzuimDocumentInfo info = new VerzuimDocumentInfo();
		info.setVerzuimId(verzuim.getId());
		info.setAanmaakdatum(new Date());
		info.setAanmaakuser(this.getMaincontroller().getGebruiker().getId());
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		VerzuimDocumentInfo info = (VerzuimDocumentInfo)data;
		try {
			info.validate();
			model.addVerzuimdocument(info);
			VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
			selectVerzuimdocumenten(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan document niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		VerzuimDocumentInfo info = (VerzuimDocumentInfo)data;
		try {
			info.validate();
			model.saveVerzuimdocument(info);
			VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
			selectVerzuimdocumenten(verzuim);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan document niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		VerzuimDocumentInfo info = (VerzuimDocumentInfo) data;
		AbstractDetail form = super.createDetailForm(info, VerzuimDocumentDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		VerzuimDocumentInfo info = createNewVerzuimdocument();
		AbstractDetail form = super.createDetailForm(info, VerzuimDocumentDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		VerzuimDocumentInfo info = (VerzuimDocumentInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteVerzuimdocument(info);
		VerzuimInfo verzuim = model.getVerzuim(info.getVerzuimId());
		selectVerzuimdocumenten(verzuim);
	}

	public void getTableModel(List<VerzuimDocumentInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		if (documenten != null){
			for (VerzuimDocumentInfo info : documenten) {
				List<Object> colsinmodel = new ArrayList<>();
	
				setColumnValues(colsinmodel, info, colsinview);
				tblmodel.addRow(colsinmodel, info);
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, VerzuimDocumentInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__verzuimdocumentfields val = __verzuimdocumentfields.parse(colsinview.get(i));
			switch (val) {
			case DOCUMENT:
				colsinmodel.add(i, wfi.getDocumentnaam());
				break;
			case LOCATIE:
				colsinmodel.add(i, wfi.getPadnaam());
				break;
			case DATUM:
				colsinmodel.add(i,wfi.getAanmaakdatum());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(VerzuimDocumentInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit document wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		VerzuimDocumentInfo info = (VerzuimDocumentInfo)data;
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

	public List<VerzuimDocumentInfo> getVerzuimdocumentList() {
		return verzuimdocumenten;
	}
	@Override
	public void tableClicked(ControllerEventListener ves, JTable table, MouseEvent e) {
		super.tableClicked(ves, table, e);
		VerzuimDocumentInfo doc = (VerzuimDocumentInfo) selectedrow;
		URI uri;
		try {
			uri = new URI("File://"
					+ URLEncoder.encode(doc.getPadnaam(), "UTF-8"));
			this.open(uri);
		} catch (UnsupportedEncodingException | URISyntaxException | ValidationException exc) {
			ExceptionLogger.ProcessException(exc, null);
		}
		
	}

}
