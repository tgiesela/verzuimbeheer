package com.gieselaar.verzuim.views;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;


public class OeNiveauDetail extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OeNiveauInfo oeniveau = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtNiveaunummer;
	/**
	 * Create the frame.
	 */
	public OeNiveauDetail(AbstractController controller) {
		super("Rapportage structuur niveau detail", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		oeniveau = (OeNiveauInfo)info;
		displayOeniveau();
	}
	private void displayOeniveau(){
		txtNaam.setText(oeniveau.getNaam());
		txtNiveaunummer.setText(oeniveau.getOeniveau().toString());
	}
	
	private void initialize(){
		setBounds(100, 100, 606, 144);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(172, 11, 403, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Niveau omschrijving");
		lblNaam.setLabelFor(txtNaam);
		lblNaam.setBounds(29, 14, 118, 14);
		getContentPane().add(lblNaam);
		
		JLabel lblNiveaunummer = new JLabel("Niveaunummer");
		lblNiveaunummer.setBounds(29, 39, 118, 14);
		getContentPane().add(lblNiveaunummer);
		
		txtNiveaunummer = new JTextFieldTGI();
		txtNiveaunummer.setBounds(172, 36, 35, 20);
		getContentPane().add(txtNiveaunummer);
		txtNiveaunummer.setColumns(10);
	}
	@Override
	public InfoBase collectData(){
		oeniveau.setNaam(txtNaam.getText()); 
		oeniveau.setOeniveau(Integer.parseInt(txtNiveaunummer.getText()));
		return oeniveau;
	}

}
