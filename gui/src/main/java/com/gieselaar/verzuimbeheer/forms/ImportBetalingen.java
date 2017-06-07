package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.gieselaar.transactieparsers.Mt940Entry;
import com.gieselaar.transactieparsers.Mt940File;
import com.gieselaar.transactieparsers.Mt940Parser;
import com.gieselaar.transactieparsers.Mt940Record;
import com.gieselaar.transactieparsers.RaboCSVEntry;
import com.gieselaar.transactieparsers.RaboCSVEntry.DebitCreditIndicator;
import com.gieselaar.transactieparsers.RaboCSVFile;
import com.gieselaar.transactieparsers.RaboCVSParser;
import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.baseforms.WerkgeverNotification;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo.__checkresult;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;

import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.components.TablePanel;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverSelectionMode;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.lang3.StringUtils;

import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

public class ImportBetalingen extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkgeverSelection werkgeverSelection;
	private JTextFieldTGI txtImportbestandlocatie;
	private JTextFieldTGI txtSeparator;
	private TablePanel tpBetalingen;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private List<FactuurTotaalInfo> facturen;
	private List<FactuurbetalingInfo> factuurbetalingen;
	private ImportBetalingInfo selectedBetalingInfo = null;
	private List<ImportBetalingInfo> importedbetalingen = new ArrayList<>();
	private JButton btnUpdate;
	private JCheckBox chckbxDoorvoeren;
	private JRadioButton rbRaboCSV;
	private JRadioButton rbMT940;
	private JTextAreaTGI taOmschrijving;

	private HashMap<String, Integer> accnrsHolding = new HashMap<>();
	private HashMap<String, Integer> accnrsWerkgever = new HashMap<>();
	private HashMap<Integer, HoldingInfo> hmHolding = new HashMap<>();
	private HashMap<Integer, WerkgeverInfo> hmWerkgever = new HashMap<>();
	private int periodlength = 6;

	private Component thisform = this;

	private enum importresult {
		werkgevernotfound, fieldsmissing, requiredfieldmissing, invalidlength, formaterror, invalidtype, invaliddate, updaterror, emptyrow, ok, warning
	};

	private interface kolom {
		public int getValue();

		public String toString();
	}

	private enum importkolommen implements kolom {
		rekeningDVV(1) {
			public String toString() {
				return "eigenRekeningnummer";
			}
		},
		muntsoort(2) {
			public String toString() {
				return "muntsoort";
			}
		},
		renteDatum(3) {
			public String toString() {
				return "rentedatum";
			}
		},
		byAfCode(4) {
			public String toString() {
				return "byafcode";
			}
		},
		bedrag(5) {
			public String toString() {
				return "bedrag";
			}
		},
		tegenRekening(6) {
			public String toString() {
				return "tegenrekening";
			}
		},
		naamRekeningHouder(7) {
			public String toString() {
				return "naamrekeninghouder";
			}
		},
		boekDatum(8) {
			public String toString() {
				return "boekdatum";
			}
		},
		boekCode(9) {
			public String toString() {
				return "boekcode";
			}
		},
		rfu1(10) {
			public String toString() {
				return "filler";
			}
		},
		omschr1(11) {
			public String toString() {
				return "omschrijving1";
			}
		},
		omschr2(12) {
			public String toString() {
				return "omschrijving2";
			}
		},
		omschr3(13) {
			public String toString() {
				return "omschrijving3";
			}
		},
		omschr4(14) {
			public String toString() {
				return "omschrijving4";
			}
		},
		omschr5(15) {
			public String toString() {
				return "omschrijving5";
			}
		},
		omschr6(16) {
			public String toString() {
				return "omschrijving6";
			}
		},
		endToEndId(17) {
			public String toString() {
				return "endtoendid";
			}
		},
		idTegenRekHouder(18) {
			public String toString() {
				return "idtegenrekeninghouder";
			}
		},
		mandaatId(19) {
			public String toString() {
				return "mandaatid";
			}
		};

		private int value;

		importkolommen(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@SuppressWarnings("unused")
		public static importkolommen parse(Integer id) {
			importkolommen soort = null; // Default
			for (importkolommen item : importkolommen.values()) {
				if (item.getValue() == id) {
					soort = item;
					break;
				}
			}
			return soort;
		}
	}

	private class parseTable {
		kolom kolom;
		String formaat;
		int minlen;
		int maxlen;
		boolean verplicht;
		String type;

		private void storevalues(kolom kolom, String type, int minlen, int maxlen, boolean verplicht) {
			this.kolom = kolom;
			this.type = type;
			this.minlen = minlen;
			this.maxlen = maxlen;
			this.verplicht = verplicht;
			this.formaat = null;
		}

		parseTable(kolom kolom, String type, int minlen, int maxlen, boolean verplicht) {
			storevalues(kolom, type, minlen, maxlen, verplicht);
			this.formaat = null;
		}

		parseTable(kolom kolom, String type, int minlen, int maxlen, boolean verplicht, String formaat) {
			storevalues(kolom, type, minlen, maxlen, verplicht);
			this.formaat = formaat;
		}

		@SuppressWarnings("unused")
		parseTable(kolom kolom, String type, int minlen, int maxlen, boolean verplicht, String formaat,
				String allowedvalues) {
			storevalues(kolom, type, minlen, maxlen, verplicht);
			this.formaat = formaat;
		}
	}

	private parseTable[] importexcel = { new parseTable(importkolommen.rekeningDVV, "A", 0, 35, true),
			new parseTable(importkolommen.muntsoort, "A", 3, 3, true),
			new parseTable(importkolommen.renteDatum, "D", 8, 8, true, "yyyyMMdd"),
			new parseTable(importkolommen.byAfCode, "A", 1, 1, true),
			new parseTable(importkolommen.bedrag, "ND", 0, 14, true),
			new parseTable(importkolommen.tegenRekening, "A", 0, 35, false),
			new parseTable(importkolommen.naamRekeningHouder, "A", 0, 70, false),
			new parseTable(importkolommen.boekDatum, "D", 8, 8, true, "yyyyMMdd"),
			new parseTable(importkolommen.boekCode, "A", 2, 2, true),
			new parseTable(importkolommen.rfu1, "A", 0, 6, false),
			new parseTable(importkolommen.omschr1, "A", 0, 35, false),
			new parseTable(importkolommen.omschr2, "A", 0, 35, false),
			new parseTable(importkolommen.omschr3, "A", 0, 35, false),
			new parseTable(importkolommen.omschr4, "A", 0, 35, false),
			new parseTable(importkolommen.omschr5, "A", 0, 35, false),
			new parseTable(importkolommen.omschr6, "A", 0, 35, false),
			new parseTable(importkolommen.endToEndId, "A", 0, 35, false),
			new parseTable(importkolommen.idTegenRekHouder, "A", 1, 35, false),
			new parseTable(importkolommen.mandaatId, "A", 0, 35, false) };
	private JTextField txtFactuurNr;
	private JFormattedTextField txtBedragbetaling;
	private JFormattedTextField txtFactuurbedrag;
	private JFormattedTextField txtReedsbetaald;
	private JCheckBox chckbxAlleenprobleemgevallen;
	private JButton btnOphalenFacturen;
	private JSpinner spinnerMaanden;
	private JLabel lblAantalMaanden;

	public ImportBetalingen(JDesktopPaneTGI mdiPanel) {
		super("Importeer betalingen", mdiPanel);
		initialize();
	}

	public void setInfo(InfoBase info) {

		txtImportbestandlocatie.setText("");
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgevers();
			for (WerkgeverInfo wg : werkgevers) {
				hmWerkgever.put(wg.getId(), wg);
			}
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			for (HoldingInfo h : holdings) {
				hmHolding.put(h.getId(), h);
			}
			werkgeverSelection.setWerkgevers(werkgevers);
			werkgeverSelection.setHoldings(holdings);
			rbMT940.setSelected(false);
			rbRaboCSV.setSelected(true);
			initializesearch();
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1, this);
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
		}

		activateListener();
	}

	void initialize() {
		setBounds(100, 100, 827, 565);

		JLabel lblDocumentlocatie = new JLabel("Bestand");
		lblDocumentlocatie.setBounds(20, 46, 140, 14);
		getContentPane().add(lblDocumentlocatie);

		txtImportbestandlocatie = new JTextFieldTGI();
		txtImportbestandlocatie.setBounds(181, 43, 286, 20);
		getContentPane().add(txtImportbestandlocatie);

		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileClicked(e);
			}
		});
		btnNewButton.setBounds(474, 42, 35, 23);
		getContentPane().add(btnNewButton);

		JLabel lblSeparator = new JLabel("Separator");
		lblSeparator.setBounds(355, 20, 60, 14);
		getContentPane().add(lblSeparator);

		txtSeparator = new JTextFieldTGI();
		txtSeparator.setText(",");
		txtSeparator.setBounds(429, 17, 20, 20);
		getContentPane().add(txtSeparator);
		txtSeparator.setColumns(1);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(20, 92, 781, 251);
		getContentPane().add(tabbedPane);

		tpBetalingen = new TablePanel(this.getMdiPanel());
		tpBetalingen.getPanelAction().setSize(430, 30);
		tpBetalingen.getPanelAction().setLocation(5, 188);

		chckbxAlleenprobleemgevallen = new JCheckBox("alleen probleem gevallen");
		chckbxAlleenprobleemgevallen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				populateTable();
			}
		});

		tpBetalingen.getPanelAction().add(chckbxAlleenprobleemgevallen);
		tpBetalingen.getScrollPane().setBounds(5, 0, 761, 190);
		tpBetalingen.setDetailFormClass(null, ImportBetalingInfo.class);
		tpBetalingen.addColumn("Datum", null, 60, Date.class);
		tpBetalingen.addColumn("Bedrag", null, 60, BigDecimal.class);
		tpBetalingen.addColumn("Reknr betaler", null, 70);
		tpBetalingen.addColumn("Bedrijfsnaam", null, 150);
		tpBetalingen.addColumn("DVV Werkgever", null, 100);
		tpBetalingen.addColumn("DVV Factuurnr", null, 100);
		tpBetalingen.addColumn("DVV Factuurbedrag", null, 60, BigDecimal.class);
		tpBetalingen.addColumn("Melding", null, 140);
		tpBetalingen.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void rowSelected(InfoBase info) {
				ImportBetalingInfo ibi = (ImportBetalingInfo) info;
				selectedBetalingInfo = ibi;
				if (ibi.getFactuurnummer() != null) {
					txtFactuurNr.setText(ibi.getFactuurnummer().toString());
				} else {
					txtFactuurNr.setText("");
				}
				taOmschrijving.setText(ibi.getOmschrijving1());
				if (ibi.getHoldingid() != null) {
					werkgeverSelection.setHoldingId(ibi.getHoldingid());
					werkgeverSelection.setWerkgeverId(-1);
				} else {
					if (ibi.getWerkgeverid() != null) {
						werkgeverSelection.setWerkgeverId(ibi.getWerkgeverid());
						werkgeverSelection.setHoldingId(-1);
					}
				}
				txtBedragbetaling.setValue(ibi.getBedrag());
				if (ibi.getFactuurbedrag() != null)
					txtFactuurbedrag.setValue(ibi.getFactuurbedrag().setScale(2, RoundingMode.HALF_UP));
				if (ibi.getFactuur() != null) {
					txtReedsbetaald.setValue(ibi.getFactuur().getSombetalingen().setScale(2, RoundingMode.HALF_UP));
				}
				if (ibi.getCheckresult() == __checkresult.OK || ibi.getCheckresult() == __checkresult.ALREADYPAID) {
					chckbxDoorvoeren.setEnabled(false);
				} else {
					chckbxDoorvoeren.setEnabled(true);
					chckbxDoorvoeren.setSelected(ibi.isAccepted());
				}
				btnUpdate.setEnabled(true);
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				importedbetalingen.remove(info);
				return __continue.allow;
			}

			@Override
			public void populateTableRequested() {
				populateTable();
			}

		});
		tabbedPane.addTab("Betalingen", null, tpBetalingen, null);

		JButton btnImporteer = new JButton("Importeer");
		btnImporteer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnimporteerClicked(e);
			}
		});
		btnImporteer.setBounds(181, 65, 89, 23);
		getContentPane().add(btnImporteer);

		txtFactuurNr = new JTextField();
		txtFactuurNr.setBounds(95, 354, 75, 20);
		getContentPane().add(txtFactuurNr);
		txtFactuurNr.setColumns(10);

		JLabel lblFactuurnr = new JLabel("Factuurnr");
		lblFactuurnr.setBounds(20, 357, 65, 14);
		getContentPane().add(lblFactuurnr);

		JLabel lblOmschr = new JLabel("Omschr");
		lblOmschr.setBounds(20, 381, 65, 14);
		getContentPane().add(lblOmschr);

		btnUpdate = new JButton("Update...");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnUpdateClicked();
			}
		});
		btnUpdate.setBounds(376, 463, 89, 23);

		getContentPane().add(btnUpdate);
		werkgeverSelection = new WerkgeverSelection(__WerkgeverSelectionMode.SelectOne, __WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(376, 378, 378, 63);
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				// TODO Auto-generated method stub
				return false;
			}

		});

		JLabel lblBedragbetaling = new JLabel("Bedragbetaling");
		lblBedragbetaling.setBounds(180, 357, 75, 14);
		getContentPane().add(lblBedragbetaling);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		txtBedragbetaling = new JFormattedTextField(amountFormat);
		txtBedragbetaling.setColumns(10);
		txtBedragbetaling.setBounds(255, 354, 75, 20);
		getContentPane().add(txtBedragbetaling);

		JLabel lblFactuurbedrag = new JLabel("Factuurbedrag");
		lblFactuurbedrag.setBounds(340, 357, 75, 14);
		getContentPane().add(lblFactuurbedrag);

		txtFactuurbedrag = new JFormattedTextField(amountFormat);
		txtFactuurbedrag.setEditable(false);
		txtFactuurbedrag.setColumns(10);
		txtFactuurbedrag.setBounds(415, 354, 75, 20);
		getContentPane().add(txtFactuurbedrag);

		JLabel lblReedsBetaald = new JLabel("reeds betaald");
		lblReedsBetaald.setBounds(500, 357, 75, 14);
		getContentPane().add(lblReedsBetaald);

		txtReedsbetaald = new JFormattedTextField(amountFormat);
		txtReedsbetaald.setEditable(false);
		txtReedsbetaald.setColumns(10);
		txtReedsbetaald.setBounds(575, 354, 75, 20);
		getContentPane().add(txtReedsbetaald);

		chckbxDoorvoeren = new JCheckBox("Doorvoeren");
		chckbxDoorvoeren.setEnabled(false);
		chckbxDoorvoeren.setBounds(474, 463, 97, 23);
		getContentPane().add(chckbxDoorvoeren);

		btnOphalenFacturen = new JButton("Ophalen facturen");
		btnOphalenFacturen.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				periodlength = (int) spinnerMaanden.getValue();
				initializesearch();
			}
		}));
		btnOphalenFacturen.setBounds(446, 67, 129, 23);
		getContentPane().add(btnOphalenFacturen);

		rbMT940 = new JRadioButton("MT940");
		rbMT940.setSelected(true);
		rbMT940.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtSeparator.setEnabled(false);
			}
		});
		rbMT940.setBounds(181, 16, 75, 23);
		getContentPane().add(rbMT940);

		rbRaboCSV = new JRadioButton("Rabo .CSV");
		rbRaboCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtSeparator.setEnabled(true);
			}
		});
		rbRaboCSV.setBounds(266, 16, 109, 23);
		getContentPane().add(rbRaboCSV);

		ButtonGroup buttongroup = new ButtonGroup();
		buttongroup.add(rbRaboCSV);
		buttongroup.add(rbMT940);

		spinnerMaanden = new JSpinner();
		spinnerMaanden.setBounds(401, 68, 41, 20);
		getContentPane().add(spinnerMaanden);
		spinnerMaanden.setValue(6);

		// setBounds(100, 100, 485, 506);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(92, 381, 274, 107);
		getContentPane().add(scrollPane);

		taOmschrijving = new JTextAreaTGI();
		scrollPane.setColumnHeaderView(taOmschrijving);
		
		JLabel lblBestandsformaat = new JLabel("Bestandsformaat");
		lblBestandsformaat.setBounds(20, 20, 140, 14);
		getContentPane().add(lblBestandsformaat);
		
		lblAantalMaanden = new JLabel("Aantal maanden");
		lblAantalMaanden.setBounds(312, 69, 89, 14);
		getContentPane().add(lblAantalMaanden);
	}

	protected void btnUpdateClicked() {
		FactuurTotaalInfo factuur;
		DecimalFormat format = new DecimalFormat("##0.00");
		format.setParseBigDecimal(true);
		selectedBetalingInfo.setWerkgeverid(werkgeverSelection.getWerkgeverId());
		selectedBetalingInfo.setHoldingid(werkgeverSelection.getHoldingId());
		selectedBetalingInfo.setFactuurnummer(Integer.parseInt(txtFactuurNr.getText()));
		factuur = findFactuurByFactuurnr(selectedBetalingInfo.getFactuurnummer(), selectedBetalingInfo.getWerkgeverid(),
				selectedBetalingInfo.getHoldingid(),selectedBetalingInfo.getBedrag());
		selectedBetalingInfo.setFactuur(factuur);
		if (factuur != null) {
			selectedBetalingInfo.setFactuurid(factuur.getId());
			selectedBetalingInfo.setFactuurbedrag(factuur.getTotaalInclBtw());
			if (selectedBetalingInfo.getWerkgeverid() == null) {
				selectedBetalingInfo.setWerkgeverid(factuur.getWerkgeverid());
			}
			if (selectedBetalingInfo.getHoldingid() == null) {
				selectedBetalingInfo.setHoldingid(factuur.getHoldingid());
			}
			if (chckbxDoorvoeren.isEnabled()) {
				selectedBetalingInfo.setAccepted(chckbxDoorvoeren.isSelected());
			}
		} else {
			selectedBetalingInfo.setFactuurid(null);
			selectedBetalingInfo.setFactuurbedrag(null);
			selectedBetalingInfo.setAccepted(false);
		}
		try {
			selectedBetalingInfo.setBedrag(format.format(txtBedragbetaling.getValue()));
		} catch (ParseException e) {
			/* ignore */
		}
		btnUpdate.setEnabled(false);
		werkgeverSelection.setWerkgeverId(-1);
		werkgeverSelection.setHoldingId(-1);
		txtFactuurNr.setText("");
		txtBedragbetaling.setText("");
		txtFactuurbedrag.setText("");
		taOmschrijving.setText("");
		populateTable();
	}

	protected void btnimporteerClicked(ActionEvent e) {
		String docnaam = txtImportbestandlocatie.getText();
		if (docnaam.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Importbestand is leeg.");
			return;
		}
		importedbetalingen = new ArrayList<>();
		if (this.getLoginSession() != null) {
			tpBetalingen.deleteAllRows();
			LineNumberReader reader;
			try {
				reader = new LineNumberReader(new InputStreamReader(new FileInputStream(docnaam)));
				if (this.rbMT940.isSelected()) {
					Mt940Parser mt940 = new Mt940Parser();
					Mt940File mt940file = mt940.parse(reader);
					for (Mt940Record record : mt940file.getRecords()) {
						for (Mt940Entry entry : record.getEntries()) {
							if (entry.getDebitCreditIndicator() == Mt940Entry.DebitCreditIndicator.CREDIT) {
								ImportBetalingInfo ibi = new ImportBetalingInfo();
								ibi.setDatumbetaling(entry.getValutaDatum());
								ibi.setBedrag(entry.getAmount());
								ibi.setRekeningnummerBetaler(entry.getIban());
								ibi.setBedrijfsnaam(entry.getName());
								ibi.setOmschrijving1(entry.getOmschrijvingen());

								FactuurTotaalInfo factuur = selecteerFactuur(ibi);
								if (factuur != null) {
									ibi.setFactuurnummer(factuur.getFactuurnr());
									if (ibi.getWerkgeverid() == null) {
										ibi.setWerkgeverid(factuur.getWerkgeverid());
									}
									if (ibi.getHoldingid() == null) {
										ibi.setHoldingid(factuur.getHoldingid());
									}
									ibi.setFactuurbedrag(factuur.getTotaalInclBtw());
									ibi.setFactuur(factuur);
								} else {
									ibi.setFactuur(null);
								}

								importedbetalingen.add(ibi);
							}
						}
					}
				} else {
					RaboCVSParser rabo = new RaboCVSParser(txtSeparator.getText());
					RaboCSVFile rabofile = rabo.parse(reader);
					for (RaboCSVEntry entry : rabofile.getRecords()) {
						if (entry.getDebitCreditIndicator().equals(DebitCreditIndicator.CREDIT)) {
							ImportBetalingInfo ibi = new ImportBetalingInfo();
							ibi.setDatumbetaling(entry.getValutaDatum());
							ibi.setBedrag(entry.getAmount());
							ibi.setRekeningnummerBetaler(entry.getIban());
							ibi.setBedrijfsnaam(entry.getName());
							ibi.setOmschrijving1(entry.getOmschrijvingen());

							FactuurTotaalInfo factuur = selecteerFactuur(ibi);
							if (factuur != null) {
								ibi.setFactuurnummer(factuur.getFactuurnr());
								if (ibi.getWerkgeverid() == null) {
									ibi.setWerkgeverid(factuur.getWerkgeverid());
								}
								if (ibi.getHoldingid() == null) {
									ibi.setHoldingid(factuur.getHoldingid());
								}
								ibi.setFactuurbedrag(factuur.getTotaalInclBtw());
								ibi.setFactuur(factuur);
							} else {
								ibi.setFactuur(null);
							}

							importedbetalingen.add(ibi);
						}
					}
				}
			} catch (FileNotFoundException e1) {
				ExceptionLogger.ProcessException(e1, this);
				return;
			} catch (IOException e1) {
				ExceptionLogger.ProcessException(e1, this);
				return;
			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1, this);
				return;
			} catch (ParseException e1) {
				ExceptionLogger.ProcessException(e1, this);
				return;
			}
			populateTable();
		} else
			JOptionPane.showMessageDialog(this, "Logic error: loginSession is null");

	}

	private String checkBetaling(ImportBetalingInfo ibi) {
		FactuurTotaalInfo factuur = ibi.getFactuur();
		ibi.setCheckresult(__checkresult.OK);
		if (factuur != null) {
			if (factuur.isGesommeerd()) {
				if (ibi.getHoldingid() != null) {
					if (!ibi.getHoldingid().equals(ibi.getFactuur().getHoldingid())) {
						ibi.setCheckresult(__checkresult.HOLDINGMISMATCH);
					}
				}
			} else {
				if (ibi.getWerkgeverid() != null) {
					if (!ibi.getWerkgeverid().equals(ibi.getFactuur().getWerkgeverid())) {
						ibi.setCheckresult(__checkresult.WERKGEVERMISMATCH);
					}
				} else {
					if (!ibi.getHoldingid().equals(ibi.getFactuur().getHoldingid())) {
						ibi.setCheckresult(__checkresult.HOLDINGMISMATCH);
					}
				}
			}
		} else {
			ibi.setCheckresult(__checkresult.NOMATCHINGFACTUUR);
		}
		if (ibi.getCheckresult().compareTo(__checkresult.OK) == 0) {
			if (ibi.getFactuurbedrag().setScale(2, RoundingMode.HALF_UP)
					.compareTo(ibi.getBedrag().setScale(2, RoundingMode.HALF_UP)) != 0) {
				ibi.setCheckresult(__checkresult.AMOUNTMISMATCH);
			}
			if (factuur != null) {
				if (ibi.getFactuur().getSombetalingen()
						.equals(ibi.getFactuurbedrag().setScale(2, RoundingMode.HALF_UP))) {
					ibi.setCheckresult(__checkresult.ALREADYPAID);
					List<FactuurbetalingInfo> betalingen;
					try {
						betalingen = ServiceCaller.factuurFacade(getLoginSession())
								.getFactuurbetalingenForFactuur(factuur.getId());
						for (FactuurbetalingInfo fbi : betalingen) {
							if (DateOnly.equals(fbi.getDatum(), ibi.getDatumbetaling())) {
								;
							} else {
								ibi.setCheckresult(__checkresult.DOUBLEPAYMENT);
							}

						}
					} catch (VerzuimApplicationException | PermissionException | ServiceLocatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return ibi.getCheckresult().toString();
	}

	private void populateTable() {
		tpBetalingen.deleteAllRows();
		for (ImportBetalingInfo ibi : importedbetalingen) {
			if (chckbxAlleenprobleemgevallen.isSelected() && (ibi.getCheckresult() == __checkresult.OK
					|| ibi.getCheckresult() == __checkresult.ALREADYPAID)) {
				;
			} else {
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(ibi.getDatumbetaling());
				rowData.add(ibi.getBedrag());
				rowData.add(ibi.getRekeningnummerBetaler());
				rowData.add(ibi.getBedrijfsnaam());

				FactuurTotaalInfo factuur = ibi.getFactuur();
				if (factuur != null) {
					ibi.setFactuurnummer(factuur.getFactuurnr());
					// ibi.setWerkgeverid(factuur.getWerkgeverid());
					// ibi.setHoldingid(factuur.getHoldingid());
					ibi.setFactuurbedrag(factuur.getTotaalInclBtw());
					if (factuur.getHoldingid() != null) {
						rowData.add(hmHolding.get(factuur.getHoldingid()).getNaam());
					} else {
						rowData.add(hmWerkgever.get(factuur.getWerkgeverid()).getNaam());
					}
					rowData.add(factuur.getFactuurnr().toString());
					rowData.add(factuur.getTotaalInclBtw());
				} else {
					rowData.add("");
					rowData.add("");
					rowData.add(null);
				}
				rowData.add(checkBetaling(ibi));
				tpBetalingen.addRow(rowData, ibi);
			}
		}
	}

	private void initializesearch() {
		HashMap<Integer, FactuurbetalingInfo> hmbetalingen = new HashMap<>();
		Date lastmonth = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastmonth);
		lastmonth = cal.getTime();
		cal.add(Calendar.MONTH, -periodlength); // Maximaal 6 maanden terug
												// zoeken, de rest
												// moet handmatig
		Date firstmonth = cal.getTime();
		try {
			werkgeverSelection.setStartperiode(firstmonth);
			werkgeverSelection.setWerkgevers(werkgevers);
			werkgeverSelection.setHoldings(holdings);
			facturen = ServiceCaller.factuurFacade(getLoginSession()).getFacturenInPeriode(firstmonth, new Date(),
					false);
			factuurbetalingen = ServiceCaller.factuurFacade(getLoginSession()).getFactuurbetalingen();

			for (FactuurbetalingInfo fbi : factuurbetalingen) {
				if (fbi.getRekeningnummerbetaler().isEmpty()) {
					;
				} else {
					hmbetalingen.put(fbi.getFactuurid(), fbi);
				}
			}
			for (FactuurTotaalInfo fti : facturen) {
				if (fti.getSombetalingen().compareTo(BigDecimal.ZERO) == 0) {
					/* Hier zitten geen betalingen bij */
				} else {
					FactuurbetalingInfo fbi = hmbetalingen.get(fti.getId());
					if (fbi != null) {
						if (fti.getWerkgeverid() != null) {
							if (accnrsWerkgever.containsKey(fbi.getRekeningnummerbetaler())) {
								;
							} else {
								accnrsWerkgever.put(fbi.getRekeningnummerbetaler(), fti.getWerkgeverid());
							}
						}
						if (fti.getHoldingid() != null) {
							if (accnrsHolding.containsKey(fbi.getRekeningnummerbetaler())) {
								;
							} else {
								accnrsHolding.put(fbi.getRekeningnummerbetaler(), fti.getHoldingid());
							}
						}
					}
				}
			}
		} catch (ValidationException | VerzuimApplicationException | PermissionException | ServiceLocatorException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		}

	}

	private FactuurTotaalInfo selecteerFactuur(ImportBetalingInfo ibi) {
		Calendar cal = Calendar.getInstance();
		FactuurTotaalInfo factuur = null;
		List<FactuurTotaalInfo> facturen = new ArrayList<>();
		HoldingInfo holding = null;
		WerkgeverInfo werkgever = null;
		Integer werkgeverid = null;
		Integer holdingid = null;

		werkgever = findWerkgeverByRekeningNr(ibi.getRekeningnummerBetaler());
		if (werkgever == null) {
			werkgever = findKlantByNaam(ibi.getBedrijfsnaam());
		} else {
			ibi.setWerkgeverid(werkgever.getId());
		}
		if (werkgever == null) {
			holding = findHoldingByRekeningNr(ibi.getRekeningnummerBetaler());
			if (holding == null) {
				holding = findHoldingByNaam(ibi.getBedrijfsnaam());
			} else {
				ibi.setHoldingid(holding.getId());
			}
		}
		String tokens[] = (ibi.getOmschrijving1() + ibi.getOmschrijving2() + ibi.getOmschrijving3())
				.split("\\s|,|:|;|/|>|<|-");
		for (int i = 0; i < tokens.length; i++) {
			String factuurnr = tokens[i];
			if (factuurnr.length() < 7) {
				/*
				 * Soms wordt een factuurnummer over twee regels verdeeld Dan
				 * staat er uiteindelijk bijvoorbeeld 20 16123. Dit proberen we
				 * te herstellen.
				 */
				if (i > 0) {
					int totallen = tokens[i].length() + tokens[i - 1].length();
					if (StringUtils.isNumeric(tokens[i]) && StringUtils.isNumeric(tokens[i - 1])
							&& (totallen == 7 || totallen == 8)) {
						factuurnr = tokens[i - 1] + tokens[i];
					}
				}
			}
			if (factuurnr.length() == 7 || factuurnr.length() == 8) {
				if (StringUtils.isNumeric(factuurnr)) {
					String jaar = factuurnr.substring(0, 4);
					cal.setTime(ibi.getDatumbetaling());
					if (cal.get(Calendar.YEAR) == Integer.parseInt(jaar)
							|| (cal.get(Calendar.YEAR) + 1) == Integer.parseInt(jaar)) {
						factuur = findFactuurByFactuurnr(Integer.parseInt(factuurnr), ibi.getWerkgeverid(),
								ibi.getHoldingid(),ibi.getBedrag());
						if (factuur == null) {
							/*
							 * Let op: NOODOPLOSSING! Soms is de maand
							 * teruggedraaid geweest en verschillen de
							 * factuurnummers 1 cijfertje doordat er een bedrijf
							 * is tussengevoegd.
							 */
							factuur = findFactuurByFactuurnr(Integer.parseInt(factuurnr) - 1, ibi.getWerkgeverid(),
									ibi.getHoldingid(),ibi.getBedrag());
						}
						if (factuur == null && factuurnr.length() == 7) {
							/*
							 * Probeer het nog eens, maar nu met een nul er
							 * tussen ivm fout eind 2015 in toekennen
							 * factuurnummers
							 */
							String gewijzigdfactuurnr = factuurnr.substring(0, 4) + "0" + factuurnr.substring(4, 7);
							factuur = findFactuurByFactuurnr(Integer.parseInt(gewijzigdfactuurnr), ibi.getWerkgeverid(),
									ibi.getHoldingid(),ibi.getBedrag());
							if (factuur != null) {
								factuurnr = gewijzigdfactuurnr;
							}
						}
						if (factuur != null){
							facturen.add(factuur);
						}
					}
				}
			}
		}
		if (facturen.size() > 1) {
			for (FactuurTotaalInfo fti : facturen) {
				BigDecimal totaal = fti.getTotaalInclBtw();
				totaal = totaal.setScale(2, RoundingMode.HALF_UP);
				if (totaal.compareTo(ibi.getBedrag()) == 0) {
					return fti;
				}
			}
			/* Geen passend bedrag gevonden, dan geven we de eerste terug */
			return facturen.get(0);
		} else {
			if (facturen.size() == 1)
				return facturen.get(0);
			else
				return null;
		}
	}

	private FactuurTotaalInfo findFactuurByFactuurnr(int factuurnr, Integer werkgeverid, Integer holdingid, BigDecimal bedrag) {
		for (FactuurTotaalInfo fti : facturen) {
			if (fti.getFactuurnr() == factuurnr) {
				if (fti.getTotaalInclBtw().compareTo(BigDecimal.ZERO) != 0) {
					/*
					 * Facturen met bedrag nul slaan we over, want daar kunnen
					 * geen betalingen voor zijn
					 */
					if (werkgeverid != null) {
						if (fti.getWerkgeverid().equals(werkgeverid)) {
							/*
							 * match gevonden
							 */
							return fti;
						}
					} else {
						if (holdingid != null && fti.getHoldingid() != null) {
							if (fti.getHoldingid().equals(holdingid)) {
								return fti;
							}
						} else {
							if (fti.getTotaalInclBtw().compareTo(bedrag)==0){
								return fti;
							}
						}
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private HoldingInfo findHoldingByNaam(String bedrijfsnaam) {
		for (HoldingInfo hi : holdings) {
			if (hi.getNaam().equalsIgnoreCase(bedrijfsnaam))
				return hi;
		}
		return null;
	}

	private WerkgeverInfo findKlantByNaam(String bedrijfsnaam) {
		for (WerkgeverInfo wgi : werkgevers) {
			if (wgi.getNaam().equalsIgnoreCase(bedrijfsnaam))
				return wgi;
		}
		return null;
	}

	private WerkgeverInfo findWerkgeverByRekeningNr(String rekeningnummerBetaler) {
		Integer werkgeverid;
		werkgeverid = accnrsWerkgever.get(rekeningnummerBetaler);
		if (werkgeverid != null) {
			for (WerkgeverInfo wgr : werkgevers) {
				if (wgr.getId().equals(werkgeverid)) {
					return wgr;
				}
			}
		}
		return null;
	}

	private HoldingInfo findHoldingByRekeningNr(String rekeningnummerBetaler) {
		Integer holdingid;
		holdingid = accnrsHolding.get(rekeningnummerBetaler);
		if (holdingid != null) {
			for (HoldingInfo h : holdings) {
				if (h.getId().equals(holdingid)) {
					return h;
				}
			}
		}
		return null;
	}

	private Date parseDate(String strdate, String format) throws ValidationException {
		DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
		Date result;
		try {
			if (strdate.isEmpty())
				result = null;
			else
				result = df.parse(strdate);
		} catch (ParseException e) {
			throw new ValidationException("Unexpected date parsing error");
		}
		return result;
	}

	private BigDecimal parseDecimal(String strdecimal) throws ValidationException {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "##0,00";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		BigDecimal bigDecimal;
		decimalFormat.setParseBigDecimal(true);

		// parse the string
		try {
			bigDecimal = (BigDecimal) decimalFormat.parse(strdecimal);
		} catch (ParseException e) {
			throw new ValidationException("Unexpected decimal parsing error");
		}
		return bigDecimal;
	}

	private importresult parseLine(String[] tokens, StringBuilder errormessage, parseTable[] importexcel) {

		for (int i = 0; (i < tokens.length && i < importexcel.length); i++) {

			if (importexcel[i].verplicht)
				if (tokens[i].isEmpty()) {
					errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString() + ") is niet ingevuld");
					return importresult.requiredfieldmissing;
				}
			if ((tokens[i].length() > importexcel[i].maxlen) || (tokens[i].length() < importexcel[i].minlen)) {
				if (importexcel[i].verplicht == false && tokens[i].length() == 0) {
					;
				} else {
					errormessage
							.append("Veld " + i + "(" + importexcel[i].kolom.toString() + ") heeft ongeldige lengte");
					return importresult.invalidlength;
				}
			}
			if (tokens[i].length() > 0) {
				if (importexcel[i].type == "N") {
					try {
						Integer.parseInt(tokens[i]);
					} catch (NumberFormatException e) {
						errormessage
								.append("Veld " + i + "(" + importexcel[i].kolom.toString() + ") is geen geldig getal");
						return importresult.formaterror;
					}
				} else if (importexcel[i].type == "ND") {
					try {
						parseDecimal(tokens[i]);
					} catch (ValidationException e) {
						errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString()
								+ ") is geen geldig decimaal getal(nn.n)");
						return importresult.formaterror;
					}
				} else if (importexcel[i].type == "D") {
					try {
						parseDate(tokens[i], importexcel[i].formaat);
					} catch (ValidationException e) {
						errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString()
								+ ") is geen geldige datum (" + importexcel[i].formaat + ")");
						return importresult.invaliddate;
					}
				} else if (importexcel[i].type == "A") {
					// do nothing
				} else {
					errormessage.append("Veld " + i + "(" + importexcel[i].kolom.toString()
							+ ") heeft onbekend datatype. Neem contact op met T. Gieselaar");
					return importresult.invalidtype;
				}
			}
		}
		return importresult.ok;
	}

	private boolean isEmtpyRow(String[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if (!tokens[i].isEmpty())
				return false;
		}
		return true;
	}

	protected void btnSelectFileClicked(ActionEvent e) {
		File f = new File(txtImportbestandlocatie.getText());
		String path = f.getPath();
		String chosenfile;

		FileDialog fd = new FileDialog((JFrame) null, "Selecteer document", FileDialog.LOAD);
		fd.setFile(f.getName());
		fd.setDirectory(path);
		fd.setMultipleMode(false);
		// fd.setLocation(50, 50);
		fd.setVisible(true);
		if (fd.getFile() == null)
			;
		else {
			chosenfile = fd.getDirectory() + fd.getFile();
			if (chosenfile.isEmpty())
				;
			else
				txtImportbestandlocatie.setText(chosenfile);
		}
	}

	protected void okButtonClicked(ActionEvent e) {
		for (ImportBetalingInfo ibi : importedbetalingen) {
			if (ibi.getCheckresult() != __checkresult.OK && ibi.getCheckresult() != __checkresult.ALREADYPAID) {
				if (ibi.isAccepted()) {
					;
				} else {
					JOptionPane.showMessageDialog(thisform,
							"Er zijn betalingen met problemen.\n\r" + "Los die eerst op of verwijder ze.");
					return;
				}
			}
		}
		for (ImportBetalingInfo ibi : importedbetalingen) {
			FactuurbetalingInfo fbi = new FactuurbetalingInfo();
			fbi.setBedrag(ibi.getBedrag());
			fbi.setDatum(ibi.getDatumbetaling());
			if (ibi.getFactuur().isGesommeerd()) {
				fbi.setFactuurid(ibi.getFactuur().getHoldingFactuurId());
			} else {
				fbi.setFactuurid(ibi.getFactuur().getId());
			}
			fbi.setRekeningnummerbetaler(ibi.getRekeningnummerBetaler());
			if ((ibi.getCheckresult() == __checkresult.OK)
					|| (ibi.getCheckresult() == __checkresult.AMOUNTMISMATCH && ibi.isAccepted())) {
				try {
					ServiceCaller.factuurFacade(getLoginSession()).addFactuurbetaling(fbi);
				} catch (PermissionException | ValidationException | VerzuimApplicationException
						| ServiceLocatorException e1) {
					ExceptionLogger.ProcessException(e1, thisform);
					return;
				}
			}
		}
		super.okButtonClicked(e);
	}
}
