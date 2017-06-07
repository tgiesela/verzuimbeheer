package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.RolInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.SelectionRowFilter;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class RolDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3503855501976700503L;
	private LoginSessionRemote loginSession;
	private RolInfo rol = null;
	private JTextFieldTGI txtRolNaam;
	private JTextFieldTGI txtRolId;
	private List<ApplicatieFunctieInfo> applfuncAvailable;
	private List<ApplicatieFunctieInfo> applfuncSelected;
	private DefaultTableModel tblModelSelected;
	private DefaultTableModel tblModelAvailable;
	private List<__applicatiefunctie> availableValues;
	private JTable tblAvailable;
	private JTable tblSelected;
	private SelectionRowFilter rowFilter;
	/**
	 * Getters and setters of this dialog
	 */
	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public void setInfo(InfoBase info){
		int rownr;
		int id;
		rol = (RolInfo)info;
		
		txtRolNaam.setText(rol.getOmschrijving());
		txtRolId.setText(rol.getRolid());
		applfuncSelected = rol.getApplicatiefuncties();

		try {
			applfuncAvailable = ServiceCaller.autorisatieFacade(loginSession).getAppFuncties();
		} catch (PermissionException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		} catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		
		
		availableValues = new ArrayList<>();
		for (__applicatiefunctie af: __applicatiefunctie.values()){
			availableValues.add(af);
		}
		rowFilter.setAvailableRows(availableValues);
//		rowFilter.setAvailableRows(applfuncAvailable);
		
		tblModelAvailable = (DefaultTableModel) tblAvailable.getModel();
		tblModelAvailable.setRowCount(0);

		tblModelSelected = (DefaultTableModel) tblSelected.getModel();
		tblModelSelected.setRowCount(0);
		
		/* Vul beide tabellen met alle mogelijke waarden */
		for (__applicatiefunctie af: availableValues){
			Vector<Object> rowData = new Vector<>();
			rowData.add(af.toString());
			tblModelAvailable.addRow(rowData);
			tblModelSelected.addRow(rowData);
		}
		/* Verberg de rijen die al in de database voorkomen */
		if (applfuncSelected != null)
		{
			for(ApplicatieFunctieInfo a :applfuncSelected)
			{
				id = Integer.parseInt(a.getFunctieId());
				rownr = 0;
				for (__applicatiefunctie af: availableValues)
				{
					if (af.getValue() == id)
					{
						rowFilter.hideRow(tblAvailable,tblAvailable.convertRowIndexToView(rownr));
						break;
					}
					rownr++;
				}
			}
		}
		else
			applfuncSelected = new ArrayList<ApplicatieFunctieInfo>();
		activateListener();
	}
		
	/**
	 * Create the frame.
	 */
	public RolDetail(JDesktopPaneTGI mdiPanel){
		super("Samenstellen rol", mdiPanel);
		initialize();
	}
	protected void btnDeleteAllClicked(ActionEvent e) {
		rowFilter.hideAllRows(tblSelected);
	}
	protected void btnDeleteSelectedClicked(ActionEvent e) {
		rowFilter.hideSelectedRows(tblSelected);
	}
	protected void btnCopyAllClicked(ActionEvent e) {
		rowFilter.hideAllRows(tblAvailable);
	}
	protected void btnCopySelectedClicked(ActionEvent e) {
		rowFilter.hideSelectedRows(tblAvailable);
	}
	@Override
	protected void okButtonClicked(ActionEvent e) {	
		/* We moeten bepalen welke rijen geselecteerd zijn
		 * en deze toevoegen of verwijderen uit de lijst van
		 * eerder geselecteerde rijen
		 */
		int rowcount;
		int rowinlist;
		boolean found;
		__applicatiefunctie af;
		ApplicatieFunctieInfo act;
		
		rol.setOmschrijving(txtRolNaam.getText());
		rol.setRolid(txtRolId.getText());
		// We maken de lijst van oorspronkelijk geselecteerde activiteiten leeg
		// en voegen wat geselecteerd was er aan toe
		rowcount = tblSelected.getRowCount();
/*
		if (rowcount > 0)
		{
			if (applfuncSelected == null)
			{
				applfuncSelected = new ArrayList<ApplicatieFunctieInfo>();
				rol.setApplicatiefuncties(applfuncSelected);
			}
			applfuncSelected.clear();
		}
		else
			applfuncSelected.clear();
*/
		
		/*
		 * 0. Als er in applfuncSelected rijen voorkomen die niet in selectedValues voorkomen
		 *    moeten ze worden verwijderd. We zetten ze allemaal op: verwijderen
		 */
		
		for (ApplicatieFunctieInfo afi: applfuncSelected){
			afi.setAction(persistenceaction.DELETE);
		}
		List<ApplicatieFunctieInfo> newList = new ArrayList<>();
		for (int i = 0; i < rowcount; i++)
		{
			rowinlist = tblSelected.convertRowIndexToModel(i);
			/*	
			 * 1. Kijk of de geselecteerde rij al voorkomt in applfuncSelected 
			 * 
			 * */
			af = availableValues.get(rowinlist);
			found = false;
			for (ApplicatieFunctieInfo afi:applfuncSelected){
				if (Integer.parseInt(afi.getFunctieId()) == af.getValue()){
					newList.add(afi);
					found = true;
					break;
				}
			}
			/*
			 * 2. Als de rij nog niet voorkomt, voeg hem dan toe
			 */
			if (!found){
				for (ApplicatieFunctieInfo apf: applfuncAvailable){
					if (apf.getFunctieId().equals(Integer.toString(af.getValue()))){
						newList.add(apf);
					}
				}
			}
		}
		rol.setApplicatiefuncties(newList);
		if (loginSession != null)
        {
        	try {
        		rol.validate();
        		ServiceCaller.autorisatieFacade(loginSession).updateRol(rol);
				this.setVisible(false);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
	void initialize(){
		setBounds(100, 100, 728, 538);
		getContentPane().setLayout(null);
		
		txtRolNaam = new JTextFieldTGI();
		txtRolNaam.setBounds(141, 51, 181, 20);
		getContentPane().add(txtRolNaam);
		txtRolNaam.setColumns(10);
		
		JLabel lblPakketnaam = new JLabel("Rolomschrijving");
		lblPakketnaam.setBounds(39, 54, 92, 14);
		getContentPane().add(lblPakketnaam);
		
		JScrollPane scrollPaneAvailable = new JScrollPane();
		scrollPaneAvailable.setBounds(38, 79, 255, 357);
		getContentPane().add(scrollPaneAvailable);

		tblAvailable = new JTable();
		tblAvailable.setShowHorizontalLines(false);
		scrollPaneAvailable.setViewportView(tblAvailable);
		tblAvailable.setModel(new DefaultTableModel(
			new Object[][] {
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
			},
			new String[] {
				"Applicatie Functie"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7784527771669969257L;
			boolean[] columnEditables = new boolean[] {
				false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});

		JScrollPane scrollPaneSelected = new JScrollPane();
		scrollPaneSelected.setBounds(378, 78, 253, 358);
		getContentPane().add(scrollPaneSelected);
		
		tblSelected = new JTable();
		tblSelected.setShowHorizontalLines(false);
		scrollPaneSelected.setViewportView(tblSelected);
		tblSelected.setModel(new DefaultTableModel(
			new Object[][] {
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
			},
			new String[] {
				"Applicatie Functie"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3725331400408723548L;
			boolean[] columnEditables = new boolean[] {
				false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		
		JButton btnCopyAll = new JButton(">>");
		btnCopyAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCopyAllClicked(e);
			}
		});
		btnCopyAll.setBounds(303, 115, 60, 23);
		getContentPane().add(btnCopyAll);
		
		JButton btnCopySelected = new JButton(">");
		btnCopySelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCopySelectedClicked(e);
			}
		});
		btnCopySelected.setBounds(304, 149, 60, 23);
		getContentPane().add(btnCopySelected);
		
		JButton btnDeleteSelected = new JButton("<");
		btnDeleteSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDeleteSelectedClicked(e);
			}
		});
		btnDeleteSelected.setBounds(304, 180, 60, 23);
		getContentPane().add(btnDeleteSelected);
		
		JButton btnDeleteAll = new JButton("<<");
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDeleteAllClicked(e);
			}
		});
		btnDeleteAll.setBounds(304, 211, 60, 23);
		getContentPane().add(btnDeleteAll);

		rowFilter = SelectionRowFilter.CreateSelectionRowFilter(tblAvailable, tblSelected, null);
		
		JLabel lblRolid = new JLabel("RolId");
		lblRolid.setBounds(39, 29, 46, 14);
		getContentPane().add(lblRolid);
		
		txtRolId = new JTextFieldTGI();
		txtRolId.setColumns(10);
		txtRolId.setBounds(141, 26, 94, 20);
		getContentPane().add(txtRolId);
	}
}
