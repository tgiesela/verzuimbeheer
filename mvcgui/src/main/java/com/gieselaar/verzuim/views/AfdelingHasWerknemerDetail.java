package com.gieselaar.verzuim.views;

import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.controllers.AfdelingHasWerknemerController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JComboBox;

public class AfdelingHasWerknemerDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private AfdelingHasWerknemerInfo afdelinghaswerknemer;
	private DatePicker dtpIngangsdatum;
	private DatePicker dtpEinddatum;
	private JComboBox<TypeEntry> cmbAfdeling;
	private VerzuimComboBoxModel afdelingModel;
	private AfdelingHasWerknemerController afdelingcontroller; 
	/**
	 * Create the frame.
	 */
	public AfdelingHasWerknemerDetail(AbstractController controller){
		super("Afdeling bij Werknemer", controller);
		afdelingcontroller = (AfdelingHasWerknemerController)controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info) {
		afdelinghaswerknemer = (AfdelingHasWerknemerInfo) info;
		displayAfdeling();
	}
	
	public void displayAfdeling(){
		try {
			if (this.getFormmode() == __formmode.NEW){
				cmbAfdeling.setEnabled(true);
			}
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		try {
			dtpIngangsdatum.setDate(afdelinghaswerknemer.getStartdatum());
			dtpEinddatum.setDate(afdelinghaswerknemer.getEinddatum());
		} catch (PropertyVetoException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		}
		cmbAfdeling.setModel(afdelingcontroller.getComboModelWerkgeverAfdelingen());
		afdelingModel = (VerzuimComboBoxModel) cmbAfdeling.getModel();
		afdelingModel.setId(afdelinghaswerknemer.getAfdelingId());
	}
	void initialize(){
		setBounds(100, 100, 412, 197);
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
		
		JLabel lblAfdeling = new JLabel("Afdelingsnaam");
		lblAfdeling.setBounds(10, 22, 89, 14);
		getContentPane().add(lblAfdeling);
		
		cmbAfdeling = new JComboBox<TypeEntry>();
		cmbAfdeling.setBounds(141, 19, 246, 20);
		getContentPane().add(cmbAfdeling);
		
	}
/*	
	protected void okButtonClicked() {
		TypeEntry selected = (TypeEntry) afdelingModel.getSelectedItem();
		try {
			if (this.getFormmode() == __formmode.NEW){
				afdelingcontroller.addData(afdelinghaswerknemer);
			}else{
				afdelingcontroller.updateData(afdelinghaswerknemer);
			}
		}catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
        controller.closeView(this);
	}
*/	
	@Override
	public InfoBase collectData() {
		afdelinghaswerknemer.setStartdatum(dtpIngangsdatum.getDate());
		afdelinghaswerknemer.setEinddatum(dtpEinddatum.getDate());
		afdelinghaswerknemer.setAfdelingId(((VerzuimComboBoxModel)cmbAfdeling.getModel()).getId());
		return afdelinghaswerknemer;
	}
}
