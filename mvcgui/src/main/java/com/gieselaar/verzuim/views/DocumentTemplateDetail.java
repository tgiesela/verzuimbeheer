package com.gieselaar.verzuim.views;

import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class DocumentTemplateDetail extends AbstractDetail {
	private static final long serialVersionUID = 1L;
	private DocumentTemplateInfo template = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtPadnaam;
	private JTextAreaTGI taOmschrijving;
	/**
	 * Create the frame.
	 */
	public DocumentTemplateDetail (AbstractController controller) {
		super("Beheer Document templates", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		template = (DocumentTemplateInfo)info;
		displayDocumenttemplate();
	}
	void displayDocumenttemplate(){
		txtNaam.setText(template.getNaam());
		txtPadnaam.setText(template.getPadnaam());
		taOmschrijving.setText(template.getOmschrijving());
	}
	private void initialize(){
		setBounds(100, 100, 606, 296);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(170, 11, 124, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Naam");
		lblNaam.setLabelFor(txtNaam);
		lblNaam.setBounds(29, 14, 118, 14);
		getContentPane().add(lblNaam);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(29, 57, 118, 14);
		getContentPane().add(lblOmschrijving);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(170, 57, 296, 114);
		getContentPane().add(scrollPane);
		
		taOmschrijving = new JTextAreaTGI();
		taOmschrijving.setLocation(172, 0);
		taOmschrijving.setFont(txtNaam.getFont());
		scrollPane.setViewportView(taOmschrijving);
		
		JLabel lblPadnaam = new JLabel("Padnaam");
		lblPadnaam.setBounds(29, 37, 46, 14);
		getContentPane().add(lblPadnaam);
		
		txtPadnaam = new JTextFieldTGI();
		txtPadnaam.setBounds(170, 34, 294, 20);
		getContentPane().add(txtPadnaam);
		txtPadnaam.setColumns(10);
		
		JButton btnSelectFile = new JButton("...");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked(e);
			}
		});
		btnSelectFile.setBounds(470, 33, 89, 23);
		getContentPane().add(btnSelectFile);
	}
	@Override
	public InfoBase collectData(){
		template.setNaam(txtNaam.getText()); 
		template.setOmschrijving(taOmschrijving.getText());
		template.setPadnaam(txtPadnaam.getText());
		return template;
	}
	protected void btnSelectFileClicked(ActionEvent e) {
		File f = new File(txtPadnaam.getText());
		String path = f.getPath();
		String chosenfile;
		
		FileDialog fd = new FileDialog((JFrame)null, "Selecteer template", FileDialog.LOAD);
		fd.setFile(f.getName());
		fd.setDirectory(path);
		fd.setMultipleMode(false);
		fd.setVisible(true);
		if (fd.getFile() == null){
		}else{
			chosenfile = fd.getDirectory() + fd.getFile();
			if (chosenfile.isEmpty()){
			}else{
				txtPadnaam.setText(chosenfile);
			}
		}
	}
	
}
