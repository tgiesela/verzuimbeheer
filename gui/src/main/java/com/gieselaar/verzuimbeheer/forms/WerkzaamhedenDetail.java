package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.baseforms.WerkgeverNotification;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__huisbezoekurgentie;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__verzuimsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.Format;

import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverFilter;

import javax.swing.JButton;

public class WerkzaamhedenDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkzaamhedenInfo werkzaamheid;
	private WerkgeverInfo werkgever;
	private boolean vasttariefhuisbezoeken;
	private boolean tariefgevonden;

	private DatePicker dtpDatum;
	private JComboBox<TypeEntry> cmbFiliaal;
	private JComboBox<TypeEntry> cmbActiviteit;
	private JComboBox<TypeEntry> cmbSoorthuisbezoek;
	private JComboBox<TypeEntry> cmbGeslacht;
	private JComboBox<TypeEntry> cmbSoortverzuim;
	private JComboBox<TypeEntry> cmbGebruiker;
	private List<GebruikerInfo> gebruikers;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private List<OeInfo> oes;
	private JFormattedTextField txtUren;
	private JFormattedTextField txtPersoneelsnr;
	private JFormattedTextField txtAantalkm;
	private JFormattedTextField txtOverigekosten;
	private JTextAreaTGI taOmschrijving;
	private JTextFieldTGI txtWoonplaats;
	private JTextFieldTGI txtPersoon;
	private JButton btnRefreshFilialen;
	private WerkgeverSelection werkgeverSelection;

	private JLabel lblUitgevoerdDoor;

	private BaseDetailform thisform = this;

	/**
	 * Create the frame.
	 */
	public WerkzaamhedenDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer werkzaamheden", mdiPanel);
		initialize();
	}
	private void getTariefsoortHuisbezoek(Date peildatum, int werkgeverid){
		tariefgevonden = false;
		vasttariefhuisbezoeken = true;
		try {
			vasttariefhuisbezoeken = ServiceCaller.werkgeverFacade(
					thisform.getLoginSession())
					.isVasttariefHuisbezoeken(peildatum, null,
							werkgeverid);
			tariefgevonden = true;
		} catch (VerzuimApplicationException e) {
		} catch (ValidationException e) {
		} catch (ServiceLocatorException e) {
		} catch (PermissionException e) {
		}

	}

	private void initialize() {
		setBounds(0, 237, 527, 533);
		getContentPane().setLayout(null);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		dtpDatum = new DatePicker();
		dtpDatum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				werkzaamheid.setDatum(dtpDatum.getDate());
			}
		});
		dtpDatum.setBounds(123, 75, 97, 21);
		dtpDatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatum);

		JLabel lblIngangsdatum = new JLabel("Datum");
		lblIngangsdatum.setBounds(10, 78, 86, 14);
		getContentPane().add(lblIngangsdatum);

		txtPersoon = new JTextFieldTGI();
		txtPersoon.setColumns(10);
		txtPersoon.setBounds(123, 144, 355, 20);
		getContentPane().add(txtPersoon);

		cmbActiviteit = new JComboBox<>();
		cmbActiviteit.setBounds(123, 98, 238, 20);
		cmbActiviteit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cmbActiviteitClicked(e);

			}
		});
		getContentPane().add(cmbActiviteit);

		JLabel lblActiviteit = new JLabel("Activiteit");
		lblActiviteit.setBounds(10, 101, 65, 14);
		getContentPane().add(lblActiviteit);

		cmbSoorthuisbezoek = new JComboBox<>();
		cmbSoorthuisbezoek.setBounds(123, 121, 238, 20);
		getContentPane().add(cmbSoorthuisbezoek);

		JLabel lblSoortHuisbezoek = new JLabel("Soort huisbezoek");
		lblSoortHuisbezoek.setBounds(10, 124, 105, 14);
		getContentPane().add(lblSoortHuisbezoek);

		JLabel lblPersoon = new JLabel("Persoon");
		lblPersoon.setBounds(10, 147, 105, 14);
		getContentPane().add(lblPersoon);

		JLabel lblWoonplaats = new JLabel("Woonplaats");
		lblWoonplaats.setBounds(10, 170, 105, 14);
		getContentPane().add(lblWoonplaats);

		txtWoonplaats = new JTextFieldTGI();
		txtWoonplaats.setColumns(10);
		txtWoonplaats.setBounds(123, 167, 355, 20);
		getContentPane().add(txtWoonplaats);

		JLabel lblFiliaal = new JLabel("Filiaal");
		lblFiliaal.setBounds(10, 193, 105, 14);
		getContentPane().add(lblFiliaal);

		cmbFiliaal = new JComboBox<>();
		cmbFiliaal.setBounds(123, 190, 238, 20);
		getContentPane().add(cmbFiliaal);

		txtUren = new JFormattedTextField(amountFormat);
		txtUren.setBounds(123, 259, 76, 20);
		getContentPane().add(txtUren);
		txtUren.setColumns(10);
		txtUren.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblBedrag = new JLabel("Uren");
		lblBedrag.setBounds(10, 262, 95, 14);
		getContentPane().add(lblBedrag);

		JLabel lblPersoneelsnr = new JLabel("Personeelsnr");
		lblPersoneelsnr.setBounds(10, 216, 95, 14);
		getContentPane().add(lblPersoneelsnr);

		txtPersoneelsnr = new JFormattedTextField((Format) null);
		txtPersoneelsnr.setColumns(10);
		txtPersoneelsnr.setBounds(123, 213, 76, 20);
		getContentPane().add(txtPersoneelsnr);

		cmbGeslacht = new JComboBox<>();
		cmbGeslacht.setBounds(276, 213, 114, 20);
		getContentPane().add(cmbGeslacht);

		JLabel lblGeslacht = new JLabel("Geslacht");
		lblGeslacht.setBounds(209, 216, 52, 14);
		getContentPane().add(lblGeslacht);

		JLabel lblSoortVerzuim = new JLabel("Soort verzuim");
		lblSoortVerzuim.setBounds(10, 239, 105, 14);
		getContentPane().add(lblSoortVerzuim);

		cmbSoortverzuim = new JComboBox<>();
		cmbSoortverzuim.setBounds(123, 236, 238, 20);
		getContentPane().add(cmbSoortverzuim);

		JLabel lblAantalKm = new JLabel("Aantal km");
		lblAantalKm.setBounds(10, 285, 95, 14);
		getContentPane().add(lblAantalKm);

		JLabel lblOverigeKosten = new JLabel("Overige kosten");
		lblOverigeKosten.setBounds(10, 308, 95, 14);
		getContentPane().add(lblOverigeKosten);

		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(10, 331, 95, 14);
		getContentPane().add(lblOmschrijving);

		txtAantalkm = new JFormattedTextField(amountFormat);
		txtAantalkm.setColumns(10);
		txtAantalkm.setBounds(123, 282, 76, 20);
		txtAantalkm.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(txtAantalkm);

		txtOverigekosten = new JFormattedTextField(amountFormat);
		txtOverigekosten.setColumns(10);
		txtOverigekosten.setBounds(123, 305, 76, 20);
		txtOverigekosten.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(txtOverigekosten);

		taOmschrijving = new JTextAreaTGI();
		taOmschrijving.setBounds(123, 328, 337, 117);
		taOmschrijving.setFont(txtPersoon.getFont());
		getContentPane().add(taOmschrijving);

		cmbGebruiker = new JComboBox<>();
		cmbGebruiker.setBounds(123, 451, 238, 20);
		getContentPane().add(cmbGebruiker);

		lblUitgevoerdDoor = new JLabel("Uitgevoerd door");
		lblUitgevoerdDoor.setBounds(10, 454, 95, 14);
		getContentPane().add(lblUitgevoerdDoor);

		werkgeverSelection = new WerkgeverSelection(
				__WerkgeverSelectionMode.SelectBoth, __WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(10, 11, 374, 56);
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				Date peildatum = new Date();
				werkzaamheid.setWerkgeverid(werkgeverid);
				populateFilialen();
				try {
					werkgever = ServiceCaller
							.werkgeverFacade(getLoginSession()).getWerkgever(
									werkgeverid);
					if (dtpDatum.getDate() != null)
						peildatum = dtpDatum.getDate();
					getTariefsoortHuisbezoek(peildatum, werkgeverid);
				} catch (ServiceLocatorException e) {
					ExceptionLogger.ProcessException(e, thisform,
							"Unable to connect to server");
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (Exception e1) {
					ExceptionLogger.ProcessException(e1, thisform);
				}

				EnableDisableFields();
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				Date peildatum = new Date();
				werkzaamheid.setHoldingid(holdingid);
				try {
					if (dtpDatum.getDate() != null)
						peildatum = dtpDatum.getDate();
					vasttariefhuisbezoeken = true;
					vasttariefhuisbezoeken = ServiceCaller.werkgeverFacade(
							thisform.getLoginSession())
							.isVasttariefHuisbezoeken(peildatum, holdingid,
									null);
				} catch (ServiceLocatorException e) {
					ExceptionLogger.ProcessException(e, thisform,
							"Unable to connect to server");
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (Exception e1) {
					ExceptionLogger.ProcessException(e1, thisform);
				}
				EnableDisableFields();
				return false;
			}
		});

		btnRefreshFilialen = new JButton("Ververs");
		btnRefreshFilialen.setBounds(363, 189, 85, 23);
		btnRefreshFilialen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				populateFilialen();
			}
		});
		getContentPane().add(btnRefreshFilialen);
	}

	protected void cmbActiviteitClicked(ActionEvent e) {
		EnableDisableFields();
		displayWerkzaamheid();
	}

	public void setInfo(InfoBase info) {
		try {
			werkzaamheid = (WerkzaamhedenInfo) info;
			gebruikers = ServiceCaller.autorisatieFacade(getLoginSession())
					.getGebruikers();
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession())
					.allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession())
					.getHoldings();
			oes = ServiceCaller.werkgeverFacade(getLoginSession()).getOes();
		} catch (ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e, this,
					"Unable to connect to server");
			return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		werkgeverSelection.setHoldings(holdings);
		werkgeverSelection.setWerkgevers(werkgevers);

		DefaultComboBoxModel<TypeEntry> gebruikerModel = new DefaultComboBoxModel<TypeEntry>();
		for (GebruikerInfo g : gebruikers) {
			String naam = g.getVoornaam() + " " + g.getTussenvoegsel() + " "
					+ g.getAchternaam();
			TypeEntry gt = new TypeEntry(g.getId(), naam);
			gebruikerModel.addElement(gt);
		}
		cmbGebruiker.setModel(gebruikerModel);

		DefaultComboBoxModel<TypeEntry> activiteitModel = new DefaultComboBoxModel<TypeEntry>();
		for (__werkzaamhedensoort g : __werkzaamhedensoort.values()) {
			activiteitModel
					.addElement(new TypeEntry(g.getValue(), g.toString()));
		}
		cmbActiviteit.setModel(activiteitModel);

		DefaultComboBoxModel<TypeEntry> verzuimsoortModel = new DefaultComboBoxModel<TypeEntry>();
		for (__verzuimsoort g : __verzuimsoort.values()) {
			verzuimsoortModel.addElement(new TypeEntry(g.getValue(), g
					.toString()));
		}
		cmbSoortverzuim.setModel(verzuimsoortModel);

		DefaultComboBoxModel<TypeEntry> geslachtModel = new DefaultComboBoxModel<TypeEntry>();
		for (__geslacht g : __geslacht.values()) {
			geslachtModel.addElement(new TypeEntry(g.getValue(), g.toString()));
		}
		cmbGeslacht.setModel(geslachtModel);

		DefaultComboBoxModel<TypeEntry> soorthuisbezoekModel = new DefaultComboBoxModel<TypeEntry>();
		for (__huisbezoekurgentie g : __huisbezoekurgentie.values()) {
			soorthuisbezoekModel.addElement(new TypeEntry(g.getValue(), g
					.toString()));
		}
		cmbSoorthuisbezoek.setModel(soorthuisbezoekModel);

		for (int i = 0; i < cmbActiviteit.getModel().getSize(); i++) {
			TypeEntry soort = (TypeEntry) cmbActiviteit.getModel()
					.getElementAt(i);
			if (__werkzaamhedensoort.parse(soort.getValue()) == werkzaamheid
					.getSoortwerkzaamheden()) {
				cmbActiviteit.getModel().setSelectedItem(soort);
				break;
			}
		}
		if (this.getMode() != formMode.New) {
			getTariefsoortHuisbezoek(werkzaamheid.getDatum(), werkzaamheid.getWerkgeverid());
		}
		displayWerkzaamheid();
		activateListener();
	}

	private void EnableDisableFields() {
		TypeEntry activiteit = (TypeEntry) cmbActiviteit.getSelectedItem();
		if (activiteit != null) {
			switch (__werkzaamhedensoort.parse(activiteit.getValue())) {
			case HUISBEZOEK:
			case CASEMANAGEMENT:
				txtAantalkm.setEnabled(true);
				txtPersoon.setEnabled(true);
				txtPersoneelsnr.setEnabled(true);
				txtWoonplaats.setEnabled(true);
				txtOverigekosten.setEnabled(true);
				cmbGeslacht.setEnabled(true);
				cmbSoortverzuim.setEnabled(true);
				if (vasttariefhuisbezoeken)
					cmbSoorthuisbezoek.setEnabled(true);
				else
					cmbSoorthuisbezoek.setEnabled(false);
				break;

			case SECRETARIAAT:
				txtAantalkm.setEnabled(false);
				txtPersoon.setEnabled(false);
				txtPersoneelsnr.setEnabled(false);
				txtWoonplaats.setEnabled(false);
				txtOverigekosten.setEnabled(false);
				cmbGeslacht.setEnabled(false);
				cmbSoortverzuim.setEnabled(false);
				cmbSoorthuisbezoek.setEnabled(false);
				cmbFiliaal.setEnabled(false);
				btnRefreshFilialen.setEnabled(false);
				txtAantalkm.setText("");
				txtPersoon.setText("");
				txtPersoneelsnr.setText("");
				txtWoonplaats.setText("");
				txtOverigekosten.setText("");
				cmbGeslacht.setSelectedIndex(-1);
				cmbSoortverzuim.setSelectedIndex(-1);
				cmbSoorthuisbezoek.setSelectedIndex(-1);

				break;
			case DOKTERBEZOEK:
			case VERLOF:
			case OVERWERK:
				txtAantalkm.setEnabled(false);
				txtPersoon.setEnabled(false);
				txtPersoneelsnr.setEnabled(false);
				txtWoonplaats.setEnabled(false);
				txtOverigekosten.setEnabled(false);
				cmbGeslacht.setEnabled(false);
				cmbSoortverzuim.setEnabled(false);
				cmbSoorthuisbezoek.setEnabled(false);
				cmbFiliaal.setEnabled(false);
				btnRefreshFilialen.setEnabled(false);
				txtAantalkm.setText("");
				txtPersoon.setText("");
				txtPersoneelsnr.setText("");
				txtWoonplaats.setText("");
				txtOverigekosten.setText("");
				cmbGeslacht.setSelectedIndex(-1);
				cmbSoortverzuim.setSelectedIndex(-1);
				cmbSoorthuisbezoek.setSelectedIndex(-1);
				break;
			default:
				break;
			}
		}
	}

	private void populateFilialen() {
		cmbFiliaal.setEnabled(false);
		btnRefreshFilialen.setEnabled(false);
		if (oes != null && oes.size() > 0 && werkzaamheid.getWerkgeverid() > 0) {
			/*
			 * Eerst zoeken we een entry met OeNiveau 1 waarin werkgever id
			 * overeenkomt met de werkgever.
			 */
			OeInfo.sort(oes, OeInfo.__sortcol.NAAM);
			DefaultComboBoxModel<TypeEntry> filiaalModel = (DefaultComboBoxModel<TypeEntry>) cmbFiliaal
					.getModel();
			filiaalModel.removeAllElements();
			filiaalModel.addElement(new TypeEntry(-1, "[]"));
			for (OeInfo g : oes) {
				if (g.getWerkgeverId() != null) {
					if (g.getOeniveau().getOeniveau().equals(1)
							&& g.getWerkgeverId().equals(
									werkzaamheid.getWerkgeverid())) {
						/*
						 * Alle OE's onder dit niveau worden als filiaal
						 * beschouwd.
						 */
						for (OeInfo filiaal : oes) {
							if (filiaal.getParentoeId() != null) {
								if (filiaal.getParentoeId().equals(g.getId())) {
									filiaalModel
											.addElement(new TypeEntry(filiaal
													.getId(), filiaal.getNaam()));
								}
							}
						}
						cmbFiliaal.setEnabled(true);
						btnRefreshFilialen.setEnabled(true);
					}
				}
			}
		}

	}

	private void displayWerkzaamheid() {
		try {
			dtpDatum.setDate(werkzaamheid.getDatum());
			werkgeverSelection.setHoldingId(werkzaamheid.getHoldingid());
			werkgeverSelection.setWerkgeverId(werkzaamheid.getWerkgeverid());
			if (werkzaamheid.getWerkgeverid() != null)
				populateFilialen();

			cmbGeslacht.getModel().setSelectedItem(
					cmbGeslacht.getModel().getElementAt(0));
			for (int i = 0; i < cmbGeslacht.getModel().getSize(); i++) {
				TypeEntry geslacht = (TypeEntry) cmbGeslacht.getModel()
						.getElementAt(i);
				if (__geslacht.parse(geslacht.getValue()) == werkzaamheid
						.getGeslacht()) {
					cmbGeslacht.getModel().setSelectedItem(geslacht);
					break;
				}
			}

			cmbSoortverzuim.getModel().setSelectedItem(
					cmbSoortverzuim.getModel().getElementAt(0));
			for (int i = 0; i < cmbSoortverzuim.getModel().getSize(); i++) {
				TypeEntry soort = (TypeEntry) cmbSoortverzuim.getModel()
						.getElementAt(i);
				if (__verzuimsoort.parse(soort.getValue()) == werkzaamheid
						.getSoortverzuim()) {
					cmbSoortverzuim.getModel().setSelectedItem(soort);
					break;
				}
			}

			cmbSoorthuisbezoek.getModel().setSelectedItem(
					cmbSoorthuisbezoek.getModel().getElementAt(0));
			for (int i = 0; i < cmbSoorthuisbezoek.getModel().getSize(); i++) {
				TypeEntry soort = (TypeEntry) cmbSoorthuisbezoek.getModel()
						.getElementAt(i);
				if (__huisbezoekurgentie.parse(soort.getValue()) == werkzaamheid
						.getUrgentie()) {
					cmbSoorthuisbezoek.getModel().setSelectedItem(soort);
					break;
				}
			}
			if (werkzaamheid.getWerkgeverid() != null)
				cmbFiliaal.setSelectedIndex(0);
			for (int i = 0; i < cmbFiliaal.getModel().getSize(); i++) {
				TypeEntry soort = (TypeEntry) cmbFiliaal.getModel()
						.getElementAt(i);
				if (werkzaamheid.getFiliaalid() != null) {
					if (soort.getValue() == werkzaamheid.getFiliaalid()) {
						cmbFiliaal.getModel().setSelectedItem(soort);
						break;
					}
				}
			}
			for (int i = 0; i < cmbGebruiker.getModel().getSize(); i++) {
				TypeEntry soort = (TypeEntry) cmbGebruiker.getModel()
						.getElementAt(i);
				if (soort.getValue() == werkzaamheid.getUserid()) {
					cmbGebruiker.getModel().setSelectedItem(soort);
					break;
				}
			}
			txtPersoon.setText(werkzaamheid.getPersoon());
			txtPersoneelsnr.setText(werkzaamheid.getPersoneelsnummer());
			txtAantalkm.setValue(werkzaamheid.getAantalkm());
			txtOverigekosten.setValue(werkzaamheid.getOverigekosten());
			taOmschrijving.setText(werkzaamheid.getOmschrijving());
			txtUren.setValue(werkzaamheid.getUren());
			txtWoonplaats.setText(werkzaamheid.getWoonplaats());

			EnableDisableFields();

		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e, this);
		}
	}

	private BigDecimal formatBigDecimal(DecimalFormat format, String value)
			throws ValidationException {
		BigDecimal bedrag;
		ParsePosition pos = new ParsePosition(0);
		bedrag = (BigDecimal) format.parse(value, pos);
		if (pos.getIndex() < value.length()) {
			throw new ValidationException("Ongeldige tekens in bedrag");
		}
		return bedrag;
	}

	protected void okButtonClicked(ActionEvent e) {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		if (werkzaamheid.getWerkgeverid() == null
				|| werkzaamheid.getWerkgeverid().equals(-1)) {
			JOptionPane.showMessageDialog(this, "Geen werkgever geselecteerd.");
			return;
		}

		try {

			TypeEntry activiteit = (TypeEntry) cmbActiviteit.getSelectedItem();
			TypeEntry gebruiker = (TypeEntry) cmbGebruiker.getSelectedItem();

			if (werkzaamheid.getHoldingid() == null) {
				if (werkgeverSelection.getHolding() != null) {
					werkzaamheid.setHoldingid(werkgeverSelection.getHolding()
							.getId());
				}
			}

			werkzaamheid.setDatum(dtpDatum.getDate());
			werkzaamheid.setUren(formatBigDecimal(format, txtUren.getText()));
			werkzaamheid.setFiliaalid(null);
			werkzaamheid.setSoortwerkzaamheden(__werkzaamhedensoort
					.parse(activiteit.getValue()));
			werkzaamheid.setGeslacht(__geslacht.ONBEKEND);
			werkzaamheid.setUrgentie(__huisbezoekurgentie.NVT);
			werkzaamheid.setSoortverzuim(__verzuimsoort.ONBEKEND);

			werkzaamheid.setUserid(gebruiker.getValue());
			werkzaamheid.setOmschrijving(taOmschrijving.getText());

			switch (__werkzaamhedensoort.parse(activiteit.getValue())) {
			case CASEMANAGEMENT:
			case HUISBEZOEK:
				werkzaamheid.setPersoon(txtPersoon.getText());
				werkzaamheid.setPersoneelsnummer(txtPersoneelsnr.getText());
				werkzaamheid.setWoonplaats(txtWoonplaats.getText());
				if (txtOverigekosten.getText().isEmpty())
					werkzaamheid.setOverigekosten(BigDecimal.ZERO);
				else
					werkzaamheid.setOverigekosten(formatBigDecimal(format,
							txtOverigekosten.getText()));
				werkzaamheid.setAantalkm(formatBigDecimal(format,
						txtAantalkm.getText()));
				TypeEntry filiaal;
				if (cmbFiliaal.isEnabled()) {
					filiaal = (TypeEntry) cmbFiliaal.getSelectedItem();
					if (filiaal.getValue() == -1){
						JOptionPane.showMessageDialog(this, "Geen filiaal geselecteerd.");
						return;
					}
				} else {
					filiaal = null;
				}
				if (filiaal != null) {
					werkzaamheid.setFiliaalid(filiaal.getValue());
				}
				TypeEntry geslacht = (TypeEntry) cmbGeslacht.getSelectedItem();
				TypeEntry urgentie = (TypeEntry) cmbSoorthuisbezoek
						.getSelectedItem();
				if (vasttariefhuisbezoeken && (__werkzaamhedensoort.parse(activiteit.getValue()) == __werkzaamhedensoort.HUISBEZOEK)) {
					if (urgentie.getValue() == __huisbezoekurgentie.NVT
							.getValue()) {
						JOptionPane.showMessageDialog(this,
								"Geen soort huisbezoek geselecteerd.");
						return;
					}
				}
				TypeEntry soortvzm = (TypeEntry) cmbSoortverzuim
						.getSelectedItem();

				werkzaamheid.setGeslacht(__geslacht.parse(geslacht.getValue()));
				werkzaamheid.setUrgentie(__huisbezoekurgentie.parse(urgentie
						.getValue()));
				werkzaamheid.setSoortverzuim(__verzuimsoort.parse(soortvzm
						.getValue()));
				break;
			case SECRETARIAAT:
				werkzaamheid.setPersoon("");
				werkzaamheid.setPersoneelsnummer("");
				werkzaamheid.setWoonplaats("");
				werkzaamheid.setOverigekosten(BigDecimal.ZERO);
				werkzaamheid.setAantalkm(BigDecimal.ZERO);
				break;
			default:
				werkzaamheid.setPersoon("");
				werkzaamheid.setPersoneelsnummer("");
				werkzaamheid.setWoonplaats("");
				werkzaamheid.setOverigekosten(BigDecimal.ZERO);
				werkzaamheid.setAantalkm(BigDecimal.ZERO);
				break;

			}

		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		if (this.getLoginSession() != null) {
			try {
				werkzaamheid.validate();
				switch (this.getMode()) {
				case New:
					ServiceCaller.factuurFacade(getLoginSession())
							.addWerkzaamheid(werkzaamheid);
					break;
				case Update:
					ServiceCaller.factuurFacade(getLoginSession())
							.updateWerkzaamheid(werkzaamheid);
					break;
				case Delete:
					ServiceCaller.factuurFacade(getLoginSession())
							.deleteWerkzaamheid(werkzaamheid);
					break;
				}
				if (this.getMode() != formMode.New){
					super.okButtonClicked(e);
				}else{
					WerkzaamhedenInfo newwzi = new WerkzaamhedenInfo();
					newwzi.setWerkgeverid(werkzaamheid.getWerkgeverid());
					newwzi.setDatum(werkzaamheid.getDatum());
					newwzi.setHoldingid(werkzaamheid.getHoldingid());
					newwzi.setSoortwerkzaamheden(werkzaamheid
							.getSoortwerkzaamheden());
					newwzi.setUserid(werkzaamheid.getUserid());
					newwzi.setOverigekosten(BigDecimal.ZERO);
					newwzi.setAantalkm(BigDecimal.ZERO);
					newwzi.setUren(BigDecimal.ZERO);
	
					this.setInfo(newwzi);
				}

			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1, this);
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, this);
			} catch (Exception e1) {
				ExceptionLogger.ProcessException(e1, this);
			}
		} else
			JOptionPane.showMessageDialog(this,
					"Logic error: loginSession is null");
	}

	protected void cancelButtonClicked(ActionEvent e) {
		/*
		 * We beschouwen cancel hier al ok, zodat het werkzaamhedenoverzicht
		 * weer wordt ververst.
		 */
		super.okButtonClicked(e);
	}

}
