package com.gieselaar.verzuim.views;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;


public class CascodeGroepDetail extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CascodeGroepInfo cascodegroep = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtOmschrijving;
	/**
	 * Create the frame.
	 */
	public CascodeGroepDetail(AbstractController controller) {
		super("Beheer Cascode groepen", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		cascodegroep = (CascodeGroepInfo)info;
		displayCascodegroep();
	}
	private void displayCascodegroep(){
		txtNaam.setText(cascodegroep.getNaam());
		txtOmschrijving.setText(cascodegroep.getOmschrijving());
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
	@Override
	public InfoBase collectData(){
		
		cascodegroep.setNaam(txtNaam.getText()); 
		cascodegroep.setOmschrijving(txtOmschrijving.getText());
		return cascodegroep;
	}
}
