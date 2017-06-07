package com.gieselaar.verzuimbeheer.forms;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JComboBox;

public class AfdelingHasWerknemerDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AfdelingHasWerknemerInfo afdelinghaswerknemer;
	private List<AfdelingInfo> afdelingen =  new ArrayList<AfdelingInfo>();
	private DatePicker dtpIngangsdatum;
	private DatePicker dtpEinddatum;
	private JComboBox<?> cmbAfdeling;
	@SuppressWarnings("rawtypes")
	private DefaultComboBoxModel afdelingModel;

	/**
	 * Create the frame.
	 */
	public AfdelingHasWerknemerDetail(JDesktopPaneTGI mdiPanel){
		super("Afdeling bij Werknemer", mdiPanel);
		setNormalBounds(new Rectangle(100, 100, 320, 247));
		initialize();
	}
	@SuppressWarnings({ "unchecked" })
	public void setInfo(InfoBase info){
		TypeEntry afdelingtype;
		afdelinghaswerknemer = (AfdelingHasWerknemerInfo)info;
		
		try {
			try {
				Integer wgr;
				if (afdelinghaswerknemer.getAfdeling() == null)
					wgr = afdelinghaswerknemer.getWerkgeverId();
				else
					wgr = afdelinghaswerknemer.getAfdeling().getWerkgeverId();
				afdelingen = ServiceCaller.werkgeverFacade(getLoginSession()).getAfdelingenWerkgever(wgr); /* Info om combobox te vullen: afdeling van de werkgever */
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
			if (this.getMode() == formMode.New)
				cmbAfdeling.setEnabled(true);

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
		afdelingModel = new DefaultComboBoxModel<TypeEntry>();
		cmbAfdeling.setModel(afdelingModel);
		for (AfdelingInfo w: afdelingen)
		{
			TypeEntry afd = new TypeEntry(w.getId(), w.getNaam());
			afdelingModel.addElement(afd);
		}
		for (int i=0;i<afdelingModel.getSize();i++)
		{
			afdelingtype = (TypeEntry) afdelingModel.getElementAt(i);
			if (afdelingtype.getValue() == afdelinghaswerknemer.getAfdelingId())
			{
				afdelingModel.setSelectedItem(afdelingtype);
				break;
			}
		}
		activateListener();
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
		
		cmbAfdeling = new JComboBox<Object>();
		cmbAfdeling.setBounds(141, 19, 246, 20);
		getContentPane().add(cmbAfdeling);
		
	}
	protected void okButtonClicked(ActionEvent e) {
		afdelinghaswerknemer.setStartdatum(dtpIngangsdatum.getDate());
		afdelinghaswerknemer.setEinddatum(dtpEinddatum.getDate());
		TypeEntry selected = (TypeEntry) afdelingModel.getSelectedItem();
		for (AfdelingInfo w: afdelingen)
		{
			if (w.getId() == selected.getValue())
			{
				afdelinghaswerknemer.setAfdeling(w);
				afdelinghaswerknemer.setAfdelingId(w.getId());
				break;
			}
		}
		
		try {
			afdelinghaswerknemer.validate();
			super.okButtonClicked(e);
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this,false);
		}
	}

}
