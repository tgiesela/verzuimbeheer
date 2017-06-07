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
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class BedrijfsartsenOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BedrijfsartsInfo> bedrijfsartsen = null;
	private Component thisform = this;
	private ArbodienstInfo arbodienst;
	/**
	 * Create the frame.
	 */
	public BedrijfsartsenOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht bedrijfsartsen", mdiPanel);
		initialize();
	}
	void initialize(){
		this.setEventNotifier(new DefaultListFormNotification() {
			@Override
			public void newCreated(InfoBase info){
				((BedrijfsartsInfo)info).setArbodienstId(arbodienst.getId());
			}
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de bedrijfsarts wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.instantieFacade(getLoginSession()).deleteBedrijfsarts((BedrijfsartsInfo)info);
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
		getScrollPane().setBounds(36, 40, 369, 189);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		setDetailFormClass(BedrijfsartsDetail.class, BedrijfsartsInfo.class);

		addColumn("Achternaam",null,100);
		addColumn("Voornaam",null, 80);
		addColumn("Telefoon",null, 80);
		addColumn("Email",null, 80);

	}
	void displaybedrijfsartsen(){

		if (bedrijfsartsen != null)
			for(BedrijfsartsInfo a :bedrijfsartsen)
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(a.getAchternaam());
				rowData.add(a.getVoornaam());
				rowData.add(a.getTelefoon());
				rowData.add(a.getEmail());

				addRow(rowData,a);
			}
	}

	protected void populateTable() {
		arbodienst = (ArbodienstInfo)this.getParentInfo();
		try {
			if (arbodienst == null)
				bedrijfsartsen = ServiceCaller.instantieFacade(getLoginSession()).allBedrijfsartsen();
			else
				bedrijfsartsen = ServiceCaller.instantieFacade(getLoginSession()).allBedrijfsartsenArbodienst(arbodienst.getId());
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
		displaybedrijfsartsen();
	}

}
