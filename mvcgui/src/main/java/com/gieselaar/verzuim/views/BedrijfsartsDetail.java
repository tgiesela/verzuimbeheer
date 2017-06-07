package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.BedrijfsartsController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JComboBox;

public class BedrijfsartsDetail extends AbstractDetail {

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
	private VerzuimComboBoxModel geslachtModel;
	private BedrijfsartsController bedrijfsartscontroller;

	/**
	 * Create the frame.
	 */
	public BedrijfsartsDetail(AbstractController controller) {
		super("Beheer Werkgever",controller);
		bedrijfsartscontroller = (BedrijfsartsController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		bedrijfsarts = (BedrijfsartsInfo) info;
		arbodienst = bedrijfsartscontroller.getSelectedarbodienst();
		displayBedrijfsarts();
	}
	private void displayBedrijfsarts(){
		geslachtModel = controller.getMaincontroller().getEnumModel(__geslacht.class);
		geslachtModel.setId(bedrijfsarts.getGeslacht().getValue());
		cmbGeslacht.setModel(geslachtModel);
        cmbGeslacht.setModel(geslachtModel);
		txtAchternaam.setText(bedrijfsarts.getAchternaam());
		txtArbodienst.setText(arbodienst.getNaam());
		txtEmail.setText(bedrijfsarts.getEmail());
		txtTelefoon.setText(bedrijfsarts.getTelefoon());
		txtVoorletters.setText(bedrijfsarts.getVoorletters());
		txtVoornaam.setText(bedrijfsarts.getVoornaam());
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
		
		cmbGeslacht = new JComboBox<>();
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
	@Override
	public InfoBase collectData(){
		bedrijfsarts.setAchternaam(txtAchternaam.getText());
		bedrijfsarts.setEmail(txtEmail.getText());
		bedrijfsarts.setGeslacht(__geslacht.parse(geslachtModel.getId()));
		bedrijfsarts.setTelefoon(txtTelefoon.getText());
		bedrijfsarts.setVoorletters(txtVoorletters.getText());
		bedrijfsarts.setVoornaam(txtVoornaam.getText());
		return bedrijfsarts;
	}
}
