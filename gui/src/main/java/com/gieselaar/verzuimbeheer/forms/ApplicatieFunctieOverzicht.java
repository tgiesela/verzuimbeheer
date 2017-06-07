package com.gieselaar.verzuimbeheer.forms;

import java.util.Vector;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class ApplicatieFunctieOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 */
	public ApplicatieFunctieOverzicht(JDesktopPaneTGI mdiPanel){
		super("Overzicht Applicatiefuncties", mdiPanel);
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
					int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u de functie wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.autorisatieFacade(getLoginSession()).deleteAppFunctie((ApplicatieFunctieInfo)info);
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
		setDetailFormClass(ApplicatieFunctieDetail.class, ApplicatieFunctieInfo.class);
		
		addColumn("Functie Id",null,50);
		addColumn("Omschrijving",null,150);
	}
	void displayApplicatieFuncties(){

		for (__applicatiefunctie appfunc: __applicatiefunctie.values())
		{
			Vector<Object> rowData = new Vector<Object>();
			ApplicatieFunctieInfo ai = new ApplicatieFunctieInfo();
			ai.setFunctieId(String.format("%d", appfunc.getValue()));
			ai.setFunctieomschrijving(appfunc.toString());
			rowData.add(ai.getFunctieId());
			rowData.add(ai.getFunctieomschrijving());
			addRow(rowData,ai);
		}
	}
	public void populateTable() {
		displayApplicatieFuncties();
	}
}
