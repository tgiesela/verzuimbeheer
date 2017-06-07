package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
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
import java.util.Date;
import java.util.List;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JRadioButton;
import javax.swing.JButton;

import reportdatasources.VerzuimHistorieDatasource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

public class ReportVerzuimenHistorie extends BaseDetailform  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2862612204474930868L;
	private List<WerkgeverInfo> werkgevers;
	private List<WerknemerFastInfo> werknemers;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<TypeEntry>();
	private DefaultComboBoxModel<TypeEntry> soortmodelWG = new DefaultComboBoxModel<TypeEntry>();
	private DefaultComboBoxModel<TypeEntry> soortmodelWN = new DefaultComboBoxModel<TypeEntry>();
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;
	private JComboBox<TypeEntry> cmbWerknemer = new JComboBox<TypeEntry>();
	private JRadioButton rdbtnNaamVolgorde = new JRadioButton();
	private JRadioButton rdbtnBSNVolgorde = new JRadioButton();
	private Integer selectedWerknemer;
	/**
	 * Getters and setters of this dialog
	 */
	public void setInfo(){
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
		} catch (ServiceLocatorException | PermissionException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		
        cmbWerkgever.setModel(soortmodelWG);
        cmbWerknemer.setEnabled(false);
		TypeEntry dummy = new TypeEntry(-1,"[geen]");
		soortmodelWG.addElement(dummy);
        for (WerkgeverInfo wgr: werkgevers)
		{
			TypeEntry entry = new TypeEntry(wgr.getId(), wgr.getNaam());
			soortmodelWG.addElement(entry);
		}
        activateListener();
	}
	public void setInfo(Integer werkgever, Integer werknemer){
		setInfo();
		
		selectedWerknemer = werknemer;
		for (int i=0;i<soortmodelWG.getSize();i++){
		    TypeEntry wgr = soortmodelWG.getElementAt(i);
			if (wgr.getValue() == werkgever)
				soortmodelWG.setSelectedItem(wgr);
		}
//		cmbWerkgeverClicked(null);
	}
	/**
	 * Create the frame.
	 */
	public ReportVerzuimenHistorie(JDesktopPaneTGI mdiPanel) {
		super("Report werknemer verzuimen", mdiPanel);
		setTitle("Rapport Werknemer verzuimhistorie");
		initialize();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize(){
		setBounds(100, 100, 526, 312);
		getContentPane().setLayout(null);
		
		JLabel lblNaam = new JLabel("Werkgever");
		lblNaam.setBounds(30, 14, 84, 14);
		getContentPane().add(lblNaam);
		
		cmbWerkgever = new JComboBox();
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
		
		cmbWerknemer = new JComboBox();
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

		if (dtpEinddatum.getDate().before(dtpStartdatum.getDate())){
        	JOptionPane.showMessageDialog(this,"Einddatum ligt voor de startdatum!");
        	return;
		}
		if (dtpEinddatum.getDate().equals(dtpStartdatum.getDate())){
        	JOptionPane.showMessageDialog(this,"Einddatum is gelijk aan startdatum!");
        	return;
		}
		if (dtpEinddatum.getDate().after(new Date())){
        	JOptionPane.showMessageDialog(this,"Einddatum ligt in de toekomst!");
        	return;
		}
		VerzuimHistorieDatasource ds = new VerzuimHistorieDatasource(dtpStartdatum.getDate(), dtpEinddatum.getDate(), this.getLoginSession());
		JasperPrint print;
		try {
			print = ds.getReport(werknemerid);
			JFrame frame = new JFrame("Report");
			frame.getContentPane().add(new JRViewer(print));
			frame.pack();
			frame.setVisible(true);
		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
        	return;
		}

		
	}
	protected void cmbWerkgeverClicked(ActionEvent e) {
		TypeEntry entry;
		if (cmbWerkgever.getSelectedIndex() != 0){
			entry = (TypeEntry) cmbWerkgever.getSelectedItem();
			if (entry.getValue() == -1)
				cmbWerknemer.setEnabled(false);
			else{
				cmbWerknemer.setEnabled(true);
				populateWerknemers(entry.getValue());
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
		try {
			werknemers = ServiceCaller.werknemerFacade(getLoginSession()).getWerknemersByWerkgever(werkgeverid);
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
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this,"Kan werknemers niet opvragen! ");
        	return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		
	}
	protected void cancelButtonClicked(ActionEvent e) {
		this.setDatachanged(false);
		super.cancelButtonClicked(e);
	}
	protected void okButtonClicked(ActionEvent e) {
		super.okButtonClicked(e);
	}
	/*
	 * Custom list cell render for our JComboBox
	 */
	class MultiColumnRender implements ListCellRenderer<Object> {
	 
		private JPanel _panelListCell = new JPanel();
	 
		/**
		 * The serialVersionUID is a universal version identifier for a Serializable class. 
		 * Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized object. 
		 * If no match is found, then an InvalidClassException is thrown. 
		 */
		@SuppressWarnings("unused")
		private static final long serialVersionUID = -722402815727238028L; 
		private List<JLabel> _labelComponentColumn = new ArrayList<JLabel>();
		private int _colcount=0;
		/*
		 * Constructor
		 */
		public MultiColumnRender(int columncount) {
	 
			_colcount = columncount;
			// Set layout
			GridLayout gridLayout = new GridLayout(
					1, // rows 
					_colcount  // columns
					);
			_panelListCell.setLayout(gridLayout);
	 
			// Add components
			
			for (int i=0;i<_colcount;i++)
			{
				_labelComponentColumn.add(new JLabel());
				_panelListCell.add(_labelComponentColumn.get(i));
			}
			
		}	
	 
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	 
			TypeEntry entry = (TypeEntry) value;
			String[] values = entry.getLabels();
	 
			for (int i=0;i<_colcount;i++)
			{
				JLabel label = _labelComponentColumn.get(i);
				label.setText(values[i]);
				label.setOpaque(true);
				if ((isSelected) && (index != -1))	 // Change color only in selectlist
					label.setBackground(Color.BLUE); 
				else
					label.setBackground(Color.WHITE); 
			}
	 
			return _panelListCell;
		}
	}

}
