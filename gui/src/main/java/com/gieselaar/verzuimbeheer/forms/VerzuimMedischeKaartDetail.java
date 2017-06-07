package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.baseforms.BaseDialog;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import javax.swing.ScrollPaneConstants;

public class VerzuimMedischeKaartDetail extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtGebruiker;
	private VerzuimMedischekaartInfo medischekaart;
	private List<GebruikerInfo> gebruikers;
	private DatePicker dtpWijzgingsdatum;
	private JCheckBox chckbxOpenbaar;
	private JTextAreaTGI txtMedischeKaart;
	private LoginSessionRemote loginSession;

	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public VerzuimMedischeKaartDetail(JFrame jFrame, boolean modal) {
		super(jFrame, modal, "Beheer Medische kaart");
		initialize();
	}
	public void setInfo(InfoBase info){
		medischekaart = (VerzuimMedischekaartInfo) info;
		try {
			gebruikers = ServiceCaller.verzuimFacade(loginSession).getGebruikers();
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
    	if (medischekaart.getState() == persistencestate.ABSENT)
    	{
    		medischekaart.setOpenbaar(false);
    		medischekaart.setWijzigingsdatum(new Date());
    		medischekaart.setUser(loginSession.getGebruiker().getId());
    	}
		try {
			dtpWijzgingsdatum.setDate(medischekaart.getWijzigingsdatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
    	chckbxOpenbaar.setSelected(medischekaart.getOpenbaar());
		for (GebruikerInfo gi: gebruikers){
			if (gi.getId().equals(medischekaart.getUser())){
				txtGebruiker.setText(gi.getAchternaam());
				break;
			}
		}
		txtMedischeKaart.setText(medischekaart.getMedischekaart());
		activateListener();
	}

	private void initialize(){
		setBounds(100, 100, 485, 506);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 42, 449, 393);
		getContentPane().add(scrollPane);
		
		txtMedischeKaart = new JTextAreaTGI();
		scrollPane.setViewportView(txtMedischeKaart);
		
		txtGebruiker = new JTextFieldTGI();
		txtGebruiker.setEditable(false);
		txtGebruiker.setBounds(94, 11, 102, 20);
		getContentPane().add(txtGebruiker);
		txtGebruiker.setColumns(10);
		
		dtpWijzgingsdatum = new DatePicker();
		dtpWijzgingsdatum.setBounds(228, 11, 86, 21);
		dtpWijzgingsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpWijzgingsdatum);
		
		JLabel lblGewijzigdDoor = new JLabel("Gewijzigd door:");
		lblGewijzigdDoor.setBounds(10, 14, 86, 14);
		getContentPane().add(lblGewijzigdDoor);
		
		JLabel lblOp = new JLabel("op");
		lblOp.setBounds(206, 15, 24, 14);
		getContentPane().add(lblOp);
		
		chckbxOpenbaar = new JCheckBox("openbaar");
		chckbxOpenbaar.setBounds(320, 10, 97, 23);
		getContentPane().add(chckbxOpenbaar);

	}
	protected void okButtonClicked(ActionEvent e) {
		//medischekaart.setUser(loginSession.getGebruiker().getId());
		medischekaart.setMedischekaart(txtMedischeKaart.getText());
		medischekaart.setOpenbaar(chckbxOpenbaar.isSelected());
		medischekaart.setWijzigingsdatum(dtpWijzgingsdatum.getDate());

		if (this.loginSession != null)
        {
        	try {
        		medischekaart.validate();
        		if (medischekaart.getState() == persistencestate.ABSENT)
        			ServiceCaller.verzuimFacade(loginSession).addMedischekaart(medischekaart);
        		else
        			ServiceCaller.verzuimFacade(loginSession).updateMedischekaart(medischekaart);
		        super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
		super.okButtonClicked(e);

	}
	/*
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	*/
}
