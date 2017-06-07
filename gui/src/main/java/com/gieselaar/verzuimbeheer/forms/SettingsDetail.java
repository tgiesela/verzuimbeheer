package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;

import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.utils.VerzuimProperties;
import com.gieselaar.verzuimbeheer.utils.VerzuimProperties.__verzuimproperty;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SettingsDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<TypeEntry> cmbTodoInformatiekaart;
	private JComboBox<TypeEntry> cmbTodoAfsluitenDvb; 	
	private List<ActiviteitInfo> activiteiten;
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
	/**
	 * Create the frame.
	 */
	public SettingsDetail(JDesktopPaneTGI mdiPanel) {
		super("Settings",mdiPanel);
		setTitle("Settings");
		
		initialize();
	}
	public void setInfo(InfoBase info){
		DefaultComboBoxModel<TypeEntry> todoModelInformatiekaart;
		DefaultComboBoxModel<TypeEntry> todoModelAfsluitenDienstverband;
		Integer todoInformatiekaart = null;
		Integer todoAfsluitenDienstverband = null;
		try {
			settings = ServiceCaller.settingsFacade(getLoginSession()).getSettings();
			if (settings == null){
				settings = new SettingsInfo();
				settings.setId(1);
			}
			todoInformatiekaart = settings.getTodoforinformatiekaart();
			todoAfsluitenDienstverband  = settings.getTodoforafsluitendienstverband();
			activiteiten = ServiceCaller.pakketFacade(getLoginSession()).allActivteiten();
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		todoModelInformatiekaart = new DefaultComboBoxModel<TypeEntry>();
        todoModelInformatiekaart.addElement(new TypeEntry(-1,"[]"));
        cmbTodoInformatiekaart.setModel(todoModelInformatiekaart);
		todoModelAfsluitenDienstverband = new DefaultComboBoxModel<TypeEntry>();
        todoModelAfsluitenDienstverband.addElement(new TypeEntry(-1,"[]"));
        cmbTodoAfsluitenDvb.setModel(todoModelAfsluitenDienstverband);
		ActiviteitInfo.sort(activiteiten, ActiviteitInfo.__sortcol.NAAM);
        for (ActiviteitInfo  g: activiteiten)
        {
        	TypeEntry activiteit = new TypeEntry(g.getId(), g.getNaam());
        	todoModelInformatiekaart.addElement(activiteit);
        	if (g.getId().equals(todoInformatiekaart)){
        		todoModelInformatiekaart.setSelectedItem(activiteit);
        	}
        	todoModelAfsluitenDienstverband.addElement(activiteit);
        	if (g.getId().equals(todoAfsluitenDienstverband)){
        		todoModelAfsluitenDienstverband.setSelectedItem(activiteit);
        	}
        }
        
		txtHost.setText(settings.getSmtpmailhost());
		txtUsername.setText(settings.getSmtpmailuser());
		txtPassword.setText(settings.getSmtpmailpassword());
		txtFromEmailaddress.setText(settings.getSmtpmailfromaddress());
		txtBCCEmailaddress.setText(settings.getBccemailaddress());
		txtFactuurLocatie.setText(settings.getFactuurfolder());
		txtFactuuremailbody.setText(settings.getFactuurmailtextbody());
		txtServershare.setText(settings.getServershare());

		activateListener();
	}
	
	void initialize(){
		setBounds(100, 100, 450, 442);
		
		cmbTodoInformatiekaart = new JComboBox<TypeEntry>();
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
		
		cmbTodoAfsluitenDvb = new JComboBox<TypeEntry>();
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
				File selectedFile = SelectFilename(JFileChooser.DIRECTORIES_ONLY,txtFactuurLocatie.getText() );
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
						open(uri);
					} catch (URISyntaxException e1) {
						ExceptionLogger.ProcessException(e1,thisform);
					} catch (UnsupportedEncodingException e1) {
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
				File selectedFile = SelectFilename(JFileChooser.FILES_ONLY, txtFactuuremailbody.getText());
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
	private void open(URI uri) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.indexOf("win") >= 0) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler "
								+ "\""
								+ uri.getScheme()
								+ ":"
								+ URLDecoder.decode(
										uri.getSchemeSpecificPart(), "UTF-8")
								+ "\"");
			} else
				Runtime.getRuntime().exec(
						new String[] { "/usr/bin/open",
								URLDecoder.decode(uri.getPath(), "UTF-8") });
		} catch (IOException e) {
			ExceptionLogger.ProcessException(e,this);
		}
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry activiteit;
		Integer value;
        if (this.getLoginSession() != null)
        {
    		activiteit = (TypeEntry)cmbTodoInformatiekaart.getSelectedItem();
        	value = activiteit.getValue();
        	if (value.equals(-1)){
        		JOptionPane.showMessageDialog(this, "Todo voor informatiekaart is verplicht");
        		return;
        	}
			settings.setTodoforinformatiekaart(value);
    		
			activiteit = (TypeEntry)cmbTodoAfsluitenDvb.getSelectedItem();
        	value = activiteit.getValue();
        	if (value.equals(-1)){
        		JOptionPane.showMessageDialog(this, "Todo voor aflsuiten dienstverband is verplicht");
        		return;
        	}
			settings.setTodoforafsluitendienstverband(value);
			
			settings.setSmtpmailhost(txtHost.getText());
			settings.setSmtpmailuser( txtUsername.getText());
			settings.setSmtpmailpassword(new String(txtPassword.getPassword()));
			settings.setSmtpmailfromaddress(txtFromEmailaddress.getText());
			settings.setFactuurfolder(txtFactuurLocatie.getText());
			settings.setFactuurmailtextbody( txtFactuuremailbody.getText());
			settings.setBccemailaddress( txtBCCEmailaddress.getText());
			settings.setServershare(txtServershare.getText());
			try {
				ServiceCaller.settingsFacade(getLoginSession()).updateSettings(settings);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
				return;
			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1,this);
				return;
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1,this);
				return;
			} catch (ServiceLocatorException e1) {
				ExceptionLogger.ProcessException(e1,this);
				return;
			}
       		super.okButtonClicked(e);
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
	protected File SelectFilename(int mode, String currentselection) {
		File selectedfile;
		String lastsavedir;
		JFileChooser fd = new JFileChooser();
		VerzuimProperties verzuimProps = new VerzuimProperties();
		if (currentselection == null || currentselection.isEmpty()){
			lastsavedir = (String) verzuimProps
					.getProperty(__verzuimproperty.lastdocsavedir);
		} else{
			lastsavedir = currentselection;
		}

		fd.setDialogType(JFileChooser.SAVE_DIALOG);
		fd.setFileSelectionMode(mode);
		if (lastsavedir != null)
			fd.setCurrentDirectory(new File(lastsavedir));
		int retval = fd.showSaveDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			selectedfile = fd.getSelectedFile(); 
			verzuimProps.saveProperties();
			return selectedfile;
		} else
			return null;

	}
}
