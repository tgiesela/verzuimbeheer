package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
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
import java.awt.event.ActionEvent;

public class BtwDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BtwInfo btw;
	
	private DatePicker dtpIngangsdatum ;
	private DatePicker dtpEinddatum;
	private JComboBox<TypeEntry> cmbBtwSoort;
	private DefaultComboBoxModel<TypeEntry> btwModel = new DefaultComboBoxModel<TypeEntry>();
	private JFormattedTextField txtPercentage;

	/**
	 * Create the frame.
	 */
	public BtwDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer BTW percentages", mdiPanel);
		initialize();
	}
	private void initialize() {
		setBounds(0, 237, 439, 175);
		getContentPane().setLayout(null);
		
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
	public void setInfo(InfoBase info){
		try {
			btw = (BtwInfo)info;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
			
		try {
			if (this.getMode() != formMode.New){
				btw = ServiceCaller.factuurFacade(getLoginSession()).getBtwById(btw.getId());
				if (btw == null){
					JOptionPane.showMessageDialog(this, "Btw kan niet worden opgevraagd. \nDeze is mogelijk verwijderd");
					return;
				}
			}
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		cmbBtwSoort.setModel(btwModel);
		for (__btwtariefsoort w: __btwtariefsoort.values())
		{
			TypeEntry wg = new TypeEntry(w.getValue(), w.toString());
			btwModel.addElement(wg);
		}
		displayTarief();
		
		activateListener();
	}
	private void displayTarief() {
		TypeEntry btwtype;
		try {
			dtpIngangsdatum.setDate(btw.getIngangsdatum());
			dtpEinddatum.setDate(btw.getEinddatum());
			txtPercentage.setValue(btw.getPercentage());
			for (int i=0;i<btwModel.getSize();i++)
			{
				btwtype = (TypeEntry) btwModel.getElementAt(i);
				if (btw.getBtwtariefsoort().getValue().equals(btwtype.getValue())){
					btwModel.setSelectedItem(btwtype);
					break;
				}
			}
			
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
	protected void okButtonClicked(ActionEvent e) {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		TypeEntry selectedBtw = (TypeEntry)cmbBtwSoort.getSelectedItem();
		btw.setBtwtariefsoort(__btwtariefsoort.parse(selectedBtw.getValue()));
		
		btw.setEinddatum(dtpEinddatum.getDate());
		btw.setIngangsdatum(dtpIngangsdatum.getDate());
		try {
			btw.setPercentage(formatBigDecimal(format, txtPercentage.getText()));
			
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		if (this.getLoginSession() != null)
        {
        	try {
        		btw.validate();
        		switch (this.getMode())
        		{
        			case New: 		ServiceCaller.factuurFacade(getLoginSession()).addBtw(btw);
        							break;
        			case Update: 	ServiceCaller.factuurFacade(getLoginSession()).updateBtw(btw);
        							break;
        			case Delete: 	ServiceCaller.factuurFacade(getLoginSession()).deleteBtw(btw);
        							break;
        		}
		        super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
		
	}
}
