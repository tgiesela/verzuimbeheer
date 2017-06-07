package com.gieselaar.verzuim.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.DocumentTemplateModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.DocumentTemplateDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;

public class DocumenttemplateController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __documenttemplatefields {
		UNKNOWN(-1), NAAM(0),PADNAAM(1);
		private int value;

		__documenttemplatefields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __documenttemplatefields parse(int type) {
			__documenttemplatefields field = UNKNOWN; // Default
			for (__documenttemplatefields item : __documenttemplatefields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private DocumentTemplateModel model;
	
	private List<DocumentTemplateInfo> pakketten;

	public DocumenttemplateController(LoginSessionRemote session) {
		super(new DocumentTemplateModel(session), null);
		this.model = (DocumentTemplateModel)getModel();
	}

	public void selectDocumenttemplates() throws VerzuimApplicationException {
		model.selectDocumentTemplates();
	}

	private DocumentTemplateInfo createNewDocumentTemplate() {
		DocumentTemplateInfo info = new DocumentTemplateInfo();
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		DocumentTemplateInfo info = (DocumentTemplateInfo)data;
		try {
			info.validate();
			model.addDocumenttemplate(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan document template niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		DocumentTemplateInfo info = (DocumentTemplateInfo)data;
		try {
			info.validate();
			model.saveDocumenttemplate(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan document template niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		DocumentTemplateInfo info = (DocumentTemplateInfo) data;
		AbstractDetail form = super.createDetailForm(info, DocumentTemplateDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		DocumentTemplateInfo info = createNewDocumentTemplate();
		AbstractDetail form = super.createDetailForm(info, DocumentTemplateDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		DocumentTemplateInfo info = (DocumentTemplateInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteDocumenttemplate(info);
	}

	public void getTableModel(List<DocumentTemplateInfo> documenten, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (DocumentTemplateInfo info : documenten) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			tblmodel.addRow(colsinmodel, info);
		}
	}

	private void setColumnValues(List<Object> colsinmodel, DocumentTemplateInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__documenttemplatefields val = __documenttemplatefields.parse(colsinview.get(i));
			switch (val) {
			case NAAM:
				colsinmodel.add(i, wfi.getNaam());
				break;
			case PADNAAM:
				colsinmodel.add(i, wfi.getPadnaam());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(DocumentTemplateInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze documenttemplate wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		DocumentTemplateInfo info = (DocumentTemplateInfo)data;
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

	public List<DocumentTemplateInfo> getVerzuimdocumentList() {
		return pakketten;
	}

	public List<DocumentTemplateInfo> getDocumentTemplateList() {
		return model.getDocumenttemplatesList();
	}
}
