package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.AdresPanel;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import javax.swing.JLabel;

public class UitkeringsinstantieDetail extends AbstractDetail {
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
	public UitkeringsinstantieDetail(AbstractController controller) {
		super("Beheer arbodienst", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		uitkeringsinstantie = (UitvoeringsinstituutInfo) info;
		displayUitkeringsinstantie();
	}
	private void displayUitkeringsinstantie(){
		txtNaam.setText(uitkeringsinstantie.getNaam());
		txtTelefoon.setText(uitkeringsinstantie.getTelefoonnummer());

		PostadresPanel.setAdres(uitkeringsinstantie.getPostadres());
		VestigingsadresPanel.setAdres(uitkeringsinstantie.getVestigingsadres());
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
	@Override
	public InfoBase collectData(){
		uitkeringsinstantie.setNaam(txtNaam.getText());
		uitkeringsinstantie.setTelefoonnummer(txtTelefoon.getText());
        
        uitkeringsinstantie.setPostadres(PostadresPanel.getAdres());
        uitkeringsinstantie.setVestigingsadres(VestigingsadresPanel.getAdres());
        return uitkeringsinstantie;
	}
}
