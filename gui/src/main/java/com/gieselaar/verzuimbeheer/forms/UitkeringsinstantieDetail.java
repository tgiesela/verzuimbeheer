package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.components.AdresPanel;

public class UitkeringsinstantieDetail extends BaseDetailform {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtNaam;
	private AdresPanel VestigingsadresPanel;
	private AdresPanel PostadresPanel;
	private UitvoeringsinstituutInfo uitkeringsinstantie;
	private JLabel lblTelefoon;
	private JTextFieldTGI txtTelefoon;
	/**
	 * Create the frame.
	 */
	public UitkeringsinstantieDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer arbodienst", mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){
		uitkeringsinstantie = (UitvoeringsinstituutInfo) info;
		if (uitkeringsinstantie.getState() == persistencestate.EXISTS){
			try {
				uitkeringsinstantie = ServiceCaller.instantieFacade(getLoginSession()).getUitkeringsinstantie(uitkeringsinstantie.getId());
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
		txtNaam.setText(uitkeringsinstantie.getNaam());
		txtTelefoon.setText(uitkeringsinstantie.getTelefoonnummer());

		PostadresPanel.setAdres(uitkeringsinstantie.getPostadres());
		VestigingsadresPanel.setAdres(uitkeringsinstantie.getVestigingsadres());
		activateListener();
	}

	void initialize() {
		setBounds(100, 100, 476, 403);
		JLabel lblNaam = new JLabel("Naam");
		lblNaam.setBounds(29, 21, 46, 14);
		getContentPane().add(lblNaam);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(128, 17, 300, 20);
		getContentPane().add(txtNaam);
		
		VestigingsadresPanel = new AdresPanel();
		VestigingsadresPanel.setAdresSoort("Vestigingsadres");
		VestigingsadresPanel.setBounds(30, 94, 408, 93);
		getContentPane().add(VestigingsadresPanel);
		
		PostadresPanel = new AdresPanel();
		PostadresPanel.setAdresSoort("Postadres");
		PostadresPanel.setBounds(29, 201, 409, 93);
		getContentPane().add(PostadresPanel);
		
		lblTelefoon = new JLabel("Telefoon");
		lblTelefoon.setBounds(29, 43, 46, 14);
		getContentPane().add(lblTelefoon);
		
		txtTelefoon = new JTextFieldTGI();
		txtTelefoon.setBounds(128, 40, 112, 20);
		getContentPane().add(txtTelefoon);

	}
	protected void okButtonClicked(ActionEvent e) {
		uitkeringsinstantie.setNaam(txtNaam.getText());
		uitkeringsinstantie.setTelefoonnummer(txtTelefoon.getText());
        
        uitkeringsinstantie.setPostadres(PostadresPanel.getAdres());
        uitkeringsinstantie.setVestigingsadres(VestigingsadresPanel.getAdres());
        if (this.getLoginSession() != null)
        {
        	try {
	    		uitkeringsinstantie.validate();
	    		switch (this.getMode()){
	    			case New: 
	    				ServiceCaller.instantieFacade(getLoginSession()).addUitkeringsinstantie(uitkeringsinstantie);
	    				break;
	    			case Update: 
	    				ServiceCaller.instantieFacade(getLoginSession()).updateuitkeringsinstantie(uitkeringsinstantie);
	    				break;
	    			case Delete: 
	    				ServiceCaller.instantieFacade(getLoginSession()).deleteUitkeringsinstantie(uitkeringsinstantie);
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
