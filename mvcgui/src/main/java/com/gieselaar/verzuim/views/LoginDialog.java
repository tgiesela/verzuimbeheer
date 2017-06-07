package com.gieselaar.verzuim.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.components.PanelImage;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {

	/**
	 * According to Oracle documentation, Dialogs must be implemented as JInternalFrame or 
	 * JOpionpane. Not as JDialog. In this case the dialog appears before the MDI form
	 * is loaded.
	 */
	private static final long serialVersionUID = 1399297075925797639L;
	private final JPanel contentPanel = new JPanel();
	private JTextFieldTGI txtUsername;
	private JPasswordField txtPassword;
	private boolean result = false;
	private LoginSessionRemote loginSession;

	/**
	 * Getters and setters of this dialog
	 */
	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public boolean getResult(){
	
		return result;
	}
	/**
	 * Create the dialog.
	 */
	private void okButtonClicked(ActionEvent e) {
		/*
		 * Om; dit te laten werken moet je add external jar "appserv-rt.jar" in the buildpath
		 * van het project doen. appserv-rt-.jar staat in \glassfishv3\glassfish\lib
		 */
        if (loginSession != null)
        {
        	try {
				if (loginSession.authenticateGebruiker(txtUsername.getText(), new String(txtPassword.getPassword()))){
					this.setVisible(false);
					result = true;
				}else{
					JOptionPane.showMessageDialog(this, "Invalid username/password");
				}
			} catch (Exception e1) {
				ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
	private void cancelButtonClicked(ActionEvent e) {
		result = false;
		this.setVisible(false);
		
	}
	public LoginDialog(JFrame frame, boolean modal) {
		super(frame, modal);
		initialize();
	}
	private void initialize() {
		setBounds(100, 100, 243, 248);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel label = new JLabel("Gebruikernaam");
		label.setBounds(20, 105, 89, 14);
		contentPanel.add(label);
		
		txtUsername = new JTextFieldTGI();
		txtUsername.setColumns(10);
		txtUsername.setBounds(109, 102, 108, 20);
		contentPanel.add(txtUsername);
		
		JLabel label_1 = new JLabel("Wachtwoord");
		label_1.setBounds(20, 127, 78, 14);
		contentPanel.add(label_1);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(109, 124, 108, 20);
		contentPanel.add(txtPassword);
		
/*		Om een of andere reden werkt dit niet onder Windows, wel in Eclipse
 * 			Exception in thread "AWT-EventQueue-0" java.util.ServiceConfigurationError: javax.imageio.spi.ImageReaderSpi: Provider com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReaderSpi could not be instantiated: java.lang.IllegalArgumentException: vendorName == null!		
 * 
 */		
//		PanelImage panelLogo = new PanelImage("/com/gieselaar/verzuimbeheer/images/logo_de_vos.jpg");
/*		Onderstaande werkt alleen als bovenstaand pad in het classpath voorkomt */
		PanelImage panelLogo = new PanelImage("/logo_de_vos.jpg");
		panelLogo.setBounds(72, 11, 145, 83);
		contentPanel.add(panelLogo);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonClicked(e);
					}

				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonClicked(e);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
