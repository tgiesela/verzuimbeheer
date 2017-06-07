package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

public class WiapercentageDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WiapercentageInfo wiapercentage;
	private DatePicker dtpIngangsdatum;
	private DatePicker dtpEinddatum;
	private JComboBox<TypeEntry> cmbWiapercentage;

	public WiapercentageDetail(JDesktopPaneTGI mdiPanel) {
		super("Wiapercentage", mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){
		TypeEntry Wiapercentage;
		DefaultComboBoxModel<TypeEntry> WiapercentageModel;
		wiapercentage = (WiapercentageInfo) info;

		WiapercentageModel = new DefaultComboBoxModel<TypeEntry>();
        cmbWiapercentage.setModel(WiapercentageModel);
        for (__wiapercentage g: __wiapercentage.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	WiapercentageModel.addElement(soort);
        }

        if (this.getMode() == formMode.New){
        	cmbWiapercentage.setSelectedIndex(0);
        }
        else
        {
			for (int i=0;i<WiapercentageModel.getSize();i++)
			{
				Wiapercentage = (TypeEntry) WiapercentageModel.getElementAt(i);
				if (__wiapercentage.parse(Wiapercentage.getValue()) == wiapercentage.getCodeWiaPercentage())
				{
					WiapercentageModel.setSelectedItem(Wiapercentage);
					break;
				}
			}
        }
    	try {
			dtpIngangsdatum.setDate(wiapercentage.getStartdatum());
			dtpEinddatum.setDate(wiapercentage.getEinddatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
        activateListener();
	}
	void initialize(){
		setBounds(100, 100, 450, 171);
		getContentPane().setLayout(null);
		
		dtpIngangsdatum = new DatePicker();
		dtpIngangsdatum.setBounds(141, 52, 89, 21);
		dtpIngangsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpIngangsdatum);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setLabelFor(dtpIngangsdatum);
		lblIngangsdatum.setBounds(10, 55, 89, 14);

		getContentPane().add(lblIngangsdatum);
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(141, 78, 89, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblEinddatum = new JLabel("Einddatum");
		lblEinddatum.setLabelFor(dtpEinddatum);
		lblEinddatum.setBounds(10, 83, 65, 14);
		getContentPane().add(lblEinddatum);
		
		JLabel lblAfdeling = new JLabel("Wiapercentage");
		lblAfdeling.setBounds(10, 22, 89, 14);
		getContentPane().add(lblAfdeling);
		
		cmbWiapercentage = new JComboBox<TypeEntry>();
		cmbWiapercentage.setBounds(141, 19, 246, 20);
		getContentPane().add(cmbWiapercentage);
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry codeWiapercentage;
		wiapercentage.setStartdatum(dtpIngangsdatum.getDate());
		wiapercentage.setEinddatum(dtpEinddatum.getDate());
		codeWiapercentage = (TypeEntry) cmbWiapercentage.getSelectedItem();
		wiapercentage.setCodeWiaPercentage(__wiapercentage.parse(codeWiapercentage.getValue()));
		try {
			wiapercentage.validate();
			super.okButtonClicked(e);
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this,false);
		}
	}

}
