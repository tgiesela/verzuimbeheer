package com.gieselaar.verzuimbeheer.forms;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDialog;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;


public class OeNiveauDetail extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OeNiveauInfo oeniveau = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtOmschrijving;
	private LoginSessionRemote loginSession;

	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	/**
	 * Create the frame.
	 */
	public OeNiveauDetail(JFrame jFrame, boolean modal) {
		super(jFrame, modal, "Rapportage structuur niveau detail");
		initialize();
	}
	public void setInfo(InfoBase info){
		oeniveau = (OeNiveauInfo)info;
		txtNaam.setText(oeniveau.getNaam());
		txtOmschrijving.setText(oeniveau.getOeniveau().toString());
		
		activateListener();
	}
	
	private void initialize(){
		setBounds(100, 100, 606, 144);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(172, 11, 124, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Niveau omschrijving");
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
		
		oeniveau.setNaam(txtNaam.getText()); 
		oeniveau.setOeniveau(Integer.parseInt(txtOmschrijving.getText()));
        if (this.loginSession != null)
        {
        	try {
        		oeniveau.validate();
        		ServiceCaller.werkgeverFacade(loginSession).updateOeniveau(oeniveau);
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
