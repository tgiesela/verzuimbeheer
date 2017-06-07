package com.gieselaar.verzuim.main;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gieselaar.verzuim.controllers.MDIMainController;
import com.gieselaar.verzuim.models.MDIMainModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.MDIMain;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuim.views.LoginDialog;
import com.gieselaar.verzuimbeheer.utils.RemoteInterfaces;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class AppStart {
	private LoginDialog dlgLogin;
	public void Login(LoginSessionRemote session){
		dlgLogin = new LoginDialog(null, true);
		dlgLogin.setLoginSession(session);
		dlgLogin.setVisible(true);
		if (dlgLogin.getResult() == true)
		{
			dlgLogin.dispose();
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Login failure");
			dlgLogin.dispose();
			System.exit(0);
		}
	}
	public AppStart(){
        try {
        	LoginSessionRemote session   = (LoginSessionRemote) ServiceLocator.getInstance().getRemoteHome (
					RemoteInterfaces.LoginSession.getValue(),LoginSessionRemote.class);
			try {
				/*
				 * To prevent SSL errors when using STARTTLS, the truststore must be
				 * set. If not set before jndi context.lookup, the SSL libraries will
				 * fail with strange errors about trustAnchors not being set. 
				 */
				String javahome = System.getProperty("java.home");
				System.setProperty("javax.net.ssl.trustStore",javahome + "\\lib\\security\\cacerts");

				/*
				 * Set windows lookandfeel
				 */
				String laf = UIManager.getSystemLookAndFeelClassName();
				try {
					UIManager.setLookAndFeel(laf);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					ExceptionLogger.ProcessException(e, null);
				}

				Login(session);
				
				MDIMainModel mdiModel = new MDIMainModel(session);
				MDIMainController controller = new MDIMainController(mdiModel, null);
				new MDIMain(controller);
				
			} catch (Exception e) {
				ExceptionLogger.ProcessException(e, null, e.getMessage());
			}
        } catch (ServiceLocatorException e1) {
        	ExceptionLogger.ProcessException(e1,null,"Can't create LoginSession");
		}  
		
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new AppStart();
			}
		});
	}

}
