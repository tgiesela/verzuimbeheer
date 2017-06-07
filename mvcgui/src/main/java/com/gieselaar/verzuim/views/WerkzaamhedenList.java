package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.controllers.WerkzaamhedenController;
import com.gieselaar.verzuim.controllers.WerkzaamhedenController.__werkzaamhedenfields;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.util.Calendar;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JButton;

public class WerkzaamhedenList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private VerzuimComboBoxModel werkgevermodel;
	private VerzuimComboBoxModel holdingmodel;
	private VerzuimComboBoxModel gebruikermodel;
	private DatePicker dtpPeriodeeind;
	private DatePicker dtpPeriodestart;
	private JComboBox<TypeEntry> cmbGebruiker;
	private JComboBox<TypeEntry> cmbWerkgever;
	private JComboBox<TypeEntry> cmbHolding;
	private transient List<RowSorter.SortKey> sortKeys;
	private boolean initialized = false;
	private List<WerkzaamhedenInfo> werkzaamheden;

	
	private WerkzaamhedenController werkzaamhedencontroller;
	private ReportController reportcontroller;
	/**
	 * Create the frame.
	 */

	public WerkzaamhedenList(WerkzaamhedenController controller) {
		super("Werkzaamheden", controller);
		werkzaamhedencontroller = controller;
		reportcontroller = werkzaamhedencontroller.getReportController();
		initialize();
		
		werkgevermodel = new VerzuimComboBoxModel(werkzaamhedencontroller);
		werkzaamhedencontroller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, false);
		cmbWerkgever.setModel(werkgevermodel);
		holdingmodel = new VerzuimComboBoxModel(werkzaamhedencontroller);
		werkzaamhedencontroller.getMaincontroller().updateComboModelHoldings(holdingmodel, false);
		cmbHolding.setModel(holdingmodel);
		populateGebruikers();
		
		addColumn(__werkzaamhedenfields.WERKGEVER.getValue(),"Werkgever", 120);
		addColumn(__werkzaamhedenfields.DATUM.getValue(),"Datum", 60, Date.class);
		addColumn(__werkzaamhedenfields.NAAM.getValue(),"Naam", 100);
		addColumn(__werkzaamhedenfields.PLAATS.getValue(),"Plaats", 80);
		addColumn(__werkzaamhedenfields.DOOR.getValue(),"Door", 90);
		addColumn(__werkzaamhedenfields.ACTIVITEIT.getValue(),"Activiteit", 100);
		addColumn(__werkzaamhedenfields.UREN.getValue(),"Uren", 40, BigDecimal.class);
		addColumn(__werkzaamhedenfields.KM.getValue(),"Km", 40, BigDecimal.class);
		addColumn(__werkzaamhedenfields.KOSTEN.getValue(),"Kosten", 40, BigDecimal.class);
		
		sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
		this.getSorter().setSortKeys(sortKeys);
		this.getSorter().sort();
		initialized = true;
	}

	public void initialize() {
		this.setFreespaceontop(90);
		setBounds(50, 50, 754, 454);
		
		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(39, 64, 75, 14);
		getContentPane().add(lblWerkgever);

		JLabel lblPeriodevan = new JLabel("Periode van:");
		lblPeriodevan.setBounds(39, 15, 86, 14);
		getContentPane().add(lblPeriodevan);

		JLabel lblTot = new JLabel("tot:");
		lblTot.setBounds(227, 15, 31, 14);
		getContentPane().add(lblTot);

		dtpPeriodeeind = new DatePicker();
		dtpPeriodeeind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		dtpPeriodeeind.setBounds(263, 14, 86, 21);
		dtpPeriodeeind.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpPeriodeeind);

		dtpPeriodestart = new DatePicker();
		dtpPeriodestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		dtpPeriodestart.setBounds(135, 14, 86, 21);
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
		cmbWerkgever.setBounds(136, 61, 214, 20);
		getContentPane().add(cmbWerkgever);

		JLabel lblGebruiker = new JLabel("Gebruiker");
		lblGebruiker.setBounds(359, 40, 75, 14);
		getContentPane().add(lblGebruiker);

		cmbGebruiker = new JComboBox<>();
		cmbGebruiker.setBounds(415, 37, 238, 20);
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
		btnReport.setBounds(414, 11, 97, 23);
		getContentPane().add(btnReport);
		
		JLabel lblHolding = new JLabel("Holding");
		lblHolding.setBounds(38, 40, 75, 14);
		getContentPane().add(lblHolding);
		
		cmbHolding = new JComboBox<>();
		cmbHolding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbHoldingClicked(e);
			}
		});
		cmbHolding.setBounds(135, 37, 214, 20);
		getContentPane().add(cmbHolding);
	}

	protected void btnReportClicked() {
		Integer gebruikerid;
		Integer werkgeverid;
		Integer holdingid;
		
		werkgeverid = werkgevermodel.getId();
		holdingid = holdingmodel.getId();
		gebruikerid = gebruikermodel.getId();

		try {
			reportcontroller.printWerkzaamhedenOverzicht(werkgeverid, holdingid, gebruikerid, werkzaamheden);
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		
	}

	protected void cmbGebruikerClicked(ActionEvent e) {
		populateTable();
	}

	protected void cmbWerkgeverClicked(ActionEvent e) {
		populateTable();
	}

	protected void cmbHoldingClicked(ActionEvent e) {
		if (holdingmodel.getId().equals(-1)){
			cmbWerkgever.setEnabled(true);
		}else{
			cmbWerkgever.setEnabled(false);
		}
		populateTable();
	}

	public void populateGebruikers() {
		gebruikermodel = werkzaamhedencontroller.getComboModelGebruiker();
		cmbGebruiker.setModel(gebruikermodel);
		/*
		 * Standaard selecteren we alleen de werkzaamheden van de ingelogde gebruiker
		 */
		gebruikermodel.setId(werkzaamhedencontroller.getMaincontroller().getGebruiker().getId());
	}
	@Override
	public void refreshTable() {
		werkzaamheden = werkzaamhedencontroller.getWerkzaamhedenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		werkzaamhedencontroller.getTableModel(werkzaamheden, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}


	public void populateTable() {
		Integer gebruikerid;
		Integer holdingid;
		Integer werkgeverid;

		if (!initialized)
			return;
		
		gebruikerid = gebruikermodel.getId();
		holdingid = holdingmodel.getId();
		werkgeverid = werkgevermodel.getId();

		try {
			if (holdingid.equals(-1) && werkgeverid.equals(-1)){
				werkzaamhedencontroller.selectWerkzaamheden(gebruikerid
														  , dtpPeriodestart.getDate()
														  , dtpPeriodeeind.getDate());
			}else{
				if (holdingid.equals(-1)){
					werkzaamhedencontroller.selectWerkzaamhedenWerkgever(gebruikerid
																	   , dtpPeriodestart.getDate()
																	   , dtpPeriodeeind.getDate()
																	   , werkgeverid);
				}else{
					werkzaamhedencontroller.selectWerkzaamhedenHolding(gebruikerid
																	 , dtpPeriodestart.getDate()
																	 , dtpPeriodeeind.getDate()
																	 , holdingid);
				}
			}
		} catch (VerzuimApplicationException | ValidationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		}
	}
}
