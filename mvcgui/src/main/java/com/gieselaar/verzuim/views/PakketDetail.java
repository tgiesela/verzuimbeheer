package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.PakketController;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.PakketInfo;
import com.gieselaar.verzuimbeheer.utils.SelectionRowFilter;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

public class PakketDetail extends AbstractDetail {
	private static final long serialVersionUID = -3503855501976700503L;
	private PakketInfo pakket = null;
	private JTextFieldTGI txtNaam;
	List<ActiviteitInfo> activiteitAvailable;
	List<ActiviteitInfo> activiteitSelected;
	DefaultTableModel tblModelSelected;
	DefaultTableModel tblModelAvailable;
	private JTable tblAvailable;
	private JTable tblSelected;
	private SelectionRowFilter rowFilter;
	private PakketController pakketcontroller; 
	/**
	 * Create the frame.
	 */
	public PakketDetail(AbstractController controller){
		super("Samenstellen pakket", controller);
		pakketcontroller = (PakketController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		pakket = (PakketInfo) info;
		displayPakket();
	}
	private void displayPakket(){
		int rownr;
		int id;
		txtNaam.setText(pakket.getNaam());
		activiteitSelected = pakket.getAktiviteiten();
		activiteitAvailable = pakketcontroller.getMaincontroller().getActiviteiten();

		rowFilter.setAvailableRows(activiteitAvailable);
		
		tblModelAvailable = (DefaultTableModel) tblAvailable.getModel();
		tblModelAvailable.setRowCount(0);

		tblModelSelected = (DefaultTableModel) tblSelected.getModel();
		tblModelSelected.setRowCount(0);
		if (activiteitAvailable != null)
		{
			for(ActiviteitInfo a :activiteitAvailable)
			{
				Vector<String> rowData = new Vector<String>();
				rowData.add(a.getNaam());
				tblModelAvailable.addRow(rowData);
				tblModelSelected.addRow(rowData);
			}
		}
		if (activiteitSelected != null)
		{
			for(ActiviteitInfo a :activiteitSelected)
			{
				id = a.getId();
				rownr = 0;
				for (ActiviteitInfo b: activiteitAvailable)
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
	void initialize(){
		setBounds(100, 100, 728, 538);
		getContentPane().setLayout(null);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(113, 48, 181, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblPakketnaam = new JLabel("Pakketnaam");
		lblPakketnaam.setBounds(39, 51, 65, 14);
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
				"Code"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7784527771669969257L;
			boolean[] columnEditables = new boolean[] {
				false
			};
			@Override
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
				"Code"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3725331400408723548L;
			boolean[] columnEditables = new boolean[] {
				false
			};
			@Override
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
		ActiviteitInfo act;
		
		pakket.setNaam(txtNaam.getText());
		// We maken de lijst van oorspronkelijk geselecteerde activiteiten leeg
		// en voegen wat geselecteerd was er aan toe
		rowcount = tblSelected.getRowCount();
		if (rowcount > 0)
		{
			if (activiteitSelected == null)
			{
				activiteitSelected = new ArrayList<ActiviteitInfo>();
				pakket.setActiviteiten(activiteitSelected);
			}
			activiteitSelected.clear();
		}
		else
			activiteitSelected.clear();
		for (int i = 0; i < rowcount; i++)
		{
			rowinlist = tblSelected.convertRowIndexToModel(i);
			act = activiteitAvailable.get(rowinlist);
			found = false;
			for(ActiviteitInfo a :activiteitSelected)
			{
				if (act.getId().equals(a.getId()))
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
				act.setAction(persistenceaction.INSERT);
				activiteitSelected.add(act);
			}
		}
		return pakket;
	}
}
