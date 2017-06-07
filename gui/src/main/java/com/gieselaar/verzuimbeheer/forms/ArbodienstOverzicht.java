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
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class ArbodienstOverzicht extends BaseListForm {

	private List<ArbodienstInfo> arbodiensten = null;
	private Component thisform = this;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArbodienstOverzicht (JDesktopPaneTGI mdiPanel) {
		super("Overzicht arbodiensten", mdiPanel);
		initialize();
	}
	void initialize(){
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de arbodienst wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.instantieFacade(getLoginSession()).deleteArbodienst((ArbodienstInfo)info);
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
		setDetailFormClass(ArbodienstDetail.class, ArbodienstInfo.class);
		
		addColumn("Naam",null, 200);
		addColumn("Plaats",null, 100);
	}
	void displayArbodiensten(){

		if (arbodiensten != null)
			for(ArbodienstInfo a :arbodiensten)
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(a.getNaam());
				rowData.add(a.getVestigingsAdres().getPlaats());
				
				addRow(rowData,a);
			}
	}
	
	protected void populateTable() {
		try {
			arbodiensten = ServiceCaller.instantieFacade(getLoginSession()).allArbodiensten();
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
		displayArbodiensten();
	}

}
