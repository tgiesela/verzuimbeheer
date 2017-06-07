package com.gieselaar.verzuim.views;

import javax.swing.JLabel;

import com.gieselaar.verzuim.components.AdresPanel;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;

public class BedrijfsgegevensDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtNaam;
	private AdresPanel VestigingsadresPanel;
	private AdresPanel PostadresPanel;
	private BedrijfsgegevensInfo bedrijfsgegevens;
	private JLabel lblTelefoon;
	private JTextFieldTGI txtTelefoon;
	private JTextFieldTGI txtMobiel;
	private JTextFieldTGI txtFax;
	private JTextFieldTGI txtEmail;
	private JTextFieldTGI txtWebsite;
	private JTextFieldTGI txtKvknummer;
	private JTextFieldTGI txtBankrekening;
	private JTextFieldTGI txtBTWnummer;	
	/**
	 * Create the frame.
	 */
	public BedrijfsgegevensDetail(AbstractController controller) {
		super("Beheer Bedrijfsgegevens",controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		bedrijfsgegevens = (BedrijfsgegevensInfo) info;
		displayBedrijfsgegevens();
	}
	private void displayBedrijfsgegevens(){
		txtNaam.setText(bedrijfsgegevens.getNaam());
		txtTelefoon.setText(bedrijfsgegevens.getTelefoonnummer());
		txtMobiel.setText(bedrijfsgegevens.getMobiel());
		txtFax.setText(bedrijfsgegevens.getFax());
		txtWebsite.setText(bedrijfsgegevens.getWebsite());
		txtKvknummer.setText(bedrijfsgegevens.getKvknr());
		txtBankrekening.setText(bedrijfsgegevens.getBankrekening());
		txtBTWnummer.setText(bedrijfsgegevens.getBtwnummer());

		PostadresPanel.setAdres(bedrijfsgegevens.getPostAdres());
		VestigingsadresPanel.setAdres(bedrijfsgegevens.getVestigingsAdres());
	}

	void initialize() {
		setBounds(100, 100, 476, 515);
		JLabel lblNaam = new JLabel("Naam");
		lblNaam.setBounds(29, 21, 46, 14);
		getContentPane().add(lblNaam);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(128, 17, 300, 20);
		getContentPane().add(txtNaam);
		
		VestigingsadresPanel = new AdresPanel();
		VestigingsadresPanel.setAdresSoort("Vestigingsadres");
		VestigingsadresPanel.setBounds(30, 244, 408, 93);
		getContentPane().add(VestigingsadresPanel);
		
		PostadresPanel = new AdresPanel();
		PostadresPanel.setAdresSoort("Postadres");
		PostadresPanel.setBounds(29, 351, 409, 93);
		getContentPane().add(PostadresPanel);
		
		lblTelefoon = new JLabel("Telefoon");
		lblTelefoon.setBounds(29, 43, 46, 14);
		getContentPane().add(lblTelefoon);
		
		txtTelefoon = new JTextFieldTGI();
		txtTelefoon.setBounds(128, 40, 112, 20);
		getContentPane().add(txtTelefoon);
		
		JLabel lblMobiel = new JLabel("Mobiel");
		lblMobiel.setBounds(29, 66, 46, 14);
		getContentPane().add(lblMobiel);

		txtMobiel = new JTextFieldTGI();
		txtMobiel.setBounds(128, 63, 112, 20);
		getContentPane().add(txtMobiel);
		
		JLabel lblFax = new JLabel("Fax");
		lblFax.setBounds(29, 89, 46, 14);
		getContentPane().add(lblFax);
		
		txtFax = new JTextFieldTGI();
		txtFax.setBounds(128, 86, 112, 20);
		getContentPane().add(txtFax);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(29, 111, 46, 14);
		getContentPane().add(lblEmail);
		
		txtEmail = new JTextFieldTGI();
		txtEmail.setBounds(128, 108, 300, 20);
		getContentPane().add(txtEmail);
		
		JLabel lblWebsite = new JLabel("Website");
		lblWebsite.setBounds(29, 135, 46, 14);
		getContentPane().add(lblWebsite);
		
		txtWebsite = new JTextFieldTGI();
		txtWebsite.setBounds(128, 132, 300, 20);
		getContentPane().add(txtWebsite);
		
		JLabel lblKvkNr = new JLabel("KvK nr");
		lblKvkNr.setBounds(29, 158, 46, 14);
		getContentPane().add(lblKvkNr);
		
		txtKvknummer = new JTextFieldTGI();
		txtKvknummer.setBounds(128, 155, 300, 20);
		getContentPane().add(txtKvknummer);
		
		JLabel lblBankrekning = new JLabel("Bankrekening");
		lblBankrekning.setBounds(29, 181, 76, 14);
		getContentPane().add(lblBankrekning);
		
		txtBankrekening = new JTextFieldTGI();
		txtBankrekening.setBounds(128, 178, 155, 20);
		getContentPane().add(txtBankrekening);
		
		JLabel lblBtwNummer = new JLabel("BTW nummer");
		lblBtwNummer.setBounds(29, 205, 89, 14);
		getContentPane().add(lblBtwNummer);
		
		txtBTWnummer = new JTextFieldTGI();
		txtBTWnummer.setBounds(128, 202, 112, 20);
		getContentPane().add(txtBTWnummer);
		
	}
	@Override
	public InfoBase collectData(){
		bedrijfsgegevens.setNaam(txtNaam.getText());
		bedrijfsgegevens.setTelefoonnummer(txtTelefoon.getText());
		bedrijfsgegevens.setMobiel(txtMobiel.getText());
		bedrijfsgegevens.setFax(txtFax.getText());
		bedrijfsgegevens.setEmailadres(txtEmail.getText());
		bedrijfsgegevens.setWebsite(txtWebsite.getText());
		bedrijfsgegevens.setKvknr(txtKvknummer.getText());
		bedrijfsgegevens.setBankrekening(txtBankrekening.getText());
		bedrijfsgegevens.setBtwnummer(txtBTWnummer.getText());
        
        bedrijfsgegevens.setPostAdres(PostadresPanel.getAdres());
        bedrijfsgegevens.setVestigingsAdres(VestigingsadresPanel.getAdres());
        return bedrijfsgegevens;
	}
}
