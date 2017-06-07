package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import reportdatasources.VerzuimOverzichtDatasource;

public class ReportVerzuimoverzicht extends BaseDetailform  {

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
	private JCheckBox cbInclusiefZwangerschap;
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
        HoldingInfo.sort(holdings, HoldingInfo.__sortcol.NAAM);
		for (HoldingInfo a: holdings)
		{
			TypeEntry act = new TypeEntry(a.getId(), a.getNaam());
			soortmodelHD.addElement(act);
		}
        activateListener();
	}
	/**
	 * Create the frame.
	 */
	public ReportVerzuimoverzicht(JDesktopPaneTGI mdiPanel) {
		super("Beheer Activiteit", mdiPanel);
		setTitle("Rapport verzuimoverzicht");
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
		dtpStartdatum.setBounds(171, 73, 87, 21);
		dtpStartdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(171, 97, 87, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblVan = new JLabel("Van");
		lblVan.setBounds(30, 76, 84, 14);
		getContentPane().add(lblVan);
		
		JLabel lblTot = new JLabel("Tot en met");
		lblTot.setBounds(30, 100, 84, 14);
		getContentPane().add(lblTot);
		
		cbInclusiefZwangerschap = new JCheckBox("Inclusief zwangerschap");
		cbInclusiefZwangerschap.setBounds(171, 168, 160, 23);
		getContentPane().add(cbInclusiefZwangerschap);
		
		JButton btnAfdrukken = new JButton("Afdrukken...");
		btnAfdrukken.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAfdrukkenClicked(e);
			}
		}));
		btnAfdrukken.setBounds(325, 200, 102, 23);
		getContentPane().add(btnAfdrukken);
		
	}
	protected void btnAfdrukkenClicked(ActionEvent e) {
		TypeEntry entry;
		Integer werkgeverid;
		Integer holdingid;
		entry = (TypeEntry) cmbWerkgever.getSelectedItem();
		werkgeverid = entry.getValue();
		entry = (TypeEntry) cmbHolding.getSelectedItem();
		holdingid = entry.getValue();
		
		if (werkgeverid == -1 && holdingid == -1){
        	JOptionPane.showMessageDialog(this,"Geen bedrijf gekozen!");
        	return;
		}
		
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
//        		ReportSourceClass repSource = new ReportSourceClass();
//        		repSource.setSessionRemote(getLoginSession());

//				ReportClientDocument repClientDoc;
//				try {
//					repClientDoc = repSource.getReportClientDocVerzuimOverzicht(werkgeverid, holdingid, dtpStartdatum.getDate(), dtpEinddatum.getDate(), cbInclusiefZwangerschap.isSelected());
//				} catch (validationException e1) {
					// TODO Auto-generated catch block
//					ExceptionLogger.ProcessException(e1,this);
//					return;
//				}
//				ReportViewerFrame viewer = new ReportViewerFrame();
//				viewer.setReportSource(repClientDoc.getReportSource());

        		VerzuimOverzichtDatasource ds = new VerzuimOverzichtDatasource(dtpStartdatum.getDate(), dtpEinddatum.getDate(), this.getLoginSession());
        		JasperPrint print;
        		if (holdingid.equals(-1)){
        			print = ds.getReport(werkgeverid, holdingid, -1, "", cbInclusiefZwangerschap.isSelected());
        		}else{
        			String holdingnaam = cmbHolding.getSelectedItem().toString();
        			print = ds.getReport(werkgeverid, holdingid, -1, holdingnaam, cbInclusiefZwangerschap.isSelected());
        		}
       			JFrame frame = new JFrame("Report");
       			JRViewer vwr = new JRViewer(print);
       			vwr.setFitPageZoomRatio();
       			frame.getContentPane().add(vwr);
       			frame.setSize(1200, 800);
       			frame.setVisible(true);
				
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
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
class openverzuimcompare implements Comparator<ActueelVerzuimInfo>{
	@Override
	public int compare(ActueelVerzuimInfo o1, ActueelVerzuimInfo o2) {
		if (o1.getWerkgeverid() < (o2.getWerkgeverid()))
			return -1;
		else
			if (o1.getWerkgeverid() > (o2.getWerkgeverid()))
				return 1;
			else{
				// zelfde werkgeverid
				if(o1.getAfdelingid().intValue() < o2.getAfdelingid().intValue())
					return -1;
				else
					if (o1.getAfdelingid().intValue() > o2.getAfdelingid().intValue())
						return 1;
					else{
						// zelfde afdeling;
						if (o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam()) < -1)
							return -1;
						else
							if (o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam()) < -1)
								return -1;
							else
								if (o1.getWerknemerid() < o2.getWerknemerid())
									return -1;
								else
									if (o1.getWerknemerid() > o2.getWerknemerid())
										return 1;
									else{
										// zelfde werknemer
										if (o1.getVerzuimid() < o2.getVerzuimid())
											return -1;
										else
											if (o1.getVerzuimid() > o2.getVerzuimid())
												return 1;
											else
												return 0;
									}
					}
			}
	}
}
