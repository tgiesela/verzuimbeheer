package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import javax.swing.JLabel;

public class FactuurkopDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private FactuurkopInfo factuurkop;
	private JTextFieldTGI txtOmschrijving;
	private JTextFieldTGI txtPrioriteit;
	
	/**
	 * Create the frame.
	 */
	public FactuurkopDetail(AbstractController controller) {
		super("Beheer factuur koppen", controller);
		initialize();
	}
	private void initialize() {
		setBounds(50, 50, 439, 175);
		
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
	@Override
	public void setData(InfoBase info){
		factuurkop = (FactuurkopInfo)info;
			
		displayFactuurcategorie();
	}
	private void displayFactuurcategorie() {
		txtOmschrijving.setText(factuurkop.getOmschrijving());
		txtPrioriteit.setText(Integer.toString(factuurkop.getPrioriteit()));
	}
	@Override
	public InfoBase collectData(){
		factuurkop.setOmschrijving(txtOmschrijving.getText());
		factuurkop.setPrioriteit(Integer.parseInt(txtPrioriteit.getText()));
		return factuurkop;
	}
}
