package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;

import javax.swing.JLabel;

import com.michaelbaranov.microba.calendar.DatePicker;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;

import java.awt.Rectangle;

public class DienstverbandDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7152023081735978278L;

	/**
	 * Create the dialog.
	 */
	private DienstverbandInfo dienstverband = null;
	private DatePicker dtpIngangsdatum;
	private JTextFieldTGI txtPersoneelsnummer;
	private JTextFieldTGI txtFunctie;
	private JTextFieldTGI txtWerkweekUren;
	private DatePicker dtpEindatum;
	public void setInfo(InfoBase info){
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");
		
		dienstverband = (DienstverbandInfo)info;
		try {
			dtpIngangsdatum.setDate(dienstverband.getStartdatumcontract());
			dtpEindatum.setDate(dienstverband.getEinddatumcontract());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}
		txtFunctie.setText(dienstverband.getFunctie());
		txtPersoneelsnummer.setText(dienstverband.getPersoneelsnummer());
		if (dienstverband.getWerkweek() == null)
			txtWerkweekUren.setText(df.format(40.00));
		else
			txtWerkweekUren.setText(df.format(dienstverband.getWerkweek()));
		activateListener();
	}
	public DienstverbandDetail(JDesktopPaneTGI mdiPanel){
		super("Dienstverband", mdiPanel);
		setNormalBounds(new Rectangle(100, 100, 320, 247));
		initialize();
	}
	void initialize(){

		setBounds(100, 100, 320, 197);
		getContentPane().setLayout(null);
		
		dtpIngangsdatum = new DatePicker();
		dtpIngangsdatum.setBounds(141, 11, 89, 21);
		dtpIngangsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpIngangsdatum);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setLabelFor(dtpIngangsdatum);
		lblIngangsdatum.setBounds(10, 14, 89, 14);
		getContentPane().add(lblIngangsdatum);
		
		txtPersoneelsnummer = new JTextFieldTGI();
		txtPersoneelsnummer.setBounds(141, 38, 125, 20);
		getContentPane().add(txtPersoneelsnummer);
		
		JLabel lblPersoneelsnummer = new JLabel("Personeelsnummer");
		lblPersoneelsnummer.setLabelFor(txtPersoneelsnummer);
		lblPersoneelsnummer.setBounds(10, 41, 104, 14);
		getContentPane().add(lblPersoneelsnummer);
		
		txtFunctie = new JTextFieldTGI();
		txtFunctie.setBounds(141, 62, 125, 20);
		getContentPane().add(txtFunctie);
		
		JLabel lblFunctie = new JLabel("Functie");
		lblFunctie.setLabelFor(txtFunctie);
		lblFunctie.setBounds(10, 65, 46, 14);
		getContentPane().add(lblFunctie);
		
		txtWerkweekUren = new JTextFieldTGI();
		txtWerkweekUren.setBounds(141, 85, 104, 20);
		getContentPane().add(txtWerkweekUren);
		
		JLabel lblWerkweek = new JLabel("Werkweek");
		lblWerkweek.setLabelFor(txtWerkweekUren);
		lblWerkweek.setBounds(10, 88, 65, 14);
		getContentPane().add(lblWerkweek);
		
		dtpEindatum = new DatePicker();
		dtpEindatum.setBounds(141, 108, 89, 21);
		dtpEindatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEindatum);
		
		JLabel lblEinddatum = new JLabel("Einddatum");
		lblEinddatum.setLabelFor(dtpEindatum);
		lblEinddatum.setBounds(10, 113, 65, 14);
		getContentPane().add(lblEinddatum);

	}
	protected void okButtonClicked(ActionEvent e) {
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal uren;
		format.setParseBigDecimal(true);
		

		dienstverband.setStartdatumcontract(dtpIngangsdatum.getDate());
		dienstverband.setEinddatumcontract(dtpEindatum.getDate());
		dienstverband.setFunctie(txtFunctie.getText());
		uren = (BigDecimal)format.parse(txtWerkweekUren.getText(),pos);
		if (pos.getIndex() < txtWerkweekUren.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Werkweek bevat ongeldige tekens");
			return;
		}
			
		dienstverband.setWerkweek(uren);
		dienstverband.setPersoneelsnummer(txtPersoneelsnummer.getText());
		try {
			dienstverband.validate();
			super.okButtonClicked(e);
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this,false);
		}
	}
}
