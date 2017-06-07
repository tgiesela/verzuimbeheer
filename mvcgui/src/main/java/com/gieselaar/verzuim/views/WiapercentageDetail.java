package com.gieselaar.verzuim.views;

import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

public class WiapercentageDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private WiapercentageInfo wiapercentage;
	private DatePicker dtpIngangsdatum;
	private DatePicker dtpEinddatum;
	private JComboBox<TypeEntry> cmbWiapercentage;

	public WiapercentageDetail(AbstractController controller) {
		super("Wiapercentage", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		wiapercentage = (WiapercentageInfo) info;
		displayWiapercentage();
	}
	private void displayWiapercentage(){
		VerzuimComboBoxModel wiapercentageModel = controller.getMaincontroller().getEnumModel(__wiapercentage.class);
        cmbWiapercentage.setModel(wiapercentageModel);
        if (this.getFormmode() == __formmode.NEW){
        	wiapercentageModel.setId(__wiapercentage.NVT.getValue());
        }else{
        	wiapercentageModel.setId(wiapercentage.getCodeWiaPercentage().getValue());
        }
    	try {
			dtpIngangsdatum.setDate(wiapercentage.getStartdatum());
			dtpEinddatum.setDate(wiapercentage.getEinddatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
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
		
		cmbWiapercentage = new JComboBox<>();
		cmbWiapercentage.setBounds(141, 19, 246, 20);
		getContentPane().add(cmbWiapercentage);
	}
	/*
	@Override
	protected void okButtonClicked() {
		try {
			if (this.getFormmode() == __formmode.NEW){
				wiapercentagecontroller.addData(wiapercentage);
			}else{
				wiapercentagecontroller.saveData(wiapercentage);
			}
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		wiapercentagecontroller.closeView(this);
	}
	*/
	@Override
	public InfoBase collectData() {
		TypeEntry codeWiapercentage;
		wiapercentage.setStartdatum(dtpIngangsdatum.getDate());
		wiapercentage.setEinddatum(dtpEinddatum.getDate());
		codeWiapercentage = (TypeEntry) cmbWiapercentage.getSelectedItem();
		wiapercentage.setCodeWiaPercentage(__wiapercentage.parse(codeWiapercentage.getValue()));
		return wiapercentage;
	}

}
