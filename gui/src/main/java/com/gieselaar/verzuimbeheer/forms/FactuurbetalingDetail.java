package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;

public class FactuurbetalingDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FactuurbetalingInfo factuurbetaling;

	private DatePicker dtpDatum;
	private JFormattedTextField txtBedrag;
	private JTextFieldTGI txtRekeningnummerBetaler;
	private BaseDetailform thisform = this;

	/**
	 * Create the frame.
	 */
	public FactuurbetalingDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer factuur betalingen", mdiPanel);
		initialize();
	}

	private void initialize() {
		setBounds(0, 237, 527, 215);
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

	public void setInfo(InfoBase info) {
		factuurbetaling = (FactuurbetalingInfo) info;

		displayfactuurbetaling();
		activateListener();
	}

	private void EnableDisableFields() {
	}

	private void displayfactuurbetaling() {
		try {
			dtpDatum.setDate(factuurbetaling.getDatum());
			txtBedrag.setValue(factuurbetaling.getBedrag());
			txtRekeningnummerBetaler.setText(factuurbetaling.getRekeningnummerbetaler());
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

		try {

			factuurbetaling.setDatum(dtpDatum.getDate());
			factuurbetaling.setBedrag(formatBigDecimal(format, txtBedrag.getText()));
			factuurbetaling.setRekeningnummerbetaler(txtRekeningnummerBetaler.getText());

		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		if (this.getLoginSession() != null) {
			try {
				factuurbetaling.validate();
				switch (this.getMode()) {
				case New:
					ServiceCaller.factuurFacade(getLoginSession())
							.addFactuurbetaling(factuurbetaling);
					break;
				case Update:
					ServiceCaller.factuurFacade(getLoginSession())
							.updateFactuurbetaling(factuurbetaling);
					break;
				case Delete:
					ServiceCaller.factuurFacade(getLoginSession())
							.deleteFactuurbetaling(factuurbetaling);
					break;
				}
				super.okButtonClicked(e);
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
