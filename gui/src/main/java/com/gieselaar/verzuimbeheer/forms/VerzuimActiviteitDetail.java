package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JLabel;
import javax.swing.JComboBox;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class VerzuimActiviteitDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtGebruiker;
	private JComboBox<TypeEntry> cmbActiviteit;
	private DatePicker dtpDatumactiviteit;
	private DatePicker dtpDatumDeadline;
	private JTextAreaTGI taOpmerkingen;
	private VerzuimActiviteitInfo activiteit;

	private List<ActiviteitInfo> activiteiten;
	private List<GebruikerInfo> gebruikers;
	
	public VerzuimActiviteitDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer verzuim verrichtingen", mdiPanel);
		initialize();
	}
	void initialize() {
		setBounds(100, 100, 450, 300);
		JLabel lblUitvoeringsdatum = new JLabel("Datum uitgevoerd");
		lblUitvoeringsdatum.setBounds(10, 37, 101, 14);
		getContentPane().add(lblUitvoeringsdatum);
		
		cmbActiviteit = new JComboBox<TypeEntry>();
		cmbActiviteit.setBounds(103, 11, 214, 20);
		getContentPane().add(cmbActiviteit);
		
		JLabel lblActiviteit = new JLabel("Activiteit");
		lblActiviteit.setBounds(10, 14, 46, 14);
		getContentPane().add(lblActiviteit);
		
		dtpDatumactiviteit = new DatePicker();
		dtpDatumactiviteit.setBounds(103, 34, 89, 21);
		dtpDatumactiviteit.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatumactiviteit);
		
		dtpDatumDeadline = new DatePicker();
		dtpDatumDeadline.setBounds(103, 57, 89, 21);
		dtpDatumDeadline.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatumDeadline);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(103, 103, 214, 113);
		getContentPane().add(scrollPane);
		
		taOpmerkingen = new JTextAreaTGI();
		scrollPane.setViewportView(taOpmerkingen);
		
		JLabel lblOpmerkingen = new JLabel("Opmerkingen");
		lblOpmerkingen.setBounds(10, 103, 71, 14);
		getContentPane().add(lblOpmerkingen);
		
		JLabel lblNewLabel = new JLabel("Uitgevoerd door");
		lblNewLabel.setBounds(10, 83, 83, 14);
		getContentPane().add(lblNewLabel);
		
		txtGebruiker = new JTextFieldTGI();
		txtGebruiker.setEnabled(false);
		txtGebruiker.setEditable(false);
		txtGebruiker.setBounds(103, 80, 214, 20);
		getContentPane().add(txtGebruiker);
		txtGebruiker.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Datum deadline");
		lblNewLabel_1.setBounds(10, 62, 83, 14);
		getContentPane().add(lblNewLabel_1);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInfo(InfoBase info){
		try {
			activiteiten = ServiceCaller.verzuimFacade(getLoginSession()).getActiviteiten();
			gebruikers = ServiceCaller.verzuimFacade(getLoginSession()).getGebruikers();
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
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
		activiteit = (VerzuimActiviteitInfo)info;
		DefaultComboBoxModel activiteitmodel = new DefaultComboBoxModel();
		if (this.getMode() == formMode.New){
			activiteit.setUser(this.getLoginSession().getGebruiker().getId());
		}
		
        cmbActiviteit.setModel(activiteitmodel);
		for (ActiviteitInfo act: activiteiten)
		{
			TypeEntry activiteit = new TypeEntry(act.getId(), act.getNaam());
			activiteitmodel.addElement(activiteit);
			if (activiteit.getValue() == act.getId())
				activiteitmodel.setSelectedItem(activiteit);
		}
		try {
			dtpDatumDeadline.setDate(activiteit.getDatumdeadline());
			dtpDatumactiviteit.setDate(activiteit.getDatumactiviteit());
			for (GebruikerInfo gi: gebruikers){
				if (gi.getId().equals(activiteit.getUser())){
					txtGebruiker.setText(gi.getAchternaam());
					break;
				}
			}
			taOpmerkingen.setText(activiteit.getOpmerkingen());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e,this);
		}

		activateListener();
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry act;
		if (this.getMode() == formMode.New)
		{
			act = (TypeEntry)cmbActiviteit.getSelectedItem();
			activiteit.setActiviteitId(act.getValue());
			activiteit.setDatumdeadline(dtpDatumDeadline.getDate());
			activiteit.setDatumactiviteit(dtpDatumactiviteit.getDate());
		}
		if (this.getLoginSession() != null)
        {
        	try {
        		activiteit.validate();
        		switch (this.getMode())
        		{
        			case New: 		ServiceCaller.verzuimFacade(getLoginSession()).addVerzuimActiviteit(activiteit);
        							break;
        			case Update: 	ServiceCaller.verzuimFacade(getLoginSession()).updateVerzuimActiviteit(activiteit);
        							break;
        			case Delete: 	ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuimActiviteit(activiteit);
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
