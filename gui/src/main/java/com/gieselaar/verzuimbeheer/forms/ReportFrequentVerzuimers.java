package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

//import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;
//import com.gieselaar.verzuimbeheer.reporthelpers.ReportSourceClass;
//import com.gieselaar.verzuimbeheer.reports.ReportViewerFrame;
import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JSpinner;
import javax.swing.JButton;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import reportdatasources.FrequentVerzuimersDatasource;

public class ReportFrequentVerzuimers extends BaseDetailform  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2862612204474930868L;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<TypeEntry>();
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;
	private JComboBox<TypeEntry> cmbHolding = new JComboBox<TypeEntry>();
	private JSpinner spinAantal = new JSpinner(); 
	/**
	 * Getters and setters of this dialog
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInfo(){
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
		} catch (ServiceLocatorException | PermissionException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		
		DefaultComboBoxModel soortmodelWG = new DefaultComboBoxModel();
		DefaultComboBoxModel soortmodelHD = new DefaultComboBoxModel();

        cmbWerkgever.setModel(soortmodelWG);
        cmbHolding.setModel(soortmodelHD);
		TypeEntry dummy = new TypeEntry(-1,"[geen]");
		soortmodelWG.addElement(dummy);
		soortmodelHD.addElement(dummy);
        for (WerkgeverInfo wgr: werkgevers)
		{
			TypeEntry entry = new TypeEntry(wgr.getId(), wgr.getNaam());
			soortmodelWG.addElement(entry);
		}
		for (HoldingInfo a: holdings)
		{
			TypeEntry act = new TypeEntry(a.getId(), a.getNaam());
			soortmodelHD.addElement(act);
		}
		spinAantal.setValue(3);
        activateListener();
	}
	/**
	 * Create the frame.
	 */
	public ReportFrequentVerzuimers(JDesktopPaneTGI mdiPanel) {
		super("Report Frequent Verzuim", mdiPanel);
		setTitle("Rapport Frequent verzuimers");
		initialize();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize(){
		setBounds(100, 100, 526, 312);
		getContentPane().setLayout(null);
		
		JLabel lblNaam = new JLabel("Werkgever");
		lblNaam.setBounds(30, 47, 84, 14);
		getContentPane().add(lblNaam);
		
		cmbWerkgever = new JComboBox();
		cmbWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmbWerkgever.getSelectedIndex() != 0)
					cmbHolding.setSelectedIndex(0);
			}
		});
		cmbWerkgever.setBounds(171, 44, 256, 20);
		getContentPane().add(cmbWerkgever);
		
		JLabel lblHolding = new JLabel("Holding");
		lblHolding.setBounds(30, 22, 84, 14);
		getContentPane().add(lblHolding);
		
		cmbHolding = new JComboBox();
		cmbHolding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmbHolding.getSelectedIndex() != 0)
					cmbWerkgever.setSelectedIndex(0);
			}
		});
		cmbHolding.setBounds(171, 19, 256, 20);
		getContentPane().add(cmbHolding);
		
		dtpStartdatum = new DatePicker();
		dtpStartdatum.setBounds(171, 69, 87, 21);
		dtpStartdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(171, 92, 87, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblVan = new JLabel("Van");
		lblVan.setBounds(30, 72, 84, 14);
		getContentPane().add(lblVan);
		
		JLabel lblTot = new JLabel("Tot en met");
		lblTot.setBounds(30, 95, 84, 14);
		getContentPane().add(lblTot);
		
		JLabel lblMinumumAantalVerzuimen = new JLabel("Minimum aantal verzuimen");
		lblMinumumAantalVerzuimen.setBounds(30, 164, 137, 15);
		getContentPane().add(lblMinumumAantalVerzuimen);
		
		spinAantal = new JSpinner();
		spinAantal.setBounds(171, 162, 29, 20);
		getContentPane().add(spinAantal);
		
		JButton btnAfdrukken = new JButton("Afdrukken...");
		btnAfdrukken.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnAfdrukkenClicked(e);
				} catch (ValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}));
		
		btnAfdrukken.setBounds(324, 160, 103, 23);
		getContentPane().add(btnAfdrukken);
		
	}
	protected void btnAfdrukkenClicked(ActionEvent e) throws ValidationException {
		TypeEntry entry;
		Integer werkgeverid;
		Integer holdingid;
		entry = (TypeEntry) cmbWerkgever.getSelectedItem();
		werkgeverid = entry.getValue();
		entry = (TypeEntry) cmbHolding.getSelectedItem();
		holdingid = entry.getValue();
		
	/*	if (werkgeverid == -1 && holdingid == -1){
        	JOptionPane.showMessageDialog(this,"Geen bedrijf gekozen!");
        	return;
		}
	*/	
		if (dtpEinddatum.getDate().before(dtpStartdatum.getDate())){
        	JOptionPane.showMessageDialog(this,"Einddatum ligt voor de startdatum!");
        	return;
		}
		if (dtpEinddatum.getDate().equals(dtpStartdatum.getDate())){
        	JOptionPane.showMessageDialog(this,"Einddatum is gelijk aan startdatum!");
        	return;
		}
		if (dtpEinddatum.getDate().after(new Date())){
        	JOptionPane.showMessageDialog(this,"Einddatum ligt in de toekomst!");
        	return;
		}
		
		
		if (this.getLoginSession() != null)
        {
        	try {
        		// Get the data to show in the report
//        		ReportSourceClass repSource = new ReportSourceClass();
//        		repSource.setSessionRemote(getLoginSession());

        		List<WerknemerVerzuimInfo> verzuimen;

        		verzuimen = ServiceCaller.reportFacade(getLoginSession()).getWerknemerVerzuimen(werkgeverid, holdingid, -1, dtpStartdatum.getDate(), dtpEinddatum.getDate(),(int) spinAantal.getValue());
				
//				ReportViewerFrame viewer = new ReportViewerFrame();
//        		viewer.createReport(repSource.getReportAbsolutePath("FrequentVerzuim.rpt"));
        		
//        		viewer.setDatatable(verzuimen, WerknemerVerzuimInfo.class, "WerknemerVerzuimInfo", "");
//        		viewer.setParameterFieldValue("", "startdatum", dtpStartdatum.getDate());
//        		viewer.setParameterFieldValue("", "einddatum", dtpEinddatum.getDate());
//        		viewer.setParameterFieldValue("", "minimumaantalverzuimen", spinAantal.getValue());
//        		viewer.setReportSource();

        		FrequentVerzuimersDatasource ds = new FrequentVerzuimersDatasource(dtpStartdatum.getDate(), dtpEinddatum.getDate(), this.getLoginSession());
        		JasperPrint print;
        		try {
        			print = ds.getReport(werkgeverid, holdingid, -1, (Integer)spinAantal.getValue());
        			JFrame frame = new JFrame("Report");
        			frame.getContentPane().add(new JRViewer(print));
        			frame.pack();
        			frame.setVisible(true);
        		} catch (ValidationException e1) {
        			ExceptionLogger.ProcessException(e1, this);
                	return;
        		}
        		
        	} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (ServiceLocatorException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e2) {
				ExceptionLogger.ProcessException(e2,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
	protected void cancelButtonClicked(ActionEvent e) {
		this.setDatachanged(false);
		super.cancelButtonClicked(e);
	}
	protected void okButtonClicked(ActionEvent e) {
		super.okButtonClicked(e);
	}
}
