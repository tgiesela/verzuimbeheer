package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class FactuurbetalingDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private FactuurbetalingInfo factuurbetaling;

	private DatePicker dtpDatum;
	private JFormattedTextField txtBedrag;
	private JTextFieldTGI txtRekeningnummerBetaler;
	/**
	 * Create the frame.
	 */
	public FactuurbetalingDetail(AbstractController controller) {
		super("Beheer factuur betalingen", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info) {
		factuurbetaling = (FactuurbetalingInfo) info;
		displayfactuurbetaling();
	}

	private void initialize() {
		setBounds(50, 50, 527, 215);
		getContentPane().setLayout(null);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		dtpDatum = new DatePicker();
		dtpDatum.setBounds(98, 43, 97, 21);
		dtpDatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatum);

		JLabel lblIngangsdatum = new JLabel("Datum");
		lblIngangsdatum.setBounds(10, 46, 86, 14);
		getContentPane().add(lblIngangsdatum);

		txtBedrag = new JFormattedTextField(amountFormat);
		txtBedrag.setBounds(98, 90, 76, 20);
		getContentPane().add(txtBedrag);
		txtBedrag.setColumns(10);

		JLabel lblBedrag = new JLabel("Bedrag");
		lblBedrag.setBounds(10, 93, 86, 14);
		getContentPane().add(lblBedrag);
		
		JLabel lblReknrBetaler = new JLabel("Reknr. betaler");
		lblReknrBetaler.setBounds(10, 70, 86, 14);
		getContentPane().add(lblReknrBetaler);
		
		txtRekeningnummerBetaler = new JTextFieldTGI();
		txtRekeningnummerBetaler.setColumns(10);
		txtRekeningnummerBetaler.setBounds(98, 67, 238, 20);
		getContentPane().add(txtRekeningnummerBetaler);
	}

	private void displayfactuurbetaling() {
		try {
			dtpDatum.setDate(factuurbetaling.getDatum());
			txtBedrag.setValue(factuurbetaling.getBedrag());
			txtRekeningnummerBetaler.setText(factuurbetaling.getRekeningnummerbetaler());
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

		try {

			factuurbetaling.setDatum(dtpDatum.getDate());
			factuurbetaling.setBedrag(formatBigDecimal(format, txtBedrag.getText()));
			factuurbetaling.setRekeningnummerbetaler(txtRekeningnummerBetaler.getText());

		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return null;
		}
		return factuurbetaling;
	}
}
