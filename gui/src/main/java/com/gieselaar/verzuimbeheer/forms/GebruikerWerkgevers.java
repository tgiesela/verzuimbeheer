package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDialog;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerWerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.SelectionRowFilter;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class GebruikerWerkgevers extends BaseDialog {

	private static final long serialVersionUID = 1L;
	private LoginSessionRemote loginSession;
	private GebruikerInfo gebruiker = null;
	List<WerkgeverInfo> werkgeverAvailable;
	List<GebruikerWerkgeverInfo> werkgeverSelected;
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
	public void setInfo(GebruikerInfo info){
		int rownr;
		int id;
		gebruiker = info;
		
		werkgeverSelected = gebruiker.getWerkgevers();
		try {
			werkgeverAvailable = ServiceCaller.werkgeverFacade(loginSession).allWerkgeversList();
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

		rowFilter.setAvailableRows(werkgeverAvailable);
		
		tblModelAvailable = (DefaultTableModel) tblAvailable.getModel();
		tblModelAvailable.setRowCount(0);

		tblModelSelected = (DefaultTableModel) tblSelected.getModel();
		tblModelSelected.setRowCount(0);
		if (werkgeverAvailable != null)
		{
			for(WerkgeverInfo w :werkgeverAvailable)
			{
				Vector<String> rowData = new Vector<String>();
				rowData.add(w.getNaam());
				tblModelAvailable.addRow(rowData);
				tblModelSelected.addRow(rowData);
			}
		}
		if (werkgeverSelected != null)
		{
			for(GebruikerWerkgeverInfo a :werkgeverSelected)
			{
				id = a.getWerkgeverid();
				rownr = 0;
				for (WerkgeverInfo b: werkgeverAvailable)
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
	public GebruikerWerkgevers(JFrame frame, boolean modal) {
		super(frame, modal,"Selecteer werkgevers");
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
		WerkgeverInfo wgr;
		
		// We maken de lijst van oorspronkelijk geselecteerde activiteiten leeg
		// en voegen wat geselecteerd was er aan toe
		rowcount = tblSelected.getRowCount();
		if (rowcount > 0)
		{
			if (werkgeverSelected == null)
			{
				werkgeverSelected = new ArrayList<GebruikerWerkgeverInfo>();
				gebruiker.setWerkgevers(werkgeverSelected);
			}
			//werkgeverSelected.clear();
		}
		else
			werkgeverSelected.clear();
		for (int i = 0; i < rowcount; i++)
		{
			rowinlist = tblSelected.convertRowIndexToModel(i);
			wgr = werkgeverAvailable.get(rowinlist);
			found = false;
			for(GebruikerWerkgeverInfo a :werkgeverSelected)
			{
				if (wgr.getId().equals(a.getWerkgeverid()))
				{
					/* was al eerder geselecteerd */
					found = true;
					break;
				}
			}
			if (found)
				continue;
			else
			{
				/* Toevoegen aan de lijst van geselecteerde items*/
				GebruikerWerkgeverInfo gebruikerwg = new GebruikerWerkgeverInfo();
				gebruikerwg.setGebruikerid(this.gebruiker.getId());
				gebruikerwg.setWerkgeverid(wgr.getId());
				gebruikerwg.setAction(persistenceaction.INSERT);
				werkgeverSelected.add(gebruikerwg);
			}
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
				"Werkgever"
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
				"Werkgever"
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
