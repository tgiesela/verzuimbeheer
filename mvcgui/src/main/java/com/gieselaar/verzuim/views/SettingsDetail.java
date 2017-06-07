package com.gieselaar.verzuim.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.SettingsController;
import com.gieselaar.verzuim.controllers.AbstractController.__filedialogtype;
import com.gieselaar.verzuim.controllers.AbstractController.__selectfileoption;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SettingsDetail extends AbstractDetail {
	private static final long serialVersionUID = 1L;
	private VerzuimComboBoxModel todoModelInformatiekaart;
	private VerzuimComboBoxModel todoModelAfsluitenDienstverband;
	private JComboBox<TypeEntry> cmbTodoInformatiekaart;
	private JComboBox<TypeEntry> cmbTodoAfsluitenDvb; 	
	private JTextFieldTGI txtUsername;
	private JPasswordField txtPassword;
	private JTextFieldTGI txtHost;
	private JTextFieldTGI txtFromEmailaddress;
	private JTextFieldTGI txtFactuurLocatie;
	private JTextFieldTGI txtFactuuremailbody;
	private JTextFieldTGI txtBCCEmailaddress;
	private JTextFieldTGI txtServershare;
	private SettingsInfo settings;
	private Component thisform = this;
	private SettingsController settingscontroller; 
	
	/**
	 * Create the frame.
	 */
	public SettingsDetail(AbstractController controller) {
		super("Settings",controller);
		settingscontroller = (SettingsController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		settings = (SettingsInfo)info;
		displaySettings();
	}
	private void displaySettings(){

		todoModelInformatiekaart = settingscontroller.getComboModelActviteiten();
        todoModelInformatiekaart.setId(settings.getTodoforinformatiekaart());
        cmbTodoInformatiekaart.setModel(todoModelInformatiekaart);
		todoModelAfsluitenDienstverband = settingscontroller.getComboModelActviteiten();
		todoModelAfsluitenDienstverband.setId(settings.getTodoforafsluitendienstverband());
        cmbTodoAfsluitenDvb.setModel(todoModelAfsluitenDienstverband);
        
		txtHost.setText(settings.getSmtpmailhost());
		txtUsername.setText(settings.getSmtpmailuser());
		txtPassword.setText(settings.getSmtpmailpassword());
		txtFromEmailaddress.setText(settings.getSmtpmailfromaddress());
		txtBCCEmailaddress.setText(settings.getBccemailaddress());
		txtFactuurLocatie.setText(settings.getFactuurfolder());
		txtFactuuremailbody.setText(settings.getFactuurmailtextbody());
		txtServershare.setText(settings.getServershare());

	}
	
	void initialize(){
		setBounds(100, 100, 450, 442);
		
		cmbTodoInformatiekaart = new JComboBox<>();
		cmbTodoInformatiekaart.setBounds(210, 11, 203, 20);
		getContentPane().add(cmbTodoInformatiekaart);
		
		JLabel lblTodoInformatiekaart = new JLabel("Todo voor informatiekaart via web");
		lblTodoInformatiekaart.setToolTipText("Todo die aangemaakt wordt als via de web interface iets aan de informatiekaart (medische kaart) wordt toegevoegd of gewijzigd");
		lblTodoInformatiekaart.setBounds(10, 14, 190, 14);
		getContentPane().add(lblTodoInformatiekaart);
		
		JLabel lblTodoVoorAfsluiten = new JLabel("Todo voor afsluiten dienstverband");
		lblTodoVoorAfsluiten.setToolTipText("Todo die aangemaakt wordt als dienstverband wordt afgesloten bij een open verzuim");
		lblTodoVoorAfsluiten.setBounds(10, 45, 190, 14);
		getContentPane().add(lblTodoVoorAfsluiten);
		
		cmbTodoAfsluitenDvb = new JComboBox<>();
		cmbTodoAfsluitenDvb.setBounds(210, 42, 203, 20);
		getContentPane().add(cmbTodoAfsluitenDvb);
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "SMTP instellingen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(13, 79, 333, 152);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		txtUsername = new JTextFieldTGI();
		txtUsername.setBounds(135, 47, 170, 20);
		panel.add(txtUsername);
		
		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(10, 50, 71, 14);
		panel.add(lblNewLabel);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(135, 69, 170, 20);
		panel.add(txtPassword);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 72, 71, 14);
		panel.add(lblPassword);
		
		JLabel lblHost = new JLabel("Host");
		lblHost.setBounds(10, 28, 71, 14);
		panel.add(lblHost);
		
		txtHost = new JTextFieldTGI();
		txtHost.setBounds(135, 25, 170, 20);
		panel.add(txtHost);
		
		JLabel lblAfzenderEmail = new JLabel("Afzender email");
		lblAfzenderEmail.setBounds(10, 94, 85, 14);
		panel.add(lblAfzenderEmail);
		
		txtFromEmailaddress = new JTextFieldTGI();
		txtFromEmailaddress.setBounds(135, 91, 170, 20);
		panel.add(txtFromEmailaddress);
		
		JLabel lblBccEmail = new JLabel("BCC email");
		lblBccEmail.setBounds(10, 119, 85, 14);
		panel.add(lblBccEmail);
		
		txtBCCEmailaddress = new JTextFieldTGI();
		txtBCCEmailaddress.setBounds(135, 116, 170, 20);
		panel.add(txtBCCEmailaddress);
		
		JLabel lblFacturenOpslaanIn = new JLabel("Facturen opslaan in");
		lblFacturenOpslaanIn.setBounds(25, 242, 115, 14);
		getContentPane().add(lblFacturenOpslaanIn);
		
		txtFactuurLocatie = new JTextFieldTGI();
		txtFactuurLocatie.setBounds(150, 239, 171, 20);
		getContentPane().add(txtFactuurLocatie);
		
		JButton btnSelect = new JButton("...");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File selectedFile = controller.selectFilename(__selectfileoption.FILEORDIRECTORY,__filedialogtype.OPEN,txtFactuurLocatie.getText(),null);
				if (selectedFile != null){
					txtFactuurLocatie.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		btnSelect.setBounds(330, 238, 32, 23);
		getContentPane().add(btnSelect);
		
		JLabel lblFactuurEmailBody = new JLabel("Factuur email body");
		lblFactuurEmailBody.setBounds(25, 270, 115, 14);
		getContentPane().add(lblFactuurEmailBody);
		
		txtFactuuremailbody = new JTextFieldTGI();
		txtFactuuremailbody.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {/*noop*/}
			
			@Override
			public void mousePressed(MouseEvent e) {/*noop*/}
			
			@Override
			public void mouseExited(MouseEvent e) {/*noop*/}
			
			@Override
			public void mouseEntered(MouseEvent e) {/*noop*/}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2){
					URI uri;
					try {
						uri = new URI("File://"
								+ URLEncoder.encode(txtFactuuremailbody.getText(), "UTF-8"));
						settingscontroller.open(uri);
					} catch (URISyntaxException e1) {
						ExceptionLogger.ProcessException(e1,thisform);
					} catch (UnsupportedEncodingException e1) {
						ExceptionLogger.ProcessException(e1,thisform);
					} catch (ValidationException e1) {
						ExceptionLogger.ProcessException(e1,thisform);
					}
				}
			}
		});
			
		txtFactuuremailbody.setBounds(150, 267, 171, 20);
		getContentPane().add(txtFactuuremailbody);
		
		JButton btnSelectEmailbody = new JButton("...");
		btnSelectEmailbody.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File selectedFile = controller.selectFilename(__selectfileoption.FILEONLY,__filedialogtype.OPEN, txtFactuuremailbody.getText(),null);
				if (selectedFile != null){
					txtFactuuremailbody.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		btnSelectEmailbody.setBounds(330, 266, 32, 23);
		getContentPane().add(btnSelectEmailbody);
		
		JLabel lblServershareVoorArcodocs = new JLabel("Share voor docs");
		lblServershareVoorArcodocs.setBounds(13, 319, 115, 14);
		getContentPane().add(lblServershareVoorArcodocs);
		
		txtServershare = new JTextFieldTGI();
		txtServershare.setBounds(138, 316, 170, 20);
		getContentPane().add(txtServershare);
		
	}
	@Override
	public InfoBase collectData(){
   		Integer activiteitId;
   		activiteitId = todoModelInformatiekaart.getId();
       	if (activiteitId.equals(-1)){
       		JOptionPane.showMessageDialog(this, "Todo voor informatiekaart is verplicht");
       		return null;
       	}
		settings.setTodoforinformatiekaart(activiteitId);
		activiteitId = todoModelAfsluitenDienstverband.getId();
       	if (activiteitId.equals(-1)){
       		JOptionPane.showMessageDialog(this, "Todo voor aflsuiten dienstverband is verplicht");
       		return null;
       	}
		settings.setTodoforafsluitendienstverband(activiteitId);
			
		settings.setSmtpmailhost(txtHost.getText());
		settings.setSmtpmailuser( txtUsername.getText());
		settings.setSmtpmailpassword(new String(txtPassword.getPassword()));
		settings.setSmtpmailfromaddress(txtFromEmailaddress.getText());
		settings.setFactuurfolder(txtFactuurLocatie.getText());
		settings.setFactuurmailtextbody( txtFactuuremailbody.getText());
		settings.setBccemailaddress( txtBCCEmailaddress.getText());
		return settings;
	}
}
