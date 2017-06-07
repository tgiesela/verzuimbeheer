package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.components.AdresPanel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.components.ContactpersoonPanel;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class HoldingDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Create the frame.
	 */
	private JTextFieldTGI txtNaam;
	private DatePicker dtpStartdate; 
	private DatePicker dtpEnddate;
	private AdresPanel VestigingsadresPanel;
	private AdresPanel PostadresPanel;
	private AdresPanel FactuuradresPanel;
	private ContactpersoonPanel contactpersoonPanel;
	private ContactpersoonPanel contactpersoonfactuurPanel;
	private HoldingInfo holding;
	private JLabel lblTelefoon;
	private JTextFieldTGI txtTelefoon;
	private JTextFieldTGI txtBTWNummer;
	private JTextFieldTGI txtDebNr;
	private JCheckBox chckbxFactureren;
	private JCheckBox chckbxDetailsSecretariaat;
	private boolean accesstotarieven = false;
	private JTextFieldTGI txtEmailadresfactuur;
	private JPanel panelFactureren;
	private JButton btnTarieven;
	private JLabel lblBtwNummer;
	private JComboBox<TypeEntry> cmbFactuurtype;	
	
	public HoldingDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Holding",mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){

		holding = (HoldingInfo) info;

		try {
			accesstotarieven = ServiceCaller.autorisatieFacade(getLoginSession()).isAuthorised(getLoginSession().getGebruiker(), __applicatiefunctie.RAADPLEGENTARIEVEN);
		} catch (ServiceLocatorException | PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		}
		
		DefaultComboBoxModel<TypeEntry> factuurtypeModel = new DefaultComboBoxModel<TypeEntry>();
		cmbFactuurtype.setModel(factuurtypeModel);
		for (__factuurtype g : __factuurtype.values()) {
			TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
			factuurtypeModel.addElement(soort);
		}
		for (int i = 0; i < factuurtypeModel.getSize(); i++) {
			TypeEntry factuurtype = (TypeEntry) factuurtypeModel.getElementAt(i);
			if (__factuurtype.parse(factuurtype.getValue()) == holding
					.getFactuurtype()) {
				factuurtypeModel.setSelectedItem(factuurtype);
				break;
			}
		}

		
		txtNaam.setText(holding.getNaam());
		txtTelefoon.setText(holding.getTelefoonnummer());

		try {
			dtpStartdate.setDate(holding.getStartdatumcontract());
			dtpEnddate.setDate(holding.getEinddatumcontract());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}

		
		PostadresPanel.setAdres(holding.getPostAdres());
		VestigingsadresPanel.setAdres(holding.getVestigingsAdres());
		FactuuradresPanel.setAdres(holding.getFactuurAdres());
		contactpersoonPanel.setContactpersoon(holding.getContactpersoon());
		contactpersoonfactuurPanel.setContactpersoon(holding.getContactpersoonfactuur());
		
		chckbxDetailsSecretariaat.setSelected(holding.isDetailsecretariaat());
		chckbxFactureren.setSelected(holding.isFactureren());
		txtBTWNummer.setText(holding.getBtwnummer());
		txtBTWNummer.setText(holding.getBtwnummer());
		if (holding.getDebiteurnummer() == null)
			txtDebNr.setText("");
		else
			txtDebNr.setText(holding.getDebiteurnummer().toString());
		if (accesstotarieven){
			btnTarieven.setEnabled(true);
			chckbxFactureren.setEnabled(true);
			chckbxDetailsSecretariaat.setEnabled(true);
		} else {
			btnTarieven.setEnabled(false);
			chckbxFactureren.setEnabled(false);
			chckbxDetailsSecretariaat.setEnabled(false);
		}
		txtEmailadresfactuur.setText(holding.getEmailadresfactuur());
		
		activateListener();
	}

	void initialize() {
		setBounds(100, 100, 701, 649);
		JLabel lblNaam = new JLabel("Naam");
		lblNaam.setBounds(29, 21, 46, 14);
		getContentPane().add(lblNaam);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(128, 17, 300, 20);
		getContentPane().add(txtNaam);
		
		VestigingsadresPanel = new AdresPanel();
		VestigingsadresPanel.setAdresSoort("Vestigingsadres");
		VestigingsadresPanel.setBounds(29, 68, 408, 93);
		getContentPane().add(VestigingsadresPanel);
		
		PostadresPanel = new AdresPanel();
		PostadresPanel.setAdresSoort("Postadres");
		PostadresPanel.setBounds(29, 159, 409, 93);
		getContentPane().add(PostadresPanel);
		
		FactuuradresPanel = new AdresPanel();
		FactuuradresPanel.setAdresSoort("Factuuradres");
		FactuuradresPanel.setBounds(29, 254, 409, 93);
		getContentPane().add(FactuuradresPanel);

		lblTelefoon = new JLabel("Telefoon");
		lblTelefoon.setBounds(29, 43, 46, 14);
		getContentPane().add(lblTelefoon);
		
		txtTelefoon = new JTextFieldTGI();
		txtTelefoon.setBounds(128, 40, 112, 20);
		getContentPane().add(txtTelefoon);
		
		contactpersoonPanel = new ContactpersoonPanel();
		contactpersoonPanel.setBounds(29, 346, 408, 112);
		getContentPane().add(contactpersoonPanel);
			
		JLabel label = new JLabel("Deb. nummer");
		label.setBounds(448, 21, 69, 14);
		getContentPane().add(label);
		
		txtDebNr = new JTextFieldTGI();
		txtDebNr.setEnabled(false);
		txtDebNr.setColumns(10);
		txtDebNr.setBounds(516, 17, 112, 20);
		getContentPane().add(txtDebNr);
		
		panelFactureren = new JPanel();
		panelFactureren.setLayout(null);
		panelFactureren.setBorder(new TitledBorder(null, "Facturatie", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelFactureren.setBounds(447, 74, 224, 118);
		getContentPane().add(panelFactureren);
		
		chckbxFactureren = new JCheckBox("Factureren");
		chckbxFactureren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				facturerenClicked(arg0);
			}
		});
		chckbxFactureren .setBounds(6, 16, 137, 20);
		panelFactureren.add(chckbxFactureren );
		
		chckbxDetailsSecretariaat = new JCheckBox("Gedetailleerde secr. kosten");
		chckbxDetailsSecretariaat.setBounds(6, 37, 169, 20);
		panelFactureren.add(chckbxDetailsSecretariaat);
		
		txtEmailadresfactuur = new JTextFieldTGI();
		txtEmailadresfactuur.setColumns(10);
		txtEmailadresfactuur.setBounds(47, 87, 167, 20);
		panelFactureren.add(txtEmailadresfactuur);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(10, 90, 29, 14);
		panelFactureren.add(lblEmail);
		
		cmbFactuurtype = new JComboBox<TypeEntry>();
		cmbFactuurtype.setBounds(6, 59, 210, 20);
		panelFactureren.add(cmbFactuurtype);
		
		btnTarieven = new JButton("Tarieven");
		btnTarieven.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTarievenClicked(e);
			}
		});
		btnTarieven.setBounds(568, 203, 103, 23);
		getContentPane().add(btnTarieven);
		
		txtBTWNummer = new JTextFieldTGI();
		txtBTWNummer.setColumns(10);
		txtBTWNummer.setBounds(516, 40, 112, 20);
		getContentPane().add(txtBTWNummer);
		
		lblBtwNummer = new JLabel("BTW nummer");
		lblBtwNummer.setBounds(448, 43, 69, 14);
		getContentPane().add(lblBtwNummer);
		
		contactpersoonfactuurPanel = new ContactpersoonPanel();
		contactpersoonfactuurPanel.setBounds(29, 456, 408, 122);
		contactpersoonfactuurPanel.setTitle("Contactpersoon Factuur");
		getContentPane().add(contactpersoonfactuurPanel);

		dtpStartdate = new DatePicker();
		dtpStartdate.setBounds(570, 254, 103, 21);
		dtpStartdate.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdate);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setBounds(467, 257, 69, 14);
		getContentPane().add(lblIngangsdatum);
		
		JLabel lblBeeindigingsdatum = new JLabel("Beeindigingsdatum");
		lblBeeindigingsdatum.setBounds(467, 281, 96, 14);
		getContentPane().add(lblBeeindigingsdatum);
		
		dtpEnddate = new DatePicker();
		dtpEnddate.setBounds(570, 281, 103, 21);
		dtpEnddate.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEnddate);
		try {
			dtpEnddate.setDate(null);
			
			JButton btnWerkgevers = new JButton("Werkgevers");
			btnWerkgevers.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnWerkgeversClicked(e);
				}
			});
			btnWerkgevers.setBounds(568, 324, 103, 23);
			getContentPane().add(btnWerkgevers);
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}

	}
	protected void facturerenClicked(ActionEvent arg0) {
		if (chckbxFactureren.isSelected()){
			chckbxDetailsSecretariaat.setEnabled(true);
			cmbFactuurtype.setEnabled(true);
			txtEmailadresfactuur.setEnabled(true);
		}else{
			chckbxDetailsSecretariaat.setEnabled(false);
			cmbFactuurtype.setEnabled(false);
			txtEmailadresfactuur.setEnabled(false);
		}
	}
	protected void btnTarievenClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		TariefOverzicht dlgTariefOverzicht = new TariefOverzicht(this.getMdiPanel());
		dlgTariefOverzicht.setLoginSession(this.getLoginSession());
		dlgTariefOverzicht.setParentInfo(holding);
		dlgTariefOverzicht.setVisible(true);
		dlgTariefOverzicht.ReloadTable();
		mdiPanel.add(dlgTariefOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgTariefOverzicht);
	}
	protected void btnWerkgeversClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		WerkgeverOverzicht dlgWerkgevers = new WerkgeverOverzicht(this.getMdiPanel());
		dlgWerkgevers.setLoginSession(this.getLoginSession());
		dlgWerkgevers.setParentInfo(holding);
		dlgWerkgevers.populateTable();
		dlgWerkgevers.setVisible(true);
		mdiPanel.add(dlgWerkgevers);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerkgevers);
	}
	
	protected void okButtonClicked(ActionEvent e) {
		holding.setNaam(txtNaam.getText());
		holding.setTelefoonnummer(txtTelefoon.getText());
        
        holding.setPostAdres(PostadresPanel.getAdres());
        holding.setVestigingsAdres(VestigingsadresPanel.getAdres());
        holding.setFactuurAdres(FactuuradresPanel.getAdres());
        holding.setContactpersoon(contactpersoonPanel.getContactpersoon());
        holding.setContactpersoonfactuur(contactpersoonfactuurPanel.getContactpersoon());

       	holding.setFactureren(chckbxFactureren.isSelected());
       	holding.setDetailsecretariaat(chckbxDetailsSecretariaat.isSelected());
       	holding.setBtwnummer(txtBTWNummer.getText().trim());
       	holding.setEmailadresfactuur(txtEmailadresfactuur.getText());
       	TypeEntry factuurtype = (TypeEntry) cmbFactuurtype.getSelectedItem();
       	holding.setFactuurtype(__factuurtype.parse(factuurtype.getValue()));
        holding.setStartdatumcontract(dtpStartdate.getDate());
        holding.setEinddatumcontract(dtpEnddate.getDate());
        if (this.getLoginSession() != null)
        {
        	try {
	    		holding.validate();
	    		switch (this.getMode()){
	    			case New: 
	    				ServiceCaller.werkgeverFacade(getLoginSession()).addHolding(holding);
	    				break;
	    			case Update: 
	    				ServiceCaller.werkgeverFacade(getLoginSession()).updateHolding(holding);
	    				break;
	    			case Delete: 
	    				ServiceCaller.werkgeverFacade(getLoginSession()).deleteHolding(holding);
	    				break;
	    		}
	    		super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
	        	return;
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
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
}
