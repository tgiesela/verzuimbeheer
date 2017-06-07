package com.gieselaar.verzuim.views;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurController;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class FactuurDetail extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FactuurTotaalInfo factuur;
	private HoldingInfo holding;
	
	private DatePicker dtpAanmaakdatum ;
	private List<HoldingInfo> holdings;
	private JFormattedTextField txtKilometerkosten;
	private JFormattedTextField txtUurkosten;
	private JFormattedTextField txtVastekosten;
	private JFormattedTextField txtSociaalbezoekkosten;
	private JFormattedTextField txtSecretariaatskosten;
	private JFormattedTextField txtOverigekosten;
	private JFormattedTextField txtLosseposten;
	private JFormattedTextField txtAbonnementskosten;
	private JFormattedTextField txtAansluitkosten;
	private JFormattedTextField txtCorrectiebedrag;
	private JFormattedTextField txtmaandkostenSecretariaat;
	private JFormattedTextField txtTotaalbedrag;
	private JFormattedTextField txtBtwlaag;
	private JFormattedTextField txtBtwhoog;
	private JTextFieldTGI txtFactuurnummer;
	private JTextFieldTGI txtStatus;
	private JTextFieldTGI txtJaar;
	private JTextFieldTGI txtMaand; 

	private JButton btnAfdrukken;
	
	private JPopupMenu popupMenu;
	private JMenuItem mntmAfdrukken; 
	private JMenuItem mntmAfdrukkenHolding;
	private JMenuItem mntmVerzenden;
	private JMenuItem mntmVerzendenHolding;
	private FactuurController factuurcontroller;
	private ReportController reportcontroller; 

	/**
	 * Create the frame.
	 */
	public FactuurDetail(AbstractController controller) {
		super("Beheer factuur", controller);
		factuurcontroller = (FactuurController) controller;
		reportcontroller = factuurcontroller.getReportController();
		initialize();
		/* Treat OK as Cancel */
		super.getOkButton().setActionCommand(controller.cancelDetailActionCommand);
		
	}
	@Override
	public void setData(InfoBase info){
		factuur = (FactuurTotaalInfo)info;
		int holdingid;
		holdings = factuurcontroller.getMaincontroller().getHoldings();
		if (factuur.getWerkgever() != null && factuur.getWerkgever().getFactureren()){
			/*
			 * Factureren op werkgever niveau. Dan is er geen sprake van een Holding factuur
			 */
			mntmAfdrukkenHolding.setEnabled(false);
			mntmVerzendenHolding.setEnabled(false);
			mntmAfdrukken.setEnabled(true);
			if (factuur.getWerkgever().getEmailadresfactuur() != null && !factuur.getWerkgever().getEmailadresfactuur().isEmpty()){
				mntmVerzenden.setEnabled(true);
			}else{
				mntmVerzenden.setEnabled(false);
			}
		} else {
			if (factuur.getHoldingid() == null){
				JOptionPane.showMessageDialog(this, "Holding is leeg!", "Error", JOptionPane.OK_OPTION);
				return;
			}
			holdingid = factuur.getHoldingid();
			holding = null;
			for (HoldingInfo hi: holdings){
				if (hi.getId() == holdingid){
					holding = hi;
				}
			}
			if (holding == null){
				JOptionPane.showMessageDialog(this, "Logic error: Holding niet gevonden!", "Error", JOptionPane.OK_OPTION);
				return;
			}
			if (holding.getFactuurtype().equals(__factuurtype.SEPARAAT)){
				mntmAfdrukken.setEnabled(true);
				mntmVerzenden.setEnabled(true);
			}else{
				mntmAfdrukken.setEnabled(false);
				mntmVerzenden.setEnabled(false);
			}
			mntmAfdrukkenHolding.setEnabled(true);
			if (holding.getEmailadresfactuur().isEmpty()){
				mntmVerzendenHolding.setEnabled(false);
			}else{
				mntmVerzendenHolding.setEnabled(true);
			}
		}
			
		
		displayFactuur();
	}
	
	private void initialize() {
		setBounds(0, 237, 527, 389);
		getContentPane().setLayout(null);
		
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		dtpAanmaakdatum = new DatePicker();
		dtpAanmaakdatum.setEnabled(false);
		dtpAanmaakdatum.setBounds(123, 28, 97, 21);
		dtpAanmaakdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpAanmaakdatum);
		
		JLabel lblAanmaakdatum = new JLabel("Aanmaakdatum");
		lblAanmaakdatum.setBounds(10, 31, 101, 14);
		getContentPane().add(lblAanmaakdatum);
		
		JLabel lblFactuurnummer = new JLabel("Factuurnummer");
		lblFactuurnummer.setBounds(10, 56, 95, 14);
		getContentPane().add(lblFactuurnummer);
		
		txtFactuurnummer = new JTextFieldTGI();
		txtFactuurnummer.setEditable(false);
		txtFactuurnummer.setColumns(10);
		txtFactuurnummer.setBounds(123, 51, 97, 20);
		getContentPane().add(txtFactuurnummer);
		
		
		JLabel lblStatus = new JLabel("Status");
		lblStatus.setBounds(10, 8, 65, 14);
		getContentPane().add(lblStatus);
		
		txtStatus = new JTextFieldTGI();
		txtStatus.setEditable(false);
		txtStatus.setBounds(123, 5, 135, 20);
		getContentPane().add(txtStatus);
		
		JLabel lblJaar = new JLabel("Jaar");
		lblJaar.setBounds(10, 77, 95, 14);
		getContentPane().add(lblJaar);
		
		txtJaar = new JTextFieldTGI();
		txtJaar.setEditable(false);
		txtJaar.setColumns(10);
		txtJaar.setBounds(123, 74, 56, 20);
		getContentPane().add(txtJaar);
		
		JLabel lblMaand = new JLabel("Maand");
		lblMaand.setBounds(187, 77, 46, 14);
		getContentPane().add(lblMaand);
		
		txtMaand = new JTextFieldTGI();
		txtMaand.setEditable(false);
		txtMaand.setColumns(10);
		txtMaand.setBounds(231, 74, 46, 20);
		getContentPane().add(txtMaand);
		
		JLabel lblNewLabel = new JLabel("Kilometerkosten");
		lblNewLabel.setBounds(10, 100, 95, 14);
		getContentPane().add(lblNewLabel);
		
		txtKilometerkosten = new JFormattedTextField(amountFormat);
		txtKilometerkosten.setEditable(false);
		txtKilometerkosten.setBounds(123, 97, 76, 20);
		getContentPane().add(txtKilometerkosten);
		txtKilometerkosten.setColumns(10);
		txtKilometerkosten.setValue(new BigDecimal(100));
		txtKilometerkosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblUurkosten = new JLabel("Uurkosten");
		lblUurkosten.setBounds(10, 123, 108, 14);
		getContentPane().add(lblUurkosten);
		
		txtUurkosten = new JFormattedTextField(amountFormat);
		txtUurkosten.setEditable(false);
		txtUurkosten.setBounds(123, 120, 76, 20);
		getContentPane().add(txtUurkosten);
		txtUurkosten.setColumns(10);
		txtUurkosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblKmtarief = new JLabel("Sociaalbezoek");
		lblKmtarief.setBounds(10, 166, 114, 14);
		getContentPane().add(lblKmtarief);
		
		txtSociaalbezoekkosten = new JFormattedTextField(amountFormat);
		txtSociaalbezoekkosten.setEditable(false);
		txtSociaalbezoekkosten.setBounds(123, 163, 76, 20);
		getContentPane().add(txtSociaalbezoekkosten);
		txtSociaalbezoekkosten.setColumns(10);
		txtSociaalbezoekkosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSecretariaatskosten = new JLabel("Secretariaat");
		lblSecretariaatskosten.setBounds(10, 189, 108, 14);
		getContentPane().add(lblSecretariaatskosten);
		
		txtSecretariaatskosten = new JFormattedTextField(amountFormat);
		txtSecretariaatskosten.setEditable(false);
		txtSecretariaatskosten.setBounds(123, 186, 76, 20);
		getContentPane().add(txtSecretariaatskosten);
		txtSecretariaatskosten.setColumns(10);
		txtSecretariaatskosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblCasemanagement = new JLabel("Overige kosten");
		lblCasemanagement.setBounds(10, 212, 108, 14);
		getContentPane().add(lblCasemanagement);
		
		txtOverigekosten = new JFormattedTextField(amountFormat);
		txtOverigekosten.setEditable(false);
		txtOverigekosten.setBounds(123, 209, 76, 20);
		getContentPane().add(txtOverigekosten);
		txtOverigekosten.setColumns(10);
		txtOverigekosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblLossePosten = new JLabel("Losse posten");
		lblLossePosten.setBounds(10, 235, 95, 14);
		getContentPane().add(lblLossePosten);
		
		txtLosseposten = new JFormattedTextField(amountFormat);
		txtLosseposten.setEditable(false);
		txtLosseposten.setBounds(123, 232, 76, 20);
		getContentPane().add(txtLosseposten);
		txtLosseposten.setColumns(10);
		txtLosseposten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblZelfdeDag = new JLabel("Abonnementskosten");
		lblZelfdeDag.setBounds(241, 100, 108, 14);
		getContentPane().add(lblZelfdeDag);
		
		txtAbonnementskosten = new JFormattedTextField(amountFormat);
		txtAbonnementskosten.setEditable(false);
		txtAbonnementskosten.setBounds(354, 97, 76, 20);
		getContentPane().add(txtAbonnementskosten);
		txtAbonnementskosten.setColumns(10);
		txtAbonnementskosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblZaterdag = new JLabel("Aansluitkosten");
		lblZaterdag.setBounds(241, 123, 108, 14);
		getContentPane().add(lblZaterdag);
		
		txtAansluitkosten = new JFormattedTextField(amountFormat);
		txtAansluitkosten.setEditable(false);
		txtAansluitkosten.setBounds(354, 120, 76, 20);
		getContentPane().add(txtAansluitkosten);
		txtAansluitkosten.setColumns(10);
		txtAansluitkosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSpoed = new JLabel("Correctiebedrag");
		lblSpoed.setBounds(241, 146, 108, 14);
		getContentPane().add(lblSpoed);
		
		txtCorrectiebedrag = new JFormattedTextField(amountFormat);
		txtCorrectiebedrag.setEditable(false);
		txtCorrectiebedrag.setBounds(354, 143, 76, 20);
		getContentPane().add(txtCorrectiebedrag);
		txtCorrectiebedrag.setColumns(10);
		txtCorrectiebedrag.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSpoedAfgesprokenTijd = new JLabel("Maandbedrag secr.");
		lblSpoedAfgesprokenTijd.setBounds(241, 169, 108, 14);
		getContentPane().add(lblSpoedAfgesprokenTijd);
		
		txtmaandkostenSecretariaat = new JFormattedTextField(amountFormat);
		txtmaandkostenSecretariaat.setEditable(false);
		txtmaandkostenSecretariaat.setBounds(354, 166, 76, 20);
		getContentPane().add(txtmaandkostenSecretariaat);
		txtmaandkostenSecretariaat.setColumns(10);
		txtmaandkostenSecretariaat.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblTelefonischeControle = new JLabel("Totaalbedrag");
		lblTelefonischeControle.setBounds(10, 285, 108, 14);
		getContentPane().add(lblTelefonischeControle);
		
		txtTotaalbedrag = new JFormattedTextField(amountFormat);
		txtTotaalbedrag.setEditable(false);
		txtTotaalbedrag.setBounds(123, 282, 76, 20);
		getContentPane().add(txtTotaalbedrag);
		txtTotaalbedrag.setColumns(10);
		txtTotaalbedrag.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSecrKostenPm = new JLabel("BTW laag");
		lblSecrKostenPm.setBounds(241, 192, 108, 14);
		getContentPane().add(lblSecrKostenPm);
		
		txtBtwlaag = new JFormattedTextField(amountFormat);
		txtBtwlaag.setEditable(false);
		txtBtwlaag.setBounds(354, 189, 76, 20);
		getContentPane().add(txtBtwlaag);
		txtBtwlaag.setColumns(10);
		txtBtwlaag.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblBtwHoog = new JLabel("BTW hoog");
		lblBtwHoog.setBounds(241, 215, 108, 14);
		getContentPane().add(lblBtwHoog);
		
		txtBtwhoog = new JFormattedTextField(amountFormat);
		txtBtwhoog.setEditable(false);
		txtBtwhoog.setColumns(10);
		txtBtwhoog.setBounds(354, 212, 76, 20);
		txtBtwhoog.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(txtBtwhoog);
		
		JButton btnDetails = new JButton("Details...");
		btnDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDetailClicked(e);
			}
		});
		btnDetails.setBounds(241, 281, 97, 23);
		getContentPane().add(btnDetails);
		
		btnAfdrukken = new JButton("Afdr./Verz.");
		btnAfdrukken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAfdrukkenClicked(e);
			}
		});
		btnAfdrukken.setBounds(341, 281, 89, 23);
		getContentPane().add(btnAfdrukken);
		
		popupMenu = new JPopupMenu();
		addPopup(btnAfdrukken, popupMenu);
		
		mntmAfdrukken = new JMenuItem("Afdrukken");
		mntmAfdrukken.setIcon(null);
		mntmAfdrukken.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				factuurcontroller.printWerkgeverFactuur(factuur);
			}
		});
		popupMenu.add(mntmAfdrukken);
		
		mntmAfdrukkenHolding = new JMenuItem("Afdrukken holding factuur");
		mntmAfdrukkenHolding.setIcon(null);
		mntmAfdrukkenHolding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				factuurcontroller.printHoldingFactuur(factuur);
			}
		});
		popupMenu.add(mntmAfdrukkenHolding);
		
		mntmVerzenden = new JMenuItem("Verzenden");
		mntmVerzenden.setIcon(null);
		mntmVerzenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				factuurcontroller.emailWerkgeverFactuur(factuur);
			}
		});
		popupMenu.add(mntmVerzenden);

		mntmVerzendenHolding = new JMenuItem("Verzenden holding factuur");
		mntmVerzendenHolding.setIcon(null);
		mntmVerzendenHolding.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				factuurcontroller.emailHoldingFactuur(factuur);
			}
		});
		popupMenu.add(mntmVerzendenHolding);
		
		JLabel lblVastekosten = new JLabel("Vastekosten");
		lblVastekosten.setBounds(10, 144, 108, 14);
		getContentPane().add(lblVastekosten);
		
		txtVastekosten = new JFormattedTextField(amountFormat);
		txtVastekosten.setEditable(false);
		txtVastekosten.setColumns(10);
		txtVastekosten.setBounds(123, 141, 76, 20);
		txtVastekosten.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(txtVastekosten);
		
	}
	protected void btnAfdrukkenClicked(ActionEvent e) {
		popupMenu.show(btnAfdrukken, btnAfdrukken.getWidth()/2, btnAfdrukken.getHeight()/2);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	protected void btnDetailClicked(ActionEvent ae) {

		try {
			if (this.factuur.getPdflocation() == null || this.factuur.getPdflocation().isEmpty())
				reportcontroller.printFactuur(factuur);
			else{
				URI uri = new URI("File://"
						+ URLEncoder.encode(this.factuur.getPdflocation(), "UTF-8"));
				factuurcontroller.open(uri);
			}
		} catch (Exception e) {
			ExceptionLogger.ProcessException(e, this, true);
		}
	}
	private void displayFactuur() {
		try {
			dtpAanmaakdatum.setDate(factuur.getAanmaakdatum());
			
			txtStatus.setText(factuur.getFactuurstatus().toString());
			txtJaar.setText(factuur.getJaar().toString());
			txtMaand.setText(factuur.getMaand().toString());
			
			txtAansluitkosten.setValue(factuur.getAansluitkosten());
			txtLosseposten.setValue(factuur.getSomitembedrag());
			txtBtwlaag.setValue(factuur.getBtwpercentagelaag());
			txtBtwhoog.setValue(factuur.getBtwpercentagehoog());
			txtmaandkostenSecretariaat.setValue(factuur.getMaandbedragsecretariaat());
			txtAbonnementskosten.setValue(factuur.getAbonnementskosten());
			txtCorrectiebedrag.setValue(BigDecimal.ZERO);
			txtSociaalbezoekkosten.setValue(factuur.getSomcasemanagementkosten());
			txtOverigekosten.setValue(factuur.getSomoverigekostenbezoek());
			txtSecretariaatskosten.setValue(factuur.getSomsecretariaatskosten());
			txtKilometerkosten.setValue(factuur.getSomkilometerkosten());
			txtUurkosten.setValue(factuur.getSomuurkosten());
			txtVastekosten.setValue(factuur.getSomvastekosten());
			
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e,this);
		}
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
