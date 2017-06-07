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
import com.gieselaar.verzuim.interfaces.WerkgeverNotification;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import javax.swing.JTabbedPane;

public class AfsluitenDienstverbanden extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String PANELRESULTS = "Importresults"; 
	private JTextFieldTGI txtDocumentlocatie;
	private JTextFieldTGI txtSeparator;
	private JCheckBox chckbxVeldnamenrij ;
	private DatatablePanel tpResults;
	private List<ImportResult> importresults = null;
	private WerkgeverSelection werkgeverSelection;
	private ImportController importcontroller;
	
	public AfsluitenDienstverbanden(AbstractController controller) {
		super("Afsluiten dienstverbanden", controller);
		importcontroller = (ImportController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		txtDocumentlocatie.setText("");
	}
	
	void initialize(){
		setBounds(100, 100, 625, 481);
		
		JLabel lblDocumentlocatie = new JLabel("Document (.csv)");
		lblDocumentlocatie.setBounds(20, 87, 95, 14);
		getContentPane().add(lblDocumentlocatie);
		
		txtDocumentlocatie = new JTextFieldTGI();
		txtDocumentlocatie.setBounds(122, 84, 286, 20);
		getContentPane().add(txtDocumentlocatie);
		
		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked();
			}
		});
		btnNewButton.setBounds(415, 83, 35, 23);
		getContentPane().add(btnNewButton);
		
		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setBounds(20, 112, 60, 14);
		getContentPane().add(lblSeparator);
		
		txtSeparator = new JTextFieldTGI();
		txtSeparator.setText(";");
		txtSeparator.setBounds(122, 109, 20, 20);
		getContentPane().add(txtSeparator);
		txtSeparator.setColumns(1);
		
		chckbxVeldnamenrij = new JCheckBox("Veldnamenrij");
		chckbxVeldnamenrij.setBounds(122, 136, 97, 23);
		getContentPane().add(chckbxVeldnamenrij);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(20, 203, 579, 207);
		getContentPane().add(tabbedPane);
	
		addTabResults(tabbedPane);
		
		JButton btnImporteer = new JButton("Importeer");
		btnImporteer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnimporteerClicked(e);
			}
		});
		btnImporteer.setBounds(122, 158, 89, 23);
		getContentPane().add(btnImporteer);
	
		addWerkgeverSelectionPanel();
	}
	private void addWerkgeverSelectionPanel() {
		werkgeverSelection = new WerkgeverSelection(__WerkgeverSelectionMode.SelectOne, __WerkgeverFilter.Actief );
		werkgeverSelection.setBounds(10, 11, 378, 63);
		werkgeverSelection.setMaincontroller(controller.getMaincontroller());
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				return false;
			}
		});
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
		Integer holdingid = werkgeverSelection.getHoldingId();
		if (holdingid == null || holdingid == -1){
        	JOptionPane.showMessageDialog(this,"Geen holding geselecteerd.");
		}
		importcontroller.afsluitenDienstverbanden(docnaam, holdingid, txtSeparator.getText(), chckbxVeldnamenrij.isSelected());
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
	protected void btnSelectFileClicked() {
		File f = importcontroller.selectFilename(__selectfileoption.FILEONLY,__filedialogtype.OPEN, txtDocumentlocatie.getText(), ".csv");
		txtDocumentlocatie.setText(f.getPath());
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
