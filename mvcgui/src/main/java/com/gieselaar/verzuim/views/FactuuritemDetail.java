package com.gieselaar.verzuim.views;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.controllers.FactuuritemController;
import com.gieselaar.verzuim.interfaces.WerkgeverNotification;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuim.viewsutils.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FactuuritemDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private FactuuritemInfo factuuritem;
	private DatePicker dtpDatum;
	private JComboBox<TypeEntry> cmbFactuurkoppen;
	private JComboBox<TypeEntry> cmbFactuurcategorien;
	private VerzuimComboBoxModel factuurcategoriemodel;
	private VerzuimComboBoxModel factuurkopmodel;
	private JFormattedTextField txtBedrag;
	private JTextAreaTGI taOmschrijving;

	private WerkgeverSelection werkgeverSelection;
	private FactuuritemController factuuritemcontroller;

	/**
	 * Create the frame.
	 */
	public FactuuritemDetail(AbstractController controller) {
		super("Beheer losse factuuritems", controller);
		factuuritemcontroller = (FactuuritemController) controller;

		setCloseAfterSave(false);

		initialize();
	}
	@Override
	public void setData(InfoBase info) {
		factuuritem = (FactuuritemInfo) info;
		factuurkopmodel = factuuritemcontroller.getComboModelKoppen();
		cmbFactuurkoppen.setModel(factuurkopmodel);

		displayFactuuritem();
	}

	private void initialize() {
		setBounds(0, 237, 527, 354);
		getContentPane().setLayout(null);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		dtpDatum = new DatePicker();
		dtpDatum.setBounds(98, 77, 97, 21);
		dtpDatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatum);

		JLabel lblIngangsdatum = new JLabel("Datum");
		lblIngangsdatum.setBounds(10, 77, 86, 14);
		getContentPane().add(lblIngangsdatum);

		cmbFactuurkoppen = new JComboBox<>();
		cmbFactuurkoppen.setBounds(98, 100, 238, 20);
		cmbFactuurkoppen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cmbFactuurkoppenClicked(e);

			}
		});
		getContentPane().add(cmbFactuurkoppen);

		JLabel lblActiviteit = new JLabel("Factuurgroep");
		lblActiviteit.setBounds(10, 100, 65, 14);
		getContentPane().add(lblActiviteit);

		cmbFactuurcategorien = new JComboBox<>();
		cmbFactuurcategorien.setBounds(98, 123, 238, 20);
		getContentPane().add(cmbFactuurcategorien);

		JLabel lblSoortHuisbezoek = new JLabel("Factuurcategorie");
		lblSoortHuisbezoek.setBounds(10, 123, 105, 14);
		getContentPane().add(lblSoortHuisbezoek);

		txtBedrag = new JFormattedTextField(amountFormat);
		txtBedrag.setBounds(98, 146, 76, 20);
		getContentPane().add(txtBedrag);
		txtBedrag.setColumns(10);

		JLabel lblBedrag = new JLabel("Bedrag");
		lblBedrag.setBounds(10, 146, 95, 14);
		getContentPane().add(lblBedrag);

		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(10, 166, 95, 14);
		getContentPane().add(lblOmschrijving);

		taOmschrijving = new JTextAreaTGI();
		taOmschrijving.setBounds(98, 169, 337, 117);
		taOmschrijving.setFont(cmbFactuurcategorien.getFont());
		getContentPane().add(taOmschrijving);
		
		addWerkgeverSelectionPanel();
	}
		
	private void addWerkgeverSelectionPanel() {
		werkgeverSelection = new WerkgeverSelection(
				__WerkgeverSelectionMode.SelectBoth, __WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(0, 11, 374, 56);
		werkgeverSelection.setMaincontroller(controller.getMaincontroller());
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				factuuritem.setWerkgeverid(werkgeverid);
				EnableDisableFields();
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				factuuritem.setHoldingid(holdingid);
				EnableDisableFields();
				return false;
			}
		});
		
	}

	protected void cmbFactuurkoppenClicked(ActionEvent e) {
		EnableDisableFields();
		factuurcategoriemodel = factuuritemcontroller.getComboModelCategorien(factuurkopmodel.getId());
		cmbFactuurcategorien.setModel(factuurcategoriemodel);
		factuurcategoriemodel.setId(factuuritem.getFactuurcategorieid());
	}

	private void EnableDisableFields() {
	}

	private void displayFactuuritem() {
		try {
			dtpDatum.setDate(factuuritem.getDatum());

			werkgeverSelection.setHoldingId(factuuritem.getHoldingid());
			werkgeverSelection.setWerkgeverId(factuuritem.getWerkgeverid());
			
			/*
			 * Factuuritem contains reference to Factuurcategorie
			 * Factuurcategorie contains reference to factuurkop
			 */
			
//			factuurcategoriemodel.setId(factuuritem.getFactuurcategorieid());
			Integer kopid = factuuritemcontroller.getFactuurkopforCategorie(factuuritem.getFactuurcategorieid());
			factuurkopmodel.setId(kopid);
//			factuurcategoriemodel = factuuritemcontroller.getComboModelCategorien(kopid);
//			cmbFactuurcategorien.setModel(factuurcategoriemodel);
			
			cmbFactuurkoppenClicked(null);

			taOmschrijving.setText(factuuritem.getOmschrijving());
			txtBedrag.setValue(factuuritem.getBedrag());

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

	@Override
	public InfoBase collectData() {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		if (factuuritem.getWerkgeverid() == -1) {
			JOptionPane.showMessageDialog(this, "Geen werkgever geselecteerd.");
			return null;
		}

		try {

			factuuritem.setDatum(dtpDatum.getDate());
			factuuritem.setBedrag(formatBigDecimal(format, txtBedrag.getText()));

			if (factuuritem.getHoldingid() == null){
				if (werkgeverSelection.getHolding() != null){
					factuuritem.setHoldingid(werkgeverSelection.getHolding().getId());
				}
			}
			if (factuuritem.getWerkgeverid() == null || factuuritem.getWerkgeverid().equals(-1)){
				throw new ValidationException("Geen werkgever geselecteerd");
			}
			TypeEntry categorie = (TypeEntry) cmbFactuurcategorien
					.getSelectedItem();
			if (categorie == null){
				throw new ValidationException("Geen categorie geselecteerd");
			}
			factuuritem.setFactuurcategorieid(categorie.getValue());
			factuuritem.setOmschrijving(taOmschrijving.getText());
			if (this.getFormmode() == __formmode.NEW){
				factuuritem.setUserid(factuuritemcontroller.getMaincontroller().getGebruiker().getId());
			}

		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return null;
		}
		return factuuritem;

	}
}
