package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import java.util.Vector;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.PakketInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class PakketOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4160789227769412510L;

	private List<PakketInfo> pakketten = null;
	private Component thisform = this;
	
	/**
	 * Getters and setters of this dialog
	 */
	public PakketOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht pakketten", mdiPanel);
		initialize();
	}
	private void initialize(){
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u het Pakket wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.pakketFacade(getLoginSession()).deletePakket((PakketInfo)info);
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
		setBounds(100, 100, 476, 460);
		getContentPane().setLayout(null);
		setDetailFormClass(PakketDetail.class, PakketInfo.class);

		addColumn("Pakketnaam",null, 150);
	}
	/**
	 * Create the frame.
	 */
	void displayPakketten(){

		for(PakketInfo a :pakketten)
		{
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(a.getNaam());
			
			addRow(rowData,a);
		}
	}
	
	public void populateTable() {
		try {
			pakketten = ServiceCaller.pakketFacade(getLoginSession()).allPaketten();
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

		displayPakketten();
	}

}
