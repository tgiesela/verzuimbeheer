package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.FactuurInfo.__factuurstatus;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;

import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.util.Calendar;

import com.michaelbaranov.microba.calendar.DatePicker;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class FactuurOverzicht extends BaseListForm {
	private static final long serialVersionUID = 1L;
	private List<FactuurTotaalInfo> facturen;
	private List<GebruikerInfo> gebruikers;
	List<FactuurTotaalInfo> somfacturen;
	private FactuurTotaalInfo factuur;
	private Component thisform = this;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private Hashtable<Integer, Object> hashWerkgevers = new Hashtable<Integer, Object>();
	private Hashtable<Integer, Object> hashHoldings = new Hashtable<Integer, Object>();
	private JLabel lblPeriodevan;
	private DefaultComboBoxModel<TypeEntry> werkgeverModel;
	private DatePicker dtpPeriodeeind;
	private DatePicker dtpPeriodestart;
	private JComboBox<TypeEntry> cmbWerkgever;

	private boolean initialized = false;
	private JButton btnBetalingen;
	private JCheckBox chckbxHideEmtpyBills;

	/**
	 * Create the frame.
	 */

	public FactuurOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Factuur", mdiPanel);
		getPanelAction().setSize(524, 33);
		getExportButton().setLocation(0, 146);
		initialize();
		List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		this.getSorter().setSortKeys(sortKeys);
		this.getSorter().sort();
	}

	public void initialize() {
		setBounds(100, 100, 791, 454);
		setDetailFormClass(FactuurDetail.class, FactuurTotaalInfo.class);
		this.setFiltering(true);

		getPanelAction().setLocation(0, 394);

		btnBetalingen = new JButton("Betalingen...");
		btnBetalingen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnBetalingenClicked(arg0);
			}
		});
		btnBetalingen.setEnabled(false);
		getPanelAction().add(btnBetalingen);
		
		chckbxHideEmtpyBills = new JCheckBox("Verberg lege facturen");
		chckbxHideEmtpyBills.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				populateTable();
			}
		});
		getPanelAction().add(chckbxHideEmtpyBills);
		getScrollPane().setBounds(39, 86, 726, 297);

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
		dtpPeriodeeind.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpPeriodeeind);

		dtpPeriodestart = new DatePicker();
		dtpPeriodestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dtpPeriodeeind.setDate(dtpPeriodestart.getDate());
				} catch (PropertyVetoException e1) {
				}
				populateTable();
			}
		});
		dtpPeriodestart.setBounds(135, 37, 86, 21);
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
		cmbWerkgever.setBounds(135, 60, 214, 20);
		getContentPane().add(cmbWerkgever);

		this.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				populateTable();
			}

			@Override
			public void rowSelected(InfoBase info) {
				btnBetalingen.setEnabled(true);
				factuur = (FactuurTotaalInfo) info;
			}

			@Override
			public void newCreated(InfoBase info) {
				@SuppressWarnings("unused")
				FactuurTotaalInfo werkzaamheid = (FactuurTotaalInfo) info;
			}

			@Override
			public __continue newButtonClicked() {
				return __continue.dontallow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de regel wilt verwijderen?",
							"", JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION) {
						FactuurInfo factuur = (FactuurInfo)info;
						factuur.setFactuurstatus(__factuurstatus.VERWIJDERD);
						ServiceCaller.factuurFacade(getLoginSession()).updateFactuur(factuur);
					} else {
						return __continue.dontallow;
					}
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
		addColumn("Factuurnr", null, 60);
		addColumn("Jaar", null, 60);
		addColumn("Maand", null, 60);
		addColumn("factuurbedrag", null, 80, BigDecimal.class);
		addColumn("betaaldbedrag", null, 80, BigDecimal.class);
		addColumn("Status", null, 100);
		addColumn("Email", null, 150);
	}

	protected void btnBetalingenClicked(ActionEvent arg0) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		FactuurbetalingenOverzicht dlgFactuurbetalingen = new FactuurbetalingenOverzicht(this.getMdiPanel());
		dlgFactuurbetalingen.setLoginSession(this.getLoginSession());
		dlgFactuurbetalingen.setParentInfo(factuur);
		dlgFactuurbetalingen.setInfo(factuur);
		dlgFactuurbetalingen.ReloadTable();
		dlgFactuurbetalingen.setVisible(true);
		mdiPanel.add(dlgFactuurbetalingen);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgFactuurbetalingen);
	}

	protected void cmbGebruikerClicked(ActionEvent e) {
		populateTable();
	}

	protected void cmbWerkgeverClicked(ActionEvent e) {
		populateTable();
	}

	public void setInfo(InfoBase info) {
		factuur = (FactuurTotaalInfo) info;
		/*
		 * We gaan nu alle details ophalen van deze werknemer en de cascodes
		 */
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			gebruikers = ServiceCaller.autorisatieFacade(getLoginSession()).getGebruikers();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
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
		for (HoldingInfo hi : holdings) {
			hashHoldings.put(hi.getId(), hi.getNaam());
		}

		initialized = true;
	}

	private void displayFactuur() {
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);
		if (!initialized)
			return;
		Integer werkgever;
		TypeEntry selectedWerkgever = (TypeEntry) cmbWerkgever.getSelectedItem();
		werkgever = selectedWerkgever.getValue();

		deleteAllRows();
		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		RowFilter<? super TableModel, ? super Integer> filter = this
				.getSorter().getRowFilter();
		this.getSorter().setSortKeys(null);
		this.getSorter().setRowFilter(null);

		if (somfacturen != null) {
			for (FactuurTotaalInfo v : somfacturen) {
				if (v.getAction() == persistenceaction.DELETE || 
						(chckbxHideEmtpyBills.isSelected() && (v.getTotaalInclBtw().compareTo(BigDecimal.ZERO)==0))) {
					;
				} else {
					if (werkgever == -1 || v.getWerkgeverid().equals(werkgever)) {
						Vector<Object> rowData = new Vector<Object>();
						if (v.isGesommeerd()) {
							rowData.add(hashHoldings.get(v.getHoldingid()));
						} else {
							rowData.add(hashWerkgevers.get(v.getWerkgeverid()));
						}
						rowData.add(v.getFactuurnr().toString());
						rowData.add(v.getJaar().toString());
						rowData.add(v.getMaand().toString());
						// TODO
						// rowData.add(amountFormat.format(v.getTotaalInclBtw()));
						rowData.add(v.getTotaalInclBtw());
						rowData.add(v.getSombetalingen());
						rowData.add(v.getFactuurstatus().toString());
						if (v.isGesommeerd()) {
							if (v.getWerkgever().getHolding() != null){
								if (v.getWerkgever().getHolding().getEmailadresfactuur() == null
										|| v.getWerkgever().getHolding().getEmailadresfactuur().isEmpty()) {
									rowData.add("");
								} else {
									rowData.add(v.getWerkgever().getHolding().getEmailadresfactuur());
								}
							}else{
								rowData.add("");
							}
						} else {
							if (v.getWerkgever().getEmailadresfactuur() == null
									|| v.getWerkgever().getEmailadresfactuur().isEmpty()) {
								rowData.add("");
							} else {
								rowData.add(v.getWerkgever().getEmailadresfactuur());
							}
						}
						if (v.getTotaalInclBtw().compareTo(BigDecimal.ZERO) == 0) {
							addRow(rowData, v);
						} else {
							int result = v.getSombetalingen().setScale(2,RoundingMode.HALF_UP).compareTo(v.getTotaalInclBtw().setScale(2,RoundingMode.HALF_UP));
							if (result == 0) {
								addRow(rowData, v, Color.GREEN);
							} else {
								if (result > 0) {
									addRow(rowData, v, Color.RED);
								} else {
									addRow(rowData, v);
								}
							}
						}
					}
				}
			}
		}
		this.getSorter().setRowFilter(filter);
		this.getSorter().setSortKeys(keys);

	}

	public void populateTable() {
		if (!initialized)
			return;
		try {
			btnBetalingen.setEnabled(false);
			somfacturen = ServiceCaller.factuurFacade(getLoginSession())
					.getFacturenInPeriode(dtpPeriodestart.getDate(), dtpPeriodeeind.getDate(), false);
		} catch (PermissionException e2) {
			ExceptionLogger.ProcessException(e2, this, "Kan Factuur niet opvragen");
			return;
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		displayFactuur();
	}
}
