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
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class GebruikerOverzicht extends BaseListForm {

	private static final long serialVersionUID = 2998408587286724321L;
	private List<GebruikerInfo> gebruikers = null;
	private Component thisform = this;
	/**
	 * Getters and setters of this dialog
	 */
	public GebruikerOverzicht(JDesktopPaneTGI mdiPanel){
		super("Overzicht gebruikers", mdiPanel);
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
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de gebruiker wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.autorisatieFacade(getLoginSession()).deleteGebruiker((GebruikerInfo)info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
		        	ExceptionLogger.ProcessException(e,null);
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,null,false);
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e,null,false);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,null);
				}
				return __continue.allow;
			}
		});
		getScrollPane().setBounds(36, 40, 391, 314);
		setBounds(100, 100, 474, 437);
		getContentPane().setLayout(null);
		setDetailFormClass(GebruikerDetail.class, GebruikerInfo.class);
		
		addColumn("Id",null,100);
		addColumn("Achternaam",null, 200);
	}

	void displayGebruikers(){

		if (gebruikers != null)
			for(GebruikerInfo a :gebruikers)
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(a.getName());
				rowData.add(a.getAchternaam());
				
				addRow(rowData,a);
			}
	}
	
	public void populateTable() {
		try {
			gebruikers = ServiceCaller.autorisatieFacade(getLoginSession()).getGebruikers();
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
		displayGebruikers();
	}

}
