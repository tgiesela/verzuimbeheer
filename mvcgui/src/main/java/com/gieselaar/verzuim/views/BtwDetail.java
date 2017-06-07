package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
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
import java.awt.event.ActionEvent;

public class BtwDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private BtwInfo btw;
	
	private DatePicker dtpIngangsdatum ;
	private DatePicker dtpEinddatum;
	private JComboBox<TypeEntry> cmbBtwSoort;
	private VerzuimComboBoxModel btwModel;
	private JFormattedTextField txtPercentage;
	/**
	 * Create the frame.
	 */
	public BtwDetail(AbstractController controller) {
		super("Beheer BTW percentages", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		btw = (BtwInfo)info;
		btwModel = controller.getMaincontroller().getEnumModel(__btwtariefsoort.class);
		cmbBtwSoort.setModel(btwModel);
		displayBtw();
		
	}
	private void initialize() {
		setBounds(50, 50, 439, 175);
		
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(1);
		amountFormat.setMinimumFractionDigits(1);

		dtpIngangsdatum = new DatePicker();
		dtpIngangsdatum.setBounds(123, 28, 97, 21);
		dtpIngangsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpIngangsdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(123, 51, 97, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setBounds(10, 31, 86, 14);
		getContentPane().add(lblIngangsdatum);
		
		JLabel lblEinddatum = new JLabel("Einddatum");
		lblEinddatum.setBounds(10, 54, 108, 14);
		getContentPane().add(lblEinddatum);
		
		cmbBtwSoort = new JComboBox<TypeEntry>();
		cmbBtwSoort.setBounds(122, 3, 239, 20);
		getContentPane().add(cmbBtwSoort);
		
		JLabel lblBtwSoort = new JLabel("BTW soort");
		lblBtwSoort.setBounds(10, 8, 65, 14);
		getContentPane().add(lblBtwSoort);
		
		JLabel lblPercentage = new JLabel("Percentage");
		lblPercentage.setBounds(10, 87, 95, 14);
		getContentPane().add(lblPercentage);
		
		txtPercentage = new JFormattedTextField(amountFormat);
		txtPercentage.setBounds(123, 83, 76, 20);
		getContentPane().add(txtPercentage);
		txtPercentage.setColumns(10);
		
	}
	protected void cmbWerkgeverClicked(ActionEvent e) {
	}
	private void displayBtw() {
		try {
			dtpIngangsdatum.setDate(btw.getIngangsdatum());
			dtpEinddatum.setDate(btw.getEinddatum());
			txtPercentage.setValue(btw.getPercentage());
			btwModel.setId(btw.getBtwtariefsoort().getValue());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e,this);
		}
	}
	private BigDecimal formatBigDecimal(DecimalFormat format, String value) throws ValidationException{
		BigDecimal bedrag;
		ParsePosition pos = new ParsePosition(0);		
		bedrag = (BigDecimal)format.parse(value,pos);
		if (pos.getIndex() < value.length()) {
			throw new ValidationException("Ongeldige tekens in bedrag");
		}
		return bedrag;
	}
	@Override
	public InfoBase collectData() {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		Integer selectedBtwid = btwModel.getId();
		btw.setBtwtariefsoort(__btwtariefsoort.parse(selectedBtwid));
		
		btw.setEinddatum(dtpEinddatum.getDate());
		btw.setIngangsdatum(dtpIngangsdatum.getDate());
		try {
			btw.setPercentage(formatBigDecimal(format, txtPercentage.getText()));
			
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return null;
		}
		return btw;
	}
}
