package com.gieselaar.verzuim.views;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuim.controllers.TodoController;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JComboBox;

public class TodoDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private TodoInfo todo;
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

	private TodoController todocontroller; 
	/**
	 * Create the frame.
	 */
	public TodoDetail(AbstractController controller){
		super("Todo", controller);
		todocontroller = (TodoController)controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info) {
		todo = (TodoInfo) info;
		displayTodo();
	}
	
	public void displayTodo(){
		VerzuimComboBoxModel soortmodel;
		VerzuimComboBoxModel activiteitmodel;
		List<GebruikerInfo> gebruikers;
		gebruikers = todocontroller.getMaincontroller().getGebruikers();
		if (this.getFormmode() == __formmode.NEW){
			cmbActiviteit.setEnabled(true);
		}
		soortmodel = controller.getMaincontroller().getEnumModel(__soort.class);
		cmbSoort.setModel(soortmodel);
		((VerzuimComboBoxModel)cmbSoort.getModel()).setId(todo.getSoort().getValue());
		
		try {
			dtpDeadlinedatum.setDate(todo.getDeadlinedatum());
			dtpWaarschuwingsdatum.setDate(todo.getWaarschuwingsdatum());
			dtpAangemaakt.setDate(todo.getAanmaakdatum());
			if (todo.getVerzuimActiviteit() != null){
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

		activiteitmodel = new VerzuimComboBoxModel(todocontroller.getMaincontroller());
		
		cmbActiviteit.setModel(activiteitmodel);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshActiviteiten();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener);
		
		cmbActiviteit.setModel(activiteitmodel);
		controller.getMaincontroller().updateComboModelActiviteiten(activiteitmodel);
		if (this.getFormmode() != __formmode.NEW){
			((VerzuimComboBoxModel)cmbActiviteit.getModel()).setId(todo.getActiviteitId());
		}
		
	}
	protected void refreshActiviteiten() {
		controller.getMaincontroller().updateComboModelActiviteiten((VerzuimComboBoxModel)cmbActiviteit.getModel());
	}
	void initialize(){
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
		todocontroller.afrondenTodo(todo);
		displayTodo();
	}
/*	
	protected void okButtonClicked() {
		try {
			if (this.getFormmode() == __formmode.NEW){
				todocontroller.addData(todo);
			}else{
				todocontroller.updateData(todo);
			}
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
        controller.closeView(this);
	}
*/	
	@Override
	public InfoBase collectData() {
		TypeEntry activiteit;
		activiteit = (TypeEntry)cmbActiviteit.getSelectedItem();
		if (this.getFormmode() == __formmode.NEW || todo.getSoort()==__soort.HANDMATIG){
			todo.setAanmaakdatum(dtpAangemaakt.getDate());
		}
		todo.setWaarschuwingsdatum(dtpWaarschuwingsdatum.getDate());
		todo.setDeadlinedatum(dtpDeadlinedatum.getDate());
		todo.setActiviteitId(activiteit.getValue());
		todo.setHerhalen(chckbxHerhalen.isSelected());
		todo.setOpmerking(taOpmerking.getText());
		todo.setSoort(__soort.parse(((TypeEntry)cmbSoort.getSelectedItem()).getValue()));
		
		return todo;
	}
}
