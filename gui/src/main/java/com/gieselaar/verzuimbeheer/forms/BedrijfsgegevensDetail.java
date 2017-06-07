package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.components.AdresPanel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class BedrijfsgegevensDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Create the frame.
	 */
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
	public BedrijfsgegevensDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Bedrijfsgegevens",mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){

		try {
			if (info == null){
				bedrijfsgegevens = ServiceCaller.instantieFacade(getLoginSession()).allBedrijfsgegevens().get(0);
				if (bedrijfsgegevens == null){
					bedrijfsgegevens = new BedrijfsgegevensInfo();
					this.setMode(formMode.New);
				} else
					this.setMode(formMode.Update);
			}
			else
				bedrijfsgegevens = (BedrijfsgegevensInfo) info;
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
		
		activateListener();
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
	protected void okButtonClicked(ActionEvent e) {
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
        if (this.getLoginSession() != null)
        {
        	try {
	    		bedrijfsgegevens.validate();
	    		switch (this.getMode()){
	    			case New: 
	    				ServiceCaller.instantieFacade(getLoginSession()).addBedrijfsgegevens(bedrijfsgegevens);
	    				break;
	    			case Update: 
	    				ServiceCaller.instantieFacade(getLoginSession()).updateBedrijfsgegevens(bedrijfsgegevens);
	    				break;
	    			case Delete: 
	    				ServiceCaller.instantieFacade(getLoginSession()).deleteBedrijfsgegevens(bedrijfsgegevens);
	    				break;
	    		}
	    		super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
}
