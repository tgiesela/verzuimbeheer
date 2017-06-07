package com.gieselaar.verzuim.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ImportController;
import com.gieselaar.verzuim.controllers.ImportController.__importbestandfields;
import com.gieselaar.verzuim.controllers.ImportController.__importresultfields;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.interfaces.WerkgeverNotification;
import com.gieselaar.verzuim.utils.CursorController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import javax.swing.JTabbedPane;

public class ImportWerknemers extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String PANELRESULTS = "Importresults"; 
	private WerkgeverInfo werkgever;
	private List<WerkgeverInfo> werkgevers;
	private JTextFieldTGI txtSeparator;
	private JCheckBox chckbxVeldnamenrij ;
	private DatatablePanel tpBestanden; 
	private DatatablePanel tpResults;
	private WerkgeverSelection werkgeverSelection;
	private Component thisform = this;
	private ImportBestand importbestand = null;
	private List<ImportBestand> importbestanden = new ArrayList<>();
	private boolean skipsummary = false;

	private ImportController importcontroller;
	protected List<ImportResult> importresults;
	public ImportWerknemers(AbstractController controller) {
		super("Importeer werknemers", controller);
		importcontroller = (ImportController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		werkgevers = controller.getMaincontroller().getWerkgevers();
	}
	
	void initialize(){
		setBounds(100, 100, 773, 641);
		
		JButton btnNewButton = new JButton("Selecteer bestanden...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked(e);
			}
		});
		btnNewButton.setBounds(30, 11, 145, 23);
		getContentPane().add(btnNewButton);
		
		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setBounds(195, 15, 60, 14);
		getContentPane().add(lblSeparator);
		
		txtSeparator = new JTextFieldTGI();
		txtSeparator.setText(";");
		txtSeparator.setBounds(251, 12, 20, 20);
		getContentPane().add(txtSeparator);
		txtSeparator.setColumns(1);
		
		chckbxVeldnamenrij = new JCheckBox("Veldnamenrij");
		chckbxVeldnamenrij.setBounds(287, 11, 97, 23);
		getContentPane().add(chckbxVeldnamenrij);
		
		JButton btnImporteer = new JButton("Importeer");
		btnImporteer.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnimporteerClicked(e);
			}
		}));
		btnImporteer.setBounds(627, 313, 89, 23);
		getContentPane().add(btnImporteer);
		
		addTabBestanden();
	
		addWerkgeverSelectionPanel();

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(21, 347, 708, 207);
		getContentPane().add(tabbedPane);
		
		addTabResults(tabbedPane);
	}
	private void addTabBestanden() {
		tpBestanden = new DatatablePanel(controller);
		tpBestanden.getPanelAction().setBounds(36, 146, 492, 30);
		tpBestanden.setBounds(21, 101, 695, 176);
		tpBestanden.getTableScrollPane().setBounds(10, 11, 688, 161);

		tpBestanden.addColumn(__importbestandfields.BESTAND.getValue(),"Bestand", 120);
		tpBestanden.addColumn(__importbestandfields.WERKGEVER.getValue(),"Werkgever", 100);
		tpBestanden.addColumn(__importbestandfields.RESULTAAT.getValue(),"Resultaat", 50);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
			}
			@Override
			public void rowSelected(int rownr, Object info) {
				if (info instanceof ImportBestand){
					importbestand = (ImportBestand)info;
					werkgeverSelection.setWerkgeverId(importbestand.getWerkgeverid());
					if (importbestand.results != null){
						importcontroller.setImportresults(importbestand.results);
						importcontroller.setDocumentnaam(importbestand.bestand.getAbsolutePath());
					}
				}
			}
		};
		registerControllerListener(importcontroller, listener);
		getContentPane().add(tpBestanden);
	}
	private void addWerkgeverSelectionPanel() {
		werkgeverSelection = new WerkgeverSelection(WerkgeverSelection.__WerkgeverSelectionMode.SelectBoth,
				  WerkgeverSelection.__WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(21, 280, 378, 56);
		werkgeverSelection.setMaincontroller(controller.getMaincontroller());
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				if (importbestand == null)
					return false;
				importbestand.setWerkgeverid(werkgeverid);
				try {
					werkgever = werkgeverSelection.getWerkgever();
					importbestand.setWerkgevernaam(werkgever.getNaam());
					displayImportBestanden();
				} catch (Exception e1) {
					ExceptionLogger.ProcessException(e1, thisform);
					return false;
				}

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
		tpResults.getTableScrollPane().setBounds(5, 0, 688, 146);
		tpResults.setName(PANELRESULTS);
		
		tpResults.addColumn(__importresultfields.RESULTAAT.getValue(),"Resultaat", 80);
		tpResults.addColumn(__importresultfields.INPUT.getValue(),"Input", 200);
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
				if (!skipsummary){
					displaySummary();
				}
			}
		};

		registerControllerListener(importcontroller, listener);
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
	protected void btnimporteerClicked(ActionEvent e) {
		skipsummary = true;
		for (ImportBestand row:importbestanden){
			String docnaam = row.getBestand().getAbsolutePath();
			if (docnaam.isEmpty()){
	        	JOptionPane.showMessageDialog(this,"Documentnaam is leeg.");
	        	return;
			}
			if (!row.isProcessed()){
				importcontroller.importWerknemers(docnaam, row.getWerkgeverid(), txtSeparator.getText(), chckbxVeldnamenrij.isSelected());
				row.setResults(importcontroller.getImportResults());
        		setProcessingResult(row, row.getResults());
        	}
		}
		skipsummary = false;
		displayImportBestanden();
	}
	protected void displayImportBestanden() {
		ColorTableModel tblmodel = (ColorTableModel) tpBestanden.getTable().getModel();
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(tpBestanden.colsinview.size());
		if (importbestanden != null){
			for (ImportBestand info : importbestanden) {
				List<Object> colsinmodel = new ArrayList<>();
	
				setColumnValues(colsinmodel, info, tpBestanden.colsinview);
				if (info.getWerkgeverid() == -1){
					tblmodel.addRow(colsinmodel, info, Color.orange);
				}else{
					if (info.getOkCount() > 0){
						if (info.getErrorCount() == 0 && info.getWarnCount() == 0){
							tblmodel.addRow(colsinmodel, info,Color.green);
						}else{
							if (info.getErrorCount() > 0){
								tblmodel.addRow(colsinmodel, info,Color.red);
							}else{
								tblmodel.addRow(colsinmodel, info,Color.yellow);
							}
						}
					}else{
						if (info.getErrorCount() > 0){
							tblmodel.addRow(colsinmodel, info,Color.red);
						}else{
							if (info.getWarnCount() > 0){
								tblmodel.addRow(colsinmodel, info,Color.yellow);
							}else{
								tblmodel.addRow(colsinmodel, info);
							}
						}
					}
				}
			}
		}
	}
	private void setColumnValues(List<Object> colsinmodel, ImportBestand wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__importbestandfields val = __importbestandfields.parse(colsinview.get(i));
			switch (val) {
			case BESTAND:
				colsinmodel.add(i, wfi.getBestand().getName());
				break;
			case RESULTAAT:
				colsinmodel.add(i, "");
				break;
			case WERKGEVER:
				colsinmodel.add(i, wfi.getWerkgevernaam());
				break;
			default:
				break;
			}
		}
	}
	private void setProcessingResult(ImportBestand row, List<ImportResult> results) {
		int countok, countwarn, counterror;
		countok=countwarn=counterror=0;
		for (ImportResult rslt:results){
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
		row.setErrorCount(counterror);
		row.setWarnCount(countwarn);
		row.setOkCount(countok);
		if (counterror > 0){
			row.setErrorMessage("Fouten gevonden tijdens importeren. Import mislukt");
		}else{
			if (countwarn > 0){
				row.setErrorMessage("Import succesvol, problemen gevonden: " + countwarn + ", aantal verwerkt: " + countok);
			}else{
				row.setErrorMessage("Import succesvol, aantal verwerkt: " + countok);
				row.setProcessed(true);
			}
		}
	}
	protected void btnSelectFileClicked(ActionEvent e) {
		File f = new File("");
		String path = f.getPath();
		File[] chosenfiles;
		
		FileDialog fd = new FileDialog((JFrame)null, "Selecteer bestanden", FileDialog.LOAD);
		fd.setFile(f.getName());
		fd.setDirectory(path);
		fd.setMultipleMode(true);
		//fd.setLocation(50, 50);
		importbestanden = new ArrayList<>();
		
		fd.setVisible(true);
		chosenfiles = fd.getFiles();
		if (chosenfiles == null)
			;
		else
		{
			for (File chosenfile: chosenfiles){
				ImportBestand row = new ImportBestand();

				row.setBestand(chosenfile);
				row.setWerkgeverid(-1);
				
				String filename = chosenfile.getName();
				String werkgevernaam;
				if (filename.startsWith("KFC ")){
					String subparts[] = filename.split("\\.| ");
					werkgevernaam = "KFC";
					if (subparts[1].equalsIgnoreCase("CREW") || subparts[1].equalsIgnoreCase("RGM")){
						for (int i = 2;i<subparts.length-1;i++){
							werkgevernaam = werkgevernaam + " " + subparts[i];
						}
					}else{
						for (int i = 1;i<subparts.length-1;i++){
							werkgevernaam = werkgevernaam + " " + subparts[i];
						}
					}
				}else{
					String subparts[] = filename.split("\\.");
					werkgevernaam = subparts[0];
					for (int i = 1;i<subparts.length-1;i++){
						werkgevernaam = werkgevernaam + " " + subparts[i];
					}
				}
				/*
				 * Attempt to lookup werkgeverid based on werkgevernaam...
				 */
				
				for (WerkgeverInfo wgr:werkgevers){
					if (wgr.getNaam().equalsIgnoreCase(werkgevernaam)){
						row.setWerkgeverid(wgr.getId());
					}
				}
				if (row.getWerkgeverid() == -1){
					row.setWerkgevernaam("");
				}else{
					row.setWerkgevernaam(werkgevernaam);
				}
				importbestanden.add(row);
			}
		}
		displayImportBestanden();
	}
	private class ImportBestand extends InfoBase{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private File bestand;
		private int werkgeverid;
		private String werkgevernaam;
		private String errorMessage;
		private int errorCount=0;
		private int warnCount=0;
		private int okCount=0;
		private boolean processed = false;
		private List<ImportResult> results;
		public File getBestand() {
			return bestand;
		}
		public void setBestand(File bestand) {
			this.bestand = bestand;
		}
		public int getWerkgeverid() {
			return werkgeverid;
		}
		public void setWerkgeverid(int werkgeverid) {
			this.werkgeverid = werkgeverid;
		}
		public String getWerkgevernaam() {
			return werkgevernaam;
		}
		public void setWerkgevernaam(String werkgevernaam) {
			this.werkgevernaam = werkgevernaam;
		}
		public List<ImportResult> getResults() {
			return results;
		}
		public void setResults(List<ImportResult> results) {
			this.results = results;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		public int getErrorCount() {
			return errorCount;
		}
		public void setErrorCount(int errorCount) {
			this.errorCount = errorCount;
		}
		public int getWarnCount() {
			return warnCount;
		}
		public void setWarnCount(int warnCount) {
			this.warnCount = warnCount;
		}
		public int getOkCount() {
			return okCount;
		}
		public void setOkCount(int okCount) {
			this.okCount = okCount;
		}
		public boolean isProcessed() {
			return processed;
		}
		public void setProcessed(boolean processed) {
			this.processed = processed;
		}
		@Override
		public boolean validate() throws ValidationException {
			// TODO Auto-generated method stub
			return false;
		}
	}
	@Override
	public InfoBase collectData() {
		// TODO Auto-generated method stub
		return null;
	}
}

