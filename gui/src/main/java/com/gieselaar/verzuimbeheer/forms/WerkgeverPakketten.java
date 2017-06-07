package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDialog;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.PakketInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.SelectionRowFilter;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;

public class WerkgeverPakketten extends BaseDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoginSessionRemote loginSession;
	private WerkgeverInfo werkgever = null;
	List<PakketInfo> pakketAvailable;
	List<PakketInfo> pakketSelected;
	DefaultTableModel tblModelSelected;
	DefaultTableModel tblModelAvailable;
	private JTable tblAvailable;
	private JTable tblSelected;
	private SelectionRowFilter rowFilter;
	/**
	 * Getters and setters of this dialog
	 */
	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public void setInfo(WerkgeverInfo info){
		int rownr;
		int id;
		werkgever = info;
		
		pakketSelected = werkgever.getPakketten();
		try {
			pakketAvailable = ServiceCaller.pakketFacade(loginSession).allPaketten();
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		rowFilter.setAvailableRows(pakketAvailable);
		
		tblModelAvailable = (DefaultTableModel) tblAvailable.getModel();
		tblModelAvailable.setRowCount(0);

		tblModelSelected = (DefaultTableModel) tblSelected.getModel();
		tblModelSelected.setRowCount(0);
		if (pakketAvailable != null)
		{
			for(PakketInfo p :pakketAvailable)
			{
				Vector<String> rowData = new Vector<String>();
				rowData.add(p.getNaam());
				tblModelAvailable.addRow(rowData);
				tblModelSelected.addRow(rowData);
			}
		}
		if (pakketSelected != null)
		{
			for(PakketInfo a :pakketSelected)
			{
				id = a.getId();
				rownr = 0;
				for (PakketInfo b: pakketAvailable)
				{
					if (b.getId() == id)
					{
						rowFilter.hideRow(tblAvailable,tblAvailable.convertRowIndexToView(rownr));
						break;
					}
					rownr++;
				}
			}
		}
	}
	/**
	 * Create the frame.
	 */
	public WerkgeverPakketten(JFrame frame, boolean modal) {
		super(frame, modal,"Selecteer pakketten");
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
		PakketInfo actSelected;
		List<PakketInfo> newSelected = new ArrayList<PakketInfo>();
		// Op het scherm staan de geselecteerde pakketten
		// Die moeten we vergelijken met wat de werkgever al had
		// om te bepalen wat weg moet en wat nieuw is.
		
		// We maken de lijst van oorspronkelijk geselecteerde activiteiten leeg
		// en voegen wat geselecteerd was er aan toe
		rowcount = tblSelected.getRowCount();
		for (int i = 0; i < rowcount; i++)
		{
			rowinlist = tblSelected.convertRowIndexToModel(i);
			actSelected = pakketAvailable.get(rowinlist);
			/*
			 * De vraag is nu: was dit pakket al geselecteerd. Maw: komt het al voor bij de 
			 * werkgever?
			 */
			found = false;
			for (PakketInfo p:werkgever.getPakketten()){
				if (p.getId().equals(actSelected.getId())){
				    found = true;
				}
			}
			if (found == false){
				/* 
				 * pakket moet worden toegevoegd
				 */
				actSelected.setAction(persistenceaction.INSERT);
				newSelected.add(actSelected);
			}
		}
		/*
		 * De pakketten die niet meer nodig zijn moeten worden 
		 * verwijderd
		 */
		for (PakketInfo p: werkgever.getPakketten()){
			found = false;
			rowcount = tblSelected.getRowCount();
			for (int i = 0; i < rowcount; i++)
			{
				rowinlist = tblSelected.convertRowIndexToModel(i);
				actSelected = pakketAvailable.get(rowinlist);
				if (actSelected.getId().equals(p.getId())){
					found = true;
					break;
				}
			}
			if (found == false){
				/*
				 * pakket komt niet voor en moet dus worden verwijderd
				 */
				p.setAction(persistenceaction.DELETE);
			}
		}

		/*
		 * Nu nog de nieuwe pakketten toevoegen. Die staan in pakketSelected.
		 */
		
		for (PakketInfo p:newSelected){
			werkgever.getPakketten().add(p);
		}
		if (loginSession != null)
        {
			this.setVisible(false);
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}

	public void initialize() {
		setBounds(100, 100, 669, 457);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPaneAvailable = new JScrollPane();
		scrollPaneAvailable.setBounds(31, 53, 253, 320);
		getContentPane().add(scrollPaneAvailable);
		
		tblAvailable = new JTable();
		scrollPaneAvailable.setViewportView(tblAvailable);
		tblAvailable.setShowHorizontalLines(false);
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
			},
			new String[] {
				"Pakket"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] {
				false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
			
		JScrollPane scrollPaneSelected = new JScrollPane();
		scrollPaneSelected.setBounds(371, 52, 251, 320);
		getContentPane().add(scrollPaneSelected);
		
		tblSelected = new JTable();
		scrollPaneSelected.setViewportView(tblSelected);
		tblSelected.setShowHorizontalLines(false);
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
			},
			new String[] {
				"Pakket"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
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
		btnCopyAll.setBounds(295, 63, 60, 23);
		getContentPane().add(btnCopyAll);
		
		JButton btnCopySelected = new JButton(">");
		btnCopySelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCopySelectedClicked(e);
			}
		});
		btnCopySelected.setBounds(296, 97, 60, 23);
		getContentPane().add(btnCopySelected);
		
		JButton btnDeleteSelected = new JButton("<");
		btnDeleteSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDeleteSelectedClicked(e);
			}
		});
		btnDeleteSelected.setBounds(296, 128, 60, 23);
		getContentPane().add(btnDeleteSelected);
		
		JButton btnDeleteAll = new JButton("<<");
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDeleteAllClicked(e);
			}
		});
		btnDeleteAll.setBounds(296, 159, 60, 23);
		getContentPane().add(btnDeleteAll);
		
		rowFilter = SelectionRowFilter.CreateSelectionRowFilter(tblAvailable, tblSelected, null);
	}
}
