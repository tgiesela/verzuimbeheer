package com.gieselaar.verzuimbeheer.forms;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import javax.swing.JLabel;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class VerzuimDocumentDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerzuimDocumentInfo verzuimdoc;
	private List<GebruikerInfo> gebruikers;
	private JTextFieldTGI txtDocumentnaam;
	private JTextFieldTGI txtDocumentlocatie;
	private JTextAreaTGI taOmschrijving ;
	private DatePicker dtpAanmaakdatum;
	private JTextFieldTGI txtAanmaakuser;

	public VerzuimDocumentDetail(JDesktopPaneTGI mdiPanel) {
		super("Verzuim document", mdiPanel);
		
		initialize();
	}
	public void setInfo(InfoBase info){
		
		verzuimdoc = (VerzuimDocumentInfo) info;
		try {
			if (this.getMode() == formMode.New){
				verzuimdoc.setAanmaakdatum(new Date());
				verzuimdoc.setAanmaakuser(this.getLoginSession().getGebruiker().getId());
			}
				
			gebruikers = ServiceCaller.verzuimFacade(getLoginSession()).getGebruikers();

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

    	try {
			dtpAanmaakdatum.setDate(verzuimdoc.getAanmaakdatum());
			txtDocumentlocatie.setText(verzuimdoc.getPadnaam());
			txtDocumentnaam.setText(verzuimdoc.getDocumentnaam());
			taOmschrijving.setText(verzuimdoc.getOmschrijving());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
		for (GebruikerInfo gi: gebruikers)
			if (gi.getId() == verzuimdoc.getAanmaakuser()){
				txtAanmaakuser.setText(gi.getAchternaam());
				break;
			}
        activateListener();
	}
	
	void initialize(){
		setBounds(100, 100, 482, 300);
		JLabel lblDocumentnaam = new JLabel("Documentnaam");
		lblDocumentnaam.setBounds(20, 23, 95, 14);
		getContentPane().add(lblDocumentnaam);
		
		txtDocumentnaam = new JTextFieldTGI();
		txtDocumentnaam.setBounds(125, 20, 182, 20);
		getContentPane().add(txtDocumentnaam);
		
		JLabel lblDocumentlocatie = new JLabel("Documentlocatie");
		lblDocumentlocatie.setBounds(20, 46, 95, 14);
		getContentPane().add(lblDocumentlocatie);
		
		txtDocumentlocatie = new JTextFieldTGI();
		txtDocumentlocatie.setBounds(125, 43, 283, 20);
		getContentPane().add(txtDocumentlocatie);
		
		JLabel lblOpmerkingen = new JLabel("Opmerkingen");
		lblOpmerkingen.setBounds(20, 66, 95, 14);
		getContentPane().add(lblOpmerkingen);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(125, 66, 283, 109);
		getContentPane().add(scrollPane);
		
		taOmschrijving = new JTextAreaTGI();
		taOmschrijving.setLocation(127, 0);
		scrollPane.setViewportView(taOmschrijving);
		
		JLabel lblOpgevoerdOp = new JLabel("Opgevoerd op");
		lblOpgevoerdOp.setBounds(10, 211, 77, 14);
		getContentPane().add(lblOpgevoerdOp);
		
		dtpAanmaakdatum = new DatePicker();
		dtpAanmaakdatum.setEnabled(false);
		dtpAanmaakdatum.setBounds(99, 208, 89, 21);
		dtpAanmaakdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpAanmaakdatum);
		
		JLabel lblDoor = new JLabel("door");
		lblDoor.setBounds(198, 211, 35, 14);
		getContentPane().add(lblDoor);
		
		txtAanmaakuser = new JTextFieldTGI();
		txtAanmaakuser.setEnabled(false);
		txtAanmaakuser.setBounds(243, 208, 165, 20);
		getContentPane().add(txtAanmaakuser);
		
		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked(e);
			}
		});
		btnNewButton.setBounds(415, 42, 35, 23);
		getContentPane().add(btnNewButton);
	}
	protected void btnSelectFileClicked(ActionEvent e) {
		File f = new File(txtDocumentlocatie.getText());
		String path = f.getPath();
		String chosenfile;
		
		FileDialog fd = new FileDialog((JFrame)null, "Selecteer document", FileDialog.LOAD);
		fd.setFile(f.getName());
		fd.setDirectory(path);
		fd.setMultipleMode(false);
		//fd.setLocation(50, 50);
		fd.setVisible(true);
		if (fd.getFile() == null)
			;
		else
		{
			chosenfile = fd.getDirectory() + fd.getFile();
			if (chosenfile.isEmpty())
				;
			else
				txtDocumentlocatie.setText(chosenfile);
		}
	}
	protected void okButtonClicked(ActionEvent e) {

		verzuimdoc.setAanmaakdatum(dtpAanmaakdatum.getDate());
		verzuimdoc.setDocumentnaam(txtDocumentnaam.getText());
		verzuimdoc.setOmschrijving(taOmschrijving.getText());
		verzuimdoc.setPadnaam(txtDocumentlocatie.getText());

		if (this.getLoginSession() != null)
        {
        	try {
        		verzuimdoc.validate();
        		switch (this.getMode()){
        			case New: 
        				ServiceCaller.verzuimFacade(getLoginSession()).addVerzuimDocument(verzuimdoc);
        				break;
        			case Update: 
        				ServiceCaller.verzuimFacade(getLoginSession()).updateVerzuimDocument(verzuimdoc);
        				break;
        			case Delete: 
        				ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuimDocument(verzuimdoc);
        				break;
        		}
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
		
	}
}
