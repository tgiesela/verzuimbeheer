package com.gieselaar.verzuimbeheer.forms;

import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class DocumentTemplateOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<DocumentTemplateInfo> templates;
	public DocumentTemplateOverzicht (JDesktopPaneTGI mdiPanel){
		super("Overzicht document templates", mdiPanel);
		initialize();
	}
	public void initialize(){
		this.setEventNotifier(new DefaultListFormNotification(){
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u de template wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.verzuimFacade(getLoginSession()).deleteDocumentTemplate((DocumentTemplateInfo)info);
					else 
						return __continue.dontallow;
				} catch (PermissionException e) {
		        	ExceptionLogger.ProcessException(e,null);
				} catch (ValidationException e) {
		        	ExceptionLogger.ProcessException(e,null,false);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,null);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,null);
				}
				return __continue.allow;
			}
		});
		getScrollPane().setBounds(36, 40, 391, 314);
		setBounds(100, 100, 474, 437);
		getContentPane().setLayout(null);
		setDetailFormClass(DocumentTemplateDetail.class, DocumentTemplateInfo.class);
		
		addColumn("Naam",null,50);
		addColumn("Padnaam",null,150);
	}
	protected void populateTable() {
		try {
			templates = ServiceCaller.verzuimFacade(getLoginSession()).getDocumentTemplates();
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		displayDocumentTemplates();
	}
	private void displayDocumentTemplates() {
		for (DocumentTemplateInfo template: templates)
		{
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(template.getNaam());
			rowData.add(template.getPadnaam());
			addRow(rowData,template);
		}
	}

}
