package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.VerzuimAuthorisation;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.util.Calendar;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JButton;

import reportdatasources.WerkzaamhedenDatasource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;

public class WerkzaamhedenOverzicht extends BaseListForm {
	private static final long serialVersionUID = 1L;
	private List<WerkzaamhedenInfo> werkzaamheden;
	private List<GebruikerInfo> gebruikers;
	private WerkzaamhedenInfo werk;
	private Component thisform = this;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private Hashtable<Integer, Object> hashWerkgevers = new Hashtable<Integer, Object>();
	private Hashtable<Integer, Object> hashGebruikers = new Hashtable<Integer, Object>();
	private JLabel lblPeriodevan;
	private DefaultComboBoxModel<TypeEntry> werkgeverModel;
	private DefaultComboBoxModel<TypeEntry> holdingModel;
	private DefaultComboBoxModel<TypeEntry> gebruikerModel;
	private DatePicker dtpPeriodeeind;
	private DatePicker dtpPeriodestart;
	private JComboBox<TypeEntry> cmbGebruiker;
	private JComboBox<TypeEntry> cmbWerkgever;
	private JComboBox<TypeEntry> cmbHolding;
	private Integer holding;
	private Integer werkgever;
	
	private boolean initialized = false;
	
	/**
	 * Create the frame.
	 */

	public WerkzaamhedenOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Werkzaamheden", mdiPanel);
		getExportButton().setLocation(0, 146);
		initialize();
	}

	public void initialize() {
		setBounds(100, 100, 754, 454);
		setDetailFormClass(WerkzaamhedenDetail.class, WerkzaamhedenInfo.class);
		this.setFiltering(true);

		getPanelAction().setLocation(10, 229);
		getScrollPane().setBounds(39, 112, 687, 271);

		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(39, 87, 75, 14);
		getContentPane().add(lblWerkgever);

		lblPeriodevan = new JLabel("Periode van:");
		lblPeriodevan.setBounds(39, 38, 86, 14);
		getContentPane().add(lblPeriodevan);

		JLabel lblTot = new JLabel("tot:");
		lblTot.setBounds(227, 38, 31, 14);
		getContentPane().add(lblTot);

		dtpPeriodeeind = new DatePicker();
		dtpPeriodeeind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		dtpPeriodeeind.setBounds(263, 37, 86, 21);
		dtpPeriodeeind.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpPeriodeeind);

		dtpPeriodestart = new DatePicker();
		dtpPeriodestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		dtpPeriodestart.setBounds(135, 37, 86, 21);
		dtpPeriodestart.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpPeriodestart);

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND, 0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dtpPeriodeeind.setDate(sdf.parse(sdf.format(cal.getTime())));
			cal.set(Calendar.DAY_OF_MONTH, 1);
			dtpPeriodestart.setDate(sdf.parse(sdf.format(cal.getTime())));
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		} catch (ParseException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		
		cmbWerkgever = new JComboBox<>();
		cmbWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWerkgeverClicked(e);
			}
		});
		cmbWerkgever.setBounds(136, 84, 214, 20);
		getContentPane().add(cmbWerkgever);

		JLabel lblGebruiker = new JLabel("Gebruiker");
		lblGebruiker.setBounds(359, 63, 75, 14);
		getContentPane().add(lblGebruiker);

		cmbGebruiker = new JComboBox<>();
		cmbGebruiker.setBounds(415, 60, 238, 20);
		cmbGebruiker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbGebruikerClicked(e);
			}
		});
		getContentPane().add(cmbGebruiker);
		
		JButton btnReport = new JButton("Overzicht");
		btnReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				btnReportClicked();
			}
		});
		btnReport.setBounds(414, 34, 97, 23);
		getContentPane().add(btnReport);
		
		JLabel lblHolding = new JLabel("Holding");
		lblHolding.setBounds(38, 63, 75, 14);
		getContentPane().add(lblHolding);
		
		cmbHolding = new JComboBox<TypeEntry>();
		cmbHolding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbHoldingClicked(e);
			}
		});
		cmbHolding.setBounds(135, 60, 214, 20);
		getContentPane().add(cmbHolding);
		this.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				populateTable();
			}

			@Override
			public void newCreated(InfoBase info) {
				WerkzaamhedenInfo werkzaamheid = (WerkzaamhedenInfo) info;
				werkzaamheid.setUserid(getLoginSession().getGebruiker().getId());
				werkzaamheid.setUren(BigDecimal.ZERO);
				werkzaamheid.setAantalkm(BigDecimal.ZERO);
				werkzaamheid.setOverigekosten(BigDecimal.ZERO);
				werkzaamheid.setDatum(new Date());
				werkzaamheid.setSoortwerkzaamheden(__werkzaamhedensoort.SECRETARIAAT);
			}

			@Override
			public __continue newButtonClicked() {
				return __continue.allow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform,
							"Weet u zeker dat u de regel wilt verwijderen?",
							"", JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.factuurFacade(getLoginSession())
								.deleteWerkzaamheid((WerkzaamhedenInfo) info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e, thisform, false);
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (Exception e1) {
					ExceptionLogger.ProcessException(e1, thisform);
				}
				return __continue.allow;
			}
		});

		addColumn("Werkgever", null, 120);
		addColumn("Datum", null, 60, Date.class);
		addColumn("Naam", null, 100);
		addColumn("Plaats", null, 80);
		addColumn("Door", null, 90);
		addColumn("Activiteit", null, 100);
		addColumn("Uren", null, 40, BigDecimal.class);
		addColumn("Km", null, 40, BigDecimal.class);
		addColumn("Kosten", null, 40, BigDecimal.class);
	}

	protected void btnReportClicked() {
		Integer gebruiker = null;
		Integer werkgever = null;
		Integer holding = null;
		if (this.getLoginSession() != null)
        {
			try{
				URL url = JRLoader.getResource("WerkzaamhedenOverzicht.jasper");
				if (url == null){
					System.out.println("Kan resource WerkzaamhedenOverzicht.jasper niet vinden!");
					throw new RuntimeException("Kan resource WerkzaamhedenOverzicht.jasper niet vinden!");
				}
				String path = new File(url.getFile()).getParent();
		
				Map<String,Object> parameters = new HashMap<String, Object>();
				parameters.put("SUBREPORT_DIR", path);
				parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl","NL"));
				JasperReport report;
		
				List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
				bedrijfsgegevens.add(ServiceCaller.instantieFacade(getLoginSession()).allBedrijfsgegevens().get(0));
				parameters.put("Footer_datasource",bedrijfsgegevens);
				parameters.put("holdingid", holding);
				parameters.put("werkgeverid", werkgever);
				report = (JasperReport) JRLoader.loadObject(url);

				WerkzaamhedenDatasource wzds = new WerkzaamhedenDatasource(werkgevers, gebruikers, holdings);

				TypeEntry selectedGebruiker = (TypeEntry) cmbGebruiker.getSelectedItem();
				gebruiker = selectedGebruiker.getValue();

				TypeEntry selectedWerkgever = (TypeEntry) cmbWerkgever.getSelectedItem();
				werkgever = selectedWerkgever.getValue();
				
				TypeEntry selectedHolding = (TypeEntry) cmbHolding.getSelectedItem();
				holding = selectedHolding.getValue();
				
				JasperPrint print = JasperFillManager.fillReport(report, parameters, wzds.getDataSource(werkzaamheden, gebruiker, werkgever, holding));

				JFrame frame = new JFrame("Report");
				frame.getContentPane().add(new JRViewer(print));
				frame.pack();
				frame.setVisible(true);
			} catch (JRException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (ServiceLocatorException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
		
	}

	protected void cmbGebruikerClicked(ActionEvent e) {
		populateTable();
	}

	protected void cmbWerkgeverClicked(ActionEvent e) {
		populateTable();
	}

	protected void cmbHoldingClicked(ActionEvent e) {
		populateTable();
	}

	public void setInfo(InfoBase info) {
		werk = (WerkzaamhedenInfo) info;
		/*
		 * We gaan nu alle details ophalen van deze werknemer en de cascodes
		 */
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession())
					.allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession())
					.getHoldings();
			gebruikers = ServiceCaller.autorisatieFacade(getLoginSession()).getGebruikers();
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}

		werkgeverModel = new DefaultComboBoxModel<TypeEntry>();
		werkgeverModel.addElement(new TypeEntry(-1, "[Alle]"));
		cmbWerkgever.setModel(werkgeverModel);
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		for (WerkgeverInfo wi : werkgevers) {
			werkgeverModel.addElement(new TypeEntry(wi.getId(), wi.getNaam()));
			hashWerkgevers.put(wi.getId(), wi.getNaam());
		}

		holdingModel = new DefaultComboBoxModel<TypeEntry>();
		holdingModel.addElement(new TypeEntry(-1, "[]"));
		cmbHolding.setModel(holdingModel);
		HoldingInfo.sort(holdings, HoldingInfo.__sortcol.NAAM);
		for (HoldingInfo wi : holdings) {
			holdingModel.addElement(new TypeEntry(wi.getId(), wi.getNaam()));
		}

		gebruikerModel = new DefaultComboBoxModel<TypeEntry>();
		gebruikerModel.addElement(new TypeEntry(-1, "[Alle]"));
		cmbGebruiker.setModel(gebruikerModel);
		for (GebruikerInfo g : gebruikers) {
			String naam = g.getVoornaam() + " " + g.getTussenvoegsel() + " "
					+ g.getAchternaam();
			gebruikerModel.addElement(new TypeEntry(g.getId(), naam));
			hashGebruikers.put(g.getId(), naam);
		}
		if (VerzuimAuthorisation.isAuthorised(this.getLoginSession().getGebruiker(), __applicatiefunctie.WERKZAAMHEDENALLUSERS)){
			cmbGebruiker.setEnabled(true);
		}else{
			cmbGebruiker.setEnabled(false);
		}
		/*
		 * Standaard selecteren we alleen de werkzaamheden van de ingelogde gebruiker
		 */
		for (int i = 0; i < cmbGebruiker.getModel().getSize(); i++) {
			TypeEntry soort = (TypeEntry) cmbGebruiker.getModel()
					.getElementAt(i);
			if (soort.getValue() == getLoginSession().getGebruiker().getId()) {
				cmbGebruiker.getModel().setSelectedItem(soort);
				break;
			}
		}

		initialized = true;
		cmbHoldingClicked(null);
	}

	private void displayWerkzaamheden() {
		if (!initialized)
			return;
		List<RowSorter.SortKey> sortKeys;
		TypeEntry selectedWerkgever = (TypeEntry) cmbWerkgever.getSelectedItem();
		werkgever = selectedWerkgever.getValue();
		
		TypeEntry selectedHolding= (TypeEntry) cmbHolding.getSelectedItem();
		holding = selectedHolding.getValue();

		deleteAllRows();
		if (werkzaamheden != null) {
			for (WerkzaamhedenInfo v : werkzaamheden) {
				if (v.getAction() == persistenceaction.DELETE){
					;
				} else {
					Vector<Object> rowData = new Vector<Object>();
					if (holding.equals(-1)){
						rowData.add(hashWerkgevers.get(v.getWerkgeverid()));
					}else{
						rowData.add(selectedHolding.toString());
					}
					rowData.add(v.getDatum());
					rowData.add(v.getPersoon());
					rowData.add(v.getWoonplaats());
					rowData.add(hashGebruikers.get(v.getUserid()));
					rowData.add(v.getSoortwerkzaamheden().toString());
					rowData.add(v.getUren());
					rowData.add(v.getAantalkm());
					rowData.add(v.getOverigekosten());
					addRow(rowData, v);
				}
			}
			sortKeys = new ArrayList<RowSorter.SortKey>();
			sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
			this.getSorter().setSortKeys(sortKeys);
			this.getSorter().sort();
		}
		// this.getTable().setRowSelectionInterval(0, 0);
	}

	public void populateTable() {
		if (!initialized)
			return;
		Integer gebruiker;
		TypeEntry selectedGebruiker = (TypeEntry) cmbGebruiker.getSelectedItem();
		if (selectedGebruiker.getValue() == -1){
			gebruiker = null;
		} else {
			gebruiker = selectedGebruiker.getValue();
		}

		TypeEntry selectedHolding = (TypeEntry) cmbHolding.getSelectedItem();
		holding = selectedHolding.getValue();
		if (holding.equals(-1)){
			cmbWerkgever.setEnabled(true);
		}else{
			cmbWerkgever.setEnabled(false);
		}
		
		TypeEntry selectedWerkgever = (TypeEntry) cmbWerkgever.getSelectedItem();
		werkgever = selectedWerkgever.getValue();

		try {
			if (holding.equals(-1) && werkgever.equals(-1)){
				werkzaamheden = ServiceCaller
					.factuurFacade(getLoginSession())
					.getWerkzaamheden(
							gebruiker,
							dtpPeriodestart.getDate(), dtpPeriodeeind.getDate());
			}else{
				if (holding.equals(-1)){
					werkzaamheden = ServiceCaller
							.factuurFacade(getLoginSession())
							.getWerkzaamhedenWerkgever(
									gebruiker,
									dtpPeriodestart.getDate(), dtpPeriodeeind.getDate(), werkgever);
				}else{
					werkzaamheden = ServiceCaller
							.factuurFacade(getLoginSession())
							.getWerkzaamhedenHolding(
									gebruiker,
									dtpPeriodestart.getDate(), dtpPeriodeeind.getDate(), holding);
				}
			}
		} catch (PermissionException e2) {
			ExceptionLogger.ProcessException(e2, this,
					"Kan werkzaamheden niet opvragen");
			return;
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		displayWerkzaamheden();
	}
}
