package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gieselaar.verzuim.controllers.FactuuritemController;
import com.gieselaar.verzuim.controllers.FactuuritemController.__factuuritemfields;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.util.Calendar;

import com.michaelbaranov.microba.calendar.DatePicker;

public class FactuuritemList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private DatePicker dtpPeriodeeind;
	private DatePicker dtpPeriodestart;
	private JComboBox<TypeEntry> cmbWerkgever;
	private transient List<RowSorter.SortKey> sortKeys;
	
	private FactuuritemController factuuritemcontroller;
	private VerzuimComboBoxModel werkgevermodel;
	private boolean initialized = false;
	
	/**
	 * Create the frame.
	 */

	public FactuuritemList(FactuuritemController controller) {
		super("Factuuritems", controller);
		factuuritemcontroller = controller;
		initialize();

		werkgevermodel = new VerzuimComboBoxModel(controller);
		factuuritemcontroller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, false);
		cmbWerkgever.setModel(werkgevermodel);

		addColumn(__factuuritemfields.WERKGEVER.getValue(),"Werkgever", 120);
		addColumn(__factuuritemfields.DATUM.getValue(),"Datum", 60, Date.class);
		addColumn(__factuuritemfields.CATEGORIE.getValue(),"Categorie", 100);
		addColumn(__factuuritemfields.BEDRAG.getValue(),"Bedrag", 60);

		sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
		this.getSorter().setSortKeys(sortKeys);
		this.getSorter().sort();
		initialized = true;
	}

	public void initialize() {
		this.setFreespaceontop(60);
		setBounds(50, 50, 754, 454);

		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(38, 37, 75, 14);
		getContentPane().add(lblWerkgever);

		JLabel lblPeriodevan = new JLabel("Periode van:");
		lblPeriodevan.setBounds(39, 12, 86, 14);
		getContentPane().add(lblPeriodevan);

		JLabel lblTot = new JLabel("tot:");
		lblTot.setBounds(227, 12, 31, 14);
		getContentPane().add(lblTot);

		dtpPeriodeeind = new DatePicker();
		dtpPeriodeeind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		dtpPeriodeeind.setBounds(263, 11, 86, 21);
		dtpPeriodeeind.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpPeriodeeind);

		dtpPeriodestart = new DatePicker();
		dtpPeriodestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTable();
			}
		});
		dtpPeriodestart.setBounds(135, 11, 86, 21);
		dtpPeriodestart.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpPeriodestart);

		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND, 0);
			dtpPeriodeeind.setDate(cal.getTime());
			cal.set(Calendar.DAY_OF_MONTH, 1);
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
		cmbWerkgever.setBounds(135, 34, 214, 20);
		getContentPane().add(cmbWerkgever);

	}

	protected void cmbWerkgeverClicked(ActionEvent e) {
		factuuritemcontroller.setSelectedWerkgever(werkgevermodel.getId());
	}

	@Override
	public void refreshTable() {
		List<FactuuritemInfo> factuuritems;
		factuuritems = factuuritemcontroller.getFactuuritemList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		factuuritemcontroller.getTableModel(factuuritems, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
	public void populateTable() {
		if (!initialized)
			return;
		try {
			factuuritemcontroller.selectFactuuritems(dtpPeriodestart.getDate()
					  							   , dtpPeriodeeind.getDate());
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}
}
