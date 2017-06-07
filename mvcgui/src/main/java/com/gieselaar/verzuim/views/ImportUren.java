package com.gieselaar.verzuim.views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ImportController;
import com.gieselaar.verzuim.controllers.AbstractController.__filedialogtype;
import com.gieselaar.verzuim.controllers.AbstractController.__selectfileoption;
import com.gieselaar.verzuim.controllers.ImportController.__importresultfields;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import javax.swing.JTabbedPane;

public class ImportUren extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String PANELRESULTS = "Results"; 
	private JTextFieldTGI txtDocumentlocatie;
	private JTextFieldTGI txtSeparator;
	private JCheckBox chckbxVeldnamenrij ;
	private DatatablePanel tpResults;
	private List<ImportResult> importresults;
	private ImportController importcontroller;
	
	public ImportUren(AbstractController controller) {
		super("Importeer uren", controller);
		importcontroller = (ImportController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
    	txtDocumentlocatie.setText("");
	}
	
	void initialize(){
		setBounds(100, 100, 625, 429);
		
		JLabel lblDocumentlocatie = new JLabel("Document (.csv)");
		lblDocumentlocatie.setBounds(20, 46, 95, 14);
		getContentPane().add(lblDocumentlocatie);
		
		txtDocumentlocatie = new JTextFieldTGI();
		txtDocumentlocatie.setBounds(122, 43, 286, 20);
		getContentPane().add(txtDocumentlocatie);
		
		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked(e);
			}
		});
		btnNewButton.setBounds(415, 42, 35, 23);
		getContentPane().add(btnNewButton);
		
		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setBounds(20, 71, 60, 14);
		getContentPane().add(lblSeparator);
		
		txtSeparator = new JTextFieldTGI();
		txtSeparator.setText(";");
		txtSeparator.setBounds(122, 68, 20, 20);
		getContentPane().add(txtSeparator);
		txtSeparator.setColumns(1);
		
		chckbxVeldnamenrij = new JCheckBox("Veldnamenrij");
		chckbxVeldnamenrij.setBounds(122, 95, 97, 23);
		getContentPane().add(chckbxVeldnamenrij);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(20, 151, 579, 207);
		getContentPane().add(tabbedPane);
	
		addTabResults(tabbedPane);
		
		JButton btnImporteer = new JButton("Importeer");
		btnImporteer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnimporteerClicked(e);
			}
		});
		btnImporteer.setBounds(122, 117, 89, 23);
		getContentPane().add(btnImporteer);
	
	}
	private void addTabResults(JTabbedPane tabbedPane) {
		tpResults = new DatatablePanel(controller);
		tpResults.getTableScrollPane().setBounds(5, 0, 595, 146);
		tpResults.setName(PANELRESULTS);

		tpResults.addColumn(__importresultfields.RESULTAAT.getValue(),"Resultaat", 80);
		tpResults.addColumn(__importresultfields.INPUT.getValue(),"Input", 70);
		tpResults.addColumn(__importresultfields.FOUTMELDING.getValue(),"Foutmelding", 70);
		tabbedPane.addTab("Resultaten",null,tpResults,null);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){

			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpResults.disableSorter();

				importresults = importcontroller.getImportResults();
				importcontroller.getTableModel(importresults, (ColorTableModel) tpResults.getTable().getModel(), tpResults.colsinview);

				/* Add sort again */
				tpResults.enableSorter();
				displaySummary();
			}
		};
		registerControllerListener(importcontroller, listener);
	}
	protected void btnimporteerClicked(ActionEvent e) {
		String docnaam = txtDocumentlocatie.getText();
		if (docnaam.isEmpty()){
        	JOptionPane.showMessageDialog(this,"Documentnaam is leeg.");
        	return;
		}
		importcontroller.importeerUren(docnaam, txtSeparator.getText(), chckbxVeldnamenrij.isSelected());
	}
	protected void displaySummary() {
		int counterror = 0;
		int countok = 0;
		int countwarn = 0;
		if (importresults == null){
			JOptionPane.showMessageDialog(this, "Bestand niet correct verwerkt, zie serverlog voor details");
			return;
		}
		
		for (ImportResult rslt:importresults){
			if (rslt.isImportok()){
				// rowData.add("OK");
				countok++;
			}else{
				if (rslt.isWarning()){
					countwarn++;
				}else{
					counterror++;
				}
			}
		}
		if (counterror > 0){
			JOptionPane.showMessageDialog(this, "Fouten gevonden tijdens importeren. Import mislukt");
		}else{
			if (countwarn > 0){
				JOptionPane.showMessageDialog(this, "Import succesvol, problemen gevonden: " + countwarn + ", aantal verwerkt: " + countok);
			}else{
				JOptionPane.showMessageDialog(this, "Import succesvol, aantal verwerkt: " + countok);
			}
		}
	}
	protected void btnSelectFileClicked(ActionEvent e) {
		File f = importcontroller.selectFilename(__selectfileoption.FILEONLY,__filedialogtype.OPEN, txtDocumentlocatie.getText(), ".csv");
		txtDocumentlocatie.setText(f.getPath());
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
