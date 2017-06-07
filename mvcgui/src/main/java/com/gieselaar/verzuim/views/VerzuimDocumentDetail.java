package com.gieselaar.verzuim.views;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JFrame;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.VerzuimdocumentenController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;

import javax.swing.JLabel;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class VerzuimDocumentDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private VerzuimDocumentInfo verzuimdocument;
	private List<GebruikerInfo> gebruikers;
	private JTextFieldTGI txtDocumentnaam;
	private JTextFieldTGI txtDocumentlocatie;
	private JTextAreaTGI taOmschrijving ;
	private DatePicker dtpAanmaakdatum;
	private JTextFieldTGI txtAanmaakuser;

	private VerzuimdocumentenController verzuimdocumentcontroller; 
	public VerzuimDocumentDetail(AbstractController controller) {
		super("Verzuim document", controller);
		verzuimdocumentcontroller = (VerzuimdocumentenController)controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		verzuimdocument = (VerzuimDocumentInfo)info;
		displayDocument();
	}
	private void displayDocument(){
		gebruikers = verzuimdocumentcontroller.getMaincontroller().getGebruikers();

    	try {
			dtpAanmaakdatum.setDate(verzuimdocument.getAanmaakdatum());
			txtDocumentlocatie.setText(verzuimdocument.getPadnaam());
			txtDocumentnaam.setText(verzuimdocument.getDocumentnaam());
			taOmschrijving.setText(verzuimdocument.getOmschrijving());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
		for (GebruikerInfo gi: gebruikers){
			if (gi.getId() == verzuimdocument.getAanmaakuser()){
				txtAanmaakuser.setText(gi.getAchternaam());
				break;
			}
		}
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
/*
	@Override
	protected void okButtonClicked() {


    	try {
    		if (this.getFormmode() == __formmode.NEW){
    			verzuimdocumentcontroller.addData(verzuimdocument);
    		}else{
    			verzuimdocumentcontroller.updateData(verzuimdocument);
    		}		    
    		controller.closeView(this);
    	}catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
	*/
	@Override
	public InfoBase collectData() {
		verzuimdocument.setAanmaakdatum(dtpAanmaakdatum.getDate());
		verzuimdocument.setDocumentnaam(txtDocumentnaam.getText());
		verzuimdocument.setOmschrijving(taOmschrijving.getText());
		verzuimdocument.setPadnaam(txtDocumentlocatie.getText());
		return verzuimdocument;
	}
}
