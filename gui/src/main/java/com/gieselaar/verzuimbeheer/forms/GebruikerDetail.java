package com.gieselaar.verzuimbeheer.forms;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class GebruikerDetail extends BaseDetailform {

	/**
	 * 
	 */
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
	private GebruikerWerkgevers dlgGebruikerWerkgevers;
	private GebruikerRollen dlgGebruikerRollen;
	private JButton btnRollen;
	private JButton btnWerkgevers;
	private JCheckBox chckbxAlleWerkgevers;
	private JTextFieldTGI txtDomainname;
	private JCheckBox chckbxAdUser;
	/**
	 * Create the frame.
	 */
	public GebruikerDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer gebruiker", mdiPanel);
		initialize();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInfo(InfoBase info){
		gebruiker = (GebruikerInfo)info;
		try {
			if (this.getMode() != formMode.New)
				gebruiker = ServiceCaller.autorisatieFacade(getLoginSession()).getGebruiker(gebruiker.getId());
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
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

		DefaultComboBoxModel statusmodel = new DefaultComboBoxModel();

        cmbStatus.setModel(statusmodel);
		for (__status srt: __status.values())
		{
			TypeEntry status = new TypeEntry(srt.getValue(), srt.toString());
			statusmodel.addElement(status);
			if (status.getValue() == gebruiker.getStatus().getValue())
				statusmodel.setSelectedItem(status);
		}
		activateListener();
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
		
		cmbStatus = new JComboBox<TypeEntry>();
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
		btnRollen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRollenClicked(e);
			}
		});
		btnRollen.setBounds(174, 227, 107, 23);
		getContentPane().add(btnRollen);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(31, 144, 65, 14);
		getContentPane().add(lblPassword);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(174, 141, 269, 20);
		getContentPane().add(txtPassword);
		
		btnWerkgevers = new JButton("Werkgevers...");
		btnWerkgevers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWerkgeversClicked(e);
			}
		});
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
	protected void btnWerkgeversClicked(ActionEvent e) {
		dlgGebruikerWerkgevers = new GebruikerWerkgevers((JFrame)SwingUtilities.getAncestorOfClass(Window.class, btnWerkgevers),true);
		dlgGebruikerWerkgevers.setLoginSession(this.getLoginSession());
		dlgGebruikerWerkgevers.setInfo(gebruiker);
		dlgGebruikerWerkgevers.setVisible(true);
	}
	protected void btnRollenClicked(ActionEvent e) {
		dlgGebruikerRollen = new GebruikerRollen((JFrame)SwingUtilities.getAncestorOfClass(Window.class, btnRollen),true);
		dlgGebruikerRollen.setLoginSession(this.getLoginSession());
		dlgGebruikerRollen.setInfo(gebruiker);
		dlgGebruikerRollen.setVisible(true);
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry status;		
		
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
		status = (TypeEntry) cmbStatus.getSelectedItem();
        gebruiker.setStatus(__status.parse(status.getValue()));
        gebruiker.setAduser(chckbxAdUser.isSelected());
        gebruiker.setDomainname(txtDomainname.getText());
        
		if (this.getLoginSession() != null)
        {
        	try {
        		gebruiker.validate();
				try {
	        		if (this.getMode() == formMode.New)
						ServiceCaller.autorisatieFacade(getLoginSession()).addGebruiker(gebruiker);
    				else
    					ServiceCaller.autorisatieFacade(getLoginSession()).updateGebruiker(gebruiker);
				} catch (PermissionException e1) {
		        	ExceptionLogger.ProcessException(e1,this);
				} catch (VerzuimApplicationException e1) {
		        	ExceptionLogger.ProcessException(e1,this);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,this);
				}
				super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
}
