package com.gieselaar.verzuimbeheer.importuren;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import com.gieselaar.verzuimbeheer.components.TablePanel;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverSelectionMode;

import javax.swing.JTabbedPane;

public class AfsluitenDienstverbanden extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkgeverInfo werkgever;
	private JTextFieldTGI txtDocumentlocatie;
	private JTextFieldTGI txtSeparator;
	private JCheckBox chckbxVeldnamenrij ;
	private TablePanel tpResults;
	private List<ImportResult> results = null;
	private WerkgeverSelection werkgeverSelection;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	public AfsluitenDienstverbanden(JDesktopPaneTGI mdiPanel) {
		super("Importeer uren", mdiPanel);
		setTitle("Afsluiten dienstverbanden");
		initialize();
	}
	public void setInfo(InfoBase info){
		
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			werkgeverSelection.setWerkgevers(werkgevers);
			werkgeverSelection.setHoldings(holdings);
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
		
		werkgever = (WerkgeverInfo) info;
    	txtDocumentlocatie.setText("");
        activateListener();
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
				btnSelectFileClicked(e);
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
	
		tpResults = new TablePanel(this.getMdiPanel());
		tpResults.getPanelAction().setLocation(5, 146);
		tpResults.getScrollPane().setBounds(5, 0, 595, 146);
		tpResults.setDetailFormClass(null,
				ImportResult.class);
		tpResults.addColumn("Resultaat", null, 80);
		tpResults.addColumn("Input", null, 70);
		tpResults.addColumn("Foutmelding", null, 70);
		tpResults.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				displayResults();
			}
			@Override
			public __continue newButtonClicked() {return __continue.dontallow;}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {return __continue.dontallow;}

			@Override
			public __continue detailButtonClicked(InfoBase info) {return __continue.dontallow;}

			@Override
			public void newCreated(InfoBase info) {
			}
		});
		tabbedPane.addTab("Resultaten",null,tpResults,null);
		
		JButton btnImporteer = new JButton("Importeer");
		btnImporteer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnimporteerClicked(e);
			}
		});
		btnImporteer.setBounds(122, 158, 89, 23);
		getContentPane().add(btnImporteer);
	
		werkgeverSelection = new WerkgeverSelection(__WerkgeverSelectionMode.SelectOne, __WerkgeverFilter.Actief );
		werkgeverSelection.setBounds(10, 11, 378, 63);
		getContentPane().add(werkgeverSelection);
	}
	protected void btnimporteerClicked(ActionEvent e) {
		String docnaam = txtDocumentlocatie.getText();
		if (docnaam.isEmpty()){
        	JOptionPane.showMessageDialog(this,"Documentnaam is leeg.");
        	return;
		}
		if (this.getLoginSession() != null)
        {
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
				Integer holdingid = werkgeverSelection.getHoldingId();
				if (holdingid == null || holdingid == -1)
					throw new ValidationException("Geen holding geselecteerd");
        		results = ServiceCaller.werkgeverFacade(getLoginSession()).afsluitenDienstverbanden(holdingid, txtSeparator.getText(), barray, chckbxVeldnamenrij.isSelected());
        		if (results != null)
        			displayResults();
        		else{
        			JOptionPane.showMessageDialog(this, "Bestand niet correct verwerkt, zie serverlog voor details");
        		}
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
	protected void displayResults() {
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
		tpResults.setMdiPanel(this.getMdiPanel());
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
        super.okButtonClicked(e);
	}
}
