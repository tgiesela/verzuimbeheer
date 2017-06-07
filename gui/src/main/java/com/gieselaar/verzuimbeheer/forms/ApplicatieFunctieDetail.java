package com.gieselaar.verzuimbeheer.forms;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

public class ApplicatieFunctieDetail extends BaseDetailform {

	private static final long serialVersionUID = 1L;
	private ApplicatieFunctieInfo applfunc = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtOmschrijving;

	/**
	 * Create the frame.
	 */
	public ApplicatieFunctieDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Applicatiefunctie", mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){
		applfunc = (ApplicatieFunctieInfo)info;
		txtNaam.setText(applfunc.getFunctieId());
		txtOmschrijving.setText(applfunc.getFunctieomschrijving());
		activateListener();
	}
	
	private void initialize(){
		setBounds(100, 100, 606, 124);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(172, 11, 124, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Functie identificatie");
		lblNaam.setLabelFor(txtNaam);
		lblNaam.setBounds(29, 14, 118, 14);
		getContentPane().add(lblNaam);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(29, 39, 118, 14);
		getContentPane().add(lblOmschrijving);
		
		txtOmschrijving = new JTextFieldTGI();
		txtOmschrijving.setBounds(172, 36, 403, 20);
		getContentPane().add(txtOmschrijving);
		txtOmschrijving.setColumns(10);
	}
	protected void okButtonClicked(ActionEvent e) {
		
		applfunc.setFunctieId(txtNaam.getText()); 
		applfunc.setFunctieomschrijving(txtOmschrijving.getText());
        if (this.getLoginSession() != null)
        {
        	try {
        		applfunc.validate();
				ServiceCaller.autorisatieFacade(getLoginSession()).updateAppFunctie(applfunc);
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
