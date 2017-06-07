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
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class UitkeringsinstantieOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<UitvoeringsinstituutInfo> uitkeringsinstanties = null;
	private Component thisform = this;
	/**
	 * Create the frame.
	 */
	public UitkeringsinstantieOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht uitvoeringsinstanties", mdiPanel);
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
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de uitvoeringsinstantie wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.instantieFacade(getLoginSession()).deleteUitkeringsinstantie((UitvoeringsinstituutInfo)info);
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
		getScrollPane().setBounds(36, 40, 370, 293);
		setBounds(100, 100, 450, 399);
		getContentPane().setLayout(null);
		setDetailFormClass(UitkeringsinstantieDetail.class, UitvoeringsinstituutInfo.class);
		
		addColumn("Naam",null,150);
		addColumn("Plaats",null, 200);

	}
	void displayUitkeringsinstanties(){

		if (uitkeringsinstanties != null)
			for(UitvoeringsinstituutInfo a :uitkeringsinstanties)
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(a.getNaam());
				rowData.add(a.getVestigingsadres().getPlaats());
				
				addRow(rowData,a);
			}
	}
	
	protected void populateTable() {
		try {
			uitkeringsinstanties = ServiceCaller.instantieFacade(getLoginSession()).allUitkeringsinstanties();
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
		displayUitkeringsinstanties();
	}

}
