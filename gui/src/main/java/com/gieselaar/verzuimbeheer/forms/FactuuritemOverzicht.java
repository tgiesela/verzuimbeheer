package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.util.Calendar;

import com.michaelbaranov.microba.calendar.DatePicker;

public class FactuuritemOverzicht extends BaseListForm {
	private static final long serialVersionUID = 1L;
	private List<FactuuritemInfo> items;
	private List<FactuurcategorieInfo> factuurcategorien;
	private Component thisform = this;
	private List<WerkgeverInfo> werkgevers;
	private Hashtable<Integer, Object> hashWerkgevers = new Hashtable<Integer, Object>();
	private Hashtable<Integer, Object> hashFactuurcategorien = new Hashtable<Integer, Object>();
	private JLabel lblPeriodevan;
	private DefaultComboBoxModel<TypeEntry> werkgeverModel;
	private DatePicker dtpPeriodeeind;
	private DatePicker dtpPeriodestart;
	private JComboBox<TypeEntry> cmbWerkgever;
	
	private boolean initialized = false;
	
	/**
	 * Create the frame.
	 */

	public FactuuritemOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Factuuritems", mdiPanel);
		getExportButton().setLocation(0, 146);
		initialize();
	}

	public void initialize() {
		setBounds(100, 100, 754, 454);
		setDetailFormClass(FactuuritemDetail.class, FactuuritemInfo.class);
		this.setFiltering(true);

		getPanelAction().setLocation(10, 229);
		getScrollPane().setBounds(39, 86, 687, 297);

		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(38, 63, 75, 14);
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
		cmbWerkgever.setBounds(135, 60, 214, 20);
		getContentPane().add(cmbWerkgever);

		this.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				populateTable();
			}

			@Override
			public void newCreated(InfoBase info) {
				FactuuritemInfo item = (FactuuritemInfo) info;
				item.setWerkgeverid(-1);
				item.setDatum(new Date());
				item.setFactuurcategorieid(-1);
				item.setBedrag(BigDecimal.ZERO);
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
								.deleteFactuuritem((FactuuritemInfo) info);
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
		addColumn("Categorie", null, 100);
		addColumn("Bedrag", null, 60);
		addColumn("Gefactureerd", null, 100);
	}

	protected void cmbWerkgeverClicked(ActionEvent e) {
		populateTable();
	}

	public void setInfo(InfoBase info) {
		/*
		 * We gaan nu alle details ophalen van deze werknemer en de cascodes
		 */
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession())
					.allWerkgeversList();
			factuurcategorien = ServiceCaller.factuurFacade(getLoginSession()).getFactuurcategorien();
			for (FactuurcategorieInfo fc: factuurcategorien){
				hashFactuurcategorien.put(fc.getId(), fc.getOmschrijving());
			}
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
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		cmbWerkgever.setModel(werkgeverModel);
		for (WerkgeverInfo wi : werkgevers) {
			werkgeverModel.addElement(new TypeEntry(wi.getId(), wi.getNaam()));
			hashWerkgevers.put(wi.getId(), wi.getNaam());
		}

		initialized = true;
	}

	private void displayFactuuritems() {
		if (!initialized)
			return;
		List<RowSorter.SortKey> sortKeys;
		Integer werkgever;
		TypeEntry selectedWerkgever = (TypeEntry) cmbWerkgever.getSelectedItem();
		werkgever = selectedWerkgever.getValue();
		
		deleteAllRows();
		if (items != null) {
			for (FactuuritemInfo v : items) {
				if (v.getAction() == persistenceaction.DELETE){
					;
				} else {
					if (werkgever == -1 || v.getWerkgeverid().equals(werkgever)){
						Vector<Object> rowData = new Vector<Object>();
						rowData.add(hashWerkgevers.get(v.getWerkgeverid()));
						rowData.add(v.getDatum());
						rowData.add(hashFactuurcategorien.get(v.getFactuurcategorieid()));
						rowData.add(v.getBedrag().toString());
						addRow(rowData, v);
					}
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
		try {
			items = ServiceCaller
					.factuurFacade(getLoginSession())
					.getFactuuritems(dtpPeriodestart.getDate(), dtpPeriodeeind.getDate());
		} catch (PermissionException e2) {
			ExceptionLogger.ProcessException(e2, this,
					"Kan factuuritems niet opvragen");
			return;
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		displayFactuuritems();
	}
}
