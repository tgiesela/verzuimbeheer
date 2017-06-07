package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CascodeOverzicht extends BaseListForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<CascodeInfo> cascodes = null;
	private Component thisform = this;
	private CascodeGroepInfo cascodegroep = null;
	private JComboBox<TypeEntry> cmbCascodeGroep;
	private DefaultComboBoxModel<TypeEntry> cascodegroepModel;
	private List<CascodeGroepInfo> cascodegroepen;
	private boolean initialized = false;
	
	public CascodeOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht cascodes", mdiPanel);
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
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de CAS-code wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.cascodeFacade(getLoginSession()).deleteCascode((CascodeInfo)info);
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
		getScrollPane().setBounds(36, 83, 391, 314);
		setBounds(100, 100, 474, 488);
		getContentPane().setLayout(null);
		
		cmbCascodeGroep = new JComboBox<TypeEntry>();
		cmbCascodeGroep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbCascodeGroepClicked(e);
			}
		});
		cmbCascodeGroep.setBounds(36, 46, 251, 20);
		getContentPane().add(cmbCascodeGroep);
		setDetailFormClass(CascodeDetail.class, CascodeInfo.class);
		
		addColumn("Naam",null,150);
		addColumn("Omschrijving",null,150);
		
		cascodegroepModel = new DefaultComboBoxModel<TypeEntry>();
		cmbCascodeGroep.setModel(cascodegroepModel);
	}
	protected void cmbCascodeGroepClicked(ActionEvent e) {
		TypeEntry cascodegroeptype;
		if (initialized){
			cascodegroeptype = (TypeEntry) cmbCascodeGroep.getSelectedItem();
			if (cascodegroeptype.getValue() == -1)
				this.setParentInfo(null);
			else
			{
				for (CascodeGroepInfo cgi: cascodegroepen)
				{
					if (cgi.getId() == cascodegroeptype.getValue())
					{
						cascodegroep = cgi;
						this.setParentInfo(cgi);
						break;
					}
				}
			}
			this.ReloadTable();
		}
	}
	void displayCascodes(){

		for(CascodeInfo a :cascodes)
		{
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(a.getCascode());
			rowData.add(a.getOmschrijving());
			
			addRow(rowData,a);
		}
	}
	
	public void populateTable() {
		TypeEntry cascodegroeptype;

		try {
			cascodegroepen = ServiceCaller.cascodeFacade(getLoginSession()).allCascodeGroepen();
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
			return;
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
		for (CascodeGroepInfo cg: cascodegroepen)
		{
			TypeEntry cc = new TypeEntry(cg.getId(), cg.getNaam());
			cascodegroepModel.addElement(cc);
		}
		cascodegroepModel.addElement(new TypeEntry(-1, "[Alles]"));
		initialized = true;
		this.deleteAllRows();
		cascodegroep = (CascodeGroepInfo)this.getParentInfo();
		try {
			if (cascodegroep == null)
				cascodes = ServiceCaller.cascodeFacade(getLoginSession()).allCascodes();
			else
			{
				cascodes = ServiceCaller.cascodeFacade(getLoginSession()).getCascodesForGroep(cascodegroep.getId());
				for (int i=0;i<cascodegroepModel.getSize();i++)
				{
					cascodegroeptype = (TypeEntry) cascodegroepModel.getElementAt(i);
					if (cascodegroeptype.getValue() == cascodegroep.getId())
					{
						cascodegroepModel.setSelectedItem(cascodegroeptype);
						break;
					}
				}
			}
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

		displayCascodes();
	}
}
