package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

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
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.Font;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TodoDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TodoInfo todo;
	private List<ActiviteitInfo> activiteiten;
	private List<GebruikerInfo> gebruikers;
	
	private DatePicker dtpDeadlinedatum ;
	private DatePicker dtpWaarschuwingsdatum;
	private DatePicker dtpAangemaakt;
	private JTextFieldTGI txtUser;
	private JCheckBox chckbxHerhalen;
	private JComboBox<TypeEntry> cmbActiviteit;
	private JComboBox<TypeEntry> cmbSoort;
	private JTextAreaTGI taOpmerking;
	private JCheckBox chckbxAfgerond;
	private DatePicker dtpAfgerond;
	private JTextFieldTGI txtUserAanmaak;
	/**
	 * Create the frame.
	 */
	public TodoDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Todo", mdiPanel);
		initialize();
	}
	private void initialize() {
		setBounds(0, 237, 416, 373);
		getContentPane().setLayout(null);
		
		dtpDeadlinedatum = new DatePicker();
		dtpDeadlinedatum.setBounds(123, 59, 97, 21);
		dtpDeadlinedatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDeadlinedatum);
		
		dtpWaarschuwingsdatum = new DatePicker();
		dtpWaarschuwingsdatum.setBounds(123, 82, 97, 21);
		dtpWaarschuwingsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpWaarschuwingsdatum);
		
		JLabel lblDatumDeadline = new JLabel("Datum deadline");
		lblDatumDeadline.setBounds(10, 62, 86, 14);
		getContentPane().add(lblDatumDeadline);
		
		JLabel lblWaarschuwingsdatum = new JLabel("Waarschuwingsdatum");
		lblWaarschuwingsdatum.setBounds(10, 85, 108, 14);
		getContentPane().add(lblWaarschuwingsdatum);
		
		chckbxHerhalen = new JCheckBox("Herhalen");
		chckbxHerhalen.setBounds(6, 105, 97, 23);
		getContentPane().add(chckbxHerhalen);
		
		cmbActiviteit = new JComboBox<TypeEntry>();
		cmbActiviteit.setBounds(123, 36, 175, 20);
		getContentPane().add(cmbActiviteit);
		
		JLabel lblActiviteit = new JLabel("Activiteit");
		lblActiviteit.setBounds(10, 39, 46, 14);
		getContentPane().add(lblActiviteit);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(124, 128, 238, 107);
		getContentPane().add(scrollPane);
		
		taOpmerking = new JTextAreaTGI();
		taOpmerking.setLocation(0, 159);
		scrollPane.setViewportView(taOpmerking);
		taOpmerking.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblOpmerking = new JLabel("Opmerking");
		lblOpmerking.setBounds(10, 128, 64, 14);
		getContentPane().add(lblOpmerking);
		
		chckbxAfgerond = new JCheckBox("Afgerond op");
		chckbxAfgerond.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxAfgerondClicked(e);
			}
		});
		chckbxAfgerond.setBounds(6, 271, 90, 23);
		getContentPane().add(chckbxAfgerond);
		
		dtpAfgerond = new DatePicker();
		dtpAfgerond.setEnabled(false);
		dtpAfgerond.setBounds(123, 271, 86, 21);
		dtpAfgerond.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpAfgerond);
		
		JLabel lblDoor = new JLabel("door");
		lblDoor.setBounds(213, 275, 30, 14);
		getContentPane().add(lblDoor);
		
		txtUser = new JTextFieldTGI();
		txtUser.setEditable(false);
		txtUser.setBounds(240, 272, 86, 20);
		getContentPane().add(txtUser);
		txtUser.setColumns(10);
		
		cmbSoort = new JComboBox<TypeEntry>();
		cmbSoort.setEnabled(false);
		cmbSoort.setBounds(123, 11, 175, 20);
		getContentPane().add(cmbSoort);
		
		JLabel lblSoort = new JLabel("Soort");
		lblSoort.setBounds(10, 14, 46, 14);
		getContentPane().add(lblSoort);
		
		dtpAangemaakt = new DatePicker();
		dtpAangemaakt.setEnabled(false);
		dtpAangemaakt.setBounds(123, 246, 86, 21);
		dtpAangemaakt.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpAangemaakt);
		
		JLabel label = new JLabel("door");
		label.setBounds(213, 250, 30, 14);
		getContentPane().add(label);
		
		txtUserAanmaak = new JTextFieldTGI();
		txtUserAanmaak.setEditable(false);
		txtUserAanmaak.setColumns(10);
		txtUserAanmaak.setBounds(240, 247, 86, 20);
		getContentPane().add(txtUserAanmaak);
		
		JLabel lblAangemaaktDoor = new JLabel("Aangemaakt op:");
		lblAangemaaktDoor.setBounds(28, 250, 90, 14);
		getContentPane().add(lblAangemaaktDoor);
		
	}
	protected void chckbxAfgerondClicked(ActionEvent e) {
		VerzuimActiviteitInfo vai = new VerzuimActiviteitInfo();
		vai.setDatumactiviteit(new Date());
		vai.setDatumdeadline(todo.getDeadlinedatum());
		vai.setVerzuimId(todo.getVerzuimId());
		vai.setActiviteitId(todo.getActiviteitId());
		vai.setUser(this.getLoginSession().getGebruiker().getId());
		todo.setVerzuimActiviteit(vai);
		try {
			dtpAfgerond.setDate(vai.getDatumactiviteit());
			txtUser.setText(this.getLoginSession().getGebruiker().getAchternaam());
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			ExceptionLogger.ProcessException(e1,this);
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInfo(InfoBase info){
		DefaultComboBoxModel<TypeEntry> SoortModel;
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
		if (info instanceof TodoInfo)
			todo = (TodoInfo)info;
		else{
			todo = new TodoInfo();
			todo.setId(((TodoFastInfo)info).getId());
		}
			
		if (this.getMode() == formMode.New){
			todo.setUser(this.getLoginSession().getGebruiker().getId());
			todo.setAchternaam(this.getLoginSession().getGebruiker().getAchternaam());
			todo.setDeadlinedatum(new Date());
			todo.setHerhalen(false);
			todo.setSoort(__soort.HANDMATIG);
			todo.setVerzuimId(-1);
			todo.setWaarschuwingsdatum(new Date());
			todo.setAanmaakdatum(new Date());
			cmbActiviteit.setEnabled(true);
		}
		else{
			try {
				todo = ServiceCaller.verzuimFacade(getLoginSession()).getTodo(todo.getId());
				if (todo == null){
					JOptionPane.showMessageDialog(this, "Todo kan niet worden opgevraagd. \nDeze is mogelijk verwijderd");
					return;
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
		}
		SoortModel = new DefaultComboBoxModel<TypeEntry>();
        cmbSoort.setModel(SoortModel);
        for (__soort s: __soort.values())
        {
        	TypeEntry soort = new TypeEntry(s.getValue(), s.toString());
        	SoortModel.addElement(soort);
        	if (soort.getValue() == todo.getSoort().getValue()){
				SoortModel.setSelectedItem(soort);
        	}
        }
		try {
			dtpDeadlinedatum.setDate(todo.getDeadlinedatum());
			dtpWaarschuwingsdatum.setDate(todo.getWaarschuwingsdatum());
			dtpAangemaakt.setDate(todo.getAanmaakdatum());
			if (todo.getVerzuimactiviteitId() != null){
				chckbxAfgerond.setSelected(true);
				chckbxAfgerond.setEnabled(false);
				dtpAfgerond.setDate(todo.getVerzuimActiviteit().getDatumactiviteit());
				for (GebruikerInfo gi: gebruikers){
					if (gi.getId() == todo.getVerzuimActiviteit().getUser()){
						txtUser.setText(gi.getAchternaam());
						break;
					}
				}
			}
			else
			{
				chckbxAfgerond.setSelected(false);
				dtpAfgerond.setDate(null);
				chckbxAfgerond.setEnabled(true);
			}
			for (GebruikerInfo gi: gebruikers){
				if (gi.getId() == todo.getUser()){
					txtUserAanmaak.setText(gi.getAchternaam());
				}
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e,this);
		}
		chckbxHerhalen.setSelected(todo.getHerhalen());
		taOpmerking.setText(todo.getOpmerking());

		DefaultComboBoxModel activiteitmodel = new DefaultComboBoxModel();

        cmbActiviteit.setModel(activiteitmodel);
		TypeEntry activiteit = new TypeEntry(-1,"[]");
		activiteitmodel.addElement(activiteit);
		ActiviteitInfo.sort(activiteiten, ActiviteitInfo.__sortcol.NAAM);

		for (ActiviteitInfo act: activiteiten)
		{
			activiteit = new TypeEntry(act.getId(), act.getNaam());
			activiteitmodel.addElement(activiteit);
			if (this.getMode() != formMode.New)
				if (activiteit.getValue() == todo.getActiviteitId())
					activiteitmodel.setSelectedItem(activiteit);
		}
		activateListener();
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry activiteit;
		activiteit = (TypeEntry)cmbActiviteit.getSelectedItem();
		if (this.getMode() == formMode.New || todo.getSoort()==__soort.HANDMATIG)
		{
			todo.setVerzuimId(todo.getVerzuim().getId());
			todo.setUser(this.getLoginSession().getGebruiker().getId());
			todo.setAanmaakdatum(dtpAangemaakt.getDate());
		}
		todo.setWaarschuwingsdatum(dtpWaarschuwingsdatum.getDate());
		todo.setDeadlinedatum(dtpDeadlinedatum.getDate());
		todo.setActiviteitId(activiteit.getValue());
		todo.setHerhalen(chckbxHerhalen.isSelected());
		todo.setOpmerking(taOpmerking.getText());
		todo.setSoort(__soort.parse(((TypeEntry)cmbSoort.getSelectedItem()).getValue()));
		if (this.getLoginSession() != null)
        {
        	try {
        		todo.validate();
        		switch (this.getMode())
        		{
        			case New: 		ServiceCaller.verzuimFacade(getLoginSession()).addTodo(todo);
        							break;
        			case Update: 	ServiceCaller.verzuimFacade(getLoginSession()).updateTodo(todo);
        							break;
        			case Delete: 	ServiceCaller.verzuimFacade(getLoginSession()).deleteTodo(todo);
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
