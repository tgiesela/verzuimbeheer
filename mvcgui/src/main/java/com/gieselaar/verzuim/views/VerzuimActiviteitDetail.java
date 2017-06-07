package com.gieselaar.verzuim.views;

import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JLabel;
import javax.swing.JComboBox;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JScrollPane;

public class VerzuimActiviteitDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtGebruiker;
	private JComboBox<TypeEntry> cmbActiviteit;
	private DatePicker dtpDatumactiviteit;
	private DatePicker dtpDatumDeadline;
	private JTextAreaTGI taOpmerkingen;
	private VerzuimActiviteitInfo activiteit;

	private List<GebruikerInfo> gebruikers;
	
	public VerzuimActiviteitDetail(AbstractController controller) {
		super("Beheer verzuim verrichtingen", controller);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		activiteit = (VerzuimActiviteitInfo)info;
		displayActiviteit();
	}
	private void displayActiviteit(){
		controller.getMaincontroller().getActiviteiten();
		gebruikers = controller.getMaincontroller().getGebruikers();

		refreshActiviteiten();
		if (this.getFormmode() == __formmode.NEW){
			activiteit.setUser(controller.getGebruiker().getId());
		}
		VerzuimComboBoxModel activiteitmodel = (VerzuimComboBoxModel) cmbActiviteit.getModel();
		activiteitmodel.setId(activiteit.getId());

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
			ExceptionLogger.ProcessException(e,this);
		}
	}
	
	void initialize() {
		setBounds(100, 100, 450, 300);
		JLabel lblUitvoeringsdatum = new JLabel("Datum uitgevoerd");
		lblUitvoeringsdatum.setBounds(10, 37, 101, 14);
		getContentPane().add(lblUitvoeringsdatum);
		
		cmbActiviteit = new JComboBox<>();
		cmbActiviteit.setBounds(103, 11, 214, 20);

		VerzuimComboBoxModel activiteitmodel = new VerzuimComboBoxModel(controller.getMaincontroller());
		cmbActiviteit.setModel(activiteitmodel);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshActiviteiten();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener);
		
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
		
		JLabel lblDeadline = new JLabel("Datum deadline");
		lblDeadline.setBounds(10, 62, 83, 14);
		getContentPane().add(lblDeadline);
	}
	protected void refreshActiviteiten() {
		controller.getMaincontroller().updateComboModelActiviteiten((VerzuimComboBoxModel) cmbActiviteit.getModel());
	}
/*
	@Override
	protected void okButtonClicked() {
		Integer act;
		if (this.getFormmode() == __formmode.NEW)
		{
		}
       	try {
       		if (this.getFormmode() == __formmode.NEW){
       			verzuimactiviteitcontroller.addData(activiteit);
       		}else{
       			verzuimactiviteitcontroller.updateData(activiteit);
       		}
    		controller.closeView(this);
    	}catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
*/	
	@Override
	public InfoBase collectData() {
		VerzuimComboBoxModel actmodel = (VerzuimComboBoxModel) cmbActiviteit.getModel();
		activiteit.setActiviteitId(actmodel.getId());
		activiteit.setDatumdeadline(dtpDatumDeadline.getDate());
		activiteit.setDatumactiviteit(dtpDatumactiviteit.getDate());
		return activiteit;
	}
}
