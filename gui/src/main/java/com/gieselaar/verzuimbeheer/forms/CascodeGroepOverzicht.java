package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CascodeGroepOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<CascodeGroepInfo> cascodegroepen = null;
	private CascodeGroepInfo cascodegroep = null;
	private Component thisform = this;
	private JButton btnCascodes;
	
	public CascodeGroepOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht cascodegroepen", mdiPanel);
		//getPanelAction().setBounds(0, 377, 271, 30);
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
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de CAS-code groep wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.cascodeFacade(getLoginSession()).deleteCascodeGroep((CascodeGroepInfo)info);
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
			@Override
			public void rowSelected(InfoBase info) {				
				cascodegroep = (CascodeGroepInfo)info;
				enableButtons(true);
			}
		});
		getScrollPane().setBounds(36, 40, 391, 314);
		setBounds(100, 100, 473, 462);
		getContentPane().setLayout(null);
		
		btnCascodes = new JButton("Cascodes...");
		btnCascodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCascodesClicked(e);
			}
		});
		btnCascodes.setBounds(36, 365, 103, 23);
		getContentPane().add(btnCascodes);
		setDetailFormClass(CascodeGroepDetail.class, CascodeGroepInfo.class);
		
		addColumn("Naam",null,150);
		addColumn("Omschrijving",null,150);
		
		enableButtons(false);
	}
	protected void btnCascodesClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		CascodeOverzicht dlgCascodes = new CascodeOverzicht(this.getMdiPanel());
		dlgCascodes.setLoginSession(this.getLoginSession());
		dlgCascodes.setParentInfo(cascodegroep);
		dlgCascodes.ReloadTable();
		dlgCascodes.setVisible(true);
		mdiPanel.add(dlgCascodes);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgCascodes);
	}
	private void enableButtons(boolean rowSelected){
		if (rowSelected){
			btnCascodes.setEnabled(true);
		}
		else {
			btnCascodes.setEnabled(false);
		}
	}
	
	void displayCascodeGroepen(){

		for(CascodeGroepInfo a :cascodegroepen)
		{
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(a.getNaam());
			rowData.add(a.getOmschrijving());
			
			addRow(rowData,a);
		}
	}
	
	public void populateTable() {
		try {
			cascodegroepen = ServiceCaller.cascodeFacade(getLoginSession()).allCascodeGroepen();
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

		displayCascodeGroepen();
	}
}
