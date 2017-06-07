package com.gieselaar.verzuimbeheer.components;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

public class ContactpersoonPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 16859447490510997L;
	private JPanel panelContactpersoon;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoorvoegsel;
	private JTextFieldTGI txtVoorletters;
	private JTextFieldTGI txtVoornaam;
	private JTextFieldTGI txtTelefoon;
	private JTextFieldTGI txtMobiel;
	private JTextFieldTGI txtEmail;
	private JComboBox<TypeEntry> cmbGeslacht;
	private ContactpersoonInfo contactpersoon = null;
	/**
	 * Create the panel.
	 */
	public ContactpersoonPanel() {
		super();
		initialize();
	}
	private void initialize(){
		setLayout(null);
		panelContactpersoon = new JPanel();
		panelContactpersoon.setBorder(new TitledBorder(null, "Contactpersoon", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelContactpersoon.setBounds(0, 0, 403, 112);
		this.add(panelContactpersoon);
		panelContactpersoon.setLayout(null);
		{
			JLabel lblAchternaam = new JLabel("Achternaam");
			lblAchternaam.setBounds(17, 19, 64, 14);
			panelContactpersoon.add(lblAchternaam);
		}
		{
			txtAchternaam = new JTextFieldTGI();
			txtAchternaam.setColumns(10);
			txtAchternaam.setBounds(103, 16, 152, 20);
			panelContactpersoon.add(txtAchternaam);
		}
		{
			JLabel lblVoornaam = new JLabel("Voornaam");
			lblVoornaam.setBounds(141, 42, 64, 14);
			panelContactpersoon.add(lblVoornaam);
		}
		{
			txtVoorvoegsel = new JTextFieldTGI();
			txtVoorvoegsel.setColumns(10);
			txtVoorvoegsel.setBounds(341, 16, 46, 20);
			panelContactpersoon.add(txtVoorvoegsel);
		}
		{
			JLabel lblTussenvoegsel = new JLabel("Tussenvoegsel");
			lblTussenvoegsel.setBounds(260, 19, 71, 14);
			panelContactpersoon.add(lblTussenvoegsel);
		}
		{
			txtVoorletters = new JTextFieldTGI();
			txtVoorletters.setColumns(10);
			txtVoorletters.setBounds(103, 39, 28, 20);
			panelContactpersoon.add(txtVoorletters);
		}
		{
			txtVoornaam = new JTextFieldTGI();
			txtVoornaam.setColumns(10);
			txtVoornaam.setBounds(193, 39, 53, 20);
			panelContactpersoon.add(txtVoornaam);
		}
		{
			JLabel lblVoorletters = new JLabel("Voorletters");
			lblVoorletters.setBounds(17, 42, 64, 14);
			panelContactpersoon.add(lblVoorletters);
		}
		{
			JLabel lblGeslacht = new JLabel("Geslacht");
			lblGeslacht.setBounds(260, 42, 64, 14);
			panelContactpersoon.add(lblGeslacht);
		}
		{
			cmbGeslacht = new JComboBox<TypeEntry>();
			cmbGeslacht.setMaximumRowCount(3);
			cmbGeslacht.setBounds(325, 39, 62, 20);
			panelContactpersoon.add(cmbGeslacht);
		}
		{
			JLabel lblTelefoon = new JLabel("Telefoon");
			lblTelefoon.setBounds(17, 65, 64, 14);
			panelContactpersoon.add(lblTelefoon);
		}
		{
			txtTelefoon = new JTextFieldTGI();
			txtTelefoon.setColumns(10);
			txtTelefoon.setBounds(103, 62, 93, 20);
			panelContactpersoon.add(txtTelefoon);
		}
		{
			JLabel lblMobiel = new JLabel("Mobiel");
			lblMobiel.setBounds(221, 65, 38, 14);
			panelContactpersoon.add(lblMobiel);
		}	
		{
			txtMobiel = new JTextFieldTGI();
			txtMobiel.setColumns(10);
			txtMobiel.setBounds(267, 62, 120, 20);
			panelContactpersoon.add(txtMobiel);
		}
		{
			JLabel lblEmail = new JLabel("Email");
			lblEmail.setBounds(17, 88, 64, 14);
			panelContactpersoon.add(lblEmail);
		}
		{
			txtEmail = new JTextFieldTGI();
			txtEmail.setColumns(10);
			txtEmail.setBounds(103, 85, 284, 20);
			panelContactpersoon.add(txtEmail);
		}
	}
	public ContactpersoonInfo getContactpersoon() {
		TypeEntry geslacht;
        this.contactpersoon.setAchternaam(txtAchternaam.getText());
        this.contactpersoon.setEmailadres(txtEmail.getText());

        geslacht = (TypeEntry) cmbGeslacht.getSelectedItem();
        this.contactpersoon.setGeslacht(__geslacht.parse(geslacht.getValue()));

        this.contactpersoon.setMobiel(txtMobiel.getText());
        this.contactpersoon.setTelefoon(txtTelefoon.getText());
        this.contactpersoon.setVoorletters(txtVoorletters.getText());
        this.contactpersoon.setVoornaam(txtVoornaam.getText());
        this.contactpersoon.setVoorvoegsel(txtVoorvoegsel.getText());
        if (this.contactpersoon.isEmpty())
        	return null;
        else
        	return this.contactpersoon;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setContactpersoon(ContactpersoonInfo cp) {
		TypeEntry geslacht;
		DefaultComboBoxModel geslachtModel;
		
		this.contactpersoon = cp;
        if (this.contactpersoon == null)
        	contactpersoon = new ContactpersoonInfo();
		geslachtModel = new DefaultComboBoxModel();
        cmbGeslacht.setModel(geslachtModel);
        for (__geslacht g: __geslacht.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	geslachtModel.addElement(soort);
        }
		if ( contactpersoon == null)
			cmbGeslacht.setSelectedIndex(0);
		else
		{
			txtAchternaam.setText(contactpersoon.getAchternaam());
			txtEmail.setText(contactpersoon.getEmailadres());

			for (int i=0;i<geslachtModel.getSize();i++)
			{
				geslacht = (TypeEntry) geslachtModel.getElementAt(i);
				if (__geslacht.parse(geslacht.getValue()) == contactpersoon.getGeslacht())
				{
					geslachtModel.setSelectedItem(geslacht);
					break;
				}
			}

			txtMobiel.setText(contactpersoon.getMobiel());
			txtTelefoon.setText(contactpersoon.getTelefoon());
			txtVoorletters.setText(contactpersoon.getVoorletters());
			txtVoornaam.setText(contactpersoon.getVoornaam());
			txtVoorvoegsel.setText(contactpersoon.getVoorvoegsel());
		}
	}
	public void setTitle(String title){
		TitledBorder border = (TitledBorder) this.panelContactpersoon.getBorder();
		border.setTitle(title);
	}
}
