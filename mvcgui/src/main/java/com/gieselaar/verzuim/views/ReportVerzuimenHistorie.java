package com.gieselaar.verzuim.views;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.utils.CursorController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JRadioButton;
import javax.swing.JButton;

public class ReportVerzuimenHistorie extends AbstractDetail  {
	private static final long serialVersionUID = -2862612204474930868L;
	private List<WerknemerFastInfo> werknemers;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<>();
	private VerzuimComboBoxModel werkgevermodel;  
	private DefaultComboBoxModel<TypeEntry> soortmodelWN = new DefaultComboBoxModel<>();
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;
	private JComboBox<TypeEntry> cmbWerknemer = new JComboBox<>();
	private JRadioButton rdbtnNaamVolgorde = new JRadioButton();
	private JRadioButton rdbtnBSNVolgorde = new JRadioButton();
	private Integer selectedWerknemer;

	private ReportController reportcontroller; 
	public ReportVerzuimenHistorie(AbstractController controller) {
		super("Report werknemer verzuimen", controller);
		reportcontroller = (ReportController) controller;
		initialize();
		/* Treat OK as Cancel */
		super.getOkButton().setActionCommand(controller.cancelDetailActionCommand);
	}
	public void setData(Integer werkgever, Integer werknemer){
		setData();
		
		selectedWerknemer = werknemer;
		if (werkgever == null){
			werkgevermodel.setId(-1);
		}else{
			werkgevermodel.setId(werkgever);
		}
	}
	public void setData(){

		werkgevermodel =  new VerzuimComboBoxModel(controller.getMaincontroller());
        controller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, true);
		cmbWerkgever.setModel(werkgevermodel);
	}
	private void initialize(){
		setBounds(100, 100, 526, 312);
		getContentPane().setLayout(null);
		
		JLabel lblNaam = new JLabel("Werkgever");
		lblNaam.setBounds(30, 14, 84, 14);
		getContentPane().add(lblNaam);
		
		cmbWerkgever = new JComboBox<>();
		cmbWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWerkgeverClicked(e);
			}
		});
		cmbWerkgever.setBounds(171, 11, 256, 20);
		getContentPane().add(cmbWerkgever);
		
		JLabel lblHolding = new JLabel("Werknemer");
		lblHolding.setBounds(30, 39, 84, 14);
		getContentPane().add(lblHolding);
		
		cmbWerknemer = new JComboBox<>();
		cmbWerknemer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		cmbWerknemer.setBounds(171, 34, 256, 20);
		getContentPane().add(cmbWerknemer);
		
		dtpStartdatum = new DatePicker();
		dtpStartdatum.setBounds(171, 64, 87, 21);
		dtpStartdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(171, 89, 87, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblVan = new JLabel("Van");
		lblVan.setBounds(30, 67, 84, 14);
		getContentPane().add(lblVan);
		
		JLabel lblTot = new JLabel("Tot en met");
		lblTot.setBounds(30, 92, 84, 14);
		getContentPane().add(lblTot);
		
		rdbtnNaamVolgorde = new JRadioButton("sorteer op naam");
		rdbtnNaamVolgorde.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWerkgeverClicked(e);
			}
		});
		rdbtnNaamVolgorde.setSelected(true);
		rdbtnNaamVolgorde.setBounds(318, 62, 109, 23);
		getContentPane().add(rdbtnNaamVolgorde);
		
		rdbtnBSNVolgorde = new JRadioButton("sorteer op BSN");
		rdbtnBSNVolgorde.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWerkgeverClicked(e);
			}
		});
		rdbtnBSNVolgorde.setBounds(318, 89, 109, 23);
		getContentPane().add(rdbtnBSNVolgorde);
		
		ButtonGroup buttongroup = new ButtonGroup();
		buttongroup.add(rdbtnNaamVolgorde);
		buttongroup.add(rdbtnBSNVolgorde);
		
		JButton btnAfdrukken = new JButton("Afdrukken...");
		btnAfdrukken.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAfdrukkenClicked(e);
			}
		}));
		btnAfdrukken.setBounds(318, 161, 109, 23);
		getContentPane().add(btnAfdrukken);
		
	}
	protected void btnAfdrukkenClicked(ActionEvent e) {
		TypeEntry entry;
		Integer werknemerid;
		entry = (TypeEntry) cmbWerknemer.getSelectedItem();
		if (entry.getValue() == -1) {
        	JOptionPane.showMessageDialog(this,"Geen werknemer geselecteerd!");
        	return;
		}
		werknemerid = entry.getValue();
		
		try {
			reportcontroller.printVerzuimhistorie(werknemerid, dtpStartdatum.getDate(),dtpEinddatum.getDate());
		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
		
	}
	protected void cmbWerkgeverClicked(ActionEvent e) {
		if (cmbWerkgever.getSelectedIndex() != 0){
			Integer werkgeverid = werkgevermodel.getId();
			if (werkgeverid == -1)
				cmbWerknemer.setEnabled(false);
			else{
				cmbWerknemer.setEnabled(true);
				populateWerknemers(werkgeverid);
				if (selectedWerknemer == null)
					cmbWerknemer.setSelectedIndex(0);
			}
		}
	}
	protected void populateWerknemers(int werkgeverid) {
		soortmodelWN.removeAllElements();
        cmbWerknemer.setModel(soortmodelWN);
        cmbWerknemer.setRenderer(new MultiColumnRender(2));
		TypeEntry dummy = new TypeEntry(-1,new String[] {"[geen]",""});
		soortmodelWN.addElement(dummy);
		reportcontroller.selectWerknemers(werkgeverid);
		refreshTable();
	}
	public void refreshTable() {
		werknemers = reportcontroller.getWerknemerList();
		if (rdbtnNaamVolgorde.isSelected())
			WerknemerFastInfo.sort(werknemers, WerknemerFastInfo.__sortcol.ACHTERNAAM);
		else
			if (rdbtnBSNVolgorde.isSelected())
				WerknemerFastInfo.sort(werknemers, WerknemerFastInfo.__sortcol.BSN);

        for (WerknemerFastInfo wnr: werknemers)
		{
        	String naam = "";
        	naam = naam + wnr.getAchternaam();
        	if (wnr.getVoorvoegsel() != null)
        		naam = naam + "," + wnr.getVoorvoegsel() + " ";
        	if (wnr.getVoorletters() != null)
        		naam = naam + " " + wnr.getVoorletters() + " ";
			TypeEntry entry = new TypeEntry(wnr.getId(), new String[]{naam, wnr.getBurgerservicenummer()});
			soortmodelWN.addElement(entry);
			if (wnr.getId().equals(selectedWerknemer))
				soortmodelWN.setSelectedItem(entry);
		}
		
	};
	/*
	 * Custom list cell render for our JComboBox
	 */
	class MultiColumnRender implements ListCellRenderer<Object> {
	 
		private JPanel panelListCell = new JPanel();
	 
		@SuppressWarnings("unused")
		private static final long serialVersionUID = -722402815727238028L; 
		private List<JLabel> labelComponentColumn = new ArrayList<JLabel>();
		private int colcount=0;
		/*
		 * Constructor
		 */
		public MultiColumnRender(int columncount) {
	 
			colcount = columncount;
			// Set layout
			GridLayout gridLayout = new GridLayout(
					1, // rows 
					colcount  // columns
					);
			panelListCell.setLayout(gridLayout);
	 
			// Add components
			
			for (int i=0;i<colcount;i++)
			{
				labelComponentColumn.add(new JLabel());
				panelListCell.add(labelComponentColumn.get(i));
			}
			
		}	
	 
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	 
			TypeEntry entry = (TypeEntry) value;
			String[] values = entry.getLabels();
	 
			for (int i=0;i<colcount;i++)
			{
				JLabel label = labelComponentColumn.get(i);
				label.setText(values[i]);
				label.setOpaque(true);
				if ((isSelected) && (index != -1))	 // Change color only in selectlist
					label.setBackground(Color.BLUE); 
				else
					label.setBackground(Color.WHITE); 
			}
	 
			return panelListCell;
		}
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
