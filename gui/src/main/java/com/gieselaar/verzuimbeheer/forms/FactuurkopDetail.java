package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.event.ActionEvent;

public class FactuurkopDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FactuurkopInfo factuurkop;
	private JTextFieldTGI txtOmschrijving;
	private JTextFieldTGI txtPrioriteit;
	
	private boolean initialized = false;
	private Component thisform = this;

	/**
	 * Create the frame.
	 */
	public FactuurkopDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer factuur koppen", mdiPanel);
		initialize();
		
		
	}
	private void initialize() {
		setBounds(0, 237, 439, 175);
		getContentPane().setLayout(null);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(10, 15, 95, 14);
		getContentPane().add(lblOmschrijving);
		
		txtOmschrijving = new JTextFieldTGI();
		txtOmschrijving.setBounds(123, 11, 257, 20);
		getContentPane().add(txtOmschrijving);
		txtOmschrijving.setColumns(10);
		
		JLabel lblPrioriteit = new JLabel("Prioriteit");
		lblPrioriteit.setBounds(10, 44, 95, 14);
		getContentPane().add(lblPrioriteit);
		
		txtPrioriteit = new JTextFieldTGI();
		txtPrioriteit.setColumns(10);
		txtPrioriteit.setBounds(123, 40, 39, 20);
		getContentPane().add(txtPrioriteit);
		
	}
	protected void cmbWerkgeverClicked(ActionEvent e) {
	}
	public void setInfo(InfoBase info){
		try {
			factuurkop = (FactuurkopInfo)info;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
			
		displayFactuurcategorie();
		
		activateListener();
        initialized = true;
	}
	private void displayFactuurcategorie() {
		txtOmschrijving.setText(factuurkop.getOmschrijving());
		txtPrioriteit.setText(Integer.toString(factuurkop.getPrioriteit()));
	}
	protected void okButtonClicked(ActionEvent e) {
		factuurkop.setOmschrijving(txtOmschrijving.getText());
		factuurkop.setPrioriteit(Integer.parseInt(txtPrioriteit.getText()));
		if (this.getLoginSession() != null)
        {
        	try {
        		factuurkop.validate();
        		switch (this.getMode())
        		{
        			case New: 		ServiceCaller.factuurFacade(getLoginSession()).addFactuurkop(factuurkop);
        							break;
        			case Update: 	ServiceCaller.factuurFacade(getLoginSession()).updateFactuurkop(factuurkop);
        							break;
        			case Delete: 	ServiceCaller.factuurFacade(getLoginSession()).deleteFactuurkop(factuurkop);
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
