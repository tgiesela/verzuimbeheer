package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.AdresPanel;
import com.gieselaar.verzuimbeheer.components.ContactpersoonPanel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;

import javax.swing.JButton;

import java.awt.event.ActionListener;

public class ArbodienstDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArbodienstInfo arbodienst;
	private AdresPanel VestigingsadresPanel;
	private AdresPanel PostadresPanel;
	private ContactpersoonPanel contactpersoonPanel;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtTelefoonnummer;
	private JLabel lblTelefoonnr;
	private JButton btnArtsen;
	/**
	 * Create the frame.
	 */
	public ArbodienstDetail(JDesktopPaneTGI mdiPanel) {
		super("Arbodienst", mdiPanel);
		
		initialize();
	}
	public void setInfo(InfoBase info){

		if (this.getMode() == formMode.New)
			btnArtsen.setEnabled(false);
		arbodienst = (ArbodienstInfo) info;
		if (arbodienst.getState() == persistencestate.EXISTS){
			try {
				arbodienst = ServiceCaller.instantieFacade(getLoginSession()).getArbodienst(arbodienst.getId());
			} catch (PermissionException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
	        	return;
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
	        	return;
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
	        	return;
			}// Get all the details
		}
		txtNaam.setText(arbodienst.getNaam());
		txtTelefoonnummer.setText(arbodienst.getTelefoonnummer());

		PostadresPanel.setAdres(arbodienst.getPostAdres());
		VestigingsadresPanel.setAdres(arbodienst.getVestigingsAdres());
		contactpersoonPanel.setContactpersoon(arbodienst.getContactpersoon());
		activateListener();
	}

	void initialize(){
		setBounds(100, 100, 543, 500);
		VestigingsadresPanel = new AdresPanel();
		VestigingsadresPanel.setAdresSoort("Vestigingsadres");
		VestigingsadresPanel.setBounds(44, 86, 445, 95);
		getContentPane().add(VestigingsadresPanel);
		
		PostadresPanel = new AdresPanel();
		PostadresPanel.setAdresSoort("Postadres");
		PostadresPanel.setBounds(44, 192, 446, 95);
		getContentPane().add(PostadresPanel);
		
		contactpersoonPanel = new ContactpersoonPanel();
		contactpersoonPanel.setBounds(44, 298, 446, 107);
		getContentPane().add(contactpersoonPanel);
		
		JLabel lblNaam = new JLabel("Naam");
		lblNaam.setBounds(44, 26, 46, 14);
		getContentPane().add(lblNaam);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(144, 23, 196, 20);
		getContentPane().add(txtNaam);
		
		lblTelefoonnr = new JLabel("Alg. Telefoonnr");
		lblTelefoonnr.setBounds(44, 49, 90, 14);
		getContentPane().add(lblTelefoonnr);
		
		txtTelefoonnummer = new JTextFieldTGI();
		txtTelefoonnummer.setBounds(144, 46, 135, 20);
		getContentPane().add(txtTelefoonnummer);
		
		btnArtsen = new JButton("Bedrijfsartsen...");
		btnArtsen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnArtsenClicked(e);
			}
		});
		btnArtsen.setBounds(360, 22, 129, 23);
		getContentPane().add(btnArtsen);

	}
	protected void btnArtsenClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		BedrijfsartsenOverzicht dlgBedrijfsartsen = new BedrijfsartsenOverzicht(this.getMdiPanel());
		dlgBedrijfsartsen.setLoginSession(this.getLoginSession());
		dlgBedrijfsartsen.setParentInfo(arbodienst);
		dlgBedrijfsartsen.populateTable();
		dlgBedrijfsartsen.setVisible(true);
		mdiPanel.add(dlgBedrijfsartsen);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgBedrijfsartsen);
	}
	protected void okButtonClicked(ActionEvent e) {
		arbodienst.setNaam(txtNaam.getText());
		arbodienst.setTelefoonnummer(txtTelefoonnummer.getText());
        
        arbodienst.setPostAdres(PostadresPanel.getAdres());
        arbodienst.setVestigingsAdres(VestigingsadresPanel.getAdres());
        arbodienst.setContactpersoon(contactpersoonPanel.getContactpersoon());
        if (this.getLoginSession() != null)
        {
        	try {
	    		arbodienst.validate();
	    		switch (this.getMode()){
	    			case New: 
	    				ServiceCaller.instantieFacade(getLoginSession()).addArbodienst(arbodienst);
	    				break;
	    			case Update: 
	    				ServiceCaller.instantieFacade(getLoginSession()).updateArbodienst(arbodienst);
	    				break;
	    			case Delete: 
	    				ServiceCaller.instantieFacade(getLoginSession()).deleteArbodienst(arbodienst);
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
