package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;

import com.gieselaar.verzuim.components.AdresPanel;
import com.gieselaar.verzuim.components.ContactpersoonPanel;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.controllers.ArbodienstController;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class ArbodienstDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private ArbodienstInfo arbodienst;
	private AdresPanel vestigingsadresPanel;
	private AdresPanel postadresPanel;
	private ContactpersoonPanel contactpersoonPanel;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtTelefoonnummer;
	private JLabel lblTelefoonnr;
	private JButton btnArtsen;
	private ArbodienstController arbodienstcontroller;
	/**
	 * Create the frame.
	 */
	public ArbodienstDetail(AbstractController controller) {
		super("Arbodienst", controller);
		arbodienstcontroller = (ArbodienstController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		arbodienst = (ArbodienstInfo) info;
		displayArbodienst();
	}
	void displayArbodienst(){
		
		if (this.getFormmode() == __formmode.NEW)
			btnArtsen.setEnabled(false);
		txtNaam.setText(arbodienst.getNaam());
		txtTelefoonnummer.setText(arbodienst.getTelefoonnummer());

		postadresPanel.setAdres(arbodienst.getPostAdres());
		vestigingsadresPanel.setAdres(arbodienst.getVestigingsAdres());
		contactpersoonPanel.setContactpersoon(arbodienst.getContactpersoon());
	}

	void initialize(){
		setBounds(100, 100, 543, 500);
		vestigingsadresPanel = new AdresPanel();
		vestigingsadresPanel.setAdresSoort("Vestigingsadres");
		vestigingsadresPanel.setBounds(44, 86, 445, 95);
		getContentPane().add(vestigingsadresPanel);
		
		postadresPanel = new AdresPanel();
		postadresPanel.setAdresSoort("Postadres");
		postadresPanel.setBounds(44, 192, 446, 95);
		getContentPane().add(postadresPanel);
		
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
				arbodienstcontroller.openbedrijfsartsen();
			}
		});
		btnArtsen.setBounds(360, 22, 129, 23);
		getContentPane().add(btnArtsen);

	}
	@Override
	public InfoBase collectData(){
		arbodienst.setNaam(txtNaam.getText());
		arbodienst.setTelefoonnummer(txtTelefoonnummer.getText());
        
        arbodienst.setPostAdres(postadresPanel.getAdres());
        arbodienst.setVestigingsAdres(vestigingsadresPanel.getAdres());
        arbodienst.setContactpersoon(contactpersoonPanel.getContactpersoon());
        return arbodienst;
	}
}
