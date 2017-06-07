package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gieselaar.verzuim.controllers.FactuurController;
import com.gieselaar.verzuim.controllers.FactuurController.__factuurcommands;
import com.gieselaar.verzuim.controllers.FactuurController.__factuurfields;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.util.Calendar;

import com.michaelbaranov.microba.calendar.DatePicker;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class FactuurList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private List<FactuurTotaalInfo> facturen;
	private List<WerkgeverInfo> werkgevers;
	private JLabel lblPeriodevan;
	private DatePicker dtpPeriodeeind;
	private DatePicker dtpPeriodestart;
	private JComboBox<TypeEntry> cmbWerkgever;
	private VerzuimComboBoxModel werkgevermodel;

	private boolean initialized = false;
	private JButton btnBetalingen;

	private FactuurController factuurcontroller;
	/**
	 * Create the frame.
	 */

	public FactuurList(FactuurController controller) {
		super("Factuur", controller);
		factuurcontroller = controller;
		
		initialize();

		addColumn(__factuurfields.WERKGEVER.getValue(),"Werkgever", 120);
		addColumn(__factuurfields.FACTUURNR.getValue(),"Factuurnr", 60);
		addColumn(__factuurfields.JAAR.getValue(),"Jaar", 60);
		addColumn(__factuurfields.MAAND.getValue(),"Maand", 60);
		addColumn(__factuurfields.FACTUURBEDRAG.getValue(),"factuurbedrag", 80, BigDecimal.class);
		addColumn(__factuurfields.BETAALDBEDRAG.getValue(),"betaaldbedrag", 80, BigDecimal.class);
		addColumn(__factuurfields.STATUS.getValue(),"Status", 100);
		addColumn(__factuurfields.EMAIL.getValue(),"Email", 150);
		
		List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		this.getSorter().setSortKeys(sortKeys);
		this.getSorter().sort();

		werkgevermodel = new VerzuimComboBoxModel(factuurcontroller);
		factuurcontroller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, false);
		cmbWerkgever.setModel(werkgevermodel);
		
		werkgevers = factuurcontroller.getMaincontroller().getWerkgevers();
		factuurcontroller.getMaincontroller().getHoldings();
		factuurcontroller.getMaincontroller().getGebruikers();
		
		initialized = true;
		
	}

	public void initialize() {
		setBounds(100, 100, 791, 454);
		this.setFreespaceontop(60);

		btnBetalingen = new JButton("Betalingen...");
		btnBetalingen.setSelected(false);
		btnBetalingen.setActionCommand(__factuurcommands.BETALINGENTONEN.toString());
		btnBetalingen.addActionListener(CursorController.createListener(this, controller));
		super.getDatatable().getPanelAction().add(btnBetalingen);

		JCheckBox chckbxHideEmtpyBills = new JCheckBox("Verberg lege facturen");
		chckbxHideEmtpyBills.setSelected(false);
		chckbxHideEmtpyBills.setBounds(297, 422, 104, 23);
		chckbxHideEmtpyBills.setActionCommand(__factuurcommands.VERBERGLEGEFACTUREN.toString());
		chckbxHideEmtpyBills.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(chckbxHideEmtpyBills);

		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(37, 37, 75, 14);
		getContentPane().add(lblWerkgever);

		lblPeriodevan = new JLabel("Periode van:");
		lblPeriodevan.setBounds(38, 12, 86, 14);
		getContentPane().add(lblPeriodevan);

		JLabel lblTot = new JLabel("tot:");
		lblTot.setBounds(226, 12, 31, 14);
		getContentPane().add(lblTot);

		dtpPeriodeeind = new DatePicker();
		dtpPeriodeeind.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		}));
		dtpPeriodeeind.setBounds(262, 11, 86, 21);
		dtpPeriodeeind.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpPeriodeeind);

		dtpPeriodestart = new DatePicker();
		dtpPeriodestart.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dtpPeriodeeind.setDate(dtpPeriodestart.getDate());
				} catch (PropertyVetoException e1) {
				}
			}
		}));
		dtpPeriodestart.setBounds(134, 11, 86, 21);
		dtpPeriodestart.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpPeriodestart);

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.MONTH, -1);
			dtpPeriodeeind.setDate(cal.getTime());
			dtpPeriodestart.setDate(cal.getTime());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		}

		cmbWerkgever = new JComboBox<>();
		cmbWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWerkgeverClicked(e);
			}
		});
		cmbWerkgever.setBounds(134, 34, 214, 20);
		getContentPane().add(cmbWerkgever);

	}

	protected void cmbGebruikerClicked(ActionEvent e) {
		populateTable();
	}

	protected void cmbWerkgeverClicked(ActionEvent e) {
		factuurcontroller.setSelectedWerkgever(werkgevermodel.getId());
	}

	public void populateTable() {
		if (!initialized)
			return;
		btnBetalingen.setEnabled(false);
		try {
			factuurcontroller.selectFacturen(dtpPeriodestart.getDate(), dtpPeriodeeind.getDate());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}
	@Override
	public void refreshTable() {
		facturen = factuurcontroller.getFacturenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		WerkgeverInfo.sort(werkgevers,WerkgeverInfo.__sortcol.NAAM);
		factuurcontroller.getTableModel(facturen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
		btnBetalingen.setEnabled(false);
	}
	public void setRowSelected() {
		btnBetalingen.setEnabled(true);
	}
}
