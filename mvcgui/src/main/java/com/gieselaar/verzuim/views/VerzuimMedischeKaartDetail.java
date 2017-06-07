package com.gieselaar.verzuim.views;

import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

import javax.swing.ScrollPaneConstants;

public class VerzuimMedischeKaartDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtGebruiker;
	private VerzuimMedischekaartInfo medischekaart;
	private List<GebruikerInfo> gebruikers;
	private DatePicker dtpWijzgingsdatum;
	private JCheckBox chckbxOpenbaar;
	private JTextAreaTGI txtMedischeKaart;
	
	public VerzuimMedischeKaartDetail(AbstractController controller) {
		super("Beheer Medische kaart", controller);
		initialize();
	}
	public void setData(InfoBase info){
		medischekaart = (VerzuimMedischekaartInfo) info;
		gebruikers = controller.getMaincontroller().getGebruikers();
    	if (medischekaart.getState() == persistencestate.ABSENT)
    	{
    		medischekaart.setOpenbaar(false);
    		medischekaart.setWijzigingsdatum(new Date());
    		medischekaart.setUser(controller.getGebruiker().getId());
    	}
		try {
			dtpWijzgingsdatum.setDate(medischekaart.getWijzigingsdatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
    	chckbxOpenbaar.setSelected(medischekaart.getOpenbaar());
		for (GebruikerInfo gi: gebruikers){
			if (gi.getId().equals(medischekaart.getUser())){
				txtGebruiker.setText(gi.getAchternaam());
				break;
			}
		}
		txtMedischeKaart.setText(medischekaart.getMedischekaart());
	}

	private void initialize(){
		setBounds(100, 100, 485, 506);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 42, 449, 393);
		getContentPane().add(scrollPane);
		
		txtMedischeKaart = new JTextAreaTGI();
		scrollPane.setViewportView(txtMedischeKaart);
		
		txtGebruiker = new JTextFieldTGI();
		txtGebruiker.setEditable(false);
		txtGebruiker.setBounds(94, 11, 102, 20);
		getContentPane().add(txtGebruiker);
		txtGebruiker.setColumns(10);
		
		dtpWijzgingsdatum = new DatePicker();
		dtpWijzgingsdatum.setBounds(228, 11, 86, 21);
		dtpWijzgingsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpWijzgingsdatum);
		
		JLabel lblGewijzigdDoor = new JLabel("Gewijzigd door:");
		lblGewijzigdDoor.setBounds(10, 14, 86, 14);
		getContentPane().add(lblGewijzigdDoor);
		
		JLabel lblOp = new JLabel("op");
		lblOp.setBounds(206, 15, 24, 14);
		getContentPane().add(lblOp);
		
		chckbxOpenbaar = new JCheckBox("openbaar");
		chckbxOpenbaar.setBounds(320, 10, 97, 23);
		getContentPane().add(chckbxOpenbaar);

	}
	/*
	@Override
	protected void okButtonClicked() {

    	try {
    		if (medischekaart.getState() == persistencestate.ABSENT){
        		verzuimmedischekaarcontroller.addData(medischekaart);
        	}else{
        		verzuimmedischekaarcontroller.updateData(medischekaart);
        	}
		    controller.closeView(this);
		} catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
	*/
	@Override
	public InfoBase collectData() {
		medischekaart.setMedischekaart(txtMedischeKaart.getText());
		medischekaart.setOpenbaar(chckbxOpenbaar.isSelected());
		medischekaart.setWijzigingsdatum(dtpWijzgingsdatum.getDate());
		return medischekaart;
	}
}
