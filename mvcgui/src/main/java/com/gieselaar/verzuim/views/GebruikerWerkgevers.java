package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.GebruikerWerkgeverController;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerWerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.SelectionRowFilter;

public class GebruikerWerkgevers extends AbstractDetail {
	private static final long serialVersionUID = 1L;
	private GebruikerInfo gebruiker = null;
	List<WerkgeverInfo> werkgeverAvailable;
	List<GebruikerWerkgeverInfo> werkgeverSelected;
	DefaultTableModel tblModelSelected;
	DefaultTableModel tblModelAvailable;
	private JTable tblAvailable;
	private JTable tblSelected;
	private SelectionRowFilter rowFilter;
	private GebruikerWerkgeverController werkgevercontroller;
	/**
	 * Getters and setters of this dialog
	 */
	/**
	 * Create the frame.
	 */
	public GebruikerWerkgevers(AbstractController controller) {
		super("Selecteer werkgevers", controller);
		werkgevercontroller = (GebruikerWerkgeverController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		gebruiker = (GebruikerInfo) info;
		displayWerkgevers();
	}
	private void displayWerkgevers(){
		int rownr;
		int id;
		werkgeverSelected = werkgevercontroller.getGebruikerWerkgeversList();
		werkgeverAvailable = controller.getMaincontroller().getWerkgevers();

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
	public InfoBase collectData(){
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
		return gebruiker;
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
