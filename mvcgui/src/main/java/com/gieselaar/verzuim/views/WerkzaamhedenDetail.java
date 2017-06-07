package com.gieselaar.verzuim.views;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.WerkzaamhedenController;
import com.gieselaar.verzuim.interfaces.WerkgeverNotification;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__huisbezoekurgentie;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__verzuimsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.Format;

import javax.swing.JButton;

public class WerkzaamhedenDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private AbstractDetail thisform = this;
	private WerkzaamhedenInfo werkzaamheid;
	private boolean vasttariefhuisbezoeken;
	private DatePicker dtpDatum;
	private JComboBox<TypeEntry> cmbFiliaal;
	private JComboBox<TypeEntry> cmbActiviteit;
	private JComboBox<TypeEntry> cmbSoorthuisbezoek;
	private JComboBox<TypeEntry> cmbGeslacht;
	private JComboBox<TypeEntry> cmbSoortverzuim;
	private JComboBox<TypeEntry> cmbGebruiker;
	private VerzuimComboBoxModel gebruikerModel; 
	private VerzuimComboBoxModel activiteitModel; 
	private VerzuimComboBoxModel verzuimsoortModel;
	private VerzuimComboBoxModel geslachtModel;
	private VerzuimComboBoxModel soorthuisbezoekModel;
	private VerzuimComboBoxModel filiaalModel;
	private JFormattedTextField txtUren;
	private JFormattedTextField txtPersoneelsnr;
	private JFormattedTextField txtAantalkm;
	private JFormattedTextField txtOverigekosten;
	private JTextAreaTGI taOmschrijving;
	private JTextFieldTGI txtWoonplaats;
	private JTextFieldTGI txtPersoon;
	private JButton btnRefreshFilialen;
	private WerkgeverSelection werkgeverSelection;
	private WerkzaamhedenController werkzaamhedencontroller;


	/**
	 * Create the frame.
	 */
	public WerkzaamhedenDetail(AbstractController controller) {
		super("Beheer werkzaamheden", controller);
		werkzaamhedencontroller = (WerkzaamhedenController) controller;

		setCloseAfterSave(false);
		
		initialize();
	}
	@Override
	public void setData(InfoBase info) {
		werkzaamheid = (WerkzaamhedenInfo) info;

		gebruikerModel = werkzaamhedencontroller.getComboModelGebruiker();
		cmbGebruiker.setModel(gebruikerModel);

		activiteitModel = controller.getMaincontroller().getEnumModel(__werkzaamhedensoort.class);
		cmbActiviteit.setModel(activiteitModel);

		verzuimsoortModel = controller.getMaincontroller().getEnumModel(__verzuimsoort.class);
		cmbSoortverzuim.setModel(verzuimsoortModel);

		geslachtModel = controller.getMaincontroller().getEnumModel(__geslacht.class);
		cmbGeslacht.setModel(geslachtModel);

		soorthuisbezoekModel = controller.getMaincontroller().getEnumModel(__huisbezoekurgentie.class);
		cmbSoorthuisbezoek.setModel(soorthuisbezoekModel);

		activiteitModel.setId(werkzaamheid.getSoortwerkzaamheden().getValue());
		if (this.getFormmode() != __formmode.NEW) {
			getTariefsoortHuisbezoek(werkzaamheid.getDatum(), werkzaamheid.getHoldingid(), werkzaamheid.getWerkgeverid());
		}
		displayWerkzaamheid();
	}
	private void getTariefsoortHuisbezoek(Date peildatum,  Integer holdingid, Integer werkgeverid){
		vasttariefhuisbezoeken = true;
		try {
			vasttariefhuisbezoeken = werkzaamhedencontroller.isVasttariefHuisbezoeken(peildatum, holdingid, werkgeverid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		} 
	}

	private void initialize() {
		setBounds(50, 50, 527, 533);

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

		JLabel lblUitgevoerdDoor = new JLabel("Uitgevoerd door");
		lblUitgevoerdDoor.setBounds(10, 454, 95, 14);
		getContentPane().add(lblUitgevoerdDoor);

		addWerkgeverSelectionPanel();

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

	private void addWerkgeverSelectionPanel() {
		werkgeverSelection = new WerkgeverSelection(
				__WerkgeverSelectionMode.SelectBoth, __WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(10, 11, 374, 56);
		werkgeverSelection.setMaincontroller(controller.getMaincontroller());
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				Date peildatum = new Date();
				werkzaamheid.setWerkgeverid(werkgeverid);

				populateFilialen();
				if (dtpDatum.getDate() != null)
					peildatum = dtpDatum.getDate();
				getTariefsoortHuisbezoek(peildatum, werkgeverSelection.getHoldingId(), werkgeverid);

				EnableDisableFields();
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				Date peildatum = new Date();
				werkzaamheid.setHoldingid(holdingid);
				if (dtpDatum.getDate() != null)
					peildatum = dtpDatum.getDate();
				vasttariefhuisbezoeken = true;
				try {
					vasttariefhuisbezoeken = werkzaamhedencontroller.isVasttariefHuisbezoeken(peildatum, holdingid, werkgeverSelection.getWerkgeverId());
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
				}
				EnableDisableFields();
				return false;
			}
		});
		
	}
	protected void cmbActiviteitClicked(ActionEvent e) {
		EnableDisableFields();
		displayWerkzaamheid();
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
		filiaalModel = werkzaamhedencontroller.getComboModelFilialen(werkzaamheid.getWerkgeverid());
		cmbFiliaal.setModel(filiaalModel);
		if (filiaalModel.getSize() > 1){
			cmbFiliaal.setEnabled(true);
			btnRefreshFilialen.setEnabled(true);
		}

	}

	private void displayWerkzaamheid() {
		try {
			dtpDatum.setDate(werkzaamheid.getDatum());
			werkgeverSelection.setHoldingId(werkzaamheid.getHoldingid());
			werkgeverSelection.setWerkgeverId(werkzaamheid.getWerkgeverid());
			if (werkzaamheid.getWerkgeverid() != null)
				populateFilialen();

			geslachtModel.setId(werkzaamheid.getGeslacht().getValue());
			verzuimsoortModel.setId(werkzaamheid.getSoortverzuim().getValue());
			soorthuisbezoekModel.setId(werkzaamheid.getUrgentie().getValue());
			filiaalModel = werkzaamhedencontroller.getComboModelFilialen(werkzaamheid.getWerkgeverid());
			cmbFiliaal.setModel(filiaalModel);
			filiaalModel.setId(werkzaamheid.getFiliaalid());
			gebruikerModel.setId(werkzaamheid.getUserid());
			txtPersoon.setText(werkzaamheid.getPersoon());
			txtPersoneelsnr.setText(werkzaamheid.getPersoneelsnummer());
			txtAantalkm.setValue(werkzaamheid.getAantalkm());
			txtOverigekosten.setValue(werkzaamheid.getOverigekosten());
			taOmschrijving.setText(werkzaamheid.getOmschrijving());
			txtUren.setValue(werkzaamheid.getUren());
			txtWoonplaats.setText(werkzaamheid.getWoonplaats());

			EnableDisableFields();

		} catch (PropertyVetoException e) {
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
	@Override
	public InfoBase collectData() {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		if (werkzaamheid.getWerkgeverid() == null
				|| werkzaamheid.getWerkgeverid().equals(-1)) {
			JOptionPane.showMessageDialog(this, "Geen werkgever geselecteerd.");
			return null;
		}

		Integer activiteitid = activiteitModel.getId();
		Integer gebruikerid = gebruikerModel.getId();

		if ((werkzaamheid.getHoldingid() == null) &&
		    (werkgeverSelection.getHolding() != null)) {
			werkzaamheid.setHoldingid(werkgeverSelection.getHolding().getId());
		}

		werkzaamheid.setDatum(dtpDatum.getDate());
		try {
			werkzaamheid.setUren(formatBigDecimal(format, txtUren.getText()));
		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
		werkzaamheid.setFiliaalid(null);
		werkzaamheid.setSoortwerkzaamheden(__werkzaamhedensoort.parse(activiteitid));
		werkzaamheid.setGeslacht(__geslacht.ONBEKEND);
		werkzaamheid.setUrgentie(__huisbezoekurgentie.NVT);
		werkzaamheid.setSoortverzuim(__verzuimsoort.ONBEKEND);

		werkzaamheid.setUserid(gebruikerid);
		werkzaamheid.setOmschrijving(taOmschrijving.getText());

		switch (__werkzaamhedensoort.parse(activiteitid)) {
		case CASEMANAGEMENT:
		case HUISBEZOEK:
			werkzaamheid.setPersoon(txtPersoon.getText());
			werkzaamheid.setPersoneelsnummer(txtPersoneelsnr.getText());
			werkzaamheid.setWoonplaats(txtWoonplaats.getText());
			if (txtOverigekosten.getText().isEmpty())
				werkzaamheid.setOverigekosten(BigDecimal.ZERO);
			else
				try {
					werkzaamheid.setOverigekosten(formatBigDecimal(format,
							txtOverigekosten.getText()));
					werkzaamheid.setAantalkm(formatBigDecimal(format,
							txtAantalkm.getText()));
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e, this);
				}
			Integer filiaalid; 
			if (cmbFiliaal.isEnabled()) {
				filiaalid = filiaalModel.getId();
				if (filiaalid == -1){
					JOptionPane.showMessageDialog(this, "Geen filiaal geselecteerd.");
					return null;
				}
			} else {
				filiaalid = null;
			}
			if (filiaalid != null && filiaalid != -1) {
				werkzaamheid.setFiliaalid(filiaalid);
			}
			Integer geslachtid = geslachtModel.getId();
			Integer urgentieid = soorthuisbezoekModel.getId();
			if (vasttariefhuisbezoeken && 
			   (__werkzaamhedensoort.parse(activiteitid) == __werkzaamhedensoort.HUISBEZOEK) &&
			   (urgentieid == __huisbezoekurgentie.NVT.getValue())) {
				JOptionPane.showMessageDialog(this,	"Geen soort huisbezoek geselecteerd.");
			}
			Integer soortvzmid = verzuimsoortModel.getId();

			werkzaamheid.setGeslacht(__geslacht.parse(geslachtid));
			werkzaamheid.setUrgentie(__huisbezoekurgentie.parse(urgentieid));
			werkzaamheid.setSoortverzuim(__verzuimsoort.parse(soortvzmid));
			break;
		case SECRETARIAAT:
		default:
			werkzaamheid.setPersoon("");
			werkzaamheid.setPersoneelsnummer("");
			werkzaamheid.setWoonplaats("");
			werkzaamheid.setOverigekosten(BigDecimal.ZERO);
			werkzaamheid.setAantalkm(BigDecimal.ZERO);
			break;
		}
		return werkzaamheid;
	}

}
