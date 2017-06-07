package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

public class AfdelingDetail extends BaseDetailform {
	private static final long serialVersionUID = 2166872417227071674L;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtWerkgever;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoorvoegsel;
	private JTextFieldTGI txtVoorletters;
	private JTextFieldTGI txtVoornaam;
	private JTextFieldTGI txtTelefoon;
	private JTextFieldTGI txtMobiel;
	private JTextFieldTGI txtEmail;
	private JComboBox<TypeEntry> cmbGeslacht;
	private AfdelingInfo afdeling = null;

	@SuppressWarnings("unchecked")
	public void setInfo(InfoBase info){
		ContactpersoonInfo contactpersoon;
		TypeEntry geslacht;
		
		afdeling = (AfdelingInfo) info;
		txtNaam.setText(afdeling.getNaam());
		contactpersoon = afdeling.getContactpersoon(); 
		@SuppressWarnings("rawtypes")
		DefaultComboBoxModel geslachtModel = new DefaultComboBoxModel();
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
		activateListener();
	}
	public AfdelingDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Afdeling", mdiPanel);
		initialize();
	}
	public void okButtonClicked(ActionEvent e){
		TypeEntry geslacht;
		ContactpersoonInfo contactpersoon = afdeling.getContactpersoon();
		
		afdeling.setNaam(txtNaam.getText());
		
        if (contactpersoon == null)
        {
        	afdeling.setContactpersoon(new ContactpersoonInfo());
        	contactpersoon = afdeling.getContactpersoon();
        }
        contactpersoon.setAchternaam(txtAchternaam.getText());
        contactpersoon.setEmailadres(txtEmail.getText());

        geslacht = (TypeEntry) cmbGeslacht.getSelectedItem();
        contactpersoon.setGeslacht(__geslacht.parse(geslacht.getValue()));

        contactpersoon.setMobiel(txtMobiel.getText());
        contactpersoon.setTelefoon(txtTelefoon.getText());
        contactpersoon.setVoorletters(txtVoorletters.getText());
        contactpersoon.setVoornaam(txtVoornaam.getText());
        contactpersoon.setVoorvoegsel(txtVoorvoegsel.getText());
        if (this.getLoginSession() != null)
        {
        	try {
        		afdeling.validate();
				this.setVisible(false);
				super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
		
	}
	private void initialize(){
		//getContentPane().setLocation(-32, -100);
		
		setBounds(100, 100, 471, 289);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(153, 68, 216, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Afdelingnaam");
		lblNaam.setBounds(69, 71, 74, 14);
		getContentPane().add(lblNaam);
		
		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(69, 46, 72, 14);
		getContentPane().add(lblWerkgever);
		
		txtWerkgever = new JTextFieldTGI();
		txtWerkgever.setEnabled(false);
		txtWerkgever.setBounds(153, 43, 216, 20);
		getContentPane().add(txtWerkgever);
		txtWerkgever.setColumns(10);
		
		JPanel panelContactpersoon = new JPanel();
		panelContactpersoon.setLayout(null);
		panelContactpersoon.setBorder(new TitledBorder(null, "Contactpersoon", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelContactpersoon.setBounds(48, 100, 403, 112);
		getContentPane().add(panelContactpersoon);
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
		
		cmbGeslacht = new JComboBox<TypeEntry>();
		cmbGeslacht.setMaximumRowCount(3);
		cmbGeslacht.setBounds(325, 39, 62, 20);
		panelContactpersoon.add(cmbGeslacht);
		
		JLabel lblTelefoon = new JLabel("Telefoon");
		lblTelefoon.setBounds(17, 65, 64, 14);
		panelContactpersoon.add(lblTelefoon);
		
		txtTelefoon = new JTextFieldTGI();
		txtTelefoon.setColumns(10);
		txtTelefoon.setBounds(103, 62, 93, 20);
		panelContactpersoon.add(txtTelefoon);
		
		JLabel lblMobiel = new JLabel("Mobiel");
		lblMobiel.setBounds(221, 65, 38, 14);
		panelContactpersoon.add(lblMobiel);
		
		txtMobiel = new JTextFieldTGI();
		txtMobiel.setColumns(10);
		txtMobiel.setBounds(267, 62, 120, 20);
		panelContactpersoon.add(txtMobiel);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(17, 88, 64, 14);
		panelContactpersoon.add(lblEmail);
		
		txtEmail = new JTextFieldTGI();
		txtEmail.setColumns(10);
		txtEmail.setBounds(103, 85, 284, 20);
		panelContactpersoon.add(txtEmail);
		

	}
}
