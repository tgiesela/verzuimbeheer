package com.gieselaar.verzuim.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.GebruikerController;
import com.gieselaar.verzuim.controllers.GebruikerController.__gebruikercommands;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class GebruikerDetail extends AbstractDetail {
	private static final long serialVersionUID = 1L;
	private GebruikerInfo gebruiker= null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoornaam;
	private JTextFieldTGI txtTussenvoegsel;
	private JTextFieldTGI txtInlogfouten;
	private JTextFieldTGI txtLaatstepoging;
	private JTextFieldTGI txtEmailadres;
	private JPasswordField txtPassword;
	private JComboBox<TypeEntry> cmbStatus;
	private VerzuimComboBoxModel statusmodel;
	private JButton btnRollen;
	private JButton btnWerkgevers;
	private JCheckBox chckbxAlleWerkgevers;
	private JTextFieldTGI txtDomainname;
	private JCheckBox chckbxAdUser;
	private GebruikerController gebruikercontroller; 
	
	/**
	 * Create the frame.
	 */
	public GebruikerDetail(AbstractController controller) {
		super("Beheer gebruiker", controller);
		gebruikercontroller = (GebruikerController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		gebruiker = (GebruikerInfo)info;
		displayGebruiker();
	}
	private void displayGebruiker(){
		txtNaam.setText(gebruiker.getName());
		txtAchternaam.setText(gebruiker.getAchternaam());
		txtEmailadres.setText(gebruiker.getEmailadres());
		txtInlogfouten.setText(Integer.toString(gebruiker.getInlogfouten()));
		if (gebruiker.getLaatstepoging() != null)
			txtLaatstepoging.setText(gebruiker.getLaatstepoging().toString());
		else
			txtLaatstepoging.setText("");
		txtTussenvoegsel.setText(gebruiker.getTussenvoegsel());
		txtVoornaam.setText(gebruiker.getVoornaam());

		chckbxAdUser.setSelected(gebruiker.isAduser());
		txtDomainname.setText(gebruiker.getDomainname());
		if (chckbxAdUser.isSelected()){
			txtDomainname.setEnabled(true);
			txtPassword.setEnabled(false);
		}
		else{
			txtDomainname.setEnabled(false);
			txtPassword.setEnabled(true);
		}
		if (gebruiker.isAlleklanten())
			chckbxAlleWerkgevers.setSelected(true);
		else
			chckbxAlleWerkgevers.setSelected(false);
		cbAlleWerkgeversClicked(null);

		statusmodel = controller.getMaincontroller().getEnumModel(__status.class);
		statusmodel.setId(gebruiker.getStatus().getValue());
		cmbStatus.setModel(statusmodel);
	}
	private void initialize(){
		setBounds(100, 100, 606, 341);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(172, 11, 124, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Gebruikernaam");
		lblNaam.setLabelFor(txtNaam);
		lblNaam.setBounds(29, 14, 118, 14);
		getContentPane().add(lblNaam);
		
		JLabel lblOmschrijving = new JLabel("Achternaam");
		lblOmschrijving.setBounds(29, 39, 118, 14);
		getContentPane().add(lblOmschrijving);
		
		txtAchternaam = new JTextFieldTGI();
		txtAchternaam.setBounds(172, 36, 269, 20);
		getContentPane().add(txtAchternaam);
		txtAchternaam.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Voornaam");
		lblNewLabel.setBounds(29, 64, 65, 14);
		getContentPane().add(lblNewLabel);
		
		txtVoornaam = new JTextFieldTGI();
		txtVoornaam.setBounds(172, 61, 124, 20);
		getContentPane().add(txtVoornaam);
		txtVoornaam.setColumns(10);
		
		txtTussenvoegsel = new JTextFieldTGI();
		txtTussenvoegsel.setBounds(474, 36, 86, 20);
		getContentPane().add(txtTussenvoegsel);
		txtTussenvoegsel.setColumns(10);
		
		JLabel lblAantalFouteInlogpogingen = new JLabel("Aantal foute inlogpogingen");
		lblAantalFouteInlogpogingen.setBounds(31, 178, 139, 14);
		getContentPane().add(lblAantalFouteInlogpogingen);
		
		txtInlogfouten = new JTextFieldTGI();
		txtInlogfouten.setEditable(false);
		txtInlogfouten.setEnabled(false);
		txtInlogfouten.setBounds(174, 175, 47, 20);
		getContentPane().add(txtInlogfouten);
		txtInlogfouten.setColumns(10);
		
		JLabel lblLaatstePoging = new JLabel("Laatste poging");
		lblLaatstePoging.setBounds(258, 178, 80, 14);
		getContentPane().add(lblLaatstePoging);
		
		txtLaatstepoging = new JTextFieldTGI();
		txtLaatstepoging.setEditable(false);
		txtLaatstepoging.setEnabled(false);
		txtLaatstepoging.setBounds(348, 175, 153, 20);
		getContentPane().add(txtLaatstepoging);
		txtLaatstepoging.setColumns(10);
		
		cmbStatus = new JComboBox<>();
		cmbStatus.setBounds(173, 201, 190, 20);
		getContentPane().add(cmbStatus);
		
		JLabel lblStatus = new JLabel("Status");
		lblStatus.setBounds(31, 204, 46, 14);
		getContentPane().add(lblStatus);
		
		JLabel lblEmailAdres = new JLabel("Email adres");
		lblEmailAdres.setBounds(29, 92, 65, 14);
		getContentPane().add(lblEmailAdres);
		
		txtEmailadres = new JTextFieldTGI();
		txtEmailadres.setBounds(172, 89, 388, 20);
		getContentPane().add(txtEmailadres);
		txtEmailadres.setColumns(10);
		
		btnRollen = new JButton("Rollen...");
		btnRollen.setBounds(174, 227, 107, 23);
		btnRollen.setActionCommand(__gebruikercommands.ROLLENTONEN.toString());
		btnRollen.addActionListener(gebruikercontroller);
		getContentPane().add(btnRollen);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(31, 144, 65, 14);
		getContentPane().add(lblPassword);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(174, 141, 269, 20);
		getContentPane().add(txtPassword);
		
		btnWerkgevers = new JButton("Werkgevers...");
		btnWerkgevers.setActionCommand(__gebruikercommands.WERKGEVERGEBRUIKERSTONEN.toString());
		btnWerkgevers.addActionListener(gebruikercontroller);
		btnWerkgevers.setBounds(291, 227, 107, 23);
		getContentPane().add(btnWerkgevers);
		
		chckbxAlleWerkgevers = new JCheckBox("Alle werkgevers");
		chckbxAlleWerkgevers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbAlleWerkgeversClicked(e);
			}
		});
		chckbxAlleWerkgevers.setBounds(404, 227, 109, 23);
		getContentPane().add(chckbxAlleWerkgevers);
		
		chckbxAdUser = new JCheckBox("AD User: Domain name");
		chckbxAdUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxAdUser.isSelected()){
					txtDomainname.setEnabled(true);
					txtPassword.setEnabled(false);
				}
				else{
					txtDomainname.setEnabled(false);
					txtPassword.setEnabled(true);
				}
			}
		});
		chckbxAdUser.setBounds(25, 113, 145, 23);
		getContentPane().add(chckbxAdUser);
		
		txtDomainname = new JTextFieldTGI();
		txtDomainname.setEnabled(false);
		txtDomainname.setBounds(172, 114, 226, 20);
		getContentPane().add(txtDomainname);
		txtDomainname.setColumns(10);
	}
	protected void cbAlleWerkgeversClicked(ActionEvent e) {
		if (chckbxAlleWerkgevers.isSelected())
			btnWerkgevers.setEnabled(false);
		else
			btnWerkgevers.setEnabled(true);
	}
	@Override
	public InfoBase collectData(){
		gebruiker.setName(txtNaam.getText());
		gebruiker.setEmailadres(txtEmailadres.getText());
		gebruiker.setTussenvoegsel(txtTussenvoegsel.getText());
		gebruiker.setAchternaam(txtAchternaam.getText());
		gebruiker.setVoornaam(txtVoornaam.getText());
		if (txtInlogfouten.getText().isEmpty())
			gebruiker.setInlogfouten(0);
		else
			gebruiker.setInlogfouten(Integer.parseInt(txtInlogfouten.getText()));
		
		gebruiker.setAlleklanten((chckbxAlleWerkgevers.isSelected()));
		gebruiker.setNewPassword(new String(txtPassword.getPassword()));
        gebruiker.setStatus(__status.parse(statusmodel.getId()));
        gebruiker.setAduser(chckbxAdUser.isSelected());
        gebruiker.setDomainname(txtDomainname.getText());
        return gebruiker;
	}
}
