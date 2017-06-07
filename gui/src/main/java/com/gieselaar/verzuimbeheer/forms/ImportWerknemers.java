package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.baseforms.WerkgeverNotification;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import com.gieselaar.verzuimbeheer.components.TablePanel;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection;

import javax.swing.JTabbedPane;

public class ImportWerknemers extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkgeverInfo werkgever;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private JTextFieldTGI txtSeparator;
	private JCheckBox chckbxVeldnamenrij ;
	private TablePanel tpBestanden; 
	private List<ImportResult> results = null;
	private TablePanel tpResults;
	private WerkgeverSelection wgSelect;
	private Component thisform = this;
	private ImportBestand importbestand = null;
	private List<ImportBestand> importbestanden = new ArrayList<>();
	public ImportWerknemers(JDesktopPaneTGI mdiPanel) {
		super("Importeer werknemers", mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){
		
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			wgSelect.setWerkgevers(werkgevers);
			wgSelect.setHoldings(holdings);
			wgSelect.setWerkgeverId(-1);
			wgSelect.setHoldingId(-1);
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (RuntimeException e) {
			ExceptionLogger.ProcessException(e,this);
			throw e;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
        activateListener();
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
		
		tpBestanden = new TablePanel(this.getMdiPanel());
		tpBestanden.setSize(708, 224);
		tpBestanden.setLocation(21, 45);
		tpBestanden.getPanelAction().setLocation(10, 183);
		tpBestanden.getScrollPane().setBounds(10, 11, 688, 161);
		tpBestanden.setDetailFormClass(null,
				ImportBestand.class);
		tpBestanden.addColumn("Bestand", null, 120);
		tpBestanden.addColumn("Werkgever", null, 100);
		tpBestanden.addColumn("Resultaat", null, 50);
		tpBestanden.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				displayImportBestanden();
			}
			@Override
			public __continue newButtonClicked() {return __continue.dontallow;}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {return __continue.dontallow;}

			@Override
			public __continue detailButtonClicked(InfoBase info) {
				importbestand = (ImportBestand)info;
				URI uri;
				try {
					uri = new URI("File://"
							+ URLEncoder.encode(importbestand.getBestand().getAbsolutePath(), "UTF-8"));
					open(uri);
				} catch (URISyntaxException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (UnsupportedEncodingException e) {
					ExceptionLogger.ProcessException(e,thisform);
				}
				return __continue.dontallow;
			}

			@Override
			public void newCreated(InfoBase info) {
			}
			@Override
			public void rowSelected(InfoBase info){
				importbestand = (ImportBestand)info;
				wgSelect.setWerkgeverId(importbestand.getWerkgeverid());
				if (importbestand.results != null){
					displayResults(importbestand.results);
				}
			}
		});
		getContentPane().add(tpBestanden);
		
		wgSelect = new WerkgeverSelection(WerkgeverSelection.__WerkgeverSelectionMode.SelectBoth,
		 								  WerkgeverSelection.__WerkgeverFilter.Actief);
		wgSelect.setBounds(21, 280, 378, 56);
		getContentPane().add(wgSelect);
		wgSelect.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				if (importbestand == null)
					return false;
				importbestand.setWerkgeverid(werkgeverid);
				try {
					werkgever = ServiceCaller
							.werkgeverFacade(getLoginSession()).getWerkgever(
									werkgeverid);
					importbestand.setWerkgevernaam(werkgever.getNaam());
					displayImportBestanden();
				} catch (ServiceLocatorException e) {
					ExceptionLogger.ProcessException(e, thisform,
							"Unable to connect to server");
					return false;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e, thisform);
					return false;
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
					return false;
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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(21, 347, 708, 207);
		getContentPane().add(tabbedPane);
		
		tpResults = new TablePanel(this.getMdiPanel());
		tpResults.getPanelAction().setLocation(5, 146);
		tpResults.getScrollPane().setBounds(5, 0, 688, 146);
		tpResults.setDetailFormClass(null,
				ImportResult.class);
		tpResults.addColumn("Resultaat", null, 50);
		tpResults.addColumn("Input", null, 350);
		tpResults.addColumn("Foutmelding", null, 70);
		tpResults.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				//displayResults();
			}
			@Override
			public __continue newButtonClicked() {return __continue.dontallow;}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {return __continue.dontallow;}

			@Override
			public __continue detailButtonClicked(InfoBase info) {
				URI uri;
				try {
					uri = new URI("File://"
							+ URLEncoder.encode(importbestand.getBestand().getAbsolutePath(), "UTF-8"));
					open(uri);
				} catch (URISyntaxException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (UnsupportedEncodingException e) {
					ExceptionLogger.ProcessException(e,thisform);
				}
				return __continue.dontallow;
			}

			@Override
			public void newCreated(InfoBase info) {
			}
		});
		tabbedPane.addTab("Resultaten",null,tpResults,null);
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
	protected void btnimporteerClicked(ActionEvent e) {
		for (ImportBestand row:importbestanden){
			String docnaam = row.getBestand().getAbsolutePath();
			if (docnaam.isEmpty()){
	        	JOptionPane.showMessageDialog(this,"Documentnaam is leeg.");
	        	return;
			}
			if (this.getLoginSession() != null )
	        {
				if (!row.isProcessed()){
					try {
		        		File csvFile = new File(docnaam);
		        		FileInputStream input;
		        		byte[] barray;
						try {
							input = new FileInputStream(csvFile);
			        		barray = new byte[(int) csvFile.length()];
			        		input.read(barray);
			        		input.close();
						} catch (FileNotFoundException e1) {
				        	ExceptionLogger.ProcessException(e1,this);
				        	return;
						} catch (IOException e1) {
				        	ExceptionLogger.ProcessException(e1,this);
				        	return;
						}
		        		
		        		results = ServiceCaller.werkgeverFacade(getLoginSession()).importWerknemers(row.getWerkgeverid(), txtSeparator.getText(), barray, chckbxVeldnamenrij.isSelected());
		        		row.setResults(results);
		        		setProcessingResult(row, results);
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
	        }
	        else{
	        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	        }
		}
		displayImportBestanden();
	}
	protected void displayImportBestanden() {
		tpBestanden.deleteAllRows();
		for (ImportBestand row:importbestanden){
			Vector<Object> rowData = new Vector<Object>();
		
			rowData.add(row.getBestand().getName());
			rowData.add(row.getWerkgevernaam());
			rowData.add("");
			if (row.getWerkgeverid() == -1){
				tpBestanden.addRow(rowData, row, Color.orange);
			}else{
				if (row.getOkCount() > 0){
					if (row.getErrorCount() == 0 && row.getWarnCount() == 0){
						tpBestanden.addRow(rowData, row,Color.green);
					}else{
						if (row.getErrorCount() > 0){
							tpBestanden.addRow(rowData, row,Color.red);
						}else{
							tpBestanden.addRow(rowData, row,Color.yellow);
						}
					}
				}else{
					if (row.getErrorCount() > 0){
						tpBestanden.addRow(rowData, row,Color.red);
					}else{
						if (row.getWarnCount() > 0){
							tpBestanden.addRow(rowData, row,Color.yellow);
						}else{
							tpBestanden.addRow(rowData, row);
						}
					}
				}
			}
		}
		tpResults.deleteAllRows();
	}
	protected void displayResults(List<ImportResult> results) {
		int counterror = 0;
		int countok = 0;
		int countwarn = 0;
		tpResults.deleteAllRows();
		for (ImportResult rslt:results){
			Vector<Object> rowData = new Vector<Object>();
			rowData.add("" + rslt.getResult());
			rowData.add(rslt.getSourceLine());
			if (rslt.isImportok()){
				// rowData.add("OK");
				countok++;
			}else{
				if (rslt.isWarning()){
					rowData.add("WARN: " + rslt.getErrorMessage());
					countwarn++;
				}else{
					rowData.add(rslt.getErrorMessage());
					counterror++;
				}
				tpResults.addRow(rowData, rslt);
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
		tpResults.setMdiPanel(this.getMdiPanel());
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
	protected void okButtonClicked(ActionEvent e) {
        super.okButtonClicked(e);
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
		public String getErrorMessage() {
			return errorMessage;
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
}

