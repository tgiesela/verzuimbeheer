package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.RolInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class RolOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2998408587286724321L;
	private List<RolInfo> rollen = null;
	private Component thisform = this;
	
	/**
	 * Getters and setters of this dialog
	 */
	public RolOverzicht(JDesktopPaneTGI mdiPanel){
		super("Overzicht rollen", mdiPanel);
		initialize();
	}
	public void initialize(){
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de rol wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.autorisatieFacade(getLoginSession()).deleteRol((RolInfo)info);
					else
						return __continue.dontallow;
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}

		});
		getScrollPane().setBounds(36, 40, 391, 314);
		setBounds(100, 100, 474, 437);
		getContentPane().setLayout(null);
		setDetailFormClass(RolDetail.class, RolInfo.class);
		
		addColumn("Rol",null,50);
		addColumn("Omschrijving",null,150);
	}

	void displayRollen(){

		if (rollen != null)
			for(RolInfo a :rollen)
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(a.getRolid());
				rowData.add(a.getOmschrijving());
				
				addRow(rowData,a);
			}
	}
	
	public void populateTable() {
		try {
			rollen = ServiceCaller.autorisatieFacade(getLoginSession()).getRollen();
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

		displayRollen();
	}

}
