package com.gieselaar.verzuim.views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ImportbetalingenController;
import com.gieselaar.verzuim.controllers.AbstractController.__filedialogtype;
import com.gieselaar.verzuim.controllers.AbstractController.__selectfileoption;
import com.gieselaar.verzuim.controllers.ImportbetalingenController.__importbetalingencommands;
import com.gieselaar.verzuim.controllers.ImportbetalingenController.__importedbetalingenfields;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.interfaces.WerkgeverNotification;
import com.gieselaar.verzuim.utils.CursorController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo.__checkresult;

import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ImportBetalingen extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkgeverSelection werkgeverSelection;
	private final String PANELNAMEBETALINGEN = "Betalingen"; 
	private JTextFieldTGI txtImportbestandlocatie;
	private JTextFieldTGI txtSeparator;
	private DatatablePanel tpBetalingen;
	private ImportBetalingInfo selectedBetaling = null;
	private List<ImportBetalingInfo> importedbetalingen = new ArrayList<>();
	private JButton btnUpdate;
	private JCheckBox chckbxDoorvoeren;
	private JRadioButton rbRaboCSV;
	private JRadioButton rbMT940;
	private JTextAreaTGI taOmschrijving;

	private int periodlength = 6;

	private JTextField txtFactuurNr;
	private JFormattedTextField txtBedragbetaling;
	private JFormattedTextField txtFactuurbedrag;
	private JFormattedTextField txtReedsbetaald;
	private JCheckBox chckbxAlleenprobleemgevallen;
	private JSpinner spinnerMaanden;
	private JLabel lblAantalMaanden;

	private ImportbetalingenController betalingencontroller;
	public ImportBetalingen(AbstractController controller) {
		super("Importeer betalingen", controller);
		betalingencontroller = (ImportbetalingenController) controller;
		initialize();
		
		/* add actionhandler to do own processing */
		super.getOkButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okButtonClicked();
			}
		});
		super.setCloseAfterSave(false);
	}
	@Override
	public void setData(InfoBase info) {

		txtImportbestandlocatie.setText("");
		rbMT940.setSelected(false);
		rbRaboCSV.setSelected(true);
		initializesearch();
	}

	void initialize() {
		setBounds(100, 100, 827, 565);

		JLabel lblDocumentlocatie = new JLabel("Bestand");
		lblDocumentlocatie.setBounds(20, 46, 140, 14);
		getContentPane().add(lblDocumentlocatie);

		txtImportbestandlocatie = new JTextFieldTGI();
		txtImportbestandlocatie.setBounds(181, 43, 286, 20);
		getContentPane().add(txtImportbestandlocatie);

		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked(e);
			}
		}));
		btnNewButton.setBounds(474, 42, 35, 23);
		getContentPane().add(btnNewButton);

		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setBounds(355, 20, 60, 14);
		getContentPane().add(lblSeparator);

		txtSeparator = new JTextFieldTGI();
		txtSeparator.setText(",");
		txtSeparator.setBounds(429, 17, 20, 20);
		getContentPane().add(txtSeparator);
		txtSeparator.setColumns(1);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(20, 92, 781, 251);
		getContentPane().add(tabbedPane);

		addTabBetalingen(tabbedPane);
		JButton btnImporteer = new JButton("Importeer");
		btnImporteer.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnimporteerClicked(e);
			}
		}));
		btnImporteer.setBounds(181, 65, 89, 23);
		getContentPane().add(btnImporteer);

		txtFactuurNr = new JTextField();
		txtFactuurNr.setBounds(95, 354, 75, 20);
		getContentPane().add(txtFactuurNr);
		txtFactuurNr.setColumns(10);

		JLabel lblFactuurnr = new JLabel("Factuurnr");
		lblFactuurnr.setBounds(20, 357, 65, 14);
		getContentPane().add(lblFactuurnr);

		JLabel lblOmschr = new JLabel("Omschr");
		lblOmschr.setBounds(20, 381, 65, 14);
		getContentPane().add(lblOmschr);

		btnUpdate = new JButton("Update...");
		btnUpdate.setActionCommand(__importbetalingencommands.UPDATEIMPORTEDBETALING.toString());
		btnUpdate.addActionListener(CursorController.createListener(this, betalingencontroller));
		btnUpdate.setBounds(376, 463, 89, 23);

		getContentPane().add(btnUpdate);
		addWerkgeverSelectionPanel();

		JLabel lblBedragbetaling = new JLabel("Bedragbetaling");
		lblBedragbetaling.setBounds(180, 357, 75, 14);
		getContentPane().add(lblBedragbetaling);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		txtBedragbetaling = new JFormattedTextField(amountFormat);
		txtBedragbetaling.setColumns(10);
		txtBedragbetaling.setBounds(255, 354, 75, 20);
		getContentPane().add(txtBedragbetaling);

		JLabel lblFactuurbedrag = new JLabel("Factuurbedrag");
		lblFactuurbedrag.setBounds(340, 357, 75, 14);
		getContentPane().add(lblFactuurbedrag);

		txtFactuurbedrag = new JFormattedTextField(amountFormat);
		txtFactuurbedrag.setEditable(false);
		txtFactuurbedrag.setColumns(10);
		txtFactuurbedrag.setBounds(415, 354, 75, 20);
		getContentPane().add(txtFactuurbedrag);

		JLabel lblReedsBetaald = new JLabel("reeds betaald");
		lblReedsBetaald.setBounds(500, 357, 75, 14);
		getContentPane().add(lblReedsBetaald);

		txtReedsbetaald = new JFormattedTextField(amountFormat);
		txtReedsbetaald.setEditable(false);
		txtReedsbetaald.setColumns(10);
		txtReedsbetaald.setBounds(575, 354, 75, 20);
		getContentPane().add(txtReedsbetaald);

		chckbxDoorvoeren = new JCheckBox("Doorvoeren");
		chckbxDoorvoeren.setEnabled(false);
		chckbxDoorvoeren.setBounds(474, 463, 97, 23);
		getContentPane().add(chckbxDoorvoeren);

		rbMT940 = new JRadioButton("MT940");
		rbMT940.setSelected(true);
		rbMT940.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtSeparator.setEnabled(false);
			}
		});
		rbMT940.setBounds(181, 16, 75, 23);
		getContentPane().add(rbMT940);

		rbRaboCSV = new JRadioButton("Rabo .CSV");
		rbRaboCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtSeparator.setEnabled(true);
			}
		});
		rbRaboCSV.setBounds(266, 16, 83, 23);
		getContentPane().add(rbRaboCSV);

		ButtonGroup buttongroup = new ButtonGroup();
		buttongroup.add(rbRaboCSV);
		buttongroup.add(rbMT940);

		spinnerMaanden = new JSpinner();
		spinnerMaanden.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				periodlength = (int) spinnerMaanden.getValue();
				initializesearch();
			}
		});
		spinnerMaanden.setBounds(401, 68, 41, 20);
		getContentPane().add(spinnerMaanden);
		spinnerMaanden.setValue(6);

		// setBounds(100, 100, 485, 506);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(92, 381, 274, 107);
		getContentPane().add(scrollPane);

		taOmschrijving = new JTextAreaTGI();
		scrollPane.setColumnHeaderView(taOmschrijving);
		
		JLabel lblBestandsformaat = new JLabel("Bestandsformaat");
		lblBestandsformaat.setBounds(20, 20, 140, 14);
		getContentPane().add(lblBestandsformaat);
		
		lblAantalMaanden = new JLabel("Aantal maanden");
		lblAantalMaanden.setBounds(312, 69, 89, 14);
		getContentPane().add(lblAantalMaanden);
	}
	private void addTabBetalingen(JTabbedPane tabbedPane) {
		tpBetalingen = new DatatablePanel(betalingencontroller);
		tpBetalingen.getTableScrollPane().setBounds(36, 0, 681, 189);
		tpBetalingen.setName(PANELNAMEBETALINGEN);
		tpBetalingen.getPanelAction().setLocation(5, 188);

		tpBetalingen.addColumn(__importedbetalingenfields.DATUM.getValue(),"Datum", 60, Date.class);
		tpBetalingen.addColumn(__importedbetalingenfields.BEDRAG.getValue(),"Bedrag",  60, BigDecimal.class);
		tpBetalingen.addColumn(__importedbetalingenfields.REKENINGBETALER.getValue(),"Reknr betaler", 70);
		tpBetalingen.addColumn(__importedbetalingenfields.BEDRIJFSNAAM.getValue(),"Bedrijfsnaam", 150);
		tpBetalingen.addColumn(__importedbetalingenfields.DVVWERKGEVER.getValue(),"DVV Werkgever", 100);
		tpBetalingen.addColumn(__importedbetalingenfields.DVVFACTUURNUMMER.getValue(),"DVV Factuurnr", 100);
		tpBetalingen.addColumn(__importedbetalingenfields.DVVFACTUURBEDRAG.getValue(),"DVV Factuurbedrag", 60, BigDecimal.class);
		tpBetalingen.addColumn(__importedbetalingenfields.DVVMELDING.getValue(),"Melding", 140);
		
		chckbxAlleenprobleemgevallen = new JCheckBox("alleen probleem gevallen");
		chckbxAlleenprobleemgevallen.setActionCommand(__importbetalingencommands.ALLEENPROBLEEMGEVALLENTONEN.toString());
		chckbxAlleenprobleemgevallen.addActionListener(CursorController.createListener(this, betalingencontroller));
		tpBetalingen.getPanelAction().add(chckbxAlleenprobleemgevallen);

		tabbedPane.addTab("Betalingen", null, tpBetalingen, null);
		
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpBetalingen.disableSorter();

				importedbetalingen = betalingencontroller.getImportedBetalingen();
				betalingencontroller.getTableModel(importedbetalingen, (ColorTableModel) tpBetalingen.getTable().getModel(), tpBetalingen.colsinview);

				/* Add sort again */
				tpBetalingen.enableSorter();
				
				/* Disable update button */
				btnUpdate.setEnabled(false);
				selectedBetaling = null;
				
			}
			@Override
			public void rowSelected(int rownr, Object info) {
				ImportBetalingInfo ibi = (ImportBetalingInfo) info;
				selectedBetaling = ibi;
				if (ibi.getFactuurnummer() != null) {
					txtFactuurNr.setText(ibi.getFactuurnummer().toString());
				} else {
					txtFactuurNr.setText("");
				}
				taOmschrijving.setText(ibi.getOmschrijving1());
				if (ibi.getHoldingid() != null) {
					werkgeverSelection.setHoldingId(ibi.getHoldingid());
					werkgeverSelection.setWerkgeverId(-1);
				} else {
					if (ibi.getWerkgeverid() != null) {
						werkgeverSelection.setWerkgeverId(ibi.getWerkgeverid());
						werkgeverSelection.setHoldingId(-1);
					}
				}
				txtBedragbetaling.setValue(ibi.getBedrag());
				if (ibi.getFactuurbedrag() != null)
					txtFactuurbedrag.setValue(ibi.getFactuurbedrag().setScale(2, RoundingMode.HALF_UP));
				if (ibi.getFactuur() != null) {
					txtReedsbetaald.setValue(ibi.getFactuur().getSombetalingen().setScale(2, RoundingMode.HALF_UP));
				}
				if (ibi.getCheckresult() == __checkresult.OK || ibi.getCheckresult() == __checkresult.ALREADYPAID) {
					chckbxDoorvoeren.setEnabled(false);
				} else {
					chckbxDoorvoeren.setEnabled(true);
					chckbxDoorvoeren.setSelected(ibi.isAccepted());
				}
				btnUpdate.setEnabled(true);
			}
		};
		registerControllerListener(betalingencontroller, listener);

	}
	private void addWerkgeverSelectionPanel() {
		werkgeverSelection = new WerkgeverSelection(__WerkgeverSelectionMode.SelectOne, __WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(376, 378, 378, 63);
		werkgeverSelection.setMaincontroller(controller.getMaincontroller());
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				return false;
			}
		});
	}

	@Override
	public InfoBase collectData() {
		if (selectedBetaling == null){
			return null;
		}
		FactuurTotaalInfo factuur;
		DecimalFormat format = new DecimalFormat("##0.00");
		format.setParseBigDecimal(true);
		selectedBetaling.setWerkgeverid(werkgeverSelection.getWerkgeverId());
		selectedBetaling.setHoldingid(werkgeverSelection.getHoldingId());
		if (txtFactuurNr.getText().isEmpty()){
			JOptionPane.showConfirmDialog(this, "Factuurnummer niet ingevuld!","Fout",JOptionPane.OK_OPTION);
			return null;
		}
		selectedBetaling.setFactuurnummer(Integer.parseInt(txtFactuurNr.getText()));
		factuur = betalingencontroller.findFactuurByFactuurnr(selectedBetaling.getFactuurnummer(), selectedBetaling.getWerkgeverid(),
				selectedBetaling.getHoldingid(),selectedBetaling.getBedrag());
		selectedBetaling.setFactuur(factuur);
		if (factuur != null) {
			selectedBetaling.setFactuurid(factuur.getId());
			selectedBetaling.setFactuurbedrag(factuur.getTotaalInclBtw());
			if (selectedBetaling.getWerkgeverid() == null) {
				selectedBetaling.setWerkgeverid(factuur.getWerkgeverid());
			}
			if (selectedBetaling.getHoldingid() == null) {
				selectedBetaling.setHoldingid(factuur.getHoldingid());
			}
			if (chckbxDoorvoeren.isEnabled()) {
				selectedBetaling.setAccepted(chckbxDoorvoeren.isSelected());
			}
		} else {
			selectedBetaling.setFactuurid(null);
			selectedBetaling.setFactuurbedrag(null);
			selectedBetaling.setAccepted(false);
		}
		try {
			selectedBetaling.setBedrag(format.format(txtBedragbetaling.getValue()));
		} catch (ParseException e) {
			/* ignore */
		}
		
		btnUpdate.setEnabled(false);
		werkgeverSelection.setWerkgeverId(-1);
		werkgeverSelection.setHoldingId(-1);
		txtFactuurNr.setText("");
		txtBedragbetaling.setText("");
		txtFactuurbedrag.setText("");
		taOmschrijving.setText("");
		
		return selectedBetaling;
	}

	protected void btnimporteerClicked(ActionEvent e) {
		String docnaam = txtImportbestandlocatie.getText();
		if (docnaam.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Importbestand is leeg.");
			return;
		}
		try {
			betalingencontroller.importeerbetalingen(docnaam, txtSeparator.getText(), rbMT940.isSelected(), periodlength);
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
	}

	private void initializesearch() {
		Date lastmonth = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastmonth);
		lastmonth = cal.getTime();
		cal.add(Calendar.MONTH, -periodlength); // Maximaal 6 maanden terug
												// zoeken, de rest
												// moet handmatig
		Date firstmonth = cal.getTime();
		werkgeverSelection.setStartperiode(firstmonth);
	}

	protected void btnSelectFileClicked(ActionEvent e) {
		
		File f = betalingencontroller.selectFilename(__selectfileoption.FILEONLY,__filedialogtype.OPEN, "", "");
		txtImportbestandlocatie.setText(f.getAbsolutePath());
	}

	protected void okButtonClicked() {
		betalingencontroller.addFactuurbetalingen(importedbetalingen);
		JOptionPane.showMessageDialog(this, "Betalingen doorgevoerd");
	}
}
