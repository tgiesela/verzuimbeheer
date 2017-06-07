package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;

import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JComboBox;

public class BedrijfsartsDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoornaam ;
	private JComboBox<TypeEntry> cmbGeslacht ;
	private JTextFieldTGI txtVoorletters ;
	private JTextFieldTGI txtTelefoon ;
	private JTextFieldTGI txtEmail;
	private JTextFieldTGI txtArbodienst;
	
	private BedrijfsartsInfo bedrijfsarts = null;
	private ArbodienstInfo arbodienst = null;

	/**
	 * Create the frame.
	 */
	public BedrijfsartsDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Werkgever",mdiPanel);
		
		initialize();
	}
	public void setInfo(InfoBase info){
		DefaultComboBoxModel<TypeEntry> geslachtModel;
		bedrijfsarts = (BedrijfsartsInfo) info;
		try {
			if (bedrijfsarts.getState() == persistencestate.EXISTS)
				bedrijfsarts = ServiceCaller.instantieFacade(getLoginSession()).getBedrijfsarts(bedrijfsarts.getId());
			else
				bedrijfsarts.setGeslacht(__geslacht.MAN);
			arbodienst = ServiceCaller.instantieFacade(getLoginSession()).getArbodienst(bedrijfsarts.getArbodienstId());
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		geslachtModel = new DefaultComboBoxModel<TypeEntry>();
        cmbGeslacht.setModel(geslachtModel);
        for (__geslacht g: __geslacht.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	geslachtModel.addElement(soort);
        	if (g == bedrijfsarts.getGeslacht()){
        		geslachtModel.setSelectedItem(soort);
        	}
        }

		txtAchternaam.setText(bedrijfsarts.getAchternaam());
		txtArbodienst.setText(arbodienst.getNaam());
		txtEmail.setText(bedrijfsarts.getEmail());
		txtTelefoon.setText(bedrijfsarts.getTelefoon());
		txtVoorletters.setText(bedrijfsarts.getVoorletters());
		txtVoornaam.setText(bedrijfsarts.getVoornaam());
		
		activateListener();
	}
	
	void initialize(){
		setBounds(100, 100, 450, 300);
		JLabel lblAchternaam = new JLabel("Achternaam");
		lblAchternaam.setBounds(10, 39, 67, 14);
		getContentPane().add(lblAchternaam);

		txtAchternaam = new JTextFieldTGI();
		txtAchternaam.setBounds(87, 36, 204, 20);
		getContentPane().add(txtAchternaam);
		
		JLabel lblVoornaam = new JLabel("Voornaam");
		lblVoornaam.setBounds(10, 62, 57, 14);
		getContentPane().add(lblVoornaam);
		
		txtVoornaam = new JTextFieldTGI();
		txtVoornaam.setBounds(87, 59, 204, 20);
		getContentPane().add(txtVoornaam);
		
		cmbGeslacht = new JComboBox<TypeEntry>();
		cmbGeslacht.setBounds(87, 105, 89, 20);
		getContentPane().add(cmbGeslacht);
		
		JLabel lblGeslacht = new JLabel("Geslacht");
		lblGeslacht.setBounds(10, 108, 46, 14);
		getContentPane().add(lblGeslacht);
		
		txtTelefoon = new JTextFieldTGI();
		txtTelefoon.setBounds(87, 128, 204, 20);
		getContentPane().add(txtTelefoon);
		
		JLabel lblTelefoon = new JLabel("Telefoon");
		lblTelefoon.setBounds(10, 131, 46, 14);
		getContentPane().add(lblTelefoon);
		
		txtVoorletters = new JTextFieldTGI();
		txtVoorletters.setBounds(87, 82, 204, 20);
		getContentPane().add(txtVoorletters);
		
		JLabel lblVoorletters = new JLabel("Voorletters");
		lblVoorletters.setBounds(10, 85, 67, 14);
		getContentPane().add(lblVoorletters);
		
		txtEmail = new JTextFieldTGI();
		txtEmail.setBounds(87, 151, 204, 20);
		getContentPane().add(txtEmail);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(10, 154, 46, 14);
		getContentPane().add(lblEmail);
		
		JLabel lblRbodienst = new JLabel("Arbodienst");
		lblRbodienst.setBounds(10, 16, 67, 14);
		getContentPane().add(lblRbodienst);
		
		txtArbodienst = new JTextFieldTGI();
		txtArbodienst.setEditable(false);
		txtArbodienst.setBounds(87, 13, 204, 20);
		getContentPane().add(txtArbodienst);
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry geslacht;
		bedrijfsarts.setAchternaam(txtAchternaam.getText());
		bedrijfsarts.setEmail(txtEmail.getText());
		geslacht = (TypeEntry)cmbGeslacht.getSelectedItem();
		bedrijfsarts.setGeslacht(__geslacht.parse(geslacht.getValue()));
		bedrijfsarts.setTelefoon(txtTelefoon.getText());
		bedrijfsarts.setVoorletters(txtVoorletters.getText());
		bedrijfsarts.setVoornaam(txtVoornaam.getText());
		
        if (this.getLoginSession() != null)
        {
        	try {
        		bedrijfsarts.validate();
	    		switch (this.getMode()){
    			case New: 
    				ServiceCaller.instantieFacade(getLoginSession()).addBedrijfsarts(bedrijfsarts);
    				break;
    			case Update: 
    				ServiceCaller.instantieFacade(getLoginSession()).updateBedrijfsarts(bedrijfsarts);
    				break;
    			case Delete: 
    				ServiceCaller.instantieFacade(getLoginSession()).deleteBedrijfsarts(bedrijfsarts);
    				break;
    		}
    		super.okButtonClicked(e);
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
